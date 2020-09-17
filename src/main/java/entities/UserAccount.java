package entities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserAccount extends QueryObject {
    private String username;
    private String password;
    private int employeeID;
    private Employee employee;

    public UserAccount(String username, String password, int employeeID) {
        setUsername(username);
        setPassword(password);
        setEmployeeID(employeeID);
    }

    public UserAccount() {

    }

    public boolean edit(String password){

        setPassword(password);

        statement = "UPDATE user_account " +
                "SET " +
                "password = '" + this.getPassword() +  "' " +
                "WHERE username = '" + this.getUsername() + "'";

        return executeUpdate(statement);
    }

    public boolean add(){
        statement = "INSERT INTO user_account (username, password, employee_id) VALUES ('" +
                this.getUsername() + "', '" + this.getPassword() + "', " + this.getEmployee().getID() +
                ")";
        return executeUpdate(statement);
    }

    public boolean delete(){
        statement =
                "DELETE FROM user_account WHERE username = '" +
                        this.getUsername() + "'";
        return executeUpdate(statement);
    }

    public static ObservableList<UserAccount> findAll(){
        List<UserAccount> userAccounts = new ArrayList<>();
        try {
            statement = "SELECT * FROM user_account";
            executeQuery(statement);
            while(resultSet.next()) {
                UserAccount userAccount = new UserAccount();
                setUserFromQuery(userAccount);
                userAccounts.add(userAccount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return FXCollections.observableList(userAccounts);
    }

    public UserAccount findByUsername(String username){
        UserAccount userAccount = new UserAccount();
        try {
            statement = "SELECT * FROM user_account WHERE username = '" + username + "'";
            executeQuery(statement);
            if (resultSet.next()) {
                setUserFromQuery(userAccount);
            } else {
                userAccount = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
           terminateQuery();
        }
        return userAccount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Employee getEmployee() {
        if(this.employee == null) {
            setEmployee(Employee.findByID(getEmployeeID()));
        }
        return this.employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getEmployeeName(){
        return getEmployee().getFirstName() + " " + getEmployee().getLastName();
    }

    public String getEmployeeEmail(){
        return getEmployee().getEmail();
    }

    public boolean exists() {
        return (findByUsername(username) != null);
    }

    public int getEmployeeID() { return employeeID; }

    public void setEmployeeID(int employeeID) { this.employeeID = employeeID; }

    private static void setUserFromQuery(UserAccount userAccount) throws SQLException {
        userAccount.setUsername(resultSet.getString("username"));
        userAccount.setPassword(resultSet.getString("password"));
        userAccount.setEmployeeID(resultSet.getInt("employee_id"));
    }
}