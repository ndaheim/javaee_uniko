package echo.mapper;

import echo.dto.ContractDTO;
import echo.dto.PersonDTO;
import echo.dto.TimeSheetDTO;
import echo.entities.Contract;
import echo.entities.Person;
import echo.entities.TimeSheet;

import java.util.List;
import java.util.stream.Collectors;

public class ContractMapper extends SimpleMapper<Contract, ContractDTO> {

    private PersonMapper personMapper = new PersonMapper();
    private TimeSheetMapper timeSheetMapper = new TimeSheetMapper();

    @Override
    public Contract toEntity(ContractDTO dto, Class<Contract> clazz) {
        Contract contract = super.toEntity(dto, clazz);
        contract.setDelegates(personMapper.toEntity(dto.getDelegates(), Person.class));
        contract.setEmployee(personMapper.toEntity(dto.getEmployee(), Person.class));
        contract.setSecretaries(personMapper.toEntity(dto.getSecretaries(), Person.class));
        contract.setSupervisor(personMapper.toEntity(dto.getSupervisor(), Person.class));
        contract.setTimeSheets(timeSheetMapper.toEntity(dto.getTimeSheets(), TimeSheet.class));
        return contract;
    }

    @Override
    public ContractDTO toDTO(Contract entity, Class<ContractDTO> clazz) {
        ContractDTO contractDTO = super.toDTO(entity, clazz);
        contractDTO.setDelegates(personMapper.toDTO(entity.getDelegates(), PersonDTO.class));
        contractDTO.setEmployee(personMapper.toDTO(entity.getEmployee(), PersonDTO.class));
        contractDTO.setSecretaries(personMapper.toDTO(entity.getSecretaries(), PersonDTO.class));
        contractDTO.setSupervisor(personMapper.toDTO(entity.getSupervisor(), PersonDTO.class));
        contractDTO.setTimeSheets(timeSheetMapper.toDTO(entity.getTimeSheets(), TimeSheetDTO.class));
        return contractDTO;
    }

    @Override
    public List<Contract> toEntity(List<ContractDTO> dtoList, Class<Contract> clazz) {
        return dtoList.stream().map(d -> toEntity(d, clazz)).collect(Collectors.toList());
    }

    @Override
    public List<ContractDTO> toDTO(List<Contract> entityList, Class<ContractDTO> clazz) {
        return entityList.stream().map(e -> toDTO(e, clazz)).collect(Collectors.toList());
    }

}
