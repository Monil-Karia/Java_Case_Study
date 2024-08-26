import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Connection conn = DatabaseUtils.connect(); Scanner scanner = new Scanner(System.in)) {
            DatabaseUtils.createTables(conn);
            Menus.showMainMenu(conn, scanner);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
