package aster.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import aster.task.Todo;

/**
 * Tests that a {@link Ui} given a line writer sends every line it shows there, and that
 * the text interface's dividers still frame the greeting and the farewell.
 *
 * <p>Every case collects the lines into a list, so nothing here reads from the keyboard
 * or writes to the screen, and no standard stream is replaced.
 */
class UiTest {
    private static final String DIVIDER =
            "____________________________________________________________";

    @Test
    void constructorWithLineWriter_showAdded_sendsEachLine() {
        List<String> lines = new ArrayList<>();

        new Ui(lines::add).showAdded(new Todo("read book"), 1);

        assertEquals(List.of("Got it. I've added this task:",
                "  [T][ ] read book",
                "Now you have 1 task in the list."), lines);
    }

    @Test
    void showWelcome_lineWriter_framesGreetingWithDividers() {
        List<String> lines = new ArrayList<>();

        new Ui(lines::add).showWelcome();

        assertEquals(List.of(DIVIDER,
                "Hello! I'm Aster.",
                "I'm a simple chatbot, and I'm glad you're here.",
                "What can I do for you?",
                DIVIDER), lines);
    }

    @Test
    void showGoodbye_lineWriter_framesFarewellWithDividers() {
        List<String> lines = new ArrayList<>();

        new Ui(lines::add).showGoodbye();

        assertEquals(List.of(DIVIDER, "Goodbye for now. Take care!", DIVIDER), lines);
    }

    @Test
    void hasNextCommand_lineWriterConstructor_readsNothing() {
        // An interface built for collecting a reply must never wait on the keyboard.
        assertFalse(new Ui(line -> { }).hasNextCommand());
    }
}
