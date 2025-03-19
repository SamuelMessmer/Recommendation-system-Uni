package edu.kit.kastel.recommendationsystem.view.commands;

import edu.kit.kastel.recommendationsystem.view.Result;
import edu.kit.kastel.recommendationsystem.view.Communication;

/**
 * This class represents a command that quits an interaction of an user interface. No arguments are expected.
 * 
 * @author Programmieren-Team
 */
public class CommandQuit implements Command<Communication> {

    /**
     * Quits an interaction of the provided user interface by invoking {@link Communication#stop()}.
     * 
     * @param handle the user interface to be stopped
     * @return {@code null}
     */
    @Override
    public Result execute(Communication handle) {
        return null;
    }
}
