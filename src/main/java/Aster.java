import java.util.Scanner;

/**
 * Entry point for the Aster chatbot.
 *
 * <p>At this stage Aster greets the user, stores each ordinary command entered
 * as a task, lists the stored tasks with their done status on the command
 * {@code list}, marks a task as done on {@code mark <number>} and as not done on
 * {@code unmark <number>}, and exits on the command {@code bye}. Tasks are held
 * in memory only; nothing is saved to disk.
 */
public class Aster {
    /**
     * Maximum number of tasks Aster can hold, as permitted by the requirements.
     */
    private static final int MAX_TASKS = 100;

    private static final String MARK_PREFIX = "mark ";
    private static final String UNMARK_PREFIX = "unmark ";

    /**
     * Greets the user, then reads commands from standard input until {@code bye}
     * or the end of input. The command {@code list} shows the stored tasks in the
     * order they were added, {@code mark} and {@code unmark} change the done
     * status of one task, and any other command is stored as a new task.
     *
     * @param args command line arguments; not used
     */
    public static void main(String[] args) {
        final String divider = "____________________________________________________________";

        // Fixed-size array is sufficient because at most MAX_TASKS tasks are assumed.
        Task[] tasks = new Task[MAX_TASKS];
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
            } else if (command.startsWith(UNMARK_PREFIX)) {
                Task task = tasks[parseTaskIndex(command, UNMARK_PREFIX)];
                task.markAsNotDone();
                System.out.println("Alright, I've marked this task as not done yet:");
                System.out.println("  " + task);
            } else if (command.startsWith(MARK_PREFIX)) {
                Task task = tasks[parseTaskIndex(command, MARK_PREFIX)];
                task.markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + task);
            } else {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println("added: " + command);
            }
            System.out.println(divider);
        }

        System.out.println(divider);
        System.out.println("Goodbye for now. Take care!");
        System.out.println(divider);
    }

    /**
     * Converts the 1-based task number in a mark or unmark command into a 0-based
     * array index. The number is assumed to be present and valid; validating user
     * input belongs to a later increment.
     *
     * @param command the full command line entered by the user
     * @param prefix the command prefix that precedes the task number
     * @return the 0-based index of the task the command refers to
     */
    private static int parseTaskIndex(String command, String prefix) {
        String number = command.substring(prefix.length());
        return Integer.parseInt(number) - 1;
    }
}
