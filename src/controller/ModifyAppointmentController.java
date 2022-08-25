package controller;

import DatabaseAccess.AppointmentDatabaseAccess;
import DatabaseAccess.ContactDatabaseAccess;
import DatabaseAccess.CustomerDatabaseAccess;
import DatabaseAccess.UserDatabaseAccess;
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
import model.Appointment;
import model.Contact;
import model.Customer;
import model.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import static main.DateTime.UTCLocalDTConversion;

/**
 * Controls Modify Appointment fxml
 * @author Andre Simmons
 */
public class ModifyAppointmentController {
    @FXML private ComboBox<String> customerIDField;
    @FXML private ComboBox<String> userIDField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker appointmentEndDate;
    @FXML private ComboBox<String> appointmentEndTime;
    @FXML private TextField appointmentIDField;
    @FXML private TextField appointmentLocationField;
    @FXML private Button saveButton;
    @FXML private DatePicker appointmentStartDate;
    @FXML private ComboBox<String> appointmentStartTime;
    @FXML private TextField appointmentTitleField;
    @FXML private ComboBox<String> appointmentContactField;
    @FXML private Button cancelButton;
    @FXML private TextField appointmentTypeField;

    private static Appointment selectedAppointmentModify;

    /**
     * Initializes the Modify Appointment screen
     * @throws SQLException
     */
    public void initialize() throws SQLException {
        selectedAppointmentModify = MainAppointmentsController.getSelectedAppointmentModify();
        if (selectedAppointmentModify != null) {

            /**
             * Fills contact combo box
             */
            ObservableList<Contact> allContacts = ContactDatabaseAccess.loadAllContacts();
            ObservableList<String> allContactsNames = FXCollections.observableArrayList();
            String displayContactName = "";

            /**
             * Fills customer ID and user ID combo boxes
             */
            Connection connection = ConnectDB.startConnection();
            ObservableList<Customer> listOfCustomers = CustomerDatabaseAccess.loadAllCustomers(connection);
            ObservableList<String> custIDList = FXCollections.observableArrayList();
            ObservableList<UserDatabaseAccess> listOfUsers = UserDatabaseAccess.loadAllUsers();
            ObservableList<String> userIDList = FXCollections.observableArrayList();
            for(Customer c: listOfCustomers){
                custIDList.add(String.valueOf(c.getCustomerID()));
            }
            for(User u: listOfUsers){
                userIDList.add(String.valueOf(u.getUserID()));
            }
            customerIDField.setItems(custIDList);
            userIDField.setItems(userIDList);

            /**
             * lambda function that creates observable list containing contact names
             */
            allContacts.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
            appointmentContactField.setItems(allContactsNames);
            for (Contact contact: allContacts) {
                if (selectedAppointmentModify.getContactName().equals(contact.getContactName())) {
                    displayContactName = contact.getContactName();
                }
            }
            appointmentIDField.setText(String.valueOf(selectedAppointmentModify.getAppointmentID()));
            appointmentTitleField.setText(selectedAppointmentModify.getAppointmentTitle());
            descriptionField.setText(selectedAppointmentModify.getAppointmentDescription());
            appointmentLocationField.setText(selectedAppointmentModify.getAppointmentLocation());
            appointmentTypeField.setText(selectedAppointmentModify.getAppointmentType());
            customerIDField.setValue(String.valueOf(selectedAppointmentModify.getCustomerID()));
            appointmentStartDate.setValue(selectedAppointmentModify.getStart().toLocalDate());
            appointmentEndDate.setValue(selectedAppointmentModify.getEnd().toLocalDate());
            appointmentStartTime.setValue(String.valueOf(selectedAppointmentModify.getStart().toLocalTime()));
            appointmentEndTime.setValue(String.valueOf(selectedAppointmentModify.getEnd().toLocalTime()));
            userIDField.setValue(String.valueOf(selectedAppointmentModify.getUserID()));
            appointmentContactField.setValue(displayContactName);
            ObservableList<String> appointmentTimes = FXCollections.observableArrayList();
            LocalTime firstAppointment = LocalTime.MIN.plusHours(6);
            LocalTime lastAppointment = LocalTime.MAX.minusHours(1);
            if (!firstAppointment.equals(0) || !lastAppointment.equals(0)) {
                while (firstAppointment.isBefore(lastAppointment)) {
                    appointmentTimes.add(String.valueOf(firstAppointment));
                    firstAppointment = firstAppointment.plusMinutes(15);
                }
            }
            appointmentStartTime.setItems(appointmentTimes);
            appointmentEndTime.setItems(appointmentTimes);
        }

    }

