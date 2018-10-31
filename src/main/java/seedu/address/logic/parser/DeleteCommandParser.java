package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns an DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        requireNonNull(args);

        // we expect the following:
        // argSplit[0]: cmdType
        // argSplit[1]: target (parseException if not valid format)
        // argSplit[2]: empty (parseException otherwise)
        String cmdType;
        String target;
        try {
            String[] argSplit = args.trim().split(" ", 3);
            cmdType = argSplit[0];
            target = argSplit[1];
            if (argSplit.length > 2) {
                args = argSplit[2];
            } else {
                args = "";
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        if (!cmdType.equals(CMDTYPE_PATIENT) && !cmdType.equals(CMDTYPE_APPOINTMENT) || !args.trim().equals("")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        return new DeleteCommand(cmdType, target);
        /*
        // older index-based implementation
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE), pe);
        }
        */
    }

}
