import java.util.Scanner;

/**
 * Entry point for the Aster chatbot.
 *
 * <p>At this stage Aster greets the user and reads commands until {@code bye}.
 * The commands {@code todo}, {@code deadline} and {@code event} add a task of
 * the matching type, {@code list} shows the stored tasks with their type and
 * done status, and {@code mark <number>} and {@code unmark <number>} change the
 * done status of one task. Any other command is still stored as a plain task,
 * as it was before typed tasks existed. Tasks are held in memory only; nothing
 * is saved to disk.
 */
public class Aster {
    /**
     * Maximum number of tasks Aster can hold, as permitted by the requirements.
     */
    private static final int MAX_TASKS = 100;

    private static final String MARK_PREFIX = "mark ";
    private static final String UNMARK_PREFIX = "unmark ";
    private static final String TODO_PREFIX = "todo ";
    private static final String DEADLINE_PREFIX = "deadline ";
    private static final String EVENT_PREFIX = "event ";

    // The surrounding spaces keep these markers from matching text inside a description.
    private static final String BY_SEPARATOR = " /by ";
    private static final String FROM_SEPARATOR = " /from ";
    private static final String TO_SEPARATOR = " /to ";

    /**
     * Greets the user, then reads commands from standard input until {@code bye}
     * or the end of input. The command {@code list} shows the stored tasks in the
     * order they were added, {@code mark} and {@code unmark} change the done
     * status of one task, {@code todo}, {@code deadline} and {@code event} add a
     * task of that type, and any other command is stored as a plain task.
     *
     * @param args command line arguments; not used
     */
    public static void main(String[] args) {
        final String divider = "____________________________________________________________";

        // Fixed-size array is sufficient because at most MAX_TASKS tasks are assumed.
        // It holds every task type, since each subclass is also a Task.
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
            } else if (command.startsWith(TODO_PREFIX)) {
                tasks[taskCount] = new Todo(command.substring(TODO_PREFIX.length()));
                taskCount++;
                printAdded(tasks[taskCount - 1], taskCount);
            } else if (command.startsWith(DEADLINE_PREFIX)) {
                // Command formats are assumed valid; checking them belongs to Level-5.
                String details = command.substring(DEADLINE_PREFIX.length());
                int byAt = details.indexOf(BY_SEPARATOR);
                String description = details.substring(0, byAt);
                String by = details.substring(byAt + BY_SEPARATOR.length());
                tasks[taskCount] = new Deadline(description, by);
                taskCount++;
                printAdded(tasks[taskCount - 1], taskCount);
            } else if (command.startsWith(EVENT_PREFIX)) {
                String details = command.substring(EVENT_PREFIX.length());
                int fromAt = details.indexOf(FROM_SEPARATOR);
                // Search for /to after /from so a description cannot hide the real marker.
                int toAt = details.indexOf(TO_SEPARATOR, fromAt + FROM_SEPARATOR.length());
                String description = details.substring(0, fromAt);
                String from = details.substring(fromAt + FROM_SEPARATOR.length(), toAt);
                String to = details.substring(toAt + TO_SEPARATOR.length());
                tasks[taskCount] = new Event(description, from, to);
                taskCount++;
                printAdded(tasks[taskCount - 1], taskCount);
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
     * Reports a newly added typed task and how many tasks are now stored.
     *
     * @param task the task that was just added
     * @param taskCount the number of tasks stored after the addition
     */
    private static void printAdded(Task task, int taskCount) {
        String noun = taskCount == 1 ? "task" : "tasks";
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " " + noun + " in the list.");
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
