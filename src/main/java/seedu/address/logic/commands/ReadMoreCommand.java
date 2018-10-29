package seedu.address.logic.commands;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBookModel;
import seedu.address.model.DiagnosisModel;
import seedu.address.model.ScheduleModel;
import seedu.address.model.DrugSearchUtility;

import static java.util.Objects.requireNonNull;

/**
 * Displays extended pharmacological information about a given drug from search results.
 */

public class ReadMoreCommand extends Command {

    public static final String COMMAND_WORD = "readmore";

    public static final String MESSAGE_USAGE = COMMAND_WORD + "Displays extended pharmacological information "
            + "about any matching drug from the most recent search of the drug database.\n"
            + "Parameters: INDEX (must be from the list of search results)...\n"
            + "Example: "
            + COMMAND_WORD
            + " readmore"
            + " 3\n";

    private final int searchIndex;

    public ReadMoreCommand(int searchIndex) {
        this.searchIndex = searchIndex;
    }

    @Override
    public CommandResult execute(AddressBookModel addressBookModel, ScheduleModel scheduleModel,
                                 DiagnosisModel diagnosisModel, CommandHistory history) throws CommandException {
        requireNonNull(addressBookModel);
        requireNonNull(scheduleModel);
        requireNonNull(diagnosisModel);

        CommandResult cmdResult = new CommandResult(DrugSearchUtility.readMore(searchIndex));
        if (cmdResult == null) {
            throw new CommandException("Please carry out a search using \"drug [drugname]\" first.");
        }
        else return cmdResult;
    }
}

