package echo.mapper;

import echo.dto.TimeSheetDTO;
import echo.dto.WorkReportDTO;
import echo.entities.TimeSheet;
import echo.entities.WorkReport;

import java.util.List;
import java.util.stream.Collectors;

public class WorkReportMapper extends SimpleMapper<WorkReport, WorkReportDTO> {

    private SimpleMapper<TimeSheet, TimeSheetDTO> timeSheetMapper = new SimpleMapper<>();

    @Override
    public WorkReport toEntity(WorkReportDTO dto, Class<WorkReport> clazz) {
        WorkReport workReport = super.toEntity(dto, clazz);
        workReport.setTimeSheet(timeSheetMapper.toEntity(dto.getTimeSheet(), TimeSheet.class));
        return workReport;
    }

    @Override
    public WorkReportDTO toDTO(WorkReport entity, Class<WorkReportDTO> clazz) {
        WorkReportDTO workReportDTO = super.toDTO(entity, clazz);
        workReportDTO.setTimeSheet(timeSheetMapper.toDTO(entity.getTimeSheet(), TimeSheetDTO.class));
        return workReportDTO;
    }

    @Override
    public List<WorkReport> toEntity(List<WorkReportDTO> dtoList, Class<WorkReport> clazz) {
        return dtoList.stream().map(d -> toEntity(d, clazz)).collect(Collectors.toList());
    }

    @Override
    public List<WorkReportDTO> toDTO(List<WorkReport> entityList, Class<WorkReportDTO> clazz) {
        return entityList.stream().map(e -> toDTO(e, clazz)).collect(Collectors.toList());
    }

}
