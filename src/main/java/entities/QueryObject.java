package entities;

import java.sql.*;

public class QueryObject {

    protected final  String connectionURL = "jdbc:sqlserver://ec2-34-200-224-73.compute-1.amazonaws.com:1433/FANCYPICNICS";
    protected final  String connectionUser = "JavaUser";
    protected final  String connectionPassword = "cnsltiq";

    protected String statement;
    protected Connection conn = null;
    protected PreparedStatement stmt = null;


    protected boolean query(String statement) {
        try {
            conn = DriverManager.getConnection(connectionURL, connectionUser, connectionPassword);
            stmt = conn.prepareStatement(statement);
            int result = stmt.executeUpdate();
            if (result > 0)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
        return false;
    }
}
