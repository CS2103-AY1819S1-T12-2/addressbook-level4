package seedu.address.model.event;

/**
 * Represents a EventID for a CalendarEvent in the calendar for patient book.
 * EventID only increases, and each event is bound to its EventID forever.
 */
public class EventID {

    // EventID iterator
    private static int idCounter = 0;

    private static final String ID_VALIDATION_REGEX = "^e[0-9]+$";
    public final String value;

    /**
     * Constructs a {@code EventID}
     */
    public EventID() { value = getNewID(); }

    private static String getNewID() {
        String id = new String("e"+Integer.toString(idCounter));
        idCounter += 1;
        return id;
    }

    public static boolean isValidID(String test) { return test.matches(ID_VALIDATION_REGEX); }

    @Override
    public String toString() { return value; }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof EventID // instanceof handles nulls
                && value.equals(((EventID) other).value)); // state check
    }

    @Override
    public int hashCode() { return value.hashCode(); }
}
