package echo.beans;

import echo.dto.PersonDTO;
import echo.dto.enums.ReportType;
import echo.logic.EnterWorkReportLogic;
import echo.logic.SessionCacheLogic;
import echo.models.WorkLogWrapper;
import echo.security.LoggedInWebSecurityHandler;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class EnterWorkReportBean implements Serializable {
    @EJB
    private EnterWorkReportLogic enterWorkReportLogic;
    @EJB
    private LoggedInWebSecurityHandler loggedInWebSecurityHandler;
    @EJB
    private SessionCacheLogic sessionCache;

    private List<WorkLogWrapper> workReports;

    @PostConstruct
    private void init() {
        loggedInWebSecurityHandler.isPostConstructGranted("#isAuthenticated($null)");
        Optional<PersonDTO> person = sessionCache.getSessionPerson();
        if (person.isPresent()) {
            workReports = enterWorkReportLogic
                    .getContractsWithActiveTimeSheet(person.get())
                    .entrySet()
                    .stream()
                    .map(WorkLogWrapper::of)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Saves the entered Worklog
     *
     * @param wrapper
     */
    @SuppressWarnings("unused")
    public void saveEnteredWorkHours(WorkLogWrapper wrapper) {
        enterWorkReportLogic.saveWorkReport(
                wrapper.getContract(),
                wrapper.getTimeSheet(),
                wrapper.getWorkReport());
        init();
    }

    /**
     * Returns a {@link List} of all {@link WorkLogWrapper}
     *
     * @return a {@link List} of all {@link WorkLogWrapper}
     */
    public List<WorkLogWrapper> getWorkReports() {
        return workReports;
    }

    public void setWorkReports(List<WorkLogWrapper> workReports) {
        this.workReports = workReports;
    }

    /**
     * Returns an array of all {@link ReportType}
     *
     * @return an array of all {@link ReportType}
     */
    public ReportType[] getAllTypes() {
        return ReportType.values();
    }
}