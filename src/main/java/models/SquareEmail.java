package models;

import entities.QueryObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SquareEmail extends QueryObject {
    private String id;
    private Date emailDate;
    private int invoiceId;

    public SquareEmail(String id, Date emailDate, int invoiceId) {
        setId(id);
        setEmailDate(emailDate);
        setInvoiceId(invoiceId);
    }

    public SquareEmail() {

    }

    public boolean add(){
        statement = "INSERT INTO square_email (id, email_date) VALUES ('" +
                this.getId() + "', '" + this.getEmailDate() + "', " +
                ")";
        return executeUpdate(statement);
    }

    public boolean update(){
        statement = "UPDATE square_email SET invoice_id = " + this.getInvoiceId() +
                " WHERE id = " + this.getId();
        return executeUpdate(statement);
    }

    public boolean delete(){
        statement =
                "DELETE FROM square_email WHERE id = '" +
                        this.getId() + "'";
        return executeUpdate(statement);
    }

    public static ObservableList<SquareEmail> findAll(){
        List<SquareEmail> squareEmails = new ArrayList<>();
        try {
            statement = "SELECT id, email_date, invoice_id FROM square_email";
            executeQuery(statement);
            while(resultSet.next()) {
                SquareEmail squareEmail = new SquareEmail();
                setUserFromQuery(squareEmail);
                squareEmails.add(squareEmail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return FXCollections.observableList(squareEmails);
    }

    public SquareEmail findByID(String id){
        SquareEmail squareEmail = new SquareEmail();
        try {
            statement = "SELECT id, square_email, invoice_id FROM square_email WHERE id = '" + id + "'";
            executeQuery(statement);
            if (resultSet.next()) {
                setUserFromQuery(squareEmail);
            } else {
                squareEmail = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return squareEmail;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }

    public Date getEmailDate(){
        return this.emailDate;
    }

    public void setEmailDate(Date emailDate){
        this.emailDate = emailDate;
    }

    public int getInvoiceId(){
        return this.invoiceId;
    }

    public void setInvoiceId(int invoiceId){
        this.invoiceId = invoiceId;
    }

    public boolean exists() {
        return (findByID(id) != null);
    }

    private static void setUserFromQuery(SquareEmail squareEmail) throws SQLException {
        squareEmail.setId(resultSet.getString("id"));
        squareEmail.setEmailDate(resultSet.getDate("email_date"));
        squareEmail.setInvoiceId(resultSet.getInt("invoice_id"));
    }
}
