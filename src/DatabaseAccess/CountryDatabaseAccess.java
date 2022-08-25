package DatabaseAccess;
import model.Country;
import main.ConnectDB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Accesses country information in database
 */
public class CountryDatabaseAccess extends Country {
    public CountryDatabaseAccess(int country_ID, String nameOFCountry) {
        super(country_ID, nameOFCountry);
    }

    /**
     * Creates an observable list containing country and country ID
     * @throws SQLException
     * @return countriesObservableList
     */
    public static ObservableList<CountryDatabaseAccess> loadAllCountries() throws SQLException {
        ObservableList<CountryDatabaseAccess> allCountries = FXCollections.observableArrayList();
        String sqlData = "SELECT Country_ID, Country from Countries";
        PreparedStatement ps = ConnectDB.getConnection().prepareStatement(sqlData);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int countryID = rs.getInt("Country_ID");
            String name = rs.getString("Country");
            CountryDatabaseAccess country = new CountryDatabaseAccess(countryID, name);
            allCountries.add(country);
        }
        return allCountries;
    }
}
