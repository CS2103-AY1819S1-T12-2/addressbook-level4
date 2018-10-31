package seedu.address.ui;

import javafx.stage.Stage;

/**
 * API of UI component
 */
public interface Ui {

    /** Starts the UI (and the App).  */
    void start(Stage primaryStage, Stage secondaryStage);

    /** Switches the UI to the Schedule Mode */
    void switchToAppointment();

    /** Switches the UI to the Patient Mode */
    void switchToPatient();

    /** Stops the UI. */
    void stop();

}
