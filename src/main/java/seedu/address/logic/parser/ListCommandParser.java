package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_DISEASE;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListCommand object
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns an FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public ListCommand parse(String args) throws ParseException {

        String cmdType;
        try {
            String[] argSplit = args.trim().split(" ", 2);
            cmdType = argSplit[0];
            if (argSplit.length > 1) {
                args = argSplit[1];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        if (!cmdType.equals(CMDTYPE_PATIENT) && !cmdType.equals(CMDTYPE_DISEASE)
                && !cmdType.equals(CMDTYPE_APPOINTMENT)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        return new ListCommand(cmdType, args);
    }
}
