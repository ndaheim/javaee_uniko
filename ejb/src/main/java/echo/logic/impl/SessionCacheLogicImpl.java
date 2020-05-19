package echo.logic.impl;

import echo.dto.PersonDTO;
import echo.logic.SessionCacheLogic;
import echo.models.TimedData;
import echo.util.SessionUtils;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Optional;

@Startup
@Singleton
public class SessionCacheLogicImpl implements SessionCacheLogic {
    private static final int SESSION_TIMEOUT_HOURS = 0;
    private HashMap<String, TimedData<PersonDTO>> sessionCache = new HashMap<>();

    @Override
    public Optional<PersonDTO> getSessionPerson() {
        Optional<PersonDTO> person = Optional.empty();
        String sessionId = SessionUtils.getCurrentSessionId();

        if (sessionCache.containsKey(sessionId)) {
            person = Optional.ofNullable(sessionCache.get(sessionId).getData());
        }

        return person;
    }

    @Override
    public void setSessionPerson(PersonDTO person) {
        String sessionId = SessionUtils.getCurrentSessionId();
        sessionCache.put(sessionId, new TimedData<>(person));
    }

    @Override
    public void invalidateCurrent() {
        sessionCache.remove(SessionUtils.getCurrentSessionId());
    }

    @Schedule(minute = "*/15", hour = "*", info = "15MinScheduler", persistent = false)
    public void cleanOldSessions() {
        sessionCache.entrySet().stream()
                .filter(x -> x.getValue().getStamp().isBefore(LocalDateTime.now().minus(SESSION_TIMEOUT_HOURS, ChronoUnit.HOURS)))
                .forEach(x -> sessionCache.remove(x.getKey()));
    }
}