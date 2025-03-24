package edu.kit.kastel.recommendationsystem.view.commands;

import edu.kit.kastel.recommendationsystem.view.Arguments;
import edu.kit.kastel.recommendationsystem.view.InvalidArgumentException;

/**
 * This interface provides a command instance constructed with the given arguments.
 * @param <T> the type of the value that is handled by the command
 *           
 * @author Programmieren-Team
 * @author urrwg
 */
public interface CommandProvider<T> {

    /**
     * Constructs a new command instance with the given arguments.
     * @param arguments the arguments to be used for constructing the command
     * @return the constructed command
     * @throws InvalidArgumentException if parsing/retrieving an argument fails
     */
    Command<T> provide(Arguments arguments) throws InvalidArgumentException;
}
