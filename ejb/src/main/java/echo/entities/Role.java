package echo.entities;

import echo.models.RoleName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class Role extends SimpleEntity {
    @Column
    @Enumerated(EnumType.STRING)
    private RoleName name;

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName type) {
        this.name = type;
    }
}
