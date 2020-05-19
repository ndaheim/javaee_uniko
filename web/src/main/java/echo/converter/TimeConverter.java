package echo.converter;

import echo.util.StringUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.sql.Time;
import java.time.LocalTime;

@FacesConverter("timeConverter")
public class TimeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
        Time parsedTime = null;

        if (StringUtils.isNotEmpty(value)) {
            parsedTime = Time.valueOf(LocalTime.parse(value));
        }

        return parsedTime;
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent component, Object value) {
        String timeString = "";

        if (value instanceof String) {
            timeString = (String) value;
        } else if (value != null) {
            timeString = ((Time) value).toLocalTime().toString();
        }

        return timeString;
    }
}
