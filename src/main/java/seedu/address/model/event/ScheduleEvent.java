package seedu.address.model.event;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javafx.util.Pair;

import seedu.address.model.person.PersonID;
import seedu.address.model.tag.Tag;

/**
 * Represents a Schedule Event in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class ScheduleEvent {

    // Enumerated Variable to represent calendar event attributes
    private enum ScheduleEventProperty {
        DATETIME, PERSONID, DETAILS, TAGS}
    private final HashMap<ScheduleEventProperty, Object> attributes;


    // Identity fields
    private final EventID id;

    /**
     *
     * Every field must be present and not null.
     */
    public ScheduleEvent(Pair<Calendar, Calendar> date, PersonID personID, String details, Set<Tag> tags) {
        requireAllNonNull(date, personID, details, tags);
        this.id = new EventID();
        this.attributes = new HashMap<>();

        Pair<Calendar, Calendar> scheduleEventDate = new Pair<>(date.getKey(), date.getValue());
        this.attributes.put(ScheduleEventProperty.DATETIME, scheduleEventDate);
        this.attributes.put(ScheduleEventProperty.PERSONID, personID);
        this.attributes.put(ScheduleEventProperty.DETAILS, details);

        Set<Tag> calendarEventTags = new HashSet<>(tags); // adds all tags into here
        this.attributes.put(ScheduleEventProperty.TAGS, calendarEventTags);
    }

    public EventID getID() { return this.id; }

    public Pair<Calendar, Calendar> getDate() {
        Pair<?, ?> returnedDate = (Pair<?, ?>) this.attributes.get(ScheduleEventProperty.DATETIME);
        return (new Pair<>((Calendar) returnedDate.getKey(), (Calendar) returnedDate.getValue()));
    }

    public PersonID getPersonID() { return (PersonID) this.attributes.get(ScheduleEventProperty.PERSONID); }

    public String getDetails() { return (String) this.attributes.get(ScheduleEventProperty.DETAILS); }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    @SuppressWarnings("unchecked")
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet((Set<Tag>) this.attributes.get(ScheduleEventProperty.TAGS));
    }

    /**
     * Returns true if both events have the same ID, even if they have different attributes.
     */
    public boolean isSameEvent(ScheduleEvent otherEvent) {
        if (otherEvent == this) {
            return true;
        }

        return otherEvent != null
                && otherEvent.getID().equals(getID());
    }

    /**
     * Returns true if both persons have the same ID.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ScheduleEvent)) {
            return false;
        }

        ScheduleEvent otherEvent = (ScheduleEvent) other;
        return otherEvent.getID().equals(getID());

    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ScheduleEvent (Appointment) for PersonID: ")
                .append(getID())
                .append(" for daterange ")
                .append(getDate().getKey())
                .append(" to ")
                .append(getDate().getValue())
                .append("\nDetails: ")
                .append(getDetails())
                .append("\nTags: ");
        getTags().forEach(builder::append);
        return builder.toString();
    }

}