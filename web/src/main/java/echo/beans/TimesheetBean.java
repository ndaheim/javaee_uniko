package echo.beans;

import echo.dto.ContractDTO;
import echo.dto.PersonDTO;
import echo.dto.TimeSheetDTO;
import echo.dto.WorkReportDTO;
import echo.dto.enums.ReportType;
import echo.dto.enums.TimeSheetStatus;
import echo.logic.ContractManagementLogic;
import echo.logic.PersonConfigLogic;
import echo.logic.SessionCacheLogic;
import echo.logic.TimeSheetLogic;
import echo.security.ContractWebSecurityHandler;
import echo.security.LoggedInWebSecurityHandler;
import echo.validator.TimeSheetValidator;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ManagedBean
@ViewScoped
public class TimesheetBean implements Serializable {

    @EJB
    private TimeSheetLogic timeSheetLogic;
    @EJB
    private ContractManagementLogic contractManagementLogic;
    @EJB
    private SessionCacheLogic sessionCache;
    @EJB
    private PersonConfigLogic personConfigLogic;
    @EJB
    private ContractWebSecurityHandler contractWebSecurityHandler;
    @EJB
    private LoggedInWebSecurityHandler loggedInWebSecurityHandler;

    private long contractId;
    private List<WorkReportDTO> toRemoveList = new ArrayList<>();
    private ContractDTO selectedContract;
    private PersonDTO sessionPerson;
    private Map<TimeSheetDTO, String> errorMap = new HashMap<TimeSheetDTO, String>();

    @PostConstruct
    public void init() {
        loggedInWebSecurityHandler.isPostConstructGranted("#isAuthenticated($null)");
        sessionCache.getSessionPerson().ifPresent(personDTO -> sessionPerson = personDTO);
    }

    public void onSelect(ContractDTO selectedContract) {
        contractWebSecurityHandler.isGranted("#hasRoleInContract($0)", selectedContract);
        this.selectedContract = selectedContract;
    }

    /**
     * Returns a {@link List} of all {@link ContractDTO}, which should be shown to the currently logged-in
     * {@link PersonDTO}.
     *
     * @return A {@link List} of all {@link ContractDTO} to be shown to the User
     */
    public List<ContractDTO> getAllContracts() {
        List<ContractDTO> relatedContracts = new ArrayList<>();

        if (sessionPerson != null) {
            relatedContracts = timeSheetLogic.getRelatedContractsForPerson(sessionPerson);
        }

        return relatedContracts;
    }

    /**
     * Returns selected contract
     *
     * @return selected contract
     */
    public ContractDTO getContract() {
        return selectedContract;
    }

