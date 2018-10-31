package seedu.address.model.event;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.Pair;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;


/**
 * Represents a Schedule Event in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class ScheduleEvent {

    // Standard datetime String format to be used by this application
    public static final SimpleDateFormat SDF;
    public static final SimpleDateFormat STORAGE_SDF;

    static {
        SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm - HH:mm");
        STORAGE_SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }

    /**
     * Enumerated Variable to represent calendar event attributes
     */
    private enum ScheduleEventProperty {
        DATETIME, PERSONID, DETAILS, TAGS
    }

    private final HashMap<ScheduleEventProperty, Object> attributes;


    // Identity fields
    private final EventId id;

    /**
     * Every field must be present and not null.
     */
    public ScheduleEvent(Pair<Calendar> date, PersonId personId, String details, Set<Tag> tags) {
        this(new EventId(), date, personId, details, tags);
    }

    public ScheduleEvent(EventId eventId, Pair<Calendar> date,
                         PersonId personId, String details, Set<Tag> tags) {
        requireAllNonNull(eventId, date, personId, details, tags);
        this.id = eventId;
        this.attributes = new HashMap<>();

        Pair<Calendar> scheduleEventDate = new Pair<>(date.getKey(), date.getValue());
        this.attributes.put(ScheduleEventProperty.DATETIME, scheduleEventDate);
        this.attributes.put(ScheduleEventProperty.PERSONID, personId);
        this.attributes.put(ScheduleEventProperty.DETAILS, details);

        Set<Tag> calendarEventTags = new HashSet<>(tags); // adds all tags into here
        this.attributes.put(ScheduleEventProperty.TAGS, calendarEventTags);
    }


    public EventId getId() {
        return this.id;
    }

    public Pair<Calendar> getDate() {
        Pair<?> returnedDate = (Pair<?>) this.attributes.get(ScheduleEventProperty.DATETIME);
        return (new Pair<>((Calendar) returnedDate.getKey(), (Calendar) returnedDate.getValue()));
    }

    public String getDateToString() {
        StringBuilder dateBuilder = new StringBuilder();
        dateBuilder.append(STORAGE_SDF.format(getDate().getKey().getTime()))
                   .append(" to ")
                   .append(STORAGE_SDF.format(getDate().getValue().getTime()));
        return dateBuilder.toString();
    }

    public PersonId getPersonId() {
        return (PersonId) this.attributes.get(ScheduleEventProperty.PERSONID);
    }

    public String getDetails() {
        return (String) this.attributes.get(ScheduleEventProperty.DETAILS);
    }

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
                && otherEvent.getId().equals(getId());
    }

    /**
     * Returns true if the event clashes with a given duration
     */
    public boolean isClashing(Pair<Calendar> duration) {
        boolean isClashing = false;
        Calendar thisStart = this.getDate().getKey();
        Calendar thisEnd = this.getDate().getValue();
        Calendar otherStart = duration.getKey();
        Calendar otherEnd = duration.getValue();
        if (thisStart.equals(otherStart)) { // if two start times are the same, they will clash definitely
            isClashing = true;
        }
        if (thisStart.before(otherStart) && otherStart.before(thisEnd)) {
            isClashing = true;
        }
        if (otherStart.before(thisStart) && thisStart.before(otherEnd)) {
            isClashing = true;
        }
        return isClashing;
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
        return otherEvent.getId().equals(getId());

    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Appointment ID: ")
                .append(getId())
                .append(" scheduled for patient ID: ")
                .append(getPersonId())
                .append(" during: ")
                .append(STORAGE_SDF.format(getDate().getKey().getTime()))
                .append(" to ")
                .append(STORAGE_SDF.format(getDate().getValue().getTime()))
                .append("\nDetails: ")
                .append(getDetails())
                .append("\nTags: ");
        getTags().forEach(builder::append);
        return builder.toString();
    }

}
