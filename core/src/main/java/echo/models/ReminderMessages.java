package echo.models;

import echo.dto.enums.TimeSheetStatus;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ReminderMessages {

    public static final HashMap<String, String> greetings = new HashMap<String, String>() {
        {
            this.put("de", "<html><body style=\"margin: 0; padding: 0;\">Hallo %s,<br>");
            this.put("en", "<html><body style=\"margin: 0; padding: 0;\">Dear %s,<br>");
        }
    };
    public static final HashMap<String, String> closings = new HashMap<String, String>() {
        {
            this.put("de", "<br>" +
                    "Mit freundlichen Gr&uuml;&szlig;en,<br>" +
                    "das TSS Team</body></html>");
            this.put("en", "<br>" +
                    "Best regards,<br>" +
                    "the TSS Team</body></html>");
        }
    };
    private static final HashMap<String, String> progress = new HashMap<String, String>() {
        {
            this.put("de", "<br>" +
                    "Wir m&ouml;chten Sie daran erinnern, dass Ihr Stundenzettel %s noch nicht vollst&auml;ndig ausgefüllt oder von Ihnen unterschrieben wurde.<br>" +
                    "Bitte holen Sie dies zeitnah nach.<br>");
            this.put("en", "<br>" +
                    "We would like to remind you, that your Timesheet %s is still incomplete or missing your signature.<br>" +
                    "Please finish your Timesheet soon.<br>");
        }
    };
    private static final HashMap<String, String> signedEmp = new HashMap<String, String>() {
        {
            this.put("de", "<br>" +
                    "Wir m&ouml;chten Sie dar&uuml;ber informieren, dass der Stundenzettel %s von %s bereit zur Abnahme ist.<br>" +
                    "Bitte lehnen Sie den Stundenzettel zeitnah an oder ab.<br>");
            this.put("en", "<br>" +
                    "We would like to inform you, that the Timesheet %s from %s is ready for inspection.<br>" +
                    "Please accept or decline the Timesheet.<br>");
        }
    };
    private static final HashMap<String, String> signedSup = new HashMap<String, String>() {
        {
            this.put("de", "<br>" +
                    "Wir m&ouml;chten Sie dar&uuml;ber informieren, dass der Stundenzettel %s von %s bereit zur Archivierung ist.<br>");
            this.put("en", "<br>" +
                    "We would like to inform you, that the Timesheet %s from %s is ready to be archived.<br>");
        }
    };
    public static final Map<TimeSheetStatus, HashMap> message = new EnumMap<TimeSheetStatus, HashMap>(TimeSheetStatus.class) {
        {
            this.put(TimeSheetStatus.IN_PROGESS, progress);
            this.put(TimeSheetStatus.SIGNED_BY_EMPLOYEE, signedEmp);
            this.put(TimeSheetStatus.SIGNED_BY_SUPERVISOR, signedSup);
        }
    };

}
