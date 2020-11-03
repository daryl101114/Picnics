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

import models.*;


import java.net.URL;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.ResourceBundle;

public class AddEventController extends Controller implements Initializable{

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
    @FXML private TextField event_textfield8;
    @FXML private TextField event_textfield9;

    @FXML private TableColumn<InvoiceItem, String> column1;
    @FXML private TableColumn<InvoiceItem, String> column2;
    @FXML private TableColumn<InvoiceItem, String> column3;
    @FXML private TableColumn<InvoiceItem, String> column4;
    @FXML private TableColumn<InvoiceItem, String> column5;

    @FXML private TableView<InvoiceItem> tableView;

    private ObservableList<InvoiceItem> invoiceItemObservableList;
    private InvoiceItem selectedAddon;

    private boolean isNew = false;
    private boolean createSquareInvoice = false;
    private boolean isImport = false;

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

        column1.maxWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        column2.maxWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        column3.maxWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        column4.maxWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        column5.maxWidthProperty().bind(tableView.widthProperty().multiply(0.20));

        column1.setCellValueFactory(new PropertyValueFactory<>("itemDesc"));
        column2.setCellValueFactory(new PropertyValueFactory<>("itemQuantityString"));
        column3.setCellValueFactory(new PropertyValueFactory<>("itemCostString"));
        column4.setCellValueFactory(new PropertyValueFactory<>("itemSupplierCostString"));
        column5.setCellValueFactory(new PropertyValueFactory<>("note"));

        column1.setCellFactory(TextFieldTableCell.forTableColumn());
        column2.setCellFactory(TextFieldTableCell.forTableColumn());
        column3.setCellFactory(TextFieldTableCell.forTableColumn());
        column4.setCellFactory(TextFieldTableCell.forTableColumn());
        column5.setCellFactory(TextFieldTableCell.forTableColumn());

