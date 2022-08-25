package DatabaseAccess;
import main.ConnectDB;
import model.Appointment;
import model.Reports;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Accesses reports from database
 * @author Andre Simmons
 */
public class ReportsDatabaseAccess extends Appointment{
    /**
     * Constructs a Report with the proper data
     * @param appointment_ID
     * @param title
     * @param description
     * @param location
     * @param type
     * @param startTime
     * @param endTime
     * @param customer_ID
     * @param user_ID
     * @param contactName
     */
    public ReportsDatabaseAccess(int appointment_ID, String title, String description, String location, String type, LocalDateTime startTime, LocalDateTime endTime, int customer_ID, int user_ID, String contactName) {
        super(appointment_ID, title, description, location, title, startTime, endTime, customer_ID, user_ID, contactName);
    }

    /**
     * Pulls data to create reports including country and customers by country
     * @throws SQLException
     * @return countriesObservableList
     */
    public static ObservableList<Reports> loadDivisionsToFilter() throws SQLException {
        ObservableList<Reports> allCountries = FXCollections.observableArrayList();
        String sqlData = "SELECT FLDivisions.Division, COUNT(*) AS divisionCount FROM Customers INNER JOIN FLDivisions ON Customers.Division = FLDivisions.Division WHERE FLDivisions.Division_ID = Customers.Division_ID GROUP BY FLDivisions.Division_ID ORDER BY COUNT(*) DESC";
        PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sqlData);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String name = rs.getString("Division");
            int divisionCount = rs.getInt("divisionCount");
            Reports r = new Reports(name, divisionCount);
            allCountries.add(r);

        }
        return allCountries;
    }
}
