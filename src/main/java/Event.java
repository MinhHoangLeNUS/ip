import java.time.LocalDate;

/**
 * A task that spans a stated start and end date.
 *
 * <p>Both endpoints are held as real dates, for the same reason as in
 * {@link Deadline}: Aster can then tell a date from any other wording and show it in
 * a friendlier form than the one it is typed in.
 *
 * <p>The end is not required to fall on or after the start. Nothing in the task
 * description says it must, so an event that reads backwards is accepted rather than
 * refused on a rule Aster invented.
 */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event with the given description and endpoints, initially not done.
     *
     * @param description text describing the task.
     * @param from the date the event starts on.
     * @param to the date the event ends on.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date of this event.
     *
     * @return the date the event starts on.
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the end date of this event.
     *
     * @return the date the event ends on.
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Returns this event as its type marker, the inherited status and
     * description, and both endpoints, for example
     * {@code [E][ ] project meeting (from: Aug 06 2019 to: Aug 08 2019)}.
     *
     * @return the display form of this event.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + TaskDates.format(from)
                + " to: " + TaskDates.format(to) + ")";
    }
}
