import java.sql.*;
import java.util.Scanner;

public class testing_admin {
        private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
        private static final String DB_USER = "system";
        private static final String DB_PASSWORD = "12345";

        // Connection to the database
        private static Connection connect() throws SQLException {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }

        // Create database tables
        private static void createTables(Connection conn) throws SQLException{
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
                            ")",

                    "CREATE TABLE AdminUsers (" +
                            "Username VARCHAR2(100) PRIMARY KEY, " +
                            "Password VARCHAR2(100) NOT NULL" +
                            ")",

                    "CREATE TABLE MemberUsers (" +
                            "Username VARCHAR2(100) PRIMARY KEY, " +
                            "Password VARCHAR2(100) NOT NULL" +
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

        private static void addUser(Connection conn, Scanner scanner, String tableName) throws SQLException {
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            String sql = "INSERT INTO " + tableName + " (Username, Password) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.executeUpdate();
                System.out.println("User added successfully.");
            }
        }

        private static boolean authenticateUser(Connection conn, Scanner scanner, String tableName) throws SQLException {
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE Username = ? AND Password = ? ";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, tableName); // Sanitize tableName before setting
                stmt.setString(2, username);
                stmt.setString(3, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return true;
                    } else {
                        System.out.println("Invalid username or password.");
                        return false;
                    }
                }
            }
        }
        // Add a new member
        private static void addMember(Connection conn, Scanner scanner) throws SQLException {
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
        private static void showAllMembers(Connection conn) throws SQLException {
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
        private static void addBook(Connection conn, Scanner scanner) throws SQLException {
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
        private static void deleteBook(Connection conn, Scanner scanner) throws SQLException {
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
        private static void deleteMember(Connection conn, Scanner scanner) throws SQLException {
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
        }

        private static void issueOrReissueBook(Connection conn, Scanner scanner) throws SQLException {
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
        private static void getBooksIssuedByMember(Connection conn, Scanner scanner) throws SQLException {
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
        private static void getTotalBooksInfo(Connection conn) throws SQLException {
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
        private static void daysRemainingForBook(Connection conn, Scanner scanner) throws SQLException {
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

        // Main menu
        private static void menu() {
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as Member");
            System.out.println("3. Exit");
        }

        // Members menu
        private static void membersMenu(Connection conn, Scanner scanner) throws SQLException {
            while (true) {
                System.out.println("1. Show All Members");
                System.out.println("2. Logout");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        showAllMembers(conn);
                        break;
                    case 2:
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        }


        // Review all tables
        private static void reviewTables(Connection conn) throws SQLException {
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

        // Admin menu
        private static void adminMenu(Connection conn, Scanner scanner) throws SQLException {
            while (true) {
                System.out.println("1. Add Book");
                System.out.println("2. Delete Book");
                System.out.println("3. Issue/Reissue Book");
                System.out.println("4. Get Books Issued by a Member");
                System.out.println("5. Add Member");
                System.out.println("6. Delete Member");
                System.out.println("7. Add Admin User");
                System.out.println("8. Add Member User");
                System.out.println("9. Get Total Books Info");
                System.out.println("10. Days Remaining for a Book");
                System.out.println("11. Review All Tables");
                System.out.println("12. Create Tables");
                System.out.println("13. Logout");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        addBook(conn, scanner);
                        break;
                    case 2:
                        deleteBook(conn, scanner);
                        break;
                    case 3:
                        issueOrReissueBook(conn, scanner);
                        break;
                    case 4:
                        getBooksIssuedByMember(conn, scanner);
                        break;
                    case 5:
                        addMember(conn, scanner);
                        break;
                    case 6:
                        deleteMember(conn, scanner);
                        break;
                    case 7:
                        addUser(conn, scanner, "AdminUsers");
                        break;
                    case 8:
                        addUser(conn, scanner, "MemberUsers");
                        break;
                    case 9:
                        getTotalBooksInfo(conn);
                        break;
                    case 10:
                        daysRemainingForBook(conn, scanner);
                        break;
                    case 11:
                        reviewTables(conn);
                        break;
                    case 12:
                        createTables(conn);
                        break;
                    case 13:
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        }

        // Main method
        public static void main(String[] args) {
            try (Connection conn = connect(); Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    menu();
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:
                            if (authenticateUser(conn, scanner, "ADMINUSERS")) {
                                adminMenu(conn, scanner);
                            }
                            break;
                        case 2:
                            if (authenticateUser(conn, scanner, "MEMBERUSERS")) {
                                membersMenu(conn, scanner);
                            }
                            break;
                        case 3:
                            System.out.println("Exiting...");
                            return;
                        default:
                            System.out.println("Invalid choice.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
