package echo.beans;

import echo.dto.ContractDTO;
import echo.dto.PersonDTO;
import echo.dto.enums.ContractStatus;
import echo.dto.enums.TimeSheetFrequency;
import echo.dto.enums.TimeSheetStatus;
import echo.logic.*;
import echo.models.FederalState;
import echo.models.PersonConfigIdentifier;
import echo.models.RoleName;
import echo.security.ContractWebSecurityHandler;
import echo.security.LoggedInWebSecurityHandler;
import echo.util.I18nUtil;
import echo.util.StringUtils;
import echo.validator.ContractValidator;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named
@ViewScoped
public class ContractManagementBean implements Serializable {

    @EJB
    private ContractManagementLogic contractManagementLogic;
    @EJB
    private PersonManagementLogic personManagementLogic;
    @EJB
    private SessionCacheLogic sessionCache;
    @EJB
    private LoggedInWebSecurityHandler loggedInWebSecurityHandler;
    @EJB
    private LoginLogic loginLogic;
    @EJB
    private TimeSheetLogic timeSheetLogic;
    @EJB
    private PersonConfigLogic personConfigLogic;

    private ContractDTO selectedContract;

    private String newDelegateChoice;
    private String newSecretaryChoice;

    private List<PersonDTO> allDelegates = new ArrayList<>();
    private List<PersonDTO> allSecretaries = new ArrayList<>();
    private List<PersonDTO> allSupervisor = new ArrayList<>();
    private List<ContractDTO> allContracts = new ArrayList<>();
    private String errorMessages;

    @PostConstruct
    private void init() {
        loggedInWebSecurityHandler.isPostConstructGranted("#isAuthenticated($null)");
        //Load persons for auto completion asynchronous
        new Thread(() -> {
            allDelegates = contractManagementLogic.findAllPersonsByRole(RoleName.DELEGATE);
            allSecretaries = contractManagementLogic.findAllPersonsByRole(RoleName.SECRETARY);
            allSupervisor = contractManagementLogic.findAllPersonsByRole(RoleName.SUPERVISOR);
        }).start();

        if (allContracts.isEmpty()) {
            createNew();
        } else {
            selectedContract = allContracts.get(0);
        }
    }

    /**
     * Returns a {@link List} of all {@link ContractDTO}
     *
     * @return a {@link List} of all {@link ContractDTO}
     */
    public List<ContractDTO> getAllContracts() {
        List<ContractDTO> relatedContracts = new ArrayList<>();
        Optional<PersonDTO> sessionPerson = sessionCache.getSessionPerson();

        if (sessionPerson.isPresent()) {
            if (sessionPerson.get().getRoles().stream().anyMatch(r -> r.getName().equals(RoleName.ADMINISTRATOR))) {
                relatedContracts = contractManagementLogic.getAllContracts();
            } else {
                relatedContracts = timeSheetLogic.getRelatedContractsForPerson(sessionPerson.get());
            }
        }

        return relatedContracts;
    }

    /**
     * Selects the given {@link ContractDTO}
     *
     * @param selectedContract
     */
    public void onSelect(ContractDTO selectedContract) {
        this.selectedContract = selectedContract;
        this.errorMessages = null;
    }

