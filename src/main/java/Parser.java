import java.time.LocalDate;

/**
 * Makes sense of what the user typed.
 *
 * <p>This class works out which command was meant, and for {@code todo},
 * {@code deadline}, {@code event} and {@code list} it also checks the parts that
 * command needs: descriptions, markers, and whether the dates named are real dates.
 *
 * <p>{@code mark}, {@code unmark} and {@code delete} are a deliberate exception.
 * Nothing about their argument is checked here, not even whether one was given or
 * whether it is a number. Those checks run together in {@link IndexedCommand} when the
 * command is carried out, because one of them depends on how many tasks there are and
 * it has to be asked before the others; see that class for why.
 *
 * <p>Nothing here reads from or writes to the screen. It either produces the value a
 * command needs, or explains in an {@link AsterException} why it cannot.
 */
public final class Parser {
    // Markers are matched as whole words, so wording inside a description cannot be
    // mistaken for one of them.
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";

    private static final String TODO_USAGE = "Try: todo read book";
    private static final String DEADLINE_USAGE = "Try: deadline return book /by 2019-12-02";
    private static final String EVENT_USAGE = "Try: event project meeting /from 2019-12-02 "
            + "/to 2019-12-03";
    private static final String DATE_USAGE = "Dates go in the form yyyy-MM-dd, for example "
            + "2019-12-02.";

    /**
     * Prevents instances being created, since this class holds only static helpers.
     */
    private Parser() {
    }

    /**
     * Returns whether the given line is the command to leave.
     *
     * <p>This is asked before the exchange is framed, because leaving prints only the
     * parting words rather than a framed reply. Only the exact word leaves; {@code bye}
     * with anything after it is a mistake like any other, and is reported as one.
     *
     * @param fullCommand the trimmed command line entered by the user.
     * @return {@code true} if the user asked to leave.
     */
    public static boolean isExit(String fullCommand) {
        return fullCommand.equals(CommandType.BYE.keyword());
    }

    /**
     * Returns the command the given line asks for.
     *
     * <p>A command that is returned has a keyword Aster knows. For most commands its
     * wording has also been checked, but a returned {@link IndexedCommand} carries the
     * argument exactly as typed and has had nothing about it checked yet, so it may
     * still turn out to name no task once it runs.
     *
     * @param fullCommand the trimmed command line entered by the user.
     * @return the command to carry out.
     * @throws AsterException if the line names no command Aster knows, or if the parts
     *     a todo, deadline, event or list command needs are missing or malformed.
     */
    public static Command parse(String fullCommand) throws AsterException {
        // The line is already trimmed, so this separates the keyword from the rest.
        String[] parts = fullCommand.split("\\s+", 2);
        String keyword = parts[0];
        String arguments = parts.length > 1 ? parts[1] : "";

        // A blank line has no keyword to look up, so it is answered before the lookup.
        if (keyword.isEmpty()) {
            throw new AsterException("I didn't catch a command. Type list to see "
                    + "your tasks, or bye to leave.");
        }

        // Listing case null alongside the constants makes this switch exhaustive, so
        // the compiler reports any command added to CommandType but not handled here.
        return switch (CommandType.fromKeyword(keyword)) {
            case null -> throw new AsterException("I don't recognise \"" + keyword + "\". I "
                    + "understand: " + CommandType.keywordList() + ".");
            case BYE -> throw new AsterException("To leave, type bye on its own, with "
                    + "nothing after it.");
            case LIST -> {
                requireNoArguments(arguments, CommandType.LIST);
                yield new ListCommand();
            }
            case MARK -> new MarkCommand(arguments);
            case UNMARK -> new UnmarkCommand(arguments);
            case DELETE -> new DeleteCommand(arguments);
            case TODO -> new AddCommand(parseTodo(arguments));
            case DEADLINE -> new AddCommand(parseDeadline(arguments));
            case EVENT -> new AddCommand(parseEvent(arguments));
        };
    }

