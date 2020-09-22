package controllers;

import entities.Controller;
import entities.ControllerType;
import entities.GmailService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class MainMenuController extends Controller implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText(null);
        failureAlert = new Alert(Alert.AlertType.ERROR);
        failureAlert.setTitle("Failure");
        failureAlert.setHeaderText(null);
        confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to perform this operation?");

        /////////////////////////////////////////////
        GmailService gmail = new GmailService();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date begDate = dateFormat.parse("2020-09-01");
            Date endDate = Calendar.getInstance().getTime();
            squareEmailObservableList = FXCollections.observableList(gmail.getEmails(begDate, endDate, 100));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /////////////////////////////////////////////
    }

    public void userPushed(ActionEvent event) throws IOException {
        loadScene(event, "/views/UserAccount.fxml", ControllerType.USER);
    }

    public void logOutPushed(ActionEvent event) throws IOException {
        SESSION_USER = null;
        loadScene(event, "/views/LogIn.fxml", ControllerType.LOGIN);
    }

    public void employeesPushed(ActionEvent event) throws IOException {
        SESSION_USER = null;
        loadScene(event, "/views/Employee.fxml", ControllerType.EMPLOYEE);
    }
}
