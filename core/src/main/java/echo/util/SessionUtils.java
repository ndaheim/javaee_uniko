package echo.util;

import javax.faces.context.FacesContext;

public class SessionUtils {
    private SessionUtils() {
    }

    public static String getCurrentSessionId() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionId(false);
    }
}
