package controllers;

import com.jfoenix.controls.JFXComboBox;
import entities.Controller;
import entities.Employee;
import entities.UserAccount;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserAccountController extends Controller implements Initializable {

    @FXML
    private TableColumn<UserAccount, String> column1;
    @FXML private TableColumn<UserAccount, String> column2;
    @FXML private TableColumn<UserAccount, String> column3;

    @FXML private TableView<UserAccount> tableView;

    @FXML private TextField textfield1;
    @FXML private PasswordField passwordField1;
    @FXML private PasswordField passwordField2;
    @FXML private JFXComboBox<Employee> comboBox1;

    private ObservableList<UserAccount> userAccountObservableList;
    private ObservableList<Employee> employeeObservableList;

    private UserAccount selectedObject;

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

        // Loads the data into the TableView and ComboBox
        loadData();

        // When a row is selected, populates the text fields with the information from the row.
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedObject = newSelection;
                loadSelectedRecord();
            }
        });

        column1.setCellValueFactory(new PropertyValueFactory<>("username"));
        column2.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        column3.setCellValueFactory(new PropertyValueFactory<>("employeeEmail"));
    }

    public void addButtonPushed() {
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

    public void deleteButtonPushed() {
        selectedObject = tableView.getSelectionModel().getSelectedItem();

        if (selectedObject != null) {
            result = confirmationAlert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (selectedObject.delete()) {
                    loadData();

                    clearTextBox();
                    clearPasswordTextBox();
                    successAlert.setContentText("Record successfully deleted");
                    successAlert.showAndWait();
                } else {
                    failureAlert.setContentText("There was an error deleting the record from the database.");
                    failureAlert.showAndWait();
                }
            }
        } else {
            failureAlert.setContentText("Please select a valid record.");
            failureAlert.showAndWait();
        }
    }

    public void editButtonPushed() {
        if (tableView.getSelectionModel().getSelectedIndex() != -1) {
            if(comboBox1.getSelectionModel().getSelectedIndex() != -1) {
                if(textfield1.getText().equals(selectedObject.getUsername())) {
                    if (!passwordField1.getText().equals("") && !passwordField2.getText().equals("")) {
                        if(passwordField1.getText().equals(passwordField2.getText())){
                            result = confirmationAlert.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                int employeeID = comboBox1.getSelectionModel().getSelectedItem().getID();
                                String tempPassword = passwordField1.getText();
                                selectedObject.setEmployeeID(employeeID);
                                selectedObject.setPassword(tempPassword);
                                if (selectedObject.edit()) {
                                    clearTextBox();
                                    clearPasswordTextBox();
                                    loadData();

                                    successAlert.setContentText("Record successfully updated");
                                    successAlert.showAndWait();
                                } else {
                                    failureAlert.setContentText("There was an error updating the record to the database.");
                                    failureAlert.showAndWait();
                                }
                            }
                        }else {
                            clearPasswordTextBox();
                            failureAlert.setContentText("Passwords do not match.");
                            failureAlert.showAndWait();
                        }
                    } else
                    {
                        failureAlert.setContentText("Please enter a password.");
                        failureAlert.showAndWait();
                    }
                } else {
                    loadSelectedRecord();
                    clearPasswordTextBox();
                    failureAlert.setContentText("You can't edit the username for an account.");
                    failureAlert.showAndWait();
                }
            } else {
                failureAlert.setContentText("Please select an employee.");
                failureAlert.showAndWait();
            }
        } else {
            failureAlert.setContentText("Please select a valid record to update.");
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

    public void loadSelectedRecord(){
        textfield1.setText(selectedObject.getUsername());
        for(Employee employee : employeeObservableList) {
            if (selectedObject.getEmployeeID() == employee.getID()){
                comboBox1.getSelectionModel().select(employee);
            }
        }
    }

    public void loadData() {
        userAccountObservableList = UserAccount.findAll();
        if (!userAccountObservableList.isEmpty())
            tableView.setItems(userAccountObservableList);
        else
            tableView.getItems().clear();

        employeeObservableList = Employee.findAll();
        if (!employeeObservableList.isEmpty())
            comboBox1.setItems(employeeObservableList);
        else
            comboBox1.getItems().clear();
    }
}

