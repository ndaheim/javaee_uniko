package echo.validator;

import echo.util.I18nUtil;
import echo.util.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.Map;

@FacesValidator("timeValidator")
public class TimeValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) {

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        boolean noValidate = false;
        for(String key : params.keySet()) {
            if(key.contains("novalidate")) {
                noValidate = true;
            }
        }

        if(noValidate) return;

        Map<String, Object> attributes = component.getAttributes();
        Object startTime = ((UIInput) attributes.get("startTime")).getSubmittedValue();
        Object endTime = ((UIInput) attributes.get("endTime")).getSubmittedValue();

        if (isValidInput(startTime) && isValidInput(endTime)) {
            String[] arrStartTime = ((String) startTime).split(":");
            String[] arrEndTime = ((String) endTime).split(":");

            int startHour = Integer.parseInt(arrStartTime[0], 10);
            int startMinute = Integer.parseInt(arrStartTime[1], 10);

            int endHour = Integer.parseInt(arrEndTime[0], 10);
            int endMinute = Integer.parseInt(arrEndTime[1], 10);

            if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
                FacesMessage msg = new FacesMessage("", I18nUtil.getString("time.validation.error"));
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
        else{
            FacesMessage msg = new FacesMessage("", I18nUtil.getString("time.validation.error.input"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    private boolean isValidInput(Object toCheck) {
        return toCheck instanceof String && ((String) toCheck).contains(":");
    }
}
