# 📝 task-cli

**Task CLI** is a simple command-line tool built in Java to manage your to-do list. Created as a learning project based on the [roadmap.sh](https://roadmap.sh/projects/task-tracker) guidelines.

## 🚀 Features  

- **Add** new tasks
-  **Update** task descriptions
-  **Delete** tasks
-  **Mark** tasks as `todo`, `in-progress`, or `done`
-  **List** tasks based on their status
-  **Persistent storage** in a JSON file

## 📦 Installation  
> [!IMPORTANT]
> The minimum JDK version required is 22. Ensure you have JDK 22 or a higher version installed to avoid any issues.

Clone the repository and compile the Java code:

```
git clone https://github.com/mmistika/task-cli.git
cd task-cli  
javac --class-path src/main/java -d build/ src/main/java/io/github/mmistika/Main.java
```

Run the CLI:

```
java -cp build io.github.mmistika.Main
```

## 📖 Usage  

```
task-cli <command> [options]
```

### Commands  

| Command                         | Description                                            | Example                                             |
|---------------------------------|--------------------------------------------------------|-----------------------------------------------------|
| `add <description>`             | Add a new task                                         | `task-cli add "Buy groceries"`                      |
| `update <id> <new-description>` | Update a task's description                            | `task-cli update 1 "Buy groceries and cook dinner"` |
| `delete <id>`                   | Remove a task permanently                              | `task-cli delete 1`                                 |
| `mark-todo <id>`                | Mark a task as "To Do"                                 | `task-cli mark-todo 1`                              |
| `mark-in-progress <id>`         | Mark a task as "In Progress"                           | `task-cli mark-in-progress 1`                       |
| `mark-done <id>`                | Mark a task as "Done"                                  | `task-cli mark-done 1`                              |
| `list`                          | Show all tasks                                         | `task-cli list`                                     |
| `list <status>`                 | Filter tasks by status (`todo`, `in-progress`, `done`) | `task-cli list done`                                |
