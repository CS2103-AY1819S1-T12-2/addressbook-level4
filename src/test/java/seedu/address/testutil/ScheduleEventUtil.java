package seedu.address.testutil;

import static seedu.address.logic.parser.CmdTypeCliSyntax.CMDTYPE_APPOINTMENT;
import static seedu.address.logic.parser.ScheduleEventCliSyntax.PREFIX_DETAILS;
import static seedu.address.logic.parser.ScheduleEventCliSyntax.PREFIX_PERSON;
import static seedu.address.logic.parser.ScheduleEventCliSyntax.PREFIX_TAGS;

import java.util.Set;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.model.event.ScheduleEvent;
import seedu.address.model.tag.Tag;




/**
 * A utility class for ScheduleEvent.
 */
public class ScheduleEventUtil {

    /**
     * Returns an add command string for adding the {@code scheduleEvent}.
     */
    public static String getAddCommand(ScheduleEvent scheduleEvent) {
        return AddCommand.COMMAND_WORD + " " + CMDTYPE_APPOINTMENT + " " + getScheduleEventDetails(scheduleEvent);
    }

    /**
     * Returns the part of command string for the given {@code scheduleEvent}'s details.
     */
    public static String getScheduleEventDetails(ScheduleEvent scheduleEvent) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_PERSON + scheduleEvent.getPersonId().value + " ");

        //datetime goes here
        /*
        // none of this in initial input
        sb.append(PREFIX_DETAILS + scheduleEvent.getDetails() + " ");
        scheduleEvent.getTags().stream().forEach(
                s -> sb.append(PREFIX_TAGS + s.tagName + " ")
        );
        */
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditScheduleEvent Descriptor}'s details.
     */
    public static String getEditScheduleEventDescriptorDetails(EditCommand.EditScheduleEventDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getPerson().ifPresent(personId -> sb.append(PREFIX_PERSON).append(personId.value).append(" "));

        //datetime goes here
        descriptor.getDetails().ifPresent(details -> sb.append(PREFIX_DETAILS).append(details).append(" "));
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (tags.isEmpty()) {
                sb.append(PREFIX_TAGS);
            } else {
                tags.forEach(s -> sb.append(PREFIX_TAGS).append(s.tagName).append(" "));
            }
        }
        return sb.toString();
    }

    /**
     * Since ScheduleEvents are uniquely identified by ID, testing requires us to compare everything but
     * ID to ensure that other fields were added correctly. This method returns whether all fields match. IDs may
     * match, but is not a requirement.
     */
    public static boolean matchEventProperties(ScheduleEvent event, ScheduleEvent otherEvent) {
        return event.getDate().equals(otherEvent.getDate())
                && event.getDetails().equals(otherEvent.getDetails())
                && event.getPersonId().equals(otherEvent.getPersonId())
                && event.getTags().equals(otherEvent.getTags());
    }
}
