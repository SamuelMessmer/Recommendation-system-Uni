package edu.kit.kastel.recommendationsystem.view.commands;

import edu.kit.kastel.recommendationsystem.view.Result;
import edu.kit.kastel.recommendationsystem.view.Communication;

public class CommandLoad implements Command<Communication> {

    @Override
    public Result execute(Communication handle) {
        handle.setGraph(null);

        return null;
    }
}
