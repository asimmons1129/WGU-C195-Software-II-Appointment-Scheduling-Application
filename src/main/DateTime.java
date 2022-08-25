package main;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Creates Date Object used for timezone conversion
 * @author Andre Simmons
 */
public class DateTime {
    /**
     * Function that converts the current time to UTC
     * @param dateTime
     * @return
     */
    public static String UTCLocalDTConversion(String dateTime) {
        Timestamp timeCurrently = Timestamp.valueOf(String.valueOf(dateTime));
        LocalDateTime currentLocalDateTime = timeCurrently.toLocalDateTime();
        ZonedDateTime currentZoneDateTime = currentLocalDateTime.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
        ZonedDateTime currentUTCDateTime = currentZoneDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime currentLocalDateTimeOutput = currentUTCDateTime.toLocalDateTime();
        String UTCOutput = currentLocalDateTimeOutput.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return UTCOutput;
    }
}
