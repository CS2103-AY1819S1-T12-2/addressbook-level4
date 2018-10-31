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
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.testutil.PersonBuilder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.DeleteCommand.MESSAGE_INVALID_PERSON_ID;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;
import static seedu.address.model.AddressBookModel.PREDICATE_SHOW_ALL_PERSONS;

public class DeleteCommandTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    void delete_patient_success() throws Exception {
        AddressBookModel model = new AddressBookModelManager();
        Person person = new PersonBuilder().build();
        model.addPerson(person);
        PersonId personId = person.getId();
        assertDeletePatientSuccess(model, personId.toString());
    }

    @Test
    void delete_patient_invalidId_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(MESSAGE_INVALID_PERSON_ID);
        AddressBookModel model = new AddressBookModelManager();

        triggerDeletePatientFailure(model, "invalidID");
    }

    @Test
    void delete_patient_idNonExistent_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);

        // these two people have different IDs as the PersonID assigned is a static variable
        // which is incremented after p1 is instantiated.
        Person p1 = new PersonBuilder().build();
        Person p2 = new PersonBuilder().build();

        thrown.expectMessage(String.format(DeleteCommand.MESSAGE_PERSON_ID_NOT_FOUND,
                p2.getId().toString()));

        AddressBookModel model = new AddressBookModelManager();
        model.addPerson(p1);

        triggerDeletePatientFailure(model, p2.getId().toString());
    }

    @Test
    void delete_invalidCmdType() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(DeleteCommand.MESSAGE_UNEXPECTED_CMDTYPE);

        new DeleteCommand("invalid", "ignored")
                .execute(new AddressBookModelManager(),
                        new ScheduleModelManager(),
                        new DiagnosisModelManager(),
                        new CommandHistory());
    }

    private void assertDeletePatientSuccess(AddressBookModel addressBookModel, String target)
            throws Exception {

        ScheduleModel scheduleModel = new ScheduleModelManager();
        DiagnosisModel diagnosisModel = new DiagnosisModelManager();
        CommandHistory commandHistory =  new CommandHistory();

        DeleteCommand cmd = new DeleteCommand(CMDTYPE_PATIENT, target);

        CommandResult result = cmd.execute(addressBookModel, scheduleModel,
                diagnosisModel, commandHistory);

        addressBookModel.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // the first and only entry should still be present, but soft-deleted
        // it should also produce the correct success message.
        assertTrue(addressBookModel.getFilteredPersonList().size() == 1);
        assertFalse(addressBookModel.getFilteredPersonList().get(0).getExists());
        assertEquals(result, new CommandResult(String.format(DeleteCommand.MESSAGE_DELETE_EVENT_SUCCESS,
                target)));
    }

    private void triggerDeletePatientFailure(AddressBookModel addressBookModel, String target)
            throws Exception {

        ScheduleModel scheduleModel = new ScheduleModelManager();
        DiagnosisModel diagnosisModel = new DiagnosisModelManager();
        CommandHistory commandHistory =  new CommandHistory();

        DeleteCommand cmd = new DeleteCommand(CMDTYPE_PATIENT, target);

        cmd.execute(addressBookModel, scheduleModel, diagnosisModel, commandHistory);

    }
}
