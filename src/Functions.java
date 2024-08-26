import java.sql.*;
import java.util.Scanner;

public class Functions {
    public static void addMember(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Member ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Member Name: ");
        String name = scanner.nextLine();

        String sql = "INSERT INTO Members (ID, Name) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.executeUpdate();
            System.out.println("Member added successfully.");
        }
    }

    public static void showAllMembers(Connection conn) throws SQLException {
        String sql = "SELECT * FROM Members";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("All Members:");
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Name");
                String bookIssued = rs.getString("BookIssued");
                Date membershipStartDate = rs.getDate("MembershipStartDate");
                Date membershipEndDate = rs.getDate("MembershipEndDate");

                System.out.printf("ID: %d, Name: %s, Book Issued: %s, Membership Start Date: %s, Membership End Date: %s%n",
                        id, name, bookIssued, membershipStartDate, membershipEndDate);
            }
        }
    }

    public static void addBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Book ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Book Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Member Name (optional, or leave blank): ");
        String memberName = scanner.nextLine();

        String sql = "INSERT INTO Books (ID, Name, MemberName) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            if (memberName.isEmpty()) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, memberName);
            }
            stmt.executeUpdate();
            System.out.println("Book added successfully.");
        }
    }

    public static void deleteBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Book ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        // Check if the book is issued before deleting
        String checkIssuedSQL = "SELECT COUNT(*) FROM Admin WHERE BookName IN (SELECT Name FROM Books WHERE ID = ?)";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkIssuedSQL)) {
            checkStmt.setInt(1, id);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Book is issued. Please handle the issued records before deleting.");
                    return;
                }
            }
        }

        String deleteSQL = "DELETE FROM Books WHERE ID = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL)) {
            deleteStmt.setInt(1, id);
            int rowsAffected = deleteStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Book deleted successfully.");
            } else {
                System.out.println("Book not found.");
            }
        }
    }

    public static void addOrDeleteMember(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("1. Add Member");
        System.out.println("2. Delete Member");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                addMember(conn, scanner);
                break;
            case 2:
                deleteMember(conn, scanner);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void deleteMember(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Member Name to delete: ");
        String name = scanner.nextLine();

        // Check if the member has any issued books
        String checkIssuedSQL = "SELECT COUNT(*) FROM Books WHERE MemberName = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkIssuedSQL)) {
            checkStmt.setString(1, name);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Member has issued books. Please handle the issued books before deleting.");
                    return;
                }
            }
        }

        // Check if the member exists before attempting to delete
        String checkMemberSQL = "SELECT COUNT(*) FROM Members WHERE Name = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkMemberSQL)) {
            checkStmt.setString(1, name);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("Member not found.");
                    return;
                }
            }
        }

        String deleteSQL = "DELETE FROM Members WHERE Name = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL)) {
            deleteStmt.setString(1, name);
            int rowsAffected = deleteStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Member deleted successfully.");
            } else {
                System.out.println("Member not found.");
            }
        }
    }

    public static void issueOrReissueBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Book Name to Issue/Reissue: ");
        String bookName = scanner.nextLine();
        System.out.print("Enter Member Name: ");
        String memberName = scanner.nextLine();
        System.out.print("Enter Issue Start Date (YYYY-MM-DD): ");
        String issueStartDate = scanner.nextLine();
        System.out.print("Enter Issue End Date (YYYY-MM-DD): ");
        String issueEndDate = scanner.nextLine();

        String checkBookSQL = "SELECT COUNT(*) FROM Books WHERE Name = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkBookSQL)) {
            checkStmt.setString(1, bookName);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("Book not found.");
                    return;
                }
            }
        }

        String checkMemberSQL = "SELECT COUNT(*) FROM Members WHERE Name = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkMemberSQL)) {
            checkStmt.setString(1, memberName);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("Member not found.");
                    return;
                }
            }
        }

        String issueSQL = "INSERT INTO Admin (BookName, MemberName, IssueStart, IssueEnd) VALUES (?, ?, ?, ?)";
        try (PreparedStatement issueStmt = conn.prepareStatement(issueSQL)) {
            issueStmt.setString(1, bookName);
            issueStmt.setString(2, memberName);
            issueStmt.setDate(3, Date.valueOf(issueStartDate));
            issueStmt.setDate(4, Date.valueOf(issueEndDate));
            issueStmt.executeUpdate();
            System.out.println("Book issued/reissued successfully.");
        }
    }

    public static void getBooksIssuedByMember(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Member Name: ");
        String memberName = scanner.nextLine();

        String sql = "SELECT BookName FROM Admin WHERE MemberName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberName);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("Books issued to " + memberName + ":");
                while (rs.next()) {
                    String bookName = rs.getString("BookName");
                    System.out.println(bookName);
                }
            }
        }
    }

    public static void getTotalBooksInfo(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) AS TotalBooks FROM Books";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int totalBooks = rs.getInt("TotalBooks");
                System.out.println("Total Books: " + totalBooks);
            }
        }
    }

    public static void daysRemainingForBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Book Name: ");
        String bookName = scanner.nextLine();

        String sql = "SELECT IssueEnd FROM Admin WHERE BookName = ? AND CURRENT_DATE <= IssueEnd";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date issueEndDate = rs.getDate("IssueEnd");
                    long daysRemaining = (issueEndDate.getTime() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
                    System.out.println("Days remaining for book: " + daysRemaining);
                } else {
                    System.out.println("Book not found or no longer valid.");
                }
            }
        }
    }

    public static void reviewTables(Connection conn) throws SQLException {
        String[] tables = {"Members", "Books", "Admin"};

        for (String table : tables) {
            System.out.println("Table: " + table);
            String sql = "SELECT * FROM " + table;
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                // Print column names
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rsmd.getColumnName(i) + "\t");
                }
                System.out.println();

                // Print rows
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println();
                }
            }
        }
    }
}
