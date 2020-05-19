package echo.logic;

import echo.dto.ContractDTO;
import echo.dto.PersonDTO;
import echo.dto.TimeSheetDTO;
import echo.dto.WorkReportDTO;

import javax.ejb.Local;
import java.util.Map;

@Local
public interface EnterWorkReportLogic {
    /**
     * Returns a {@link java.util.HashMap} of all {@link ContractDTO} with active {@link TimeSheetDTO}.
     * The {@link ContractDTO} is the key of the map and the current {@link TimeSheetDTO} the value.
     *
     * @param person The {@link PersonDTO}, for whom the {@link ContractDTO} shall be returned.
     * @return a {@link java.util.HashMap} of all {@link ContractDTO} with active {@link TimeSheetDTO},
     * which has the {@link ContractDTO} as key and the active {@link TimeSheetDTO as value}
     */
    Map<ContractDTO, TimeSheetDTO> getContractsWithActiveTimeSheet(PersonDTO person);

    /**
     * Saves a {@link WorkReportDTO}
     *
     * @param contract   The {@link ContractDTO} of the {@link WorkReportDTO}
     * @param timeSheet  The {@link TimeSheetDTO} of the {@link WorkReportDTO}
     * @param workReport The {@link WorkReportDTO}
     */
    void saveWorkReport(ContractDTO contract, TimeSheetDTO timeSheet, WorkReportDTO workReport);
}
