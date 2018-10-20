package seedu.address.logic.parser;

import java.util.Calendar;
import java.util.List;

import javafx.util.Pair;
import seedu.address.model.ScheduleModel;
import seedu.address.model.event.ScheduleEvent;


/**
 * Parses date time input arguments and creates the corresponding Calendar objects
 * to represent the start time and end time of an appointment.
 * Accepts date time input in natural expression forms and in fixed format.
 */
public class DateTimeParser {

    private ScheduleModel scheduleModel;

    public DateTimeParser (ScheduleModel model) {
        this.scheduleModel = model;
    }

    /**
     * Generates the intended time duration from given user input.
     * @param dateInput User's initial date input in natural expressions or in a fixed format (DD/MM/YYYY).
     * @return The time slot intended by the user, represented by a Pair of Calendar objects.
     */
    public Pair<Calendar, Calendar> parseDateTime(String dateInput) {
        Pair<Calendar, Calendar> resultantDateInterval = parseDate(dateInput);
        Pair<Calendar, Calendar> resultantTimeSlot = parseTime(resultantDateInterval);
        return resultantTimeSlot;
    }

    /**
     * Parses date from input string.
     * @param input Input string for date(s).
     * @return The date interval meant by the string input, represented as a Pair of Calendar objects.
     */
    private Pair<Calendar, Calendar> parseDate(String input) {
        Calendar currentTime = Calendar.getInstance();
        Pair<Calendar, Calendar> resultantDateInterval = getResultantDate(currentTime, input);
        return resultantDateInterval;

    }

    /**
     * Refines the datetime interval into a specific time slot on a day.
     * @param resultantDateInterval Date interval.
     * @return The refined final time slot ready to be inserted into the schedule.
     */
    private Pair<Calendar, Calendar> parseTime(Pair<Calendar, Calendar> resultantDateInterval) {
        String timeSlot = promptForTimeSlot(resultantDateInterval);
        // TO-DO
        // now do the parsing from the string input to Pair<Calendar, Calendar>
        // the final Pair of Calendar should be ready to be put into a ScheduleEvent;
        return null;
    }

    /**
     * Prompts the user for a refined time slot input, by providing a list of available time slots
     * during a given date interval.
     * @param resultantDateInterval Date interval.
     * @return The user's further input for a refined time slot during the date interval.
     */
    private String promptForTimeSlot(Pair<Calendar, Calendar> resultantDateInterval) {
        String availableTime = getAvailableTimeBetween(resultantDateInterval);
        Prompt prompt = new Prompt();
        return prompt.promptForMoreInput(availableTime);
    }

    /**
     * Gets available time slots for a given date interval.
     * @param resultantDateInterval Date interval.
     * @return Available time slots during the date interval, represented as a String.
     */
    private String getAvailableTimeBetween(Pair<Calendar, Calendar> resultantDateInterval) {
        scheduleModel.updateFilteredEventList((scheduleEvent) -> {
            return scheduleEvent.getDate().getKey().after(resultantDateInterval.getKey())
                    && scheduleEvent.getDate().getValue().before(resultantDateInterval.getValue());
        });
        // after and before here are strictly
        // this probably means I need to set my date start time at 8:59 and end at 18:01
        List<ScheduleEvent> scheduledAppointments = scheduleModel.getFilteredEventList();
        StringBuilder availableTime = new StringBuilder();
        // TO-DO
        // now need to find the complement of scheduledAppointments;
        return availableTime.toString();
    }

