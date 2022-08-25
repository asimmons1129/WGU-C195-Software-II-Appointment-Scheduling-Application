package controller;

import DatabaseAccess.AppointmentDatabaseAccess;
import DatabaseAccess.CountryDatabaseAccess;
import DatabaseAccess.CustomerDatabaseAccess;
import DatabaseAccess.FLDivisionDatabaseAccess;
import DatabaseAccess.CountryDatabaseAccess;
import DatabaseAccess.CustomerDatabaseAccess;
import DatabaseAccess.FLDivisionDatabaseAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.ConnectDB;
import main.ConnectDB;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controls customer fxml
 * @author Andre Simmons
 */
public class CustomerController implements Initializable {
    @FXML private TableColumn<?, ?> customerNameColumn;
    @FXML private Button customerAddButton;
    @FXML private Button customerBackButton;
    @FXML private Button customerEditButton;
    @FXML private Button customerDeleteButton;
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<?, ?> customerAddressColumn;
    @FXML private TableColumn<?, ?> customerIDColumn;
    @FXML private TableColumn<?, ?> customerPhoneColumn;
    @FXML private TableColumn<?, ?> customerPostalCodeColumn;
    @FXML private TableColumn<?, ?> customerStateColumn;
    @FXML private TextField customerIDField;
    @FXML private TextField customerNameField;
    @FXML private TextField customerPhoneField;
    @FXML private TextField customerPostalField;
    @FXML private ComboBox<String> customerState;
    @FXML private ComboBox<String> customerCountry;
    @FXML private TextField customerAddressField;

    private static Customer selectedCustomerModify;
    public static Customer getSelectedCustomerModify(){ return selectedCustomerModify;}



    /**
     * Initialize the controller
     * Lambda function fills the appropriate observable list with the first level division names from the database
     * @param url
     * @param resourceBundle
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Connection connection = ConnectDB.startConnection();

            ObservableList<CountryDatabaseAccess> allCountries = CountryDatabaseAccess.loadAllCountries();
            ObservableList<String> countryNames = FXCollections.observableArrayList();
            ObservableList<FLDivisionDatabaseAccess> allFirstLevelDivisions = FLDivisionDatabaseAccess.loadFLDivisions();
            ObservableList<String> firstLevelDivisionAllNames = FXCollections.observableArrayList();
            ObservableList<Customer> allCustomersList = CustomerDatabaseAccess.loadAllCustomers(connection);

            customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
            customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
            customerPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("customerPostalCode"));
            customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
            customerStateColumn.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

            allCountries.stream().map(Country::getCountryName).forEach(countryNames::add);
            customerCountry.setItems(countryNames);

            /*
             * lambda function that adds first level division names to observable list
             * eventually used to populate customerState combo box
             */
            allFirstLevelDivisions.forEach(firstLevelDivision -> firstLevelDivisionAllNames.add(firstLevelDivision.getDivisionName()));

