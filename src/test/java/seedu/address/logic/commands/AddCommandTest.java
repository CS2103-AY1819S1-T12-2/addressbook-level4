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
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

import static org.junit.Assert.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.AddCommand.MESSAGE_USAGE_PERSON;
import static seedu.address.logic.commands.CommandTestUtil.*;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;
import static seedu.address.logic.parser.PersonCliSyntax.*;
import static seedu.address.testutil.PersonBuilder.*;
import static seedu.address.testutil.PersonUtil.matchProperties;
import static seedu.address.testutil.TypicalPersons.ALICE;

public class AddCommandTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    void parse_patient_success() throws Exception {

        // normal input
        Person person = new PersonBuilder(ALICE).withTags(VALID_TAG_FRIEND).build();
        Person addedPerson = testAddressBookModel(PersonUtil.getPersonDetails(person));
        assertTrue(matchProperties(person, addedPerson));

        // multiple names - last name accepted
        person = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        addedPerson = testAddressBookModel(PersonUtil.getPersonDetails(person)
                + PREFIX_NAME + DEFAULT_NAME);
        assertTrue(matchProperties(person, addedPerson));

        // multiple phones - last phone accepted
        person = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        addedPerson = testAddressBookModel(PersonUtil.getPersonDetails(person)
                + PREFIX_PHONE + DEFAULT_PHONE);
        assertTrue(matchProperties(person, addedPerson));

        // multiple emails - last email accepted
        person = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        addedPerson = testAddressBookModel(PersonUtil.getPersonDetails(person)
                + PREFIX_EMAIL + DEFAULT_EMAIL);
        assertTrue(matchProperties(person, addedPerson));

        // multiple addresses - last address accepted
        person = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        addedPerson = testAddressBookModel(PersonUtil.getPersonDetails(person)
                + PREFIX_ADDRESS + DEFAULT_ADDRESS);
        assertTrue(matchProperties(person, addedPerson));

        // multiple tags - all accepted
        person = new PersonBuilder(ALICE).withTags(VALID_TAG_FRIEND)
                .withTags(VALID_TAG_HUSBAND).build();
        addedPerson = testAddressBookModel(PersonUtil.getPersonDetails(person));
        assertTrue(matchProperties(person, addedPerson));
    }

    @Test
    public void parse_patient_optionalFieldsMissing_success() throws Exception {
        Person person = new PersonBuilder(ALICE).build();
        Person addedPerson = testAddressBookModel(PersonUtil.getPersonDetails(person));
        assertTrue(matchProperties(person, addedPerson));
    }

    @Test
    void parse_patient_missingCompulsoryFields_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE_PERSON));
        testAddressBookModel(NAME_DESC_AMY + PHONE_DESC_AMY + ADDRESS_DESC_AMY);
    }

    @Test
    void parse_patient_incorrectNameFormat_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE_PERSON));
        // no & allowed in name
        Person person = new PersonBuilder(ALICE).withEmail("James&").build();
        testAddressBookModel(PersonUtil.getPersonDetails(person));
    }

    @Test
    void parse_patient_incorrectPhoneFormat_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE_PERSON));
        // no a allowed in phone
        Person person = new PersonBuilder(ALICE).withPhone("911a").build();
        testAddressBookModel(PersonUtil.getPersonDetails(person));
    }

    @Test
    void parse_patient_incorrectEmailFormat_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE_PERSON));
        // no @ allowed in email
        Person person = new PersonBuilder(ALICE).withEmail("bob!yahoo").build();
        testAddressBookModel(PersonUtil.getPersonDetails(person));
    }

    @Test
    void parse_patient_incorrectAddressFormat_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE_PERSON));
        // empty string not allowed for address
        Person person = new PersonBuilder(ALICE).withAddress("").build();
        testAddressBookModel(PersonUtil.getPersonDetails(person));
    }

    @Test
    void parse_patient_incorrectTagFormat_throwsCommandException() throws Exception {
        thrown.expect(CommandException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE_PERSON));
        // * not allowed in tag
        Person person = new PersonBuilder(ALICE).withTags("tag*").build();
        testAddressBookModel(PersonUtil.getPersonDetails(person));
    }

    private Person testAddressBookModel(String toAdd) throws Exception {

        AddressBookModel addressBookModel = new AddressBookModelManager();
        ScheduleModel scheduleModel = new ScheduleModelManager();
        DiagnosisModel diagnosisModel = new DiagnosisModelManager();
        CommandHistory commandHistory =  new CommandHistory();

        AddCommand cmd = new AddCommand(CMDTYPE_PATIENT, toAdd);

        cmd.execute(addressBookModel, scheduleModel, diagnosisModel, commandHistory);

        // added person should be in index 0
        return addressBookModel.internalGetFromPersonList(unused -> true).get(0);
    }
}
