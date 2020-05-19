package echo.logic;

import echo.dto.enums.TimeSheetFrequency;
import echo.dto.enums.TimeSheetStatus;

import javax.ejb.Local;

@Local
public interface ReminderLogic {

    /**
     * Send out all the {@link echo.entities.Reminder} mails via a scheduled job
     */
    void sendReminders();

    /**
     * Create {@link echo.entities.Reminder}s for {@link echo.entities.TimeSheet}s,
     * which are in {@link TimeSheetStatus} IN_PROGRESS
     * with a WEEKLY {@link TimeSheetFrequency}
     * via a scheduled job at the end of the week
     */
    void createWeeklyTimeSheetReminders();

    /**
     * Create {@link echo.entities.Reminder}s for {@link echo.entities.TimeSheet}s,
     * which are in {@link TimeSheetStatus} IN_PROGRESS
     * with a MONTHLY {@link TimeSheetFrequency}
     * via a scheduled job at the end of the month
     */
    void createMonthlyTimeSheetReminders();

    /**
     * Create Reminders for {@link echo.entities.TimeSheet}s, which are in
     * {@link TimeSheetStatus} SIGNED_BY_EMPLOYEE or SIGNED_BY_SUPERVISOR
     *
     * @param id The id of the {@link echo.entities.TimeSheet}, for which the Reminders should be created
     */
    void createRemindersByTimeSheet(long id);

    /**
     * Remove all {@link echo.entities.Reminder}s, which are associated with the given
     * {@link echo.entities.TimeSheet}
     *
     * @param id The id of the {@link echo.entities.TimeSheet}
     */
    void removeByTimeSheet(long id);

}
