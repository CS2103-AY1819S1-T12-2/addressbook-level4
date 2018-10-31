package seedu.address.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * The API of the AddressBookModel component.
 */
public interface AddressBookModel {
    /**
     * {@code Predicate} that shows existing persons
     */
    Predicate<Person> PREDICATE_SHOW_ALL_EXISTING_PERSONS = Person::getExists;

    /**
     * {@code Predicate} that shows all persons, including undeleted ones.
     */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * Clears existing backing addressBookModel and replaces with the provided new data.
     */
    void resetData(ReadOnlyAddressBook newData);

    /**
     * Returns the AddressBook
     */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void updatePerson(Person target, Person editedPerson);

    /**
     * Returns an unmodifiable view of the filtered person list
     */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Internally used function to get a list of persons without updating the UI.
     * @param predicate Predicate to match.
     * @return list of persons.
     */
    ObservableList<Person> internalGetFromPersonList(Predicate<Person> predicate);

    /**
     * Finds a person by their Id. O
     * @param personId Lookup id.
     * @return Person object.
     * @throws PersonNotFoundException
     */
    Person getPersonById(PersonId personId) throws PersonNotFoundException;

}
