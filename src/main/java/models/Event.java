package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event extends QueryObject {

    private int id;
    private String squareEmailId;
    private Date picnicDateTime;
    private int invoiceId;
    private int customerId;
    private String guestCount;
    private String eventLocation;
    private String eventAddress;
    private String eventType;
    private int employeeId;
    private int googleCalendarId;
    private String style;
    private String customPalette;

    public Event(int id, String squareEmailId, Date picnicDateTime, int invoiceId, int customerId, String guestCount, String eventLocation, String eventAddress, String eventType, int employeeId, int googleCalendarId, String style, String customPalette) {
        this.id = id;
        this.squareEmailId = squareEmailId;
        this.picnicDateTime = picnicDateTime;
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.guestCount = guestCount;
        this.eventLocation = eventLocation;
        this.eventAddress = eventAddress;
        this.eventType = eventType;
        this.employeeId = employeeId;
        this.googleCalendarId = googleCalendarId;
        this.style = style;
        this.customPalette = customPalette;
    }

    public Event(){
    }

    public boolean edit(){

        statement = "UPDATE event " +
                "SET " +
                "square_email_id = '" + this.getSquareEmailId() +  "', " +
                "picnic_date_time = '" + this.getPicnicDateTime().toInstant() +  "', " +
                "invoice_id = " + this.getInvoiceId() +  ", " +
                "customer_id = " + this.getCustomerId() +  ", " +
                "guest_count = " + this.getGuestCount() +  ", " +
                "event_location = '" + this.getEventLocation() +  "', " +
                "event_address = '" + this.getEventAddress() +  "', " +
                "event_type = '" + this.getEventType() +  "', " +
                "employee_id = " + (this.getEmployeeId() == 0 ? "null" : this.getEmployeeId()) +  ", " +
                "google_calendar_id = " + (this.getGoogleCalendarId() == 0 ? "null" : this.getGoogleCalendarId()) +  ", " +
                "style = '" + this.getStyle() +  "', " +
                "custom_palette = '" + this.getCustomPalette() +  "' " +
                "WHERE id = " + this.getId();

        return executeUpdate(statement);
    }

    public boolean add(){
        statement = "Insert INTO event (square_email_id, picnic_date_time, invoice_id, customer_id, guest_count, event_location, event_address, event_type, employee_id, google_calendar_id, custom_palette, style)  VALUES ('" +
                this.getSquareEmailId() + "', '" + this.getPicnicDateTime().toInstant() + "', " + this.getInvoiceId() + ", " + this.getCustomerId() + ", '" + this.getGuestCount() + "', '"  + this.getEventLocation() + "', '" + this.getEventAddress() + "', '" + this.getEventType() + "', " + (this.getEmployeeId() == 0 ? "null" : this.getEmployeeId()) + ", " + (this.getGoogleCalendarId() == 0 ? "null" : this.getGoogleCalendarId()) + ", '" + this.getCustomPalette() + "', '" + this.getStyle() +
                "')";

        return executeUpdate(statement);
    }

    public static ObservableList<Event> findAll(){
        List<Event> events = new ArrayList<>();
        try {
            statement = "SELECT * FROM event";
            executeQuery(statement);
            while(resultSet.next()) {
                Event event = new Event();
                setEmployeeFromQuery(event);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return FXCollections.observableList(events);
    }

    public static Event findByID(int id){
        Event event = new Event();
        try {
            statement = "SELECT * FROM event WHERE id = " + id;
            executeQuery(statement);
            if (resultSet.next()) {
                setEmployeeFromQuery(event);
            } else {
                event = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return event;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSquareEmailId() {
        return squareEmailId;
    }

    public void setSquareEmailId(String squareEmailId) {
        this.squareEmailId = squareEmailId;
    }

    public Date getPicnicDateTime() {
        return picnicDateTime;
    }

    public void setPicnicDateTime(Date picnicDateTime) {
        this.picnicDateTime = picnicDateTime;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(String guestCount) {
        this.guestCount = guestCount;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getGoogleCalendarId() {
        return googleCalendarId;
    }

    public void setGoogleCalendarId(int googleCalendarId) {
        this.googleCalendarId = googleCalendarId;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getCustomPalette() {
        return customPalette;
    }

    public void setCustomPalette(String customPalette) {
        this.customPalette = customPalette;
    }

    public boolean exists() {
        return (findByID(this.id) != null);
    }

    public void getIDFromDB(){
        setId(getLastID("event"));
    }

    private static void setEmployeeFromQuery(Event event) throws SQLException {
        event.setId(resultSet.getInt("id"));
        event.setSquareEmailId(resultSet.getString("square_email_id"));
        event.setPicnicDateTime(resultSet.getDate("picnic_date_time"));
        event.setInvoiceId(resultSet.getInt("invoice_id"));
        event.setCustomerId(resultSet.getInt("customer_id"));
        event.setGuestCount(resultSet.getString("guest_count"));
        event.setEventLocation(resultSet.getString("event_location"));
        event.setEventAddress(resultSet.getString("event_address"));
        event.setEventType(resultSet.getString("event_type"));
        event.setEmployeeId(resultSet.getInt("employee_id"));
        event.setGoogleCalendarId(resultSet.getInt("google_calendar_id"));
        event.setStyle(resultSet.getString("style"));
        event.setCustomPalette(resultSet.getString("custom_palette"));
    }

    public static int getChecksum(){
        return getChecksum("event");
    }
}