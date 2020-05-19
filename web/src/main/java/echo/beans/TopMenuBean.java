package echo.beans;

import echo.dto.PersonDTO;
import echo.dto.RoleDTO;
import echo.logic.SessionCacheLogic;
import echo.models.AppView;
import echo.models.RoleName;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class TopMenuBean implements Serializable {

    @EJB
    private SessionCacheLogic sessionCache;

    /**
     * Returns the full name of the currently logged-in {@link PersonDTO}
     *
     * @return The full name of the currently logged-in {@link PersonDTO}, if it exists,
     * "Person" otherwise
     */
    public String getPersonFullName() {
        Optional<PersonDTO> sessionPerson = sessionCache.getSessionPerson();
        if (sessionPerson.isPresent()) {
            return sessionPerson.get().getFullName();
        }

        return "Person";
    }

    /**
     * Returns the visibility of the MenuItem
     *
     * @return "true" if it is visible,
     * "false" otherwise
     */
    public boolean getMenuItemVisibility() {
        boolean displayVisibility = true;

        if (!sessionCache.getSessionPerson().isPresent()) {
            displayVisibility = false;
        }

        return displayVisibility;
    }

    /**
     * Returns the visibility of the MenuItem for a given {@link AppView}
     *
     * @return "true" if it is visible,
     * "false" otherwise
     */
    public boolean getMenuItemVisibilityByView(AppView appView) {
        boolean displayVisibility = true;

        Optional<PersonDTO> sessionPerson = sessionCache.getSessionPerson();
        if (!sessionPerson.isPresent()) {
            displayVisibility = false;
        } else {
            List<RoleName> givenRoles = sessionPerson.get().getRoles().stream().map(RoleDTO::getName).collect(Collectors.toList());
            List<RoleName> neededRoles = appView.getAuthorizedRoles();

            if (givenRoles.stream().noneMatch(neededRoles::contains)) {
                displayVisibility = false;
            }
        }

        return displayVisibility;
    }

    public String navigateTo(AppView appView) {
        return appView.getFileNameWithRedirect();
    }
}