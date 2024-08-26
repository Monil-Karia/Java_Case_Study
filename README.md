# Java_Case_Study

## Overview

The System is a Java-based application designed for managing books and members in a library. It provides functionalities for adding and deleting books and members, issuing books, and reviewing the data stored in the database. The application interacts with an Oracle database and is designed to simplify management tasks.

## Files and Responsibilities

### `DatabaseUtils.java`

**Responsibilities:**
- Establishes a connection to the Oracle database.
- Creates the required tables (`Members`, `Books`, and `Admin`) if they do not already exist.

**Key Methods:**
- `connect()`: Establishes and returns a connection to the database.
- `createTables(Connection conn)`: Creates the database tables.

### `Menus.java`

**Responsibilities:**
- Manages user interface and navigation.
- Provides menus for different functionalities (members and admin).

**Key Methods:**
- `showMainMenu(Connection conn, Scanner scanner)`: Displays the main menu and handles user navigation.
- `showMembersMenu(Connection conn, Scanner scanner)`: Displays the members menu.
- `showAdminMenu(Connection conn, Scanner scanner)`: Displays the admin menu.

### `Functions.java`

**Responsibilities:**
- Implements core functionalities such as adding/deleting members and books, issuing books, and viewing data.

**Key Methods:**
- `addMember(Connection conn, Scanner scanner)`: Adds a new member.
- `showAllMembers(Connection conn)`: Displays all members.
- `addBook(Connection conn, Scanner scanner)`: Adds a new book.
- `deleteBook(Connection conn, Scanner scanner)`: Deletes a book.
- `issueOrReissueBook(Connection conn, Scanner scanner)`: Issues or reissues a book to a member.
- `getBooksIssuedByMember(Connection conn, Scanner scanner)`: Retrieves books issued to a specific member.
- `getTotalBooksInfo(Connection conn)`: Displays total book counts.
- `daysRemainingForBook(Connection conn, Scanner scanner)`: Shows remaining days for a book.
- `reviewTables(Connection conn)`: Reviews and prints data from all tables.

### `Main.java`

**Responsibilities:**
- Entry point of the application.
- Initializes the database connection and displays the main menu.

**Key Methods:**
- `main(String[] args)`: Starts the application and manages user input.

## Database Schema

The application uses the following database tables:

### `Members`

**Description:**
- Stores information about library members.

**Columns:**
- `ID NUMBER PRIMARY KEY`: Unique identifier for each member.
- `Name VARCHAR2(100) UNIQUE`: Name of the member (must be unique).
- `BookIssued VARCHAR2(100)`: The book currently issued to the member (can be null).
- `MembershipStartDate DATE`: Date when the membership started.
- `MembershipEndDate DATE`: Date when the membership will end.

### `Books`

**Description:**
- Stores information about books available in the library.

**Columns:**
- `ID NUMBER PRIMARY KEY`: Unique identifier for each book.
- `Name VARCHAR2(100) UNIQUE`: Name of the book (must be unique).
- `MemberName VARCHAR2(100)`: Name of the member to whom the book is currently issued (can be null).
- `NumberOfMembershipDays NUMBER`: Number of days for which the book can be issued (can be null).

**Constraints:**
- `FOREIGN KEY (MemberName) REFERENCES Members(Name)`: Ensures that the `MemberName` exists in the `Members` table.

### `Admin`

**Description:**
- Stores information about the book issuance and management by admins.

**Columns:**
- `ID NUMBER PRIMARY KEY`: Unique identifier for each admin record.
- `BookName VARCHAR2(100)`: Name of the book being managed.
- `MemberName VARCHAR2(100)`: Name of the member to whom the book is issued.
- `IssueStart DATE`: Start date of the book issuance.
- `IssueEnd DATE`: End date of the book issuance.

**Constraints:**
- `FOREIGN KEY (BookName) REFERENCES Books(Name)`: Ensures that the `BookName` exists in the `Books` table.
- `FOREIGN KEY (MemberName) REFERENCES Members(Name)`: Ensures that the `MemberName` exists in the `Members` table.

## Requirements

1. **Java Development Kit (JDK)**: JDK 8 or higher.
2. **Oracle Database**: An Oracle database instance with appropriate credentials.
3. **Oracle JDBC Driver**: Ensure the Oracle JDBC driver JAR is included in your classpath.

## Setup Instructions

1. **Database Setup:**
    - Ensure your Oracle database is up and running.
    - Update `DatabaseFiles.java` with your database URL, username, and password if needed.

2. **Compile and Run:**
    - Compile the Java files:
      ```sh
      javac -d bin src/*.java
      ```
    - Run the application:
      ```sh
      java -cp bin Main
      ```

## Usage

1. **Starting the Application:**
    - Run the `Main` class to start the application.
    - Navigate through the menus to perform various actions.

2. **Available Actions:**
    - **Members Menu**: Add a member, show all members.
    - **Admin Menu**: Add/delete a book, issue/reissue a book, get books issued by a member, add/delete member, get total books info, days remaining for a book, review all tables.

## Troubleshooting

- **Database Connection Issues:**
    - Verify database URL, username, and password in `DatabaseFiles.java`.
    - Ensure the Oracle JDBC driver is correctly included in the classpath.

- **SQL Errors:**
    - Check SQL syntax and ensure that the database schema matches the expected structure.
