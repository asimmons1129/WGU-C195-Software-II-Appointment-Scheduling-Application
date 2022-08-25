package controller;
import DatabaseAccess.AppointmentDatabaseAccess;
import main.ConnectDB;
import model.Appointment;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Controls the main appointments fxml
 * @author Andre Simmons
 */
public class MainAppointmentsController {
    @FXML private RadioButton allAppointmentRadio;
    @FXML private RadioButton appointmentWeekRadio;
    @FXML private RadioButton appointmentMonthRadio;
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<?, ?> contactColumn;
    @FXML private TableColumn<?, ?> customerIDColumn;
    @FXML private TableColumn<?, ?> descriptionColumn;
    @FXML private TableColumn<?, ?> appointmentEndColumn;
    @FXML private TableColumn<?, ?> appointmentIDColumn;
    @FXML private TableColumn<?, ?> locationColumn;
    @FXML private TableColumn<?, ?> appointmentStartColumn;
    @FXML private TableColumn<?, ?> appointmentTitleColumn;
    @FXML private TableColumn<?, ?> appointmentTypeColumn;
    @FXML private TableColumn<?, ?> contactIDColumn;
    @FXML private TableColumn<?, ?> userIDColumn;
    @FXML private Button backButton;
    @FXML private Button deleteButton;
    @FXML private Button updateButton;
    @FXML private Button addButton;

    private static Appointment selectedAppointmentModify;

    public static Appointment getSelectedAppointmentModify() {
        return selectedAppointmentModify;
    }

    /**
     * Initializes the main appointments screen
     *
     * @throws SQLException
     */
    public void initialize() throws SQLException {
        ObservableList<Appointment> allAppointmentsList = AppointmentDatabaseAccess.loadAllAppointments();
        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentLocation"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        appointmentsTable.setItems(allAppointmentsList);
    }

