package echo.dao;

import echo.entities.Reminder;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

@Stateless
@LocalBean
public class ReminderAccess extends SimpleEntityAccess<Reminder> {

    public ReminderAccess() {
        super(Reminder.class);
    }

    public void removeByTimeSheet(long id) {
        Query query = em
                .createQuery("DELETE FROM Reminder r WHERE r.timeSheet.id = :id")
                .setParameter("id", id);
        query.executeUpdate();
    }

}
