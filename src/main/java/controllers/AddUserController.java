package controllers;

import com.jfoenix.controls.JFXComboBox;
import entities.Controller;
import entities.ControllerType;
import entities.Employee;
import entities.UserAccount;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddUserController extends Controller implements Initializable {
    @FXML private TextField textfield1;
    @FXML private PasswordField passwordField1;
    @FXML private PasswordField passwordField2;
    @FXML private JFXComboBox<Employee> comboBox1;

//    private ObservableList<UserAccount> userAccountObservableList;
    private ObservableList<Employee> employeeObservableList;

//    private UserAccount selectedObject;

    private Alert successAlert;
    private Alert failureAlert;
    private Alert confirmationAlert;
    Optional<ButtonType> result;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText(null);
        failureAlert = new Alert(Alert.AlertType.ERROR);
        failureAlert.setTitle("Failure");
        failureAlert.setHeaderText(null);
        confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to perform this operation?");

        loadData();
    }

    public void addButtonPushed(ActionEvent event) throws IOException {
//        loadScene(event, "/views/AddUser.fxml", ControllerType.ADDUSER);
        if(comboBox1.getSelectionModel().getSelectedIndex() != -1) {
            if(!textfield1.getText().isEmpty()) {
                if (!passwordField1.getText().equals("") && !passwordField2.getText().equals("")) {
                    if (passwordField1.getText().equals(passwordField2.getText())) {
                        String tempUsername = textfield1.getText();
                        int tempEmployeeID = comboBox1.getSelectionModel().getSelectedItem().getID();
                        String tempPassword = passwordField1.getText();
                        UserAccount temp = new UserAccount(tempUsername, tempPassword, tempEmployeeID);

                        result = confirmationAlert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            if (!temp.exists()) {
                                if (temp.add()) {
                                    clearTextBox();
                                    clearPasswordTextBox();
                                    loadData();

                                    successAlert.setContentText("Record successfully added");
                                    successAlert.showAndWait();
                                } else {
                                    failureAlert.setContentText("There was an error adding the record to the database.");
                                    failureAlert.showAndWait();
                                }
                            } else {
                                failureAlert.setContentText("That username is already taken. Please select a different one.");
                                failureAlert.showAndWait();
                            }
                        }
                    } else {
                        clearPasswordTextBox();
                        failureAlert.setContentText("Passwords do not match.");
                        failureAlert.showAndWait();
                    }
                } else {
                    failureAlert.setContentText("Please enter a password.");
                    failureAlert.showAndWait();
                }
            } else {
                failureAlert.setContentText("Please insert a username.");
                failureAlert.showAndWait();
            }
        } else {
            failureAlert.setContentText("Please select an employee.");
            failureAlert.showAndWait();
        }

    }
    public void clearTextBox(){
        textfield1.clear();
        comboBox1.getSelectionModel().clearSelection();
    }

    public void clearPasswordTextBox(){
        passwordField1.clear();
        passwordField2.clear();
    }
    // Loads the data into the ComboBox
        public void loadData() {


            employeeObservableList = Employee.findAll();
            if (!employeeObservableList.isEmpty())
                comboBox1.setItems(employeeObservableList);
            else
                comboBox1.getItems().clear();
        }
    }