    /**
     * Controls add button
     * Opens the add appointment fxml when clicked
     *
     * @param event
     * @throws IOException
     */
    @FXML
    void addAppointment(ActionEvent event) throws IOException {
        Parent addAppointments = FXMLLoader.load(getClass().getResource("../view/AddAppointment.fxml"));
        Scene scene = new Scene(addAppointments);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * Opens Modify Appointment fxml when clicked
     *
     * @param event
     * @throws IOException
     */
    @FXML
    void OnClickModifyAppointment(ActionEvent event) throws IOException {
        try {
            ConnectDB.startConnection();
            selectedAppointmentModify = appointmentsTable.getSelectionModel().getSelectedItem();
            if (selectedAppointmentModify == null) {
                Alert alertError = new Alert(Alert.AlertType.ERROR);
                alertError.setTitle("Error");
                alertError.setHeaderText("No Appointment Selected! Please select an appointment");
                alertError.showAndWait();
            } else {
                Parent updateAppointment = FXMLLoader.load(getClass().getResource("../view/ModifyAppointment.fxml"));
                Scene scene = new Scene(updateAppointment);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Controls back button
     * Goes back to the main screen when clicked
     *
     * @param event
     * @throws IOException
     */
    @FXML
    void backButtonClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../view/MainScreen.fxml"));
        Scene scene = new Scene(root);
        Stage MainScreenReturn = (Stage) ((Node) event.getSource()).getScene().getWindow();
        MainScreenReturn.setScene(scene);
        MainScreenReturn.show();

    }

    /**
     * Displays all appointments when radio button is selected
     *
     * @param event
     * @throws SQLException
     */
    @FXML
    void allAppointmentsRadioClick(ActionEvent event) throws SQLException {
        try {
            ObservableList<Appointment> listOfAllAppointments = AppointmentDatabaseAccess.loadAllAppointments();
            if (listOfAllAppointments != null)
                for (model.Appointment a : listOfAllAppointments) {
                    appointmentsTable.setItems(listOfAllAppointments);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Filters appointments by month when radio button selected
     *
     * @throws SQLException
     */
    @FXML
    void monthRadioClick(ActionEvent event) throws SQLException {
        try {
            ObservableList<Appointment> listOfAllAppointments = AppointmentDatabaseAccess.loadAllAppointments();
            ObservableList<Appointment> appointmentsByMonth = FXCollections.observableArrayList();

            LocalDateTime appointmentCurrentMonth = LocalDateTime.now();


            if (listOfAllAppointments != null)
                listOfAllAppointments.forEach(appointment -> {
                    if (appointment.getStart().getMonth().equals(appointmentCurrentMonth.getMonth()) || appointment.getEnd().getMonth().equals(appointmentCurrentMonth.getMonth())) {
                        appointmentsByMonth.add(appointment);
                    }
                    appointmentsTable.setItems(appointmentsByMonth);
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Filters appointments by week when radio button selected
     *
     * @throws SQLException
     */
    @FXML
    void weekRadioClick(ActionEvent event) throws SQLException {
        try {
            ObservableList<Appointment> listOfAllAppointments = AppointmentDatabaseAccess.loadAllAppointments();
            ObservableList<Appointment> appointmentsByWeek = FXCollections.observableArrayList();
            LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime weekStart = currentDate;
            LocalDateTime weekEnd = currentDate;
            if(currentDate.getDayOfWeek().equals(DayOfWeek.MONDAY)){
                weekStart = currentDate;
                weekEnd = currentDate.plusDays(4);
            }
            else if (currentDate.getDayOfWeek().equals(DayOfWeek.TUESDAY)){
                weekStart = currentDate.minusDays(1);
                weekEnd = currentDate.plusDays(3);
            }
            else if (currentDate.getDayOfWeek().equals(DayOfWeek.WEDNESDAY)){
                weekStart = currentDate.minusDays(2);
                weekEnd = currentDate.plusDays(2);
            }
            else if (currentDate.getDayOfWeek().equals(DayOfWeek.THURSDAY)){
                weekStart = currentDate.minusDays(3);
                weekEnd = currentDate.plusDays(1);
            }
            else if (currentDate.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {
                weekStart = currentDate.minusDays(4);
                weekEnd = currentDate;
            }
            if (listOfAllAppointments != null) {
                LocalDateTime finalWeekStart = weekStart;
                LocalDateTime finalWeekEnd = weekEnd;
                listOfAllAppointments.forEach(appointment -> {
                    if (appointment.getStart().isEqual(finalWeekStart) || appointment.getStart().isEqual(finalWeekEnd) || appointment.getEnd().isEqual(finalWeekStart) || appointment.getEnd().isEqual(finalWeekEnd) || (appointment.getEnd().isAfter(finalWeekStart) && appointment.getEnd().isBefore(finalWeekEnd)) || (appointment.getStart().isAfter(finalWeekStart) && appointment.getStart().isBefore(finalWeekEnd))) {
                        appointmentsByWeek.add(appointment);
                    }
                    appointmentsTable.setItems(appointmentsByWeek);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Controls the delete appointment button
     * Deletes selected appointment
     *
     * @param event
     * @throws Exception
     */
    @FXML
    void deleteAppointment(ActionEvent event) throws Exception {
        selectedAppointmentModify = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppointmentModify == null) {
            Alert alertError = new Alert(Alert.AlertType.ERROR);
            alertError.setTitle("Error");
            alertError.setHeaderText("No Appointment Selected! Please select an appointment");
            alertError.showAndWait();
        } else {
            try {
                Connection c = ConnectDB.startConnection();
                int appointIDDelete = appointmentsTable.getSelectionModel().getSelectedItem().getAppointmentID();
                String appointmentTypeDelete = appointmentsTable.getSelectionModel().getSelectedItem().getAppointmentType();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure that you want to delete the selected appointment?");
                Optional<ButtonType> confirmation = alert.showAndWait();
                if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                    AppointmentDatabaseAccess.appointmentDelete(appointIDDelete, c);
                    ObservableList<Appointment> allAppointmentsList = AppointmentDatabaseAccess.loadAllAppointments();
                    appointmentsTable.setItems(allAppointmentsList);
                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION, "The appointment with appointment ID: " + appointIDDelete + " and appointment type: " + appointmentTypeDelete + " has been successfully cancelled.");
                    Optional<ButtonType> c2 = alert2.showAndWait();
                    System.out.println("The appointment has been deleted");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}