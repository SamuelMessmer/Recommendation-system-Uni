// CommandRecommend.java
package edu.kit.kastel.recommendationsystem.view.commands;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import edu.kit.kastel.recommendationsystem.model.Graph;
import edu.kit.kastel.recommendationsystem.model.Node;
import edu.kit.kastel.recommendationsystem.model.parser.DataParsException;
import edu.kit.kastel.recommendationsystem.util.RecursiveDescentParser;
import edu.kit.kastel.recommendationsystem.util.SortUtils;
import edu.kit.kastel.recommendationsystem.view.Result;

/**
 * Implements the recommendation command with recursive descent parsing.
 * Supports union/intersection operations between recommendation strategies.
 * Validates input syntax and handles strategy execution.
 * 
 * @author urrwg
 */
public class CommandRecommend implements Command<Graph> {

    private static final String OUTPUT_SEPARATOR = " ";
    private static final String EMPTY_OUTPUT = "";

    private final String input;

    /**
     * Constructs a new CommandRecommend instance.
     * 
     * @param input the input string representing the recommendation query,
     *              which will be parsed to determine the recommendation strategy
     */
    public CommandRecommend(String input) {
        this.input = input;
    }

    @Override
    public Result execute(Graph handle) {
        try {
            Set<Node> recommendations = RecursiveDescentParser.parse(input, handle);
            return Result.success(formatOutput(recommendations));
        } catch (DataParsException exception) {
            return Result.error(exception.getMessage());
        }
    }

    private String formatOutput(Set<Node> nodes) {
        if (nodes.isEmpty()) {
            return EMPTY_OUTPUT;
        }

        List<Node> sorted = new ArrayList<>(nodes);
        SortUtils.sortNodes(sorted);

        StringBuilder sb = new StringBuilder();
        for (Node node : sorted) {
            if (sb.length() > 0) {
                sb.append(OUTPUT_SEPARATOR);
            }
            sb.append(node.toString().toLowerCase());
        }
        return sb.toString();
    }
}