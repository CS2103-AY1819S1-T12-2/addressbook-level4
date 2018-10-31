package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.Prompt.MESSAGE_PROMPT_TIMESLOT_FORMAT;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import seedu.address.commons.util.Pair;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.event.ScheduleEvent;



/**
 * Parses date time input arguments and creates the corresponding Calendar objects
 * to represent the start time and end time of an appointment.
 * Accepts date time input in natural expression forms and in fixed format.
 */
public class DateTimeParser {

    public static final String MESSAGE_INVALID_SLOT = "Invalid time slot! \n%1$s";
    public static final String MESSAGE_END_BEFORE_START = "The end time of an appointment "
                                                        + "must be after the start time! \n";
    public static final String MESSAGE_SLOT_NOT_WITHIN_RANGE = "Time slot entered is not in the range specified! \n";
    public static final String MESSAGE_SLOT_CLASHING = "Time slot entered clashes with existing appointments! \n";
    private static final String MESSAGE_NO_SLOTS = "No time slots available!\n";
    private static final String MESSAGE_HAVE_SLOTS = "You have time slots available during:\n";


    /**
     * Parses date from input string.
     * @param input Input string for date(s).
     * @return The date interval meant by the string input, represented as a Pair of Calendar objects.
     * @throws ParseException if the user input is not an accepted natural expression or not in the expected format.
     */
    public Pair<Calendar> parseDate(String input, Calendar currentTime) throws ParseException {
        zeroOutExtraPrecision(currentTime);
        return getResultantDate(currentTime, input.trim());
    }


