package controllers;

import entities.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import models.InvoiceItem;


import java.net.URL;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.ResourceBundle;

public class AddEvent extends Controller implements Initializable{

    @FXML private TextField customer_textField1;
    @FXML private TextField customer_textField2;
    @FXML private TextField customer_textField3;
    @FXML private TextField customer_textField4;

    @FXML private DatePicker event_datePicker;
    @FXML private TextField event_textfield1;
    @FXML private TextField event_textfield2;
    @FXML private TextField event_textfield3;
    @FXML private TextField event_textfield4;
    @FXML private TextField event_textfield5;
    @FXML private TextField event_textfield6;
    @FXML private TextField event_textfield7;

    @FXML private TableColumn<InvoiceItem, String> column1;
    @FXML private TableColumn<InvoiceItem, String> column2;
    @FXML private TableColumn<InvoiceItem, String> column3;
    @FXML private TableColumn<InvoiceItem, String> column4;

    @FXML private TableView<InvoiceItem> tableView;

    private ObservableList<InvoiceItem> invoiceItemObservableList;
    private InvoiceItem selectedAddon;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        invoiceItemObservableList = FXCollections.observableArrayList();
        tableView.setItems(invoiceItemObservableList);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedAddon = newSelection;
            }
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Remove Addon");
        menuItem1.setOnAction(event -> removeAddon() );
        MenuItem menuItem2 = new MenuItem("New Addon");
        menuItem2.setOnAction(event -> newAddon() );

        contextMenu.getItems().add(menuItem1);
        contextMenu.getItems().add(menuItem2);
        tableView.setContextMenu(contextMenu);

        column1.maxWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        column2.maxWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        column3.maxWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        column4.maxWidthProperty().bind(tableView.widthProperty().multiply(0.25));

        column1.setCellValueFactory(new PropertyValueFactory<>("itemDesc"));
        column2.setCellValueFactory(new PropertyValueFactory<>("itemQuantityString"));
        column3.setCellValueFactory(new PropertyValueFactory<>("itemCostString"));
        column4.setCellValueFactory(new PropertyValueFactory<>("itemSupplierCostString"));

        column1.setCellFactory(TextFieldTableCell.forTableColumn());
        column2.setCellFactory(TextFieldTableCell.forTableColumn());
        column3.setCellFactory(TextFieldTableCell.forTableColumn());
        column4.setCellFactory(TextFieldTableCell.forTableColumn());

        column1.setOnEditCommit(event -> editRecord(event, 1) );
        column2.setOnEditCommit(event -> editRecord(event, 2) );
        column3.setOnEditCommit(event -> editRecord(event, 3) );
        column4.setOnEditCommit(event -> editRecord(event, 4) );
    }

    public void editRecord(TableColumn.CellEditEvent<InvoiceItem, String> event, int column){
        selectedAddon = tableView.getSelectionModel().getSelectedItem();
        boolean success = false;
        String errorMessage = "There was an error updating the record.";
        try {
            switch (column) {
                case 1:
                    selectedAddon.setItemDesc(event.getNewValue());
                    success = true;
                    break;
                case 2:
                    int tmpQuantity = Integer.parseInt(event.getNewValue());
                    selectedAddon.setItemQuantity(tmpQuantity);
                    selectedAddon.setItemQuantityString(event.getNewValue());
                    success = true;
                    break;
                case 3:
                    double tmpCost = Double.parseDouble(event.getNewValue());
                    selectedAddon.setItemCost(tmpCost);
                    selectedAddon.setItemCostString(event.getNewValue());
                    success = true;
                    break;
                case 4:
                    double tmpSupCost = Double.parseDouble(event.getNewValue());
                    selectedAddon.setItemSupplierCost(tmpSupCost);
                    selectedAddon.setItemSupplierCostString(event.getNewValue());
                    success = true;
                    break;
                default:
                    break;
            }
        }catch(Exception ex){
            errorMessage = "Please enter a valid number.";
        }
        if (success){
            if(selectedAddon.exists()) {
                if(selectedAddon.edit()) {
                    tableView.refresh();
                } else {
                    success = false;
                }
            } else
                tableView.refresh();
        }

        if(!success) {
            switch (column){
                case 1:
                    selectedAddon.setItemDesc(event.getOldValue());
                    //try to auto populate the other fields?
                    break;
                case 2:
                    selectedAddon.setItemQuantityString(event.getOldValue());
                    if(event.getOldValue() != null)
                        selectedAddon.setItemQuantity(Integer.parseInt(event.getOldValue()));
                    break;
                case 3:
                    selectedAddon.setItemCostString(event.getOldValue());
                    if(event.getOldValue() != null)
                        selectedAddon.setItemCost(Double.parseDouble(event.getOldValue()));
                    break;
                case 4:
                    selectedAddon.setItemSupplierCostString(event.getOldValue());
                    if(event.getOldValue() != null)
                        selectedAddon.setItemSupplierCost(Double.parseDouble(event.getOldValue()));
                    break;
                default:
                    break;
            }
            tableView.refresh();
            failureAlert.setContentText(errorMessage);
            failureAlert.showAndWait();
        }
    }

    public void removeAddon(){
        if(selectedAddon != null)
            invoiceItemObservableList.remove(selectedAddon);
        tableView.refresh();
    }

    public void newAddon(){
        InvoiceItem tmp = new InvoiceItem();
        tmp.setItemDesc("New Addon");
        invoiceItemObservableList.add(tmp);
        tableView.refresh();
    }


    public void addButtonPushed(ActionEvent event) {
        if(validateFields()){
            if(validateAddons()){

            }
        };
    }

    private boolean validateAddons() {



        return true;
    }

    private boolean validateFields() {
        if(customer_textField1.getText().trim().equals(""))
        {
            failureAlert.setContentText("Please enter customer name.");
            failureAlert.showAndWait();
            return false;
        }

        if(customer_textField2.getText().trim().equals(""))
        {
            failureAlert.setContentText("Please enter customer email.");
            failureAlert.showAndWait();
            return false;
        }

        if(customer_textField3.getText().trim().equals(""))
        {
            failureAlert.setContentText("Please enter customer phone number.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_datePicker.getValue() == null){
            failureAlert.setContentText("Please select a date.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield1.getText().trim().equals("")){
            failureAlert.setContentText("Please enter a time.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield2.getText().trim().equals("")){
            failureAlert.setContentText("Please enter guest count.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield3.getText().trim().equals("")){
            failureAlert.setContentText("Please enter a guests count price.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield4.getText().trim().equals("")){
            failureAlert.setContentText("Please enter a location.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield5.getText().trim().equals("")){
            failureAlert.setContentText("Please enter a style.");
            failureAlert.showAndWait();
            return false;
        }

        try{
            LocalTime time = LocalTime.parse(event_textfield1.getText(), DateTimeFormatter.ofPattern("HH:mm:ss"));
        }catch (Exception ex){
            failureAlert.setContentText("Enter time in the following format: HH:mm:ss.");
            failureAlert.showAndWait();
            return false;
        }

        try{
            double guest_price = Double.parseDouble(event_textfield3.getText());
        }catch (Exception ex){
            failureAlert.setContentText("Please enter a valid guest price.");
            failureAlert.showAndWait();
            return false;
        }

        return true;
    }

    public void cancelButtonPushed(ActionEvent event){
        closeChildWindow(event);
    }

}

