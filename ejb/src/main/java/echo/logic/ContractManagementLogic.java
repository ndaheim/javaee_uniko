package echo.logic;

import echo.dto.ContractDTO;
import echo.dto.PersonDTO;
import echo.models.RoleName;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ContractManagementLogic {

    /**
     * Returns all contracts
     *
     * @return a list of all {@link ContractDTO}
     */
    List<ContractDTO> getAllContracts();

    /**
     * Returns all contracts of a given employee
     *
     * @param employee
     * @return a list of all {@link ContractDTO} of the employee
     */
    List<ContractDTO> getContractsByEmployee(PersonDTO employee);

    /**
     * Returns all contracts, where the given {@link PersonDTO} is secretary
     *
     * @param secretary
     * @return a list of all {@link ContractDTO} of the secretary
     */
    List<ContractDTO> getContractsBySecretary(PersonDTO secretary);

    /**
     * Returns all contracts, where the given {@link PersonDTO} is delegate
     *
     * @param delegate
     * @return a list of all {@link ContractDTO} of the delegate
     */
    List<ContractDTO> getContractsByDelegate(PersonDTO delegate);

    /**
     * Returns all contracts, where the given {@link PersonDTO} is supervisor
     *
     * @param supervisor
     * @return a list of all {@link ContractDTO} of the delegate
     */
    List<ContractDTO> getContractsBySupervisor(PersonDTO supervisor);

    /**
     * A list of all persons with the role Delegate
     *
     * @param delegate
     * @return
     */
    List<PersonDTO> findAllPersonsByRole(RoleName delegate);

    /**
     * Deletes the contract from the database
     *
     * @param contract
     */
    void delete(ContractDTO contract);

    /**
     * Finds a contract by the id
     *
     * @param id
     * @return
     */
    ContractDTO findContract(long id);

    /**
     * Saves a contract
     *
     * @param contract
     * @return
     */
    ContractDTO saveContract(ContractDTO contract);

    /**
     * Starts a contract and creates all timesheets
     *
     * @param contract
     */
    ContractDTO start(ContractDTO contract);

    /**
     * Terminates a contract and removes all unsigned timesheets
     *
     * @param contract
     * @return
     */
    ContractDTO terminate(ContractDTO contract);

    /**
     * Returns whether a {@link PersonDTO} has the {@link RoleName} Secretary in the {@link ContractDTO}
     *
     * @param contractDTO
     * @param personDTO
     * @return
     */
    boolean isSecretaryInContract(ContractDTO contractDTO, PersonDTO personDTO);

    /**
     * Returns whether a {@link PersonDTO} has the {@link RoleName} Supervisor in the {@link ContractDTO}
     *
     * @param contractDTO
     * @param personDTO
     * @return
     */
    boolean isSupervisorInContract(ContractDTO contractDTO, PersonDTO personDTO);

    /**
     * Returns whether a {@link PersonDTO} has the {@link RoleName} Delegate in the {@link ContractDTO}
     *
     * @param contractDTO
     * @param personDTO
     * @return
     */
    boolean isDelegateInContract(ContractDTO contractDTO, PersonDTO personDTO);

    /**
     * Checks if a {@link echo.entities.Person} with the given emailAddress exists.
     *
     * @param emailAddress
     * @return true if a person was found, otherwise false.
     */
    boolean existsByEmail(String emailAddress);

    /**
     * Validates if the {@link echo.entities.Person} of the given email has the defined {@link RoleName}
     *
     * @param emailAddress
     * @param role
     * @return true if the {@link echo.entities.Person} has the {@link RoleName}, otherwise false.
     */
    boolean hasPersonRole(String emailAddress, RoleName role);
}
