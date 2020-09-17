package entities;

import controllers.LogInController;
import controllers.MainMenuController;
import controllers.UserController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller extends QueryObject{

    protected static UserAccount SESSION_USER;


    public void mainMenuPushed(ActionEvent event) throws IOException {
        loadScene(event, "/views/MainMenu.fxml", ControllerType.MAIN_MENU);
    }

    public void loadScene(ActionEvent event, String file, ControllerType type) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(file));
        Parent parent = loader.load();

        // Initialize controllers below (variables, etc.)
        switch (type){
            case MAIN_MENU: {
                MainMenuController mainMenuController = loader.getController();
                break;
            }

            case USER: {
                UserController userController = loader.getController();
                break;
            }
            case LOGIN: {
                LogInController logInController = loader.getController();
                break;
            }

            default:
                break;
        }

        Scene scene = new Scene(parent, 900, 900);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}