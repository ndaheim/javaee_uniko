package echo.mapper;

import echo.dto.ContractDTO;
import echo.dto.TimeSheetDTO;
import echo.dto.WorkReportDTO;
import echo.entities.Contract;
import echo.entities.TimeSheet;
import echo.entities.WorkReport;

import java.util.List;
import java.util.stream.Collectors;

public class TimeSheetMapper extends SimpleMapper<TimeSheet, TimeSheetDTO> {

    private SimpleMapper<WorkReport, WorkReportDTO> workReportMapper = new SimpleMapper<>();
    private SimpleMapper<Contract, ContractDTO> contractMapper = new SimpleMapper<>();

    @Override
    public TimeSheet toEntity(TimeSheetDTO dto, Class<TimeSheet> clazz) {
        TimeSheet timeSheet = super.toEntity(dto, clazz);
        List<WorkReport> workReports = workReportMapper.toEntity(dto.getWorkReports(), WorkReport.class);
        workReports.forEach(wr -> wr.setTimeSheet(timeSheet));
        timeSheet.setWorkReports(workReports);
        timeSheet.setContract(contractMapper.toEntity(dto.getContract(), Contract.class));
        return timeSheet;
    }

    @Override
    public TimeSheetDTO toDTO(TimeSheet entity, Class<TimeSheetDTO> clazz) {
        TimeSheetDTO timeSheetDTO = super.toDTO(entity, clazz);
        List<WorkReportDTO> workReportDTOs = workReportMapper.toDTO(entity.getWorkReports(), WorkReportDTO.class);
        workReportDTOs.forEach(wr -> wr.setTimeSheet(timeSheetDTO));
        timeSheetDTO.setWorkReports(workReportDTOs);
        timeSheetDTO.setContract(contractMapper.toDTO(entity.getContract(), ContractDTO.class));
        return timeSheetDTO;
    }

    @Override
    public List<TimeSheet> toEntity(List<TimeSheetDTO> dtoList, Class<TimeSheet> clazz) {
        return dtoList.stream().map(d -> toEntity(d, clazz)).collect(Collectors.toList());
    }

    @Override
    public List<TimeSheetDTO> toDTO(List<TimeSheet> entityList, Class<TimeSheetDTO> clazz) {
        return entityList.stream().map(e -> toDTO(e, clazz)).collect(Collectors.toList());
    }

}
