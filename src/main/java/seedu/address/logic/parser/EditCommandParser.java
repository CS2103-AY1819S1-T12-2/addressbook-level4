package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;
import static seedu.address.logic.parser.PersonCliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.PersonCliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.PersonCliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.PersonCliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.PersonCliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.ScheduleEventCliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.ScheduleEventCliSyntax.PREFIX_DETAILS;
import static seedu.address.logic.parser.ScheduleEventCliSyntax.PREFIX_PERSON;
import static seedu.address.logic.parser.ScheduleEventCliSyntax.PREFIX_TAGS;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String cmdType;
        String target;
        try {
            String[] argSplit = args.trim().split(" ", 3);
            cmdType = argSplit[0];
            target = argSplit[1];
            args = argSplit[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        ArgumentMultimap argMultimap;

        if (cmdType.equals(CMDTYPE_PATIENT)) {
            argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE,
                    PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG);
        } else if (cmdType.equals(CMDTYPE_APPOINTMENT)) {
            argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_PERSON, PREFIX_DATETIME,
                    PREFIX_DETAILS, PREFIX_TAGS);
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        /* We remove the index based representation so there's no risk of modifying the wrong entry, which would be
        a severe problem in an application with sensitive information.
         */
        /*Index index;


        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditCommand.MESSAGE_USAGE), pe);
        }
        */

        return new EditCommand(cmdType, target, argMultimap);

        /*
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            editPersonDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            editPersonDescriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
        */
    }



}
