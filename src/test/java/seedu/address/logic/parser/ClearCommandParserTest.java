package seedu.address.logic.parser;

import org.junit.Test;
import seedu.address.logic.commands.ClearCommand;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

public class ClearCommandParserTest {

    private ClearCommandParser parser = new ClearCommandParser();

    @Test
    public void parse_validArgs_returnsClearCommand() {
        assertParseSuccess(parser, CMDTYPE_APPOINTMENT, new ClearCommand(CMDTYPE_APPOINTMENT));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, CMDTYPE_PATIENT,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_extraArgs_throwsParseException() {
        assertParseFailure(parser, CMDTYPE_APPOINTMENT + " extra arguments",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noArgs_throwsParseException() {
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
    }
}
