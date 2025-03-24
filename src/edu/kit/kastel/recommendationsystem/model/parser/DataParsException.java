package edu.kit.kastel.recommendationsystem.model.parser;

import java.util.List;

/**
 * Signals that parsing/retrieving a Database failed.
 * 
 * @author Programmieren-Team
 * @author urrwg
 */
public class DataParsException extends Exception {

    private final List<String> processedLines;
    private final String errorMessage;

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param processedLines the processedLines tup to the liine where the exception
     *                       was thrown
     * @param errorMessage   the detailed message. The detail message is saved for
     *                       later retrieval by the {@link Throwable#getMessage()}
     *                       method.
     */
    public DataParsException(List<String> processedLines, String errorMessage) {
        super(errorMessage);
        this.processedLines = processedLines;
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the already processed lines, processed by the {@link LineParser} up
     * to the line which triggered the exception.
     * 
     * @return the list of processed lines
     */
    public List<String> getProcessedLines() {
        return processedLines;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
