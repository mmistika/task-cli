package io.github.mmistika.domain;

import java.time.LocalDateTime;

/**
 * Represents a task in the task management system.
 * Includes an ID, description, status, and timestamps for creation and update.
 */
public class Task {
    /**
     * Unique ID of the task.
     */
    private final int id;

    /**
     * Description of the task.
     */
    private String description;

    /**
     * Current status of the task.
     */
    private TaskStatus status;

    /**
     * Timestamp of creation of the task.
     */
    private final LocalDateTime createdAt;

    /**
     * Timestamp of last modification of the task.
     */
    private LocalDateTime updatedAt;

    /**
     * Constructor to initialize a task with given ID, description, status, and timestamps.
     *
     * @param id          Unique ID for the task.
     * @param description Description of the task.
     * @param status      Initial status of the task.
     * @param createdAt   Timestamp of creation of the task.
     * @param updatedAt   Timestamp of last modification of the task.
     */
    public Task(int id, String description, TaskStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the unique ID of the task.
     *
     * @return The task ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the description of the task.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the task.
     *
     * @param description The new task description.
     */
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Gets the current status of the task.
     *
     * @return The task status.
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the task.
     *
     * @param status The new task status.
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Gets the creation timestamp of the task.
     *
     * @return The timestamp of creation of the task.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the last modification timestamp of the task.
     *
     * @return The timestamp of the last modification of the task.
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
