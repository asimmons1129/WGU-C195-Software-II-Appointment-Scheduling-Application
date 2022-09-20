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
import model.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Controls the reports screen
 * @author Andre Simmons
 */
public class ReportController {
    @FXML private ComboBox<String> contactName;
    @FXML private TableView<Appointment> contactScheduleTable;
    @FXML private TableColumn<Appointment, Integer> appointmentId;
    @FXML private TableColumn<Appointment, String> appointmentTitle;
    @FXML private TableColumn<Appointment, String> appointmentType;
    @FXML private TableColumn<Appointment, String> appointmentDescription;
    @FXML private TableColumn<Appointment, String> appointmentLocation;
    @FXML private TableColumn<Appointment, LocalDateTime> appointmentStart;
    @FXML private TableColumn<Appointment, LocalDateTime> appointmentEnd;
    @FXML private TableColumn<Appointment, Integer> appointmentCustomerId;
    @FXML private TableView<ReportByMonth> appointmentByMonthTable;
    @FXML private TableColumn<ReportByMonth, String> appointmentMonth;
    @FXML private TableColumn<ReportByMonth, String> appointmentTypeColumn;
    @FXML private TableColumn<ReportByMonth, Integer> typeTotal;
    @FXML private TableView<Report> customerByDivisionTable;
    @FXML private TableColumn<Report, String> divisionName;
    @FXML private TableColumn<Report, Integer> divisionTotal;
    @FXML private Button backButton;
    @FXML private Button logoutButton;

    /**
     * Initializes screen
     * Loads data into appropriate tables
     * @throws SQLException
     */
    public void initialize () throws SQLException{
        ObservableList<Contact> listOfContacts = DatabasePull.getContactsFromDatabase();
        ObservableList<String> contactNamesList = FXCollections.observableArrayList();
        for(Contact c: listOfContacts){
            contactNamesList.add(c.getName());
        }
        contactName.setItems(contactNamesList);
        appointmentId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentStart.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        appointmentEnd.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        appointmentCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appointmentMonth.setCellValueFactory(new PropertyValueFactory<>("month"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeTotal.setCellValueFactory(new PropertyValueFactory<>("typeTotal"));
        divisionName.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
        divisionTotal.setCellValueFactory(new PropertyValueFactory<>("divisionTotal"));
        appointmentByMonthTable.setItems(generateAppointmentByMonthAndType());
        customerByDivisionTable.setItems(generateCustomReport());
    }

    /**
     * Generates data for custom report(total customers for each division)
     * Accomplished through use of sql statement that groups by division id and does a count for each division
     * Displays each division name and the total for that division in the appropriate table
     * @return
     * @throws SQLException
     */
    public static ObservableList<Report> generateCustomReport() throws SQLException{
        ObservableList <Report> customReport = FXCollections.observableArrayList();
        Connection conn = ConnectDB.getConnection();
        String sql1 = "SELECT first_level_divisions.Division, COUNT(*) AS divisionCount FROM customers INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID WHERE first_level_divisions.Division_ID = customers.Division_ID GROUP BY first_level_divisions.Division_ID ORDER BY COUNT(*) DESC";
        ConnectDB.setPreparedStatement(conn, sql1);
        PreparedStatement preparedStatement = ConnectDB.getPreparedStatement();
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            String divisionName = resultSet.getString("Division");
            int divisionTotal = resultSet.getInt("divisionCount");
            Report report = new Report(divisionTotal, divisionName);
            customReport.add(report);
        }
        return customReport;
    }

    /**
     * Generates a report that shows the total appointments that have the same type for each month
     * Accomplished through sql statement that grabs the monthname and type for each appointment and performs a count for the total appointments for that type
     * Displays each month, type and total in the appropriate table
     * @return
     * @throws SQLException
     */
    public static ObservableList<ReportByMonth> generateAppointmentByMonthAndType() throws SQLException{
        ObservableList<ReportByMonth> reportByMonths = FXCollections.observableArrayList();
        Connection conn = ConnectDB.getConnection();
        String sql = "SELECT MONTHNAME(Start) AS month, \n" +
                "appointments.Type, COUNT(appointments.Type) AS typeCount \n" +
                "FROM appointments \n" +
                "GROUP BY appointments.Type \n" +
                "ORDER BY month DESC";
        ConnectDB.setPreparedStatement(conn, sql);
        PreparedStatement preparedStatement = ConnectDB.getPreparedStatement();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            String monthName = resultSet.getString("month");
            String typeName = resultSet.getString("Type");
            int typeTotal = resultSet.getInt("typeCount");
            ReportByMonth reportByMonth = new ReportByMonth(monthName, typeName, typeTotal);
            reportByMonths.add(reportByMonth);
        }
        return reportByMonths;
    }



    /**
     * Controls the select contact combo box from the contact schedule tab
     * Loads all appointments associated with the contact selected
     * @throws SQLException
     */
    public void contactScheduleDropDown() throws SQLException {
        ObservableList<Appointment> listOfAppointments = DatabasePull.getAppointmentsFromDatabase();
        ObservableList<Appointment> appointmentsFiltered = FXCollections.observableArrayList();
        String contactSelected = contactName.getSelectionModel().getSelectedItem();
        for(Appointment a: listOfAppointments){
            if(contactSelected.equals(a.getContactName())){
                appointmentsFiltered.add(a);
            }
        }
        contactScheduleTable.setItems(appointmentsFiltered);
    }

    /**
     * Controls the click action for the back button
     * Loads the main console when selected
     * @param actionEvent
     * @throws IOException
     */
    public void backButtonOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../views/MainConsole.fxml"));
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
