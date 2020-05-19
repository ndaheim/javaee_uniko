package echo.converter;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.FacesConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@FacesConverter("localDateConverter")
public class LocalDateConverter extends DateTimeConverter {
    private static final String DELIMITER_MINUS = "-";
    private static final String DELIMITER_SLASH = "/";
    private static final String DELIMITER_DOT = ".";

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        if(component != null && component.getAttributes().get("format") != null && component.getAttributes().get("format").equals("MM-YYYY")) {
            return LocalDate.parse("01-" + value, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        }
        if (value.matches("\\d+\\.\\d+\\.\\d+")) {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd.MM.yy"));
        }
        return LocalDate.parse(value, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String stringDate = "";

        if (value instanceof String) {
            stringDate = (String) value;
        } else if (value != null) {
            LocalDate date = (LocalDate) value;

            if(component != null && component.getAttributes().get("format") != null) {
                return date.format(DateTimeFormatter.ofPattern((String)component.getAttributes().get("format")));
            }

            FacesContext facesContext = FacesContext.getCurrentInstance();
            UIViewRoot viewRoot = facesContext.getViewRoot();
            String locale = viewRoot.getLocale().getLanguage();

            if (locale.equals("de")) {
                return date.format(DateTimeFormatter.ofPattern("dd.MM.yy"));
            } else {
                return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            }
        }

        return stringDate;
    }
}