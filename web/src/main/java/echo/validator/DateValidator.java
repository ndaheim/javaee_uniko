package echo.validator;

import echo.converter.LocalDateConverter;
import echo.dto.TimeSheetDTO;
import echo.logic.TimeSheetLogic;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

@Named
public class DateValidator implements Validator {

    @EJB
    private TimeSheetLogic timeSheetLogic;

    private LocalDateConverter localDateConverter = new LocalDateConverter();

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

        Object objDate = ((UIInput) component.getAttributes().get("date")).getSubmittedValue();
        TimeSheetDTO timeSheetDTO = (TimeSheetDTO) component.getAttributes().get("timeSheetDTO");

        if(objDate == null || ((String)objDate).equals("")) {
            ResourceBundle bundle = ResourceBundle.getBundle("languages.tss", context.getViewRoot().getLocale());
            String message = bundle.getString("date.validation.nodate");
            FacesMessage msg =
                    new FacesMessage("",
                            message);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
        String strDate = (String) objDate;
        LocalDate date = (LocalDate) localDateConverter.getAsObject(null, null, strDate);
        if (timeSheetLogic.getDisallowedDates(timeSheetDTO).contains(date)) {
            ResourceBundle bundle = ResourceBundle.getBundle("languages.tss", context.getViewRoot().getLocale());
            String message = bundle.getString("date.validation.holiday");
            FacesMessage msg =
                    new FacesMessage("",
                            message);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

        if (timeSheetDTO.getStartDate().isAfter(date) || timeSheetDTO.getEndDate().isBefore(date)) {
            ResourceBundle bundle = ResourceBundle.getBundle("languages.tss", context.getViewRoot().getLocale());
            String message = bundle.getString("date.validation.notinperiod");
            FacesMessage msg =
                    new FacesMessage("",
                            message);
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }
}
