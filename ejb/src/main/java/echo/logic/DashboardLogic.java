package echo.logic;

import echo.dto.PersonDTO;

import javax.ejb.Local;
import java.util.List;

@Local
public interface DashboardLogic {
    /**
     * Returns the total number of {@link echo.entities.Person} in the System
     *
     * @return the total number of {@link echo.entities.Person} in the System
     */
    long getPersonCount();

    /**
     * Returns all {@link echo.entities.Person} in the System
     *
     * @return all {@link echo.entities.Person} in the System
     */
    List<PersonDTO> getAllPersons();
}
