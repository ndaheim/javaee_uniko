package echo.beans;

import echo.dto.PersonDTO;
import echo.logic.PersonConfigLogic;
import echo.logic.SessionCacheLogic;
import echo.models.PersonConfigIdentifier;
import echo.security.LoggedInWebSecurityHandler;
import echo.util.I18nUtil;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Named
@ViewScoped
public class ProfileBean implements Serializable {
    @EJB
    private PersonConfigLogic personConfigLogic;
    @EJB
    private LoggedInWebSecurityHandler loggedInWebSecurityHandler;
    @EJB
    private SessionCacheLogic sessionCache;

    private String language;
    private List<Locale> availableLanguages;

    @PostConstruct
    private void init() {
        loggedInWebSecurityHandler.isPostConstructGranted("#isAuthenticated($null)");
    }

    public String getLanguage() {
        return personConfigLogic.getSessionPersonLocale().toLanguageTag();
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Returns a {@link List} of all available {@link Locale}
     *
     * @return a {@link List} of all available {@link Locale}
     */
    public List<Locale> getAllLanguages() {
        if (availableLanguages == null) {
            availableLanguages = new ArrayList<>();
            availableLanguages.add(Locale.ENGLISH);
            availableLanguages.add(Locale.GERMAN);
        }
        return availableLanguages;
    }

    /**
     * Saves the {@link echo.entities.PersonConfig} of the currently logged-in {@link PersonDTO}
     */
    public void save() {
        Optional<PersonDTO> sessionPerson = sessionCache.getSessionPerson();
        if (sessionPerson.isPresent()) {
            personConfigLogic.setConfig(sessionPerson.get().getId(), PersonConfigIdentifier.UI_LANGUAGE, language);
            I18nUtil.changeLocale(Locale.forLanguageTag(language));
        }
    }
}