package entities;

public class UserAccount extends QueryObject {
    private String username;
    private String password;
    private int employee_id;

    public UserAccount(String username, String password, int employee_id) {
        this.username = username;
        this.password = password;
        this.employee_id = employee_id;
    }

    public boolean Edit(String password){

        setPassword(password);

        statement = "UPDATE User_Account " +
                "SET " +
                "password = '" + this.getPassword() +  "' " +
                "WHERE username = '" + this.getUsername() + "'";

        return query(statement);
    }

    public boolean Add(){
        statement = "INSERT INTO User_Account (username, password, employee_id) VALUES ('" +
                this.getUsername() + "', '" + this.getPassword() + "', " + this.getEmployee_ID() +
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getEmployee_ID() {
        return employee_id;
    }

    public void setEmployee_ID(int employee_id) {
        this.employee_id = employee_id;
    }
}
