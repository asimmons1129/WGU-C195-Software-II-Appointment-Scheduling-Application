package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import main.ConnectDB;
import main.DatabasePull;
import main.IDNameConversions;
import model.Country;
import model.Customer;
import model.FirstLevelDivision;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Controls the modify customer screen
 * @author Andre Simmons
 */
public class ModifyCustomer {
    @FXML private TextField customerId;
    @FXML private TextField name;
    @FXML private TextField address;
    @FXML private TextField phoneNumber;
    @FXML private ComboBox<String> country;
    @FXML private ComboBox<String> division;
    @FXML private TextField postalCode;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    private static Customer select;

    /**
     * Initializes the screen
     * Loads all information into appropriate fields for the selected customer
     * Loads first level divisions into the division combo box based on the selected customer's country
     * @throws SQLException
     */
    public void initialize() throws SQLException{
        select = MainConsole.getSelectedCustomer();
        ObservableList<Country> listOfCountries = DatabasePull.getCountriesFromDatabase();
        ObservableList<String> countryNamesList = FXCollections.observableArrayList();
        for(Country c: listOfCountries){
            countryNamesList.add(c.getName());
        }
        country.setItems(countryNamesList);
        ObservableList<FirstLevelDivision> listOfDivisions = DatabasePull.getDivisionsFromDatabase();
        ObservableList<String> USDivisionsList = FXCollections.observableArrayList();
        ObservableList<String> UKDivisionsList = FXCollections.observableArrayList();
        ObservableList<String> CanadaDivisionsList = FXCollections.observableArrayList();
        ObservableList<Integer> USDivisionIDList = FXCollections.observableArrayList();
        ObservableList<Integer> UKDivisionIDList = FXCollections.observableArrayList();
        ObservableList<Integer> CanadaDivisionIDList = FXCollections.observableArrayList();
        for(FirstLevelDivision f: listOfDivisions){
            if(f.getCountryId() == 1){
                USDivisionsList.add(f.getName());
                USDivisionIDList.add(f.getId());
            }
            else if(f.getCountryId() == 2){
                UKDivisionsList.add(f.getName());
                UKDivisionIDList.add(f.getId());
            }
            else if(f.getCountryId() == 3){
                CanadaDivisionsList.add(f.getName());
                CanadaDivisionIDList.add(f.getId());
            }
        }
        if(USDivisionIDList.contains(select.getDivisionId())){
            country.setValue("US");
            division.setItems(USDivisionsList);
        }
        else if(UKDivisionIDList.contains(select.getDivisionId())) {
            country.setValue("UK");
            division.setItems(UKDivisionsList);
        }
        else if(CanadaDivisionIDList.contains(select.getDivisionId())) {
            country.setValue("Canada");
            division.setItems(CanadaDivisionsList);
        }
        customerId.setText(String.valueOf(select.getId()));
        name.setText(select.getName());
        address.setText(select.getAddress());
        phoneNumber.setText(select.getPhone());
        division.setValue(select.getDivisionName());
        postalCode.setText(select.getPostalCode());
    }

    /**
     * Loads appropriate first level divisions into the divisions combo box based on the country selection
     * @param actionEvent
     * @throws SQLException
     */
    public void loadDivisions(ActionEvent actionEvent) throws SQLException{
        int countryId = 0;
        ObservableList<Country> listOfCountries = DatabasePull.getCountriesFromDatabase();
        ObservableList<FirstLevelDivision> listOfDivisions = DatabasePull.getDivisionsFromDatabase();
        ObservableList<String> USDivisionsList = FXCollections.observableArrayList();
        ObservableList<String> UKDivisionsList = FXCollections.observableArrayList();
        ObservableList<String> CanadaDivisionsList = FXCollections.observableArrayList();

        for(FirstLevelDivision f: listOfDivisions){
            if(f.getCountryId() == 1){
                USDivisionsList.add(f.getName());
            }
            else if(f.getCountryId() == 2){
                UKDivisionsList.add(f.getName());
            }
            else if(f.getCountryId() == 3){
                CanadaDivisionsList.add(f.getName());
            }
        }
        String countrySelected = country.getSelectionModel().getSelectedItem();
        for(Country c: listOfCountries) {
            if(countrySelected.equals(c.getName())) {
                countryId = IDNameConversions.convertCountryNameToID(countrySelected);
                if (countryId == 1) {
                    division.setItems(USDivisionsList);
                } else if (countryId == 2) {
                    division.setItems(UKDivisionsList);
                } else if (countryId == 3) {
                    division.setItems(CanadaDivisionsList);
                }
            }
        }
    }

    /**
     * Checks if all fields are filled
     * Customer cannot be updated if all fields aren't filled
     * @return
     */
    public Boolean allFieldsFilled(){
        if(!name.getText().isEmpty() && !address.getText().isEmpty() && !phoneNumber.getText().isEmpty() && country.getValue() != null && division.getValue() != null && !postalCode.getText().isEmpty()){
            return true;
        }
        return false;
    }

    /**
     * Controls the click action for the save button
     * Displays error message if all fields are not filled
     * Updates customer in database based on customer id if save successful
     * Updates customer table in main console if save successful
     * Loads main console and displays message that the customer was successfully updated
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void saveOnClick(ActionEvent actionEvent) throws SQLException, IOException {
        if(allFieldsFilled()){
            Connection conn = ConnectDB.getConnection();
            ConnectDB.setPreparedStatement(conn, "UPDATE customers SET Customer_ID = ?, Customer_Name = ?, Address = ?, Phone = ?, Postal_Code = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?");
            PreparedStatement preparedStatement = ConnectDB.getPreparedStatement();
            preparedStatement.setInt(1, Integer.parseInt(customerId.getText()));
            preparedStatement.setString(2, name.getText());
            preparedStatement.setString(3, address.getText());
            preparedStatement.setString(4, phoneNumber.getText());
            preparedStatement.setString(5, postalCode.getText());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(7, "admin");
            preparedStatement.setInt(8, IDNameConversions.convertDivisionNameToId(division.getValue()));
            preparedStatement.setInt(9, Integer.parseInt(customerId.getText()));
            preparedStatement.execute();

            Parent parent = FXMLLoader.load(getClass().getResource("../views/MainConsole.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("The customer with ID: " + customerId.getText() + " was successfully updated");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("One or more fields empty!");
            alert.setContentText("Please fill out all required fields");
            alert.showAndWait();
        }
    }

    /**
     * Controls the click action for the cancel button
     * Loads the main console screen and doesn't update the customer when clicked
     * @param actionEvent
     * @throws IOException
     */
    public void cancelOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../views/MainConsole.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
