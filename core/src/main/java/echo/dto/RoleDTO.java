package echo.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import echo.models.RoleName;
import echo.util.I18nUtil;

public class RoleDTO extends SimpleDTO {
    private RoleName name;

    public RoleDTO() {
    }

    public RoleDTO(RoleName name, Long id) {
        this.name = name;
        this.setId(id);
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    @JsonIgnore
    public String getLocalizedName() {
        return I18nUtil.getString("role." + name.name().toLowerCase());
    }
}
