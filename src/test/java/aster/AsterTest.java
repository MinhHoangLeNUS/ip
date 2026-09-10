package aster;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the two ways of talking to {@link Aster}: one message at a time through
 * {@link Aster#startConversation()} and {@link Aster#getResponse(String)}, and a whole
 * conversation in the terminal through {@link Aster#run()}.
 *
 * <p>The one-message cases check replies exactly as a graphical interface receives
 * them, so they also pin down that a reply carries no dividers. The terminal case
 * checks a scripted session line by line, including the empty list, so the refactoring
 * behind the one-message boundary cannot change what the terminal shows.
 *
 * <p>Every test keeps its tasks in its own temporary directory, so the real
 * {@code data/aster.txt} is never touched. Only the terminal case stands in for the
 * standard streams, and both are restored after every test.
 */
class AsterTest {
    private static final String DATA_FILE_NAME = "aster.txt";
    private static final String DIVIDER =
            "____________________________________________________________";
    private static final String GREETING = "Hello! I'm Aster.\n"
            + "I'm a simple chatbot, and I'm glad you're here.\n"
            + "What can I do for you?";

    @TempDir
    Path tempDir;

    private InputStream realIn;
    private PrintStream realOut;

    @BeforeEach
    void rememberStandardStreams() {
        realIn = System.in;
        realOut = System.out;
    }

    @AfterEach
    void restoreStandardStreams() {
        System.setIn(realIn);
        System.setOut(realOut);
    }

    // ---------- startConversation ----------

    @Test
    void startConversation_noSavedTasks_returnsGreetingNotExit() {
        Response opening = newAster().startConversation();

        assertEquals(GREETING, opening.message());
        assertFalse(opening.isExit());
    }

    @Test
    void startConversation_unreadableFile_returnsLoadingErrorAsExitAndLeavesFile()
            throws IOException {
        Path file = tempDir.resolve(DATA_FILE_NAME);
        Files.writeString(file, "X | 0 | bad\n", StandardCharsets.UTF_8);
        byte[] before = Files.readAllBytes(file);
        Aster aster = new Aster(file);

        Response opening = aster.startConversation();

        assertTrue(opening.isExit());
        assertTrue(opening.message().startsWith("I couldn't read your saved tasks from "),
                "the reply must explain the loading failure: " + opening.message());
        assertThrows(IllegalStateException.class, () -> aster.getResponse("list"),
                "no command may run after the tasks failed to load");
        assertArrayEquals(before, Files.readAllBytes(file),
                "the unreadable file must be left exactly as it was");
    }

    // ---------- getResponse ----------

    @Test
    void getResponse_beforeStart_throwsIllegalStateException() {
        Aster aster = newAster();

        assertThrows(IllegalStateException.class, () -> aster.getResponse("list"));
    }

    @Test
    void getResponse_todo_returnsAddedReplyWithoutDividers() {
        Response reply = startedAster().getResponse("todo read book");

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 task in the list.", reply.message());
        assertFalse(reply.isExit());
    }

    @Test
    void getResponse_todoThenRestart_taskIsLoadedAgain() {
        startedAster().getResponse("todo read book");

        // A second Aster, so the task can only have come back from the saved file.
        Response listed = startedAster().getResponse("list");

        assertEquals("1. [T][ ] read book", listed.message());
    }

    @Test
    void getResponse_unknownCommand_returnsErrorNotExit() {
        Response reply = startedAster().getResponse("blah");

        assertEquals("I don't recognize \"blah\". I understand: todo, deadline, event, list, "
                + "find, mark, unmark, delete and bye.", reply.message());
        assertFalse(reply.isExit());
    }

    @Test
    void getResponse_bye_returnsFarewellAsExit() {
        Response reply = startedAster().getResponse("bye");

        assertEquals("Goodbye for now. Take care!", reply.message());
        assertTrue(reply.isExit());
    }

    @Test
    void getResponse_byeWithExtraWords_returnsErrorNotExit() {
        Response reply = startedAster().getResponse("bye now");

        assertEquals("To leave, type bye on its own, with nothing after it.", reply.message());
        assertFalse(reply.isExit());
    }

    @Test
    void getResponse_surroundingSpaces_isTrimmed() {
        Response reply = startedAster().getResponse("  bye \t");

        assertEquals("Goodbye for now. Take care!", reply.message());
        assertTrue(reply.isExit());
    }

    @Test
    void getResponse_emptyList_returnsEmptyListReply() {
        Response reply = startedAster().getResponse("list");

        assertEquals("Your list is empty.", reply.message());
        assertFalse(reply.isExit());
    }

    @Test
    void getResponse_blankInput_returnsNoCommandError() {
        Response reply = startedAster().getResponse("   ");

        assertEquals("I didn't catch a command. Type list to see your tasks, or bye to leave.",
                reply.message());
        assertFalse(reply.isExit());
    }

    // ---------- run ----------

    @Test
    void run_scriptedSession_printsFramedTranscript() {
        System.setIn(new ByteArrayInputStream(
                "list\ntodo read book\nlist\nbye\n".getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));

        // Created after standard input is replaced, because Aster reads the input it
        // finds when it is created.
        newAster().run();

        // The empty list shows only its two dividers here: the empty-list reply belongs
        // to the one-message boundary alone.
        assertEquals(List.of(DIVIDER,
                "Hello! I'm Aster.",
                "I'm a simple chatbot, and I'm glad you're here.",
                "What can I do for you?",
                DIVIDER,
                DIVIDER,
                DIVIDER,
                DIVIDER,
                "Got it. I've added this task:",
                "  [T][ ] read book",
                "Now you have 1 task in the list.",
                DIVIDER,
                DIVIDER,
                "1. [T][ ] read book",
                DIVIDER,
                DIVIDER,
                "Goodbye for now. Take care!",
                DIVIDER), printedLines(printed));
    }

    // ---------- helpers ----------

    /**
     * Returns an Aster that keeps its tasks in this test's temporary directory.
     *
     * @return a chatbot whose conversation has not started.
     */
    private Aster newAster() {
        return new Aster(tempDir.resolve(DATA_FILE_NAME));
    }

    /**
     * Returns an Aster whose conversation has started with the tasks already saved in
     * this test's temporary directory.
     *
     * @return a chatbot ready to answer messages.
     */
    private Aster startedAster() {
        Aster aster = newAster();
        Response opening = aster.startConversation();
        assertFalse(opening.isExit(), "the conversation must start: " + opening.message());
        return aster;
    }

    /**
     * Returns the lines printed to the given stream.
     *
     * @param printed the stream standing in for standard output.
     * @return the printed lines, without their line endings.
     */
    private static List<String> printedLines(ByteArrayOutputStream printed) {
        return List.of(printed.toString(StandardCharsets.UTF_8).split("\\R"));
    }
}
