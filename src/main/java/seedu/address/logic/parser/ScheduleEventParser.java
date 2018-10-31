package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.commons.core.Messages.MESSAGE_PERSONS_MATCH_BY_NAME_FAIL;
import static seedu.address.logic.parser.Prompt.MESSAGE_PROMPT_ID;
import static seedu.address.logic.parser.Prompt.MESSAGE_PROMPT_NOTES;
import static seedu.address.logic.parser.Prompt.MESSAGE_PROMPT_TAGS;
import static seedu.address.logic.parser.Prompt.MESSAGE_PROMPT_TIMESLOT;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.util.Pair;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.exceptions.PromptException;
import seedu.address.model.AddressBookModel;
import seedu.address.model.ScheduleModel;
import seedu.address.model.event.ScheduleEvent;
import seedu.address.model.person.MatchPersonPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.tag.Tag;


/**
 * Parses input arguments and creates a new ScheduleEvent object
 */
public class ScheduleEventParser {


    private ScheduleModel scheduleModel;
    private AddressBookModel addressBookModel;


    public ScheduleEventParser(AddressBookModel addressBookModel, ScheduleModel scheduleModel) {
        this.scheduleModel = scheduleModel;
        this.addressBookModel = addressBookModel;
    }

    /**
     * Generates the intended ScheduleEvent from given user input.
     * @param input User input
     * @return The ScheduleEvent intended
     */
    public ScheduleEvent parse(String input) throws ParseException {
        String[] splitInput = input.split("\\s+");
        int dateInputStartAt = breakdownInput(splitInput);
        String[] nameInput = generateNameInput(splitInput, dateInputStartAt);
        String dateInput = generateDateInput(splitInput, dateInputStartAt);
        PersonId patientId = parseNameInput(nameInput);
        Pair<Calendar> timeSlot = parseDateInput(dateInput);
        Set<Tag> tags = promptForTags();
        String notes = promptForNotes();
        return new ScheduleEvent(timeSlot, patientId, notes, tags);
    }

