package io.github.mmistika;

import io.github.mmistika.cli.CommandHandler;
import io.github.mmistika.domain.TaskManager;
import io.github.mmistika.persistence.RepositoryException;
import io.github.mmistika.persistence.TaskRepository;

public class Main {
    public static void main(String[] args) {
        try {
            var repo = new TaskRepository();
            var taskMgr = new TaskManager(repo);
            var cmdHandler = new CommandHandler(taskMgr);
            cmdHandler.handle(args);
        } catch (RepositoryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}