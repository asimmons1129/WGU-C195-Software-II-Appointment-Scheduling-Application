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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.DatabasePull;
import model.Appointment;
import model.User;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Controls the login screen
 * @author Andre Simmons
 */
public class   Login {
    @FXML private Label loginLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label timeZoneLabel;
    @FXML private Label timeZoneDisplay;
    @FXML private TextField usernameText;
    @FXML private TextField passwordText;
    @FXML private Button loginButton;
    @FXML private Button exitButton;

    /**
     * Initializes the screen
     * Sets each text to the language of the local machine
     * Displays the machine's local time zone
     * @throws Exception
     */
    public void initialize() throws Exception{
        try{
            Locale locale = Locale.getDefault();
            Locale.setDefault(locale);
            ResourceBundle localLanguage = ResourceBundle.getBundle("language/local_language", Locale.getDefault());
            loginLabel.setText(localLanguage.getString("LoginLabel"));
            usernameLabel.setText(localLanguage.getString("UsernameLabel"));
            passwordLabel.setText(localLanguage.getString("PasswordLabel"));
            timeZoneLabel.setText(localLanguage.getString("TimeZoneLabel"));
            loginButton.setText(localLanguage.getString("LoginLabel"));
            exitButton.setText(localLanguage.getString("ExitButton"));
            timeZoneDisplay.setText(String.valueOf(ZoneId.systemDefault()));
        }
        catch(MissingResourceException e){
            System.out.println(e);
        }
        catch(Exception e){
            System.out.println(e);
        }

    }


    /**
     * Controls the click action for the login button
     * Contains lambda functions to add usernames and passwords for each user from the database
     * Lambda function helps to replace the for loops
     * Displays error message in local machine's language if invalid credentials entered
     * Loads the main console screen when login successful
     * Logs successful/unsuccessful login attempts in txt file with date and time
     * Displays message if appointment upcoming in 15 mins with appointment id and start time
     * Displays message if no upcoming appointments
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void loginOnClick(ActionEvent actionEvent) throws SQLException, IOException {
            ResourceBundle localLanguage = ResourceBundle.getBundle("language/local_language", Locale.getDefault());
            ObservableList<User> listOfUsers = DatabasePull.getUsersFromDatabase();
            ObservableList<String> usernameList = FXCollections.observableArrayList();
            ObservableList<String> passwordList = FXCollections.observableArrayList();

            /*
            Lambda functions adding usernames and passwords to their respective lists
             */
            listOfUsers.forEach(user -> usernameList.add(user.getUsername()));
            listOfUsers.forEach(user -> passwordList.add(user.getPassword()));

            FileWriter txtLoggerFile = new FileWriter("login_activity.txt", true);
            if(usernameList.contains(usernameText.getText()) && passwordList.contains(passwordText.getText())){
                Parent parent = FXMLLoader.load(getClass().getResource("../views/MainConsole.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();

                txtLoggerFile.write("The user with username: " + usernameText.getText() + " successfully logged in at " + LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_TIME) + " on " + LocalDate.now() + "\n\n");
                int appointmentId = 0;
                LocalDateTime upcomingStart = null;
                if(appointmentSoon()){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("The appointment with ID: " + appointmentUpcoming(appointmentId) + " starts within the next 15 minutes! This appointment starts at " + upcomingStart(upcomingStart));
                    alert.showAndWait();
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("There are no upcoming appointments");
                    alert.showAndWait();
                }
            }
            else if (!usernameList.contains(usernameText.getText()) || !passwordList.contains(passwordText.getText()) || usernameText.getText().isEmpty() || passwordText.getText().isEmpty()){
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle(localLanguage.getString("Error"));
                a.setContentText(localLanguage.getString("IncorrectLogin"));
                a.showAndWait();
                txtLoggerFile.write("The user with username: " + usernameText.getText() + " failed to login at " + LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_TIME) + " on " + LocalDate.now() + "\n\n");
            }
            txtLoggerFile.close();
    }

    /**
     * Checks if an appointment starts within the next 15 mins
     * Compares the appointment start time with the local machine's time to determine this
     * @return upcoming
     * @throws SQLException
     */
    public Boolean appointmentSoon() throws SQLException {
        boolean upcoming = false;
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime timeAhead = LocalDateTime.now().plusMinutes(15);
        ObservableList<Appointment> listOfAppointments = DatabasePull.getAppointmentsFromDatabase();
        for(Appointment a: listOfAppointments){
            if(a.getStartTime().isEqual(timeAhead) || (a.getStartTime().isAfter(timeNow) && a.getStartTime().isBefore(timeAhead))){
                upcoming = true;
            }
        }
        return upcoming;
    }

    /**
     * Grabs the appointment id if an appointment starts within the next 15 mins
     * @param appointmentId
     * @return appointmentId
     * @throws SQLException
     */
    public int appointmentUpcoming(int appointmentId) throws SQLException {
        ObservableList<Appointment> listOfAppointments = DatabasePull.getAppointmentsFromDatabase();
        for(Appointment a: listOfAppointments){
            if(appointmentSoon()){
                appointmentId = a.getAppointmentId();
            }
        }
        return appointmentId;
    }

    /**
     * Grabs the appointment start date/time if the appointment starts within the next 15 mins
     * @param start
     * @return
     * @throws SQLException
     */
     public LocalDateTime upcomingStart(LocalDateTime start) throws SQLException{
         ObservableList<Appointment> listOfAppointments = DatabasePull.getAppointmentsFromDatabase();
         for(Appointment a: listOfAppointments){
             if(appointmentSoon()){
                 start = a.getStartTime();
             }
         }
         return start;
     }

    /**
     * Controls the click action for the exit button
     * Closes the application when clicked
     * @param actionEvent
     */
     public void exitOnClick(ActionEvent actionEvent){
        System.exit(0);
     }
}
