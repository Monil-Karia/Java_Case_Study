import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;


public class Main {
    public static void main(String[] args) throws SQLException,ClassNotFoundException{
        Statement stmt = null;
        ResultSet rs = null;
        Class.forName("oracle.jdbc.driver.OracleDriver");

        Connection conn =DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "12345");

        String sql = "SELECT * from HELP";
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        // or alternatively, if you don't know ahead of time that
        // the query will be a SELECT...
        if (stmt.execute(sql)) {
            rs = stmt.getResultSet();
        }
        System.out.println("Hello world!");
    }
}
