package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.ConnectDB;
import main.DatabasePull;
import main.IDNameConversions;
import model.Country;
import model.FirstLevelDivision;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Controls the AddCustomer screen
 * @author Andre Simmons
 */
public class AddCustomer {
    @FXML private TextField customerId;
    @FXML private TextField name;
    @FXML private TextField address;
    @FXML private TextField phoneNumber;
    @FXML private ComboBox<String> country;
    @FXML private ComboBox<String> division;
    @FXML private TextField postalCode;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    /**
     * Initializes the screen and fills the appropriate combo boxes (country, division)
     * Conatins lambda function that handles the action events for both the save and cancel buttons
     * Lambda function accurately assigns the correct function to each button with the appropriate exceptions
     * Removes the need to manually assign the function in scene builder
     * @throws SQLException
     */
    public void initialize() throws SQLException {
        ObservableList<Country> listOfCountries = DatabasePull.getCountriesFromDatabase();
        ObservableList<String> countryNamesList = FXCollections.observableArrayList();
        for(Country c: listOfCountries){
            countryNamesList.add(c.getName());
        }
        country.setItems(countryNamesList);
        /*
        Lambda expression to handle click event for the save button
         */
        saveButton.setOnAction(actionEvent -> {
            try {
                saveOnClick(actionEvent);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cancelButton.setOnAction(actionEvent -> {
            try {
                cancelOnClick(actionEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Loads the first level divisions to the combo box based on user selection
     * @param actionEvent
     * @throws SQLException
     */
    @FXML
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
     * Customer can't be added if any fields are empty
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
     * Adds new customer to the customers table in the database
     * Displays error message if all fields are not filled
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    public void saveOnClick(ActionEvent actionEvent) throws SQLException, IOException {
        if(allFieldsFilled()){
            int customerId = (int) (Math.random() *100);
            Connection conn = ConnectDB.getConnection();
            ConnectDB.setPreparedStatement(conn, "INSERT INTO customers (Customer_ID, Customer_Name, Address, Phone, Postal_Code, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement preparedStatement = ConnectDB.getPreparedStatement();
            preparedStatement.setInt(1, customerId);
            preparedStatement.setString(2, name.getText());
            preparedStatement.setString(3, address.getText());
            preparedStatement.setString(4, phoneNumber.getText());
            preparedStatement.setString(5, postalCode.getText());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(7, "admin");
            preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(9, "admin");
            preparedStatement.setInt(10, IDNameConversions.convertDivisionNameToId(division.getValue()));
            preparedStatement.execute();

            Parent parent = FXMLLoader.load(getClass().getResource("../views/MainConsole.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("A customer with ID: " + customerId + " was successfully added");
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
     * loads the main console screen when clicked
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    public void cancelOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../views/MainConsole.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


}
