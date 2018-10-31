package seedu.address.logic;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.PatientBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBookModel;
import seedu.address.model.DiagnosisModel;
import seedu.address.model.ScheduleModel;
import seedu.address.model.event.ScheduleEvent;
import seedu.address.model.person.Person;

/**
 * The main LogicManager of the app.
 */
public class LogicManager extends ComponentManager implements Logic {
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final AddressBookModel addressBookModel;
    private final ScheduleModel scheduleModel;
    private final DiagnosisModel diagnosisModel;
    private final CommandHistory history;
    private final PatientBookParser patientBookParser;

    public LogicManager(AddressBookModel addressBookModel, ScheduleModel scheduleModel, DiagnosisModel diagnosisModel) {
        this.addressBookModel = addressBookModel;
        this.scheduleModel = scheduleModel;
        this.diagnosisModel = diagnosisModel;
        history = new CommandHistory();
        patientBookParser = new PatientBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        try {
            Command command = patientBookParser.parseCommand(commandText);
            return command.execute(addressBookModel, scheduleModel, diagnosisModel, history);
        } finally {
            history.add(commandText);
        }
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return addressBookModel.getFilteredPersonList();
    }

    @Override
    public ObservableList<ScheduleEvent> getFilteredEventList() {
        return scheduleModel.getFilteredEventList();
    }

    @Override
    public ListElementPointer getHistorySnapshot() {
        return new ListElementPointer(history.getHistory());
    }
}