    /**
     * Sets selected contract
     *
     * @param contract
     */
    public void setContract(ContractDTO contract) {
        this.selectedContract = contract;
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    /**
     * Adds an empty entry to a given timesheet
     *
     * @param index Index of the timesheet in contract
     */
    public void addEntry(int index) {
        WorkReportDTO wr = timeSheetLogic.createWorkReportDTO();
        wr.setTimeSheet(selectedContract.getTimeSheets().get(index));
        selectedContract.getTimeSheets().get(index).getWorkReports().add(wr);
    }

    /**
     * Removes given workreport from given timesheet
     *
     * @param tsIndex Timesheet index
     * @param wrIndex Workreport index
     */
    public void removeEntry(int tsIndex, int wrIndex) {
        WorkReportDTO toRemove = this.selectedContract.getTimeSheets().get(tsIndex).getWorkReports().remove(wrIndex);
        toRemoveList.add(toRemove);
    }

    /**
     * Checks if given timesheet is active for employees
     */
    public boolean isActiveForEmployee(TimeSheetDTO timeSheetDTO) {
        return timeSheetLogic.isActiveForEmployee(timeSheetDTO);
    }

    /**
     * Checks if given timesheet is signed by employee but not supervisor
     *
     * @param index Timesheet index
     * @return true if not signed, false otherwise
     */
    public boolean isWaitingForEmployee(int index) {
        TimeSheetDTO ts = this.selectedContract.getTimeSheets().get(index);
        return timeSheetLogic.isWaitingForEmployee(ts);
    }

    /**
     * Checks if given timesheet is signed by both employee and supervisor
     *
     * @param index Timesheet index
     * @return true if signed by both
     */
    public boolean isInactiveForEmployee(int index) {
        TimeSheetDTO ts = this.selectedContract.getTimeSheets().get(index);
        return timeSheetLogic.isInactiveForEmployee(ts);
    }

    /**
     * Saves given timesheet. repopulates sheets with updated workreports
     *
     * @param index the index of the {@link TimeSheetDTO}
     */
    public void save(int index) {
        contractWebSecurityHandler.isGranted("#isEmployeeInContract($0)", selectedContract);
        TimeSheetDTO toSave = selectedContract.getTimeSheets().get(index);
        Iterator<WorkReportDTO> iter = toRemoveList.iterator();

        TimeSheetValidator timeSheetValidator = TimeSheetValidator.of(toSave, contractManagementLogic);

        boolean valid = timeSheetValidator.validate();

        if (valid) {
            while (iter.hasNext()) {
                WorkReportDTO toRemove = iter.next();

                if (toRemove.getTimeSheet().equals(toSave)) {
                    timeSheetLogic.removeWorkReport(toRemove);
                    iter.remove();
                }
            }
            timeSheetLogic.save(toSave);
            selectedContract.getTimeSheets().set(index, timeSheetLogic.find(toSave.getId()));
            errorMap.put(toSave, "");
        } else {
            errorMap.put(toSave, timeSheetValidator.getFormattedErrorMessage());
        }
    }

    /**
     * Signs given timesheet
     *
     * @param index the index of the {@link TimeSheetDTO}
     * @return
     */
    public String sign(int index) {
        contractWebSecurityHandler.isGranted("#isEmployeeInContract($0)", selectedContract);
        TimeSheetDTO timeSheetDTO = selectedContract.getTimeSheets().get(index);
        timeSheetDTO = timeSheetLogic.setSignedByEmployee(timeSheetDTO);
        selectedContract.getTimeSheets().set(index, timeSheetDTO);
        this.save(index);
        return "";
    }

    /**
     * Revokes signature on given timesheet
     *
     * @param index the index of the {@link TimeSheetDTO}
     * @return
     */
    public String revokeSignature(int index) {
        contractWebSecurityHandler.isGranted("#isEmployeeInContract($0) || #isSupervisorInContract($0) || #isDelegateInContract($0)", selectedContract);
        TimeSheetDTO timeSheetDTO = selectedContract.getTimeSheets().get(index);
        timeSheetDTO = timeSheetLogic.removeSignedByEmployee(timeSheetDTO);
        selectedContract.getTimeSheets().set(index, timeSheetDTO);
        this.save(index);
        return "";
    }

    /**
     * Returns a formatted String, which contains a friendly title of the selected {@link TimeSheetDTO}
     *
     * @param timeSheetDTO
     * @return              A friendly title of the selected {@link TimeSheetDTO}
     */
    public String toFriendlyTitle(TimeSheetDTO timeSheetDTO) {
        return String.format("%s - %s",
                toFriendlyDate(timeSheetDTO.getStartDate()),
                toFriendlyDate(timeSheetDTO.getEndDate()));
    }

    /**
     * Returns an array of all {@link ReportType}
     *
     * @return an array of all {@link ReportType}
     */
    public ReportType[] getReportTypes() {
        return ReportType.values();
    }

    public void archive(TimeSheetDTO timeSheetDTO) {
        contractWebSecurityHandler.isGranted("#isSecretaryInContract($0)", selectedContract);
        timeSheetLogic.archive(timeSheetDTO);
    }

    /**
     * Returns whether the {@link TimeSheetDTO} can be archived by the currently logged in {@link PersonDTO}.
     * This is the case if the {@link PersonDTO} has {@link echo.models.RoleName#SECRETARY} and the {@link TimeSheetDTO}
     * is in {@link TimeSheetStatus#SIGNED_BY_SUPERVISOR}
     *
     * @param index The index of the {@link TimeSheetDTO}
     * @return      true if the {@link TimeSheetDTO} can be archived,
     *              false else
     */
    public boolean canBeArchived(int index) {
        TimeSheetDTO timeSheetDTO = selectedContract.getTimeSheets().get(index);
        Optional<PersonDTO> sessionPerson = sessionCache.getSessionPerson();

        boolean canBeArchived = false;

        if (sessionPerson.isPresent()) {
            canBeArchived = timeSheetDTO.getTimeSheetStatus() == TimeSheetStatus.SIGNED_BY_SUPERVISOR &&
                    contractManagementLogic.isSecretaryInContract(timeSheetDTO.getContract(), sessionPerson.get());
        }

        return canBeArchived;
    }

    /**
     * Returns whether the currently logged in {@link PersonDTO} is {@link echo.models.RoleName#SECRETARY}
     * in the selected {@link ContractDTO}
     *
     * @return
     */
    public boolean isSecretary() {
        return contractManagementLogic.isSecretaryInContract(selectedContract, sessionPerson);
    }

    /**
     * Returns whether the currently logged in {@link PersonDTO} is {@link echo.models.RoleName#SUPERVISOR}
     * in the selected {@link ContractDTO}
     *
     * @return
     */
    public boolean isSupervisor() {
        return contractManagementLogic.isSupervisorInContract(selectedContract, sessionPerson);
    }

    /**
     * Returns whether the currently logged in {@link PersonDTO} is {@link echo.models.RoleName#DELEGATE}
     * in the selected {@link ContractDTO}
     *
     * @return
     */
    public boolean isDelegate() {
        return contractManagementLogic.isDelegateInContract(selectedContract, sessionPerson);
    }

    /**
     * Returns whether the given {@link TimeSheetDTO} is active for its {@link echo.models.RoleName#SUPERVISOR}
     *
     * @param timeSheetDTO
     * @return
     */
    public boolean isActiveForSupervisor(TimeSheetDTO timeSheetDTO) {
        return timeSheetLogic.isActiveForSupervisor(timeSheetDTO);
    }

    /**
     * Returns whether the given {@link TimeSheetDTO} is active for its {@link echo.models.RoleName#SECRETARY}
     *
     * @param timeSheetDTO
     * @return
     */
    public boolean isActiveForSecretary(TimeSheetDTO timeSheetDTO) {
        return timeSheetLogic.isActiveForSecretary(timeSheetDTO);
    }

    /**
     * Returns whether the given {@link TimeSheetDTO} is in {@link TimeSheetStatus#ARCHIVED}
     *
     * @param timeSheetDTO
     * @return
     */
    public boolean isArchived(TimeSheetDTO timeSheetDTO) {
        return timeSheetLogic.isArchived(timeSheetDTO);
    }

    /**
     * Signs the {@link TimeSheetDTO} by the {@link echo.models.RoleName#SUPERVISOR}, i.e. sets it to
     * {@link TimeSheetStatus#SIGNED_BY_SUPERVISOR} and sets the {@link TimeSheetDTO#signedBySupervisor}
     *
     * @param index The index of the {@link TimeSheetDTO}, which should be signed
     */
    public void signSupervisor(int index) {
        contractWebSecurityHandler.isGranted("#isSupervisorInContract($0)", selectedContract);
        TimeSheetDTO timeSheetDTO = selectedContract.getTimeSheets().get(index);
        timeSheetDTO = timeSheetLogic.setSignedBySupervisor(timeSheetDTO);
        selectedContract.getTimeSheets().set(index, timeSheetDTO);
        save(index);
    }

    /**
     * Returns a String representation of the given {@link LocalDate} in the format of the current {@link Locale}
     *
     * @param localDate
     * @return          A String representation of the given {@link LocalDate} in format
     *                  dd.MM.yy for {@link Locale#GERMAN}
     *                  MM/dd/yyyy for {@link Locale#ENGLISH}
     */
    public String toFriendlyDate(LocalDate localDate) {
        String friendlyDate = "";

        if (localDate != null && sessionPerson != null && personConfigLogic.getSessionPersonLocale().equals(Locale.GERMAN)) {
            friendlyDate = localDate.format(DateTimeFormatter.ofPattern("dd.MM.yy"));
        } else if (localDate != null) {
            friendlyDate = localDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        }

        return friendlyDate;
    }

    /**
     * Returns whether the TimeSheetManagement page shall be rendered or not for the currently selected {@link ContractDTO}.
     *
     * @return "true" if the currently logged-in {@link PersonDTO} is {@link echo.models.RoleName#EMPLOYEE} in the selected
     *         {@link ContractDTO},
     *         "false" otherwise
     */
    public String renderTimesheetManagement() {
        boolean show = false;

        if (selectedContract != null && selectedContract.getEmployee().equals(sessionPerson)) {
            show = true;
        }

        return String.valueOf(show);
    }

    /**
     * Returns whether the TimeSheetAdministration page shall be rendered or not for the currently selected {@link ContractDTO}.
     *
     * @return "true" if the currently logged-in {@link PersonDTO} is {@link echo.models.RoleName#SECRETARY} or
     *         {@link echo.models.RoleName#SUPERVISOR} in the selected {@link ContractDTO},
     *         "false" otherwise
     */
    public boolean renderTimesheetAdministration() {
        boolean show = false;

        if (selectedContract != null) {
            boolean isSecretary = selectedContract.getSecretaries().contains(sessionPerson);
            boolean isSupervisor = selectedContract.getSupervisor().equals(sessionPerson);

            if (isSecretary || isSupervisor) {
                show = true;
            }
        }

        return show;
    }

    /**
     * Returns whether a {@link ContractDTO} is currently selected or not
     * @return
     */
    public boolean isContractSelected() {
        return selectedContract != null;
    }

    /**
     * Returns whether the given {@link TimeSheetDTO} is visible in the waiting section of the TimeSheetAdministration
     * page
     *
     * @param timeSheetDTO
     * @return true if the currently logged-in {@link PersonDTO} is {@link echo.models.RoleName#SUPERVISOR} or
     *         {@link echo.models.RoleName#SECRETARY}, the {@link TimeSheetDTO} is active and not
     *         {@link TimeSheetStatus#ARCHIVED}
     */
    public boolean isAdminWaitingTimeSheetVisible(TimeSheetDTO timeSheetDTO) {
        return ((isSupervisor() && isActiveForSupervisor(timeSheetDTO)) ||
                (isSecretary() && isActiveForSecretary(timeSheetDTO))) &&
                !isArchived(timeSheetDTO);
    }

    /**
     * Returns the error messages for the underlying {@link TimeSheetDTO}
     *
     * @param index The index of the {@link TimeSheetDTO}, for which the error messages shall be returned
     * @return      A String of the error messages for the underlying {@link TimeSheetDTO}
     */
    public String getErrorMessages(int index) {
        TimeSheetDTO timeSheetDTO = selectedContract.getTimeSheets().get(index);
        String value = errorMap.get(timeSheetDTO);
        return value;
    }

    /**
     * Returns whether there are error messages to be shown.
     *
     * @param index The index of the {@link TimeSheetDTO}, for which the error messages shall be returned
     * @return      true if error messages exist,
     *              false otherwise
     */
    public boolean showErrorMessages(int index) {
        TimeSheetDTO timeSheetDTO = selectedContract.getTimeSheets().get(index);
        String value = errorMap.get(timeSheetDTO);
        return value != null && !value.isEmpty();
    }
}
