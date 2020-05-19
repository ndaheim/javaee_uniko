package echo.entities;

import echo.dto.enums.ContractStatus;
import echo.dto.enums.TimeSheetFrequency;
import echo.models.FederalState;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Entity
public class Contract extends SimpleEntity {
    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private LocalDate terminationDate;

    @Column
    private Double hoursPerWeek;

    @Column
    @Enumerated(EnumType.STRING)
    private TimeSheetFrequency frequency;

    @Column
    @Enumerated(EnumType.STRING)
    private FederalState federalState;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Person supervisor;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "CONTRACT_DELEGATE",
            joinColumns = {@JoinColumn(name = "contract_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "delegate_id", referencedColumnName = "id")})
    private List<Person> delegates;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Person employee;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "CONTRACT_SECRETARY",
            joinColumns = {@JoinColumn(name = "contract_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "secretary_id", referencedColumnName = "id")})
    private List<Person> secretaries;

    @OneToMany(orphanRemoval = true)
    private List<TimeSheet> timeSheets;

    @Column
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

    public Double getHoursPerWeek() {
        return hoursPerWeek;
    }

    public void setHoursPerWeek(Double hoursPerWeek) {
        this.hoursPerWeek = hoursPerWeek;
    }

    public TimeSheetFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(TimeSheetFrequency frequency) {
        this.frequency = frequency;
    }

    public Person getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Person supervisor) {
        this.supervisor = supervisor;
    }

    public List<Person> getDelegates() {
        return delegates;
    }

    public void setDelegates(List<Person> delegates) {
        this.delegates = delegates;
    }

    public Person getEmployee() {
        return employee;
    }

    public void setEmployee(Person employee) {
        this.employee = employee;
    }

    public List<Person> getSecretaries() {
        return secretaries;
    }

    public void setSecretaries(List<Person> secretaries) {
        this.secretaries = secretaries;
    }

    public List<TimeSheet> getTimeSheets() {
        return timeSheets;
    }

    public void setTimeSheets(List<TimeSheet> timeSheets) {
        this.timeSheets = timeSheets;
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
     * order to fulfull his contract hours
     *
     * @return double
     */
    @Transient
    public double getHoursDue() {
        return this.timeSheets
                .stream()
                .map(TimeSheet::getHoursDue)
                .mapToDouble(Double::valueOf)
                .sum();
    }

    /**
     * Returns the total number of hours the student assistant has worked so far
     *
     * @return double
     */
    @Transient
    public double getHoursDone() {
        return this.timeSheets
                .stream()
                .map(TimeSheet::getHoursDone)
                .mapToDouble(Double::valueOf)
                .sum();
    }

    /**
     * Returns the vacation hours of a student assistant
     * <p>
     *
     * @return double
     */
    @Transient
    public double getVacationHours() {
        Period durationOfContract = Period.between(
                this.startDate.withDayOfMonth(1),
                this.endDate.withDayOfMonth(1));
        double contractYears = (double) durationOfContract.getMonths() / 12;
        return 20 * contractYears * (hoursPerWeek / 5);
    }

    /**
     * Returns all {@link Person}, who are involved in the {@link Contract}
     *
     * @return
     */
    public List<Person> getInvolvedPersons() {
        List<Person> ret = this.delegates;
        ret.addAll(this.secretaries);
        ret.add(this.employee);
        ret.add(this.supervisor);
        return ret;
    }

}
