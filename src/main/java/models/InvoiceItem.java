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
    private String itemCostString;
    private String itemQuantityString;
    private String itemSupplierCostString;

    public InvoiceItem(int invoiceId, String itemDesc, int itemQuantity, double itemCost, double itemSupplierCost) {
        this.invoiceId = invoiceId;
        this.itemDesc = itemDesc;
        this.itemQuantity = itemQuantity;
        this.itemCost = itemCost;
        this.itemSupplierCost = itemSupplierCost;
        this.itemCostString = String.valueOf(itemCost);
        this.itemQuantityString = String.valueOf(itemQuantity);
        this.itemSupplierCostString = String.valueOf(itemSupplierCost);
    }

    public InvoiceItem(){
    }

    public boolean edit(){
        statement = "UPDATE invoice_item " +
                "SET " +
                "item_desc = '" + this.getItemDesc() +  "', " +
                "item_quantity = " + this.getItemQuantity() +  ", " +
                "item_cost = " + this.getItemCost() + ", " +
                "item_cost = " + this.getItemSupplierCost() + " " +
                "WHERE invoice_id = " + this.getInvoiceId();

        return executeUpdate(statement);
    }

    public boolean add(){
        statement = "INSERT INTO invoice_item (item_desc, item_quantity, item_cost, item_supplier_cost) VALUES ('" +
                this.getItemDesc() + "', " + this.getItemQuantity() +  ", " + this.getItemCost() +  ", " + this.getItemSupplierCost() +
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
        return itemCostString;
    }

    public void setItemCostString(String itemCostString) {
        this.itemCostString = itemCostString;
    }

    public String getItemQuantityString() {
        return itemQuantityString;
    }

    public void setItemQuantityString(String itemQuantityString) {
        this.itemQuantityString = itemQuantityString;
    }

    public String getItemSupplierCostString() {
        return itemSupplierCostString;
    }

    public void setItemSupplierCostString(String itemSupplierString) {
        this.itemSupplierCostString = itemSupplierString;
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
    }

    public static int getChecksum(){
        return getChecksum("invoice_item");
    }
}
