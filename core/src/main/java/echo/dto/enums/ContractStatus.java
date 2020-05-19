package echo.dto.enums;

import echo.util.I18nUtil;

public enum ContractStatus {
    PREPARED,
    STARTED,
    TERMINATED,
    ARCHIVED;

    public String getLocalizedName() {
        return I18nUtil.getString("contract.status." + name().toLowerCase());
    }
}
