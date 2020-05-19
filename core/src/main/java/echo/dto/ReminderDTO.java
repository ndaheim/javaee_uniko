package echo.dto;

public class ReminderDTO extends SimpleDTO {
    private TimeSheetDTO timeSheet;
    private PersonDTO person;

    public static ReminderDTO of(TimeSheetDTO timeSheet, PersonDTO person) {
        ReminderDTO reminderDTO = new ReminderDTO();
        reminderDTO.setPerson(person);
        reminderDTO.setTimeSheet(timeSheet);
        return reminderDTO;
    }

    public TimeSheetDTO getTimeSheet() {
        return timeSheet;
    }

    public void setTimeSheet(TimeSheetDTO timeSheet) {
        this.timeSheet = timeSheet;
    }

    public PersonDTO getPerson() {
        return person;
    }

    public void setPerson(PersonDTO person) {
        this.person = person;
    }
}
