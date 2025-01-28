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
        var lines = getTasksAll().stream()
                .map(TaskRepository::jsonFromTaskWithComma)
                .toList();

        lines.addLast(jsonFromTask(task));

        try {
            Files.write(Path.of(FILE_PATH), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RepositoryException("Cannot write JSON file");
        }
    }

    /**
     * Retrieves all tasks from the JSON file.
     *
     * @return A list of all tasks stored in the repository.
     * @throws RepositoryException if tasks cannot be read from the file.
     */
    public List<Task> getTasksAll() throws RepositoryException {
        try {
            var lines = Files.readAllLines(Path.of(FILE_PATH), StandardCharsets.UTF_8);

            if (lines.isEmpty()) {
                return new ArrayList<>();
            }
            if (!lines.removeFirst().equals("[") || !lines.removeLast().equals("]")) {
                return new ArrayList<>();
            }

            return lines.stream()
                    .filter(line -> line.matches("\\{.*},?"))
                    .map(TaskRepository::taskFromJson)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            throw new RepositoryException("Cannot read JSON file");
        }
    }

    /**
     * Retrieves all tasks with a specific status.
     *
     * @param status The status to filter tasks by.
     * @return A list of tasks matching the specified status.
     */
    public List<Task> getTasksByStatus(TaskStatus status) {
        try {
            return getTasksAll().stream()
                    .filter(task -> task.getStatus().equals(status))
                    .toList();
        } catch (RepositoryException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves a task by its unique ID.
     *
     * @param id The unique ID of the task.
     * @return The task with the specified ID, or {@code null} if not found.
     */
    public Task getTaskById(int id) {
        try {
            return getTasksAll().stream()
                    .filter(task -> task.getId() == id)
                    .findFirst()
                    .orElse(null);
        } catch (RepositoryException e) {
            return null;
        }
    }

    /**
     * Deletes a task by its unique ID.
     *
     * @param id The unique ID of the task to delete.
     * @throws RepositoryException if the task cannot be deleted from the file.
     */
    public void deleteTaskById(int id) throws RepositoryException {
        var lines = getTasksAll().stream()
                .filter(task -> task.getId() != id)
                .map(TaskRepository::jsonFromTaskWithComma)
                .toList();

        try {
            Files.write(Path.of(FILE_PATH), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RepositoryException("Cannot write JSON file");
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
        var valuePattern = String.format("(?<=\"%s\":\"?).*?(?=[\",}])", key);
        var matcher = Pattern.compile(valuePattern).matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Value not found");
        }
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
                + "\"updatedAt\":\"" + task.getUpdatedAt() + "\"}";
    }

    /**
     * Converts a Task object into a JSON string, adding a trailing comma for list formatting.
     *
     * @param task The task to convert.
     * @return The JSON string representation of the task with a trailing comma.
     */
    private static String jsonFromTaskWithComma(Task task) {
        return jsonFromTask(task) + ",";
    }
}
