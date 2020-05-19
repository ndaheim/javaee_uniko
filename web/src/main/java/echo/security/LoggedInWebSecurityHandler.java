package echo.security;

import echo.logic.SessionCacheLogic;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class LoggedInWebSecurityHandler extends AbstractWebSecurityHandler {

    @EJB
    private SessionCacheLogic sessionCacheLogic;

    private boolean isAuthenticated() {
        this.setRedirectTarget("login.xhtml");
        return sessionCacheLogic.getSessionPerson().isPresent();
    }
}
