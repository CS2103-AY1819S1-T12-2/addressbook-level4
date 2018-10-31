package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Optional;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.Version;
import seedu.address.commons.events.ui.ExitAppRequestEvent;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.commons.util.ConfigUtil;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.AddressBook;
import seedu.address.model.AddressBookModel;
import seedu.address.model.AddressBookModelManager;
import seedu.address.model.DiagnosisModel;
import seedu.address.model.DiagnosisModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlySchedule;
import seedu.address.model.Schedule;
import seedu.address.model.ScheduleModel;
import seedu.address.model.ScheduleModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.AddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.ScheduleStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.storage.UserPrefsStorage;
import seedu.address.storage.XmlAddressBookStorage;
import seedu.address.storage.XmlScheduleStorage;
import seedu.address.ui.PromptWindow;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * The main entry point to the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(0, 6, 0, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected AddressBookModel addressBookModel;
    protected ScheduleModel scheduleModel;
    protected DiagnosisModel diagnosisModel;
    protected Config config;
    protected UserPrefs userPrefs;
    protected Stage secondaryStage = new Stage();

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing PatientBook ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        userPrefs = initPrefs(userPrefsStorage);
        AddressBookStorage addressBookStorage = new XmlAddressBookStorage(userPrefs.getAddressBookFilePath());
        ScheduleStorage scheduleStorage = new XmlScheduleStorage(userPrefs.getScheduleFilePath());
        storage = new StorageManager(addressBookStorage, scheduleStorage, userPrefsStorage);

        initLogging(config);

        addressBookModel = initModelManager(storage, userPrefs);
        scheduleModel = initScheduleModel(storage, userPrefs);
        diagnosisModel = new DiagnosisModelManager();

        logic = new LogicManager(addressBookModel, scheduleModel, diagnosisModel);

        ui = new UiManager(logic, config, userPrefs);

        initEventsCenter();
    }
    /**
     * Returns a {@code ScheduleModelManager} with the data from [@code storage}'s scheduel and {@code userPrefs}. <br>
     * The data fromthe sample schedule will be used instead if {@code storage}'s schedule is not found,
     * or an empty schedule will be used instead if errors occur when reading {@code storage}'s schedule.
     */
    private ScheduleModel initScheduleModel(Storage storage, UserPrefs userPrefs) {
        Optional<ReadOnlySchedule> scheduleOptional;
        ReadOnlySchedule initialData;

        try {
            scheduleOptional = storage.readSchedule();
            if (!scheduleOptional.isPresent()) {
                logger.info("Data file not found. Will be staring with sample Schedule.");
            }
            initialData = scheduleOptional.orElseGet(SampleDataUtil::getSampleSchedule);
        } catch (DataConversionException e) {
            logger.warning("Data file not in the correct format. Will be starting with an empty Schedule.");
            initialData = new Schedule();
        } catch (IOException e) {
            logger.warning("Error reading from file. Will be starting with an empty schedule.");
            initialData = new Schedule();
        } catch (ParseException e) {
            logger.warning("Error parsing dates from file. Will be starting with an empty schedule.");
            initialData = new Schedule();
        }

        return new ScheduleModelManager(initialData, userPrefs);
    }

    /**
     * Returns a {@code AddressBookModelManager} with the data from {@code storage}'s address book and {@code userPrefs}
     * . <br>
     * The data from the sample address book will be used instead if {@code storage}'s address book is not found,
     * or an empty address book will be used instead if errors occur when reading {@code storage}'s address book.
     */
    private AddressBookModel initModelManager(Storage storage, UserPrefs userPrefs) {
        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook initialData;
        try {
            addressBookOptional = storage.readAddressBook();
            if (!addressBookOptional.isPresent()) {
                logger.info("Data file not found. Will be starting with a sample AddressBook.");
            }
            initialData = addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (DataConversionException e) {
            logger.warning("Data file not in the correct format. Will be starting with an empty AddressBook.");
            initialData = new AddressBook();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty AddressBook");
            initialData = new AddressBook();
        }

        return new AddressBookModelManager(initialData, userPrefs);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataConversionException e) {
            logger.warning("Config file at " + configFilePathUsed + " is not in the correct format. "
                    + "Using default config properties");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using prefs file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataConversionException e) {
            logger.warning("UserPrefs file at " + prefsFilePath + " is not in the correct format. "
                    + "Using default user prefs");
            initializedPrefs = new UserPrefs();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty AddressBook");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    private void initEventsCenter() {
        EventsCenter.getInstance().registerHandler(this);
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting PatientBook " + MainApp.VERSION);
        ui.start(primaryStage, secondaryStage);
    }

    /**
     * Opens a prompt window to display the available time slot. If the user
     * clicks Enter, the command of the user is saved as a string and true
     * is returned.
     *
     * @param input the available time slot to be displayed
     * @return true if the user clicked Enter, false otherwise.
     */
    public PromptWindow showPromptWindow (String input) {
        try {
            // Load the fxml file and create a new stage for the Prompt Window.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/PromptWindow.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the Prompt Window Stage.
            Stage promptStage = new Stage();
            promptStage.setTitle("Prompt Window");
            promptStage.initModality(Modality.WINDOW_MODAL);
            promptStage.initOwner(null);
            Scene scene = new Scene(page);
            promptStage.setScene(scene);

            // Set the input into the controller.
            PromptWindow controller = loader.getController();
            controller.setPromptStage(promptStage);
            controller.setDisplay(input);
            scene.getAccelerators().put(controller.getEnter(), controller.getPressEnter());
            scene.getAccelerators().put(controller.getCancel(), controller.getPressCancel());

            // Show the dialog and wait until the user closes it
            promptStage.showAndWait();

            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return new PromptWindow();
        }
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping Address Book ] =============================");
        ui.stop();
        try {
            storage.saveUserPrefs(userPrefs);
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
        Platform.exit();
        System.exit(0);
    }

    @Subscribe
    public void handleExitAppRequestEvent(ExitAppRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
