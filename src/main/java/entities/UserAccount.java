package entities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserAccount extends QueryObject {
    private String username;
    private String password;
    private int employeeID;

    private Employee employee;
    private String employeeName;
    private String employeeEmail;

    public UserAccount(String username, String password, int employeeID) {
        setUsername(username);
        setPassword(password);
        setEmployeeID(employeeID);
    }

    public UserAccount() {

    }

    public boolean edit(){
        clearEmployee();

        statement = "UPDATE user_account " +
                "SET " +
                "password = '" + this.getPassword() +  "', " +
                "employee_id = " + this.getEmployeeID() + " " +
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
            statement = "SELECT username, password, employee_id, first_name, last_name, email FROM user_account JOIN employee ON user_account.employee_id = employee.id";
            executeQuery(statement);
            while(resultSet.next()) {
                UserAccount userAccount = new UserAccount();
                setUserFromQuery(userAccount);
                userAccount.setEmployeeName(resultSet.getString("first_name") + " " + resultSet.getString("last_name"));
                userAccount.setEmployeeEmail(resultSet.getString("email"));
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
            statement = "SELECT username, password, employee_id, active FROM user_account JOIN employee ON user_account.employee_id = employee.id WHERE username = '" + username + "'";
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
        String salt = BCrypt.gensalt(12);
        this.password = BCrypt.hashpw(password, salt);
    }

    public void setPassword(String password, boolean encrypt) {
        if(!encrypt) {
            this.password = password;
        }
    }

    public static boolean checkPassword(String passwordPlain, String passwordStored){
        if(passwordStored == null || !passwordStored.startsWith("$2a$"))
            throw new IllegalArgumentException("Invalid hash stored in the database");

        return BCrypt.checkpw(passwordPlain, passwordStored);
    }

    public Employee getEmployee() {
        if(this.employee == null) {
            this.employee = Employee.findByID(getEmployeeID());
        }
        return this.employee;
    }

    public void setEmployee(Employee employee){
        this.employee = employee;
    }

    public void clearEmployee() {
        this.employee = null;
        this.employeeEmail = null;
        this.employeeName = null;
    }

    public String getEmployeeName(){
        if(employeeName != null) {
            return this.employeeName;
        }
        else {
            return this.getEmployee().getFirstName() + " " + this.getEmployee().getLastName();
        }
    }

    public void setEmployeeName(String employeeName){this.employeeName = employeeName;}

    public String getEmployeeEmail()
    {
        if(employeeEmail != null) {
            return this.employeeEmail;
    }
    else {
        return this.getEmployee().getEmail();
    } }

    public void setEmployeeEmail(String employeeEmail){ this.employeeEmail = employeeEmail;}

    public boolean exists() {
        return (findByUsername(username) != null);
    }

    public int getEmployeeID() { return employeeID; }

    public void setEmployeeID(int employeeID) { this.employeeID = employeeID; }

    private static void setUserFromQuery(UserAccount userAccount) throws SQLException {
        userAccount.setUsername(resultSet.getString("username"));
        userAccount.setPassword(resultSet.getString("password"), false);
        userAccount.setEmployeeID(resultSet.getInt("employee_id"));
    }
}