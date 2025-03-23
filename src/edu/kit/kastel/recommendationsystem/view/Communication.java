package edu.kit.kastel.recommendationsystem.view;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.Set;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.view.commands.Command;

/**
 * Handles user input and command execution for the recommendation system.
 * This class reads input from a provided source, processes commands, and
 * executes them on the managed graph instance.
 * 
 * @author urrwg
 */
public class Communication {

    private static final String COMMAND_SEPARATOR = " ";
    private static final String ERROR_PREFIX = "Error, ";
    private static final String ERROR_UNKNOWN_COMMAND_FORMAT = ERROR_PREFIX + " unknown command: %s";
    private static final String ERROR_TOO_MANY_ARGUMENTS = ERROR_PREFIX + "too many arguments provided.";
    private static final String ERROR_INVALID_PRECONDITION = ERROR_PREFIX + "command cannot be used right now.";
    private final Set<GraphKeyword> graphKeywords = EnumSet.allOf(GraphKeyword.class);
    private final Set<ViewKeyword> viewKeywords = EnumSet.allOf(ViewKeyword.class);

    private final InputStream inputSource;
    private final PrintStream defaultStream;
    private final PrintStream errorStream;

    private Graph graph;
    private boolean isRunning;

    /**
     * Constructs a new Communication instance.
     *
     * @param inputSource   the input stream used to read user commands
     * @param defaultStream the output stream used for standard messages
     * @param errorStream   the output stream used for error messages
     */
    public Communication(InputStream inputSource, PrintStream defaultStream, PrintStream errorStream) {
        this.inputSource = inputSource;
        this.defaultStream = defaultStream;
        this.errorStream = errorStream;
    }

    /**
     * Sets the graph instance that is provided by this class {@link Communication}
     * to its managed commands.
     * 
     * @param graph the graph instance to be provided to the commands
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    /**
     * Stops this instance from reading further input from the source.
     */
    public void stop() {
        this.isRunning = false;
    }

    /**
     * Starts the interaction with the user. This method will block while
     * interacting.
     * The interaction will continue as long as the provided source has more lines
     * to read,
     * or until it is stopped. The provided input source is closed after the
     * interaction is finished.
     *
     * @see Scanner#hasNextLine()
     * @see #stop()
     * @see #UserInterface(InputStream, PrintStream, PrintStream)
     */
    public void handleUserInput() {
        this.isRunning = true;
        try (Scanner scanner = new Scanner(this.inputSource)) {
            while (this.isRunning && scanner.hasNextLine()) {
                handleLine(scanner.nextLine());
            }
        }
    }

    private void handleLine(String line) {
        String[] split = line.split(COMMAND_SEPARATOR, -1);
        String command = split[0];
        String[] arguments = Arrays.copyOfRange(split, 1, split.length);

        if (!findAndHandleCommand(this.viewKeywords, this, command, arguments)
                && !findAndHandleCommand(this.graphKeywords, graph, command, arguments)) {
            this.errorStream.println(ERROR_UNKNOWN_COMMAND_FORMAT.formatted(command));
        }
    }

    private <S, T extends Keyword<S>> boolean findAndHandleCommand(Set<T> keywords, S value, String command,
            String[] arguments) {
        T keyword = retrieveKeyword(keywords, command);
        if (keyword != null) {
            handleCommand(value, arguments, keyword);
            return true;
        }
        return false;
    }

    private <S, T extends Keyword<S>> void handleCommand(S value, String[] arguments, T keyword) {
        if (value == null) {
            this.errorStream.println(ERROR_INVALID_PRECONDITION);
            return;
        }

        Arguments argumentsHolder = new Arguments(this.graph, arguments);
        Command<S> providedCommand;
        try {
            providedCommand = keyword.provide(argumentsHolder);
        } catch (InvalidArgumentException exception) {
            this.errorStream.println(ERROR_PREFIX + exception.getMessage());
            return;
        }

        if (!argumentsHolder.isExhausted()) {
            this.errorStream.println(ERROR_TOO_MANY_ARGUMENTS);
            return;
        }

        handleResult(providedCommand.execute(value));
    }

    private void handleResult(Result result) {
        if (result == null) {
            return;
        }

        PrintStream outputStream = switch (result.getType()) {
            case SUCCESS -> this.defaultStream;
            case FAILURE -> this.errorStream;
        };

        if (result.getMessage() != null) {
            outputStream.println((result.getType().equals(ResultType.FAILURE) ? ERROR_PREFIX : "")
                    + result.getMessage());
        }
    }

    private static <T extends Keyword<?>> T retrieveKeyword(Collection<T> keywords, String command) {
        for (T keyword : keywords) {
            if (keyword.matches(command)) {
                return keyword;
            }
        }
        return null;
    }
}
