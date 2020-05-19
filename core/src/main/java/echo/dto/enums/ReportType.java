package echo.dto.enums;

import echo.util.I18nUtil;

public enum ReportType {
    WORK,
    VACATION,
    SICK_LEAVE;

    public String getLocalizedName() {
        return I18nUtil.getString("workreport.type." + name().toLowerCase());
    }

}
