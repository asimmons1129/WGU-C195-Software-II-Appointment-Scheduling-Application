package controller;

import DatabaseAccess.AppointmentDatabaseAccess;
import DatabaseAccess.UserDatabaseAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controls the Login Page fxml
 * @author Andre Simmons
 */
public class LoginPageController implements Initializable{
    @FXML private Button loginButton;
    @FXML private TextField timeZoneField;
    @FXML private PasswordField passwordField;
    @FXML private TextField usernameField;
    @FXML private Label passwordLabel;
    @FXML private Label usernameLabel;
    @FXML private Label loginLabel;
    @FXML private Button exitButton;
    @FXML private Label timeZoneLabel;
    @FXML private Label languageLabel;
    @FXML private ComboBox<String> languageSelect;


    Stage stage;

    /**
     * Controls cancel button
     * Exits the app when clicked
     * @param event
     */
    public void loginCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Initializes the login page
     * Contains correct time zone/language based on pc settings
     * @param url, rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        try
        {
            Locale machineLocale = Locale.getDefault();
            Locale.setDefault(machineLocale);
            ZoneId timeZone = ZoneId.systemDefault();
            ObservableList<String> languageList = FXCollections.observableArrayList();
            languageList.add(0, "English");
            languageList.add(1, "Français");
            timeZoneField.setText(String.valueOf(timeZone));
            rb = ResourceBundle.getBundle("language_properties/login", Locale.getDefault());
            loginLabel.setText(rb.getString("Login"));
            usernameLabel.setText(rb.getString("Username"));
            passwordLabel.setText(rb.getString("Password"));
            loginButton.setText(rb.getString("Login"));
            exitButton.setText(rb.getString("Exit"));
            timeZoneLabel.setText(rb.getString("TimeZone"));
            languageLabel.setText(rb.getString("Language"));
            languageSelect.setItems(languageList);
        } catch(MissingResourceException e) {
            System.out.println("Resource file missing: " + e);
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * Controls the login button on click
     * Validates login credentials and shows any upcoming appointments
     * Displays Error if user/password is invalid
     * Records successful and unsuccessful login attempts in txt file
     * @param event
     * @throws SQLException
     * @throws IOException
     * @throws Exception
     **/

    @FXML
    private void loginButton(ActionEvent event) throws SQLException, IOException, Exception {
        try {
            ObservableList<Appointment> listOfAppointments = AppointmentDatabaseAccess.loadAllAppointments();
            LocalDateTime timeNow = LocalDateTime.now();
            LocalDateTime time15MinAhead = LocalDateTime.now().plusMinutes(15);
            LocalDateTime appointmentStart;
            boolean appointmentUpcoming = false;
            boolean appointmentNow = false;
            int appointmentID = 0;
            LocalDateTime timeDisplayed = null;
            ResourceBundle rb = ResourceBundle.getBundle("language_properties/login", Locale.getDefault());
            String username = usernameField.getText();
            String password = passwordField.getText();
            int userID = UserDatabaseAccess.validateUser(username, password);
            FileWriter loggerFile = new FileWriter("login_activity.txt", true);
            PrintWriter loggerOutput = new PrintWriter(loggerFile);
            if (userID > 0) {
                Parent parent = FXMLLoader.load(getClass().getResource("../view/MainScreen.fxml"));
                Scene scene = new Scene(parent);
                stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();

                /*
                 * shows successful login
                 */
                loggerOutput.print("User: " + username + " had a successful login at the time: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");

                /*
                 * Checks to see if any appointments are upcoming
                 * Displays alert if appointment within 15 mins
                 * Displays a different alert if the appointment is starting now
                 * Displays alert if no appointments are upcoming
                 */
                for (Appointment appointment: listOfAppointments) {
                    appointmentStart = appointment.getStart();
                    if ((appointmentStart.isAfter(timeNow.plusSeconds(60)) && appointmentStart.isBefore(time15MinAhead)) || (appointmentStart.isEqual(time15MinAhead))) {
                        appointmentID = appointment.getAppointmentID();
                        timeDisplayed = appointmentStart;
                        appointmentUpcoming = true;
                    }
                    if(appointmentStart.isEqual(timeNow) || (appointmentStart.isAfter(timeNow) && appointmentStart.isBefore(timeNow.plusSeconds(60)))){
                        appointmentID = appointment.getAppointmentID();
                        timeDisplayed = appointmentStart;
                        appointmentNow = true;
                    }
                }
                if (appointmentUpcoming != false){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "The appointment with ID: " + appointmentID + " is within the next 15 mins. This appointment has a start date/time of: " + timeDisplayed);
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    System.out.println("There is an appointment within 15 minutes");
                } else if (appointmentNow != false) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "The appointment with ID: " + appointmentID + " is about to start. This appointment has a start date/time of: " + timeDisplayed);
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    System.out.println("There is an appointment about to start");
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have no upcoming appointments.");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    System.out.println("No Upcoming Appointments");
                }
            } else if (userID < 0) {
                String selectedLanguage = languageSelect.getSelectionModel().getSelectedItem();
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle(rb.getString("Error"));
                alert1.setContentText(rb.getString("Incorrect"));
                alert1.show();
                if (selectedLanguage.equals("Français")) {
                    Alert a1 = new Alert(Alert.AlertType.ERROR);
                    a1.setTitle("Erreur");
                    a1.setContentText("Nom d'utilisateur / Mot de passe incorrect. Veuillez réessayer!");
                    a1.show();
                    return;
                } else if (selectedLanguage.equals("English")) {
                    Alert a3 = new Alert(Alert.AlertType.ERROR);
                    a3.setTitle("Error");
                    a3.setContentText("Incorrect username/password.  Please Try Again!");
                    a3.show();
                    return;
                }
                loggerOutput.print("User: " + username + " failed to login at the time: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");
            }
            loggerOutput.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * Extra language option
     * Controls the language combo box
     * Switches all login page text to the language selected
     * @param event
     */
    @FXML
    private void languageOnClick(ActionEvent event){
        String selectedLanguage = languageSelect.getSelectionModel().getSelectedItem();
        String french = "Français";
        if(selectedLanguage.equals(french)){
            ZoneId timeZone = ZoneId.systemDefault();
            timeZoneField.setText(String.valueOf(timeZone));
            loginLabel.setText("Connexion");
            usernameLabel.setText("Nom d'utilisateur");
            passwordLabel.setText("Mot de passe");
            loginButton.setText("Connexion");
            exitButton.setText("Sortir");
            timeZoneLabel.setText("Fuseau Horaire");
            languageLabel.setText("Langue");
        } else {
            ZoneId timeZone = ZoneId.systemDefault();
            timeZoneField.setText(String.valueOf(timeZone));
            loginLabel.setText("Login");
            usernameLabel.setText("Username");
            passwordLabel.setText("Password");
            loginButton.setText("Login");
            exitButton.setText("Exit");
            timeZoneLabel.setText("TimeZone");
            languageLabel.setText("Language");
        }

    }
}
