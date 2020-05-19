package echo.models;

import java.util.Arrays;
import java.util.List;

public enum AppView {
    //TODO:
    LOGIN("login.xhtml", RoleName.values()),
    DASHBOARD("dashboard.xhtml", RoleName.values()),
    PERSON_CONFIG("person-config.xhtml", RoleName.values()),
    PERSON_MANAGEMENT("person-management.xhtml", RoleName.ADMINISTRATOR),
    CONTRACT("contract.xhtml", RoleName.values()),
    TIMESHEET_OVERVIEW("timesheet-overview.xhtml", RoleName.values());

    private String fileName;
    private List<RoleName> authorizedRoles;

    AppView(String fileName, RoleName... authorizedRoles) {
        this.fileName = fileName;
        this.authorizedRoles = Arrays.asList(authorizedRoles);
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileNameWithRedirect() {
        return fileName + "?faces-redirect=true";
    }

    public List<RoleName> getAuthorizedRoles() {
        return authorizedRoles;
    }
}
