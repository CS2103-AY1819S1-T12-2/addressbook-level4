package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_DISEASE;
import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_PATIENT;
import static seedu.address.logic.parser.DiseaseMatcherCliSyntax.PREFIX_DISEASE;
import static seedu.address.logic.parser.DiseaseMatcherCliSyntax.PREFIX_SYMPTOM;
import static seedu.address.logic.parser.PersonCliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.PersonCliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.PersonCliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.PersonCliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.PersonCliSyntax.PREFIX_TAG;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.SwitchToAppointmentEvent;
import seedu.address.commons.events.ui.SwitchToPatientEvent;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.ArgumentTokenizer;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.Prefix;
import seedu.address.logic.parser.ScheduleEventParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBookModel;
import seedu.address.model.DiagnosisModel;
import seedu.address.model.ScheduleModel;
import seedu.address.model.event.ScheduleEvent;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.symptom.Disease;
import seedu.address.model.symptom.Symptom;
import seedu.address.model.tag.Tag;


/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Use 'add patient' , 'add appointment' or "
            + "'add disease' to add a patient, appointment or disease respectively. "
            + "\n"
            + "Parameters to add patient: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_ADDRESS + "ADDRESS "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + "patient "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney"
            + "\n"
            + "Parameters to add appointment: "
            + "for NAME "
            + "DATE/DURATION \n"
            + "Example: " + COMMAND_WORD + " "
            + "appointment "
            + "for David Lee tomorrow"
            + "\n"
            + "Parameters to add disease: "
            + PREFIX_DISEASE + "DISEASE "
            + "[" + PREFIX_SYMPTOM + "SYMPTOM]...\n"
            + "Example: " + COMMAND_WORD + " "
            + "disease "
            + PREFIX_DISEASE + "acne "
            + PREFIX_SYMPTOM + "pustules "
            + PREFIX_SYMPTOM + "blackheads "
            + "\n";

    public static final String MESSAGE_USAGE_PERSON = "Expected format for adding a patient: \n"
            + COMMAND_WORD + " patient "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_ADDRESS + "ADDRESS "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + "patient "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney"
            + "\n";

    public static final String MESSAGE_USAGE_APPOINTMENT = "Expected format for adding an appointment: \n"
            + COMMAND_WORD + " appointment "
            + "for NAME  "
            + "DATE/DURATION \n"
            + "Example: " + COMMAND_WORD + " "
            + "appointment "
            + "for David Lee tomorrow"
            + "\n"
            + "Please enter DATE/DURATION in natural expressions or in DD/MM/YYYY format.\n"
            + "Refer to User Guide for the complete list of accepted natural expressions.\n";

    public static final String MESSAGE_USAGE_DISEASE = "Expected format for adding a disease: \n "
            + COMMAND_WORD + " disease "
            + PREFIX_DISEASE + "DISEASE "
            + "[" + PREFIX_SYMPTOM + "SYMPTOM]...\n"
            + "Example: " + COMMAND_WORD + " "
            + "disease "
            + PREFIX_DISEASE + "acne "
            + PREFIX_SYMPTOM + "pustules "
            + PREFIX_SYMPTOM + "blackheads "
            + "\n";

    public static final String MESSAGE_SUCCESS_ADDRESSBOOK = "New patient added: %1$s";
    public static final String MESSAGE_SUCCESS_SCHEDULE = "New appointment added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This patient already exists in the patient book";
    public static final String MESSAGE_DUPLICATE_DISEASE = "This disease already exists in the patient book";
    private static final String MESSAGE_INVALID_PATIENT_FORMAT = "Invalid input format for patient";

    private final String addType;
    private final String args;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(String addType, String args) {
        this.addType = addType;
        this.args = args;
    }

    @Override
    public CommandResult execute(AddressBookModel addressBookModel, ScheduleModel scheduleModel,
                                 DiagnosisModel diagnosisModel, CommandHistory history) throws CommandException {
        requireNonNull(addressBookModel);
        requireNonNull(scheduleModel);
        requireNonNull(diagnosisModel);

        if (addType.equals(CMDTYPE_PATIENT)) {
            // adds a patient into the addressbook
            ArgumentMultimap argMultimap =
                    ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE,
                            PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG);

            if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL)) {
                throw new CommandException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE_PERSON));
            }

            try {
                Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
                Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
                Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
                Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
                Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
                Person person = new Person(name, phone, email, address, tagList);
                if (addressBookModel.hasPerson(person)) {
                    throw new CommandException(MESSAGE_DUPLICATE_PERSON);
                }
                addressBookModel.addPerson(person);
                EventsCenter.getInstance().post(new SwitchToPatientEvent());
                return new CommandResult(String.format(MESSAGE_SUCCESS_ADDRESSBOOK, person.getName()));
            } catch (ParseException e) {
                throw new CommandException(MESSAGE_INVALID_PATIENT_FORMAT, e);
            }
        } else if (addType.equals(CMDTYPE_APPOINTMENT)) {
            // adds an event into the schedule
            try {
                ScheduleEvent newEvent = new ScheduleEventParser(addressBookModel, scheduleModel).parse(args);
                System.out.println(newEvent);
                scheduleModel.addEvent(newEvent);
                EventsCenter.getInstance().post(new SwitchToAppointmentEvent());
                return new CommandResult(String.format(MESSAGE_SUCCESS_SCHEDULE, newEvent));
            } catch (ParseException e) {
                throw new CommandException(e.getMessage());
            }
        } else if (addType.equals(CMDTYPE_DISEASE)) {
            // adds a disease into the addressbook
            ArgumentMultimap argMultimap =
                    ArgumentTokenizer.tokenize(args, PREFIX_DISEASE, PREFIX_SYMPTOM);

            if (!arePrefixesPresent(argMultimap, PREFIX_DISEASE, PREFIX_SYMPTOM)) {
                throw new CommandException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE_DISEASE));
            }

            try {
                Optional<String> diseaseValue = argMultimap.getValue(PREFIX_DISEASE);
                if (!diseaseValue.isPresent()) {
                    throw new CommandException("No disease value when parsing (parsing performed during execution).");
                }
                Disease disease = ParserUtil.parseDisease(diseaseValue.get());
                Set<Symptom> symptomSet = ParserUtil.parseSymptoms(argMultimap.getAllValues(PREFIX_SYMPTOM));
                if (diagnosisModel.hasDisease(disease)) {
                    throw new CommandException(MESSAGE_DUPLICATE_DISEASE);
                }
                diagnosisModel.addMatcher(disease, symptomSet);
                String cmdResult = "New disease "
                        + disease.toString()
                        + " has been added into our record.";
                return new CommandResult(cmdResult);
            } catch (ParseException e) {
                throw new CommandException("Unexpected Error: unacceptable values should have been prompted for.", e);
            }
        } else {
            throw new CommandException("Unexpected Values: should have been caught in AddCommandParser.");
        }


    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        Stream.of(prefixes).forEach(prefix -> System.out.println(prefix));
        Stream.of(prefixes).forEach(prefix -> System.out.println(argumentMultimap.getValue(prefix)));
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddCommand // instanceof handles nulls
                && addType.equals(((AddCommand) other).addType)
                && args.equals(((AddCommand) other).args));
    }
}
