package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_DISEASE;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {

        String[] argSplit = args.trim().split(" ", 2);

        String cmdType;
        try {
            cmdType = argSplit[0];
            args = argSplit[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddCommand.MESSAGE_USAGE));
        }
        // we change the parsing to take place in the command execution phase
        // instead so we can prompt the user for missing fields directly rather than
        // reject the command altogether.
        if (!cmdType.equals(CMDTYPE_PATIENT) && !cmdType.equals(CMDTYPE_APPOINTMENT)
                && !cmdType.equals(CMDTYPE_DISEASE)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddCommand.MESSAGE_USAGE));
        }
        /*if (cmdType.equals("patient")) {
            ArgumentMultimap argMultimap =
                    ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                        PREFIX_ADDRESS, PREFIX_TAG);

            if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL)
                    || !argMultimap.getPreamble().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
            }

            Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
            Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
            Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
            Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
            Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

            person = new Person(name, phone, email, address, tagList);
        } else if (cmdType.equals("appointment")) {
            // we can't parse appointments all at once, so we simply pass in the entire string,
            // so that the refinement of input may be done during the execute phase.
            scheduleEvent = new ScheduleEvent(args);
        } */

        return new AddCommand(cmdType, args);
    }

}
