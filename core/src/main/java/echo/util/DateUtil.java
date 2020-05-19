package echo.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {
    private DateUtil() {
    }

    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate toLocalDate(Date date) {
        return LocalDate.from(Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()));
    }

    /**
     * Checks if the toCheck Date is in the closed date interval between start and end Range.
     *
     * @param toCheck
     * @param startRange
     * @param endRange
     * @return true if the toCheck date is in the interval
     */
    public static boolean isBetweenClosed(LocalDate toCheck, LocalDate startRange, LocalDate endRange) {
        if (toCheck.isEqual(startRange) || toCheck.isEqual(endRange)) {
            return true;
        }

        return toCheck.isAfter(startRange) && toCheck.isBefore(endRange);
    }
}
