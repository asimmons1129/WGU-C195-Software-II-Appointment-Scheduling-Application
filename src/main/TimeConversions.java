package main;

import java.time.*;

/**
 * Time conversions for the application
 * @author Andre Simmons
 */
public class TimeConversions {
    /**
     * Converts time variable to eastern time
     * Used to test if appointment is outside of business hours
     * @param date
     * @param time
     * @return
     */
    public static LocalTime convertToEasternTime(LocalDate date, LocalTime time){
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime convertedZone = zonedDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        LocalTime easternTime = convertedZone.toLocalTime();
        return easternTime;
    }

    /**
     * Converts date variable to eastern time zone
     * Used to test if appointment falls on a weekend day
     * @param date
     * @param time
     * @return
     */
    public static LocalDate convertToEasternDate(LocalDate date, LocalTime time){
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime convertedZone = zonedDateTime.withZoneSameInstant(ZoneId.of("America/New_York"));
        LocalDate easternDate = convertedZone.toLocalDate();
        return easternDate;
    }
}
