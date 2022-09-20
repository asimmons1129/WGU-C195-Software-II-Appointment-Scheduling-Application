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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.ConnectDB;
import main.DatabasePull;
import model.Appointment;
import model.Customer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Controls the main console screen
 * @author Andre Simmons
 */
public class MainConsole {
    @FXML private RadioButton allAppointments;
    @FXML private RadioButton currentMonth;
    @FXML private RadioButton currentWeek;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> appointmentId;
    @FXML private TableColumn<Appointment, String> appointmentTitle;
    @FXML private TableColumn<Appointment, String> appointmentType;
    @FXML private TableColumn<Appointment, String> appointmentDescription;
    @FXML private TableColumn<Appointment, String> appointmentLocation;
    @FXML private TableColumn<Appointment, LocalDateTime> appointmentStart;
    @FXML private TableColumn<Appointment, LocalDateTime> appointmentEnd;
    @FXML private TableColumn<Appointment, String> appointmentContact;
    @FXML private TableColumn<Appointment, Integer> appointmentCustomerId;
    @FXML private TableColumn<Appointment, Integer> appointmentUserId;
    @FXML private Button appointmentAdd;
    @FXML private Button appointmentUpdate;
    @FXML private Button appointmentDelete;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> customerId;
    @FXML private TableColumn<Customer, String> customerName;
    @FXML private TableColumn<Customer, String> customerAddress;
    @FXML private TableColumn<Customer, String> customerPhone;
    @FXML private TableColumn<Customer, String> customerDivision;
    @FXML private TableColumn<Customer, String> customerPostalCode;
    @FXML private Button customerAdd;
    @FXML private Button customerUpdate;
    @FXML private Button customerDelete;
    @FXML private Button reportsButton;
    @FXML private Button logoutButton;
    @FXML private ToggleGroup toggleGroup;
    /**
     * Grabs the selected appointment from the screen
     * Used when modifying an appointment
     */
    private static Appointment selectedAppointment;
    public static Appointment getSelectedAppointment(){
        return selectedAppointment;
    }

    /**
     * Grabs the selected customer from the screen
     * Used when modifying a customer
     */
    private static Customer selectedCustomer;
    public static Customer getSelectedCustomer(){
        return selectedCustomer;
    }

    /**
     * Initializes the screen
     * Fills both the appointment and customer tables with the appropriate data from the database
     * @throws SQLException
     */
    public void initialize() throws SQLException {
        ObservableList<Appointment> listOfAppointments = DatabasePull.getAppointmentsFromDatabase();
        ObservableList<Customer> listOfCustomers = DatabasePull.getCustomersFromDatabase();
        appointmentId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentStart.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        appointmentEnd.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        appointmentContact.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        appointmentUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        appointmentCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appointmentTable.setItems(listOfAppointments);
        customerId.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerDivision.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
        customerPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerTable.setItems(listOfCustomers);
    }

    /**
     * Controls the click action for the all appointments radio button
     * Displays all appointments from the database in the appointments table
     * @param actionEvent
     * @throws SQLException
     */
    public void allAppointmentsSelected(ActionEvent actionEvent) throws SQLException {
        ObservableList<Appointment> listOfAppointments = DatabasePull.getAppointmentsFromDatabase();
        appointmentTable.setItems(listOfAppointments);
    }

    /**
     * Controls the click action for the current month radio button
     * When selected, displays all appointments that are in the current month of the local machine
     * @param actionEvent
     * @throws SQLException
     */
    public void currentMonthSelected(ActionEvent actionEvent) throws SQLException{
        ObservableList<Appointment> listOfAppointments = DatabasePull.getAppointmentsFromDatabase();
        ObservableList<Appointment> currentMonthAppointments = FXCollections.observableArrayList();
        LocalDateTime timeNow = LocalDateTime.now();
        for(Appointment a: listOfAppointments){
            if(a.getStartTime().getYear() == timeNow.getYear() && (a.getStartTime().getMonth().equals(timeNow.getMonth()) || a.getEndTime().getMonth().equals(timeNow.getMonth()))){
                currentMonthAppointments.add(a);
            }
            appointmentTable.setItems(currentMonthAppointments);
        }
    }

