package aster;

import java.nio.file.Path;
import java.nio.file.Paths;

import aster.exception.AsterException;
import aster.parser.Parser;
import aster.storage.Storage;
import aster.task.TaskList;
import aster.ui.Ui;

/**
 * Entry point for the Aster chatbot.
 *
 * <p>Aster greets the user and reads commands until {@code bye}. The commands
 * {@code todo}, {@code deadline} and {@code event} add a task of the matching type,
 * {@code list} shows the stored tasks with their type and done status,
 * {@code find <keyword>} shows those whose description contains the keyword,
 * {@code mark <number>} and {@code unmark <number>} change the done status of one
 * task, and {@code delete <number>} removes one task. Anything else is refused with
 * an explanation: unrecognised commands, missing descriptions, missing, repeated or
 * out-of-order {@code /by}, {@code /from} and {@code /to} parts, dates not written
 * as {@code yyyy-MM-dd}, and unusable task numbers. A refused command leaves the
 * task list unchanged. The tasks are saved whenever the list changes, and read
 * back when Aster next starts, so the list survives leaving and returning.
 *
 * <p>This class holds the conversation together and nothing else: the {@link Ui} does
 * the talking, the {@link Parser} works out what was asked, a {@code Command} carries
 * it out on the {@link TaskList}, and the {@link Storage} keeps the tasks between
 * visits.
 */
public class Aster {
    // Kept relative, and built from its parts rather than written with a separator, so
    // it means the same thing on every operating system.
    private static final Path DATA_FILE = Paths.get("data", "aster.txt");

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Creates a chatbot that keeps its tasks in the given file.
     *
     * @param dataFile the file to read the tasks from and write them back to.
     */
    public Aster(Path dataFile) {
        this.ui = new Ui();
        this.storage = new Storage(dataFile);
    }

    /**
     * Reads any saved tasks, greets the user, then carries out commands until
     * {@code bye} or the end of input.
     *
     * <p>If the saved tasks cannot be read, Aster explains why and stops before taking
     * any command, so that a file which may still be worth keeping is not written over.
     *
     * <p>Every failure surfaces in one place here, so nothing else reports errors, and
     * the task list keeps its previous contents whenever a command is refused.
     */
    public void run() {
        try {
            tasks = new TaskList(storage.load());
        } catch (AsterException e) {
            ui.showLoadingError(e.getMessage());
            return;
        }

        ui.showWelcome();
        while (ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();
            if (Parser.isExit(fullCommand)) {
                break;
            }
            ui.showLine();
            try {
                Parser.parse(fullCommand).execute(tasks, ui, storage);
            } catch (AsterException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
        }
        ui.showGoodbye();
    }

    /**
     * Starts the chatbot.
     *
     * @param args command line arguments; not used.
     */
    public static void main(String[] args) {
        new Aster(DATA_FILE).run();
    }
}
