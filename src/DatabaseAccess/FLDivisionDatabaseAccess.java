package DatabaseAccess;
import main.ConnectDB;
import model.FLDivision;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Accesses all First Level Division info from database
 * @author Andre Simmons
 */
public class FLDivisionDatabaseAccess extends FLDivision{
    public FLDivisionDatabaseAccess(int division_ID, String divisionName, int country_ID) {
        super(division_ID, divisionName, country_ID);
    }

    /**
     * Creates observable list containing all first level division info
     * @throws SQLException
     * @return firstLevelDivisionsObservableList
     */
    public static ObservableList<FLDivisionDatabaseAccess> loadFLDivisions() throws SQLException {
        ObservableList<FLDivisionDatabaseAccess> allFLDivisions = FXCollections.observableArrayList();
        String sqlData = "SELECT * from FLDivisions";
        PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sqlData);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");
            int country_ID = rs.getInt("Country_ID");
            FLDivisionDatabaseAccess flDivision = new FLDivisionDatabaseAccess(divisionID, divisionName, country_ID);
            allFLDivisions.add(flDivision);
        }
        return allFLDivisions;
    }

}
