package aster.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import aster.exception.AsterException;
import aster.storage.Storage;
import aster.task.TaskList;
import aster.task.Todo;
import aster.ui.Ui;

/**
 * Tests the exact messages a command naming a task by its number gives when that
 * number is missing or is not a number.
 *
 * <p>The messages are compared in full, and through two different commands, so that
 * restructuring how the usage example in them is built cannot change what the user
 * reads, or which keyword it names, without a test noticing.
 *
 * <p>Each command is refused before anything is saved, but it is still handed a store
 * pointed at a temporary directory, so the real {@code data/aster.txt} is never at risk.
 */
class IndexedCommandTest {
    @TempDir
    Path tempDir;

    @Test
    void execute_markWithoutNumber_throwsMissingNumberMessage() {
        AsterException thrown = assertThrows(AsterException.class, () -> execute(new MarkCommand("")));

        assertEquals("Tell me which task to mark. Try: mark 2", thrown.getMessage());
    }

    @Test
    void execute_deleteWithNonNumber_throwsNotANumberMessage() {
        AsterException thrown = assertThrows(AsterException.class, () -> execute(new DeleteCommand("x")));

        assertEquals("\"x\" is not a task number. Try: delete 2", thrown.getMessage());
    }

    // ---------- helpers ----------

    /**
     * Carries out the command on a list holding one task, so that the empty-list check
     * does not answer first, with its output collected and its store pointed at this
     * test's temporary directory.
     */
    private void execute(Command command) throws AsterException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        List<String> lines = new ArrayList<>();
        command.execute(tasks, new Ui(lines::add), new Storage(tempDir.resolve("aster.txt")));
    }
}
