package echo.util;

import javax.faces.context.FacesContext;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18nUtil {
    private static final String BUNDLE_NAME = "msg";

    private I18nUtil() {
    }

    public static void changeLocale(Locale locale) {
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
        FacesContext.getCurrentInstance().getApplication().setDefaultLocale(locale);
        Locale.setDefault(locale);
    }

    public static String getString(String key) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, BUNDLE_NAME);
        return bundle.getString(key);
    }
}
