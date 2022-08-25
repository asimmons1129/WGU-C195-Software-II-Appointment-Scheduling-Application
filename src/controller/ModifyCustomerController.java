package controller;

import DatabaseAccess.CountryDatabaseAccess;
import DatabaseAccess.FLDivisionDatabaseAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.ConnectDB;
import model.Country;
import model.Customer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Controls modify customer fxml
 * @author Andre Simmons
 */
public class ModifyCustomerController {
    @FXML private TextField customerIDField;
    @FXML private TextField customerNameField;
    @FXML private TextField customerPhoneField;
    @FXML private TextField customerPostalCodeField;
    @FXML private ComboBox<String> customerState;
    @FXML private ComboBox<String> customerCountry;
    @FXML private TextField customerAddressField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    /**
     * Initializes modify customer screen
     * Loads information into proper fields
     * @throws SQLException
     */
    public void initialize() throws SQLException {
        ConnectDB.startConnection();
        Customer selectedCustomer = CustomerController.getSelectedCustomerModify();
        String USSelect = "US";
        String UKSelect = "UK";
        String CanadaSelect = "Canada";
        if (selectedCustomer != null) {
            ObservableList<CountryDatabaseAccess> getAllCountries = CountryDatabaseAccess.loadAllCountries();
            ObservableList<String> allFLDivisionCountry = FXCollections.observableArrayList();
            getAllCountries.stream().map(Country::getCountryName).forEach(allFLDivisionCountry::add);
            customerCountry.setItems(allFLDivisionCountry);
            customerIDField.setText(String.valueOf((selectedCustomer.getCustomerID())));
            customerNameField.setText(selectedCustomer.getCustomerName());
            customerAddressField.setText(selectedCustomer.getCustomerAddress());
            customerPostalCodeField.setText(selectedCustomer.getCustomerPostalCode());
            customerPhoneField.setText(selectedCustomer.getCustomerPhone());
            customerState.setValue(selectedCustomer.getDivisionName());
            if(selectedCustomer.getCustomerDivisionID() >=1 && selectedCustomer.getCustomerDivisionID() <=50){
                customerCountry.setValue(USSelect);
            }else if (selectedCustomer.getCustomerDivisionID() >=51 && selectedCustomer.getCustomerDivisionID() <=69){
                customerCountry.setValue(UKSelect);
            }else if (selectedCustomer.getCustomerDivisionID() >=70 && selectedCustomer.getCustomerDivisionID() <=82){
                customerCountry.setValue(CanadaSelect);
            }
        }
    }

    /**
     * Controls save button
     * Saves updated customer information
     * @param event
     */
    @FXML
    void saveUpdatedCustomer(ActionEvent event)  {
        try {
            Connection connection = ConnectDB.startConnection();
            if (!customerNameField.getText().isEmpty() && !customerAddressField.getText().isEmpty() && !customerPostalCodeField.getText().isEmpty() && !customerPhoneField.getText().isEmpty() && !customerCountry.getValue().isEmpty() && !customerState.getValue().isEmpty())
            {
                int flDivisionName = 0;
                for (FLDivisionDatabaseAccess flDivison : FLDivisionDatabaseAccess.loadFLDivisions()) {
                    if (customerState.getSelectionModel().getSelectedItem().equals(flDivison.getDivisionName())) {
                        flDivisionName = flDivison.getDivisionID();
                    }
                }

                /**
                 * Updates customer information in appropriate database table
                 */
                String insertStatement = "UPDATE Customers SET Customer_ID = ?, Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ?, Division = ? WHERE Customer_ID = ?";
                ConnectDB.setPreparedStatement(ConnectDB.getConnection(), insertStatement);
                PreparedStatement ps = ConnectDB.getPreparedStatement();
                ps.setInt(1, Integer.parseInt(customerIDField.getText()));
                ps.setString(2, customerNameField.getText());
                ps.setString(3, customerAddressField.getText());
                ps.setString(4, customerPostalCodeField.getText());
                ps.setString(5, customerPhoneField.getText());
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(7, "admin");
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setInt(10, flDivisionName);
                ps.setString(11, customerState.getValue());
                ps.setInt(12, Integer.parseInt(customerIDField.getText()));
                ps.execute();

                /**
                 * Returns to the customer screen when clicked
                 */
                Parent root = FXMLLoader.load(getClass().getResource("../view/Customer.fxml"));
                Scene scene = new Scene(root);
                Stage MainScreenReturn = (Stage) ((Node) event.getSource()).getScene().getWindow();
                MainScreenReturn.setScene(scene);
                MainScreenReturn.show();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "The customer was successfully updated!");
                Optional<ButtonType> confirmation = alert.showAndWait();
                System.out.println("Customer successfully updated.");
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
     * Controls cancel button
     * Returns to main customer screen when clicked
     * @param event
     * @throws IOException
     */
    @FXML
    public void cancelModifyCustomer(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../view/Customer.fxml"));
        Scene scene = new Scene(root);
        Stage MainScreenReturn = (Stage) ((Node) event.getSource()).getScene().getWindow();
        MainScreenReturn.setScene(scene);
        MainScreenReturn.show();
    }
}
