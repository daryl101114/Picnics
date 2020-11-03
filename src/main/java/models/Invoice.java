package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Invoice extends QueryObject {

    private int id;
    private Float subtotal;
    private Float taxRate;
    private Float total;
    private boolean isPayed;

    public Invoice(int id, Float subtotal, Float taxRate, Float total, boolean isPayed) {
        this.id = id;
        this.subtotal = subtotal;
        this.taxRate = taxRate;
        this.total = total;
        this.isPayed = isPayed;
    }

    public Invoice(){
    }

    public boolean edit(){

        statement = "UPDATE invoice " +
                "SET " +
                "subtotal = '" + this.getSubtotal() +  "', " +
                "tax_rate = '" + this.getTaxRate() +  "', " +
                "is_payed = " + this.getIsPayedBit() +  ", " +
                "total = '" + this.getTotal() +  "', " +
                "WHERE id = " + this.getID();

        return executeUpdate(statement);
    }

    public boolean add(){
        statement = "INSERT INTO invoice (id, subtotal, tax_rate, is_payed, total) VALUES (" +
                this.getID() + ", " + this.getSubtotal() + ", " + this.getTaxRate() + ", " + this.getIsPayedBit() + ", " + this.getTotal() +
                ")";

        return executeUpdate(statement);
    }

    public static ObservableList<Invoice> findAll(){
        List<Invoice> invoices = new ArrayList<>();
        try {
            statement = "SELECT * FROM invoice ORDER BY id DESC";
            executeQuery(statement);
            while(resultSet.next()) {
                Invoice invoice = new Invoice();
                setFromQuery(invoice);
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return FXCollections.observableList(invoices);
    }

    public static Invoice findByID(int id){
        Invoice invoice = new Invoice();
        try {
            statement = "SELECT * FROM invoice WHERE id = " + id;
            executeQuery(statement);
            if (resultSet.next()) {
                setFromQuery(invoice);
            } else {
                invoice = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return invoice;
    }

    public int getID() {
        return id;
    }

    public void setID(int id){
        this.id = id;
    }

    public int getNewIDFromDB() {
        int result = -1;
        try {
            statement = "SELECT NEXT VALUE FOR invoice_id_sequence";
            executeQuery(statement);
            if (resultSet.next()) {
                result = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }

        return result;
    }

    public Float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Float subtotal) {
        this.subtotal = subtotal;
    }

    public Float getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Float taxRate) {
        this.taxRate = taxRate;
    }

    public Float getTotal() {
        return Math.round((subtotal * (1 + (taxRate / 100))) * 100.0) / 100.0f;
    }

    public boolean getIsPayed() {
        return isPayed;
    }

    public void setIsPayed(boolean payed) {
        isPayed = payed;
    }

    public int getIsPayedBit(){
        return isPayed ? 1 : 0;
    }

    public boolean exists() {
        return (findByID(this.id) != null);
    }

    public void getIDFromDB(){
        setID(getLastID("invoice"));
    }

    private static void setFromQuery(Invoice invoice) throws SQLException {
        invoice.setID(resultSet.getInt("id"));
        invoice.setSubtotal(resultSet.getFloat("subtotal"));
        invoice.setTaxRate(resultSet.getFloat("tax_rate"));
        invoice.setIsPayed(resultSet.getBoolean("is_payed"));
        invoice.setTaxRate(resultSet.getFloat("total"));
    }

    public static int getChecksum(){
        return getChecksum("invoice");
    }
}
