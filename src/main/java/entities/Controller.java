package entities;

import controllers.LogInController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller extends QueryObject{

    public void mainMenuPushed(ActionEvent event) throws IOException {
        loadScene(event, "/views/MainMenu.fxml", ControllerType.MAINMENU);
    }

    public void loadScene(ActionEvent event, String file, ControllerType type) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(file));
        Parent parent = loader.load();
        Scene scene;

        // Initialize controllers below (variables, etc.)
        switch (type){
            case MAINMENU: {
                scene = new Scene(parent, 900, 900);
                break;
            }
            case LOGIN: {
                LogInController controller = loader.getController();
                scene = new Scene(parent, 900, 900);
                break;
            }

            default:
                scene = new Scene(parent, 900, 900);
                break;
        }

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}