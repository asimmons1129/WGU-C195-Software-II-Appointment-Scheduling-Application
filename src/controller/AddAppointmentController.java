package controller;
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
import DatabaseAccess.AppointmentDatabaseAccess;
import DatabaseAccess.ContactDatabaseAccess;
import DatabaseAccess.CustomerDatabaseAccess;
import DatabaseAccess.UserDatabaseAccess;
import static main.DateTime.UTCLocalDTConversion;

/**
 * Controls the AddAppointment fxml
 * @author Andre Simmons
 */
public class AddAppointmentController {
    @FXML private ComboBox<String> customerIDField;
    @FXML private ComboBox<String> userIDField;
    @FXML private TextField appointmentDescriptionField;
    @FXML private DatePicker appointmentEndDate;
    @FXML private ComboBox<String> appointmentEndTime;
    @FXML private TextField appointmentIDField;
    @FXML private TextField appointmentLocationField;
    @FXML private Button appointmentSaveButton;
    @FXML private DatePicker appointmentStartDate;
    @FXML private ComboBox<String> appointmentStartTime;
    @FXML private TextField appointmentTitleField;
    @FXML private ComboBox<String> appointmentContact;
    @FXML private Button appointmentsCancelButton;
    @FXML private TextField appointmentTypeField;

    /**
     * Initializes/Fills start/end time text fields in 15 min increments
     * Contains lambda #2 that adds contact names to the allContactsNames observabelist.
     * The lambda function replaces the for loop and is eventually used to populate the appointmentContact combo box
     * @throws SQLException
     */
    @FXML
    public void initialize() throws SQLException {
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
        ObservableList<Contact> contactsList = ContactDatabaseAccess.loadAllContacts();
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        /*
        lambda function to add contact names from database to observable list
         */
        contactsList.forEach(contacts -> contactNames.add(contacts.getContactName()));
        ObservableList<String> timeOfAppointments = FXCollections.observableArrayList();
        LocalTime appointmentFirst = LocalTime.MIN.plusHours(6);
        LocalTime appointmentLast = LocalTime.MAX.minusHours(1);

        /**
         *
         */
        if (!appointmentFirst.equals(0) || !appointmentLast.equals(0)) {
            while (appointmentFirst.isBefore(appointmentLast)) {
                timeOfAppointments.add(String.valueOf(appointmentFirst));
                appointmentFirst = appointmentFirst.plusMinutes(15);
            }
        }
        appointmentStartTime.setItems(timeOfAppointments);
        appointmentEndTime.setItems(timeOfAppointments);
        appointmentContact.setItems(contactNames);

    }

