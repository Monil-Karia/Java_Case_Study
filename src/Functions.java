import java.sql.*;
import java.util.Scanner;

public class Functions {
    // Add a new member
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

    // Show all members
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

    // Add a new book
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

    // Delete a book
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

    // Add or delete a member
    public static void addOrDeleteMember(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("1. Add Member");
        System.out.println("2. Delete Member");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            addMember(conn, scanner);
        } else if (choice == 2) {
            System.out.print("Enter Member Name to delete: ");
            String name = scanner.nextLine();

            // Check for references in Books and Admin
            String checkBooksSQL = "SELECT COUNT(*) FROM Books WHERE MemberName = ?";
            int bookCount;
            try (PreparedStatement stmt = conn.prepareStatement(checkBooksSQL)) {
                stmt.setString(1, name);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        bookCount = rs.getInt(1);
                    } else {
                        System.out.println("Error checking books.");
                        return;
                    }
                }
            }

            if (bookCount > 0) {
                System.out.println("Member has issued books. Please return or reassign the books before deleting the member.");
                return;
            }

            String checkAdminSQL = "SELECT COUNT(*) FROM Admin WHERE MemberName = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkAdminSQL)) {
                stmt.setString(1, name);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        bookCount = rs.getInt(1);
                    } else {
                        System.out.println("Error checking admin.");
                        return;
                    }
                }
            }

            if (bookCount > 0) {
                System.out.println("Member is referenced in the Admin table. Please update the records before deleting the member.");
                return;
            }

            // Delete the member if no references are found
            String sql = "DELETE FROM Members WHERE Name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Member deleted successfully.");
                } else {
                    System.out.println("Member not found.");
                }
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    public static void issueOrReissueBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Book ID to issue/reissue: ");
        int bookId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter Member Name: ");
        String memberName = scanner.nextLine();

        System.out.print("Enter Membership Start Date (yyyy-mm-dd): ");
        String startDateInput = scanner.nextLine();
        Date membershipStartDate = Date.valueOf(startDateInput);
        Date membershipEndDate = new Date(membershipStartDate.getTime() + 90L * 24 * 60 * 60 * 1000); // 90 days later

        // Retrieve the book name using the book ID
        String getBookNameSQL = "SELECT Name FROM Books WHERE ID = ?";
        String bookName;
        try (PreparedStatement getBookNameStmt = conn.prepareStatement(getBookNameSQL)) {
            getBookNameStmt.setInt(1, bookId);
            try (ResultSet rs = getBookNameStmt.executeQuery()) {
                if (rs.next()) {
                    bookName = rs.getString("Name");
                } else {
                    System.out.println("Book not found.");
                    return;
                }
            }
        }

        // Update the Members table with the retrieved book name
        String updateMembersSQL = "UPDATE Members " +
                "SET MembershipStartDate = ?, " +
                "    MembershipEndDate = ?, " +
                "    BookIssued = ? " +
                "WHERE Name = ?";
        try (PreparedStatement updateMembersStmt = conn.prepareStatement(updateMembersSQL)) {
            updateMembersStmt.setDate(1, membershipStartDate);
            updateMembersStmt.setDate(2, membershipEndDate);
            updateMembersStmt.setString(3, bookName);
            updateMembersStmt.setString(4, memberName);
            int rowsAffected = updateMembersStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Membership dates and issued book updated successfully.");
            } else {
                System.out.println("Member not found.");
            }
        }

        // Update the Books table with the member's name
        String updateBooksSQL = "UPDATE Books " +
                "SET MemberName = ? " +
                "WHERE ID = ?";
        try (PreparedStatement updateBooksStmt = conn.prepareStatement(updateBooksSQL)) {
            updateBooksStmt.setString(1, memberName);
            updateBooksStmt.setInt(2, bookId);
            updateBooksStmt.executeUpdate();
        }

        // Update the number of membership days remaining in the Books table
        String updateBooksDaysRemainingSQL = "UPDATE Books b " +
                "SET NumberOfMembershipDays = (" +
                "    SELECT M.MembershipEndDate - M.MembershipStartDate " +
                "    FROM Members M " +
                "    WHERE M.Name = b.MemberName" +
                ") " +
                "WHERE b.ID = ?";
        try (PreparedStatement updateBooksDaysRemainingStmt = conn.prepareStatement(updateBooksDaysRemainingSQL)) {
            updateBooksDaysRemainingStmt.setInt(1, bookId);
            updateBooksDaysRemainingStmt.executeUpdate();
        }

        System.out.println("Book issued/reissued and membership updated successfully.");
    }



    // Get books issued by a member
    public static void getBooksIssuedByMember(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Member Name: ");
        String memberName = scanner.nextLine();

        String sql = "SELECT ID, Name FROM Books WHERE MemberName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("Book ID: %d, Book Name: %s%n", rs.getInt("ID"), rs.getString("Name"));
                }
            }
        }
    }

    // Get total books info
    public static void getTotalBooksInfo(Connection conn) throws SQLException {
        String[] queries = {
                "SELECT COUNT(*) AS TotalBooks FROM Books",
                "SELECT COUNT(*) AS IssuedBooks FROM Books WHERE MemberName IS NOT NULL",
                "SELECT COUNT(*) AS AvailableBooks FROM Books WHERE MemberName IS NULL"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String query : queries) {
                try (ResultSet rs = stmt.executeQuery(query)) {
                    if (rs.next()) {
                        System.out.println(rs.getMetaData().getColumnName(1) + ": " + rs.getInt(1));
                    }
                }
            }
        }
    }

    // Get days remaining for a book
    public static void daysRemainingForBook(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Book ID: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();

        String sql = "SELECT NumberOfMembershipDays AS DaysRemaining FROM Books WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Days remaining for the book: " + rs.getInt("DaysRemaining"));
                } else {
                    System.out.println("Book not found.");
                }
            }
        }
    }

    // Review all tables
    public static void reviewTables(Connection conn) throws SQLException {
        String[] tableNames = {"Members", "Admin", "Books"};
        try (Statement stmt = conn.createStatement()) {
            for (String tableName : tableNames) {
                System.out.println("Table: " + tableName);

                String sql = "SELECT * FROM " + tableName;
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Print column names
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.printf("%-20s", metaData.getColumnName(i));
                    }
                    System.out.println();

                    // Print rows
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            System.out.printf("%-20s", rs.getString(i));
                        }
                        System.out.println();
                    }
                    System.out.println(); // Blank line between tables
                } catch (SQLException e) {
                    System.out.println("Error querying table " + tableName + ": " + e.getMessage());
                }
            }
        }
    }
}
