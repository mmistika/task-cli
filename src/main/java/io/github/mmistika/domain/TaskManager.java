package io.github.mmistika.domain;

import io.github.mmistika.persistence.RepositoryException;
import io.github.mmistika.persistence.TaskRepository;

import java.time.LocalDateTime;

/**
 * The TaskManager class provides high-level methods to manage tasks,
 * including creating, updating, listing, and deleting tasks.
 * It uses a {@link TaskRepository} to persist tasks.
 */
public class TaskManager {
    /**
     * The task repository.
     */
    private final TaskRepository taskRepository;

    /**
     * Constructs a TaskManager with the given TaskRepository.
     *
     * @param taskRepository The repository used for task persistence
     */
    public TaskManager(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Adds a new task with the given description.
     *
     * @param description The description of the new task
     */
    public void addTask(String description) {
        try {
            var id = taskRepository.getNextAvailableId();
            var createdAt = LocalDateTime.now();
            var task = new Task(id, description, TaskStatus.TODO, createdAt, createdAt);
            taskRepository.saveTask(task);
            System.out.println("Task added successfully (ID: " + id + ")");
        } catch (RepositoryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Lists all tasks in the repository.
     * If no tasks are found, a message is displayed.
     */
    public void listTasksAll() {
        try {
            var tasks = taskRepository.getTasksAll();
            if (tasks.isEmpty()) {
                System.out.println("No tasks found");
            }
            tasks.forEach(TaskManager::printTask);
        } catch (RepositoryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Lists all tasks with the specified status.
     * If no tasks with the given status are found, a message is displayed.
     *
     * @param status The status of tasks to list.
     */
    public void listTasksByStatus(TaskStatus status) {
        var tasks = taskRepository.getTasksByStatus(status);
        if (tasks.isEmpty()) {
            System.out.println("No tasks found with status: " + status);
        }
        tasks.forEach(TaskManager::printTask);
    }

    /**
     * Updates the description of an existing task with the given ID.
     *
     * @param id          The ID of the task to update
     * @param description The new description for the task
     */
    public void updateTask(int id, String description) {
        var updated = taskRepository.getTaskById(id);
        if (updated != null) {
            updated.setDescription(description);
            updateTask(updated);
        } else {
            System.out.println("Task not found");
        }
    }

    /**
     * Updates the status of an existing task with the given ID.
     *
     * @param id     The ID of the task to update
     * @param status The new status for the task
     */
    public void updateTask(int id, TaskStatus status) {
        var updated = taskRepository.getTaskById(id);
        if (updated != null) {
            updated.setStatus(status);
            updateTask(updated);
        } else {
            System.out.println("Task not found");
        }
    }

    /**
     * Deletes the task with the given ID.
     *
     * @param id The ID of the task to delete
     */
    public void deleteTask(int id) {
        try {
            taskRepository.deleteTaskById(id);
        } catch (RepositoryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Saves an updated task back to the repository.
     *
     * @param updated The updated task object
     */
    private void updateTask(Task updated) {
        try {
            taskRepository.deleteTaskById(updated.getId());
            taskRepository.saveTask(updated);
        } catch (RepositoryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Prints the details of a task to the console.
     *
     * @param task The task to print
     */
    private static void printTask(Task task) {
        System.out.println("\n\nTask ID: " + task.getId());
        System.out.println("Description: " + task.getDescription());
        System.out.println("Status: " + task.getStatus());
        System.out.println("Created at: " + task.getCreatedAt());
        System.out.println("Updated at: " + task.getUpdatedAt());
    }
}