            customerState.setItems(firstLevelDivisionAllNames);
            customersTable.setItems(allCustomersList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes selected customer along with associated appointments
     * Verifies if a customer is selected
     * Confirms with user before deletion
     * Displays confirmation message after customer is deleted
     * @throws Exception
     * @param event
     */
    @FXML
    void deleteCustomer(ActionEvent event) throws Exception {
        selectedCustomerModify = customersTable.getSelectionModel().getSelectedItem();
        if(selectedCustomerModify == null){
            Alert alertError = new Alert(Alert.AlertType.ERROR);
            alertError.setTitle("Error");
            alertError.setHeaderText("No Customer Selected! Please select a customer");
            alertError.showAndWait();
        }
        else {
            Connection connection = ConnectDB.startConnection();
            ObservableList<Appointment> getAllAppointmentsList = AppointmentDatabaseAccess.loadAllAppointments();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure that you want to delete the selected customer and all appointments? ");
            Optional<ButtonType> confirmation = alert.showAndWait();
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                int deleteCustomerID = customersTable.getSelectionModel().getSelectedItem().getCustomerID();
                AppointmentDatabaseAccess.appointmentDelete(deleteCustomerID, connection);

                /**
                 * Deletes customer from appropriate table in database
                 */
                String sqlDelete = "DELETE FROM customers WHERE Customer_ID = ?";
                ConnectDB.setPreparedStatement(ConnectDB.getConnection(), sqlDelete);

                PreparedStatement psDelete = ConnectDB.getPreparedStatement();
                int customerFromTable = customersTable.getSelectionModel().getSelectedItem().getCustomerID();

                /**
                 * Deletes appointments associated with selected customer from database
                 */
                for (Appointment appointment : getAllAppointmentsList) {
                    int customerFromAppointments = appointment.getCustomerID();
                    if (customerFromTable == customerFromAppointments) {
                        String deleteStatementAppointments = "DELETE FROM appointments WHERE Appointment_ID = ?";
                        ConnectDB.setPreparedStatement(ConnectDB.getConnection(), deleteStatementAppointments);
                    }
                }
                psDelete.setInt(1, customerFromTable);
                psDelete.execute();
                ObservableList<Customer> refreshCustomersList = CustomerDatabaseAccess.loadAllCustomers(connection);
                customersTable.setItems(refreshCustomersList);
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION, "The customer with customer ID: " + selectedCustomerModify.getCustomerID() + " has been successfully deleted.");
                Optional<ButtonType> c2 = alert2.showAndWait();
                System.out.println("The customer has been deleted");
            }
        }
    }

    /**
     * Controls edit button
     * Loads modify customer fxml when clicked
     * If no customer selected, displays message
     * @throws SQLException
     * @param event
     */

    @FXML
    void customerEdit(ActionEvent event) throws IOException {
        selectedCustomerModify = customersTable.getSelectionModel().getSelectedItem();
        if(selectedCustomerModify == null){
            Alert alertError = new Alert(Alert.AlertType.ERROR);
            alertError.setTitle("Error");
            alertError.setHeaderText("No Customer Selected! Please select an customer");
            alertError.showAndWait();
        }
        else {
            Parent root = FXMLLoader.load(getClass().getResource("../view/ModifyCustomer.fxml"));
            Scene scene = new Scene(root);
            Stage MainScreenReturn = (Stage) ((Node) event.getSource()).getScene().getWindow();
            MainScreenReturn.setScene(scene);
            MainScreenReturn.show();
        }
    }

    /**
     * Controls the add button
     * Adds customer to the database
     * @param event
     */
    @FXML
    void customerAdd(ActionEvent event)  {
        try {
            Connection connection = ConnectDB.startConnection();
            if (!customerNameField.getText().isEmpty() && !customerAddressField.getText().isEmpty() && !customerAddressField.getText().isEmpty() && !customerPostalField.getText().isEmpty() && !customerPhoneField.getText().isEmpty() && !customerCountry.getValue().isEmpty() && !customerState.getValue().isEmpty()){
                /**
                 * Randomizes ID for new customer
                 */
                Integer newCustomerID = (int) (Math.random() * 100);
                /**
                 * Loads first level divisions
                 */
                int firstLevelDivisionName = 0;
                for (FLDivisionDatabaseAccess firstLevelDivision : FLDivisionDatabaseAccess.loadFLDivisions()) {
                    if (customerState.getSelectionModel().getSelectedItem().equals(firstLevelDivision.getDivisionName())) {
                        firstLevelDivisionName = firstLevelDivision.getDivisionID();
                    }
                }
                /**
                 * Adds customer to appropriate table in database
                 */
                String insertStatement = "INSERT INTO Customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID, Division) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
                ConnectDB.setPreparedStatement(ConnectDB.getConnection(), insertStatement);
                PreparedStatement ps = ConnectDB.getPreparedStatement();
                ps.setInt(1, newCustomerID);
                ps.setString(2, customerNameField.getText());
                ps.setString(3, customerAddressField.getText());
                ps.setString(4, customerPostalField.getText());
                ps.setString(5, customerPhoneField.getText());
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(7, "admin");
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setInt(10, firstLevelDivisionName);
                ps.setString(11, customerState.getValue());
                ps.execute();

                customerIDField.clear();
                customerNameField.clear();
                customerAddressField.clear();
                customerPostalField.clear();
                customerPhoneField.clear();

                ObservableList<Customer> refreshCustomersList = CustomerDatabaseAccess.loadAllCustomers(connection);
                customersTable.setItems(refreshCustomersList);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "The customer was successfully added!");
                Optional<ButtonType> confirmation = alert.showAndWait();
                System.out.println("Customer successfully added.");
                return;
            }
            else {
                Alert alertError = new Alert(Alert.AlertType.ERROR);
                alertError.setTitle("Error");
                alertError.setHeaderText("One or more fields are empty.  Please fill any missing fields!");
                alertError.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads first level divisions into drop down menus
     * @param event
     * @throws SQLException
     */
    public void editCountryDropDownCustomer(ActionEvent event) throws SQLException {
        try {
            ConnectDB.startConnection();
            String userCountrySelect = customerCountry.getSelectionModel().getSelectedItem();
            ObservableList<FLDivisionDatabaseAccess> loadFLDivisions = FLDivisionDatabaseAccess.loadFLDivisions();
            ObservableList<String> USDivisions = FXCollections.observableArrayList();
            ObservableList<String> UKDivisions = FXCollections.observableArrayList();
            ObservableList<String> CanadaDivisions = FXCollections.observableArrayList();

            /**
             * loads first level divisions based on country id
             */
            loadFLDivisions.forEach(FLDivision -> {
                if (FLDivision.getCountry_ID() == 01) {
                    USDivisions.add(FLDivision.getDivisionName());
                } else if (FLDivision.getCountry_ID() == 02) {
                    UKDivisions.add(FLDivision.getDivisionName());
                } else if (FLDivision.getCountry_ID() == 03) {
                    CanadaDivisions.add(FLDivision.getDivisionName());
                }
            });

            /**
             * Loads appropriate states/provinces based on selected country
             */
            if (userCountrySelect.equals("US")) {
                customerState.setItems(USDivisions);
            }
            else if (userCountrySelect.equals("UK")) {
                customerState.setItems(UKDivisions);
            }
            else if (userCountrySelect.equals("Canada")) {
                customerState.setItems(CanadaDivisions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns to the Main Screen when the Back button is clicked
     * @throws IOException
     */

    @FXML
    public void customerCancel(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../view/MainScreen.fxml"));
        Scene scene = new Scene(root);
        Stage MainScreenReturn = (Stage) ((Node) event.getSource()).getScene().getWindow();
        MainScreenReturn.setScene(scene);
        MainScreenReturn.show();
    }
}
