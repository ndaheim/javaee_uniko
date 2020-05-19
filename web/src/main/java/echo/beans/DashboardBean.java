package echo.beans;

import echo.dto.PersonDTO;
import echo.logic.DashboardLogic;
import echo.security.LoggedInWebSecurityHandler;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class DashboardBean implements Serializable {
    @EJB
    private DashboardLogic dashboardLogic;
    @EJB
    private LoggedInWebSecurityHandler loggedInWebSecurityHandler;

    @PostConstruct
    public void init() {
        loggedInWebSecurityHandler.isPostConstructGranted("#isAuthenticated($null)");
    }

    public String getPersonCount() {
        return dashboardLogic.getPersonCount() + "";
    }

    public List<PersonDTO> getAllPersons() {
        return dashboardLogic.getAllPersons();
    }
}