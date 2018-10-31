package seedu.address.model.person;

import static seedu.address.model.AddressBookModel.PREDICATE_SHOW_ALL_EXISTING_PERSONS;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;



/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 */
public class MatchPersonPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public MatchPersonPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        return PREDICATE_SHOW_ALL_EXISTING_PERSONS.test(person)
                && keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword))
                || keywords.stream().anyMatch(keyword -> person.getId().toString().equals(keyword));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof MatchPersonPredicate // instanceof handles nulls
                && keywords.equals(((MatchPersonPredicate) other).keywords)); // state check
    }

}
