package entities;

import java.sql.*;

public abstract class QueryObject {

    protected final static String connectionURL = "jdbc:sqlserver://ec2-34-200-224-73.compute-1.amazonaws.com:1433;databaseName=FANCYPICNICS;user=JavaUser;password=consltiq";

    protected static String statement;
    protected static Connection conn = null;
    protected static PreparedStatement stmt = null;
    protected static ResultSet resultSet = null;

    protected static boolean executeUpdate(String statement) {
        try {
            conn = DriverManager.getConnection(connectionURL);
            stmt = conn.prepareStatement(statement);
            int result = stmt.executeUpdate();
            if (result > 0)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            terminateQuery();
        }
        return false;
    }

    protected static void executeQuery(String statement) throws SQLException {
        conn = DriverManager.getConnection(connectionURL);
        stmt = conn.prepareStatement(statement);
        resultSet = stmt.executeQuery();
    }

    protected static void terminateQuery(){
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) { /* ignored */}
        }
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
}
