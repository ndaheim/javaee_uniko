package echo.logic.impl;

import echo.dao.ContractAccess;
import echo.dao.PersonAccess;
import echo.dao.TimeSheetAccess;
import echo.dto.ContractDTO;
import echo.dto.PersonDTO;
import echo.dto.enums.ContractStatus;
import echo.dto.enums.TimeSheetFrequency;
import echo.dto.enums.TimeSheetStatus;
import echo.entities.Contract;
import echo.entities.Person;
import echo.entities.TimeSheet;
import echo.logic.ContractManagementLogic;
import echo.mapper.ContractMapper;
import echo.mapper.PersonMapper;
import echo.models.RoleName;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Stateless
@LocalBean
public class ContractManagementLogicImpl implements ContractManagementLogic {

    @EJB
    private ContractAccess contractAccess;
    @EJB
    private PersonAccess personAccess;
    @EJB
    private TimeSheetAccess timeSheetAccess;

    private ContractMapper contractMapper = new ContractMapper();
    private PersonMapper personMapper = new PersonMapper();

    @Override
    public List<ContractDTO> getAllContracts() {
        return contractMapper.toDTO(contractAccess.findAll(), ContractDTO.class);
    }

    @Override
    public List<ContractDTO> getContractsByEmployee(PersonDTO employee) {
        List<Contract> contracts = contractAccess.getContractsByEmployee(personMapper.toEntity(employee, Person.class));
        return contractMapper.toDTO(contracts, ContractDTO.class);
    }

    @Override
    public List<ContractDTO> getContractsBySecretary(PersonDTO secretary) {
        List<Contract> contracts = contractAccess.getContractsBySecretary(personMapper.toEntity(secretary, Person.class));
        return contractMapper.toDTO(contracts, ContractDTO.class);
    }

    @Override
    public List<ContractDTO> getContractsByDelegate(PersonDTO delegate) {
        List<Contract> contracts = contractAccess.getContractsByDelegate(personMapper.toEntity(delegate, Person.class));
        return contractMapper.toDTO(contracts, ContractDTO.class);
    }

    @Override
    public List<ContractDTO> getContractsBySupervisor(PersonDTO supervisor) {
        List<Contract> contracts = contractAccess.getContractsBySupervisor(personMapper.toEntity(supervisor, Person.class));
        return contractMapper.toDTO(contracts, ContractDTO.class);
    }

    @Override
    public List<PersonDTO> findAllPersonsByRole(RoleName roleName) {
        return personMapper.toDTO(personAccess.findAllByRole(roleName), PersonDTO.class);
    }

    @Override
    public void delete(ContractDTO contract) {
        contractAccess.delete(contract.getId());
    }

    @Override
    public ContractDTO findContract(long id) {
        return contractMapper.toDTO(contractAccess.findOne(id), ContractDTO.class);
    }

    @Override
    public ContractDTO saveContract(ContractDTO contract) {
        Contract entity = contractMapper.toEntity(contract, Contract.class);

        entity.setEmployee(personAccess.findFirstByEmail(entity.getEmployee().getEmailAddress()));
        entity.setSupervisor(personAccess.findFirstByEmail(entity.getSupervisor().getEmailAddress()));
        entity.setDelegates(entity.getDelegates()
                .stream()
                .map(p -> personAccess.findFirstByEmail(p.getEmailAddress()))
                .collect(Collectors.toList()));
        entity.setSecretaries(entity.getSecretaries()
                .stream()
                .map(p -> personAccess.findFirstByEmail(p.getEmailAddress()))
                .collect(Collectors.toList()));

        Contract saved = contractAccess.save(entity);
        return contractMapper.toDTO(saved, ContractDTO.class);
    }

    @Override
    public ContractDTO start(ContractDTO contract) {
        Contract entity = contractAccess.findOne(contract.getId());

        createTimesheets(entity);
        entity.setStatus(ContractStatus.STARTED);

        Contract saved = contractAccess.save(entity);
        return contractMapper.toDTO(saved, ContractDTO.class);
    }

    @Override
    public ContractDTO terminate(ContractDTO contract) {
        Contract entity = contractAccess.findOne(contract.getId());

        List<TimeSheet> toRemove = entity.getTimeSheets().stream()
                .filter(t -> t.getTimeSheetStatus() == TimeSheetStatus.IN_PROGESS)
                .collect(Collectors.toList());
        toRemove.forEach(t -> t.setContract(null));
        entity.getTimeSheets().removeAll(toRemove);

        timeSheetAccess.delete(toRemove);

        entity.setTerminationDate(LocalDate.now());
        entity.setStatus(ContractStatus.TERMINATED);

        Contract saved = contractAccess.save(entity);
        return contractMapper.toDTO(saved, ContractDTO.class);
    }


