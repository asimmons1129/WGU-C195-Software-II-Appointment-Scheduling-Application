package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Pulls data from each database table
 * @author Andre Simmons
 */
public class DatabasePull {
    /**
     * loads all appointments from database into a list
     * @return
     * @throws SQLException
     */
    public static ObservableList<Appointment> getAppointmentsFromDatabase() throws SQLException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        Connection conn = ConnectDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT appointments.Appointment_ID, appointments.Title, appointments.Description, appointments.Location, appointments.Type, appointments.Start, appointments.End, appointments.Customer_ID, appointments.User_ID, appointments.Contact_ID, contacts.Contact_Name FROM appointments INNER JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int id = resultSet.getInt("Appointment_ID");
            String title = resultSet.getString("Title");
            String type = resultSet.getString("Type");
            String description = resultSet.getString("Description");
            String location = resultSet.getString("Location");
            LocalDateTime start = resultSet.getTimestamp("Start").toLocalDateTime();
            LocalDateTime end = resultSet.getTimestamp("End").toLocalDateTime();
            int customerId = resultSet.getInt("Customer_ID");
            int userId = resultSet.getInt("User_ID");
            int contactId = resultSet.getInt("Contact_ID");
            String contactName = IDNameConversions.convertContactIDToName(contactId);
            Appointment currentAppointment = new Appointment(id, title, description, location, type, customerId, userId, contactName, contactId, start, end);
            allAppointments.add(currentAppointment);
        }
        return allAppointments;
    }

    /**
     * loads all contacts from database into a list
     * @return
     * @throws SQLException
     */
    public static ObservableList<Contact> getContactsFromDatabase() throws SQLException{
        ObservableList<Contact> allContacts = FXCollections.observableArrayList();
        Connection conn = ConnectDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM contacts");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int id = resultSet.getInt("Contact_ID");
            String name = resultSet.getString("Contact_Name");
            String email = resultSet.getString("Email");
            Contact currentContact = new Contact(id, name, email);
            allContacts.add(currentContact);
        }
        return allContacts;
    }

    /**
     * loads all countries from database into a list
     * @return
     * @throws SQLException
     */
    public static ObservableList<Country> getCountriesFromDatabase() throws SQLException{
        ObservableList<Country> allCountries = FXCollections.observableArrayList();
        Connection conn = ConnectDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM countries");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int id = resultSet.getInt("Country_ID");
            String name = resultSet.getString("Country");
            Country currentCountry = new Country(id, name);
            allCountries.add(currentCountry);
        }
        return allCountries;
    }

    /**
     * loads all customers from database into a list
     * @return
     * @throws SQLException
     */
    public static ObservableList<Customer> getCustomersFromDatabase() throws SQLException{
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
        Connection conn = ConnectDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM customers");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            int id = resultSet.getInt("Customer_ID");
            String name = resultSet.getString("Customer_Name");
            String address = resultSet.getString("Address");
            String postalCode = resultSet.getString("Postal_Code");
            String phone = resultSet.getString("Phone");
            int divisionId = resultSet.getInt("Division_ID");
            String divisionName = IDNameConversions.convertDivisionIDToName(divisionId);
            Customer currentCustomer = new Customer(id, name, address, postalCode, phone, divisionId, divisionName);
            allCustomers.add(currentCustomer);
        }
        return allCustomers;
    }

    /**
     * loads all first level divisions from database into a list
     * @return
     * @throws SQLException
     */
    public static ObservableList<FirstLevelDivision> getDivisionsFromDatabase() throws SQLException{
        ObservableList<FirstLevelDivision> allFirstLevelDivisions = FXCollections.observableArrayList();
        Connection conn = ConnectDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM first_level_divisions");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("Division_ID");
            String name = resultSet.getString("Division");
            int countryId = resultSet.getInt("Country_ID");
            FirstLevelDivision currentDivision = new FirstLevelDivision(id, name, countryId);
            allFirstLevelDivisions.add(currentDivision);
        }
        return allFirstLevelDivisions;
    }

    /**
     * loads all users from database into a list
     * @return
     * @throws SQLException
     */
    public static ObservableList<User> getUsersFromDatabase() throws SQLException {
        ObservableList<User> allUsers = FXCollections.observableArrayList();
        Connection conn = ConnectDB.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM users");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("User_ID");
            String username = resultSet.getString("User_Name");
            String password = resultSet.getString("Password");
            User currentUser = new User(id, username, password);
            allUsers.add(currentUser);
        }
        return allUsers;
    }


}
