package seedu.address.model.person;

/**
 * Represents a Person's PersonID in the address book.
 * PersonID only increases, and a person is bound to their PersonID forever.
 */
public class PersonID {

    // PersonID iterator
    private static int idCounter = 0;

    private static final String ID_VALIDATION_REGEX = "^p[0-9]+$";
    public final String value;

    /**
     * Constructs a {@code PersonID}.
     */
    public PersonID() {
        value = getNewID();
    }

    private static String getNewID(){
        String id = new String("p"+Integer.toString(idCounter));
        idCounter += 1;
        return id;
    }

    public static boolean isValidID(String test) { return test.matches(ID_VALIDATION_REGEX); }

    @Override
    public String toString() { return value; }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PersonID // instanceof handles nulls
                && value.equals(((PersonID) other).value)); // state check
    }

    @Override
    public int hashCode() { return value.hashCode(); }
}
