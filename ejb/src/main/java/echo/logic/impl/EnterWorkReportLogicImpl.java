package echo.logic.impl;

import echo.dao.ContractAccess;
import echo.dao.TimeSheetAccess;
import echo.dao.WorkReportAccess;
import echo.dto.ContractDTO;
import echo.dto.PersonDTO;
import echo.dto.TimeSheetDTO;
import echo.dto.WorkReportDTO;
import echo.dto.enums.ContractStatus;
import echo.entities.Person;
import echo.entities.WorkReport;
import echo.logic.EnterWorkReportLogic;
import echo.mapper.ContractMapper;
import echo.mapper.PersonMapper;
import echo.mapper.WorkReportMapper;
import echo.util.DateUtil;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
@LocalBean
public class EnterWorkReportLogicImpl implements EnterWorkReportLogic {
    @EJB
    private ContractAccess contractAccess;
    @EJB
    private WorkReportAccess workReportAccess;
    @EJB
    private TimeSheetAccess timeSheetAccess;

    private ContractMapper contractMapper = new ContractMapper();
    private PersonMapper personMapper = new PersonMapper();
    private WorkReportMapper workReportMapper = new WorkReportMapper();

    @Override
    public Map<ContractDTO, TimeSheetDTO> getContractsWithActiveTimeSheet(PersonDTO person) {
        HashMap<ContractDTO, TimeSheetDTO> contractMap = new HashMap<>();

        List<ContractDTO> activeContracts = getActiveContractsForPerson(person);

        for (ContractDTO activeContract : activeContracts) {
            getCurrentTimeSheetForUser(activeContract)
                    .ifPresent(timeSheet -> contractMap.put(activeContract, timeSheet));
        }

        return contractMap;
    }

    @Override
    public void saveWorkReport(ContractDTO contract, TimeSheetDTO timeSheet, WorkReportDTO workReport) {
        workReportAccess.save(workReportMapper.toEntity(workReport, WorkReport.class));
    }

    private Optional<TimeSheetDTO> getCurrentTimeSheetForUser(ContractDTO contract) {
        return contract.getTimeSheets()
                .stream()
                .filter(t -> DateUtil.isBetweenClosed(LocalDate.now(), t.getStartDate(), t.getEndDate()))
                .map(t -> timeSheetAccess.findOneEager(t.getId()))
                .findFirst();
    }

    private List<ContractDTO> getActiveContractsForPerson(PersonDTO personDTO) {
        List<ContractDTO> contracts;

        contracts = contractAccess.getContractsByEmployee(personMapper.toEntity(personDTO, Person.class))
                .stream()
                .filter(c -> c.getStatus() == ContractStatus.STARTED)
                .filter(c -> DateUtil.isBetweenClosed(LocalDate.now(), c.getStartDate(), c.getEndDate()))
                .map(c -> contractMapper.toDTO(c, ContractDTO.class))
                .collect(Collectors.toList());

        return contracts;
    }
}
