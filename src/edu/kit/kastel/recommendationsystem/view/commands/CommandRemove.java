package edu.kit.kastel.recommendationsystem.view.commands;

import edu.kit.kastel.recommendationsystem.model.DTO;
import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.view.Result;

public class CommandRemove implements Command<Graph> {

    private final DTO dto;

    public CommandRemove(DTO dto) {
        this.dto = dto;
    }

    @Override
    public Result execute(Graph handle) {
        return handle.removeEdge(this.dto) ? Result.success() : Result.error("Edge can not be removed");
    }
}
