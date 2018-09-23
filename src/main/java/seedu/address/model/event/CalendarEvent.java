package seedu.address.model.event;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.model.person.PersonID;
import seedu.address.model.tag.Tag;

/**
 * Represents a Calendar Event in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class CalendarEvent {

    // Enumerated Variable to represent calendar event attributes
    private enum CalendarEventProperty {DATE, PERSONID, DETAILS, TAGS}
    private final HashMap<CalendarEventProperty, Object> attributes;


    // Identity fields
    private final EventID id;

    /**
     *
     * Every field must be present and not null.
     */
    public CalendarEvent(Date date, PersonID personID, String details, Set<Tag> tags) {
        requireAllNonNull(date, personID, details, tags);
        this.id = new EventID();
        this.attributes = new HashMap<>();
        this.attributes.put(CalendarEventProperty.DATE, date);
        this.attributes.put(CalendarEventProperty.PERSONID, personID);
        this.attributes.put(CalendarEventProperty.DETAILS, details);

        Set<Tag> calendarEventTags = new HashSet<>(tags); // adds all tags into here
        this.attributes.put(CalendarEventProperty.TAGS, calendarEventTags);
    }

    public EventID getID() { return this.id; }

    public Date getDate() { return (Date) this.attributes.get(CalendarEventProperty.DATE); }

    public PersonID getPersonID() { return (PersonID) this.attributes.get(CalendarEventProperty.PERSONID); }

    public String getDetails() { return (String) this.attributes.get(CalendarEventProperty.DETAILS); }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    @SuppressWarnings("unchecked")
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet((Set<Tag>) this.attributes.get(CalendarEventProperty.TAGS));
    }

    /**
     * Returns true if both events have the same ID, even if they have different attributes.
     */
    public boolean isSameEvent(CalendarEvent otherEvent) {
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

        if (!(other instanceof CalendarEvent)) {
            return false;
        }

        CalendarEvent otherEvent = (CalendarEvent) other;
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
        builder.append("CalendarEvent (Appointment) for PersonID: ")
                .append(getID())
                .append(" at ")
                .append(getDate())
                .append("\nDetails: ")
                .append(getDetails())
                .append("\nTags: ");
        getTags().forEach(builder::append);
        return builder.toString();
    }

}
