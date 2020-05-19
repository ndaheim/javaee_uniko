package echo.logic;

import echo.dto.PersonDTO;

import javax.ejb.Local;
import java.util.Optional;

@Local
public interface SessionCacheLogic {
    /**
     * Returns the currently logged in {@link PersonDTO}
     *
     * @return the currently logged in {@link PersonDTO}
     */
    Optional<PersonDTO> getSessionPerson();

    /**
     * Sets the currently logged in {@link PersonDTO}
     *
     * @param person The {@link PersonDTO}, to which the current sessionPerson shall be set
     */
    void setSessionPerson(PersonDTO person);

    /**
     * Invalidates the currently logged in Person
     */
    void invalidateCurrent();
}
