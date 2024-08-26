import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtils {

    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "12345";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void createTables(Connection conn) throws SQLException {
        String[] sqlStatements = {
                "CREATE TABLE Members (" +
                        "ID NUMBER PRIMARY KEY, " +
                        "Name VARCHAR2(100) UNIQUE, " +
                        "BookIssued VARCHAR2(100), " +
                        "MembershipStartDate DATE, " +
                        "MembershipEndDate DATE" +
                        ")",

                "CREATE TABLE Books (" +
                        "ID NUMBER PRIMARY KEY, " +
                        "Name VARCHAR2(100) UNIQUE, " +
                        "MemberName VARCHAR2(100), " +
                        "NumberOfMembershipDays NUMBER, " +
                        "CONSTRAINT fk_member " +
                        "FOREIGN KEY (MemberName) " +
                        "REFERENCES Members(Name)" +
                        ")",

                "CREATE TABLE Admin (" +
                        "ID NUMBER PRIMARY KEY, " +
                        "BookName VARCHAR2(100), " +
                        "MemberName VARCHAR2(100), " +
                        "IssueStart DATE, " +
                        "IssueEnd DATE, " +
                        "CONSTRAINT fk_book " +
                        "FOREIGN KEY (BookName) " +
                        "REFERENCES Books(Name), " +
                        "CONSTRAINT fk_member_admin " +
                        "FOREIGN KEY (MemberName) " +
                        "REFERENCES Members(Name)" +
                        ")"
        };
        try (Statement stmt = conn.createStatement()) {
            for (String sql : sqlStatements) {
                try {
                    stmt.execute(sql);
                    System.out.println("Table created successfully.");
                } catch (SQLException e) {
                    if (e.getErrorCode() == 955) {
                        System.out.println("Table already exists.");
                    } else {
                        throw e;
                    }
                }
            }
        }
    }
}