    /**
     *
     * @param input
     * @return
     * @throws ParseException
     */
    private int breakdownInput(String[] input) throws ParseException {
        if (input[0].equals("for")) {
            for (int i = 2; i < input.length; i++) { // loop starts at 2 as name is at least one string long
                if (isSomeDateInput(input, i)) {
                    return i;
                }
            }
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE_APPOINTMENT));
    }

    /**
     *
     * @param input
     * @param dateInputStartAt
     * @return
     */
    private String generateDateInput(String[] input, int dateInputStartAt) {
        StringBuilder recoveredDateInputBuilder = new StringBuilder();
        for (int j = dateInputStartAt; j < input.length; j++) {
            recoveredDateInputBuilder.append(input[j]);
            recoveredDateInputBuilder.append(" ");
        }
        return recoveredDateInputBuilder.toString();
    }

    /**
     *
     * @param input
     * @param dateInputStartAt
     * @return
     */
    private String[] generateNameInput(String[] input, int dateInputStartAt) {
        return Arrays.copyOfRange(input, 1, dateInputStartAt);
    }

    /**
     *
     * @param dateInput
     * @return
     * @throws ParseException
     */
    private Pair<Calendar> parseDateInput(String dateInput) throws ParseException {
        try {
            Calendar currentTime = Calendar.getInstance();
            DateTimeParser dateTimeParser = new DateTimeParser();
            Pair<Calendar> dateInterval = dateTimeParser.parseDate(dateInput, currentTime);
            List<ScheduleEvent> scheduledAppts = getAppointmentsBetween(dateInterval);
            String availableTimeSlots = dateTimeParser.getAvailableTimeSlotsBetween(scheduledAppts, dateInterval);
            String timeSlotInput = new Prompt().promptForMoreInput(MESSAGE_PROMPT_TIMESLOT, availableTimeSlots, true);
            Pair<Calendar> timeSlot = dateTimeParser.parseTimeSlot(timeSlotInput.trim());
            if (isTimeSlotWithinRange(timeSlot, dateInterval)) {
                for (ScheduleEvent appt: scheduledAppts) {
                    if (appt.isClashing(timeSlot)) {
                        throw new ParseException(String.format(DateTimeParser.MESSAGE_INVALID_SLOT,
                                DateTimeParser.MESSAGE_SLOT_CLASHING));
                    }
                }
                return timeSlot; // where do we check clashing time slots??
            } else {
                throw new ParseException(String.format(DateTimeParser.MESSAGE_INVALID_SLOT,
                        DateTimeParser.MESSAGE_SLOT_NOT_WITHIN_RANGE));
            }
        } catch (ParseException | PromptException e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     *
     * @param nameInput
     * @return
     */
    private PersonId parseNameInput(String[] nameInput) throws ParseException {
        try {
            List<Person> matchedPersons = addressBookModel.internalGetFromPersonList
                    (new MatchPersonPredicate(Arrays.asList(nameInput)));
            if (matchedPersons.isEmpty()) {
                throw new ParseException(MESSAGE_PERSONS_MATCH_BY_NAME_FAIL);
            }
            Set<PersonId> choosablePersonIds = getChoosablePersonIds(matchedPersons);
            while (matchedPersons.size() != 1) {
                String displayablePersons = displayPersonListAsString(matchedPersons);
                String personIdInput = new Prompt().promptForMoreInput(MESSAGE_PROMPT_ID, displayablePersons, true);
                String[] splitString = personIdInput.split("\\s+");
                matchedPersons = addressBookModel.internalGetFromPersonList
                        (new MatchPersonPredicate(Arrays.asList(splitString))); // now match by ID
                if (matchedPersons.isEmpty()) {
                    throw new ParseException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
                }
                for (Person person: matchedPersons) {
                    if (!choosablePersonIds.contains(person.getId())) {
                        throw new ParseException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
                    }
                }
            }
            return matchedPersons.get(0).getId();
        } catch (PromptException e) {
            throw new ParseException(e.getMessage());
        }
    }

    private Set<PersonId> getChoosablePersonIds(List<Person> matchedPersons) {
        Set<PersonId> choosableIds = new HashSet<>();
        for (Person person: matchedPersons) {
            choosableIds.add(person.getId());
        }
        return choosableIds;
    }

    /**
     *
     * @return
     * @throws ParseException
     */
    private Set<Tag> promptForTags() throws ParseException {
        try {
            String tags = new Prompt().promptForMoreInput(MESSAGE_PROMPT_TAGS, "", false);
            if (tags.equals("")) {
                return new HashSet<>();
            } else {
                String[] splitString = tags.split("\\s+");
                return ParserUtil.parseTags(Arrays.asList(splitString));
            }
        } catch (PromptException | ParseException e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     *
     * @return
     * @throws ParseException
     */
    private String promptForNotes() throws ParseException {
        try {
            return new Prompt().promptForMoreInput(MESSAGE_PROMPT_NOTES, "", false);
        } catch (PromptException e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     *
     * @param nameMatchedPersons
     * @return
     */
    private String displayPersonListAsString(List<Person> nameMatchedPersons) {
        StringBuilder personsBuilder = new StringBuilder();
        for (Person person: nameMatchedPersons) {
            personsBuilder.append(person.getId());
            personsBuilder.append(" ");
            personsBuilder.append(person.getName());
            personsBuilder.append("\n");
        }
        return personsBuilder.toString().trim();
    }



    /**
     *
     * @param splitString
     * @param i
     * @return
     */
    private boolean isSomeDateInput(String[] splitString, int i) {
        int noOfWords = splitString.length;
        if (i == noOfWords - 1) { // we are checking starting from the last word
            switch(splitString[i]) {
            case "tomorrow":
            case "tmr":
            case "soon":
            case "recently":
                return true;
            default:
                // do nothing
            }
            if (splitString[i].split("/").length == 3) { // assuming it's DD/MM/YYYY
                return true;
            }
            return false;
        }
        if (i == noOfWords - 2) { // we are checking starting from the second last word
            if (splitString[i].equals("this") || splitString[i].equals("next")) {
                switch (splitString[i + 1]) { // check the last word now
                case "week":
                case "month":
                case "Monday":
                case "Tuesday":
                case "Wednesday":
                case "Thursday":
                case "Friday":
                case "Saturday":
                case "Sunday":
                    return true;
                default:
                    // do nothing
                }
            }
            return false;
        }
        if (i == noOfWords - 3) { // we are checking starting from the third last word
            if (splitString[i].equals("in")) {
                switch (splitString[i + 2]) {
                case "day":
                case "days":
                case "week":
                case "weeks":
                case "month":
                case "months":
                    return true;
                default:
                    // do nothing
                }
            }
            return false;
        }
        if (i == noOfWords - 4) { // we are checking starting from the fourth last word
            String[] theDayAfterTmr = {"the", "day", "after", "tmr"};
            String[] theDayAfterTomorrow = {"the", "day", "after", "tomorrow"};
            String[] inAFewDays = {"in", "a", "few", "days"};
            if (Arrays.equals(splitString, i, noOfWords, theDayAfterTmr, 0, 4)) {
                return true;
            }
            if (Arrays.equals(splitString, i, noOfWords, theDayAfterTomorrow, 0, 4)) {
                return true;
            }
            return Arrays.equals(splitString, i, noOfWords, inAFewDays, 0, 4);
        }
        return false;
    }

    private List<ScheduleEvent> getAppointmentsBetween(Pair<Calendar> dateInterval) {
        return scheduleModel.internalGetFromEventList(scheduleEvent ->
                !scheduleEvent.getDate().getKey().before(dateInterval.getKey())
                        && !scheduleEvent.getDate().getValue().after(dateInterval.getValue()));
    }

    /**
     *
     * @param timeSlot
     * @param interval
     * @return
     */

    private boolean isTimeSlotWithinRange(Pair<Calendar> timeSlot, Pair<Calendar> interval) {
        Calendar dayStart = (Calendar) interval.getKey().clone();
        Calendar dayEnd = (Calendar) dayStart.clone();
        dayEnd.set(Calendar.HOUR_OF_DAY, 18);
        do {
            if (!timeSlot.getKey().before(dayStart) && !timeSlot.getValue().after(dayEnd)) {
                return true;
            } else {
                dayStart.add(Calendar.DATE, 1);
                dayEnd.add(Calendar.DATE, 1);
            }
        } while (!dayEnd.after(interval.getValue()));
        return false;
    }

}
