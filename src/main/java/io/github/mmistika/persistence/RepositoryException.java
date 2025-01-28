package io.github.mmistika.persistence;

/**
 * Custom exception for handling errors related to the repository operations.
 */
public class RepositoryException extends Exception {
    /**
     * Constructs a new RepositoryException with the specified detail message.
     *
     * @param message The detail message explaining the cause of the exception.
     */
    public RepositoryException(String message) {
        super(message);
    }
}
