package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_DISEASE;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;

import java.util.Arrays;
import java.util.List;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.commons.events.ui.SwitchToPatientEvent;
import seedu.address.commons.events.ui.SwitchToAppointmentEvent;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBookModel;
import seedu.address.model.DiagnosisModel;
import seedu.address.model.ScheduleModel;
import seedu.address.model.event.ScheduleEventMatchesPredicate;
import seedu.address.model.person.MatchPersonPredicate;
import seedu.address.model.symptom.Disease;
import seedu.address.model.symptom.Symptom;

/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_UNEXPECTED_PARAMETER = "Unexpected Values: "
            + "Should have been caught in FindCommandParser.";
    public static final String MESSAGE_USAGE = COMMAND_WORD + "use 'find patient' or 'find disease' "
            + "to find all persons whose names contain any of "
            + "the specified keywords (case-insensitive) or to find all symptoms related to a disease if it exists "
            + "and displays them as a list with index numbers.\n"
            + "Parameters to find persons: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: "
            + COMMAND_WORD
            + " patient"
            + " alice bob charlie\n"
            + "Parameter to find disease: DISEASE\n"
            + "Example: "
            + COMMAND_WORD
            + " disease"
            + " influenza\n";

    private final String cmdType;
    private final String searchString;

    /* public FindCommand(MatchPersonPredicate predicate) {
        this.predicate = predicate;
    } */
    public FindCommand(String cmdType, String searchString) {
        this.cmdType = cmdType;
        this.searchString = searchString;
    }

    @Override
    public CommandResult execute(AddressBookModel addressBookModel, ScheduleModel scheduleModel,
                                 DiagnosisModel diagnosisModel, CommandHistory history) throws CommandException {
        requireNonNull(addressBookModel);
        requireNonNull(scheduleModel);
        requireNonNull(diagnosisModel);
        String cmdResult;

        if (this.cmdType.equals(CMDTYPE_PATIENT)) {
            String[] nameKeywords = searchString.split("\\s+");

            addressBookModel.updateFilteredPersonList(new MatchPersonPredicate(Arrays.asList(nameKeywords)));
            EventsCenter.getInstance().post(new SwitchToPatientEvent());
            cmdResult = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW,
                    addressBookModel.getFilteredPersonList().size());
        } else if (this.cmdType.equals(CMDTYPE_APPOINTMENT)) {
            scheduleModel.updateFilteredEventList(new ScheduleEventMatchesPredicate(searchString));
            EventsCenter.getInstance().post(new SwitchToAppointmentEvent());
            cmdResult = String.format(Messages.MESSAGE_EVENTS_LISTED_OVERVIEW,
                    scheduleModel.getFilteredEventList().size());
        } else if (this.cmdType.equals(CMDTYPE_DISEASE)) {
            Disease disease = new Disease(searchString.trim().toLowerCase());
            if (!diagnosisModel.hasDisease(disease)) {
                throw new CommandException("Unexpected Error: "
                        + disease.toString()
                        + " is not present in our record,"
                        + " please add this disease and its related symptoms into the record");
            }
            List<Symptom> symptomList = diagnosisModel.getSymptoms(disease);
            cmdResult = disease.toString() + " is present in our record. Found the following symptoms matching "
                    + disease.toString() + ":\n"
                    + "\n"
                    + CommandResult.convertListToString(symptomList);
        } else {
            throw new CommandException(MESSAGE_UNEXPECTED_PARAMETER);
        }

        return new CommandResult(cmdResult);

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FindCommand // instanceof handles nulls
                && cmdType.equals(((FindCommand) other).cmdType)
                && searchString.equals(((FindCommand) other).searchString)); // state check
    }
}