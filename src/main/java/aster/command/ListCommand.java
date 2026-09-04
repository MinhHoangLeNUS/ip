package aster.command;

import aster.storage.Storage;
import aster.task.TaskList;
import aster.ui.Ui;

/**
 * Shows the tasks currently in the list.
 *
 * <p>This is the only command that leaves the list as it was, so it is also the only
 * one that does not save.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that shows the whole task list.
     */
    public ListCommand() {
    }

    /**
     * Shows the tasks in the order they were added.
     *
     * @param tasks the task list to show.
     * @param ui the user interface to show it through.
     * @param storage the store; unused, because showing the list changes nothing.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasks(tasks.asList());
    }
}