    /**
     *
     * Controls the save button
     * @throws IOException
     * @param event
     */
    @FXML
    void saveAddedAppointment(ActionEvent event) throws IOException {
        try {
            Connection connection = ConnectDB.startConnection();
            if (!appointmentTitleField.getText().isEmpty() && !appointmentDescriptionField.getText().isEmpty() && !appointmentLocationField.getText().isEmpty() && !appointmentTypeField.getText().isEmpty() && appointmentStartDate.getValue() != null && appointmentEndDate.getValue() != null && !appointmentStartTime.getValue().isEmpty() && !appointmentEndTime.getValue().isEmpty() && !customerIDField.getValue().isEmpty() && !userIDField.getValue().isEmpty() && !appointmentContact.getValue().isEmpty()) {
                ObservableList<Appointment> listOfAppointments = AppointmentDatabaseAccess.loadAllAppointments();
                /*
                 Gets start and end date/time values from the form
                 Converts to necessary times in order to properly perform error checks
                 */
                LocalDate endLocalD = appointmentEndDate.getValue();
                LocalDate startLocalD = appointmentStartDate.getValue();
                DateTimeFormatter DTMinHour = DateTimeFormatter.ofPattern("HH:mm");
                String sDate = appointmentStartDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String sTime = appointmentStartTime.getSelectionModel().getSelectedItem();
                String eDate = appointmentEndDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String eTime = appointmentEndTime.getSelectionModel().getSelectedItem();
                LocalTime localTimeStart = LocalTime.parse(appointmentStartTime.getValue(), DTMinHour);
                LocalTime LocalTimeEnd = LocalTime.parse(appointmentEndTime.getValue(), DTMinHour);
                LocalDateTime DTStart = LocalDateTime.of(startLocalD, localTimeStart);
                LocalDateTime DTEnd = LocalDateTime.of(endLocalD, LocalTimeEnd);
                LocalDateTime DTNow = LocalDateTime.now();
                ZonedDateTime startZoneDT = DTStart.atZone(ZoneId.systemDefault());
                ZonedDateTime endZoneDT = DTEnd.atZone(ZoneId.systemDefault());
                /*
                Used when checking business hours of operation in EST
                 */
                ZonedDateTime convertStartEST = startZoneDT.withZoneSameInstant(ZoneId.of("America/New_York"));
                ZonedDateTime convertEndEST = endZoneDT.withZoneSameInstant(ZoneId.of("America/New_York"));
                LocalTime appointmentStartVerify = convertStartEST.toLocalTime();
                LocalTime appointmentEndVerify = convertEndEST.toLocalTime();
                DayOfWeek appointmentStartDayVerify = convertStartEST.toLocalDate().getDayOfWeek();
                DayOfWeek appointmentEndDayVerify = convertEndEST.toLocalDate().getDayOfWeek();
                int startDayValue = appointmentStartDayVerify.getValue();
                int endDayValue = appointmentEndDayVerify.getValue();
                int businessWeekStart = DayOfWeek.MONDAY.getValue();
                int businessWeekEnd = DayOfWeek.FRIDAY.getValue();

                LocalTime businessOperationStart = LocalTime.of(8, 0, 0);
                LocalTime businessOperationEnd = LocalTime.of(22, 0, 0);
                int newAppointmentID = Integer.parseInt(String.valueOf((int) (Math.random() * 100)));
                int customerID = Integer.parseInt(customerIDField.getValue());

                /**
                 * Verifies if appointment day is within business operation
                 * Displays message if not within business hours
                 */
                if (startDayValue < businessWeekStart || startDayValue > businessWeekEnd || endDayValue < businessWeekStart || endDayValue > businessWeekEnd) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "This day is outside of business operations! Business Operations are Mon-Fri.");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    System.out.println("Selected Day Outside of Business Operations");
                    return;
                }

                /**
                 * Verifies if appointment time is within business hours of operation
                 * Displays message if not within business hours
                 */
                if (appointmentStartVerify.isBefore(businessOperationStart) || appointmentStartVerify.isAfter(businessOperationEnd) || appointmentEndVerify.isBefore(businessOperationStart) || appointmentEndVerify.isAfter(businessOperationEnd))
                {
                    System.out.println("Selected Time Outside of Business Operations");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "This time is outside of business operations! Business Operation hours are 8AM-10PM EST" + appointmentStartVerify + " - " + appointmentEndVerify + " EST");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }

