package seedu.address.model.event;

import java.util.function.Predicate;

/**
 * Tests that a {@code CalendarEvent}'s {@code Date} satisfies the constraints given.
 */
public class CalendarEventMatchesPredicate implements Predicate<CalendarEvent>{

    public final String constraints;

    /**
     * Change the argument to something which is used to match with this event.
     * It's currently set to String
     */
    public CalendarEventMatchesPredicate(String constraints) { this.constraints = constraints; }


    /**
     * TODO: Change this to however we match the event to the constraints
     */
    @Override
    public boolean test(CalendarEvent event) {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CalendarEventMatchesPredicate // instance of handles nulls
                && constraints.equals(((CalendarEventMatchesPredicate) other).constraints)); // state check
    }
}
