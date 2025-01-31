package io.github.mmistika.persistence;

import io.github.mmistika.domain.Task;
import io.github.mmistika.domain.TaskStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A repository class for managing tasks. Handles persistence of tasks in a JSON file.
 * Provides methods to save, retrieve, and delete tasks.
 */
public class TaskRepository {
    /**
     * Cached list of tasks to minimize file reads.
     */
    private List<Task> cachedTasks = null;

    /**
     * Relative path to the .json file with the application data.
     */
    private final static String FILE_PATH = "data.json";

    /**
     * Initializes the repository by ensuring the JSON file exists.
     * If the file does not exist, it will be created.
     *
     * @throws RepositoryException if the file cannot be created.
     */
    public TaskRepository() throws RepositoryException {
        var path = Path.of(FILE_PATH);
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
                Files.write(path, List.of("[", "]"), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RepositoryException("Cannot create JSON file");
            }
        }
    }

    /**
     * Finds the next available ID, filling any gaps left by deletions.
     *
     * @return The smallest free ID.
     * @throws RepositoryException if an error occurs while accessing the repository.
     */
    public int getNextAvailableId() throws RepositoryException {
        var usedIds = getTasksAll().stream()
                .map(Task::getId)
                .sorted()
                .toList();

        int nextId = 1;
        for (int id : usedIds) {
            if (id == nextId) {
                ++nextId;
            } else {
                break;
            }
        }
        return nextId;
    }

    /**
     * Saves a new task to the JSON file.
     *
     * @param task The task to be saved.
     * @throws RepositoryException if the task cannot be saved to the file.
     */
    public void saveTask(Task task) throws RepositoryException {
        var tasks = new ArrayList<>(getTasksAll());
        tasks.add(task);
        writeTasksToFile(tasks);
    }

    /**
     * Retrieves all tasks from the repository, using the cache when possible.
     *
     * @return A list of all tasks stored in the repository.
     */
    public List<Task> getTasksAll() {
        if (cachedTasks == null) {
            cachedTasks = loadTasksFromFile();
        }
        return cachedTasks;
    }

    /**
     * Retrieves all tasks with a specific status.
     *
     * @param status The status to filter tasks by.
     * @return A list of tasks matching the specified status.
     */
    public List<Task> getTasksByStatus(TaskStatus status) {
        return getTasksAll().stream()
                .filter(task -> task.getStatus().equals(status))
                .toList();
    }

    /**
     * Retrieves a task by its unique ID.
     *
     * @param id The unique ID of the task.
     * @return The task with the specified ID, or {@code null} if not found.
     */
    public Task getTaskById(int id) {
        return getTasksAll().stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Deletes a task by its unique ID.
     *
     * @param id The unique ID of the task to delete.
     * @throws RepositoryException if the task cannot be deleted from the file.
     */
    public void deleteTaskById(int id) throws RepositoryException {
        var tasks = getTasksAll().stream()
                .filter(task -> task.getId() != id)
                .toList();
        writeTasksToFile(tasks);
    }

    /**
     * Writes the task list to the JSON file and updates cache.
     *
     * @param tasks The list of tasks to write.
     * @throws RepositoryException if an error occurs while writing.
     */
    private void writeTasksToFile(List<Task> tasks) throws RepositoryException {
        var lines = new ArrayList<>(tasks.stream()
                .map(TaskRepository::jsonFromTask)
                .toList());
        lines.addFirst("[");
        lines.add("]");
        try {
            Files.write(Path.of(FILE_PATH), lines, StandardCharsets.UTF_8);
            cachedTasks = new ArrayList<>(tasks);
        } catch (IOException e) {
            throw new RepositoryException("Cannot write JSON file");
        }
    }

    /**
     * Loads tasks from the JSON file.
     *
     * @return A list of tasks parsed from the file.
     */
    private List<Task> loadTasksFromFile() {
        try {
            var lines = Files.readAllLines(Path.of(FILE_PATH), StandardCharsets.UTF_8);
            if (lines.size() < 2 || !lines.removeFirst().equals("[") || !lines.removeLast().equals("]")) {
                throw new IOException("Invalid JSON format");
            }
            cachedTasks = lines.stream()
                    .filter(line -> line.matches("\\{.*},?"))
                    .map(TaskRepository::taskFromJson)
                    .filter(Objects::nonNull)
                    .toList();
            return cachedTasks;
        } catch (IOException e) {
            System.err.println("Warning: Failed to read JSON file. Using cached data if available.");
            // Assign empty list only if cache was never loaded
            if (cachedTasks == null) {
                cachedTasks = new ArrayList<>();
            }
            return cachedTasks;
        }
    }

    /**
     * Extracts a value associated with a key from a JSON string.
     *
     * @param json The JSON string.
     * @param key  The key to search for.
     * @return The value associated with the specified key.
     * @throws IllegalArgumentException if the key or value is not found in the JSON.
     */
    private static String getValueByKey(String json, String key) throws IllegalArgumentException {
        var valuePattern = String.format("((?<=\"%s\":\"?).*?(?=[\",}]))", key);
        var matcher = Pattern.compile(valuePattern).matcher(json);
        while (matcher.find()) {
            var match = matcher.group(1);
            if (!match.isBlank()) {
                return match;
            }
        }
        throw new IllegalArgumentException("Value not found");
    }

    /**
     * Converts a JSON string into a Task object.
     *
     * @param json The JSON string representing a task.
     * @return The Task object, or {@code null} if parsing fails.
     */
    private static Task taskFromJson(String json) {
        try {
            var id = Integer.parseInt(getValueByKey(json, "id"));
            var description = getValueByKey(json, "description");
            var status = TaskStatus.valueOf(getValueByKey(json, "status"));
            var createdAt = LocalDateTime.parse(getValueByKey(json, "createdAt"));
            var updatedAt = LocalDateTime.parse(getValueByKey(json, "updatedAt"));
            return new Task(id, description, status, createdAt, updatedAt);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts a Task object into a JSON string.
     *
     * @param task The task to convert.
     * @return The JSON string representation of the task.
     */
    private static String jsonFromTask(Task task) {
        return "{"
                + "\"id\":" + task.getId() + ","
                + "\"description\":\"" + task.getDescription() + "\","
                + "\"status\":\"" + task.getStatus() + "\","
                + "\"createdAt\":\"" + task.getCreatedAt() + "\","
                + "\"updatedAt\":\"" + task.getUpdatedAt() + "\"},";
    }
}
