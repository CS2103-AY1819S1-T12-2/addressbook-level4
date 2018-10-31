package seedu.address.logic.commands;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBookModel;
import seedu.address.model.AddressBookModelManager;
import seedu.address.model.DiagnosisModel;
import seedu.address.model.DiagnosisModelManager;
import seedu.address.model.ScheduleModel;
import seedu.address.model.ScheduleModelManager;

import static org.junit.Assert.assertEquals;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;

public class ClearCommandTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    void clear_schedule_success() throws Exception {

        AddressBookModel addressBookModel = new AddressBookModelManager();
        ScheduleModel scheduleModel = new ScheduleModelManager();
        DiagnosisModel diagnosisModel = new DiagnosisModelManager();
        CommandHistory commandHistory =  new CommandHistory();

        // TODO: add a couple scheduleEvent objects here

        ClearCommand cmd = new ClearCommand(CMDTYPE_APPOINTMENT);
        CommandResult result = cmd.execute(addressBookModel, scheduleModel,
                diagnosisModel, commandHistory);

        // we check that the scheduleModel looks like a new one and is empty.
        assertEquals(scheduleModel, new ScheduleModelManager());
        assertEquals(result, new CommandResult(ClearCommand.MESSAGE_SUCCESS));
    }

    @Test
    void clear_invalidParameter_throwsCommandException() throws Exception{
        thrown.expect(CommandException.class);
        thrown.expectMessage(ClearCommand.MESSAGE_UNEXPECTED_PARAMETER);

        // we don't allow clearing the address book, by nature of this application
        new ClearCommand(CMDTYPE_PATIENT).execute(new AddressBookModelManager(),
                new ScheduleModelManager(),
                new DiagnosisModelManager(),
                new CommandHistory());
    }

}
