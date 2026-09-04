package aster.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import aster.command.FindCommand;
import aster.exception.AsterException;

/**
 * Tests how {@link Parser#parse(String)} reads a {@code find} command.
 *
 * <p>{@code find} is one of the commands whose wording the parser checks in full, so
 * these cases cover both halves of that promise: a line carrying a keyword produces a
 * command, and a line carrying none is refused before any command object exists.
 *
 * <p>Only the type of the command is asserted, never the keyword it holds, because the
 * keyword is private and nothing in the production code is made more visible for a
 * test. What the command does with that keyword is covered by {@code FindCommandTest}.
 */
class ParserTest {

    @Test
    void parse_findWithKeyword_returnsFindCommand() throws AsterException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    void parse_findWithMultiWordKeyword_returnsFindCommand() throws AsterException {
        assertInstanceOf(FindCommand.class, Parser.parse("find project meeting"));
    }

    @Test
    void parse_findWithExtraSpaces_returnsFindCommand() throws AsterException {
        assertInstanceOf(FindCommand.class, Parser.parse("find    book"));
    }

    @Test
    void parse_findWithoutKeyword_throwsAsterException() {
        assertThrows(AsterException.class, () -> Parser.parse("find"));
    }

    @Test
    void parse_findWithBlankKeyword_throwsAsterException() {
        // The user interface trims the line, so a keyword of only spaces is the one way
        // an empty argument can still reach the parser.
        assertThrows(AsterException.class, () -> Parser.parse("find   "));
    }

    @Test
    void parse_unknownCommand_messageListsFindKeyword() {
        AsterException thrown =
                assertThrows(AsterException.class, () -> Parser.parse("bicycle"));

        assertTrue(thrown.getMessage().contains("find"),
                "the list of understood commands must name find: " + thrown.getMessage());
    }
}