    /**
     * Returns the todo described by the arguments of a {@code todo} command.
     *
     * @param arguments everything the user typed after the keyword.
     * @return the todo to add.
     * @throws AsterException if the description is missing.
     */
    private static Todo parseTodo(String arguments) throws AsterException {
        return new Todo(requireNonEmpty(arguments, "A todo needs a description. " + TODO_USAGE));
    }

    /**
     * Returns the deadline described by the arguments of a {@code deadline} command.
     *
     * @param arguments everything the user typed after the keyword.
     * @return the deadline to add.
     * @throws AsterException if the description, the {@code /by} marker or its value is
     *     missing, if {@code /by} appears more than once, or if the value is not a date.
     */
    private static Deadline parseDeadline(String arguments) throws AsterException {
        requireExactlyOne(arguments, BY_MARKER, "deadline", DEADLINE_USAGE);
        int byAt = indexOfMarker(arguments, BY_MARKER, 0);
        String description = requireNonEmpty(arguments.substring(0, byAt),
                "A deadline needs a description before /by. " + DEADLINE_USAGE);
        String by = requireNonEmpty(arguments.substring(byAt + BY_MARKER.length()),
                "The /by part needs a date after it. " + DEADLINE_USAGE);
        return new Deadline(description, requireDate(by));
    }

    /**
     * Returns the event described by the arguments of an {@code event} command.
     *
     * @param arguments everything the user typed after the keyword.
     * @return the event to add.
     * @throws AsterException if the description or either marker value is missing, if a
     *     marker is repeated, if {@code /to} comes before {@code /from}, or if either
     *     value is not a date.
     */
    private static Event parseEvent(String arguments) throws AsterException {
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
                "The /from part needs a start date after it. " + EVENT_USAGE);
        String to = requireNonEmpty(arguments.substring(toAt + TO_MARKER.length()),
                "The /to part needs an end date after it. " + EVENT_USAGE);
        return new Event(description, requireDate(from), requireDate(to));
    }

    /**
     * Checks that a command that takes no arguments was given none.
     *
     * @param arguments everything the user typed after the keyword.
     * @param command the command being carried out, named in the message.
     * @throws AsterException if anything followed the keyword.
     */
    private static void requireNoArguments(String arguments, CommandType command)
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
     * @param value the text to check.
     * @param message the explanation to report if the text is blank.
     * @return the text without surrounding spaces.
     * @throws AsterException if the text is empty or only spaces.
     */
    private static String requireNonEmpty(String value, String message) throws AsterException {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new AsterException(message);
        }
        return trimmed;
    }

    /**
     * Returns the date the given text holds.
     *
     * <p>This mirrors {@link #requireNonEmpty}: it turns something the user typed into
     * the value a task needs, or explains why it cannot. Because it is called before
     * the task is created, text that is not a date leaves the task list untouched.
     *
     * @param value the text the user typed after a date marker.
     * @return the date that text holds.
     * @throws AsterException if the text is not a date written in the accepted form.
     */
    private static LocalDate requireDate(String value) throws AsterException {
        LocalDate date = TaskDates.parseOrNull(value);
        if (date == null) {
            throw new AsterException("I couldn't read \"" + value + "\" as a date. "
                    + DATE_USAGE);
        }
        return date;
    }

    /**
     * Checks that a marker such as {@code /by} appears exactly once.
     *
     * @param arguments everything the user typed after the keyword.
     * @param marker the marker to count.
     * @param taskType the task type named in the messages.
     * @param usage an example of the correct command.
     * @throws AsterException if the marker is missing or appears more than once.
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
     * @param word the word that follows the article.
     * @return {@code "n "} before a vowel, otherwise {@code " "}.
     */
    private static String article(String word) {
        return "aeiou".indexOf(word.charAt(0)) >= 0 ? "n " : " ";
    }

    /**
     * Counts how many times a marker appears as a whole word.
     *
     * @param text the text to search.
     * @param marker the marker to count.
     * @return the number of occurrences.
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
     * @param text the text to search.
     * @param marker the marker to find.
     * @param fromIndex the position to start searching from.
     * @return the index of the marker, or {@code -1} if it does not occur.
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
}
