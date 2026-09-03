/**
 * A task that spans a stated start and end point in time.
 *
 * <p>Both endpoints are kept as the text the user typed, for the same reason
 * as in {@link Deadline}: no date conversion is required at this stage.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event with the given description and endpoints, initially not done.
     *
     * @param description text describing the task
     * @param from the start date or time, as written by the user
     * @param to the end date or time, as written by the user
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date or time of this event.
     *
     * @return the start date or time, as written by the user.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the end date or time of this event.
     *
     * @return the end date or time, as written by the user.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns this event as its type marker, the inherited status and
     * description, and both endpoints, for example
     * {@code [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)}.
     *
     * @return the display form of this event
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
