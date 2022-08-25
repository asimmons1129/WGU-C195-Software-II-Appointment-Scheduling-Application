package DatabaseAccess;
import main.ConnectDB;
import model.Appointment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Accesses appointments in database
 * @author Andre Simmons
 */
public class AppointmentDatabaseAccess {
    /**
     * Creates an observable list that contains all appointments from the database
     * Converts UTC Time from database to the time based on local time zon
     * @throws SQLException
     * @return appointmentsObservableList
     */
    public static ObservableList<Appointment> loadAllAppointments() throws SQLException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        String sql = "SELECT * from Appointments";
        PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int appointmentIDSQL = rs.getInt("AppointmentID");
            String appointmentTitleSQL = rs.getString("Title");
            String appointmentDescriptionSQL = rs.getString("Description");
            String appointmentLocationSQL = rs.getString("Location");
            String appointmentTypeSQL = rs.getString("Type");
            LocalDateTime startTimeSQL = rs.getTimestamp("Start").toLocalDateTime();
            LocalDateTime endTimeSQL = rs.getTimestamp("End").toLocalDateTime();
            ZonedDateTime startTime = startTimeSQL.atZone(ZoneId.of("UTC"));
            ZonedDateTime endTime = endTimeSQL.atZone(ZoneId.of("UTC"));
            ZonedDateTime convertStart = startTime.withZoneSameInstant(ZoneId.systemDefault());
            ZonedDateTime convertEnd = endTime.withZoneSameInstant(ZoneId.systemDefault());
            LocalDateTime newStart = convertStart.toLocalDateTime();
            LocalDateTime newEnd = convertEnd.toLocalDateTime();
            int customerIDSQL = rs.getInt("CustomerID");
            int userIDSQL = rs.getInt("UserID");
            String contactSQL = rs.getString("Contact");
            Appointment a = new Appointment(appointmentIDSQL, appointmentTitleSQL, appointmentDescriptionSQL, appointmentLocationSQL, appointmentTypeSQL, newStart, newEnd, customerIDSQL, userIDSQL, contactSQL);
            allAppointments.add(a);
        }
        return allAppointments;
    }

    /**
     * Deletes an appointment based on it's ID
     * @param customerCount
     * @param c
     * @return result
     * @throws SQLException
     */
    public static int appointmentDelete(int customerCount, Connection c) throws SQLException {
        String query = "DELETE FROM Appointments WHERE AppointmentID=?";
        PreparedStatement ps = c.prepareStatement(query);
        ps.setInt(1, customerCount);
        int result = ps.executeUpdate();
        ps.close();
        return result;
    }
}
