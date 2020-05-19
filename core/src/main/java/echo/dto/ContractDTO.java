package echo.dto;

import echo.dto.enums.ContractStatus;
import echo.dto.enums.TimeSheetFrequency;
import echo.models.FederalState;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class ContractDTO extends SimpleDTO {
    private String description;
    private ContractStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate terminationDate;
    private double hoursPerWeek;
    private PersonDTO supervisor;
    private List<PersonDTO> delegates;
    private PersonDTO employee;
    private List<PersonDTO> secretaries;
    private List<TimeSheetDTO> timeSheets;
    private TimeSheetFrequency frequency;
    private FederalState federalState;
    private int archiveDuration;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    public double getHoursPerWeek() {
        return hoursPerWeek;
    }

    public void setHoursPerWeek(double hoursPerWeek) {
        this.hoursPerWeek = hoursPerWeek;
    }

    public PersonDTO getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(PersonDTO supervisor) {
        this.supervisor = supervisor;
    }

    public List<PersonDTO> getDelegates() {
        return delegates;
    }

    public void setDelegates(List<PersonDTO> delegates) {
        this.delegates = delegates;
    }

    public PersonDTO getEmployee() {
        return employee;
    }

    public void setEmployee(PersonDTO employee) {
        this.employee = employee;
    }

    public List<PersonDTO> getSecretaries() {
        return secretaries;
    }

    public void setSecretaries(List<PersonDTO> secretaries) {
        this.secretaries = secretaries;
    }

    public List<TimeSheetDTO> getTimeSheets() {
        return timeSheets;
    }

    public void setTimeSheets(List<TimeSheetDTO> timeSheets) {
        this.timeSheets = timeSheets;
    }

    public TimeSheetFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(TimeSheetFrequency frequency) {
        this.frequency = frequency;
    }

    public FederalState getFederalState() {
        return federalState;
    }

    public void setFederalState(FederalState federalState) {
        this.federalState = federalState;
    }

    public int getArchiveDuration() {
        return archiveDuration;
    }

    public void setArchiveDuration(int archiveDuration) {
        this.archiveDuration = archiveDuration;
    }

    /**
     * Returns the total number of hours the student assistant still has to work in
     * order to fulfill his contract hours
     *
     * @return double
     */
    public double getHoursDue() {
        return timeSheets == null ? 0d : timeSheets
                .stream()
                .map(TimeSheetDTO::getHoursDue)
                .mapToDouble(Double::valueOf)
                .sum();
    }

    /**
     * Returns the total number of hours the student assistant has worked so far
     *
     * @return double
     */
    public double getHoursDone() {
        return timeSheets == null ? 0d : timeSheets
                .stream()
                .map(TimeSheetDTO::getHoursDone)
                .mapToDouble(Double::valueOf)
                .sum();
    }

    /**
     * Returns the total sum of the balance of all time sheets
     *
     * @return double
     */
    public double getHoursBalance() {
        return timeSheets == null ? 0d : timeSheets
                .stream()
                .map(TimeSheetDTO::getHoursBalance)
                .mapToDouble(Double::valueOf)
                .sum();
    }

    /**
     * Returns the vacation hours of a student assistant
     * <p>
     *
     * @return double
     */
    public double getVacationHours() {
        Period durationOfContract = Period.between(
                this.startDate.withDayOfMonth(1),
                this.endDate.withDayOfMonth(1));
        double contractYears = (double) durationOfContract.getMonths() / 12;
        return 20 * contractYears * (hoursPerWeek / 5);
    }
}
