package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItem extends QueryObject {

    private int invoiceId;
    private String itemDesc;
    private int itemQuantity;
    private double itemCost;
    private double itemSupplierCost;
    private String note;

    public InvoiceItem(int invoiceId, String itemDesc, int itemQuantity, double itemCost, double itemSupplierCost, String note) {
        this.invoiceId = invoiceId;
        this.itemDesc = itemDesc;
        this.itemQuantity = itemQuantity;
        this.itemCost = itemCost;
        this.itemSupplierCost = itemSupplierCost;
        this.note = note;
    }

    public InvoiceItem(){
        this.itemQuantity = 1;
        this.itemCost = 0.00;
        this.itemSupplierCost = 0.00;
    }

    public boolean add(){
        statement = "INSERT INTO invoice_item (invoice_id, item_desc, item_quantity, item_cost, item_supplier_cost, note) VALUES (" +
                this.getInvoiceId() + ", " +
                (this.getItemDesc() == null ? this.getItemDesc() : "'" + this.getItemDesc().replaceAll("'","''") + "'") + ", " +
                this.getItemQuantity() +  ", " +
                this.getItemCost() +  ", " +
                this.getItemSupplierCost() + ", " +
                (this.getNote() == null ? this.getNote() : "'" + this.getNote().replaceAll("'","''") + "'") +
                ")";

        return executeUpdate(statement);
    }

    public boolean deleteAllFromInvoice(){
        statement = "DELETE FROM invoice_item WHERE invoice_id = " +
                this.getInvoiceId() +
                ")";

        return executeUpdate(statement);
    }

    public static ObservableList<InvoiceItem> findAll(){
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        try {
            statement = "SELECT * FROM invoice_item ORDER BY name ASC";
            executeQuery(statement);
            while(resultSet.next()) {
                InvoiceItem invoiceItem = new InvoiceItem();
                setEmployeeFromQuery(invoiceItem);
                invoiceItems.add(invoiceItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return FXCollections.observableList(invoiceItems);
    }

    public static InvoiceItem findByID(int id){
        InvoiceItem invoiceItem = new InvoiceItem();
        try {
            statement = "SELECT * FROM invoice_item WHERE invoice_id = " + id;
            executeQuery(statement);
            if (resultSet.next()) {
                setEmployeeFromQuery(invoiceItem);
            } else {
                invoiceItem = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return invoiceItem;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public double getItemCost() {
        return itemCost;
    }

    public void setItemCost(double itemCost) {
        this.itemCost = itemCost;
    }

    public double getItemSupplierCost() {
        return itemSupplierCost;
    }

    public void setItemSupplierCost(double itemSupplierCost) {
        this.itemSupplierCost = itemSupplierCost;
    }

    public String getItemCostString() {
        return String.valueOf(itemCost);
    }

    public String getItemQuantityString() {
        return String.valueOf(itemQuantity);
    }

    public String getItemSupplierCostString() {
        return String.valueOf(itemSupplierCost);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean exists() {
        return (findByID(this.invoiceId) != null);
    }

    public void getIDFromDB(){
        setInvoiceId(getLastID("invoice_item"));
    }

    private static void setEmployeeFromQuery(InvoiceItem invoiceItem) throws SQLException {
        invoiceItem.setInvoiceId(resultSet.getInt("invoice_id"));
        invoiceItem.setItemDesc(resultSet.getString("item_desc"));
        invoiceItem.setItemQuantity(resultSet.getInt("item_quantity"));
        invoiceItem.setItemCost(resultSet.getDouble("item_cost"));
        invoiceItem.setItemSupplierCost(resultSet.getDouble("item_supplier_cost"));
        invoiceItem.setNote(resultSet.getString("note"));
    }

    public static int getChecksum(){
        return getChecksum("invoice_item");
    }
}
