package main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Main class for the application
 * @author Andre Simmons
 */
public class Main extends Application{
    /**
     * Initializes the Login Page
     * @param stage
     * @throws IOException
     */
    @Override
    /**
     * Function to start the application with the appropriate Login Page fxml
     */
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/LoginPage.fxml"));
        stage.setTitle("Appointment Application");
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }

    /**
     * Function to launch the database connection
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        ConnectDB.startConnection();
        launch(args);
        ConnectDB.closeConnection();
    }

}
