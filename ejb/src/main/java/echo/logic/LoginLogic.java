package echo.logic;

import echo.dto.PersonDTO;

import javax.ejb.Local;
import java.util.Optional;

@Local
public interface LoginLogic {
    /**
     * Tries to find the person in the ldap directory by username and password. If the ldap validation is correct,
     * the method checks if the person is already in the database. If he is, the person gets updated with the data
     * from the ldap. If he is not in the database, a new person will be created with the data from the ldap.
     *
     * @param username The username to validate
     * @param password The clear password of the person
     * @return If the login was successful, the method will return a {@link Optional} of {@link PersonDTO} with the found person.
     * If the login was not successful, the method will return Optional.EMPTY.
     */
    Optional<PersonDTO> readPersonFromLdap(String username, String password);


    /**
     * Tries to find the person in the ldap directory by username. If the person was found,
     * the method checks if the person is already in the database. If he is, the person gets updated with the data
     * from the ldap. If he is not in the database, a new person will be created with the data from the ldap.
     *
     * @param username The username to read
     * @return If the person was found, the method will return a {@link Optional} of {@link PersonDTO}.
     * Otherwise Optional.EMPTY.
     */
    Optional<PersonDTO> readPersonFromLdap(String username);
}
