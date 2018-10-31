package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;

import seedu.address.logic.commands.ModeCommand;
import seedu.address.logic.parser.exceptions.ParseException;


/**
 * Parses input arguments and creates a new ModeCommand object
 */
public class ModeCommandParser implements Parser<ModeCommand> {

    /**
     * Parses the given {@code String} argument in the context of the ModeCommand
     * and returns a ModeCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public ModeCommand parse(String args) throws ParseException {

        args = args.trim();
        if (!args.equals(CMDTYPE_PATIENT) && !args.equals(CMDTYPE_APPOINTMENT)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ModeCommand.MESSAGE_USAGE));
        }

        return new ModeCommand(args);
    }
}