    /**
     * Finds the date range intended from a given date/duration input and the current time.
     * @param currentTime Current time.
     * @param dateInput Input string, possibly phrased in natural expressions.
     * @return Date range intended by the input string.
     */
    private Pair<Calendar, Calendar> getResultantDate(Calendar currentTime, String dateInput) {

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
            return getDateFromSpecified(dateInput); // user actually inputs the date (eg. 13/12/2018)
        }

    }

    /**
     * Parser "this ..." or "next ..." commands.
     * eg. "this week" "next month" "next Thur".
     * @param currentTime Current time.
     * @param dateInput Input string.
     * @return Date range intended by the input string.
     */
    private Pair<Calendar, Calendar> parseThisOrNext(Calendar currentTime, String dateInput) {
        String[] splitString = dateInput.split("\\s+");
        assert splitString[0].equals("next") || splitString[0].equals("in");
        switch (splitString[1]) {
        case "week":
            if (splitString[0].equals("next")) {
                return getWeekDates(currentTime, 1);
            } else {
                return getWeekDates(currentTime, 0);
            }
        case "month":
            if (splitString[0].equals("next")) {
                return getMonthDates(currentTime, 1);
            } else {
                return getMonthDates(currentTime, 0);
            }
        default:
            // do nothing
        }
        int dayOfWeek = -1; // requires a check somewhere
        if (splitString[1].contains("Mon")) {
            dayOfWeek = 0;
        } else if (splitString[1].contains("Tue")) {
            dayOfWeek = 1;
        } else if (splitString[1].contains("Wed")) {
            dayOfWeek = 3;
        } else if (splitString[1].contains("Thu")) {
            dayOfWeek = 4;
        } else if (splitString[1].contains("Fri")) {
            dayOfWeek = 5;
        } else if (splitString[1].contains("Sat")) {
            dayOfWeek = 6;
        } else if (splitString[1].contains("Sun")) {
            dayOfWeek = 7;
        }
        if (splitString[0].equals("next")) {
            return getWeekDayDate(currentTime, dayOfWeek, 1);
        } else if (splitString[0].equals("this")) {
            return getWeekDayDate(currentTime, dayOfWeek, 0);
        }
        return null; // need to handle
    }

    /**
     * Gets the date from a given day of the week and offset from the current week.
     * @param currentTime Current time.
     * @param dayOfWeek Day of the week, where 0 represents Monday and 6 represents Sunday.
     * @param offset Offset from the current week, where 0 represents the current weeek, 1 represents the next week etc.
     * @return The date intended with working hours applied.
     */
    private Pair<Calendar, Calendar> getWeekDayDate(Calendar currentTime, int dayOfWeek, int offset) {
        Calendar date = (Calendar) currentTime.clone();
        date.setFirstDayOfWeek(Calendar.MONDAY);
        date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        date.add(Calendar.WEEK_OF_YEAR, offset); // may need to consider the wrapping around at the year boundary
        date.add(Calendar.DATE, dayOfWeek);
        Calendar dateStart = (Calendar) date.clone();
        Calendar dateEnd = (Calendar) date.clone();
        setDateStartAndEnd(dateStart, dateEnd);
        Pair<Calendar, Calendar> finalDate = new Pair<>(dateStart, dateEnd);
        return finalDate;
    }

    /**
     * Parses "in * day(s)/week(s)/month(s)" commands.
     * @param currentTime Current time.
     * @param dateInput Input string.
     * @return Date range intended by the input string.
     */
    private Pair<Calendar, Calendar> parseIn(Calendar currentTime, String dateInput) {
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
        return null;
    }

    /**
     * Gets the date from the current time and offset from the current date.
     * @param currentDate Current time.
     * @param offset Offset from the current date.
     * @return The date intended with working hours applied.
     */
    private Pair<Calendar, Calendar> getSingleDate(Calendar currentDate, int offset) {
        Calendar dateStart = (Calendar) currentDate.clone();
        Calendar dateEnd = (Calendar) currentDate.clone();
        dateStart.add(Calendar.DATE, offset);
        dateEnd.add(Calendar.DATE, offset);
        setDateStartAndEnd(dateStart, dateEnd);
        Pair<Calendar, Calendar> date = new Pair<>(dateStart, dateEnd);
        return date;
    }

    /**
     * Applies working hours on a given date.
     * @param start The start time.
     * @param end The end time.
     */
    private void setDateStartAndEnd(Calendar start, Calendar end) {
        start.set(Calendar.HOUR, 8);
        start.set(Calendar.MINUTE, 59);
        end.set(Calendar.HOUR, 18);
        end.set(Calendar.MINUTE, 1);
    }

    /**
     * Gets the dates for an entire week from the current time and an offset from the current week.
     * @param currentDate Current time.
     * @param offset Offset from the current week.
     * @return The dates intended with working hours applied.
     */
    private Pair<Calendar, Calendar> getWeekDates(Calendar currentDate, int offset) {
        // TO-DO
        // get the next week start date and end date

        return null;

    }

    /**
     * Gets the dates for an entire month from the current time and an offset from the current month.
     * @param currentDate Current time.
     * @param offset Offset from the current month.
     * @return The dates intended with working hours applied.
     */
    private Pair<Calendar, Calendar> getMonthDates(Calendar currentDate, int offset) {
        // TO-DO
        // get the next month start date and end date

        return null;
    }

    /**
     * Gets the dates for the next seven days from the current time.
     * @param currentDate Current time.
     * @return The dates intended with working hours applied.
     */
    private Pair<Calendar, Calendar> getNearFutureDates(Calendar currentDate) {
        Calendar startDate = (Calendar) currentDate.clone();
        Calendar endDate = (Calendar) currentDate.clone();
        startDate.add(Calendar.DATE, 1);
        endDate.add(Calendar.DATE, 7);
        setDateStartAndEnd(startDate, endDate);
        Pair<Calendar, Calendar> dateDuration = new Pair<>(startDate, endDate);
        return dateDuration;
    }

    /**
     * Gets the date from a specified date input in a fixed DD/MM/YYYY format
     * @param dateTimeInput The specified date input in DD/MM/YYYY format
     * @return The date intended with working hours applied.
     */
    private Pair<Calendar, Calendar> getDateFromSpecified(String dateTimeInput) {
        // TO-DO
        // check if it is really a valid specified date input
        // if yes, create a Calendar object from the specified date input from user

        return null;
    }

}






