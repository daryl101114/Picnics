package controllers;

import entities.Controller;
import entities.ControllerType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LogInController extends Controller implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private Alert wrongCredentialsAlert;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        wrongCredentialsAlert = new Alert(Alert.AlertType.WARNING);
        wrongCredentialsAlert.setTitle("Error");
    }

    public void enterButtonPressed(ActionEvent event) throws IOException {
        if(usernameField.getText().isEmpty()){
            wrongCredentialsAlert.setHeaderText("Empty Username");
            wrongCredentialsAlert.setContentText("Please enter a username");
            wrongCredentialsAlert.showAndWait();
        } else if(passwordField.getText().isEmpty()){
            wrongCredentialsAlert.setHeaderText("Empty Password");
            wrongCredentialsAlert.setContentText("Please enter a password");
            wrongCredentialsAlert.showAndWait();
        } else {
            String username = usernameField.getText();
            String password = passwordField.getText();

            statement = "SELECT password FROM User_Account WHERE username = '" +  username + "'";
            ResultSet resultSet = null;
            try {
                conn = DriverManager.getConnection(connectionURL);
                stmt = conn.prepareStatement(statement);
                resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    if(resultSet.getString("password").equals(password)){
                        loadScene(event, "/views/MainMenu.fxml", ControllerType.MAINMENU);
                    }else {
                        wrongCredentialsAlert.setHeaderText("Invalid Password ");
                        wrongCredentialsAlert.setContentText("Please enter a valid Password");
                        wrongCredentialsAlert.showAndWait();
                    }
                } else {
                    wrongCredentialsAlert.setHeaderText("Invalid username ");
                    wrongCredentialsAlert.setContentText("Please enter a valid username");
                    wrongCredentialsAlert.showAndWait();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) { /* ignored */}
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) { /* ignored */}
                }
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) { /* ignored */}
                }
            }
        }
    }
}