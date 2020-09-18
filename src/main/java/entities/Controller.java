package entities;

import controllers.LogInController;
import controllers.MainMenuController;
import controllers.UserAccountController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class Controller{

    protected static UserAccount SESSION_USER;
    protected static int MIN_WIDTH = 600;
    protected static int MIN_HEIGHT = 600;

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
                MIN_HEIGHT = 400;
                MIN_WIDTH = 400;
                break;
            }

            case USER: {
                UserAccountController userController = loader.getController();
                MIN_HEIGHT = 400;
                MIN_WIDTH = 400;
                break;
            }
            case LOGIN: {
                LogInController logInController = loader.getController();
                MIN_HEIGHT = 550;
                MIN_WIDTH = 400;
                break;
            }

            default:
                break;
        }
        Scene scene  = new Scene(parent, 600, 600);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.setMinHeight(MIN_HEIGHT);
        window.setMinWidth(MIN_WIDTH);
        window.show();
    }
}