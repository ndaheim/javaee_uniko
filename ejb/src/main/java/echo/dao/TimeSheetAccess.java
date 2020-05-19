package echo.dao;

import echo.dto.TimeSheetDTO;
import echo.dto.enums.TimeSheetFrequency;
import echo.dto.enums.TimeSheetStatus;
import echo.entities.TimeSheet;
import echo.mapper.TimeSheetMapper;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

@Stateless
@LocalBean
public class TimeSheetAccess extends SimpleEntityAccess<TimeSheet> {
    private TimeSheetMapper timeSheetMapper = new TimeSheetMapper();

    public TimeSheetAccess() {
        super(TimeSheet.class);
    }

    public TimeSheetDTO findOneEager(Long timeSheetId) {
        return timeSheetMapper.toDTO(findOne(timeSheetId), TimeSheetDTO.class);
    }

    public List<TimeSheet> getToRemindByFrequency(TimeSheetFrequency timeSheetFrequency) {
        TypedQuery<TimeSheet> query = em
                .createQuery("SELECT t FROM TimeSheet t WHERE t.contract.frequency = :frequency " +
                        "AND t.endDate = :today AND t.timeSheetStatus = :status", TimeSheet.class)
                .setParameter("frequency", timeSheetFrequency)
                .setParameter("today", LocalDate.now())
                .setParameter("status", TimeSheetStatus.IN_PROGESS);
        return query.getResultList();
    }

}
