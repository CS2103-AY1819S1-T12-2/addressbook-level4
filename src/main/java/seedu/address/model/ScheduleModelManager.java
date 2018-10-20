package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.ScheduleChangedEvent;
import seedu.address.model.event.ScheduleEvent;

/**
 * Actual schedule model manager for handling schedule in the application.
 */
public class ScheduleModelManager extends ComponentManager implements ScheduleModel {
    private static final Logger logger = LogsCenter.getLogger(ScheduleModelManager.class);

    private final Schedule schedule;
    private final FilteredList<ScheduleEvent> filteredScheduleEventList;

    /**
     * Initializes a ScheduleModelManager with the given schedule and userPrefs.
     */
    public ScheduleModelManager(ReadOnlySchedule schedule, UserPrefs userPrefs) {
        super();
        requireAllNonNull(schedule, userPrefs);

        logger.fine("Initializing with schedule: " + schedule + "and user prefs " + userPrefs);

        this.schedule = new Schedule(schedule);
        this.filteredScheduleEventList = new FilteredList<>(schedule.getAllEventList());
    }

    public ScheduleModelManager() {
        this(new Schedule(), new UserPrefs());
    }

    /**
     * Overwrites existing schedule with the new one.
     * @param newData new schedule to replace the existing one.
     */
    @Override
    public void resetData(ReadOnlySchedule newData) {
        schedule.resetData(newData);
        indicateScheduleChanged();
    }

    @Override
    public ReadOnlySchedule getSchedule() {
        return schedule;
    }

    /** Raises an event to indicate the calendarModel has changed. */
    private void indicateScheduleChanged() {
        raise(new ScheduleChangedEvent(schedule));
    }

    @Override
    public boolean hasEvent(ScheduleEvent scheduleEvent) {
        requireNonNull(scheduleEvent);
        return schedule.hasScheduleEvent(scheduleEvent);
    }

    @Override
    public void deleteEvent(ScheduleEvent target) {
        schedule.removeScheduleEvent(target);
        indicateScheduleChanged();
    }

    @Override
    public void addEvent(ScheduleEvent event) {
        schedule.addScheduleEvent(event);
        this.updateFilteredEventList(PREDICATE_SHOW_ALL_SCHEDULE_EVENTS);
        indicateScheduleChanged();
    }

    @Override
    public void updateEvent(ScheduleEvent target, ScheduleEvent editedScheduleEvent) {
        requireAllNonNull(target, editedScheduleEvent);

        schedule.updateScheduleEvent(target, editedScheduleEvent);
        indicateScheduleChanged();
    }


    //=========== Filtered Event List Accessors ==============================

    /**
     * Returns an unmodifiable view of the list of {@code ScheduleEvent} backed by the internal list of
     * {@code Schedule}.
     */
    @Override
    public ObservableList<ScheduleEvent> getFilteredEventList() {
        return FXCollections.unmodifiableObservableList(filteredScheduleEventList);
    }

    @Override
    public void updateFilteredEventList(Predicate<ScheduleEvent> predicate) {
        requireNonNull(predicate);
        filteredScheduleEventList.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ScheduleModelManager)) {
            return false;
        }

        ScheduleModelManager other = (ScheduleModelManager) obj;
        return schedule.equals(other.schedule);
    }

}
