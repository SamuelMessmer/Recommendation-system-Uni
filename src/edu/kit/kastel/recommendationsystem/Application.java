package edu.kit.kastel.recommendationsystem;

import edu.kit.kastel.recommendationsystem.view.Communication;

/**
 * The class offering the entry point for the application.
 * 
 * @author Programmieren-Team
 */
public final class Application {

    private static final String ERROR_MESSAGE_COMMAND_LINE_ARGUMENTS = "Error, no command line arguments expected.";

    private Application() {
        // utility class
    }

    /**
     * The entry point for the application. No command line arguments are expected.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            System.err.println(ERROR_MESSAGE_COMMAND_LINE_ARGUMENTS);
            return;
        }
        
        Communication cummunication = new Communication();
        cummunication.toString();
    }
}
