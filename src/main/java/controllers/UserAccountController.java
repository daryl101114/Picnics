package controllers;

import entities.Controller;
import entities.Employee;
import entities.UserAccount;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;


import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserAccountController extends Controller implements Initializable {

    @FXML private TableColumn<UserAccount, String> column1;
    @FXML private TableColumn<UserAccount, Employee> column2;
    @FXML private TableColumn<UserAccount, String> column3;

    @FXML private TableView<UserAccount> tableView;

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
            }
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Delete");
        menuItem1.setOnAction(event -> deleteRecord() );
        contextMenu.getItems().add(menuItem1);
        contextMenu.setMinWidth(300);

        contextMenu.setMaxHeight(30);
        tableView.setContextMenu(contextMenu);

        column1.maxWidthProperty().bind(tableView.widthProperty().multiply(0.3));
        column2.maxWidthProperty().bind(tableView.widthProperty().multiply(0.4));
        column3.maxWidthProperty().bind(tableView.widthProperty().multiply(0.3));

        column1.setCellValueFactory(new PropertyValueFactory<>("username"));
        column2.setCellValueFactory(new PropertyValueFactory<>("employee"));
        column3.setCellValueFactory(new PropertyValueFactory<>("employeeEmail"));

        column2.setCellFactory(ComboBoxTableCell.forTableColumn(employeeObservableList));
        column2.setOnEditCommit(event -> editRecord(event) );
    }

    public void addButtonPushed() {
//        if(comboBox1.getSelectionModel().getSelectedIndex() != -1) {
//            if(!textField1.getText().isEmpty()) {
//                if (!passwordField1.getText().equals("") && !passwordField2.getText().equals("")) {
//                    if (passwordField1.getText().equals(passwordField2.getText())) {
//                        String tempUsername = textField1.getText();
//                        int tempEmployeeID = comboBox1.getSelectionModel().getSelectedItem().getID();
//                        String tempPassword = passwordField1.getText();
//                        UserAccount temp = new UserAccount(tempUsername, tempPassword, tempEmployeeID);
//
//                        result = confirmationAlert.showAndWait();
//                        if (result.get() == ButtonType.OK) {
//                            if (!temp.exists()) {
//                                if (temp.add()) {
//                                    clearTextBox();
//                                    clearPasswordTextBox();
//                                    loadData();
//
//                                    successAlert.setContentText("Record successfully added");
//                                    successAlert.showAndWait();
//                                } else {
//                                    failureAlert.setContentText("There was an error adding the record to the database.");
//                                    failureAlert.showAndWait();
//                                }
//                            } else {
//                                failureAlert.setContentText("That username is already taken. Please select a different one.");
//                                failureAlert.showAndWait();
//                            }
//                        }
//                    } else {
//                        clearPasswordTextBox();
//                        failureAlert.setContentText("Passwords do not match.");
//                        failureAlert.showAndWait();
//                    }
//                } else {
//                    failureAlert.setContentText("Please enter a password.");
//                    failureAlert.showAndWait();
//                }
//            } else {
//                failureAlert.setContentText("Please insert a username.");
//                failureAlert.showAndWait();
//            }
//        } else {
//            failureAlert.setContentText("Please select an employee.");
//            failureAlert.showAndWait();
//        }
    }

    public void editRecord(TableColumn.CellEditEvent<UserAccount, Employee> event){
        selectedObject = tableView.getSelectionModel().getSelectedItem();
        selectedObject.setEmployeeID(event.getNewValue().getID());
        if (selectedObject.edit()) {
            selectedObject.clearEmployee();
            selectedObject.setEmployee(event.getNewValue());
            tableView.refresh();
            if(SESSION_USER != null){
                if(SESSION_USER.getUsername().equals(selectedObject.getUsername())) {
                    SESSION_USER = SESSION_USER.findByUsername(SESSION_USER.getUsername());
                }
            }
        } else {
            selectedObject.setEmployeeID(event.getOldValue().getID());
            tableView.refresh();
            failureAlert.setContentText("There was an error updating the record.");
            failureAlert.showAndWait();
        }
    }

    public void deleteRecord() {
        selectedObject = tableView.getSelectionModel().getSelectedItem();
        if (selectedObject != null) {
            result = confirmationAlert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (selectedObject.delete()) {
                    tableView.getItems().remove(selectedObject);
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

    public void loadData() {
        userAccountObservableList = UserAccount.findAll();
        employeeObservableList = Employee.findAll();
        if (!userAccountObservableList.isEmpty())
            tableView.setItems(userAccountObservableList);
        else
            tableView.getItems().clear();
    }
}

