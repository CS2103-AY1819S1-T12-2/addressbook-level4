package seedu.address.logic.commands;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.ArgumentTokenizer;
import seedu.address.model.AddressBookModel;
import seedu.address.model.AddressBookModelManager;
import seedu.address.model.DiagnosisModel;
import seedu.address.model.DiagnosisModelManager;
import seedu.address.model.ScheduleModel;
import seedu.address.model.ScheduleModelManager;
import seedu.address.model.event.ScheduleEvent;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

import static org.junit.Assert.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.AddCommand.MESSAGE_USAGE_PERSON;
import static seedu.address.logic.commands.CommandTestUtil.*;
import static seedu.address.logic.commands.EditCommand.MESSAGE_CANNOT_EDIT_DELETED;
import static seedu.address.logic.commands.EditCommand.MESSAGE_INVALID_FORMAT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;
import static seedu.address.logic.parser.PersonCliSyntax.*;
import static seedu.address.logic.parser.ScheduleEventCliSyntax.PREFIX_DETAILS;
import static seedu.address.testutil.PersonUtil.matchProperties;

public class EditCommandTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // --- start of address book tests ---

    @Test
    public void parse_patient_success() throws Exception {
        // edit name
        Person person = new PersonBuilder().build();
        Person editedPerson = new PersonBuilder().withName(VALID_NAME_AMY).build();
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(PREFIX_NAME
                + VALID_NAME_AMY, PREFIX_NAME);
        assertTrue(matchProperties(editedPerson, testAddressBookModel(person, argMultimap)));

        // edit phone
        person = new PersonBuilder().build();
        editedPerson = new PersonBuilder().withPhone(VALID_PHONE_AMY).build();
        argMultimap = ArgumentTokenizer.tokenize(PREFIX_PHONE
                + VALID_PHONE_AMY, PREFIX_PHONE);
        assertTrue(matchProperties(editedPerson, testAddressBookModel(person, argMultimap)));

        // edit email
        person = new PersonBuilder().build();
        editedPerson = new PersonBuilder().withEmail(VALID_EMAIL_AMY).build();
        argMultimap = ArgumentTokenizer.tokenize(PREFIX_EMAIL
                + VALID_EMAIL_AMY, PREFIX_EMAIL);
        assertTrue(matchProperties(editedPerson, testAddressBookModel(person, argMultimap)));

        // add tag
        person = new PersonBuilder().build();
        editedPerson = new PersonBuilder().withTags(VALID_TAG_FRIEND).build();
        argMultimap = ArgumentTokenizer.tokenize(PREFIX_TAG + VALID_TAG_FRIEND,
                PREFIX_TAG);
        assertTrue(matchProperties(editedPerson, testAddressBookModel(person, argMultimap)));

        // remove tags
        person = new PersonBuilder().withTags(VALID_TAG_FRIEND).build();
        editedPerson = new PersonBuilder().build();
        argMultimap = ArgumentTokenizer.tokenize(PREFIX_TAG.toString(), PREFIX_TAG);
        assertTrue(matchProperties(editedPerson, testAddressBookModel(person, argMultimap)));
    }

    @Test
    void parse_patient_multipleRepeatedFields_acceptsLast() throws Exception {
        Person person = new PersonBuilder().build();
        Person editedPerson = new PersonBuilder()
                .withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB)
                .build();
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(PREFIX_NAME + VALID_NAME_AMY + " "
                + PREFIX_PHONE + VALID_PHONE_AMY + " "
                + PREFIX_NAME + VALID_NAME_BOB + " "
                + PREFIX_PHONE + VALID_PHONE_BOB);
        assertTrue(matchProperties(editedPerson, testAddressBookModel(person, argMultimap)));
    }
    @Test
    void parse_patient_invalidPatientId() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(EditCommand.MESSAGE_INVALID_PATIENT_ID);
        EditCommand cmd = new EditCommand(CMDTYPE_PATIENT, "invalidID",
                ArgumentTokenizer.tokenize(PREFIX_NAME + VALID_NAME_AMY, PREFIX_NAME));
        cmd.execute(new AddressBookModelManager(),
                new ScheduleModelManager(),
                new DiagnosisModelManager(),
                new CommandHistory());
    }

    @Test
    void parse_patient_unchanged_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(EditCommand.MESSAGE_NOT_EDITED);
        testAddressBookModel(new PersonBuilder().build(),
                ArgumentTokenizer.tokenize("", PREFIX_NAME));
    }

    @Test
    void parse_patient_invalidName_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_FORMAT, Name.class.getSimpleName()));
        // no & allowed in name
        testAddressBookModel(new PersonBuilder().build(),
                ArgumentTokenizer.tokenize(PREFIX_NAME + "James&", PREFIX_NAME));
    }

    @Test
    void parse_patient_invalidPhone_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_FORMAT, Phone.class.getSimpleName()));
        // no a allowed in phone number
        testAddressBookModel(new PersonBuilder().build(),
                ArgumentTokenizer.tokenize(PREFIX_PHONE + "911a", PREFIX_PHONE));
    }

    @Test
    void parse_patient_invalidEmail_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_FORMAT, Email.class.getSimpleName()));
        // @ missing from email
        testAddressBookModel(new PersonBuilder().build(),
                ArgumentTokenizer.tokenize(PREFIX_EMAIL + "bob!yahoo", PREFIX_EMAIL));
    }

    @Test
    void parse_patient_invalidAddress_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_FORMAT, Address.class.getSimpleName()));
        // no empty address allowed
        testAddressBookModel(new PersonBuilder().build(),
                ArgumentTokenizer.tokenize(PREFIX_ADDRESS + "", PREFIX_ADDRESS));
    }

    @Test
    void parse_patient_invalidTag_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_FORMAT, Tag.class.getSimpleName()));
        // no * allowed in tag
        testAddressBookModel(new PersonBuilder().build(),
                ArgumentTokenizer.tokenize(PREFIX_TAG + "tag*", PREFIX_TAG));
    }

    @Test
    void parse_patient_editDeleted_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(MESSAGE_CANNOT_EDIT_DELETED);
        Person person = new PersonBuilder().build();
        person.delete();
        // attempt to change person's name
        testAddressBookModel(person, ArgumentTokenizer.tokenize(PREFIX_NAME + VALID_NAME_AMY,
                PREFIX_NAME));
    }

    // --- end of address book tests ---

    // --- start of schedule tests ---

    @Test
    void parse_appointment_invalidEventId() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(EditCommand.MESSAGE_INVALID_EVENT_ID);
        EditCommand cmd = new EditCommand(CMDTYPE_APPOINTMENT, "invalidID",
                ArgumentTokenizer.tokenize(PREFIX_DETAILS + "some details", PREFIX_DETAILS));
        cmd.execute(new AddressBookModelManager(),
                new ScheduleModelManager(),
                new DiagnosisModelManager(),
                new CommandHistory());
    }

    // --- end of schedule tests ---

    private Person testAddressBookModel(Person original, ArgumentMultimap argMultimap)
            throws Exception {

        AddressBookModel addressBookModel = new AddressBookModelManager();
        ScheduleModel scheduleModel = new ScheduleModelManager();
        DiagnosisModel diagnosisModel = new DiagnosisModelManager();
        CommandHistory commandHistory =  new CommandHistory();

        addressBookModel.addPerson(original);

        EditCommand cmd = new EditCommand(CMDTYPE_PATIENT, original.getId().toString(), argMultimap);

        cmd.execute(addressBookModel, scheduleModel, diagnosisModel, commandHistory);

        // added person should be in index 0
        return addressBookModel.internalGetFromPersonList(unused -> true).get(0);
    }

    private ScheduleEvent testScheduleModel(ScheduleEvent original,
                                            ArgumentMultimap argMultimap) throws Exception {

        AddressBookModel addressBookModel = new AddressBookModelManager();
        ScheduleModel scheduleModel = new ScheduleModelManager();
        DiagnosisModel diagnosisModel = new DiagnosisModelManager();
        CommandHistory commandHistory =  new CommandHistory();

        scheduleModel.addEvent(original);

        EditCommand cmd = new EditCommand(CMDTYPE_APPOINTMENT, original.getId().toString(), argMultimap);

        cmd.execute(addressBookModel, scheduleModel, diagnosisModel, commandHistory);

        // added scheduleEvent should be in index 0
        return scheduleModel.internalGetFromEventList(unused -> true).get(0);
    }
}
