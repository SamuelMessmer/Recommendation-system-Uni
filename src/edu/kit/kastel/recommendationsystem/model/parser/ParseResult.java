package edu.kit.kastel.recommendationsystem.model.parser;

import java.util.List;

import edu.kit.kastel.recommendationsystem.model.Graph;

/**
 * fjiewofjwaio.
 * 
 * @author urrwg
 */
public class ParseResult {
    private final Graph graph;
    private final List<String> processedLines;

    /**
     * fjiaweoöfwajiefjawef.
     * 
     * @param graph          fjwioefw
     * @param processedLines ejfaiwoeawj
     */
    public ParseResult(Graph graph, List<String> processedLines) {
        this.graph = graph;
        this.processedLines = processedLines;
    }

    /**
     * fjiewaoöfjaw.
     * 
     * @return wjiefoaöw.
     */
    public Graph getGraph() {
        return this.graph;
    }

    /**
     * jfwaeioöfjaweio.
     * 
     * @return fjiwoefja.
     */
    public List<String> getProcessedLines() {
        return this.processedLines;
    }
}
