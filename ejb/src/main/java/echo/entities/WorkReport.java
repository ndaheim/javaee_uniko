package echo.entities;

import echo.dto.enums.ReportType;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Entity
@Cacheable(false)
public class WorkReport extends SimpleEntity {
    @Column
    @Enumerated(EnumType.STRING)
    private ReportType type;

    @Column
    private LocalDate day;

    @Column
    private Time startTime;

    @Column
    private Time endTime;

    @Column
    private String description;

    @ManyToOne
    private TimeSheet timeSheet;

    public TimeSheet getTimeSheet() {
        return timeSheet;
    }

    public void setTimeSheet(TimeSheet timeSheet) {
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

    public void setStartTime(Time start) {
        this.startTime = start;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time end) {
        this.endTime = end;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
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
    @Transient
    public double getHoursDone() {
        return TimeUnit.HOURS.toHours(this.endTime.getTime() - this.startTime.getTime());
    }
}