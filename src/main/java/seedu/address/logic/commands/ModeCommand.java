package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.SwitchToAppointmentEvent;
import seedu.address.commons.events.ui.SwitchToPatientEvent;
import seedu.address.logic.CommandHistory;
// import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBookModel;
import seedu.address.model.DiagnosisModel;
import seedu.address.model.ScheduleModel;



/**
 * Switch between Patient Mode and Calender Mode
 */
public class ModeCommand extends Command {

    public static final String COMMAND_WORD = "mode";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Switch between the patient mode and the appointment mode.\n"
            + "Parameters: MODE\n"

            + "Example: " + COMMAND_WORD + " " + CMDTYPE_PATIENT + "\n"
            + "Example: " + COMMAND_WORD + " " + CMDTYPE_APPOINTMENT + "\n";

    public static final String MESSAGE_CANNOT_SWITCH = "You are already in this mode.\n";
    private static final String MESSAGE_PATIENT_SWITCH_SUCCESS = "Switched to Patient Mode.\n";
    private static final String MESSAGE_SCHEDULE_SWITCH_SUCCESS = "Switched to Appointment Mode.\n";

    private final String cmdType;

    public ModeCommand(String cmdType) {
        this.cmdType = cmdType;
    }

    @Override
    public CommandResult execute(AddressBookModel addressBookModel, ScheduleModel scheduleModel,
                                 DiagnosisModel diagnosisModel, CommandHistory history) throws CommandException {
        requireNonNull(addressBookModel);
        requireNonNull(scheduleModel);

        if (this.cmdType.equals(CMDTYPE_PATIENT)) {
            EventsCenter.getInstance().post(new SwitchToPatientEvent());
            return new CommandResult(MESSAGE_PATIENT_SWITCH_SUCCESS);
        } else if (this.cmdType.equals(CMDTYPE_APPOINTMENT)) {
            EventsCenter.getInstance().post(new SwitchToAppointmentEvent());
            return new CommandResult(MESSAGE_SCHEDULE_SWITCH_SUCCESS);
        } else {
            throw new CommandException("Unexpected command type: should have been caught in ModeCommandParser.");
        }
    }
}
