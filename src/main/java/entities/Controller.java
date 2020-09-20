package entities;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public abstract class Controller{

    protected static UserAccount SESSION_USER;
    protected static double MIN_WIDTH = 600;
    protected static double MIN_HEIGHT = 600;
    protected static double PREF_WIDTH = 600;
    protected static double PREF_HEIGHT = 600;

    protected static Alert successAlert;
    protected static Alert failureAlert;
    protected static Alert confirmationAlert;
    protected static Alert warningAlert;

    protected static ObservableList<UserAccount> userAccountObservableList;
    protected static ObservableList<Employee> employeeObservableList;

    public void mainMenuPushed(ActionEvent event) throws IOException {
        loadScene(event, "/views/MainMenu.fxml", ControllerType.MAIN_MENU);
    }

    public void loadScene(ActionEvent event, String file, ControllerType type) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(file));
        Parent parent = loader.load();

        switch (type){
            case MAIN_MENU: {
                MIN_HEIGHT = 400;
                MIN_WIDTH = 400;
                PREF_HEIGHT = 600;
                PREF_WIDTH = 600;
                break;
            }

            case USER: {
                MIN_HEIGHT = 400;
                MIN_WIDTH = 400;
                PREF_HEIGHT = 600;
                PREF_WIDTH = 600;
                break;
            }
            case LOGIN: {
                MIN_HEIGHT = 550;
                MIN_WIDTH = 400;
                PREF_HEIGHT = 550;
                PREF_WIDTH = 400;
                break;
            }

            case EMPLOYEE: {
                MIN_HEIGHT = 400;
                MIN_WIDTH = 400;
                PREF_HEIGHT = 600;
                PREF_WIDTH = 600;
                break;
            }

            default:
                break;
        }
        Scene scene  = new Scene(parent, PREF_WIDTH, PREF_HEIGHT);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.setMinHeight(MIN_HEIGHT);
        window.setMinWidth(MIN_WIDTH);
        window.show();
    }

    public void loadChildScene(ActionEvent event, String file, ControllerType type) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(file));
        Parent parent = loader.load();

        switch (type){
            case ADD_USER: {
                MIN_HEIGHT = 400;
                MIN_WIDTH = 400;
                PREF_HEIGHT = 400;
                PREF_WIDTH = 400;
                break;
            }

            default:
                break;
        }
        Scene scene  = new Scene(parent, PREF_WIDTH, PREF_HEIGHT);
        Stage childWindow = new Stage();
        Stage parentWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
        childWindow.setScene(scene);
        childWindow.setMinHeight(MIN_HEIGHT);
        childWindow.setMinWidth(MIN_WIDTH);
        childWindow.initStyle(StageStyle.DECORATED);
        childWindow.initModality(Modality.WINDOW_MODAL);
        childWindow.initOwner(parentWindow);
        parentWindow.toFront();
        childWindow.show();
    }

    public void closeChildWindow(ActionEvent event){
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}