    /**
     * Returns all contracts, where the given Person is part Of
     */
    public List<ContractDTO> getAllInvolvedContracts(PersonDTO personDTO) {
        return Stream.of(getContractsByDelegate(personDTO),
                getContractsByEmployee(personDTO),
                getContractsBySecretary(personDTO),
                getContractsBySupervisor(personDTO))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns all contracts, where a given Person is the Employee
     *
     * @param employee The delegate, whose contracts shall be returned
     * @return a list of all {@link ContractDTO}, which the employee is assigned to
     */
    public List<ContractDTO> getContractsByEmployee(PersonDTO employee) {
        return contractManagementLogic.getContractsByEmployee(employee);
    }

    /**
     * Returns all contracts, where a given Person is a secretary
     *
     * @param secretary The delegate, whose contracts shall be returned
     * @return a list of all {@link ContractDTO}, which the secretary is assigned to
     */
    public List<ContractDTO> getContractsBySecretary(PersonDTO secretary) {
        return contractManagementLogic.getContractsBySecretary(secretary);
    }

    /**
     * Returns all contracts, where a given Person is a delegate
     *
     * @param delegate The delegate, whose contracts shall be returned
     * @return a list of all {@link ContractDTO}, which the delegate is assigned to
     */
    public List<ContractDTO> getContractsByDelegate(PersonDTO delegate) {
        return contractManagementLogic.getContractsByDelegate(delegate);
    }

    /**
     * Returns all contracts, where a given Person is supervisor
     *
     * @param supervisor The supervisor, whose contracts shall be returned
     * @return a list of all {@link ContractDTO}, which the suervisor is assigned to
     */
    public List<ContractDTO> getContractsBySupervisor(PersonDTO supervisor) {
        return contractManagementLogic.getContractsBySupervisor(supervisor);
    }

    /**
     * adds a delegate to the list of delegates chosen for the current contract
     */
    public void addDelegateChoice() {
        Optional<PersonDTO> maybeDelegate = personManagementLogic.getOneByEmail(newDelegateChoice);

        if (maybeDelegate.isPresent() &&
                maybeDelegate.get().getRoles().stream().anyMatch(r -> r.getName() == RoleName.DELEGATE)) {
            selectedContract.getDelegates().add(maybeDelegate.get());
        }

        newDelegateChoice = "";
    }

    /**
     * deletes a delegate from the list of delegates chosen for the current contract
     */
    public void deleteDelegateChoice(PersonDTO delegate) {
        selectedContract.getDelegates().remove(delegate);
    }

    /**
     * adds a secretary to the list of secretaries chosen for the current contract
     */
    public void addSecretaryChoice() {
        Optional<PersonDTO> maybeSecretary = personManagementLogic.getOneByEmail(newSecretaryChoice);

        if (maybeSecretary.isPresent() &&
                maybeSecretary.get().getRoles().stream().anyMatch(r -> r.getName() == RoleName.SECRETARY)) {
            selectedContract.getSecretaries().add(maybeSecretary.get());
        }

        newSecretaryChoice = "";
    }

    /**
     * deletes a secretary from the list of secretaries chosen for the current contract
     */
    public void deleteSecretaryChoice(PersonDTO secretary) {
        this.selectedContract.getSecretaries().remove(secretary);
    }

    /**
     * Indicates whether no {@link echo.entities.Contract is selected}
     *
     * @return
     */
    public boolean isNoContractSelected() {
        return selectedContract == null;
    }

    /**
     * Returns an array of all {@link ContractStatus}
     *
     * @return an array of all {@link ContractStatus}
     */
    public ContractStatus[] getAllContractStatus() {
        return ContractStatus.values();
    }

    /**
     * Returns an array of all {@link TimeSheetFrequency}
     *
     * @return an array of all {@link TimeSheetFrequency}
     */
    public TimeSheetFrequency[] getAllFrequencies() {
        return TimeSheetFrequency.values();
    }

    /**
     * Returns an array of all {@link FederalState}
     *
     * @return an array of all {@link FederalState}
     */
    public FederalState[] getAllFederalStates() {
        return FederalState.values();
    }

    public PersonManagementLogic getPersonManagementLogic() {
        return personManagementLogic;
    }

    public void setPersonManagementLogic(PersonManagementLogic personManagementLogic) {
        this.personManagementLogic = personManagementLogic;
    }

    public ContractDTO getSelectedContract() {
        return selectedContract;
    }

    public void setSelectedContract(ContractDTO selectedContract) {
        this.selectedContract = selectedContract;
    }

    public String getNewDelegateChoice() {
        return newDelegateChoice;
    }

    public void setNewDelegateChoice(String newDelegateChoice) {
        this.newDelegateChoice = newDelegateChoice;
    }

    public String getNewSecretaryChoice() {
        return newSecretaryChoice;
    }

    public void setNewSecretaryChoice(String newSecretaryChoice) {
        this.newSecretaryChoice = newSecretaryChoice;
    }

    /**
     * Indicates whether the selected {@link ContractDTO} is read-only
     *
     * @return true if the selected {@link ContractDTO} is read-only,
     * false otherwise
     */
    public boolean isContractReadOnly() {
        return this.selectedContract == null || selectedContract.getStatus() != ContractStatus.PREPARED;
    }

    /**
     * Indicates whether the selected {@link ContractDTO} can be terminated.
     * This is only the case, when it is in {@link ContractStatus#STARTED} and none of the TimeSheets are in
     * {@link TimeSheetStatus#IN_PROGESS} or {@link TimeSheetStatus#SIGNED_BY_SUPERVISOR}
     *
     * @return true if the selected {@link ContractDTO} can be terminated,
     * false otherwise
     */
    public boolean canContractTerminate() {
        return this.selectedContract != null && selectedContract.getStatus() == ContractStatus.STARTED
                && selectedContract.getTimeSheets().stream()
                .noneMatch(t -> t.getTimeSheetStatus() != TimeSheetStatus.IN_PROGESS
                        && t.getTimeSheetStatus() != TimeSheetStatus.SIGNED_BY_SUPERVISOR);
    }

    /**
     * Returns whether the currently logged-in {@link PersonDTO} can start the selected {@link ContractDTO}.
     *
     * @return true if the selected {@link ContractDTO} can be started,
     * false otherwise
     */
    public boolean canUserStartContract() {
        Optional<PersonDTO> sessionPerson = sessionCache.getSessionPerson();
        boolean canStartContract = false;

        if (sessionPerson.isPresent() && selectedContract != null && selectedContract.getId() != null) {
            PersonDTO personDTO = sessionPerson.get();

            canStartContract = !isContractReadOnly() &&
                    (contractManagementLogic.isSupervisorInContract(selectedContract, personDTO)
                            || contractManagementLogic.isDelegateInContract(selectedContract, personDTO));
        }

        return canStartContract;
    }

    private void clearForm() {
        selectedContract = new ContractDTO();
    }

    /**
     * Creates a new {@link ContractDTO} with standard settings
     */
    public void createNew() {
        clearForm();

        selectedContract.setStatus(ContractStatus.PREPARED);
        selectedContract.setFrequency(TimeSheetFrequency.WEEKLY);
        selectedContract.setFederalState(FederalState.RHINELAND_PALATINATE);
        selectedContract.setStartDate(LocalDate.now());
        selectedContract.setEndDate(LocalDate.now().plus(6, ChronoUnit.MONTHS));
        selectedContract.setHoursPerWeek(20);
        selectedContract.setArchiveDuration(24);
        selectedContract.setSupervisor(new PersonDTO());
        selectedContract.setDelegates(new ArrayList<>());
        selectedContract.setSecretaries(new ArrayList<>());
        selectedContract.setTimeSheets(new ArrayList<>());
        selectedContract.setEmployee(new PersonDTO());
    }

    /**
     * Saves the currently selected {@link ContractDTO}
     */
    public void save() {
        if (!isContractReadOnly()) {
            ContractValidator validator = ContractValidator.of(selectedContract, contractManagementLogic, loginLogic);

            if (validator.validate()) {
                errorMessages = null;
                selectedContract.setEndDate(selectedContract.getEndDate().with(TemporalAdjusters.lastDayOfMonth()));
                selectedContract = contractManagementLogic.saveContract(selectedContract);
            } else {
                errorMessages = validator.getFormattedErrorMessage();
            }
        }
    }

    /**
     * Deletes the currently selected {@link ContractDTO}
     */
    public void delete() {
        if (!isContractReadOnly()) {
            contractManagementLogic.delete(selectedContract);
        }
    }

    /**
     * Starts the currently selected {@link ContractDTO}
     */
    public void startContract() {
        if (!isContractReadOnly()) {
            this.selectedContract = contractManagementLogic.start(selectedContract);
        }
    }

    /**
     * Terminates the currently selected {@link ContractDTO}
     */
    public void terminateContract() {
        if (canContractTerminate()) {
            this.selectedContract = contractManagementLogic.terminate(selectedContract);
        }
    }

    /**
     * Returns all email addresses of the {@link PersonDTO} with {@link RoleName#DELEGATE} in a comma-separated String
     *
     * @return The email addresses of the {@link PersonDTO} with {@link RoleName#DELEGATE} in a comma-separated String
     */
    public String getAllDelegates() {
        return allDelegates.stream().map(PersonDTO::getEmailAddress).collect(Collectors.joining(","));
    }

    /**
     * Returns all email addresses of the {@link PersonDTO} with {@link RoleName#SECRETARY} in a comma-separated String
     *
     * @return The email addresses of the {@link PersonDTO} with {@link RoleName#SECRETARY} in a comma-separated String
     */
    public String getAllSecretaries() {
        return allSecretaries.stream().map(PersonDTO::getEmailAddress).collect(Collectors.joining(","));
    }

    /**
     * Returns all email addresses of the {@link PersonDTO} with {@link RoleName#SUPERVISOR} in a comma-separated String
     *
     * @return The email addresses of the {@link PersonDTO} with {@link RoleName#SUPERVISOR} in a comma-separated String
     */
    public String getAllSupervisor() {
        return allSupervisor.stream().map(PersonDTO::getEmailAddress).collect(Collectors.joining(","));
    }

    /**
     * Returns whether the save button is disabled or not
     *
     * @return true if the currently selected {@link ContractDTO} is read-only or none is selected,
     * false otherwise
     */
    public boolean isContractSaveButtonDisabled() {
        boolean canUpdateContract = false;
        Optional<PersonDTO> sessionPerson = sessionCache.getSessionPerson();

        if (this.selectedContract != null && sessionPerson.isPresent()) {

            // only uni employees should be able to create contracts
            if (this.selectedContract.getId() == null) {
                String config = personConfigLogic.getConfig(sessionPerson.get().getId(), PersonConfigIdentifier.UNIVERSITY_ROLES);
                if (config != null) {
                    canUpdateContract = config.contains("employee");
                }
            } else {
                // if a contract is selected only delegates and supervisor can make changes, CN1
                canUpdateContract = contractManagementLogic.isDelegateInContract(this.selectedContract, sessionPerson.get())
                        || contractManagementLogic.isSupervisorInContract(this.selectedContract, sessionPerson.get());
            }
        }
        return false; //(isContractReadOnly() && selectedContract.getId() != null) || !canUpdateContract;
    }


    public String getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(String errorMessages) {
        this.errorMessages = errorMessages;
    }

    public boolean showErrorMessages() {
        return StringUtils.isNotEmpty(errorMessages);
    }

    /**
     * Returns the Message, which should be contained in the confirmation model for the termination of
     * the currently selected {@link ContractDTO}. In case TimeSheets are in {@link TimeSheetStatus#IN_PROGESS}
     * a warning is displayed.
     *
     * @return A Message for the confirmation modal for the termination of the currently selected {@link ContractDTO}.
     * In case TimeSheets are in {@link TimeSheetStatus#IN_PROGESS}, a warning is added.
     */
    public String getTerminationContractMessage() {
        String warnMsg = I18nUtil.getString("contract.termination.modal.description");

        boolean anyInProgress = selectedContract
                .getTimeSheets()
                .stream()
                .anyMatch(f -> f.getTimeSheetStatus() == TimeSheetStatus.IN_PROGESS);

        if (anyInProgress) {
            warnMsg = I18nUtil.getString("contract.termination.modal.description.opentimesheet") +
                    "</br>" + warnMsg;
        }

        return warnMsg;
    }
}