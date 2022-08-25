package controller;

import DatabaseAccess.AppointmentDatabaseAccess;
import DatabaseAccess.ContactDatabaseAccess;
import DatabaseAccess.ReportsDatabaseAccess;
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
import model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Month;
import java.util.Collections;

/**
 * Controls the reports fxml
 * @author Andre Simmons
 */
public class ReportsController {
    @FXML private TableView<Appointment> appointmentsReportsTable;
    @FXML private TableColumn<?, ?> customerIDColumn;
    @FXML private TableColumn<?, ?> descriptionColumn;
    @FXML private TableColumn<?, ?> endTimeColumn;
    @FXML private TableColumn<?, ?> appointmentIDColumn;
    @FXML private TableColumn<?, ?> startTimeColumn;
    @FXML private TableColumn<?, ?> appointmentTitleColumn;
    @FXML private TableColumn<?, ?> appointmentByTypeColumn;
    @FXML private TableColumn<?, ?> appointmentByMonthColumn;
    @FXML private TableColumn<?, ?> totalByTypeColumn;
    @FXML private TableColumn<?, ?> totalByMonthColumn;
    @FXML private TableColumn<?, ?> typeColumn;
    @FXML private Button backButton;
    @FXML private ComboBox<String> contactDropDown;
    @FXML private TableView<ReportsByType> appointmentByTypeTable;
    @FXML private Tab appointmentTotalsTab;
    @FXML private Tab contactScheduleTab;
    @FXML private Tab customesByDivisionTab;
    @FXML private TableView<ReportsByMonth> appointmentByMonthTable;
    @FXML private TableView<Reports> customerByDivisionTable;
    @FXML private TableColumn<?, ?> divisionNameColumn;
    @FXML private TableColumn<?, ?> divisionTotalColumn;

    /**
     * Initialize reports screen and fills appropriate fields
     * @throws SQLException
     */
    public void initialize() throws SQLException {

        divisionNameColumn.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
        divisionTotalColumn.setCellValueFactory(new PropertyValueFactory<>("divisionCount"));
        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        appointmentByTypeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        totalByTypeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTotal"));
        appointmentByMonthColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentMonth"));
        totalByMonthColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTotal"));
        ObservableList<Contact> contactsObservableList = ContactDatabaseAccess.loadAllContacts();
        ObservableList<String> allContactsNames = FXCollections.observableArrayList();
        contactsObservableList.forEach(contacts -> allContactsNames.add(contacts.getContactName()));
        contactDropDown.setItems(allContactsNames);

    }

    /**
     * Displays total appointments filtered by both type and month when tab is clicked
     * @throws SQLException
     */
    public void appointmentTotalsClick() throws SQLException {
        try {
            ObservableList<Appointment> allAppointmentsReport = AppointmentDatabaseAccess.loadAllAppointments();
            ObservableList<Month> appointmentMonthInfo = FXCollections.observableArrayList();
            ObservableList<Month> appointmentMonthTotal = FXCollections.observableArrayList();
            ObservableList<String> appointmentTypeInfo = FXCollections.observableArrayList();
            ObservableList<String> appointmentTypeCount = FXCollections.observableArrayList();
            ObservableList<ReportsByType> reportByType = FXCollections.observableArrayList();
            ObservableList<ReportsByMonth> reportByMonth = FXCollections.observableArrayList();
            allAppointmentsReport.forEach(appointments -> {
                appointmentTypeInfo.add(appointments.getAppointmentType());
            });
            allAppointmentsReport.stream().map(appointment -> {
                return appointment.getStart().getMonth();
            }).forEach(appointmentMonthInfo::add);
            appointmentMonthInfo.stream().filter(month -> {
                return !appointmentMonthTotal.contains(month);
            }).forEach(appointmentMonthTotal::add);
            for (Appointment a: allAppointmentsReport) {
                String typeOfAppointment = a.getAppointmentType();
                if (!appointmentTypeCount.contains(typeOfAppointment)) {
                    appointmentTypeCount.add(typeOfAppointment);
                }
            }
            for (Month m: appointmentMonthTotal) {
                int monthCount = Collections.frequency(appointmentMonthInfo, m);
                String nameOfMonth = m.name();
                ReportsByMonth monthOfAppointment = new ReportsByMonth(nameOfMonth, monthCount);
                reportByMonth.add(monthOfAppointment);
            }
            appointmentByMonthTable.setItems(reportByMonth);
            for (String t: appointmentTypeCount) {
                String appointmentTypeSet = t;
                int typeTotal = Collections.frequency(appointmentTypeInfo, t);
                ReportsByType appointmentTypeReport = new ReportsByType(appointmentTypeSet, typeTotal);
                reportByType.add(appointmentTypeReport);
            }
            appointmentByTypeTable.setItems(reportByType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Filters appointments by contact when a contact is selected from dropdown menu
     * Loads all contacts into the dropdown menu
     */
    @FXML
    public void contactScheduleClick() {
        try {
            String contactName = "";
            ObservableList<Appointment> allAppointmentsReport = AppointmentDatabaseAccess.loadAllAppointments();
            ObservableList<Appointment> appointmentDetails = FXCollections.observableArrayList();
            ObservableList<Contact> allContactsInfo = ContactDatabaseAccess.loadAllContacts();
            Appointment contactAppointmentInfo;
            String selectedContact = contactDropDown.getSelectionModel().getSelectedItem();
            for (Contact c: allContactsInfo) {
                if (selectedContact.equals(c.getContactName())) {
                    contactName = c.getContactName();
                }
            }
            for (Appointment a: allAppointmentsReport) {
                if (a.getContactName().equals(contactName)) {
                    contactAppointmentInfo = a;
                    appointmentDetails.add(contactAppointmentInfo);
                }
            }
            appointmentsReportsTable.setItems(appointmentDetails);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Filters customers by division and displays total for each division when tab clicked
     * @throws SQLException
     */
    public void customersByDivisionClick() throws SQLException {
        try {
            ObservableList<Reports> appointmentDivisions = ReportsDatabaseAccess.loadDivisionsToFilter();
            ObservableList<Reports> divisionsAdded= FXCollections.observableArrayList();
            appointmentDivisions.forEach(divisionsAdded::add);
            customerByDivisionTable.setItems(divisionsAdded);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Controls Back button
     * Returns to the main screen when clicked
     * @throws IOException
     */
    @FXML
    public void reportsBackClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../view/MainScreen.fxml"));
        Scene scene = new Scene(root);
        Stage MainScreenReturn = (Stage)((Node)event.getSource()).getScene().getWindow();
        MainScreenReturn.setScene(scene);
        MainScreenReturn.show();
    }
}
