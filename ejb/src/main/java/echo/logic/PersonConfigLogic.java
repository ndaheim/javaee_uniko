package echo.logic;

import echo.models.PersonConfigIdentifier;

import javax.ejb.Local;
import java.util.Locale;

@Local
public interface PersonConfigLogic {
    /**
     * Loads a stored config value
     *
     * @param personId   the {@link echo.entities.Person} id from which person the configuration value gets loaded
     * @param identifier the identifier of the configuration value that is requested
     * @return the stored value for the identifier and person or null if no value was found
     */
    String getConfig(long personId, PersonConfigIdentifier identifier);

    /**
     * Sets a config value, if a value for the personId, identifier combination already exists, the value gets
     * overwritten by the new value
     *
     * @param personId   the id of the {@link echo.entities.Person} where the config should be stored
     * @param identifier the identifier of the configuration value
     * @param value      the value that should be stored
     */
    void setConfig(long personId, PersonConfigIdentifier identifier, String value);

    /**
     * Reads the configured locale of the current session person
     *
     * @return The users configured locale, if no one has been defined or no user is logged in,
     * Locale.ENGLISH is returned as default.
     */
    Locale getSessionPersonLocale();

    /**
     * Returns the locale of a Person behind an id
     *
     * @param id @param id the id of the {@link echo.entities.Person}, whose locale, shall be returned
     * @return The Locale of the {@link echo.entities.Person}
     */
    Locale getDBPersonLocale(long id);
}
