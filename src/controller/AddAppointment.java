package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.ConnectDB;
import main.DatabasePull;
import main.IDNameConversions;
import main.TimeConversions;
import model.Appointment;
import model.Contact;
import model.Customer;
import model.User;

import java.awt.*;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Controls the AddAppointment screen
 * @author Andre Simmons
 */
public class AddAppointment {
    @FXML private TextField appointmentId;
    @FXML private TextField appointmentTitle;
    @FXML private TextField appointmentType;
    @FXML private TextField appointmentDescription;
    @FXML private TextField appointmentLocation;
    @FXML private DatePicker appointmentStartDate;
    @FXML private ComboBox<LocalTime> appointmentStartTime;
    @FXML private DatePicker appointmentEndDate;
    @FXML private ComboBox<LocalTime> appointmentEndTime;
    @FXML private ComboBox<Integer> customerId;
    @FXML private ComboBox<Integer> userId;
    @FXML private ComboBox<String> appointmentContact;
    @FXML private Button appointmentSaveButton;
    @FXML private Button appointmentCancelButton;

    /**
     * Initializes the form
     * Adds appropriate values to each combo box (start/end times, contacts, user/customer id's)
     * @throws SQLException
     */
    public void initialize() throws SQLException{
        ObservableList<Customer> listOfCustomers = DatabasePull.getCustomersFromDatabase();
        ObservableList<Integer> customerIDList = FXCollections.observableArrayList();
        for(Customer c: listOfCustomers){
            customerIDList.add(c.getId());
        }
        ObservableList<User> listOfUsers = DatabasePull.getUsersFromDatabase();
        ObservableList<Integer> userIdList = FXCollections.observableArrayList();
        for(User u: listOfUsers){
            userIdList.add(u.getId());
        }
        ObservableList<Contact> listOfContacts = DatabasePull.getContactsFromDatabase();
        ObservableList<String> contactNamesList = FXCollections.observableArrayList();
        for(Contact c: listOfContacts){
            contactNamesList.add(c.getName());
        }
        customerId.setItems(customerIDList);
        userId.setItems(userIdList);
        appointmentContact.setItems(contactNamesList);

        ObservableList<LocalTime> appointmentTimes = FXCollections.observableArrayList();
        LocalTime firstTime = LocalTime.MIN.plusHours(1);
        LocalTime lastTime = LocalTime.MAX.minusHours(1);
        while(firstTime.isBefore(lastTime)){
            appointmentTimes.add(firstTime);
            firstTime = firstTime.plusMinutes(15);
        }
        appointmentStartTime.setItems(appointmentTimes);
        appointmentEndTime.setItems(appointmentTimes);
    }

    /**
     * Contains all custom error checks for this screen
     * Errors selected based on appropriate scenarios
     * @param alertSelected
     */
    public void displayAlert(int alertSelected){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        switch(alertSelected) {
            case 1:
                alert.setTitle("Error");
                alert.setHeaderText("Selected time is outside of business operations!");
                alert.setContentText("Business operation times are from 8am-10pm EST");
                alert.showAndWait();
                break;
            case 2:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start/end date!");
                alert.setContentText("Appointments must start and end in the same day due to business operation constraints");
                alert.showAndWait();
                break;
            case 3:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start date!");
                alert.setContentText("An appointment cannot have a start date after an end date");
                alert.showAndWait();
                break;
            case 4:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start time!");
                alert.setContentText("An appointment cannot have a start time after an end time");
                alert.showAndWait();
                break;
            case 5:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start time!");
                alert.setContentText("An appointment cannot have the same start time and end time");
                alert.showAndWait();
                break;
            case 6:
                alert.setTitle("Error");
                alert.setHeaderText("Appointment overlap!");
                alert.setContentText("This appointment overlaps with an existing appointment");
                alert.showAndWait();
                break;
            case 7:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start time!");
                alert.setContentText("An appointment cannot have a start time before the current time");
                alert.showAndWait();
                break;
            case 8:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start date!");
                alert.setContentText("An appointment cannot have a start date before the current date");
                alert.showAndWait();
                break;
            case 9:
                alert.setTitle("Error");
                alert.setHeaderText("One or more fields empty!");
                alert.setContentText("Please fill out all required fields");
                alert.showAndWait();
                break;
        }
    }

