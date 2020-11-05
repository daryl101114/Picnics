package controllers;

import entities.Controller;
import entities.ControllerType;
import entities.GoogleCalendarService;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Stage;

import models.Event;
import models.InvoiceItem;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.time.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class EventController extends Controller implements Initializable {

    @FXML private TableColumn<Event, String> column1;
    @FXML private TableColumn<Event, String> column2;
    @FXML private TableColumn<Event, String> column3;
    @FXML private TableColumn<Event, LocalDateTime> column4;
    @FXML private TableColumn<Event, String> column5;
    @FXML private TableColumn<Event, String> column6;
    @FXML private TableColumn<Event, String> column7;
    @FXML private TableColumn<Event, Float> column8;

    @FXML private DatePicker dpFrom;
    @FXML private DatePicker dpTo;

    @FXML private TextField textField1;

    @FXML private CheckBox checkBox1;

    @FXML private TableView<Event> tableView;

    @FXML private Event selectedObject;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        eventObservableList = FXCollections.observableArrayList();
        clearSelectedObjects();

        dpFrom.setValue(LocalDate.now().withDayOfMonth(1));
        dpTo.setValue(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));

        loadData();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedObject = newSelection;
            }
        });

        setContextMenu();

        column1.maxWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        column2.maxWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        column3.maxWidthProperty().bind(tableView.widthProperty().multiply(0.1));
        column4.maxWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        column5.maxWidthProperty().bind(tableView.widthProperty().multiply(0.1));
        column6.maxWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        column7.maxWidthProperty().bind(tableView.widthProperty().multiply(0.1));
        column8.maxWidthProperty().bind(tableView.widthProperty().multiply(0.1));

        column1.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        column2.setCellValueFactory(new PropertyValueFactory<>("customerEmail"));
        column3.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
        column4.setCellValueFactory(new PropertyValueFactory<>("picnicDateTimeString"));
        column5.setCellValueFactory(new PropertyValueFactory<>("eventLocation"));
        column6.setCellValueFactory(new PropertyValueFactory<>("eventAddress"));
        column7.setCellValueFactory(new PropertyValueFactory<>("guestCount"));
        column8.setCellValueFactory(new PropertyValueFactory<>("invoiceTotal"));

        textField1.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshTable();
        });

        checkBox1.selectedProperty().addListener((observable, oldValue, newValue) -> {
            refreshTable();
            setContextMenu();
        });
    }

    public void setContextMenu(){
        if(!checkBox1.isSelected()) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem1 = new MenuItem("Mark as Paid");
            menuItem1.setOnAction(event -> markPaid());
            MenuItem menuItem2 = new MenuItem("Edit Event");
            menuItem2.setOnAction(event -> editEvent());

            contextMenu.getItems().add(menuItem1);
            contextMenu.getItems().add(menuItem2);
            tableView.setContextMenu(contextMenu);
        } else {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem1 = new MenuItem("Edit Event");
            menuItem1.setOnAction(event -> editEvent());
            contextMenu.getItems().add(menuItem1);
            tableView.setContextMenu(contextMenu);
        }
    }

    public void addButtonPushed(ActionEvent event) throws IOException {
            try {
                loadEventScene((Stage) tableView.getScene().getWindow(), "/views/AddEvent.fxml", ControllerType.ADD_EVENT, true, false, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void reloadPushed(ActionEvent event){
        loadData();
    }

    public void refreshTable(){
        FilteredList<Event> filteredList = new FilteredList<>(eventObservableList);
        filteredList.setPredicate(i -> (i.getInvoice().getIsPaid() == checkBox1.isSelected()) && (textField1.getText().isEmpty() ? true : i.getCustomerName().toLowerCase().contains(textField1.getText().toLowerCase())));
        tableView.setItems(filteredList);
        tableView.refresh();
    }

    public void editEvent() {
        if(selectedObject != null) {
            selectedEvent = selectedObject;
            try {
                loadEventScene((Stage) tableView.getScene().getWindow(), "/views/AddEvent.fxml", ControllerType.ADD_EVENT, false, false, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void markPaid(){
        selectedObject = tableView.getSelectionModel().getSelectedItem();
        if (selectedObject != null) {
            confirmationAlert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
                selectedObject.getInvoice().setIsPaid(true);
                if (selectedObject.getInvoice().edit()) {
                    refreshTable();
                    String conString = confirmationAlert.getContentText();
                    confirmationAlert.setContentText("Success. Do you want to create a Google Calendar event?");
                    confirmationAlert.showAndWait().filter(selection -> selection == ButtonType.OK).ifPresent(selection -> {
                        String description = "";
                        description += "Name: " + selectedObject.getCustomerName() + "\n" ;
                        description += "Email: " + selectedObject.getCustomerEmail() + "\n" ;
                        description += "Phone number: " + selectedObject.getCustomerPhone() + "\n" ;
                        description += "Picnic Date: " + selectedObject.getPicnicDateTimeString() + "\n" ;
                        description += "Time of Picnic: " + selectedObject.getPicnicTimeString() + "\n" ;
                        description += "Estimated Guest Count: " + selectedObject.getGuestCount() + "\n" ;
                        description += "Location: " + selectedObject.getEventLocation() + "\n" ;
                        description += "Address: " + selectedObject.getEventAddress() + "\n" ;
                        description += "Type of Event: " + selectedObject.getEventType() + "\n" ;
                        description += "Picnic Style: " + selectedObject.getStyle() + "\n" ;
                        description += "Custom Style: " + selectedObject.getCustomPalette() + "\n" ;

                        List<InvoiceItem> invoiceItemList = InvoiceItem.findAllAddonsByInvoiceID(selectedObject.getInvoiceId());
                        description += "Add-ons: ";

                        InvoiceItem marquee = null;
                        for (InvoiceItem invoiceItem :
                                invoiceItemList) {
                            description += invoiceItem.getItemDesc() + ", ";
                            if(invoiceItem.getNote() != null && invoiceItem.getItemDesc().toLowerCase().contains("marquee") && invoiceItem.getNote() != null){
                                marquee = invoiceItem;
                            }
                        }

                        if(marquee != null)
                            description += "\nMarquee Letters: " + marquee.getNote();

                        String eventName = "Picnic for " + selectedObject.getCustomerName();
                        String location = selectedObject.getEventAddress();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                        String startTime = selectedObject.getPicnicDateTime().plusHours(6).format(formatter);
                        String endTime = selectedObject.getPicnicDateTime().plusHours(7).format(formatter);

                        GoogleCalendarService googleCalendarService = new GoogleCalendarService();
                        try {
                            googleCalendarService.GCalendar();
                        } catch (IOException | GeneralSecurityException e) {
                            e.printStackTrace();
                        }

                        try {
                            selectedObject.setGoogleCalendarId(googleCalendarService.insertEvent(description, eventName, location, startTime, endTime));
                            selectedObject.edit();
                            successAlert.setContentText("Event successfully added to the calendar.");
                            successAlert.showAndWait();
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    });
                    confirmationAlert.setContentText(conString);
                } else {
                    selectedObject.getInvoice().setIsPaid(false);
                    refreshTable();
                    failureAlert.setContentText("There was an error updating the record in the database.");
                    failureAlert.showAndWait();
                }
            });
        } else {
            failureAlert.setContentText("Please select a valid record.");
            failureAlert.showAndWait();
        }
    }


    public void loadData() {
        eventObservableList = Event.findAllForTable(dpFrom.getValue(), dpTo.getValue().plusDays(1));
        refreshTable();
    }
}