    /**
     * Finds the date range intended from a given date/duration input and the current time.
     * @param currentTime Current time.
     * @param dateInput Input string, possibly phrased in natural expressions.
     * @return Date range intended by the input string.
     * @throws ParseException if the user input is not an accepted natural expression or not in the expected format.
     */
    private Pair<Calendar> getResultantDate(Calendar currentTime, String dateInput) throws ParseException {

        if (dateInput.startsWith("in")) {
            return parseIn(currentTime, dateInput);
        } else if (dateInput.startsWith("this") || dateInput.startsWith("next")) {
            return parseThisOrNext(currentTime, dateInput);
        }
        switch (dateInput) {
        case "tomorrow":
        case "tmr":
            return getSingleDate(currentTime, 1);
        case "the day after tomorrow":
        case "the day after tmr":
            return getSingleDate(currentTime, 2);
        case "recently":
        case "soon":
            return getNearFutureDates(currentTime);
        default:
            if (isValidDateFormat(dateInput)) {
                return getDateFromSpecified(dateInput); // user actually inputs the date (eg. 13/12/2018)
            } else {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        AddCommand.MESSAGE_USAGE_APPOINTMENT));
            }
        }
    }

    /**
     * Parses "in * day(s)/week(s)/month(s)" commands.
     * @param currentTime Current time.
     * @param dateInput Input string.
     * @return Date range intended by the input string.
     */
    private Pair<Calendar> parseIn(Calendar currentTime, String dateInput) throws ParseException {
        String[] splitString = dateInput.split("\\s+");
        assert splitString[0].equals("in");
        if (Character.isDigit(splitString[1].charAt(0))) {
            int offset = Integer.parseInt(splitString[1]);
            switch (splitString[2]) {
            case "days":
            case "day":
                return getSingleDate(currentTime, offset);
            case "weeks":
            case "week":
                return getWeekDates(currentTime, offset);
            case "months":
            case "month":
                return getMonthDates(currentTime, offset);
            default:
            }
        } else if (dateInput.equals("in a few days")) {
            return getNearFutureDates(currentTime);
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE_APPOINTMENT));
    }

    /**
     * Parser "this ..." or "next ..." commands.
     * eg. "this week" "next month" "next Thursday".
     * @param currentTime Current time.
     * @param dateInput Input string.
     * @return Date range intended by the input string.
     */
    private Pair<Calendar> parseThisOrNext(Calendar currentTime, String dateInput) throws ParseException {
        String[] splitString = dateInput.split("\\s+");
        assert splitString[0].equals("next") || splitString[0].equals("in");
        int offset; // time offset from "now"
        if (splitString[0].equals("next")) {
            offset = 1;
        } else {
            offset = 0;
        }
        switch (splitString[1]) {
        case "week":
            return getWeekDates(currentTime, offset);
        case "month":
            return getMonthDates(currentTime, offset);
        default:
            // do nothing
        }
        int dayOfWeek = -1; // requires a check somewhere
        switch (splitString[1]) {
        case "Monday":
            dayOfWeek = 0;
            break;
        case "Tuesday":
            dayOfWeek = 1;
            break;
        case "Wednesday":
            dayOfWeek = 2;
            break;
        case "Thursday":
            dayOfWeek = 3;
            break;
        case "Friday":
            dayOfWeek = 4;
            break;
        case "Saturday":
            dayOfWeek = 5;
            break;
        case "Sunday":
            dayOfWeek = 6;
            break;
        default:
            // do nothing
        }
        if (dayOfWeek == -1) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddCommand.MESSAGE_USAGE_APPOINTMENT));
        }
        return getWeekDayDate(currentTime, dayOfWeek, offset);
    }

    /**
     * Gets the date from the current time and offset from the current date.
     * @param currentDate Current time.
     * @param offset Offset from the current date.
     * @return The date intended with working hours applied.
     */
    private Pair<Calendar> getSingleDate(Calendar currentDate, int offset) {
        Calendar dateStart = (Calendar) currentDate.clone();
        Calendar dateEnd = (Calendar) currentDate.clone();
        dateStart.add(Calendar.DATE, offset);
        dateEnd.add(Calendar.DATE, offset);
        setDateStartAndEnd(dateStart, dateEnd);
        return new Pair<>(dateStart, dateEnd);
    }

    /**
     * Gets the date from a given day of the week and offset from the current week.
     * @param currentTime Current time.
     * @param dayOfWeek Day of the week, where 0 represents Monday and 6 represents Sunday.
     * @param offset Offset from the current week, where 0 represents the current weeek, 1 represents the next week etc.
     * @return The date intended with working hours applied.
     */
    private Pair<Calendar> getWeekDayDate(Calendar currentTime, int dayOfWeek, int offset) {
        Calendar date = (Calendar) currentTime.clone();
        date.setFirstDayOfWeek(Calendar.MONDAY);
        date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        date.add(Calendar.WEEK_OF_YEAR, offset); // may need to consider the wrapping around at the year boundary
        date.add(Calendar.DATE, dayOfWeek);
        Calendar dateStart = (Calendar) date.clone();
        Calendar dateEnd = (Calendar) date.clone();
        setDateStartAndEnd(dateStart, dateEnd);
        return new Pair<>(dateStart, dateEnd);
    }

    /**
     * Gets the dates for an entire week from the current time and an offset from the current week.
     * @param currentDate Current time.
     * @param offset Offset from the current week.
     * @return The dates intended with working hours applied.
     */
    private Pair<Calendar> getWeekDates(Calendar currentDate, int offset) {
        Calendar weekOffset = (Calendar) currentDate.clone();
        weekOffset.setFirstDayOfWeek(Calendar.MONDAY);
        weekOffset.add(Calendar.WEEK_OF_YEAR, offset);
        Calendar dateStart = (Calendar) weekOffset.clone();
        Calendar dateEnd = (Calendar) weekOffset.clone();
        dateStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        dateEnd.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        setDateStartAndEnd(dateStart, dateEnd);
        return new Pair<>(dateStart, dateEnd);
    }

    /**
     * Gets the dates for an entire month from the current time and an offset from the current month.
     * @param currentDate Current time.
     * @param offset Offset from the current month.
     * @return The dates intended with working hours applied.
     */
    private Pair<Calendar> getMonthDates(Calendar currentDate, int offset) {
        Calendar dateStart = (Calendar) currentDate.clone();
        dateStart.add(Calendar.MONTH, offset);
        dateStart.set(Calendar.DAY_OF_MONTH, 1); // set to 1st day of the month
        Calendar dateEnd = (Calendar) dateStart.clone();
        dateEnd.add(Calendar.MONTH, 1); // add one extra month to the end date
        dateEnd.add(Calendar.DAY_OF_MONTH, -1); // subtract one day to get the last day of the previous month
        setDateStartAndEnd(dateStart, dateEnd);
        return new Pair<>(dateStart, dateEnd);
    }

    /**
     * Gets the dates for the next seven days from the current time.
     * @param currentDate Current time.
     * @return The dates intended with working hours applied.
     */
    private Pair<Calendar> getNearFutureDates(Calendar currentDate) {
        Calendar startDate = (Calendar) currentDate.clone();
        Calendar endDate = (Calendar) currentDate.clone();
        startDate.add(Calendar.DATE, 1);
        endDate.add(Calendar.DATE, 7);
        setDateStartAndEnd(startDate, endDate);
        return new Pair<>(startDate, endDate);
    }

    /**
     * Gets the date from a specified date input in a fixed DD/MM/YYYY format
     * @param dateTimeInput The specified date input in DD/MM/YYYY format
     * @return The date intended with working hours applied.
     */
    private Pair<Calendar> getDateFromSpecified(String dateTimeInput) throws ParseException {
        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); // use the static String in some other class
            Date date = formatter.parse(dateTimeInput);
            Calendar finalDate = new GregorianCalendar();
            finalDate.setTime(date);
            Calendar dateStart = (Calendar) finalDate.clone();
            Calendar dateEnd = (Calendar) finalDate.clone();
            setDateStartAndEnd(dateStart, dateEnd); // apply working hours by default
            return new Pair<>(dateStart, dateEnd);
        } catch (java.text.ParseException e) {
            throw new ParseException("Unexpected error, as date validity should have been checked");
        }
    }

    /**
     * Checks if the input string has valid numbers for each field in DD/MM/YYYY format, where
     * year must be a positive integer, month must range from 1 to 12, and day must be a positive integer not greater
     * than the maximum number of days in that month.
     * @param dateInput Input string.
     */
    private boolean isValidDateFormat(String dateInput) {
        String[] splitString = dateInput.split("/");
        if (splitString.length != 3) {
            return false;
        }
        try {
            int day = Integer.parseInt(splitString[0]);
            int month = Integer.parseInt(splitString[1]);
            int year = Integer.parseInt(splitString[2]);
            if (year < 0) {
                return false;
            }
            if (month <= 0 || month > 12) {
                return false;
            }
            if (day <= 0) {
                return false;
            }
            Calendar testCalendar = Calendar.getInstance();
            testCalendar.set(Calendar.YEAR, year);
            testCalendar.set(Calendar.MONTH, month - 1); // MONTH is zero-based
            int maxDays = testCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (day <= maxDays) {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Prompts the user for a refined time slot input, by providing a list of available time slots
     * during a given date interval.
     * @return The user's further input for a refined time slot during the date interval.
     */
    public String getAvailableTimeSlotsBetween(List<ScheduleEvent> scheduledAppointments, Pair<Calendar> dateInterval) {
        List<Pair<Calendar>> availableTimeList = getSlotsAsList(scheduledAppointments, dateInterval);
        return slotsListToString(availableTimeList);
    }

    /**
     * Gets available time slots for a given date interval.
     * A time period is considered available when the doctor does not have an appointment during it.
     * @return Available time slots during the date interval, represented as a list, unsorted due to implementation.
     */
    private List<Pair<Calendar>> getSlotsAsList(List<ScheduleEvent> scheduledAppts, Pair<Calendar> dateInterval) {

        List<Pair<Calendar>> emptySlots = new ArrayList<>();
        if (!scheduledAppts.isEmpty()) {
            Calendar firstScheduleStart = scheduledAppts.get(0).getDate().getKey();
            Calendar firstDayStart = (Calendar) firstScheduleStart.clone();
            firstDayStart.set(Calendar.HOUR_OF_DAY, 9);
            firstDayStart.set(Calendar.MINUTE, 0);
            if (firstScheduleStart.after(firstDayStart)) {
                emptySlots.add(new Pair<>(firstDayStart, firstScheduleStart));
            }
            for (int i = 0; i < scheduledAppts.size() - 1; i++) {
                Calendar currentEnd = scheduledAppts.get(i).getDate().getValue();
                Calendar nextStart = scheduledAppts.get(i + 1).getDate().getKey();
                if (nextStart.get(Calendar.DATE) != currentEnd.get(Calendar.DATE)) {
                    // the next appointment is on a different date already
                    // the remaining (if applicable) current day will be free all the way till day end
                    Calendar dayEnd = (Calendar) currentEnd.clone();
                    dayEnd.set(Calendar.HOUR_OF_DAY, 18);
                    dayEnd.set(Calendar.MINUTE, 0);
                    if (currentEnd.before(dayEnd)) {
                        emptySlots.add(new Pair<>(currentEnd, dayEnd));
                    }
                    // also, on the day where the next appointment is,
                    // the time preceding (if applicable) the start of the next appointment will be free
                    Calendar dayStart = (Calendar) nextStart.clone();
                    dayStart.set(Calendar.HOUR_OF_DAY, 9);
                    dayStart.set(Calendar.MINUTE, 0);
                    if (nextStart.after(dayStart)) {
                        emptySlots.add(new Pair<>(dayStart, nextStart));
                    }
                } else if (currentEnd.before(nextStart)) {
                    emptySlots.add(new Pair<>(currentEnd, nextStart));
                }
            }
            Calendar lastScheduleEnd = scheduledAppts.get(scheduledAppts.size() - 1).getDate().getValue();
            Calendar lastDayEnd = (Calendar) lastScheduleEnd.clone();
            lastDayEnd.set(Calendar.HOUR_OF_DAY, 18);
            lastDayEnd.set(Calendar.MINUTE, 0);
            if (lastScheduleEnd.before(lastDayEnd)) {
                emptySlots.add(new Pair<>(lastScheduleEnd, lastDayEnd));
            }
        }
        // Now need to find days when there is no scheduled appointment, ie. the day is completely empty
        Calendar dayPointer = (Calendar) dateInterval.getKey().clone();
        // since the maximum interval is one month, using DAY_OF_YEAR will always be safe
        while (!dayPointer.after(dateInterval.getValue())) {
            boolean isEmptyDay = true;
            if (!scheduledAppts.isEmpty()) {
                for (ScheduleEvent appt: scheduledAppts) {
                    if (appt.getDate().getKey().get(Calendar.DAY_OF_YEAR) == dayPointer.get(Calendar.DAY_OF_YEAR)) {
                        isEmptyDay = false;
                        break;
                    }
                }
                if (isEmptyDay) {
                    Calendar emptyDayStart = (Calendar) dayPointer.clone();
                    Calendar emptyDayEnd = (Calendar) dayPointer.clone();
                    setDateStartAndEnd(emptyDayStart, emptyDayEnd);
                    emptySlots.add(new Pair<>(emptyDayStart, emptyDayEnd));
                }
            } else {
                Calendar emptyDayStart = (Calendar) dayPointer.clone();
                Calendar emptyDayEnd = (Calendar) dayPointer.clone();
                setDateStartAndEnd(emptyDayStart, emptyDayEnd);
                emptySlots.add(new Pair<>(emptyDayStart, emptyDayEnd));
            }
            dayPointer.add(Calendar.DATE, 1); // next date
        }
        return emptySlots; // unsorted
    }

    /**
     * Converts a list representation of slots to a string representation.
     * @param slots List representation of slots.
     * @return String representation of the given list of slots.
     */
    private String slotsListToString(List<Pair<Calendar>> slots) {
        if (slots.isEmpty()) {
            return MESSAGE_NO_SLOTS;
        }
        StringBuilder availableTimeBuilder = new StringBuilder();
        availableTimeBuilder.append(MESSAGE_HAVE_SLOTS);
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat timeFormatter = new SimpleDateFormat("kk:mm");
        int datePointer = -1; // initialize to -1 such that the if block will always be executed in the first iteration
        for (int j = 0; j < slots.size(); j++) {
            Calendar slotStart = slots.get(j).getKey();
            Calendar slotsEnd = slots.get(j).getValue();
            if (slotStart.get(Calendar.DATE) != datePointer) {
                // a new date
                Date formattableDate = slots.get(j).getKey().getTime();
                String formattedDate = dateFormatter.format(formattableDate);
                availableTimeBuilder.append("\n" + formattedDate + ": \n");
                datePointer = slotStart.get(Calendar.DATE);
            }
            Date formattableStartTime = slotStart.getTime();
            Date formattableEndTime = slotsEnd.getTime();
            String start = timeFormatter.format(formattableStartTime);
            String end = timeFormatter.format(formattableEndTime);
            availableTimeBuilder.append(start + " - " + end + "\n");
        }
        return availableTimeBuilder.toString();
    }

    /**
     * Parses the user input for the final chosen time slot.
     * The current implementation assumes user input in DD/MM/YYYY hh:mm - hh:mm format
     * @param timeSlotInput User input for final time slot.
     * @return The final Pair of Calendar objects ready to be stored into a ScheduleEvent object.
     */
    public Pair<Calendar> parseTimeSlot(String timeSlotInput) throws ParseException {
        String[] splitString = timeSlotInput.split("\\s+");
        if (splitString.length != 4) { // {"DD/YMM/YYYY", "hh:mm", "-", "hh:mm"}
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_PROMPT_TIMESLOT_FORMAT));
        }
        String ddmmyyyy = splitString[0];
        String startTime = splitString[1];
        String endTime = splitString[3];
        if (!isValidDateFormat(ddmmyyyy)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_PROMPT_TIMESLOT_FORMAT));
        }
        Pair<Calendar> timeSlot = getDateFromSpecified(ddmmyyyy);
        if (!isValidTimeFormat(startTime) || !isValidTimeFormat(endTime)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_PROMPT_TIMESLOT_FORMAT));
        }
        setSlotStartAndEnd(timeSlot, startTime, endTime);
        if (!timeSlot.getKey().before(timeSlot.getValue())) {
            throw new ParseException(String.format(MESSAGE_INVALID_SLOT, MESSAGE_END_BEFORE_START));
        }
        return timeSlot;
    }

    /**
     * Checks if the input string has valid numbers for each field in hh:mm format, where
     * hour must be between 9 and 18 inclusive, and minute must be between 0 and 59 inclusive.
     * @param time Input string.
     */
    private boolean isValidTimeFormat(String time) {
        String[] splitString = time.split(":");
        if (splitString.length != 2) { // {"hh", "mm"}
            return false;
        }
        try {
            int hour = Integer.parseInt(splitString[0]);
            int minute = Integer.parseInt(splitString[1]);
            if (hour < 9 || hour > 18) {
                return false;
            }
            if (minute < 0 || minute > 59) {
                return false;
            }
            if (hour == 18 && minute != 0) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Sets the hour and minute fields in the given time slot according to string inputs.
     * @param timeSlot The time slot to be set.
     * @param startTime The start time for the time slot, in hh:mm format.
     * @param endTime The end time for the time slot, in hh:mm format.
     */
    private void setSlotStartAndEnd(Pair<Calendar> timeSlot, String startTime, String endTime) {
        String[] startHourMin = startTime.split(":");
        String[] endHourMin = endTime.split(":");
        timeSlot.getKey().set(Calendar.HOUR_OF_DAY, Integer.parseInt(startHourMin[0]));
        timeSlot.getKey().set(Calendar.MINUTE, Integer.parseInt(startHourMin[1]));
        timeSlot.getValue().set(Calendar.HOUR_OF_DAY, Integer.parseInt(endHourMin[0]));
        timeSlot.getValue().set(Calendar.MINUTE, Integer.parseInt(endHourMin[1]));
    }

    /**
     * Seconds and Milliseconds of the given Calendar object are zeroed out as precision is only required up to minute.
     */
    private void zeroOutExtraPrecision(Calendar calendar) {
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Applies working hours on two given dates.
     * The start date will be set to 9:00 while the end date will be set to 18:00
     * @param start The start time.
     * @param end The end time.
     */
    private void setDateStartAndEnd(Calendar start, Calendar end) {
        start.set(Calendar.HOUR_OF_DAY, 9);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        end.set(Calendar.HOUR_OF_DAY, 18);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);
    }

}






