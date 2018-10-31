package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBookModel;
import seedu.address.model.DiagnosisModel;
import seedu.address.model.Schedule;
import seedu.address.model.ScheduleModel;

/**
 * Clears the address book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Schedule has been cleared!";
    public static final String MESSAGE_UNEXPECTED_PARAMETER = "Unexpected command parameter: "
            + "Should have been caught in ClearCommandParser.";
    public static final String MESSAGE_USAGE = "Can only clear schedule.";

    private final String cmdType;

    public ClearCommand(String cmdType) {
        this.cmdType = cmdType;
    }

    @Override
    public CommandResult execute(AddressBookModel addressBookModel, ScheduleModel scheduleModel,
                                 DiagnosisModel diagnosisModel, CommandHistory history) throws CommandException {
        requireNonNull(scheduleModel);

        if (this.cmdType.equals(CMDTYPE_APPOINTMENT)) {
            scheduleModel.resetData(new Schedule());
        } else {
            throw new CommandException(MESSAGE_UNEXPECTED_PARAMETER);
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
