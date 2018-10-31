package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;


/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    public final String feedbackToUser;

    public CommandResult(String feedbackToUser) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
    }

    /**
     * Formats the list to a string for command result.
     * @param list
     * @return a formatted string.
     */
    public static String convertListToString(List<?> list) {
        String listString = "1. ";
        int i;
        for (i = 1; i <= list.size(); i++) {

            if (i % 5 == 0) {
                listString = listString.concat(i + ". " + list.get(i - 1) + "\n");
                continue;
            }

            listString = listString.concat(i + ". " + list.get(i - 1).toString() + ", ");

        }

        listString = listString.substring(3);

        if (listString.charAt(listString.length() - 1) == ' ') {
            listString = listString.substring(0, listString.length() - 2);
        }

        listString = listString.concat("\n");
        return listString;
    }

}
