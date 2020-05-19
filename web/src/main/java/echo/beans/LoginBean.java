package echo.beans;

import echo.dto.PersonDTO;
import echo.logic.LoginLogic;
import echo.logic.PersonConfigLogic;
import echo.logic.SessionCacheLogic;
import echo.models.AppView;
import echo.util.I18nUtil;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

@Named
@ViewScoped
public class LoginBean implements Serializable {
    @EJB
    private SessionCacheLogic sessionCache;
    @EJB
    private LoginLogic loginLogic;
    @EJB
    private PersonConfigLogic personConfigLogic;

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @PostConstruct
    public void init() {
        if(sessionCache.getSessionPerson().isPresent()) {
            FacesContext context = FacesContext.getCurrentInstance();
            RequestDispatcher dispatcher =
                    ((ServletRequest) context.getExternalContext().getRequest())
                            .getRequestDispatcher("dashboard.xhtml");

            try {
                dispatcher.forward((ServletRequest) context.getExternalContext().getRequest(),
                        (ServletResponse) context.getExternalContext().getResponse());
            } catch (ServletException e) {
                System.out.println("ServletException");
            } catch (IOException e) {
                System.out.println("IOException");
            }
        }
    }
    /**
     * Validate the entered credentials and creates a user session.
     * After that it navigates to the user dashboard.
     * If the login is not successful a error message will appear.
     */
    public String login() {
        Optional<PersonDTO> loginPerson = loginLogic.readPersonFromLdap(username, password);

        if (loginPerson.isPresent()) {
            sessionCache.setSessionPerson(loginPerson.get());
            loadDefaultUILanguage();
            return AppView.DASHBOARD.getFileNameWithRedirect();
        } else {
            showMessage(I18nUtil.getString("login.error"), "loginForm:readPersonFromLdap");
        }

        return null;
    }

    private void loadDefaultUILanguage() {
        I18nUtil.changeLocale(personConfigLogic.getSessionPersonLocale());
    }

    /**
     * Invalidates the current user session and navigates to the login page
     */
    public String logout() {
        sessionCache.invalidateCurrent();
        return AppView.LOGIN.getFileNameWithRedirect();
    }

    private void showMessage(String message, String container) {
        FacesMessage fmsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", message);
        FacesContext.getCurrentInstance().addMessage(container, fmsg);
    }
}