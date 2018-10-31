package seedu.address.testutil;

import seedu.address.model.Schedule;
import seedu.address.model.event.ScheduleEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypicalEvents {

    /* TODO: copy the event properties exactly out of
    XmlSerializableScheduleTest/typicalEventsSchedule.xml*/
    public static final ScheduleEvent e1 = new ScheduleEventBuilder().build();
    public static final ScheduleEvent e2 = new ScheduleEventBuilder().build();
    public static final ScheduleEvent e3 = new ScheduleEventBuilder().build();
    public static final ScheduleEvent e4 = new ScheduleEventBuilder().build();
    public static final ScheduleEvent e5 = new ScheduleEventBuilder().build();

    public static Schedule getTypicalSchedule() {
        Schedule s = new Schedule();
        for (ScheduleEvent scheduleEvent : getTypicalEvents()) {
            s.addScheduleEvent(scheduleEvent);
        }
        return s;
    }

    public static List<ScheduleEvent> getTypicalEvents() {
        return new ArrayList<>(Arrays.asList(e1, e2, e3, e4, e5));
    }
}
