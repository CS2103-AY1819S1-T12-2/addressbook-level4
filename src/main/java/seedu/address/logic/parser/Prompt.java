package seedu.address.logic.parser;

import seedu.address.MainApp;
import seedu.address.logic.parser.exceptions.PromptException;
import seedu.address.ui.PromptWindow;


/**
 * Prompt handler for users.
 */
public class Prompt {


    public static final String MESSAGE_PROMPT_NOTES = "Any additional notes for this appointment?\n\n";
    public static final String MESSAGE_PROMPT_TAGS = "Any tags for this appointment?\n\n";
    public static final String MESSAGE_PROMPT_TIMESLOT = "Please enter a time slot in DD/MM/YYYY hh:mm - hh:mm: \n\n";
    public static final String MESSAGE_PROMPT_ID = "Please enter the ID of the patient you want to schedule for: \n\n";
    public static final String MESSAGE_PROMPT_TIMESLOT_FORMAT = "Expected format: DD/MM/YYYY hh:mm - hh:mm, "
            + "where hh (hour) must be between 9 and 18 \n"
            + "Eg. 13/12/2018 13:30 - 14:30\n";
    private static final String MESSAGE_PROMPT_CANCEL = "Cancelled command\n";
    private static final String EMPTY_RESPONSE = "";
    private MainApp mainApp = new MainApp();
    private PromptWindow promptWindow;

    /**
     * Gets more input from the user
     * @param messageToUser message to be displayed
     * @return input by the user
     */
    public String promptForMoreInput (String leadingMessage, String messageToUser, boolean isInputCompulsory)
            throws PromptException {
        promptWindow = mainApp.showPromptWindow(leadingMessage + messageToUser);
        if (promptWindow.isEnterClicked()) {
            if (!promptWindow.getInput().equals("")) {
                return promptWindow.getInput();
            } else {
                if (isInputCompulsory) {
                    throw new PromptException(MESSAGE_PROMPT_CANCEL); // treated as cancelling the command
                } else {
                    return EMPTY_RESPONSE;
                }
            }
        }
        throw new PromptException(MESSAGE_PROMPT_CANCEL);
    }
}
