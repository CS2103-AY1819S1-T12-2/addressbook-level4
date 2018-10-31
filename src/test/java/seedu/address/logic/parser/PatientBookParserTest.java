package seedu.address.logic.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.ListCommand.GET_ALL_WORD;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;
import static seedu.address.logic.parser.PersonCliSyntax.*;
import static seedu.address.logic.parser.PersonCliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.ScheduleEventCliSyntax.PREFIX_PERSON;
import static seedu.address.logic.parser.ScheduleEventCliSyntax.PREFIX_DETAILS;
import static seedu.address.logic.parser.ScheduleEventCliSyntax.PREFIX_TAGS;
import static seedu.address.logic.parser.ScheduleEventCliSyntax.PREFIX_DATETIME;
import static seedu.address.testutil.PersonBuilder.DEFAULT_NAME;
import static seedu.address.testutil.ScheduleEventBuilder.SAMPLE_EVENTID;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.logic.commands.AddCommand;

import seedu.address.logic.commands.*;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;
import seedu.address.testutil.ScheduleEventBuilder;

public class PatientBookParserTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final PatientBookParser parser = new PatientBookParser();


    @Test
    public void parseCommand_addPatient() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(CMDTYPE_PATIENT, PersonUtil.getPersonDetails(person)), command);
    }

    @Test
    public void parseCommand_addAppointment() throws Exception {
        String argString = "for " + ScheduleEventBuilder.DEFAULT_PERSON + " "
                + ScheduleEventBuilder.DEFAULT_DATETIME_STRING;
        String cmdString = AddCommand.COMMAND_WORD + " " + CMDTYPE_APPOINTMENT + " " + argString;
        AddCommand command = (AddCommand) parser.parseCommand(cmdString);
        assertEquals(new AddCommand(CMDTYPE_APPOINTMENT, argString), command);
    }


    @Test
    public void parseCommand_clearAppointment() throws Exception {
        String cmdString = ClearCommand.COMMAND_WORD + " " + CMDTYPE_APPOINTMENT;
        ClearCommand command = (ClearCommand) parser.parseCommand(cmdString);
        assertEquals(new ClearCommand(CMDTYPE_APPOINTMENT), command);
    }

    @Test
    public void parseCommand_deletePatient() throws Exception {
        PersonId personId = new PersonBuilder().build().getId();
        DeleteCommand command = (DeleteCommand) parser.parseCommand(DeleteCommand.COMMAND_WORD + " "
                + CMDTYPE_PATIENT + " " + personId.toString());
        assertEquals(new DeleteCommand(CMDTYPE_PATIENT, personId.toString()), command);
    }

    @Test
    public void parseCommand_deleteAppointment() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(DeleteCommand.COMMAND_WORD + " "
                + CMDTYPE_APPOINTMENT + " " + SAMPLE_EVENTID);
        assertEquals(new DeleteCommand(CMDTYPE_PATIENT, SAMPLE_EVENTID), command);
    }

    @Test
    public void parseCommand_findPatient() throws Exception {
        FindCommand command = (FindCommand) parser.parseCommand(FindCommand.COMMAND_WORD + " "
                + CMDTYPE_PATIENT + " " + DEFAULT_NAME);
        assertEquals(new FindCommand(CMDTYPE_PATIENT, DEFAULT_NAME), command);
    }

    @Test
    public void parseCommand_findAppointment() throws Exception {
        FindCommand command = (FindCommand) parser.parseCommand(FindCommand.COMMAND_WORD + " "
                + CMDTYPE_APPOINTMENT + " " + SAMPLE_EVENTID);
        assertEquals(new FindCommand(CMDTYPE_APPOINTMENT, SAMPLE_EVENTID), command);
    }

    @Test
    public void parseCommand_editPatient() throws Exception {
        Person person = new PersonBuilder().build();
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize("", PREFIX_NAME, PREFIX_PHONE,
                PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG);

        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + CMDTYPE_PATIENT + " " + person.getId().toString() + " ");
        assertEquals(new EditCommand(CMDTYPE_PATIENT,
                person.getId().toString(), argMultimap), command);
    }

    @Test
    public void parseCommand_editAppointment() throws Exception {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize("", PREFIX_PERSON, PREFIX_DATETIME,
                PREFIX_DETAILS, PREFIX_TAGS);

        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + CMDTYPE_APPOINTMENT + " " + SAMPLE_EVENTID + " ");
        assertEquals(new EditCommand(CMDTYPE_APPOINTMENT, SAMPLE_EVENTID, argMultimap), command);
    }

    @Test
    public void parseCommand_mode() throws Exception {
        ModeCommand command = (ModeCommand) parser.parseCommand(ModeCommand.COMMAND_WORD + " "
                + CMDTYPE_PATIENT);
        assertEquals(new ModeCommand(CMDTYPE_PATIENT), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    /*
    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " "
                        + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new MatchPersonPredicate(keywords)), command);
    }
    */

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_history() throws Exception {
        assertTrue(parser.parseCommand(HistoryCommand.COMMAND_WORD) instanceof HistoryCommand);
        assertTrue(parser.parseCommand(HistoryCommand.COMMAND_WORD + " 3") instanceof HistoryCommand);

        try {
            parser.parseCommand("histories");
            throw new AssertionError("The expected ParseException was not thrown.");
        } catch (ParseException pe) {
            assertEquals(MESSAGE_UNKNOWN_COMMAND, pe.getMessage());
        }
    }

    @Test
    public void parseCommand_list() throws Exception {
        ListCommand command = (ListCommand) parser.parseCommand(ListCommand.COMMAND_WORD + " "
                + CMDTYPE_PATIENT);
        assertEquals(new ListCommand(CMDTYPE_PATIENT, GET_ALL_WORD), command);
    }

    @Test
    public void parseCommand_clearPatient_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
        parser.parseCommand(ClearCommand.COMMAND_WORD + " " + CMDTYPE_PATIENT);
    }

    @Test
    public void parseCommand_clearAppointmentWithExtraArguments_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
        parser.parseCommand(ClearCommand.COMMAND_WORD + " " + CMDTYPE_APPOINTMENT + " extraArgs");
    }

    @Test
    public void parseCommand_deletePatient_invalidInput_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        PersonId personId = new PersonBuilder().build().getId();
        parser.parseCommand(DeleteCommand.COMMAND_WORD + " "
                + CMDTYPE_PATIENT + " " + personId.toString() + " invalid string");
    }

    @Test
    public void parseCommand_deleteAppointment_invalidInput_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        parser.parseCommand(DeleteCommand.COMMAND_WORD + " "
                + CMDTYPE_APPOINTMENT + " " + SAMPLE_EVENTID + " invalid string");
    }

    @Test
    public void parseCommand_find_invalidInput_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        parser.parseCommand(FindCommand.COMMAND_WORD + " " + "invalid");
    }

    @Test
    public void parseCommand_edit_invalidInput_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        parser.parseCommand(EditCommand.COMMAND_WORD + " " + "invalid");
    }

    @Test
    public void parseCommand_list_invalidInput_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        parser.parseCommand(ListCommand.COMMAND_WORD + " " + "invalid");
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        parser.parseCommand("");
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        thrown.expectMessage(MESSAGE_UNKNOWN_COMMAND);
        parser.parseCommand("unknownCommand");
    }
}
