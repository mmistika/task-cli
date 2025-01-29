package io.github.mmistika.cli;

import io.github.mmistika.domain.TaskManager;
import io.github.mmistika.domain.TaskStatus;

/**
 * Handles command-line interface (CLI) interactions for task management.
 */
public class CommandHandler {
    /**
     * The task manager.
     */
    TaskManager taskManager;

    /**
     * Constructs a new CommandHandler with the given TaskManager.
     *
     * @param taskManager The task manager responsible for handling tasks.
     */
    public CommandHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    /**
     * Processes user input commands and executes corresponding task operations.
     *
     * @param args The command-line arguments provided by the user.
     */
    public void handle(String[] args) {
        final var STATUS_LIST = """
                
                STATUSES:
                    todo
                    in-progress
                    done
                """;

        final var USAGE_ALL = """
                Usage: task-cli <command> [<options>]
                
                COMMANDS:
                    add    - Add a new task with a description.
                    update - Update an existing task's description.
                    delete - Remove a task permanently by ID.
                    list   - Display tasks, optionally filtered by status.
                    mark   - Change a task's status (todo, in-progress, or done).
                    help   - Show detailed usage instructions.
                
                To see each command usage use:
                task-cli <command>
                """;

        final var USAGE_ADD = "Usage: task-cli add <description>";
        final var USAGE_UPD = "Usage: task-cli update <id> <new-description>";
        final var USAGE_DEL = "Usage: task-cli delete <id>";
        final var USAGE_LST = "Usage: task-cli list [status]\n" + STATUS_LIST;
        final var USAGE_MRK = "Usage: task-cli mark-<status> <id>\n" + STATUS_LIST;

        if (args.length == 0) {
            System.out.println(USAGE_ALL);
            return;
        }

        var cmd = args[0];
        switch (cmd) {
            case "add" -> {
                if (args.length >= 2) {
                    taskManager.addTask(args[1]);
                    break;
                }
                System.out.println(USAGE_ADD);
            }
            case "update" -> {
                try {
                    if (args.length >= 3) {
                        taskManager.updateTask(Integer.parseInt(args[1]), args[2]);
                        break;
                    }
                } catch (NumberFormatException _) {
                }
                System.out.println(USAGE_UPD);
            }
            case "delete" -> {
                try {
                    if (args.length >= 2) {
                        taskManager.deleteTask(Integer.parseInt(args[1]));
                        break;
                    }
                } catch (NumberFormatException _) {
                }
                System.out.println(USAGE_DEL);
            }
            case "list" -> {
                if (args.length == 1) {
                    taskManager.listTasksAll();
                    break;
                }
                var status = parseStatus(args[1]);
                if (status != null) {
                    taskManager.listTasksByStatus(status);
                    break;
                }
                System.out.println(USAGE_LST);
            }
            case "mark" -> System.out.println(USAGE_MRK);
            default -> {
                if (cmd.startsWith("mark-") && args.length == 2) {
                    try {
                        var status = parseStatus(cmd.substring("mark-".length()));
                        if (status != null) {
                            taskManager.updateTask(Integer.parseInt(args[1]), status);
                            break;
                        }
                    } catch (NumberFormatException _) {
                    }
                    System.out.println(USAGE_MRK);
                    break;
                }
                System.out.println(USAGE_ALL);
            }
        }
    }

    /**
     * Converts a string representation of a status into a {@link TaskStatus} enum value.
     *
     * @param status The status string ("todo", "in-progress", or "done").
     * @return The corresponding {@link TaskStatus} value, or {@code null} if the input is invalid.
     */
    private TaskStatus parseStatus(String status) {
        return switch (status) {
            case "todo" -> TaskStatus.TODO;
            case "in-progress" -> TaskStatus.IN_PROGRESS;
            case "done" -> TaskStatus.DONE;
            default -> null;
        };
    }
}