    /**
     * Saves a new appointment with a random, unique id
     * Contains error checking for business hours, business days, invalid entries and appointment overlaps
     * Uses error messages from the displayAlerts function
     * Loads new appointment data into the appointments table in the database
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    public void saveOnClick(ActionEvent actionEvent) throws SQLException, IOException {
        if(allFieldsFilled()){
            int appointmentId = (int) (Math.random()*100);
            LocalTime convertedStartTime = TimeConversions.convertToEasternTime(appointmentStartDate.getValue(), appointmentStartTime.getValue());
            LocalTime convertedEndTime = TimeConversions.convertToEasternTime(appointmentEndDate.getValue(), appointmentEndTime.getValue());
            if(convertedStartTime.getHour() < 8|| convertedStartTime.getHour() > 22 || convertedEndTime.getHour() < 8 || convertedEndTime.getHour() > 22 || ((convertedStartTime.getHour() == 22) && convertedStartTime.getMinute() > 0) || ((convertedEndTime.getHour() == 22) && convertedEndTime.getMinute() > 0)){
                displayAlert(1);
                return;
            }
            if(!appointmentStartDate.getValue().isEqual(appointmentEndDate.getValue())){
                displayAlert(2);
                return;
            }
            if(appointmentStartDate.getValue().isBefore(LocalDate.now())){
                displayAlert(8);
                return;
            }
            if(appointmentStartDate.getValue().equals(LocalDate.now()) && appointmentStartTime.getValue().isBefore(LocalTime.now())){
                displayAlert(7);
                return;
            }
            if(appointmentStartDate.getValue().isAfter(appointmentEndDate.getValue())){
                displayAlert(3);
                return;
            }
            if(appointmentStartTime.getValue().isAfter(appointmentEndTime.getValue())){
                displayAlert(4);
                return;
            }
            if(appointmentStartTime.getValue().equals(appointmentEndTime.getValue())){
                displayAlert(5);
                return;
            }
            LocalDateTime startDateTime = LocalDateTime.of(appointmentStartDate.getValue(), appointmentStartTime.getValue());
            LocalDateTime endDateTime = LocalDateTime.of(appointmentEndDate.getValue(), appointmentEndTime.getValue());
            if(appointmentOverlap()){
                    displayAlert(6);
                    return;
            }

            Connection conn = ConnectDB.getConnection();
            ConnectDB.setPreparedStatement(conn, "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement preparedStatement = ConnectDB.getPreparedStatement();
            preparedStatement.setInt(1, appointmentId);
            preparedStatement.setString(2, appointmentTitle.getText());
            preparedStatement.setString(3, appointmentDescription.getText());
            preparedStatement.setString(4, appointmentLocation.getText());
            preparedStatement.setString(5, appointmentType.getText());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(startDateTime));
            preparedStatement.setTimestamp(7, Timestamp.valueOf(endDateTime));
            preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(9, userId.getValue());
            preparedStatement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(11, userId.getValue());
            preparedStatement.setInt(12, customerId.getValue());
            preparedStatement.setInt(13, userId.getValue());
            preparedStatement.setInt(14, IDNameConversions.convertContactNameToID(appointmentContact.getValue()));
            preparedStatement.execute();

            Parent parent = FXMLLoader.load(getClass().getResource("../views/MainConsole.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("An appointment with ID: " + appointmentId + " was successfully added");
            alert.showAndWait();
            return;
        }
        else {
            displayAlert(9);
        }
    }

    /**
     * Controls the click action for the cancel button
     * Loads the main console when clicked and doesn't add an appointment
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

    /**
     * Checks if all fields are filled on the screen
     * Used to successfully add a new appointment(appointment can't be added if a field is empty)
     * @return
     */
    public Boolean allFieldsFilled(){
        if(!appointmentTitle.getText().isEmpty() && !appointmentType.getText().isEmpty() && !appointmentDescription.getText().isEmpty() && !appointmentLocation.getText().isEmpty() && appointmentStartDate.getValue() != null && appointmentStartTime.getValue() != null && appointmentEndDate.getValue() != null && appointmentEndTime.getValue() != null && customerId.getValue() != null && userId.getValue() != null && appointmentContact.getValue() != null){
            return true;
        }
        return false;
    }

    /**
     * Checks if the new appointment times overlap with an existing appointment
     * Based on conflicting start/end times for other appointments
     * @return
     * @throws SQLException
     */
    public Boolean appointmentOverlap() throws SQLException {
        ObservableList<Appointment> listOfAppointments = DatabasePull.getAppointmentsFromDatabase();
        LocalDateTime startDateTime = LocalDateTime.of(appointmentStartDate.getValue(), appointmentStartTime.getValue());
        LocalDateTime endDateTime = LocalDateTime.of(appointmentEndDate.getValue(), appointmentEndTime.getValue());
        for(Appointment a: listOfAppointments){
            if((startDateTime.isBefore(a.getStartTime()) && endDateTime.isAfter(a.getEndTime())) || (startDateTime.isAfter(a.getStartTime()) && endDateTime.isBefore(a.getEndTime())) || ((startDateTime.isAfter(a.getStartTime()) && startDateTime.isBefore(a.getEndTime())) && endDateTime.isAfter(a.getEndTime())) || ((endDateTime.isBefore(a.getEndTime()) && endDateTime.isAfter(a.getStartTime())) && startDateTime.isBefore(a.getStartTime())) || (startDateTime.equals(a.getStartTime()) && endDateTime.equals(a.getEndTime())) || ((startDateTime.isAfter(a.getStartTime()) && startDateTime.isBefore(a.getEndTime())) && endDateTime.isEqual(a.getEndTime())) || ((endDateTime.isBefore(a.getEndTime()) && endDateTime.isAfter(a.getStartTime())) && startDateTime.isEqual(a.getStartTime())) || (startDateTime.isBefore(a.getStartTime()) && endDateTime.isEqual(a.getEndTime())) || (startDateTime.isEqual(a.getStartTime()) && endDateTime.isAfter(a.getEndTime()))){
                return true;
            }
        }
        return false;
    }


}
