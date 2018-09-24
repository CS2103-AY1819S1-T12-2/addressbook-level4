package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.CommandHistory;
import seedu.address.model.AddressBook;
import seedu.address.model.AddressBookModel;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";


    @Override
    public CommandResult execute(AddressBookModel addressBookModel, CommandHistory history) {
        requireNonNull(addressBookModel);
        addressBookModel.resetData(new AddressBook());
        addressBookModel.commitAddressBook();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
