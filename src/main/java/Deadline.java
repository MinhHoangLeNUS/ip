/**
 * A task that must be done by a stated date or time.
 *
 * <p>The due date is kept as the text the user typed. Converting it to a real
 * date or time is not required at this stage, so any wording the user prefers
 * is accepted unchanged.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates a deadline with the given description and due date, initially not done.
     *
     * @param description text describing the task
     * @param by the due date or time, as written by the user
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the due date or time of this deadline.
     *
     * @return the due date or time, as written by the user.
     */
    public String getBy() {
        return by;
    }

    /**
     * Returns this deadline as its type marker, the inherited status and
     * description, and the due date, for example
     * {@code [D][ ] return book (by: June 6th)}.
     *
     * @return the display form of this deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