                /**
                 * Verifies valid start date
                 * Displays error if start date is before the current date
                 */
                if(DTStart.isBefore(DTNow)){
                    System.out.println("Appointment has start date/time before current date/time");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid start/end date! Appointment cannot have a start date/time before the current date/time");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }

                /**
                 * Verifies valid start/end time
                 * Displays message if incorrect
                 */
                if (DTStart.isAfter(DTEnd)) {
                    System.out.println("Appointment has start time after end time");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid start/end time! Appointment cannot have a start time after end time");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }

                /**
                 * Verifies valid start/end time
                 * Displays message if incorrect
                 */
                if (DTStart.isEqual(DTEnd)) {
                    System.out.println("Appointment has same start and end time");
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid start/end time! Appointment cannot have the same start and end time");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }

                for (Appointment a: listOfAppointments)
                {
                    LocalDateTime otherStart = a.getStart();
                    LocalDateTime otherEnd = a.getEnd();
                    /**
                     * Verifies if an appointment already exists between start/end time
                     * Displays message if incorrect
                     */
                    if ((customerID == a.getCustomerID()) && (newAppointmentID != a.getAppointmentID()) && (((DTStart.isBefore(otherStart)) && (DTEnd.isAfter(otherEnd))) || ((DTStart.isAfter(otherStart)) && (DTEnd.isBefore(otherEnd))) || (((DTStart.isAfter(otherStart)) && (DTStart.isBefore(otherEnd))) && (DTEnd.isAfter(otherEnd))) || ((DTStart.isBefore(otherStart)) && ((DTEnd.isBefore(otherEnd)) && (DTEnd.isAfter(otherStart)))) || ((DTStart.isEqual(otherStart)) && (DTEnd.isEqual(otherEnd))) || (((DTStart.isBefore(otherStart)) || (DTStart.isAfter(otherStart))) && (DTEnd.isEqual(otherEnd))) || ((DTStart.isEqual(otherStart)) && ((DTEnd.isAfter(otherEnd)) || (DTEnd.isBefore(otherEnd)))))) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "The times selected overlap with an existing appointment!");
                        Optional<ButtonType> confirmation = alert.showAndWait();
                        System.out.println("Appointment overlaps with existing appointment.");
                        return;
                    }
                }
                /**
                 * Inserts data into appropriate SQL table
                 */
                String insertStatement = "INSERT INTO Appointments (AppointmentID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, CustomerID, UserID, Contact) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                /**
                 * Connection to database
                 */
                ConnectDB.setPreparedStatement(ConnectDB.getConnection(), insertStatement);
                String startUTC = UTCLocalDTConversion(sDate + " " + sTime + ":00");
                String endUTC = UTCLocalDTConversion(eDate + " " + eTime + ":00");
                PreparedStatement ps = ConnectDB.getPreparedStatement();
                ps.setInt(1, newAppointmentID);
                ps.setString(2, appointmentTitleField.getText());
                ps.setString(3, appointmentDescriptionField.getText());
                ps.setString(4, appointmentLocationField.getText());
                ps.setString(5, appointmentTypeField.getText());
                ps.setTimestamp(6, Timestamp.valueOf(startUTC));
                ps.setTimestamp(7, Timestamp.valueOf(endUTC));
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, userIDField.getValue());
                ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(11, userIDField.getValue());
                ps.setInt(12, Integer.parseInt(customerIDField.getValue()));
                ps.setInt(13, Integer.parseInt(ContactDatabaseAccess.contactIDSearch(userIDField.getValue())));
                ps.setString(14, appointmentContact.getValue());
                ps.execute();

                /**
                 * Loads appropriate fxml
                 */
                Parent root = FXMLLoader.load(getClass().getResource("../view/MainAppointments.fxml"));
                Scene scene = new Scene(root);
                Stage MainScreenReturn = (Stage) ((Node) event.getSource()).getScene().getWindow();
                MainScreenReturn.setScene(scene);
                MainScreenReturn.show();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "The appointment was successfully added!");
                Optional<ButtonType> confirmation = alert.showAndWait();
                System.out.println("Appointment successfully added.");
                return;
            }
            /**
             * Displays error if one or more fields are emply
             */
            else {
                Alert a3 = new Alert(Alert.AlertType.ERROR);
                a3.setTitle("Error");
                a3.setHeaderText("One or more fields are empty.  Please fill any missing fields!");
                a3.showAndWait();
            }



        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * Returns to the main appointment screen when cancel button is clicked
     * @throws IOException
     */
    @FXML
    public void cancelAddAppointment (ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("../view/MainAppointments.fxml"));
        Scene scene = new Scene(root);
        Stage MainScreenReturn = (Stage)((Node)event.getSource()).getScene().getWindow();
        MainScreenReturn.setScene(scene);
        MainScreenReturn.show();
    }
}
