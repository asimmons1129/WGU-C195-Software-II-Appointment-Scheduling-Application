package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.ConnectDB;
import main.DatabasePull;
import main.IDNameConversions;
import main.TimeConversions;
import model.Appointment;
import model.Contact;
import model.Customer;
import model.User;

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
 * Controls the modify appointment screen
 * @author Andre Simmons
 */
public class ModifyAppointment {
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
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    private static Appointment select;

    /**
     * Initializes the form
     * Loads all appointment information into appropriate fields from the selected appointment
     * @throws SQLException
     */
    public void initialize() throws SQLException{
        select = MainConsole.getSelectedAppointment();
        ObservableList<Customer> listOfCustomers = DatabasePull.getCustomersFromDatabase();
        ObservableList<User> listOfUsers = DatabasePull.getUsersFromDatabase();
        ObservableList<Contact> listOfContacts = DatabasePull.getContactsFromDatabase();
        ObservableList<Integer> customerIdList = FXCollections.observableArrayList();
        ObservableList<Integer> userIdList = FXCollections.observableArrayList();
        ObservableList<String> contactNameList = FXCollections.observableArrayList();
        for(Customer c: listOfCustomers){
            customerIdList.add(c.getId());
        }
        for(User u : listOfUsers){
            userIdList.add(u.getId());
        }
        for(Contact c: listOfContacts){
            contactNameList.add(c.getName());
        }
        customerId.setItems(customerIdList);
        userId.setItems(userIdList);
        appointmentContact.setItems(contactNameList);
        ObservableList<LocalTime> appointmentTimes = FXCollections.observableArrayList();
        LocalTime firstTime = LocalTime.MIN.plusHours(1);
        LocalTime lastTime = LocalTime.MAX.minusHours(1);
        while(firstTime.isBefore(lastTime)){
            appointmentTimes.add(firstTime);
            firstTime = firstTime.plusMinutes(15);
        }
        appointmentStartTime.setItems(appointmentTimes);
        appointmentEndTime.setItems(appointmentTimes);

        appointmentId.setText(String.valueOf(select.getAppointmentId()));
        appointmentTitle.setText(select.getTitle());
        appointmentType.setText(select.getType());
        appointmentDescription.setText(select.getDescription());
        appointmentLocation.setText(select.getLocation());
        appointmentStartDate.setValue(select.getStartTime().toLocalDate());
        appointmentStartTime.setValue(select.getStartTime().toLocalTime());
        appointmentEndDate.setValue(select.getEndTime().toLocalDate());
        appointmentEndTime.setValue(select.getEndTime().toLocalTime());
        customerId.setValue(select.getCustomerId());
        userId.setValue(select.getUserId());
        appointmentContact.setValue(select.getContactName());
    }

    /**
     * Contains all error messages for this page
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
     * Checks if all fields are filled
     * Appointment cannot be updated if all fields are not filled
     * @return
     */
    public Boolean allFieldsFilled(){
        if(!appointmentTitle.getText().isEmpty() && !appointmentType.getText().isEmpty() && !appointmentDescription.getText().isEmpty() && !appointmentLocation.getText().isEmpty() && appointmentStartDate.getValue() != null && appointmentStartTime.getValue() != null && appointmentEndDate.getValue() != null && appointmentEndTime.getValue() != null && customerId.getValue() != null && userId.getValue() != null && appointmentContact.getValue() != null){
            return true;
        }
        return false;
    }

    /**
     * Checks if the appointment dates/times overlap with an existing appointment
     * Based on conflicting start/end times with other appointments
     * @return
     * @throws SQLException
     */
    public Boolean appointmentOverlap() throws SQLException {
        ObservableList<Appointment> listOfAppointments = DatabasePull.getAppointmentsFromDatabase();
        LocalDateTime startDateTime = LocalDateTime.of(appointmentStartDate.getValue(), appointmentStartTime.getValue());
        LocalDateTime endDateTime = LocalDateTime.of(appointmentEndDate.getValue(), appointmentEndTime.getValue());
        for(Appointment a: listOfAppointments){
            if((!appointmentId.getText().equals(String.valueOf(a.getAppointmentId()))) && ((startDateTime.isBefore(a.getStartTime()) && endDateTime.isAfter(a.getEndTime())) || (startDateTime.isAfter(a.getStartTime()) && endDateTime.isBefore(a.getEndTime())) || ((startDateTime.isAfter(a.getStartTime()) && startDateTime.isBefore(a.getEndTime())) && endDateTime.isAfter(a.getEndTime())) || ((endDateTime.isBefore(a.getEndTime()) && endDateTime.isAfter(a.getStartTime())) && startDateTime.isBefore(a.getStartTime())) || (startDateTime.equals(a.getStartTime()) && endDateTime.equals(a.getEndTime())) || ((startDateTime.isAfter(a.getStartTime()) && startDateTime.isBefore(a.getEndTime())) && endDateTime.isEqual(a.getEndTime())) || ((endDateTime.isBefore(a.getEndTime()) && endDateTime.isAfter(a.getStartTime())) && startDateTime.isEqual(a.getStartTime())) || (startDateTime.isBefore(a.getStartTime()) && endDateTime.isEqual(a.getEndTime())) || (startDateTime.isEqual(a.getStartTime()) && endDateTime.isAfter(a.getEndTime())))){
                return true;
            }
        }
        return false;
    }

    /**
     * Controls the click action for the save button
     * Updates the appointment in the database based on the appointment id
     * Updates the appointment table in the main console
     * Displays error message if all fields are not filled
     * Loads main console if save was successful
     * Displays message that the appointment was successfully updated
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void saveOnClick(ActionEvent actionEvent) throws SQLException, IOException {
        if(allFieldsFilled()){
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
            ConnectDB.setPreparedStatement(conn, "UPDATE appointments SET Appointment_ID = ?, Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?");
            PreparedStatement preparedStatement = ConnectDB.getPreparedStatement();
            preparedStatement.setInt(1, Integer.parseInt(appointmentId.getText()));
            preparedStatement.setString(2, appointmentTitle.getText());
            preparedStatement.setString(3, appointmentDescription.getText());
            preparedStatement.setString(4, appointmentLocation.getText());
            preparedStatement.setString(5, appointmentType.getText());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(startDateTime));
            preparedStatement.setTimestamp(7, Timestamp.valueOf(endDateTime));
            preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(9, userId.getValue());
            preparedStatement.setInt(10, customerId.getValue());
            preparedStatement.setInt(11, userId.getValue());
            preparedStatement.setInt(12, IDNameConversions.convertContactNameToID(appointmentContact.getValue()));
            preparedStatement.setInt(13, Integer.parseInt(appointmentId.getText()));
            preparedStatement.execute();

            Parent parent = FXMLLoader.load(getClass().getResource("../views/MainConsole.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("The appointment with ID: " + appointmentId.getText() + " was successfully updated");
            alert.showAndWait();
            return;

        }
        else {
            displayAlert(9);
        }
    }

    /**
     * Controls the click action for the cancel button
     * Loads the main console and doesn't update the customer when clicked
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
