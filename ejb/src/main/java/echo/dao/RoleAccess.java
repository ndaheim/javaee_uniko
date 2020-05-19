package echo.dao;

import echo.entities.Role;
import echo.models.RoleName;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
@LocalBean
public class RoleAccess extends SimpleEntityAccess<Role> {

    private Map<RoleName, Role> roleNamedCache;
    private List<Role> allRoles;

    public RoleAccess() {
        super(Role.class);
    }

    @PostConstruct
    public void postConstruct() {
        allRoles = super.findAll();
        roleNamedCache = allRoles.stream().collect(Collectors.toMap(Role::getName, r -> r));
    }

    @Override
    public List<Role> findAll() {
        return allRoles;
    }

    public Role getRole(RoleName roleName) {
        return roleNamedCache.get(roleName);
    }
}
