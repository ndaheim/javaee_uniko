package echo.dto.enums;

import echo.util.I18nUtil;

public enum TimeSheetFrequency {
    WEEKLY,
    MONTHLY;

    public String getLocalizedName() {
        return I18nUtil.getString("contract.frequency." + name().toLowerCase());
    }
}
