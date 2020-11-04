package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.time.LocalDate;
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

    private Customer customer;
    private List<InvoiceItem> invoiceItemList;
    private Invoice invoice;

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

    public Event() {
    }

    public boolean edit() {

        statement = "UPDATE event " +
                "SET " +
                "square_email_id = " + (this.getSquareEmailId() == null ? this.getSquareEmailId() : "'" + this.getSquareEmailId().replaceAll("'", "''") + "'") + ", " +
                "picnic_date_time = '" + this.getPicnicDateTime().toInstant() + "', " +
                "invoice_id = " + this.getInvoiceId() + ", " +
                "customer_id = " + this.getCustomerId() + ", " +
                "guest_count = " + (this.getGuestCount() == null ? this.getGuestCount() : "'" + this.getGuestCount().replaceAll("'", "''") + "'") + ", " +
                "event_location = " + (this.getEventLocation() == null ? this.getEventLocation() : "'" + this.getEventLocation().replaceAll("'", "''") + "'") + ", " +
                "event_address = " + (this.getEventAddress() == null ? this.getEventAddress() : "'" + this.getEventAddress().replaceAll("'", "''") + "'") + ", " +
                "event_type = " + (this.getEventType() == null ? this.getEventType() : "'" + this.getEventType().replaceAll("'", "''") + "'") + ", " +
                "employee_id = " + (this.getEmployeeId() == 0 ? "null" : this.getEmployeeId()) + ", " +
                "google_calendar_id = " + (this.getGoogleCalendarId() == 0 ? "null" : this.getGoogleCalendarId()) + ", " +
                "style = " + (this.getStyle() == null ? this.getStyle() : "'" + this.getStyle().replaceAll("'", "''") + "'") + ", " +
                "custom_palette = " + (this.getCustomPalette() == null ? this.getCustomPalette() : "'" + this.getCustomPalette().replaceAll("'", "''") + "'") + ", " +
                "WHERE id = " + this.getId();

        return executeUpdate(statement);
    }

    public boolean add() {
        statement = "Insert INTO event (square_email_id, picnic_date_time, invoice_id, customer_id, guest_count, event_location, event_address, event_type, employee_id, google_calendar_id, custom_palette, style)  VALUES (" +
                (this.getSquareEmailId() == null ? this.getSquareEmailId() : "'" + this.getSquareEmailId().replaceAll("'", "''") + "'") + ", " +
                "'" + this.getPicnicDateTime().toInstant() + "', " +
                this.getInvoiceId() + ", " +
                this.getCustomerId() + ", " +
                (this.getGuestCount() == null ? this.getGuestCount() : "'" + this.getGuestCount().replaceAll("'", "''") + "'") + ", " +
                (this.getEventLocation() == null ? this.getEventLocation() : "'" + this.getEventLocation().replaceAll("'", "''") + "'") + ", " +
                (this.getEventAddress() == null ? this.getEventAddress() : "'" + this.getEventAddress().replaceAll("'", "''") + "'") + ", " +
                (this.getEventType() == null ? this.getEventType() : "'" + this.getEventType().replaceAll("'", "''") + "'") + ", " +
                (this.getEmployeeId() == 0 ? "null" : this.getEmployeeId()) + ", " +
                (this.getGoogleCalendarId() == 0 ? "null" : this.getGoogleCalendarId()) + ", " +
                (this.getCustomPalette() == null ? this.getCustomPalette() : "'" + this.getCustomPalette().replaceAll("'", "''") + "'") + ", " +
                (this.getStyle() == null ? this.getStyle() : "'" + this.getStyle().replaceAll("'", "''") + "'") +
                ")";

        return executeUpdate(statement);
    }

    public static ObservableList<Event> findAllForTable(LocalDate fromDate, LocalDate toDate) {
        List<Event> events = new ArrayList<>();
        try {
            statement = "SELECT * FROM event WHERE picnic_date_time BETWEEN '" + fromDate.toString() + "' AND '" + toDate.toString() + "';";
            executeQuery(statement);
            while (resultSet.next()) {
                Event event = new Event();
                setEventFromQuery(event);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        for (Event event : events) {
            setEventDetails(event);
        }
        return FXCollections.observableList(events);
    }


    public static Event findByID(int id) {
        Event event = new Event();
        try {
            statement = "SELECT * FROM event WHERE id = " + id;
            executeQuery(statement);
            if (resultSet.next()) {
                setEventFromQuery(event);
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<InvoiceItem> getInvoiceItemList() {
        return invoiceItemList;
    }

    public void setInvoiceItemList(List<InvoiceItem> invoiceItemList) {
        this.invoiceItemList = invoiceItemList;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public String getCustomerName(){
        return getCustomer().getName();
    }

    public String getCustomerEmail(){
        return getCustomer().getEmail();
    }

    public String getCustomerPhone(){
        return getCustomer().getPhone();
    }

    public Float getInvoiceTotal(){
        return getInvoice().getTotal();
    }

    public boolean exists() {
        return (findByID(this.id) != null);
    }

    public void getIDFromDB() {
        setId(getLastID("event"));
    }

    private static void setEventFromQuery(Event event) throws SQLException {
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

    private static void setEventDetails(Event event) {
        event.setCustomer(Customer.findByID(event.getCustomerId()));
        event.setInvoice(Invoice.findByID(event.getInvoiceId()));
        event.setInvoiceItemList(InvoiceItem.findAllByInvoiceID(event.getInvoiceId()));
    }

    public static int getChecksum() {
        return getChecksum("event");
    }
}