    /**
     * Controls the click action for the current week radio button
     * When selected, displays all appointments for the current week of the local machine
     * @param actionEvent
     * @throws SQLException
     */
    public void currentWeekSelected(ActionEvent actionEvent) throws SQLException{
        ObservableList<Appointment> listOfAppointments = DatabasePull.getAppointmentsFromDatabase();
        ObservableList<Appointment> currentWeekAppointments = FXCollections.observableArrayList();
        LocalDate currentDate = LocalDate.now();
        LocalDate weekStart = currentDate;
        LocalDate weekEnd = currentDate;
        if(currentDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            weekEnd = currentDate.plusDays(6);
        }
        else if(currentDate.getDayOfWeek().equals(DayOfWeek.MONDAY)){
            weekStart = currentDate.minusDays(1);
            weekEnd = currentDate.plusDays(5);
        }
        else if(currentDate.getDayOfWeek().equals(DayOfWeek.TUESDAY)){
            weekStart = currentDate.minusDays(2);
            weekEnd = currentDate.plusDays(4);
        }
        else if(currentDate.getDayOfWeek().equals(DayOfWeek.WEDNESDAY)){
            weekStart = currentDate.minusDays(3);
            weekEnd = currentDate.plusDays(3);
        }
        else if(currentDate.getDayOfWeek().equals(DayOfWeek.THURSDAY)){
            weekStart = currentDate.minusDays(4);
            weekEnd = currentDate.plusDays(2);
        }
        else if(currentDate.getDayOfWeek().equals(DayOfWeek.FRIDAY)){
            weekStart = currentDate.minusDays(5);
            weekEnd = currentDate.plusDays(1);
        }
        else if(currentDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)){
            weekStart = currentDate.minusDays(6);
        }
        LocalDate finalWeekStart = weekStart;
        LocalDate finalWeekEnd = weekEnd;
        for(Appointment appointment: listOfAppointments){
            LocalDate startDate = appointment.getStartTime().toLocalDate();
            LocalDate endDate = appointment.getEndTime().toLocalDate();
            if ((appointment.getStartTime().getYear() == LocalDateTime.now().getYear() || appointment.getEndTime().getYear() == LocalDateTime.now().getYear()) && (startDate.isEqual(finalWeekStart) || startDate.isEqual(finalWeekEnd) || endDate.isEqual(finalWeekStart) || endDate.isEqual(finalWeekEnd) || (endDate.isAfter(finalWeekStart) && endDate.isBefore(finalWeekEnd)) || (startDate.isAfter(finalWeekStart) && startDate.isBefore(finalWeekEnd)))) {
                currentWeekAppointments.add(appointment);
            }
            appointmentTable.setItems(currentWeekAppointments);
        }
    }

    /**
     * Contains all error messages for this screen
     * Alert depends on the scenario
     * @param alertSelected
     */
    public void displayAlerts(int alertSelected){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        switch(alertSelected){
            case 1:
                alert.setTitle("Error");
                alert.setHeaderText("No Appointment Selected!");
                alert.setContentText("Please select an appointment");
                alert.showAndWait();
                break;
            case 2:
                alert.setTitle("Error");
                alert.setHeaderText("No Customer Selected!");
                alert.setContentText("Please select a customer");
                alert.showAndWait();
                break;
        }
    }

    /**
     * Controls the click action for the appointment add button
     * loads the add appointment screen
     * @param actionEvent
     * @throws IOException
     */
    public void addAppointmentOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../views/AddAppointment.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Controls the click action for the update appointment button
     * Displays error message if no appointment selected
     * Loads the modify appointment screen if an appointment is selected
     * @param actionEvent
     * @throws IOException
     */
    public void updateAppointmentOnClick(ActionEvent actionEvent) throws IOException{
        selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if(selectedAppointment == null){
            displayAlerts(1);
        }
        else {
            Parent parent = FXMLLoader.load(getClass().getResource("../views/ModifyAppointment.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Controls the click action for the delete appointment button
     * Displays an error message if no appointment is selected
     * Asks user for confirmation if an appointment is selected
     * Deletes appointment from database and from the appointments table if confirmed
     * Displays message if deletion successful
     * @param actionEvent
     * @throws SQLException
     */
    public void deleteAppointmentOnClick(ActionEvent actionEvent) throws SQLException {
        Appointment select = appointmentTable.getSelectionModel().getSelectedItem();
        if(select == null){
            displayAlerts(1);
        }
        else{
            Connection conn = ConnectDB.getConnection();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Are you sure that you want to delete the selected appointment?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                ConnectDB.setPreparedStatement(conn, "DELETE FROM appointments WHERE Appointment_ID=?");
                PreparedStatement preparedStatement = ConnectDB.getPreparedStatement();
                preparedStatement.setInt(1, select.getAppointmentId());
                preparedStatement.execute();
                ObservableList<Appointment> listOfAppointments = DatabasePull.getAppointmentsFromDatabase();
                appointmentTable.setItems(listOfAppointments);
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Information");
                a.setContentText("The appointment with ID: " + select.getAppointmentId() + " and type " + select.getType() + " has been successfully deleted.");
                a.showAndWait();
            }
        }
    }

    /**
     * Controls the click action for the add customer button
     * Loads the add customer screen when clicked
     * @param actionEvent
     * @throws IOException
     */
    public void addCustomerOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../views/AddCustomer.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Controls the click action for the update customer button
     * Displays error message if no customer selected
     * Loads modify customer screen if customer selected
     * @param actionEvent
     * @throws IOException
     */
    public void updateCustomerOnClick(ActionEvent actionEvent) throws IOException{
        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if(selectedCustomer == null){
            displayAlerts(2);
        }
        else {
            Parent parent = FXMLLoader.load(getClass().getResource("../views/ModifyCustomer.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Controls the click action for the delete customer button
     * Displays error message if no customer selected
     * Confirms with user if customer selected
     * Deletes customer and all associated appointments from the database and appropriate tables
     * Deletes the appointments first then the customer due to foreign key constraints
     * Displays message if deletion successful
     * @param actionEvent
     * @throws SQLException
     */
    public void deleteCustomerOnClick(ActionEvent actionEvent) throws SQLException {
        Customer select = customerTable.getSelectionModel().getSelectedItem();
        if(select == null){
            displayAlerts(2);
        }
        else{
            Connection conn = ConnectDB.getConnection();
            ObservableList<Appointment> listOfAppointments = DatabasePull.getAppointmentsFromDatabase();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Are you sure that you want to delete the selected customer?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                for(Appointment a: listOfAppointments){
                    if(select.getId() == a.getCustomerId()){
                        ConnectDB.setPreparedStatement(conn, "DELETE FROM appointments WHERE Customer_ID = ?");
                        PreparedStatement appPrepared = ConnectDB.getPreparedStatement();
                        appPrepared.setInt(1, a.getCustomerId());
                        appPrepared.execute();
                    }
                }
                ConnectDB.setPreparedStatement(conn, "DELETE FROM customers WHERE Customer_ID=?");
                PreparedStatement preparedStatement = ConnectDB.getPreparedStatement();
                preparedStatement.setInt(1, select.getId());
                preparedStatement.execute();
                ObservableList<Customer> listOfCustomers = DatabasePull.getCustomersFromDatabase();
                ObservableList<Appointment> newlistOfAppointments = DatabasePull.getAppointmentsFromDatabase();
                customerTable.setItems(listOfCustomers);
                appointmentTable.setItems(newlistOfAppointments);
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Information");
                a.setContentText("The customer with ID: " + select.getId() + " along with all associated appointments has been successfully deleted.");
                a.showAndWait();
            }
        }
    }

    /**
     * Controls the click action for the reports button
     * Loads the reports screen when clicked
     * @param actionEvent
     * @throws IOException
     */
    public void reportsOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../views/Report.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Controls the click action for the logout button
     * Loads the login screen when clicked
     * @param actionEvent
     * @throws IOException
     */
    public void logoutOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../views/Login.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