    /**
     * Creates the {@link TimeSheet} for the contract depending on the {@link TimeSheetFrequency}
     *
     * @param contract
     */
    public void createTimesheets(Contract contract) {
        if (contract.getFrequency() == TimeSheetFrequency.WEEKLY) {
            createTimesheetsWeekly(contract);
        } else if (contract.getFrequency() == TimeSheetFrequency.MONTHLY) {
            createTimesheetsMonthly(contract);
        }
    }

    /**
     * Creates the {@link TimeSheet} for the contract for the contract for a weekly {@link TimeSheetFrequency}
     *
     * @param contract
     */
    private void createTimesheetsWeekly(Contract contract) {
        Long fullWeeksInContract = ChronoUnit.WEEKS.between(
                contract.getStartDate(),
                contract.getEndDate()
        );

        // The first timesheet does not necessarily contain a full week
        TimeSheet firstWeek = new TimeSheet();
        firstWeek.setTimeSheetStatus(TimeSheetStatus.IN_PROGESS);
        firstWeek.setStartDate(contract.getStartDate());
        firstWeek.setEndDate(contract.getStartDate().with(TemporalAdjusters.dayOfWeekInMonth(1, DayOfWeek.SUNDAY)));
        contract.getTimeSheets().add(firstWeek);
        firstWeek.setContract(contract);

        // Denotes the monday of the second week of the contract, as the start of the contract can be
        // an arbitrary day
        LocalDate firstMonday = contract.getStartDate()
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        // Denotes the sunday of the second week of the contract
        LocalDate firstSunday = contract.getStartDate()
                .with(TemporalAdjusters.dayOfWeekInMonth(1, DayOfWeek.SUNDAY))
                .plus(1, ChronoUnit.WEEKS);

        IntStream.range(0, fullWeeksInContract.intValue() - 1).forEachOrdered(n -> {
            TimeSheet t = new TimeSheet();
            t.setTimeSheetStatus(TimeSheetStatus.IN_PROGESS);
            t.setStartDate(firstMonday.plus(n, ChronoUnit.WEEKS));
            t.setEndDate(firstSunday.plus(n, ChronoUnit.WEEKS));
            contract.getTimeSheets().add(t);
            t.setContract(contract);
        });

        // the last week does not necessarily contain a full week
        if (contract.getTimeSheets()
                .stream()
                .noneMatch(t -> t.getEndDate().isEqual(contract.getEndDate()))) {
            TimeSheet lastWeek = new TimeSheet();
            lastWeek.setTimeSheetStatus(TimeSheetStatus.IN_PROGESS);
            lastWeek.setStartDate(contract.getStartDate()
                    .plus(fullWeeksInContract, ChronoUnit.WEEKS)
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
            lastWeek.setEndDate(contract.getEndDate());
            contract.getTimeSheets().add(lastWeek);
            lastWeek.setContract(contract);
        }
    }

    /**
     * Creates the {@link TimeSheet} for the contract for the contract for a monthly {@link TimeSheetFrequency}
     *
     * @param contract
     */
    private void createTimesheetsMonthly(Contract contract) {
        Long monthsInContract = ChronoUnit.MONTHS.between(
                contract.getStartDate(),
                contract.getEndDate()
        ) + 1;
        LocalDate start = contract.getStartDate();
        LocalDate lastOfMonth = start.withDayOfMonth(start.lengthOfMonth());
        IntStream.range(0, monthsInContract.intValue()).forEachOrdered(n -> {
            TimeSheet t = new TimeSheet();
            t.setTimeSheetStatus(TimeSheetStatus.IN_PROGESS);
            t.setStartDate(start.plus(n, ChronoUnit.MONTHS));
            t.setEndDate(lastOfMonth.plus(n, ChronoUnit.MONTHS).with(TemporalAdjusters.lastDayOfMonth()));
            contract.getTimeSheets().add(t);
            t.setContract(contract);
        });
    }

    public boolean isSecretaryInContract(ContractDTO contractDTO, PersonDTO personDTO) {
        return contractDTO.getSecretaries().stream().anyMatch(p -> p.equals(personDTO));
    }

    @Override
    public boolean isSupervisorInContract(ContractDTO contractDTO, PersonDTO personDTO) {
        return contractDTO.getSupervisor().equals(personDTO);
    }

    @Override
    public boolean isDelegateInContract(ContractDTO contractDTO, PersonDTO personDTO) {
        return contractDTO.getDelegates().stream().anyMatch(p -> p.equals(personDTO));
    }

    @Override
    public boolean existsByEmail(String emailAddress) {
        return personAccess.existsByEmail(emailAddress);
    }

    @Override
    public boolean hasPersonRole(String emailAddress, RoleName role) {
        Person person = personAccess.findFirstByEmail(emailAddress);
        return person != null && person.getRoles().stream().anyMatch(r -> r.getName().equals(role));
    }
}
