package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Main function that starts the application
 * @author Andre Simmons
 */
public class Main extends Application {
    /**
     * loads the login screen when the program is run
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("language/local_language", Locale.getDefault());
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/Login.fxml"));
        stage.setTitle(resourceBundle.getString("Title"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }

    /**
     * Connects to the database
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        ConnectDB.startConnection();
        launch(args);
        ConnectDB.closeConnection();
    }
}
