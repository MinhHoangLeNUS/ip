import java.util.Scanner;

/**
 * Entry point for the Aster chatbot.
 *
 * <p>At this stage Aster greets the user, stores each command entered as a task,
 * lists the stored tasks on the command {@code list}, and exits on the command
 * {@code bye}. Tasks are held in memory only; nothing is saved to disk.
 */
public class Aster {
    /**
     * Maximum number of tasks Aster can hold, as permitted by the requirements.
     */
    private static final int MAX_TASKS = 100;

    /**
     * Greets the user, then reads commands from standard input until {@code bye}
     * or the end of input. The command {@code list} shows the stored tasks in the
     * order they were added; any other command is stored as a new task.
     *
     * @param args command line arguments; not used
     */
    public static void main(String[] args) {
        final String divider = "____________________________________________________________";

        // Fixed-size array is sufficient because at most MAX_TASKS tasks are assumed.
        String[] tasks = new String[MAX_TASKS];
        int taskCount = 0;

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
            if (command.equals("list")) {
                // Numbering shown to the user is 1-based, so it is offset from the index.
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("added: " + command);
            }
            System.out.println(divider);
        }

        System.out.println(divider);
        System.out.println("Goodbye for now. Take care!");
        System.out.println(divider);
    }
}