    /**
     * Controls save button
     * Saves updated appointment to database
     * @param event
     */
    @FXML
    void saveAppointment(ActionEvent event) {
        try {
            Connection connection = ConnectDB.startConnection();
            /*
             Check if all fields are filled
             */
            if (!appointmentTitleField.getText().isEmpty() && !descriptionField.getText().isEmpty() && !appointmentLocationField.getText().isEmpty() && !appointmentTypeField.getText().isEmpty() && appointmentStartDate.getValue() != null && appointmentEndDate.getValue() != null && !appointmentStartTime.getValue().isEmpty() && !appointmentEndTime.getValue().isEmpty() && !customerIDField.getValue().isEmpty() && !userIDField.getValue().isEmpty() && !appointmentContactField.getValue().isEmpty()) {
                ObservableList<Customer> listOfCustomers = CustomerDatabaseAccess.loadAllCustomers(connection);
                ObservableList<Integer> customerIDList = FXCollections.observableArrayList();
                ObservableList<UserDatabaseAccess> listOfUsers = UserDatabaseAccess.loadAllUsers();
                ObservableList<Integer> userIDList = FXCollections.observableArrayList();
                ObservableList<Appointment> listOfAppointments = AppointmentDatabaseAccess.loadAllAppointments();
                listOfCustomers.stream().map(Customer::getCustomerID).forEach(customerIDList::add);
                listOfUsers.stream().map(User::getUserID).forEach(userIDList::add);
                /*
                 Gets start and end date/time values from the form
                 Converts to necessary times in order to properly perform error checks
                 */
                LocalDate startLocalD = appointmentStartDate.getValue();
                LocalDate endLocalD = appointmentEndDate.getValue();
                DateTimeFormatter DTMinHour = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime localTimeStart = LocalTime.parse(appointmentStartTime.getValue(), DTMinHour);
                LocalTime LocalTimeEnd = LocalTime.parse(appointmentEndTime.getValue(), DTMinHour);
                LocalDateTime DTStart = LocalDateTime.of(startLocalD, localTimeStart);
                LocalDateTime DTEnd = LocalDateTime.of(endLocalD, LocalTimeEnd);
                LocalDateTime DTNow = LocalDateTime.now();
                ZonedDateTime startZoneDT = ZonedDateTime.of(DTStart, ZoneId.systemDefault());
                ZonedDateTime endZoneDT = ZonedDateTime.of(DTEnd, ZoneId.systemDefault());
                /*
                Used when checking business hours of operation in EST
                 */
                ZonedDateTime startConvertToEST = startZoneDT.withZoneSameInstant(ZoneId.of("America/New_York"));
                ZonedDateTime endConvertToEST = endZoneDT.withZoneSameInstant(ZoneId.of("America/New_York"));
                LocalTime businessOperationStart = LocalTime.of(8, 0, 0);
                LocalTime businessOperationEnd = LocalTime.of(22, 0, 0);
                int customerID = Integer.parseInt(customerIDField.getValue());

                /**
                 * Grabs the appointment start and date info converted to EST
                 * Checks if day/time is outside of business operations schedule (8AM-10PM EST)
                 */
                if (startConvertToEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue()) || startConvertToEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue()) || endConvertToEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue()) || endConvertToEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "This day is outside of business operations! Business Operations are Mon-Fri.");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    System.out.println("day is outside of business hours");
                    return;
                }
                if (startConvertToEST.toLocalTime().isBefore(businessOperationStart) || startConvertToEST.toLocalTime().isAfter(businessOperationEnd) || endConvertToEST.toLocalTime().isBefore(businessOperationStart) || endConvertToEST.toLocalTime().isAfter(businessOperationEnd)) {
                    System.out.println("time is outside of business hours");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "This time is outside of business operations! Business hours are 8AM-10PM EST" + startConvertToEST.toLocalTime() + " - " + endConvertToEST.toLocalTime() + " EST");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }
                int updatedAppointmentID = Integer.parseInt(appointmentIDField.getText());

                /**
                 * Verifies valid start date
                 * Displays error if start date is before the current date
                 * Displays error if start/end date is invalid
                 */
                if(DTStart.isBefore(DTNow)){
                    System.out.println("Appointment has start date/time before current date/time");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid start/end date! Appointment cannot have a start date/time before the current date/time");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }

                if (DTStart.isAfter(DTEnd)) {
                    System.out.println("Appointment has start time after end time");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid start/end time! Appointment cannot have a start time after end time");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }
                if (DTStart.isEqual(DTEnd)) {
                    System.out.println("Appointment has same start and end time");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid start/end time! Appointment cannot have the same start time and end time");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }
                for (Appointment a : listOfAppointments) {
                    LocalDateTime otherStart = a.getStart();
                    LocalDateTime otherEnd = a.getEnd();
                    /**
                     * Verifies if there's any overlap with other appointments
                     */
                    if ((customerID == a.getCustomerID()) && (updatedAppointmentID != a.getAppointmentID()) && (((DTStart.isBefore(otherStart)) && (DTEnd.isAfter(otherEnd))) || ((DTStart.isAfter(otherStart)) && (DTEnd.isBefore(otherEnd))) || (((DTStart.isAfter(otherStart)) && (DTStart.isBefore(otherEnd))) && (DTEnd.isAfter(otherEnd))) || ((DTStart.isBefore(otherStart)) && ((DTEnd.isBefore(otherEnd)) && (DTEnd.isAfter(otherStart)))) || ((DTStart.isEqual(otherStart)) && (DTEnd.isEqual(otherEnd))) || (((DTStart.isBefore(otherStart)) || (DTStart.isAfter(otherStart))) && (DTEnd.isEqual(otherEnd))) || ((DTStart.isEqual(otherStart)) && ((DTEnd.isAfter(otherEnd)) || (DTEnd.isBefore(otherEnd)))))) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "The times selected overlap with an existing appointment!");
                        Optional<ButtonType> confirmation = alert.showAndWait();
                        System.out.println("Appointment overlaps with existing appointment.");
                        return;
                    }
                }
                String startDate = appointmentStartDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String startTime = appointmentStartTime.getValue();
                String endDate = appointmentEndDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String endTime = appointmentEndTime.getValue();
                String UTCStart = UTCLocalDTConversion(startDate + " " + startTime + ":00");
                String UTCEnd = UTCLocalDTConversion(endDate + " " + endTime + ":00");

                /**
                 * Updates appointments in appropriate table within database
                 */
                String insertStatement = "UPDATE Appointments SET AppointmentID = ?, Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, CustomerID = ?, UserID = ?, Contact = ? WHERE AppointmentID = ?";
                ConnectDB.setPreparedStatement(ConnectDB.getConnection(), insertStatement);
                PreparedStatement ps = ConnectDB.getPreparedStatement();
                ps.setInt(1, Integer.parseInt(appointmentIDField.getText()));
                ps.setString(2, appointmentTitleField.getText());
                ps.setString(3, descriptionField.getText());
                ps.setString(4, appointmentLocationField.getText());
                ps.setString(5, appointmentTypeField.getText());
                ps.setString(6, UTCStart);
                ps.setString(7, UTCEnd);
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, userIDField.getValue());
                ps.setInt(10, Integer.parseInt(customerIDField.getValue()));
                ps.setInt(11, Integer.parseInt(userIDField.getValue()));
                ps.setString(12, appointmentContactField.getValue());
                ps.setInt(13, Integer.parseInt(appointmentIDField.getText()));
                System.out.println("ps " + ps);
                ps.execute();

                /**
                 * Goes back to the main appointments screen once clicked
                 */
                Parent root = FXMLLoader.load(getClass().getResource("../view/MainAppointments.fxml"));
                Scene scene = new Scene(root);
                Stage MainScreenReturn = (Stage) ((Node) event.getSource()).getScene().getWindow();
                MainScreenReturn.setScene(scene);
                MainScreenReturn.show();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "The appointment was successfully updated!");
                Optional<ButtonType> confirmation = alert.showAndWait();
                System.out.println("Appointment successfully updated.");
                return;

            } else {
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
     * Controls cancel button
     * Returns to the main appointment screen when clicked
     * @param event
     * @throws IOException
     */
    @FXML
    public void modifyAppointmentCancel (ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("../view/MainAppointments.fxml"));
        Scene scene = new Scene(root);
        Stage MainScreenReturn = (Stage)((Node)event.getSource()).getScene().getWindow();
        MainScreenReturn.setScene(scene);
        MainScreenReturn.show();
    }


}