        column1.setOnEditCommit(event -> editRecord(event, 1) );
        column2.setOnEditCommit(event -> editRecord(event, 2) );
        column3.setOnEditCommit(event -> editRecord(event, 3) );
        column4.setOnEditCommit(event -> editRecord(event, 4) );
        column5.setOnEditCommit(event -> editRecord(event, 5) );
    }

    public void autoPopulate(){
        if(selectedSquareEmail != null){
            customer_textField1.setText(selectedSquareEmail.getEventName());
            customer_textField2.setText(selectedSquareEmail.getEventEmail());
            customer_textField3.setText(selectedSquareEmail.getEventPhoneNumber());
            customer_textField4.setText(selectedSquareEmail.getEventSource());

            boolean isAm = selectedSquareEmail.getEventTime().toUpperCase().contains("AM");
            String timeString;
            String dateString = selectedSquareEmail.getEventDate();
            if(isAm)
                timeString = selectedSquareEmail.getEventTime().toUpperCase().split("AM")[0].trim();
            else
                timeString = selectedSquareEmail.getEventTime().toUpperCase().split("PM")[0].trim();

            String[] timeArray = timeString.split(":");
            String[] dateArray = dateString.split("/");
            timeString = "";
            dateString = "";
            for (int i = 0; i < timeArray.length; i++) {
                if(timeArray[i].length() == 1) {
                    timeArray[i] = "0" + timeArray[i];
                }
                if(i == 0){
                    timeArray[0] = isAm ? timeArray[0] : String.valueOf(Integer.parseInt(timeArray[0]) + 12);
                }
                if(i + 1 < timeArray.length)
                    timeString += timeArray[i] + ":";
                else{
                    timeString += timeArray[i];
                }
            }

            for (int i = 0; i < dateArray.length; i++) {
                if(i < 2 && dateArray[i].length() == 1) {
                    dateArray[i] = "0" + dateArray[i];
                }
                if(i + 1 < timeArray.length)
                    dateString += dateArray[i] + "/";
                else{
                    dateString += dateArray[i];
                }
            }

            DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate date = LocalDate.parse(dateString, formatter);

            event_datePicker.setValue(date);
            event_textfield1.setText(timeString);
            event_textfield2.setText(selectedSquareEmail.getEventGuestCount());

            Addon guestPriceAddon = new Addon();
            guestPriceAddon.setName(selectedSquareEmail.getEventGuestCount());
            if(guestPriceAddon.exists()){
                guestPriceAddon = Addon.findByName(selectedSquareEmail.getEventGuestCount());
                event_textfield3.setText(String.valueOf(guestPriceAddon.getPrice()));
            }

            event_textfield4.setText(selectedSquareEmail.getEventLocation());
            event_textfield5.setText(selectedSquareEmail.getEventAddress());
            event_textfield6.setText(selectedSquareEmail.getEventStyle());
            event_textfield7.setText(selectedSquareEmail.getCustomPalette());
            event_textfield8.setText(selectedSquareEmail.getEventType());

            // Get the addons
        }
    }

    public void setIsNew(boolean isNew){
        this.isNew = isNew;
    }

    public void setCreateSquareInvoice(boolean createSquareInvoice){
        this.createSquareInvoice = createSquareInvoice;
    }

    public void setIsImport(boolean isImport){
        this.isImport = isImport;
        if(isImport){
            autoPopulate();
        }
    }

    public void editRecord(TableColumn.CellEditEvent<InvoiceItem, String> event, int column){
        selectedAddon = tableView.getSelectionModel().getSelectedItem();
        boolean success = false;
        String errorMessage = "There was an error updating the record.";
        try {
            switch (column) {
                case 1:
                    selectedAddon.setItemDesc(event.getNewValue());
                    Addon tmpAddon = new Addon();
                    tmpAddon.setName(event.getNewValue());
                    if(tmpAddon.exists()) {
                        tmpAddon = Addon.findByName(tmpAddon.getName());
                        selectedAddon.setItemSupplierCost(tmpAddon.getSupplierCost());
                        selectedAddon.setItemCost(tmpAddon.getPrice());
                    }
                    success = true;
                    break;
                case 2:
                    int tmpQuantity = Integer.parseInt(event.getNewValue());
                    selectedAddon.setItemQuantity(tmpQuantity);
                    success = true;
                    break;
                case 3:
                    double tmpCost = Double.parseDouble(event.getNewValue());
                    selectedAddon.setItemCost(tmpCost);
                    success = true;
                    break;
                case 4:
                    double tmpSupCost = Double.parseDouble(event.getNewValue());
                    selectedAddon.setItemSupplierCost(tmpSupCost);
                    success = true;
                    break;
                case 5:
                    selectedAddon.setNote(event.getNewValue());
                    success = true;
                    break;
                default:
                    break;
            }
        }catch(Exception ex){
            errorMessage = "Please enter a valid number.";
        }
        if (!success){
            switch (column){
                case 1:
                    selectedAddon.setItemDesc(event.getOldValue());
                    break;
                case 2:
                    selectedAddon.setItemQuantity(Integer.parseInt(event.getOldValue()));
                    break;
                case 3:
                    selectedAddon.setItemCost(Double.parseDouble(event.getOldValue()));
                    break;
                case 4:
                     selectedAddon.setItemSupplierCost(Double.parseDouble(event.getOldValue()));
                    break;
                default:
                    break;
            }
            failureAlert.setContentText(errorMessage);
            failureAlert.showAndWait();
        }
        tableView.refresh();
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
            confirmationAlert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
                float subtotal = 0f;

                double guestPrice = Double.parseDouble(event_textfield3.getText());
                double shippingPrice = Double.parseDouble(event_textfield9.getText());
                subtotal += guestPrice + shippingPrice;

                for (InvoiceItem invoiceItem :invoiceItemObservableList) {
                    subtotal += invoiceItem.getItemQuantity() * invoiceItem.getItemCost();
                }

                String name = customer_textField1.getText();
                String email = customer_textField2.getText();
                String phone = customer_textField3.getText();
                String source = customer_textField4.getText();

                Customer customer = Customer.findByEmail(email);
                if(customer == null) {
                    customer = new Customer();
                    customer.setName(name);
                    customer.setPhone(phone);
                    customer.setSource(source);
                    customer.setEmail(email);
                    customer.add();
                    customer.getIDFromDB();
                } else {
                    customer.setPhone(phone);
                    customer.setSource(source);
                    customer.edit();
                }

                Invoice invoice = new Invoice();
                invoice.setSubtotal(subtotal);
                invoice.setTaxRate(6.25f);
                invoice.setIsPayed(false);

                if(isNew){
                    if(!createSquareInvoice) {
                        invoice.setID(invoice.getNewIDFromDB());
                    } else {
                        // Create square invoice
                        invoice.setID(1);
                    }

                    invoice.add();

                    InvoiceItem guestItem = new InvoiceItem();
                    guestItem.setInvoiceId(invoice.getID());
                    guestItem.setItemQuantity(1);
                    guestItem.setItemCost(guestPrice);
                    guestItem.setItemDesc(event_textfield2.getText() + " Guests");
                    guestItem.setItemSupplierCost(0);

                    Addon addonGuestCount = Addon.findByName(guestItem.getItemDesc());
                    if(addonGuestCount == null){
                        addonGuestCount = new Addon();
                        addonGuestCount.setName(guestItem.getItemDesc());
                        addonGuestCount.setTypeCode("GC");
                        addonGuestCount.setPrice(guestItem.getItemCost());
                        addonGuestCount.setSupplierCost(guestItem.getItemSupplierCost());
                        addonGuestCount.add();
                    } else {
                        addonGuestCount.setPrice(guestItem.getItemCost());
                        addonGuestCount.setSupplierCost(guestItem.getItemSupplierCost());
                        addonGuestCount.edit();
                    }

                    guestItem.add();

                    for (InvoiceItem invoiceItem : invoiceItemObservableList) {
                        invoiceItem.setInvoiceId(invoice.getID());
                        Addon addon = Addon.findByName(invoiceItem.getItemDesc());
                        if(addon == null){
                            addon = new Addon();
                            addon.setName(invoiceItem.getItemDesc());
                            addon.setTypeCode("AD");
                            addon.setPrice(invoiceItem.getItemCost());
                            addon.setSupplierCost(invoiceItem.getItemSupplierCost());
                            addon.add();
                        } else {
                            addon.setPrice(invoiceItem.getItemCost());
                            addon.setSupplierCost(invoiceItem.getItemSupplierCost());
                            addon.edit();
                        }
                        invoiceItem.add();
                    }

                    LocalDate date = event_datePicker.getValue();
                    LocalTime time = LocalTime.parse(event_textfield1.getText(), DateTimeFormatter.ofPattern("HH:mm:ss"));
                    String guestCount = event_textfield2.getText();

                    LocalDate picnicLocalDate = event_datePicker.getValue();
                    int year = picnicLocalDate.getYear();
                    int month = picnicLocalDate.getMonthValue();
                    int day = picnicLocalDate.getDayOfMonth();
                    String timeString = event_textfield1.getText();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateString = year + "-" + month + "-" + day + " " + timeString;
                    Date picnicDate = new Date();

                    try {
                        picnicDate  = formatter.parse(dateString);
                    } catch(Exception e){
                        e.printStackTrace();
                    }

                    InvoiceItem shippingItem = new InvoiceItem();
                    shippingItem.setInvoiceId(invoice.getID());
                    shippingItem.setItemQuantity(1);
                    shippingItem.setItemCost(shippingPrice);
                    shippingItem.setItemDesc("Event:" + invoice.getID() + " Shipping");
                    shippingItem.setItemSupplierCost(0);

                    Addon addonShipping = Addon.findByName(shippingItem.getItemDesc());
                    if(addonShipping == null){
                        addonShipping = new Addon();
                        addonShipping.setName(shippingItem.getItemDesc());
                        addonShipping.setTypeCode("SH");
                        addonShipping.setPrice(shippingItem.getItemCost());
                        addonShipping.setSupplierCost(shippingItem.getItemSupplierCost());
                        addonShipping.add();
                    } else {
                        addonShipping.setPrice(guestItem.getItemCost());
                        addonShipping.setSupplierCost(guestItem.getItemSupplierCost());
                        addonShipping.edit();
                    }

                    shippingItem.add();

                    Event tmpEvent = new Event();
                    tmpEvent.setPicnicDateTime(picnicDate);
                    tmpEvent.setInvoiceId(invoice.getID());
                    if(isImport){
                        selectedSquareEmail.add();
                        tmpEvent.setSquareEmailId(selectedSquareEmail.getId());
                    }
                    tmpEvent.setCustomerId(customer.getID());
                    tmpEvent.setGuestCount(event_textfield2.getText());
                    tmpEvent.setEventLocation(event_textfield4.getText());

                    if(event_textfield4.getText().toLowerCase().contains("home delivery")){
                        tmpEvent.setEventAddress(event_textfield5.getText());
                    } else {
                        tmpEvent.setEventAddress(event_textfield4.getText().split("\\(")[0] + ", Houston TX");
                    }

                    tmpEvent.setEventType(event_textfield8.getText());
                    tmpEvent.setStyle(event_textfield6.getText());
                    tmpEvent.setCustomPalette(event_textfield7.getText());
                    tmpEvent.add();
                } else {

                }
                successAlert.setContentText("Success.");
                successAlert.showAndWait();
                closeChildWindow(event);
            });
        }
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

        if(event_textfield5.getText().trim().equals("") && event_textfield4.getText().trim().toLowerCase().equals("home delivery")){
            failureAlert.setContentText("Please enter an address for home delivery.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield6.getText().trim().equals("")){
            failureAlert.setContentText("Please enter a style.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield8.getText().trim().equals("")){
            failureAlert.setContentText("Please enter an event type.");
            failureAlert.showAndWait();
            return false;
        }

        if(event_textfield9.getText().trim().equals("")){
            failureAlert.setContentText("Please enter a delivery price.");
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
            double guestPrice = Double.parseDouble(event_textfield3.getText());
        }catch (Exception ex){
            failureAlert.setContentText("Please enter a valid guest price.");
            failureAlert.showAndWait();
            return false;
        }

        try{
            double deliveryPrice = Double.parseDouble(event_textfield9.getText());
        }catch (Exception ex){
            failureAlert.setContentText("Please enter a valid shipping price.");
            failureAlert.showAndWait();
            return false;
        }

        for (InvoiceItem addon:invoiceItemObservableList) {
            if(addon.getItemDesc().equals("New Addon")){
                failureAlert.setContentText("Please enter addon details.");
                failureAlert.showAndWait();
                return false;
            }
        }

        return true;
    }

    public void cancelButtonPushed(ActionEvent event){
        closeChildWindow(event);
    }

}

