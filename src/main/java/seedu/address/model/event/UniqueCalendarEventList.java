package seedu.address.model.event;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.event.exceptions.DuplicateCalendarEventException;
import seedu.address.model.event.exceptions.CalendarEventNotFoundException;

/**
 * A list of calendar events that enforces uniqueness between its elements and does not allow nulls.
 * A calendar is considered unique by comparing using {@code CalendarEvent#isSameEvent(CalendarEvent)}. As such, adding
 * and updating of calendar events uses CalendarEvent#isSameEvent(CalendarEvent) for equality so as to ensure that the
 * calendar event being added or updated is unique in terms of identity in the UniqueCalendarEventList. Removal of a calendar
 * event uses CalendarEvent#equals(Object) to ensure that the correct event is removed.
 *
 * Supports a minimal set of list operations.
 *
 * Developer note: Since we're using an ID-based system to distinguish between calendar events, both
 * CalendarEvent#isSameEvent(CalendarEvent) and CalendarEvent#equals(Object) match the events by ID. However, the
 * methods are left separate so future developers can utilize them.
 *
 * @see CalendarEvent#isSameEvent(CalendarEvent)
 */
public class UniqueCalendarEventList implements Iterable<CalendarEvent> {

    private final ObservableList<CalendarEvent> internalList = FXCollections.observableArrayList();

    /**
     * Returns true if the list contains an equivalent calendar event as the given argument
     */
    public boolean contains(CalendarEvent toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameEvent);
    }

    /**
     * adds a calendar event to the list.
     * The calendar event must not already exist in the list.
     */
    public void add(CalendarEvent toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateCalendarEventException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the calendar event {@code target} in the list with {@code editedEvent}.
     * {@code target} must exist in the list.
     * The event ID of {@code editedEvent} must not be the same as another existing event in the list.
     * Event ID is matched with {@code CalendarEvent#isSameEvent(CalendarEvent)}.
     */
    public void setCalendarEvent(CalendarEvent target, CalendarEvent editedEvent) {
        requireAllNonNull(target, editedEvent);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new CalendarEventNotFoundException();
        }

        if (!target.isSameEvent(editedEvent) && contains(editedEvent)) {
            throw new DuplicateCalendarEventException();
        }

        internalList.set(index, editedEvent);
    }

    /**
     * Removes the equivalent calendar event from the list.
     * The calendar event must exist in the list.
     */
    public void remove(CalendarEvent toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new CalendarEventNotFoundException();
        }
    }

    public void setCalendarEvents(UniqueCalendarEventList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code events}.
     * {@code events} must not contain duplicate events.
     */
    public void setCalendarEvents(List<CalendarEvent> events) {
        requireAllNonNull(events);
        if (!eventsAreUnique(events)) {
            throw new DuplicateCalendarEventException();
        }

        internalList.setAll(events);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<CalendarEvent> asUnmodifiableObservableList() {
        return FXCollections.unmodifiableObservableList(internalList);
    }

    @Override
    public Iterator<CalendarEvent> iterator() { return internalList.iterator(); }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueCalendarEventList // instanceof handles nulls
                && internalList.equals(((UniqueCalendarEventList) other).internalList));
    }

    @Override
    public int hashCode() { return internalList.hashCode(); }

    /**
     * Returns true if {@code events} contain only unique events.
     */
    private boolean eventsAreUnique(List<CalendarEvent> events) {
        for (int i = 0; i < events.size() - 1; i++) {
            for (int j = i + 1; j < events.size(); j++) {
                if (events.get(i).isSameEvent(events.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
