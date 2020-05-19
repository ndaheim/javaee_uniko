package echo.logic;

import echo.dto.ContractDTO;
import echo.dto.PersonDTO;
import echo.dto.TimeSheetDTO;
import echo.dto.WorkReportDTO;
import echo.dto.enums.ContractStatus;
import echo.dto.enums.TimeSheetStatus;

import javax.ejb.Local;
import java.time.LocalDate;
import java.util.List;

@Local
public interface TimeSheetLogic {

    /**
     * Sets the {@link TimeSheetStatus} of the {@link TimeSheetDTO} to
     * {@link TimeSheetStatus#SIGNED_BY_EMPLOYEE}
     *
     * @param timeSheetDTO
     * @return The updated {@link TimeSheetDTO}
     */
    TimeSheetDTO setSignedByEmployee(TimeSheetDTO timeSheetDTO);

    /**
     * Revokes the signature of the employee and sets the {@link TimeSheetStatus}
     * of the {@link TimeSheetDTO} to {@link TimeSheetStatus#IN_PROGESS}
     *
     * @param timeSheetDTO
     * @return The updated {@link TimeSheetDTO}
     */
    TimeSheetDTO removeSignedByEmployee(TimeSheetDTO timeSheetDTO);

    /**
     * Sets the {@link TimeSheetStatus} of the {@link TimeSheetDTO} to
     * {@link TimeSheetStatus#SIGNED_BY_SUPERVISOR}
     *
     * @param timeSheetDTO
     * @return The updated {@link TimeSheetDTO}
     */
    TimeSheetDTO setSignedBySupervisor(TimeSheetDTO timeSheetDTO);

    /**
     * Archives the {@link TimeSheetDTO} and sets the {@link TimeSheetStatus}
     * to {@link TimeSheetStatus#ARCHIVED}.
     * Furthermore the status of the TimeSheets {@link echo.entities.Contract} is set to
     * {@link ContractStatus#ARCHIVED} if all TimeSheets are
     * {@link TimeSheetStatus#ARCHIVED}.
     *
     * @param timeSheetDTO
     * @return The updated {@link TimeSheetDTO}
     */
    TimeSheetDTO archive(TimeSheetDTO timeSheetDTO);

    /**
     * Returns, whether the given {@link TimeSheetDTO} is active for its employee.
     *
     * @param timeSheetDTO
     * @return
     */
    boolean isActiveForEmployee(TimeSheetDTO timeSheetDTO);

    /**
     * Returns, whether the given {@link TimeSheetDTO} is currently awaiting a signature from a supervisor.
     *
     * @param timeSheetDTO
     * @return
     */
    boolean isWaitingForEmployee(TimeSheetDTO timeSheetDTO);

    /**
     * Returns, whether the given {@link TimeSheetDTO} is inactive for its employee, i.e.
     * its status is {@link TimeSheetStatus#ARCHIVED}.
     *
     * @param timeSheetDTO
     * @return
     */
    boolean isInactiveForEmployee(TimeSheetDTO timeSheetDTO);

    /**
     * Returns, whether the given {@link TimeSheetDTO} is active for its supervisor, i.e.
     * its status is {@link TimeSheetStatus#SIGNED_BY_EMPLOYEE}
     *
     * @param timeSheetDTO
     * @return
     */
    boolean isActiveForSupervisor(TimeSheetDTO timeSheetDTO);

    /**
     * Returns, whether the given {@link TimeSheetDTO} is active for its secretary, i.e.
     * its status is {@link TimeSheetStatus#SIGNED_BY_SUPERVISOR}
     *
     * @param timeSheetDTO
     * @return
     */
    boolean isActiveForSecretary(TimeSheetDTO timeSheetDTO);

    /**
     * Returns, whether the given {@link TimeSheetDTO} is {@link TimeSheetStatus#ARCHIVED}
     *
     * @param timeSheetDTO
     * @return
     */
    boolean isArchived(TimeSheetDTO timeSheetDTO);

    /**
     * Creates a new {@link WorkReportDTO}
     *
     * @return a new {@link WorkReportDTO}
     */
    WorkReportDTO createWorkReportDTO();

    /**
     * Saves a {@link TimeSheetDTO} and also its {@link WorkReportDTO}
     *
     * @param timeSheetDTO
     */
    void save(TimeSheetDTO timeSheetDTO);

    /**
     * Finds a {@link TimeSheetDTO} by id.
     *
     * @param id The id of the {@link TimeSheetDTO}
     * @return The {@link TimeSheetDTO}
     */
    TimeSheetDTO find(long id);

    /**
     * Removes a {@link WorkReportDTO}
     *
     * @param workReportDTO
     */
    void removeWorkReport(WorkReportDTO workReportDTO);

    /**
     * Returns all {@link ContractDTO} related to the given {@link PersonDTO}
     *
     * @param personDTO
     * @return a {@link List} of all {@link ContractDTO} related to the given {@link PersonDTO}
     */
    List<ContractDTO> getRelatedContractsForPerson(PersonDTO personDTO);

    /**
     * Returns all dates within a {@link TimeSheetDTO}, for which no {@link WorkReportDTO} can be entried.
     *
     * @param timeSheetDTO
     * @return a {@link List} of all dates within the {@link TimeSheetDTO},
     * for which no {@link WorkReportDTO} can be entried.
     */
    List<LocalDate> getDisallowedDates(TimeSheetDTO timeSheetDTO);
}
