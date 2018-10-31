package seedu.address.logic.commands;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import seedu.address.commons.core.Messages;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBookModel;
import seedu.address.model.AddressBookModelManager;
import seedu.address.model.DiagnosisModel;
import seedu.address.model.DiagnosisModelManager;
import seedu.address.model.ScheduleModel;
import seedu.address.model.ScheduleModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;

public class FindCommandTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    void find_patient_success() throws Exception {
        AddressBookModel model = new AddressBookModelManager();
        Person p1 = new PersonBuilder().withName(VALID_NAME_AMY).build();
        Person p2 = new PersonBuilder().withName(VALID_NAME_BOB).build();

        // ensure that only p1 is found in the filtered list
        assertFoundInAddressBook(model, VALID_NAME_AMY);
        assertTrue(model.getFilteredPersonList().size() == 1);
        assertEquals(model.getFilteredPersonList().get(0), p1);


        // reset filtered list for next test
        model.updateFilteredPersonList(AddressBookModel.PREDICATE_SHOW_ALL_EXISTING_PERSONS);

        // ensure that only p2 is found in the filtered list
        assertFoundInAddressBook(model, p2.getId().toString());
        assertTrue(model.getFilteredPersonList().size() == 1);
        assertEquals(model.getFilteredPersonList().get(0), p2);


        // reset filtered list for next test
        model.updateFilteredPersonList(AddressBookModel.PREDICATE_SHOW_ALL_EXISTING_PERSONS);

        // ensure that both amy and bob are both found in filtered list
        assertFoundInAddressBook(model, VALID_NAME_AMY + " " + VALID_NAME_BOB);
        assertTrue(model.getFilteredPersonList().size() == 2);


        // reset filtered list for next test
        model.updateFilteredPersonList(AddressBookModel.PREDICATE_SHOW_ALL_EXISTING_PERSONS);

        // ensure that there are no entries in filtered list
        assertFoundInAddressBook(model, "other name");
        assertTrue(model.getFilteredPersonList().size() == 0);
    }

    @Test
    void find_invalidParameter() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(FindCommand.MESSAGE_UNEXPECTED_PARAMETER);

        new FindCommand("invalid", "ignored").execute(new AddressBookModelManager(),
                new ScheduleModelManager(),
                new DiagnosisModelManager(),
                new CommandHistory());
    }

    private void assertFoundInAddressBook(AddressBookModel addressBookModel, String searchString)
            throws Exception {

        ScheduleModel scheduleModel = new ScheduleModelManager();
        DiagnosisModel diagnosisModel = new DiagnosisModelManager();
        CommandHistory commandHistory = new CommandHistory();

        FindCommand cmd = new FindCommand(CMDTYPE_PATIENT, searchString);

        assertEquals(cmd.execute(addressBookModel, scheduleModel, diagnosisModel, commandHistory),
                new CommandResult(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW,
                        addressBookModel.getFilteredPersonList().size())));
    }
}
