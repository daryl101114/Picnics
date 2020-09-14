package controllers;

import entities.Controller;
import entities.ControllerType;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController extends Controller implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void logOutPushed(ActionEvent event) throws IOException {
        loadScene(event, "/views/LogIn.fxml", ControllerType.LOGIN);
    }
}
