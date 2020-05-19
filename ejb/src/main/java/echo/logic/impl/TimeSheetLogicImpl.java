package echo.logic.impl;

import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameters;
import echo.dao.ContractAccess;
import echo.dao.TimeSheetAccess;
import echo.dao.WorkReportAccess;
import echo.dto.ContractDTO;
import echo.dto.PersonDTO;
import echo.dto.TimeSheetDTO;
import echo.dto.WorkReportDTO;
import echo.dto.enums.ContractStatus;
import echo.dto.enums.ReportType;
import echo.dto.enums.TimeSheetStatus;
import echo.entities.Contract;
import echo.entities.Person;
import echo.entities.TimeSheet;
import echo.entities.WorkReport;
import echo.logic.ReminderLogic;
import echo.logic.TimeSheetLogic;
import echo.mapper.ContractMapper;
import echo.mapper.PersonMapper;
import echo.mapper.TimeSheetMapper;
import echo.mapper.WorkReportMapper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import java.sql.Time;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@LocalBean
public class TimeSheetLogicImpl implements TimeSheetLogic {

    @EJB
    private TimeSheetAccess timeSheetAccess;
    @EJB
    private WorkReportAccess workReportAccess;
    @EJB
    private ContractAccess contractAccess;
    @EJB
    private ReminderLogic reminderLogic;

    private PersonMapper personMapper = new PersonMapper();
    private ContractMapper contractMapper = new ContractMapper();
    private TimeSheetMapper timeSheetMapper = new TimeSheetMapper();
    private WorkReportMapper workReportMapper = new WorkReportMapper();

    @Override
    public WorkReportDTO createWorkReportDTO() {
        WorkReportDTO workReportDTO = new WorkReportDTO();
        workReportDTO.setStartTime(new Time(0));
        workReportDTO.setEndTime(new Time(0));
        workReportDTO.setType(ReportType.WORK);
        workReportDTO.setDescription("");
        return workReportDTO;
    }

    @Override
    public TimeSheetDTO setSignedByEmployee(TimeSheetDTO timeSheetDTO) {
        timeSheetDTO.setSignedByEmployee(LocalDate.now());
        timeSheetDTO.setTimeSheetStatus(TimeSheetStatus.SIGNED_BY_EMPLOYEE);

        this.save(timeSheetDTO);

        reminderLogic.removeByTimeSheet(timeSheetDTO.getId());
        reminderLogic.createRemindersByTimeSheet(timeSheetDTO.getId());

        return timeSheetDTO;
    }

    @Override
    public TimeSheetDTO removeSignedByEmployee(TimeSheetDTO timeSheetDTO) {
        timeSheetDTO.setSignedByEmployee(null);
        timeSheetDTO.setTimeSheetStatus(TimeSheetStatus.IN_PROGESS);
        this.save(timeSheetDTO);

        reminderLogic.removeByTimeSheet(timeSheetDTO.getId());

        return timeSheetDTO;
    }

    @Override
    public TimeSheetDTO setSignedBySupervisor(TimeSheetDTO timeSheetDTO) {
        timeSheetDTO.setSignedBySupervisor(LocalDate.now());
        timeSheetDTO.setTimeSheetStatus(TimeSheetStatus.SIGNED_BY_SUPERVISOR);
        save(timeSheetDTO);

        reminderLogic.removeByTimeSheet(timeSheetDTO.getId());
        reminderLogic.createRemindersByTimeSheet(timeSheetDTO.getId());

        return timeSheetDTO;
    }

    @Override
    public TimeSheetDTO archive(TimeSheetDTO timeSheetDTO) {
        timeSheetDTO.setTimeSheetStatus(TimeSheetStatus.ARCHIVED);
        this.save(timeSheetDTO);
        ContractDTO contractDTO = contractMapper.toDTO(
                contractAccess.findOne(timeSheetDTO.getContract().getId()),
                ContractDTO.class);

        if (contractDTO.getTimeSheets()
                .stream()
                .allMatch(t -> t.getTimeSheetStatus() == TimeSheetStatus.ARCHIVED)) {
            contractDTO.setStatus(ContractStatus.ARCHIVED);
            contractAccess.save(contractMapper.toEntity(contractDTO, Contract.class));
        }

        reminderLogic.removeByTimeSheet(timeSheetDTO.getId());

        return timeSheetDTO;
    }

