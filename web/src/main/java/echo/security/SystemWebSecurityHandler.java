package echo.security;

import echo.dto.PersonDTO;
import echo.dto.RoleDTO;
import echo.logic.PersonConfigLogic;
import echo.logic.SessionCacheLogic;
import echo.models.PersonConfigIdentifier;
import echo.models.RoleName;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Optional;

@Stateless
public class SystemWebSecurityHandler extends AbstractWebSecurityHandler {

    @EJB
    private PersonConfigLogic personConfigLogic;
    @EJB
    private SessionCacheLogic sessionCacheLogic;

    private boolean hasLDAPRole(String role) {
        Optional<PersonDTO> user = sessionCacheLogic.getSessionPerson();
        if(user.isPresent()) {
         String roles = personConfigLogic.getConfig(user.get().getId(), PersonConfigIdentifier.UNIVERSITY_ROLES);
         return roles.contains(role);
        }
        return false;
    }

    private boolean hasSystemRole(RoleName role) {
        Optional<PersonDTO> user = sessionCacheLogic.getSessionPerson();
        if(user.isPresent()) {
            List<RoleDTO> roleDTOS = user.get().getRoles();
            for(RoleDTO roleDTO : roleDTOS) {
                if(roleDTO.getName().equals(role)) return true;
            }
        }
        return false;
    }
}
