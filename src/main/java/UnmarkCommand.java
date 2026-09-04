/**
 * Marks one task as not done.
 */
public class UnmarkCommand extends IndexedCommand {
    /**
     * Creates a command that unmarks the task the given text names.
     *
     * @param arguments everything the user typed after the keyword.
     */
    public UnmarkCommand(String arguments) {
        super(CommandType.UNMARK, arguments);
    }

    /**
     * Marks the named task as not done, reports it, and saves the list.
     *
     * @param tasks the task list holding the task.
     * @param ui the user interface to report through.
     * @param storage the store to save the changed list to.
     * @throws AsterException if the number does not name a task, or if the changed list
     *     cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AsterException {
        Task task = tasks.get(resolveIndex(tasks));
        task.markAsNotDone();
        ui.showUnmarked(task);
        storage.save(tasks.asList());
    }
}
