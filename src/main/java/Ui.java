import java.util.List;
import java.util.Scanner;

/**
 * Handles everything the user reads and types.
 *
 * <p>Every line Aster prints and every line it reads passes through here, so the
 * wording and the layout of the conversation are decided in one place. Nothing else
 * writes to the screen or reads from the keyboard.
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads from standard input and writes to standard
     * output.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns whether the user has typed another command.
     *
     * <p>This is also how the conversation ends cleanly when the input stops before
     * the user types {@code bye}.
     *
     * @return {@code true} if another line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next command line, without the spaces around it.
     *
     * @return the line the user typed, trimmed.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints the line that separates one exchange from the next.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Greets the user at the start of the conversation.
     */
    public void showWelcome() {
        showLine();
        System.out.println("Hello! I'm Aster.");
        System.out.println("I'm a simple chatbot, and I'm glad you're here.");
        System.out.println("What can I do for you?");
        showLine();
    }

    /**
     * Says goodbye at the end of the conversation.
     */
    public void showGoodbye() {
        showLine();
        System.out.println("Goodbye for now. Take care!");
        showLine();
    }

    /**
     * Reports that the saved tasks could not be read, framed on its own because no
     * conversation follows it.
     *
     * @param message the explanation to show the user.
     */
    public void showLoadingError(String message) {
        showLine();
        System.out.println(message);
        showLine();
    }

    /**
     * Reports that a command could not be carried out.
     *
     * @param message the explanation to show the user.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Reports a task that has just been added, and how many tasks there are now.
     *
     * @param task the task that was added.
     * @param taskCount the number of tasks in the list afterwards.
     */
    public void showAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        showCount(taskCount);
    }

    /**
     * Reports a task that has just been removed, and how many tasks are left.
     *
     * @param task the task that was removed.
     * @param taskCount the number of tasks in the list afterwards.
     */
    public void showRemoved(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        showCount(taskCount);
    }

    /**
     * Reports a task that has just been marked as done.
     *
     * @param task the task that was marked.
     */
    public void showMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Reports a task that has just been marked as not done.
     *
     * @param task the task that was marked.
     */
    public void showUnmarked(Task task) {
        System.out.println("Alright, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Shows the tasks in the order they were added.
     *
     * <p>An empty list prints nothing at all. The numbering shown to the user starts
     * at one, so it is offset from the position in the list.
     *
     * @param tasks the tasks to show.
     */
    public void showTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Prints how many tasks the list holds, as shown after adding or removing one.
     *
     * @param taskCount the number of tasks in the list.
     */
    private void showCount(int taskCount) {
        System.out.println("Now you have " + taskCount + " " + taskNoun(taskCount)
                + " in the list.");
    }

    /**
     * Returns the singular or plural form of {@code task} for a count.
     *
     * <p>Kept here rather than shared, so that no class outside the user interface has
     * to depend on it for its wording.
     *
     * @param count the number of tasks.
     * @return {@code "task"} if the count is one, otherwise {@code "tasks"}.
     */
    private static String taskNoun(int count) {
        return count == 1 ? "task" : "tasks";
    }
}
