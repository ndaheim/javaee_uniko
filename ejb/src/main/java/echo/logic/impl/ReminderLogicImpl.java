package echo.logic.impl;

import echo.dao.PersonAccess;
import echo.dao.ReminderAccess;
import echo.dao.TimeSheetAccess;
import echo.dto.enums.TimeSheetFrequency;
import echo.entities.Person;
import echo.entities.Reminder;
import echo.entities.TimeSheet;
import echo.logic.PersonConfigLogic;
import echo.logic.ReminderLogic;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Stateless
@LocalBean
public class ReminderLogicImpl implements ReminderLogic {

    @EJB
    private ReminderAccess reminderAccess;
    @EJB
    private TimeSheetAccess timeSheetAccess;
    @EJB
    private PersonAccess personAccess;
    @EJB
    private PersonConfigLogic personConfigLogic;

    @Resource(lookup = "mail/uniko-mail")
    private Session mailSession;

    /**
     * Sends a mail with the given text and subject to the given recipients
     *
     * @param subject    The subject of the mail
     * @param recipients a String containing the recipitients of the mail, multiple recipients should be
     *                   provided via a comma-separated list
     * @param html       The HTML text of the mail
     */
    private void sendMail(String subject, String recipients, String html) {
        try {
            Message msg = new MimeMessage(mailSession);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            //Provide the recipients in one string, divided by commas e.g. "a@b.de,c@d.de"
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients, false));

            msg.setContent(html, "text/html");
            /*Multipart multipart = new MimeMultipart();

            MimeBodyPart plainText = new MimeBodyPart();
            plainText.setText(plain);

            MimeBodyPart htmlText = new MimeBodyPart();
            htmlText.setContent(html, "text/html");

            multipart.addBodyPart(plainText);
            multipart.addBodyPart(htmlText);
            msg.setContent(multipart);*/

            Transport.send(msg);
        } catch (MessagingException mex) {
            System.out.println("Sending Failed: " + mex.toString());
        }
    }

    @Override
    @Schedule(hour = "6")
    public void sendReminders() {
        List<Person> toRemind = personAccess.findAllWithReminders();

        for (Person person : toRemind) {
            this.sendMail(
                    "TSS - Reminder",
                    person.getEmailAddress(),
                    person.getCollectedHTMLReminderMessages(
                            personConfigLogic.getDBPersonLocale(person.getId()).getLanguage())
            );
        }
    }

    @Override
    @Schedule(dayOfWeek = "Sun")
    public void createWeeklyTimeSheetReminders() {
        List<TimeSheet> timeSheets = timeSheetAccess.getToRemindByFrequency(TimeSheetFrequency.WEEKLY);
        for (TimeSheet t : timeSheets) {
            reminderAccess.save(new Reminder(t, t.getContract().getEmployee()));
            personAccess.save(t.getContract().getEmployee());
        }
    }

    @Override
    @Schedule(dayOfMonth = "Last")
    public void createMonthlyTimeSheetReminders() {
        List<TimeSheet> timeSheets = timeSheetAccess.getToRemindByFrequency(TimeSheetFrequency.MONTHLY);
        for (TimeSheet t : timeSheets) {
            reminderAccess.save(new Reminder(t, t.getContract().getEmployee()));
            personAccess.save(t.getContract().getEmployee());
        }
    }

    @Override
    public void createRemindersByTimeSheet(long id) {
        TimeSheet timeSheet = timeSheetAccess.findOne(id);

        switch (timeSheet.getTimeSheetStatus()) {

            case SIGNED_BY_EMPLOYEE:
                Set<Person> toRemind = new HashSet<>(timeSheet.getContract().getDelegates());
                toRemind.add(timeSheet.getContract().getSupervisor());
                for (Person p : toRemind) {
                    reminderAccess.save(new Reminder(timeSheet, p));
                    personAccess.save(p);
                }
                break;

            case SIGNED_BY_SUPERVISOR:
                for (Person p : timeSheet.getContract().getSecretaries()) {
                    reminderAccess.save(new Reminder(timeSheet, p));
                    personAccess.save(p);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void removeByTimeSheet(long id) {
        reminderAccess.removeByTimeSheet(id);
    }

}
