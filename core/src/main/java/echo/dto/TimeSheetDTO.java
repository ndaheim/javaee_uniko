package echo.dto;

import de.jollyday.Holiday;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameters;
import echo.dto.enums.ReportType;
import echo.dto.enums.TimeSheetFrequency;
import echo.dto.enums.TimeSheetStatus;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TimeSheetDTO extends SimpleDTO {
    private ReportType type;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate signedByEmployee;
    private LocalDate signedBySupervisor;
    private TimeSheetStatus timeSheetStatus;
    private List<WorkReportDTO> workReports;
    private ContractDTO contract;

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getSignedByEmployee() {
        return signedByEmployee;
    }

    public void setSignedByEmployee(LocalDate signedByEmployee) {
        this.signedByEmployee = signedByEmployee;
    }

    public LocalDate getSignedBySupervisor() {
        return signedBySupervisor;
    }

    public void setSignedBySupervisor(LocalDate signedBySupervisor) {
        this.signedBySupervisor = signedBySupervisor;
    }

    public TimeSheetStatus getTimeSheetStatus() {
        return timeSheetStatus;
    }

    public void setTimeSheetStatus(TimeSheetStatus timeSheetStatus) {
        this.timeSheetStatus = timeSheetStatus;
    }

    public List<WorkReportDTO> getWorkReports() {
        return workReports;
    }

    public void setWorkReports(List<WorkReportDTO> workReports) {
        this.workReports = workReports;
    }

    public ContractDTO getContract() {
        return contract;
    }

    public void setContract(ContractDTO contract) {
        this.contract = contract;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate entryDate) {
        this.startDate = entryDate;
    }

    /**
     * Returns the number of hours done in the duration of the TimeSheet
     *
     * @return double
     */
    public double getHoursDone() {
        return this.workReports.isEmpty() ? 0.0 :
                this.workReports
                        .stream()
                        .map(WorkReportDTO::getHoursDone)
                        .mapToDouble(Double::valueOf)
                        .sum();
    }

    /**
     * Returns the number of hours still due in the week of the TimeSheet
     *
     * @return double
     */
    public double getHoursDue() {
        HolidayManager m = HolidayManager.getInstance(ManagerParameters.create("de"));
        Set<LocalDate> holidays = m.getHolidays(startDate, endDate, contract.getFederalState().getAbbreviation())
                .stream()
                .map(Holiday::getDate)
                .collect(Collectors.toSet());

        int workingDaysInPeriod = 0;
        long daysBetween = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays();
        System.out.println("Duration is: " + daysBetween);
        for (long i=0; i<=daysBetween; i++) {
            if (startDate.plusDays(i).getDayOfWeek() == DayOfWeek.SATURDAY || startDate.plusDays(i).getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }
            if (holidays.contains(startDate.plusDays(i))) {
                continue;
            }
            workingDaysInPeriod += 1;
        }

        return workingDaysInPeriod * getContract().getHoursPerWeek() / 5;
    }

    /**
     * Returns the hours balance of the TimeSheet
     *
     * @return double
     */
    public double getHoursBalance() {
        return getHoursDone() - getHoursDue();
    }

    /**
     * Returns the vacation hours of the TimeSheet
     *
     * @return double
     */
    public double getVacationHours() {
        double hours = 0;
        for(WorkReportDTO workReportDTO : workReports) {
            if(workReportDTO.getType().equals(ReportType.VACATION)) {
                hours += workReportDTO.getHoursDone();
            }
        }
        return hours;
    }
}
