import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Menus {
    public static void showMainMenu(Connection conn, Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("1. Members Menu");
            System.out.println("2. Admin Menu");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    showMembersMenu(conn, scanner);
                    break;
                case 2:
                    showAdminMenu(conn, scanner);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void showMembersMenu(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("1. Add Member");
        System.out.println("2. Show All Members");
        System.out.println("3. Back to Main Menu");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                Functions.addMember(conn, scanner);
                break;
            case 2:
                Functions.showAllMembers(conn);
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void showAdminMenu(Connection conn, Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("1. Add Book");
            System.out.println("2. Delete Book");
            System.out.println("3. Issue/Reissue Book");
            System.out.println("4. Get Books Issued by a Member");
            System.out.println("5. Add/Delete Member");
            System.out.println("6. Get Total Books Info");
            System.out.println("7. Days Remaining for a Book");
            System.out.println("8. Review All Tables");
            System.out.println("9. Create Tables");
            System.out.println("10. Back to Main Menu");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    Functions.addBook(conn, scanner);
                    break;
                case 2:
                    Functions.deleteBook(conn, scanner);
                    break;
                case 3:
                    Functions.issueOrReissueBook(conn, scanner);
                    break;
                case 4:
                    Functions.getBooksIssuedByMember(conn, scanner);
                    break;
                case 5:
                    Functions.addOrDeleteMember(conn, scanner);
                    break;
                case 6:
                    Functions.getTotalBooksInfo(conn);
                    break;
                case 7:
                    Functions.daysRemainingForBook(conn, scanner);
                    break;
                case 8:
                    Functions.reviewTables(conn);
                    break;
                case 9:
                    DatabaseUtils.createTables(conn);
                    break;
                case 10:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
