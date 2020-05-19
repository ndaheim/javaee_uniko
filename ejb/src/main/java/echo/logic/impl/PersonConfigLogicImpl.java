package echo.logic.impl;

import echo.dao.PersonAccess;
import echo.dao.PersonConfigAccess;
import echo.dto.PersonDTO;
import echo.entities.PersonConfig;
import echo.logic.PersonConfigLogic;
import echo.logic.SessionCacheLogic;
import echo.models.PersonConfigIdentifier;
import org.slf4j.Logger;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Locale;
import java.util.Optional;

@Stateless
@LocalBean
public class PersonConfigLogicImpl implements PersonConfigLogic {
    @EJB
    private PersonConfigAccess personConfigAccess;
    @EJB
    private PersonAccess personAccess;
    @EJB
    private SessionCacheLogic sessionCacheLogic;

    @Inject
    private Logger log;

    @Override
    public String getConfig(long personId, PersonConfigIdentifier identifier) {
        PersonConfig personConfig = personConfigAccess.findOne(personId, identifier);
        return personConfig == null ? null : personConfig.getValue();
    }

    @Override
    public void setConfig(long personId, PersonConfigIdentifier identifier, String value) {
        PersonConfig dbConfig = personConfigAccess.findOne(personId, identifier);

        if (dbConfig == null) {
            dbConfig = new PersonConfig();
        }

        dbConfig.setIdentifier(identifier);
        dbConfig.setValue(value);
        dbConfig.setPerson(personAccess.findOne(personId));

        personConfigAccess.save(dbConfig);
    }

    @Override
    public Locale getSessionPersonLocale() {
        Locale userLocale = Locale.ENGLISH;

        Optional<PersonDTO> sessionPerson = sessionCacheLogic.getSessionPerson();
        if (sessionPerson.isPresent()) {
            userLocale = getDBPersonLocale(sessionPerson.get().getId());
        }

        return userLocale;
    }

    @Override
    public Locale getDBPersonLocale(long id) {
        Locale userLocale = Locale.ENGLISH;

        String languageTag = getConfig(id, PersonConfigIdentifier.UI_LANGUAGE);
        if (languageTag != null) {
            userLocale = Locale.forLanguageTag(languageTag);
        }

        return userLocale;
    }
}
