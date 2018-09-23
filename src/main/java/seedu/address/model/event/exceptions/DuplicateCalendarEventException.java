package seedu.address.model.event.exceptions;

/**
 * Signals that the operation will result in duplicate Calendar Events (Calendar Events are considered duplicates if they have the same
 * identity).
 */
public class DuplicateCalendarEventException extends RuntimeException{
    public DuplicateCalendarEventException() { super ("Operation would result in duplicate calendar event"); }
}


