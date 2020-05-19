package echo.validator;

import echo.dto.ContractDTO;
import echo.dto.TimeSheetDTO;
import echo.dto.WorkReportDTO;
import echo.dto.enums.ReportType;
import echo.entities.Contract;
import echo.logic.ContractManagementLogic;
import echo.logic.TimeSheetLogic;
import echo.util.I18nUtil;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

public class TimeSheetValidator {
    private TimeSheetDTO timeSheetDTO;
    private ContractManagementLogic contractManagementLogic;
    private List<String> errorMessages = new ArrayList<>();

    private TimeSheetValidator(TimeSheetDTO timeSheetDTO, ContractManagementLogic contractManagementLogic) {
        this.timeSheetDTO = timeSheetDTO;
        this.contractManagementLogic = contractManagementLogic;
    }

    public static TimeSheetValidator of(TimeSheetDTO timeSheetDTO, ContractManagementLogic contractManagementLogic) {
        return new TimeSheetValidator(timeSheetDTO, contractManagementLogic);
    }

    public boolean validate() {
        List<WorkReportDTO> workReportDTOS = timeSheetDTO.getWorkReports();
        boolean isOkay = true;

        //See if there are overlapping times
        for (int i = 0; i < workReportDTOS.size(); i++) {
            for (int j = 0; j < workReportDTOS.size(); j++) {
                if (i == j) continue;
                WorkReportDTO a = workReportDTOS.get(i);
                WorkReportDTO b = workReportDTOS.get(j);

                if (a.getDay().equals(b.getDay())) {
                    if (a.getStartTime().before(b.getStartTime()) && a.getEndTime().after(b.getStartTime())) {
                        isOkay = false;
                        addIfNotExists(I18nUtil.getString("timesheet.error.overlapping"));
                    }
                    if (a.getStartTime().after(b.getStartTime()) && a.getStartTime().before(b.getEndTime())) {
                        isOkay = false;
                        addIfNotExists(I18nUtil.getString("timesheet.error.overlapping"));
                    }
                    if (a.getStartTime().before(b.getStartTime()) && a.getEndTime().after(b.getEndTime())) {
                        isOkay = false;
                        addIfNotExists(I18nUtil.getString("timesheet.error.overlapping"));
                    }
                    if (a.getStartTime().equals(b.getStartTime()) && a.getEndTime().equals(b.getEndTime())) {
                        addIfNotExists(I18nUtil.getString("timesheet.error.overlapping"));
                    }
                    if (a.getStartTime().equals(b.getStartTime()) && a.getEndTime().before(b.getEndTime())) {
                        addIfNotExists(I18nUtil.getString("timesheet.error.overlapping"));
                    }
                }
            }
        }

        double vacationHours = 0;
        ContractDTO contractDTO = contractManagementLogic.findContract(timeSheetDTO.getContract().getId());
        //Remove this TimeSheet so it doesn't count twice
        contractDTO.getTimeSheets().removeIf(ts -> ts.getId().equals(timeSheetDTO.getId()));
        for(TimeSheetDTO ts : contractDTO.getTimeSheets()) {
            vacationHours += ts.getVacationHours();
        }
        vacationHours += timeSheetDTO.getVacationHours();
        if(vacationHours > contractDTO.getVacationHours()) {
            isOkay = false;
            addIfNotExists(I18nUtil.getString("timesheet.error.vacationexceed"));
        }
        return isOkay;
    }

    public String getFormattedErrorMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");
        for (String msg : errorMessages) {
            sb.append("<li>").append(msg).append("</li>");
        }
        sb.append("</ul>");
        return sb.toString();
    }

    private void addIfNotExists(String item) {
        if(!errorMessages.contains(item)) {
            errorMessages.add(item);
        }
    }

}
