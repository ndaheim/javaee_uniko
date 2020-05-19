package echo.models;

import echo.dto.ContractDTO;
import echo.dto.TimeSheetDTO;
import echo.dto.WorkReportDTO;
import echo.dto.enums.ReportType;

import java.util.Map;

public class WorkLogWrapper {
    private ContractDTO contract;
    private TimeSheetDTO timeSheet;
    private WorkReportDTO workReport;

    private WorkLogWrapper(ContractDTO contract, TimeSheetDTO timeSheet, WorkReportDTO workReport) {
        this.contract = contract;
        this.timeSheet = timeSheet;
        this.workReport = workReport;
    }

    public static WorkLogWrapper of(Map.Entry<ContractDTO, TimeSheetDTO> entry) {
        WorkReportDTO workReport = new WorkReportDTO();
        workReport.setType(ReportType.WORK);
        workReport.setTimeSheet(entry.getValue());
        return new WorkLogWrapper(entry.getKey(), entry.getValue(), workReport);
    }

    public ContractDTO getContract() {
        return contract;
    }

    public void setContract(ContractDTO contract) {
        this.contract = contract;
    }

    public TimeSheetDTO getTimeSheet() {
        return timeSheet;
    }

    public void setTimeSheet(TimeSheetDTO timeSheet) {
        this.timeSheet = timeSheet;
    }

    public WorkReportDTO getWorkReport() {
        return workReport;
    }

    public void setWorkReport(WorkReportDTO workReport) {
        this.workReport = workReport;
    }
}
