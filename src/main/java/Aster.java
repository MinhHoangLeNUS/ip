import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Aster chatbot.
 *
 * <p>Aster greets the user and reads commands until {@code bye}. The commands
 * {@code todo}, {@code deadline} and {@code event} add a task of the matching type,
 * {@code list} shows the stored tasks with their type and done status,
 * {@code mark <number>} and {@code unmark <number>} change the done status of one
 * task, and {@code delete <number>} removes one task. Anything else is refused with
 * an explanation: unrecognised commands, missing descriptions, missing, repeated or
 * out-of-order {@code /by}, {@code /from} and {@code /to} parts, and unusable task
 * numbers. A refused command leaves the task list unchanged. Tasks are held in
 * memory only; nothing is saved to disk.
 */
public class Aster {
    // Markers are matched as whole words, so wording inside a description cannot be
    // mistaken for one of them.
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";

    private static final String TODO_USAGE = "Try: todo read book";
    private static final String DEADLINE_USAGE = "Try: deadline return book /by Sunday";
    private static final String EVENT_USAGE = "Try: event project meeting /from Mon 2pm /to 4pm";

    /**
     * Greets the user, then reads commands from standard input until {@code bye} or
     * the end of input. Each command is handled in turn, and any command Aster cannot
     * carry out is reported without changing the task list.
     *
     * @param args command line arguments; not used
     */
    public static void main(String[] args) {
        final String divider = "____________________________________________________________";

        // An ArrayList grows as needed, so no capacity is assumed and the number of
        // tasks is always tasks.size() rather than a separately tracked count. It
        // holds every task type, since each subclass is also a Task.
        List<Task> tasks = new ArrayList<>();

        System.out.println(divider);
        System.out.println("Hello! I'm Aster.");
        System.out.println("I'm a simple chatbot, and I'm glad you're here.");
        System.out.println("What can I do for you?");
        System.out.println(divider);

        Scanner scanner = new Scanner(System.in);
        // hasNextLine() also ends the loop cleanly if the input stops before "bye".
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            // Only the exact command leaves; "bye" with anything after it is an error.
            if (command.equals(Command.BYE.keyword())) {
                break;
            }
            System.out.println(divider);
            // Every failure surfaces here, so nothing else prints errors, and the list
            // keeps its previous contents whenever a command is refused.
            try {
                handleCommand(command, tasks);
            } catch (AsterException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(divider);
        }

        System.out.println(divider);
        System.out.println("Goodbye for now. Take care!");
        System.out.println(divider);
    }

    /**
     * Carries out one command.
     *
     * <p>Every check happens before the task list is touched, so a command that throws
     * leaves the list exactly as it was.
     *
     * @param command the trimmed command line entered by the user
     * @param tasks the task list, modified in place by commands that add or remove
     * @throws AsterException if the command cannot be understood or carried out
     */
    private static void handleCommand(String command, List<Task> tasks) throws AsterException {
        // The command is already trimmed, so this separates the keyword from the rest.
        String[] parts = command.split("\\s+", 2);
        String keyword = parts[0];
        String arguments = parts.length > 1 ? parts[1] : "";

        // A blank line has no keyword to look up, so it is answered before the lookup.
        if (keyword.isEmpty()) {
            throw new AsterException("I didn't catch a command. Type list to see "
                    + "your tasks, or bye to leave.");
        }

        // Listing case null alongside the constants makes this switch exhaustive, so
        // the compiler reports any command added to Command but not handled here.
        switch (Command.fromKeyword(keyword)) {
            case null -> throw new AsterException("I don't recognise \"" + keyword + "\". I "
                    + "understand: " + Command.keywordList() + ".");
            case BYE -> throw new AsterException("To leave, type bye on its own, with "
                    + "nothing after it.");
            case LIST -> {
                requireNoArguments(arguments, Command.LIST);
                printList(tasks);
            }
            case MARK -> {
                Task task = tasks.get(parseTaskIndex(arguments, tasks, Command.MARK));
                task.markAsDone();
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("  " + task);
            }
            case UNMARK -> {
                Task task = tasks.get(parseTaskIndex(arguments, tasks, Command.UNMARK));
                task.markAsNotDone();
                System.out.println("Alright, I've marked this task as not done yet:");
                System.out.println("  " + task);
            }
            case DELETE -> {
                // The number is validated before anything is removed, so a refused
                // delete leaves the list unchanged. remove() then closes the gap, which
                // is what renumbers the remaining tasks in the next list.
                Task task = tasks.remove(parseTaskIndex(arguments, tasks, Command.DELETE));
                System.out.println("Noted. I've removed this task:");
                System.out.println("  " + task);
                printCount(tasks);
            }
            case TODO -> {
                String description = requireNonEmpty(arguments,
                        "A todo needs a description. " + TODO_USAGE);
                addTask(tasks, new Todo(description));
            }
            case DEADLINE -> addDeadline(tasks, arguments);
            case EVENT -> addEvent(tasks, arguments);
        }
    }

