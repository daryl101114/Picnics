package entities;

public class User extends QueryObject {
    private String username;
    private String password;
    private String employeeName;
    private String employeeEmail;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username){
        this.username = username;
    }

    public boolean Edit(String password){

        setPassword(password);

        statement = "UPDATE User " +
                "SET " +
                "password = '" + this.getPassword() +  "' " +
                "WHERE username = '" + this.getUsername() + "'";

        return query(statement);
    }

    public boolean Add(){
        statement = "INSERT INTO User (username, password) VALUES ('" +
                this.getUsername() + "', '" + this.getPassword() +
                ")";
        return query(statement);
    }

    public boolean delete(){
        statement =
                "DELETE FROM User WHERE username = '" +
                        this.getUsername() + "'";
        return query(statement);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }
}
