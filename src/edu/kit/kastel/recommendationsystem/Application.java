package edu.kit.kastel.recommendationsystem;

import edu.kit.kastel.recommendationsystem.view.UserInterface;

/**
 * The class offering the entry point for the application.
 * 
 * @author Programmieren-Team
 * @author urrwg
 */
public final class Application {

    private static final int EXPECTED_COMMAND_LINE_ARGUMENTS_COUNT = 0;
    private static final String ERROR_MESSAGE_COMMAND_LINE_ARGUMENTS = "Error, no command line arguments expected.";

    private Application() {
        // This is a utility class
    }

    /**
     * The entry point for the application. No command line arguments are expected.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != EXPECTED_COMMAND_LINE_ARGUMENTS_COUNT) {
            System.err.println(ERROR_MESSAGE_COMMAND_LINE_ARGUMENTS);
            return;
        }

        UserInterface userInterface = new UserInterface(System.in, System.out, System.err);
        userInterface.handleUserInput();
    }
}
