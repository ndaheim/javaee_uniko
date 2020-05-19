package echo.entities;

import echo.dto.enums.TimeSheetStatus;
import echo.models.ReminderMessages;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Reminder extends SimpleEntity {
    @OneToOne
    @JoinColumn(name = "TIMESHEET_ID")
    private TimeSheet timeSheet;

    @ManyToOne
    private Person person;

    public Reminder(TimeSheet timeSheet, Person person) {
        this.timeSheet = timeSheet;
        this.person = person;
        this.person.getReminders().add(this);
    }

    public Reminder() {
    }

    public TimeSheet getTimeSheet() {
        return timeSheet;
    }

    public void setTimeSheet(TimeSheet timeSheet) {
        this.timeSheet = timeSheet;
    }

    /**
     * Returns the HTML message body for the {@link Reminder}
     *
     * @param lang The language of the {@link Person} in ISO 639 code
     * @return the HTML message body for the {@link Reminder}
     */
    public String getHTMLMessage(String lang) {
        String body = (String) ReminderMessages.message.get(timeSheet.getTimeSheetStatus()).get(lang);
        String formattedBody;

        if (timeSheet.getTimeSheetStatus() == TimeSheetStatus.IN_PROGESS) {
            formattedBody = String.format(body, timeSheet.getStartDate());
        } else {
            formattedBody = String.format(body, timeSheet.getStartDate(), person.getFullName());
        }

        return formattedBody;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
