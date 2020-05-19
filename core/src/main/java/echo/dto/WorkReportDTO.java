package echo.dto;

import echo.dto.enums.ReportType;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;

public class WorkReportDTO extends SimpleDTO {
    private ReportType type;
    private LocalDate day;
    private Time startTime;
    private Time endTime;
    private String description;
    private TimeSheetDTO timeSheet;

    public TimeSheetDTO getTimeSheet() {
        return timeSheet;
    }

    public void setTimeSheet(TimeSheetDTO timeSheet) {
        this.timeSheet = timeSheet;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        System.out.println("Setter called with " + description);
        this.description = description;
    }

    public ReportType getType() {

        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    /**
     * Returns the hours done in the WorkReport
     *
     * @return double
     */
    public double getHoursDone() {
        return Duration.between(this.startTime.toLocalTime(), this.endTime.toLocalTime()).toHours();
    }

}
