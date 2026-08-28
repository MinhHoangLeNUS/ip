import java.util.Scanner;

/**
 * Entry point for the Aster chatbot.
 *
 * <p>At this stage Aster greets the user, echoes each command entered, and exits
 * when the command {@code bye} is entered. It keeps no state between commands.
 */
public class Aster {
    /**
     * Greets the user, echoes each command read from standard input, and exits on
     * the command {@code bye} or when the input ends.
     *
     * @param args command line arguments; not used
     */
    public static void main(String[] args) {
        final String divider = "____________________________________________________________";

        System.out.println(divider);
        System.out.println("Hello! I'm Aster.");
        System.out.println("I'm a simple chatbot, and I'm glad you're here.");
        System.out.println("What can I do for you?");
        System.out.println(divider);

        Scanner scanner = new Scanner(System.in);
        // hasNextLine() also ends the loop cleanly if the input stops before "bye".
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                break;
            }
            System.out.println(divider);
            System.out.println(command);
            System.out.println(divider);
        }

        System.out.println(divider);
        System.out.println("Goodbye for now. Take care!");
        System.out.println(divider);
    }
}