    /**
     * Adds the deadline described by the arguments of a {@code deadline} command.
     *
     * @param tasks the task list
     * @param arguments everything the user typed after the keyword
     * @throws AsterException if the description, the {@code /by} marker or its value is
     *     missing, or {@code /by} appears more than once
     */
    private static void addDeadline(List<Task> tasks, String arguments) throws AsterException {
        requireExactlyOne(arguments, BY_MARKER, "deadline", DEADLINE_USAGE);
        int byAt = indexOfMarker(arguments, BY_MARKER, 0);
        String description = requireNonEmpty(arguments.substring(0, byAt),
                "A deadline needs a description before /by. " + DEADLINE_USAGE);
        String by = requireNonEmpty(arguments.substring(byAt + BY_MARKER.length()),
                "The /by part needs a date or time after it. " + DEADLINE_USAGE);
        addTask(tasks, new Deadline(description, by));
    }

    /**
     * Adds the event described by the arguments of an {@code event} command.
     *
     * @param tasks the task list
     * @param arguments everything the user typed after the keyword
     * @throws AsterException if the description or either marker value is missing, if a
     *     marker is repeated, or if {@code /to} comes before {@code /from}
     */
    private static void addEvent(List<Task> tasks, String arguments) throws AsterException {
        requireExactlyOne(arguments, FROM_MARKER, "event", EVENT_USAGE);
        requireExactlyOne(arguments, TO_MARKER, "event", EVENT_USAGE);
        int fromAt = indexOfMarker(arguments, FROM_MARKER, 0);
        int toAt = indexOfMarker(arguments, TO_MARKER, 0);
        if (toAt < fromAt) {
            throw new AsterException("An event needs /from before /to. " + EVENT_USAGE);
        }
        String description = requireNonEmpty(arguments.substring(0, fromAt),
                "An event needs a description before /from. " + EVENT_USAGE);
        String from = requireNonEmpty(arguments.substring(fromAt + FROM_MARKER.length(), toAt),
                "The /from part needs a start time after it. " + EVENT_USAGE);
        String to = requireNonEmpty(arguments.substring(toAt + TO_MARKER.length()),
                "The /to part needs an end time after it. " + EVENT_USAGE);
        addTask(tasks, new Event(description, from, to));
    }

    /**
     * Stores a task and reports it together with the new number of stored tasks.
     *
     * @param tasks the task list
     * @param task the task to store
     */
    private static void addTask(List<Task> tasks, Task task) {
        // The list grows on demand, so there is no capacity to check before adding.
        tasks.add(task);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        printCount(tasks);
    }

    /**
     * Prints how many tasks the list holds, as shown after adding or removing one.
     *
     * @param tasks the task list
     */
    private static void printCount(List<Task> tasks) {
        int count = tasks.size();
        System.out.println("Now you have " + count + " " + taskNoun(count) + " in the list.");
    }

