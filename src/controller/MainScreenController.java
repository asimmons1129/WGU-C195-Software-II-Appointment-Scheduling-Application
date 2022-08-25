package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controls main screen fxml
 * @author Andre Simmons
 */
public class MainScreenController {
    @FXML private Button appointmentsButton;
    @FXML private Button customersButton;
    @FXML private Button reportsButton;
    @FXML private Button exitButton;

    /**
     * Controls Appointments button
     * Goes to Main Appointments screen when clicked
     * @param event
     * @throws IOException
     */
    @FXML
    void appointmentsClick(ActionEvent event) throws IOException {
        Parent appointmentMenu = FXMLLoader.load(getClass().getResource("../view/MainAppointments.fxml"));
        Scene scene = new Scene(appointmentMenu);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * Controls Customer button
     * Goes to Customer screen when clicked
     * @param event
     * @throws IOException
     */
    @FXML
    void customerClick(ActionEvent event) throws IOException {

        Parent customerMenu = FXMLLoader.load(getClass().getResource("../view/Customer.fxml"));
        Scene scene = new Scene(customerMenu);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * Controls reports button
     * Goes to reports screen when clicked
     * @param event
     * @throws IOException
     */
    @FXML
    void reportsClick(ActionEvent event) throws IOException {

        Parent reportMenu = FXMLLoader.load(getClass().getResource("../view/Reports.fxml"));
        Scene scene = new Scene(reportMenu);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * Controls exit button
     * Exits app when clicked
     * @param ExitButton
     */
    public void exitClick(ActionEvent ExitButton) {
        Stage stage = (Stage) ((Node) ExitButton.getSource()).getScene().getWindow();
        stage.close();
    }
}