    @Override
    public boolean isActiveForEmployee(TimeSheetDTO timeSheetDTO) {
        return timeSheetDTO.getTimeSheetStatus() == TimeSheetStatus.IN_PROGESS
                && timeSheetDTO.getStartDate().isBefore(LocalDate.now().plusDays(1));
    }

    @Override
    public boolean isWaitingForEmployee(TimeSheetDTO timeSheetDTO) {
        return timeSheetDTO.getTimeSheetStatus() == TimeSheetStatus.SIGNED_BY_EMPLOYEE;
    }

    @Override
    public boolean isInactiveForEmployee(TimeSheetDTO timeSheetDTO) {
        return timeSheetDTO.getTimeSheetStatus() == TimeSheetStatus.SIGNED_BY_SUPERVISOR
                || timeSheetDTO.getTimeSheetStatus() == TimeSheetStatus.ARCHIVED;
    }

    @Override
    public boolean isActiveForSupervisor(TimeSheetDTO timeSheetDTO) {
        return timeSheetDTO.getTimeSheetStatus() == TimeSheetStatus.SIGNED_BY_EMPLOYEE;
    }

    @Override
    public boolean isActiveForSecretary(TimeSheetDTO timeSheetDTO) {
        return timeSheetDTO.getTimeSheetStatus() == TimeSheetStatus.SIGNED_BY_SUPERVISOR
                || timeSheetDTO.getTimeSheetStatus() == TimeSheetStatus.ARCHIVED;
    }

    @Override
    public boolean isArchived(TimeSheetDTO timeSheetDTO) {
        return timeSheetDTO.getTimeSheetStatus() == TimeSheetStatus.ARCHIVED;
    }

    @Override
    public void save(TimeSheetDTO timeSheetDTO) {
        List<WorkReport> workReports = workReportMapper.toEntity(timeSheetDTO.getWorkReports(), WorkReport.class);
        for (WorkReport workReport : workReports) {
            if (workReport.getId() != null) {
                workReportAccess.save(workReport);
            }
        }
        timeSheetAccess.save(timeSheetMapper.toEntity(timeSheetDTO, TimeSheet.class));
    }

    @Override
    public TimeSheetDTO find(long id) {
        return timeSheetMapper.toDTO(timeSheetAccess.findOne(id), TimeSheetDTO.class);
    }

    @Override
    public void removeWorkReport(WorkReportDTO workReportDTO) {
        workReportAccess.delete(workReportMapper.toEntity(workReportDTO, WorkReport.class));
    }

    @Override
    public List<ContractDTO> getRelatedContractsForPerson(PersonDTO personDTO) {
        Person person = personMapper.toEntity(personDTO, Person.class);
        List<Contract> relatedContracts = contractAccess.getContractsRelatedToPerson(person);
        return contractMapper.toDTO(relatedContracts, ContractDTO.class);
    }

    /**
     * Removes all {@link TimeSheet} of a {@link Contract}, which are past the given archiveDuration and
     * all {@link Contract}, of which all {@link TimeSheet} have been deleted
     */
    @Schedule(hour = "0")
    public void cleanUpTimesheets() {
        // Prepared contracts do not have Timesheets but should not be deleted and are therefore omitted
        List<Contract> contracts = contractAccess.findAll()
                .stream()
                .filter(c -> c.getStatus() != ContractStatus.PREPARED)
                .collect(Collectors.toList());

        for (Contract c : contracts) {

            for (TimeSheet t : c.getTimeSheets()) {

                if (t.getTimeSheetStatus() == TimeSheetStatus.SIGNED_BY_SUPERVISOR) {
                    if (LocalDate.now().isAfter(t.getSignedBySupervisor().plus(c.getArchiveDuration(), ChronoUnit.MONTHS))) {
                        timeSheetAccess.delete(t);
                    }
                }

            }

            if (c.getTimeSheets().isEmpty()) {
                contractAccess.delete(c);
            }
        }
    }

    @Override
    public List<LocalDate> getDisallowedDates(TimeSheetDTO timeSheetDTO) {
        HolidayManager holidayManager = HolidayManager.getInstance(ManagerParameters.create(HolidayCalendar.GERMANY));

        List<LocalDate> dates = holidayManager.getHolidays(timeSheetDTO.getStartDate(),
                timeSheetDTO.getEndDate(), timeSheetDTO.getContract().getFederalState().getAbbreviation())
                .stream()
                .map(Holiday::getDate).collect(Collectors.toList());

        return dates;
    }
}
