package seedu.address.logic.parser.exceptions;

/**
 *  Represents the error when a user close the prompt window
 */

public class PromptException extends Exception {

    public PromptException(String message) {
            super(message);
    }
}
