package edu.kit.kastel.recommendationsystem.view.commands;

import edu.kit.kastel.recommendationsystem.view.Result;
import edu.kit.kastel.recommendationsystem.view.UserInterface;

/**
 * This class represents a command that quits an interaction of an user
 * interface. No arguments are expected.
 * 
 * @author Programmieren-Team
 * @author urrwg
 */
public class CommandQuit implements Command<UserInterface> {

    /**
     * Quits an interaction of the provided user interface by invoking
     * {@link UserInterface#stop()}.
     * 
     * @param handle the user interface to be stopped
     * @return {@code null}
     */
    @Override
    public Result execute(UserInterface handle) {
        handle.stop();
        return Result.success();
    }
}
