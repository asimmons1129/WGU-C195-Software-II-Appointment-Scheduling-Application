package main;

import javafx.collections.ObservableList;
import model.Contact;
import model.Country;
import model.FirstLevelDivision;

import java.sql.SQLException;

/**
 * Does various name-to-id/id-to-name conversions
 * @author Andre Simmons
 */
public class IDNameConversions {
    /**
     * Converts a given contact id into contact name
     * Used to display the contact name in the appointments table of the main console
     * @param contactID
     * @return
     * @throws SQLException
     */
    public static String convertContactIDToName(int contactID) throws SQLException {
        String contactName = "";
        ObservableList<Contact> allContacts = DatabasePull.getContactsFromDatabase();
        for (Contact c: allContacts){
            if(c.getId() == contactID){
                contactName = c.getName();
            }
        }
        return contactName;
    }

    /**
     * Converts a given contact name to a contact id
     * Used to store appointment info in the database since the table requires contact id
     * @param contactName
     * @return
     * @throws SQLException
     */
    public static int convertContactNameToID(String contactName) throws SQLException{
        int contactID = 0;
        ObservableList<Contact> allContacts = DatabasePull.getContactsFromDatabase();
        for (Contact c: allContacts){
            if(c.getName().equals(contactName)){
                contactID = c.getId();
            }
        }
        return contactID;
    }

    /**
     * Converts a given division id to division name
     * Used to display division name in the customers table of the main console
     * @param divisionID
     * @return
     * @throws SQLException
     */
    public static String convertDivisionIDToName(int divisionID) throws SQLException{
        String divisionName = "";
        ObservableList<FirstLevelDivision> allFirstLevelDivisions = DatabasePull.getDivisionsFromDatabase();
        for(FirstLevelDivision f: allFirstLevelDivisions){
            if(f.getId() == divisionID){
                divisionName = f.getName();
            }
        }
        return divisionName;
    }

    /**
     * Converts a given division name to division id
     * Used to store division id in the customers table of the database
     * @param divisionName
     * @return
     * @throws SQLException
     */
    public static int convertDivisionNameToId(String divisionName) throws SQLException{
        int divisionId = 0;
        ObservableList<FirstLevelDivision> allFirstLevelDivisions = DatabasePull.getDivisionsFromDatabase();
        for(FirstLevelDivision f: allFirstLevelDivisions){
            if(f.getName().equals(divisionName)){
                divisionId = f.getId();
            }
        }
        return divisionId;
    }

    /**
     * Converts given country name to country id
     * Used as a conversion tool in the function that controls the customer country dropdown
     * @param countryName
     * @return
     * @throws SQLException
     */
    public static int convertCountryNameToID(String countryName) throws SQLException{
        int countryId = 0;
        ObservableList<Country> allCountries = DatabasePull.getCountriesFromDatabase();
        for(Country c: allCountries){
            if(c.getName().equals(countryName)){
                countryId = c.getId();
            }
        }
        return countryId;
    }


}