    /**
     * Prints the stored tasks in the order they were added.
     *
     * @param tasks the task list
     */
    private static void printList(List<Task> tasks) {
        // An empty list prints nothing, exactly as it did before Level-5.
        // Numbering shown to the user is 1-based, so it is offset from the index.
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Converts the task number in a mark, unmark or delete command into a list index.
     *
     * <p>Every way the number can be unusable is turned into an {@link AsterException},
     * so no {@code NumberFormatException} or index exception reaches the user.
     *
     * @param arguments everything the user typed after the keyword
     * @param tasks the task list the number refers to
     * @param command the command being carried out, named in the messages
     * @return the 0-based index of the task the command refers to
     * @throws AsterException if the number is missing, not a number, or outside the list
     */
    private static int parseTaskIndex(String arguments, List<Task> tasks, Command command)
            throws AsterException {
        String keyword = command.keyword();
        int taskCount = tasks.size();
        if (arguments.isEmpty()) {
            throw new AsterException("Tell me which task to " + keyword + ". Try: " + keyword
                    + " 2");
        }
        if (taskCount == 0) {
            throw new AsterException("Your list is empty, so there is nothing to " + keyword
                    + " yet.");
        }
        int number;
        try {
            number = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new AsterException("\"" + arguments + "\" is not a task number. Try: "
                    + keyword + " 2");
        }
        if (number < 1 || number > taskCount) {
            throw new AsterException("You have " + taskCount + " " + taskNoun(taskCount)
                    + ", so " + number + " is out of range. Pick a number from 1 to "
                    + taskCount + ".");
        }
        return number - 1;
    }

    /**
     * Checks that a command that takes no arguments was given none.
     *
     * @param arguments everything the user typed after the keyword
     * @param command the command being carried out, named in the message
     * @throws AsterException if anything followed the keyword
     */
    private static void requireNoArguments(String arguments, Command command)
            throws AsterException {
        if (!arguments.isEmpty()) {
            String keyword = command.keyword();
            throw new AsterException("The " + keyword + " command takes nothing after it. Type "
                    + keyword + " on its own.");
        }
    }

    /**
     * Returns the trimmed text, provided it is not blank.
     *
     * @param value the text to check
     * @param message the explanation to report if the text is blank
     * @return the text without surrounding spaces
     * @throws AsterException if the text is empty or only spaces
     */
    private static String requireNonEmpty(String value, String message) throws AsterException {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new AsterException(message);
        }
        return trimmed;
    }

    /**
     * Checks that a marker such as {@code /by} appears exactly once.
     *
     * @param arguments everything the user typed after the keyword
     * @param marker the marker to count
     * @param taskType the task type named in the messages
     * @param usage an example of the correct command
     * @throws AsterException if the marker is missing or appears more than once
     */
    private static void requireExactlyOne(String arguments, String marker, String taskType,
            String usage) throws AsterException {
        int count = countMarkers(arguments, marker);
        if (count == 0) {
            throw new AsterException("A" + article(taskType) + taskType + " needs a " + marker
                    + " part. " + usage);
        }
        if (count > 1) {
            throw new AsterException("A" + article(taskType) + taskType + " can have only one "
                    + marker + " part. " + usage);
        }
    }

    /**
     * Returns the letters that turn {@code "a"} into {@code "an"} where needed.
     *
     * @param word the word that follows the article
     * @return {@code "n "} before a vowel, otherwise {@code " "}
     */
    private static String article(String word) {
        return "aeiou".indexOf(word.charAt(0)) >= 0 ? "n " : " ";
    }

    /**
     * Counts how many times a marker appears as a whole word.
     *
     * @param text the text to search
     * @param marker the marker to count
     * @return the number of occurrences
     */
    private static int countMarkers(String text, String marker) {
        int count = 0;
        int at = indexOfMarker(text, marker, 0);
        while (at >= 0) {
            count++;
            at = indexOfMarker(text, marker, at + marker.length());
        }
        return count;
    }

    /**
     * Finds a marker that stands as a word of its own, so that wording such as
     * {@code /byte} inside a description is not mistaken for {@code /by}.
     *
     * @param text the text to search
     * @param marker the marker to find
     * @param fromIndex the position to start searching from
     * @return the index of the marker, or {@code -1} if it does not occur
     */
    private static int indexOfMarker(String text, String marker, int fromIndex) {
        int at = text.indexOf(marker, fromIndex);
        while (at >= 0) {
            boolean startsWord = at == 0 || Character.isWhitespace(text.charAt(at - 1));
            int after = at + marker.length();
            boolean endsWord = after == text.length() || Character.isWhitespace(text.charAt(after));
            if (startsWord && endsWord) {
                return at;
            }
            at = text.indexOf(marker, at + 1);
        }
        return -1;
    }

    /**
     * Returns the singular or plural form of {@code task} for a count.
     *
     * @param count the number of tasks
     * @return {@code "task"} if the count is one, otherwise {@code "tasks"}
     */
    private static String taskNoun(int count) {
        return count == 1 ? "task" : "tasks";
    }
}
