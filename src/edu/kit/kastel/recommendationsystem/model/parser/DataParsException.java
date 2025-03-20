package edu.kit.kastel.recommendationsystem.model.parser;

/**
 * Signals that parsing/retrieving a Database failed.
 * 
 * @author Programmieren-Team
 * @author urrwg
 */
public class DataParsException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param exceptionMessage the detail message. The detail message is saved for
     *                         later retrieval by the {@link Throwable#getMessage()}
     *                         method.
     */
    public DataParsException(String exceptionMessage) {
        super(String.format(exceptionMessage));
    }
}
