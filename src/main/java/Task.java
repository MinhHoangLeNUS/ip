/**
 * A task tracked by Aster, consisting of a description and a done status.
 *
 * <p>A task starts out not done, and can be switched between the done and
 * not-done states. Its status is shown as {@code X} when done and as a blank
 * when not.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a task with the given description, initially not done.
     *
     * @param description text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon shown inside the status brackets.
     *
     * @return {@code "X"} if this task is done, otherwise a single space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns this task as its status icon followed by its description,
     * for example {@code [X] read book}.
     *
     * @return the display form of this task
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
