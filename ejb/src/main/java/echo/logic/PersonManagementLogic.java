package echo.logic;

import echo.dto.PersonDTO;
import echo.dto.RoleDTO;

import javax.ejb.Local;
import java.util.List;
import java.util.Optional;

@Local
public interface PersonManagementLogic {
    /**
     * Request a list of all persons from the database
     *
     * @return A list of {@link PersonDTO}
     */
    List<PersonDTO> getAllPersons();

    /**
     * Returns the count of all persons stored in the database
     *
     * @return an long with the person count
     */
    long countOfAllPersons();

    /**
     * Saves the person in the database
     *
     * @param person the person that should be persisted
     */
    PersonDTO save(PersonDTO person);

    /**
     * @return The list of all available roles in the database
     */
    List<RoleDTO> getAllRoles();

    /**
     * Loads all university roles from a given person
     *
     * @param personId the person id
     * @return a list of university roles
     */
    List<String> getUniRoles(long personId);

    /**
     * Loads a person by EmailAdress
     *
     * @param email
     * @return
     */
    Optional<PersonDTO> getOneByEmail(String email);
}
