//package com.buet.bibliotech.db;
//
//import com.buet.bibliotech.Books;
//import com.buet.bibliotech.IssueInfo;
//import com.buet.bibliotech.RequestInfo;
//import com.buet.bibliotech.BorrowedBookModel;
//import com.buet.bibliotech.member;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.chart.PieChart;
//import javafx.scene.chart.XYChart;
//
//import java.sql.*;
//
//public class Database {
//
//    private static final String DB_URL = "jdbc:sqlite:bibliotech.db";
//    private static Database handler = null;
//    private static Connection connection = null;
//
//    private Database() {
//        createTables();
//    }
//
//    public static Database getInstance() {
//        if (handler == null) {
//            handler = new Database();
//        }
//        return handler;
//    }
//
//    public static Connection getConnection() {
//        try {
//            if (connection == null || connection.isClosed()) {
//                connection = DriverManager.getConnection(DB_URL);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return connection;
//    }
//
//    private void createTables() {
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
//            // 1. Core Tables
//            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL);");
//            stmt.execute("CREATE TABLE IF NOT EXISTS members (id TEXT PRIMARY KEY, name TEXT NOT NULL, department TEXT, batch TEXT, email TEXT, password TEXT DEFAULT '1234');");
//
//
//            stmt.execute("CREATE TABLE IF NOT EXISTS books (bookid TEXT PRIMARY KEY, bookname TEXT NOT NULL, author TEXT, category TEXT, total_copies INTEGER DEFAULT 1, available_copies INTEGER DEFAULT 1, image_path TEXT);");
//
//            // 3. Issue Table
//            stmt.execute("CREATE TABLE IF NOT EXISTS issue (issueID INTEGER PRIMARY KEY AUTOINCREMENT, bookID TEXT, memberID TEXT, issueTime TEXT, status TEXT DEFAULT 'Borrowed', FOREIGN KEY (bookID) REFERENCES books(bookid), FOREIGN KEY (memberID) REFERENCES members(id));");
//
//            // 4. Request Table
//            stmt.execute("CREATE TABLE IF NOT EXISTS requests (bookID TEXT, memberID TEXT);");
//
//            // Migration logic for existing databases
//            try { stmt.execute("ALTER TABLE books ADD COLUMN total_copies INTEGER DEFAULT 1"); } catch (SQLException e) {}
//            try { stmt.execute("ALTER TABLE books ADD COLUMN available_copies INTEGER DEFAULT 1"); } catch (SQLException e) {}
//            try { stmt.execute("ALTER TABLE issue ADD COLUMN status TEXT DEFAULT 'Borrowed'"); } catch (SQLException e) {}
//            // NEW MIGRATION: Add image_path if it doesn't exist
//            try { stmt.execute("ALTER TABLE books ADD COLUMN image_path TEXT"); } catch (SQLException e) {}
//
//            stmt.execute("INSERT OR IGNORE INTO users (username, password) VALUES ('admin', '1234')");
//        } catch (SQLException e) {
//            System.err.println("Error creating tables: " + e.getMessage());
//        }
//    }
//
//    public String getLoginRole(String username, String password) {
//        String adminQuery = "SELECT * FROM users WHERE LOWER(username) = LOWER(?) AND password = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(adminQuery)) {
//            pstmt.setString(1, username);
//            pstmt.setString(2, password);
//            if (pstmt.executeQuery().next()) return "ADMIN";
//        } catch (SQLException e) { e.printStackTrace(); }
//
//        String memberQuery = "SELECT * FROM members WHERE LOWER(name) = LOWER(?) AND id = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(memberQuery)) {
//            pstmt.setString(1, username);
//            pstmt.setString(2, password);
//            if (pstmt.executeQuery().next()) return "MEMBER";
//        } catch (SQLException e) { e.printStackTrace(); }
//
//        return "FAILED";
//    }
//
//    // UPDATED: Now accepts imagePath
//    public boolean addbook(String bookid, String bookname, String author, String category, int copies, String imagePath) {
//        String query = "INSERT INTO books (bookid, bookname, author, category, total_copies, available_copies, image_path) VALUES(?,?,?,?,?,?,?)";
//        try (Connection conn = getConnection(); PreparedStatement psmt = conn.prepareStatement(query)) {
//            psmt.setString(1, bookid);
//            psmt.setString(2, bookname);
//            psmt.setString(3, author);
//            psmt.setString(4, category);
//            psmt.setInt(5, copies);
//            psmt.setInt(6, copies);
//            psmt.setString(7, imagePath); // Save image path
//            psmt.executeUpdate();
//            return true;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean issueBook(String bID, String mID, String date) {
//        String checkStock = "SELECT available_copies FROM books WHERE bookid = ?";
//        String updateStock = "UPDATE books SET available_copies = available_copies - 1 WHERE bookid = ?";
//        String insertIssue = "INSERT INTO issue (bookID, memberID, issueTime) VALUES(?,?,?)";
//
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false);
//            try (PreparedStatement checkStmt = conn.prepareStatement(checkStock);
//                 PreparedStatement updateStmt = conn.prepareStatement(updateStock);
//                 PreparedStatement insertStmt = conn.prepareStatement(insertIssue)) {
//
//                checkStmt.setString(1, bID);
//                ResultSet rs = checkStmt.executeQuery();
//                if (rs.next() && rs.getInt("available_copies") > 0) {
//                    updateStmt.setString(1, bID);
//                    updateStmt.executeUpdate();
//
//                    insertStmt.setString(1, bID);
//                    insertStmt.setString(2, mID);
//                    insertStmt.setString(3, date);
//                    insertStmt.executeUpdate();
//
//                    conn.commit();
//                    return true;
//                }
//            } catch (SQLException e) { conn.rollback(); }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return false;
//    }
//
//    public boolean approveReturn(int issueID, String bookID) {
//        String deleteIssue = "DELETE FROM issue WHERE issueID = ?";
//        String updateStock = "UPDATE books SET available_copies = available_copies + 1 WHERE bookid = ?";
//
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false);
//            try (PreparedStatement dStmt = conn.prepareStatement(deleteIssue);
//                 PreparedStatement uStmt = conn.prepareStatement(updateStock)) {
//
//                dStmt.setInt(1, issueID);
//                dStmt.executeUpdate();
//
//                uStmt.setString(1, bookID);
//                uStmt.executeUpdate();
//
//                conn.commit();
//                return true;
//            } catch (SQLException e) { conn.rollback(); }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return false;
//    }
//
//    public boolean rejectReturn(int issueID) {
//        String query = "UPDATE issue SET status = 'Borrowed' WHERE issueID = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setInt(1, issueID);
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public boolean requestBookReturn(String bookID) {
//        String query = "UPDATE issue SET status = 'Pending Return' WHERE bookID = ? AND status = 'Borrowed'";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, bookID);
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public ObservableList<Books> getAvailableBooks() {
//        ObservableList<Books> list = FXCollections.observableArrayList();
//        String query = "SELECT * FROM books"; // Fetches all books for inventory management
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) {
//                list.add(new Books(
//                        rs.getString("bookid"),
//                        rs.getString("bookname"),
//                        rs.getString("author"),
//                        rs.getString("category"),
//                        rs.getInt("total_copies"),
//                        rs.getInt("available_copies"),
//                        rs.getString("image_path") // FETCH IMAGE PATH
//                ));
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public ObservableList<IssueInfo> getPendingReturns() {
//        ObservableList<IssueInfo> list = FXCollections.observableArrayList();
//        String query = "SELECT issue.issueID, issue.bookID, books.bookname, issue.memberID, members.name, issue.issueTime FROM issue JOIN books ON issue.bookID = books.bookid JOIN members ON issue.memberID = members.id";
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) {
//                list.add(new IssueInfo(rs.getInt("issueID"), rs.getString("bookID"), rs.getString("bookname"), rs.getString("memberID"), rs.getString("name"), rs.getString("issueTime")));
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public ObservableList<IssueInfo> getReturnRequests() {
//        ObservableList<IssueInfo> list = FXCollections.observableArrayList();
//        String query = "SELECT issue.issueID, issue.bookID, books.bookname, issue.memberID, members.name, issue.issueTime FROM issue JOIN books ON issue.bookID = books.bookid JOIN members ON issue.memberID = members.id WHERE issue.status = 'Pending Return'";
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) {
//                list.add(new IssueInfo(rs.getInt("issueID"), rs.getString("bookID"), rs.getString("bookname"), rs.getString("memberID"), rs.getString("name"), rs.getString("issueTime")));
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public boolean approveRequest(String bookID, String memberID) {
//        String updateStock = "UPDATE books SET available_copies = available_copies - 1 WHERE bookid = ?";
//        String deleteReq = "DELETE FROM requests WHERE bookID = ? AND memberID = ?";
//        String insertIssue = "INSERT INTO issue (bookID, memberID, issueTime) VALUES (?, ?, CURRENT_TIMESTAMP)";
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false);
//            try (PreparedStatement uStmt = conn.prepareStatement(updateStock);
//                 PreparedStatement dStmt = conn.prepareStatement(deleteReq);
//                 PreparedStatement iStmt = conn.prepareStatement(insertIssue)) {
//                uStmt.setString(1, bookID); uStmt.executeUpdate();
//                dStmt.setString(1, bookID); dStmt.setString(2, memberID); dStmt.executeUpdate();
//                iStmt.setString(1, bookID); iStmt.setString(2, memberID); iStmt.executeUpdate();
//                conn.commit();
//                return true;
//            } catch (SQLException e) { conn.rollback(); return false; }
//        } catch (SQLException e) { return false; }
//    }
//
//    public ObservableList<BorrowedBookModel> getBorrowedBooksByMember(String memberId) {
//        ObservableList<BorrowedBookModel> list = FXCollections.observableArrayList();
//        String query = "SELECT books.bookname, books.author, books.bookid FROM issue JOIN books ON issue.bookID = books.bookid WHERE issue.memberID = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, memberId);
//            ResultSet rs = pstmt.executeQuery();
//            int serial = 1;
//            while (rs.next()) {
//                list.add(new BorrowedBookModel(String.valueOf(serial++), rs.getString("bookname"), rs.getString("bookid"), rs.getString("author")));
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public boolean deleteMember(String memberId) {
//        // Queries
//        String findIssuedBooks = "SELECT bookID FROM issue WHERE memberID = ?";
//        String findRequestedBooks = "SELECT bookID FROM requests WHERE memberID = ?";
//        String incrementStock = "UPDATE books SET available_copies = available_copies + 1 WHERE bookid = ?";
//
//        String deleteIssue = "DELETE FROM issue WHERE memberID = ?";
//        String deleteRequests = "DELETE FROM requests WHERE memberID = ?";
//        String deleteMember = "DELETE FROM members WHERE id = ?";
//
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false); // START TRANSACTION
//
//            try (PreparedStatement psFindIssue = conn.prepareStatement(findIssuedBooks);
//                 PreparedStatement psFindReq = conn.prepareStatement(findRequestedBooks);
//                 PreparedStatement psStock = conn.prepareStatement(incrementStock);
//                 PreparedStatement psDelIssue = conn.prepareStatement(deleteIssue);
//                 PreparedStatement psDelReq = conn.prepareStatement(deleteRequests);
//                 PreparedStatement psDelMem = conn.prepareStatement(deleteMember)) {
//
//                // 1. RESTORE STOCK FROM ISSUED BOOKS
//                psFindIssue.setString(1, memberId);
//                ResultSet rsIssue = psFindIssue.executeQuery();
//                while (rsIssue.next()) {
//                    psStock.setString(1, rsIssue.getString("bookID"));
//                    psStock.executeUpdate(); // Runs +1 for every book they held
//                }
//
//                // 2. RESTORE STOCK FROM PENDING REQUESTS
//                // (Only if your logic decreases available_copies when a request is made)
//                psFindReq.setString(1, memberId);
//                ResultSet rsReq = psFindReq.executeQuery();
//                while (rsReq.next()) {
//                    psStock.setString(1, rsReq.getString("bookID"));
//                    psStock.executeUpdate(); // Returns requested books to the shelf
//                }
//
//                // 3. WIPE ALL ACTIVITY
//                psDelIssue.setString(1, memberId);
//                psDelIssue.executeUpdate();
//
//                psDelReq.setString(1, memberId);
//                psDelReq.executeUpdate();
//
//                // 4. DELETE THE MEMBER
//                psDelMem.setString(1, memberId);
//                int affectedRows = psDelMem.executeUpdate();
//
//                conn.commit(); // SAVE EVERYTHING
//                System.out.println("Member " + memberId + " wiped. Inventory restored.");
//                return affectedRows > 0;
//
//            } catch (SQLException e) {
//                conn.rollback(); // UNDO EVERYTHING if any error occurs
//                e.printStackTrace();
//                return false;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public int getTotalBooksCount(){
//        // IFNULL handles cases where the table is empty
//        String query="SELECT IFNULL(SUM(total_copies), 0) FROM books";
//        try(Connection conn=getConnection();Statement stmt=conn.createStatement(); ResultSet rs=stmt.executeQuery(query)) {
//            if(rs.next()) return rs.getInt(1);
//        } catch (SQLException e) { e.printStackTrace(); }
//        return 0;
//    }
//
//    public int getAvailableBooksCount(){
//        String query= "SELECT IFNULL(SUM(available_copies), 0) FROM books";
//        try(Connection conn=getConnection();Statement stmt=conn.createStatement(); ResultSet rs=stmt.executeQuery(query)) {
//            if(rs.next()) return rs.getInt(1);
//        } catch (SQLException e) { e.printStackTrace(); }
//        return 0;
//    }
//
//    public int getTotalMembersCount(){
//        String query="SELECT COUNT(*) FROM members";
//        try(Connection conn=getConnection();Statement stmt=conn.createStatement(); ResultSet rs=stmt.executeQuery(query)) {
//            if(rs.next()) return rs.getInt(1);
//        } catch (SQLException e) { e.printStackTrace(); }
//        return 0;
//    }
//
//    public int getTotalIssuedBooksCount(){
//        String query="SELECT COUNT(*) FROM issue";
//        try(Connection conn=getConnection();Statement stmt=conn.createStatement(); ResultSet rs=stmt.executeQuery(query)) {
//            if(rs.next()) return rs.getInt(1);
//        } catch (SQLException e) { e.printStackTrace(); }
//        return 0;
//    }
//
//    public int getPendingRequestsCount(){
//        String query="SELECT COUNT(*) FROM requests";
//        try(Connection conn=getConnection();Statement stmt=conn.createStatement(); ResultSet rs=stmt.executeQuery(query)) {
//            if(rs.next()) return rs.getInt(1);
//        } catch (SQLException e) { e.printStackTrace(); }
//        return 0;
//    }
//
//    public boolean addmember(String id, String name, String department, String Batch, String Email) {
//        String query = "INSERT INTO members (id, name, department, batch, email) VALUES(?,?,?,?,?)";
//        try (Connection conn = getConnection(); PreparedStatement psmt = conn.prepareStatement(query)) {
//            psmt.setString(1, id); psmt.setString(2, name); psmt.setString(3, department); psmt.setString(4, Batch); psmt.setString(5, Email);
//            psmt.executeUpdate(); return true;
//        } catch (SQLException e) { return false; }
//    }
//
//    public ObservableList<member> getMembers() {
//        ObservableList<member> list = FXCollections.observableArrayList();
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM members")) {
//            while (rs.next()) {
//                list.add(new member(rs.getString("id"), rs.getString("name"), rs.getString("department"), rs.getString("batch"), rs.getString("email")));
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public boolean isBookIdExists(String bookid) {
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM books WHERE bookid = ?")) {
//            pstmt.setString(1, bookid); ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) return rs.getInt(1) > 0;
//        } catch (SQLException e) { e.printStackTrace(); }
//        return false;
//    }
//
//    public boolean deleteBook(String bookId) {
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("DELETE FROM books WHERE bookid = ?")) {
//            pstmt.setString(1, bookId); return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public boolean placeRequest(String bookID, String memberID) {
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement("INSERT INTO requests (bookID, memberID) VALUES (?, ?)")) {
//            pstmt.setString(1, bookID); pstmt.setString(2, memberID); pstmt.executeUpdate(); return true;
//        } catch (SQLException e) { return false; }
//    }
//
//    public ObservableList<RequestInfo> getPendingRequests() {
//        ObservableList<RequestInfo> list = FXCollections.observableArrayList();
//        // INNER JOIN ensures we only see requests where the Book and Member still exist in the DB
//        String query = """
//        SELECT r.bookID, b.bookname, r.memberID, m.name
//        FROM requests r
//        JOIN books b ON r.bookID = b.bookid
//        JOIN members m ON r.memberID = m.id
//        """;
//
//        try (Connection conn = getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                // Match the constructor: (BookID, BookName, MemberID, MemberName)
//                list.add(new RequestInfo(
//                        rs.getString("bookID"),
//                        rs.getString("bookname"),
//                        rs.getString("memberID"),
//                        rs.getString("name")
//                ));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    public boolean isMemberIdExists(String memberid) {
//        String query = "SELECT COUNT(*) FROM members WHERE id = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, memberid);
//            ResultSet rs = pstmt.executeQuery();
//            return rs.next() && rs.getInt(1) > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public member getMemberById(String memberId) {
//        String query = "SELECT * FROM members WHERE id = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, memberId);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return new member(rs.getString("id"), rs.getString("name"), rs.getString("department"), rs.getString("batch"), rs.getString("email"));
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return null;
//    }
//
//    public ObservableList<PieChart.Data> getBookCategoryData() {
//        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
//        String query = "SELECT category, SUM(total_copies) FROM books GROUP BY category";
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) {
//                data.add(new PieChart.Data(rs.getString(1), rs.getInt(2)));
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return data;
//    }
//
//    public XYChart.Series<String, Number> getTopBorrowersData() {
//        XYChart.Series<String, Number> series = new XYChart.Series<>();
//        series.setName("Books Borrowed");
//        String query = "SELECT members.name, COUNT(issue.issueID) as count FROM issue JOIN members ON issue.memberID = members.id GROUP BY members.id ORDER BY count DESC LIMIT 5";
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) {
//                series.getData().add(new XYChart.Data<>(rs.getString(1), rs.getInt(2)));
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return series;
//    }
//
//    public boolean updateMemberProfileFull(String oldId, String newId, String name, String email, String dept, String batch) {
//        String query = "UPDATE members SET id = ?, name = ?, email = ?, department = ?, batch = ? WHERE id = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, newId);
//            pstmt.setString(2, name);
//            pstmt.setString(3, email);
//            pstmt.setString(4, dept);
//            pstmt.setString(5, batch);
//            pstmt.setString(6, oldId);
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public Books getBookById(String bookId) {
//        String query = "SELECT * FROM books WHERE bookid = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, bookId);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return new Books(
//                        rs.getString("bookid"),
//                        rs.getString("bookname"),
//                        rs.getString("author"),
//                        rs.getString("category"),
//                        rs.getInt("total_copies"),
//                        rs.getInt("available_copies"),
//                        rs.getString("image_path")
//                );
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}

//package com.buet.bibliotech.db;
//
//import com.buet.bibliotech.Books;
//import com.buet.bibliotech.IssueInfo;
//import com.buet.bibliotech.RequestInfo;
//import com.buet.bibliotech.BorrowedBookModel;
//import com.buet.bibliotech.MessageModel;
//import com.buet.bibliotech.member;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.scene.chart.PieChart;
//import javafx.scene.chart.XYChart;
//import java.sql.*;
//
//public class Database {
//
//    private static final String DB_URL = "jdbc:sqlite:bibliotech.db";
//    private static Database handler = null;
//    private static Connection connection = null;
//
//    private Database() { createTables(); }
//
//    public static Database getInstance() {
//        if (handler == null) handler = new Database();
//        return handler;
//    }
//
//    public static Connection getConnection() {
//        try {
//            if (connection == null || connection.isClosed())
//                connection = DriverManager.getConnection(DB_URL);
//        } catch (SQLException e) { e.printStackTrace(); }
//        return connection;
//    }
//
//    private void createTables() {
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
//            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL);");
//            stmt.execute("CREATE TABLE IF NOT EXISTS members (id TEXT PRIMARY KEY, name TEXT NOT NULL, department TEXT, batch TEXT, email TEXT, password TEXT DEFAULT '1234');");
//            stmt.execute("CREATE TABLE IF NOT EXISTS books (bookid TEXT PRIMARY KEY, bookname TEXT NOT NULL, author TEXT, category TEXT, total_copies INTEGER DEFAULT 1, available_copies INTEGER DEFAULT 1, image_path TEXT);");
//            stmt.execute("CREATE TABLE IF NOT EXISTS issue (issueID INTEGER PRIMARY KEY AUTOINCREMENT, bookID TEXT, memberID TEXT, issueTime TEXT, status TEXT DEFAULT 'Borrowed', FOREIGN KEY (bookID) REFERENCES books(bookid), FOREIGN KEY (memberID) REFERENCES members(id));");
//            stmt.execute("CREATE TABLE IF NOT EXISTS requests (bookID TEXT, memberID TEXT);");
//            stmt.execute("CREATE TABLE IF NOT EXISTS messages (messageID INTEGER PRIMARY KEY AUTOINCREMENT, senderID TEXT, receiverID TEXT, messageText TEXT, timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");
//
//            try { stmt.execute("ALTER TABLE books ADD COLUMN total_copies INTEGER DEFAULT 1"); } catch (SQLException e) {}
//            try { stmt.execute("ALTER TABLE books ADD COLUMN available_copies INTEGER DEFAULT 1"); } catch (SQLException e) {}
//            try { stmt.execute("ALTER TABLE issue ADD COLUMN status TEXT DEFAULT 'Borrowed'"); } catch (SQLException e) {}
//            try { stmt.execute("ALTER TABLE books ADD COLUMN image_path TEXT"); } catch (SQLException e) {}
//
//            stmt.execute("INSERT OR IGNORE INTO users (username, password) VALUES ('admin', '1234')");
//        } catch (SQLException e) { System.err.println("Error creating tables: " + e.getMessage()); }
//    }
//
//    public boolean sendMessage(String senderID, String receiverID, String text) {
//        String query = "INSERT INTO messages (senderID, receiverID, messageText) VALUES (?, ?, ?)";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, senderID);
//            pstmt.setString(2, receiverID);
//            pstmt.setString(3, text);
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public ObservableList<MessageModel> getMessagesForUser(String userID) {
//        ObservableList<MessageModel> list = FXCollections.observableArrayList();
//        String query = "SELECT * FROM messages WHERE senderID = ? OR receiverID = ? ORDER BY timestamp DESC";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, userID);
//            pstmt.setString(2, userID);
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                list.add(new MessageModel(
//                        rs.getInt("messageID"),
//                        rs.getString("senderID"),
//                        rs.getString("receiverID"),
//                        rs.getString("messageText"),
//                        rs.getString("timestamp")
//                ));
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public String getLoginRole(String username, String password) {
//        String adminQuery = "SELECT * FROM users WHERE LOWER(username) = LOWER(?) AND password = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(adminQuery)) {
//            pstmt.setString(1, username); pstmt.setString(2, password);
//            if (pstmt.executeQuery().next()) return "ADMIN";
//        } catch (SQLException e) { e.printStackTrace(); }
//        String memberQuery = "SELECT * FROM members WHERE LOWER(name) = LOWER(?) AND id = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(memberQuery)) {
//            pstmt.setString(1, username); pstmt.setString(2, password);
//            if (pstmt.executeQuery().next()) return "MEMBER";
//        } catch (SQLException e) { e.printStackTrace(); }
//        return "FAILED";
//    }
//
//    public boolean validateMemberLogin(String name, String id) {
//        String query = "SELECT * FROM members WHERE name = ? AND id = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, name); pstmt.setString(2, id);
//            return pstmt.executeQuery().next();
//        } catch (SQLException e) { return false; }
//    }
//
//    public boolean addbook(String bookid, String bookname, String author, String category, int copies, String imagePath) {
//        String query = "INSERT INTO books (bookid, bookname, author, category, total_copies, available_copies, image_path) VALUES(?,?,?,?,?,?,?)";
//        try (Connection conn = getConnection(); PreparedStatement psmt = conn.prepareStatement(query)) {
//            psmt.setString(1, bookid); psmt.setString(2, bookname); psmt.setString(3, author); psmt.setString(4, category);
//            psmt.setInt(5, copies); psmt.setInt(6, copies); psmt.setString(7, imagePath);
//            return psmt.executeUpdate() > 0;
//        } catch (SQLException e) { return false; }
//    }
//
//    public boolean issueBook(String bID, String mID, String date) {
//        String checkStock = "SELECT available_copies FROM books WHERE bookid = ?";
//        String updateStock = "UPDATE books SET available_copies = available_copies - 1 WHERE bookid = ?";
//        String insertIssue = "INSERT INTO issue (bookID, memberID, issueTime) VALUES(?,?,?)";
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false);
//            try (PreparedStatement cStmt = conn.prepareStatement(checkStock);
//                 PreparedStatement uStmt = conn.prepareStatement(updateStock);
//                 PreparedStatement iStmt = conn.prepareStatement(insertIssue)) {
//                cStmt.setString(1, bID);
//                ResultSet rs = cStmt.executeQuery();
//                if (rs.next() && rs.getInt("available_copies") > 0) {
//                    uStmt.setString(1, bID); uStmt.executeUpdate();
//                    iStmt.setString(1, bID); iStmt.setString(2, mID); iStmt.setString(3, date);
//                    iStmt.executeUpdate(); conn.commit(); return true;
//                }
//            } catch (SQLException e) { conn.rollback(); }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return false;
//    }
//    public ObservableList<member> getMembers() {
//        ObservableList<member> list = FXCollections.observableArrayList();
//        String sql = "SELECT * FROM members";
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                list.add(new member(
//                        rs.getString("id"),
//                        rs.getString("name"),
//                        rs.getString("department"),
//                        rs.getString("batch"),
//                        rs.getString("email")
//                ));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    public ObservableList<MessageModel> getChatBetween(String id1, String id2) {
//        ObservableList<MessageModel> list = FXCollections.observableArrayList();
//        String query = "SELECT * FROM messages WHERE (senderID = ? AND receiverID = ?) OR (senderID = ? AND receiverID = ?) ORDER BY timestamp ASC";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, id1);
//            pstmt.setString(2, id2);
//            pstmt.setString(3, id2);
//            pstmt.setString(4, id1);
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                list.add(new MessageModel(
//                        rs.getInt("messageID"),
//                        rs.getString("senderID"),
//                        rs.getString("receiverID"),
//                        rs.getString("messageText"),
//                        rs.getString("timestamp")
//                ));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//    // মেম্বার অ্যাড করার সঠিক মেথড (৫টি প্যারামিটার)
//    public boolean addmember(String id, String name, String department, String batch, String email) {
//        String query = "INSERT INTO members (id, name, department, batch, email) VALUES(?,?,?,?,?)";
//        try (Connection conn = getConnection(); PreparedStatement psmt = conn.prepareStatement(query)) {
//            psmt.setString(1, id);
//            psmt.setString(2, name);
//            psmt.setString(3, department);
//            psmt.setString(4, batch);
//            psmt.setString(5, email);
//            psmt.executeUpdate();
//            return true;
//        } catch (SQLException e) {
//            System.err.println("Error adding member: " + e.getMessage());
//            return false;
//        }
//    }
//    public boolean isBookIdExists(String bookid) {
//        String query = "SELECT COUNT(*) FROM books WHERE bookid = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, bookid);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                // যদি কাউন্ট ০ এর চেয়ে বেশি হয়, তার মানে আইডিটি অলরেডি আছে
//                return rs.getInt(1) > 0;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//    public boolean approveReturn(int issueID, String bookID) {
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false);
//            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM issue WHERE issueID = ?");
//                 PreparedStatement ps2 = conn.prepareStatement("UPDATE books SET available_copies = available_copies + 1 WHERE bookid = ?")) {
//                ps1.setInt(1, issueID); ps1.executeUpdate();
//                ps2.setString(1, bookID); ps2.executeUpdate();
//                conn.commit(); return true;
//            } catch (SQLException e) { conn.rollback(); return false; }
//        } catch (SQLException e) { return false; }
//    }
//
//    public boolean rejectReturn(int issueID) {
//        String query = "UPDATE issue SET status = 'Borrowed' WHERE issueID = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setInt(1, issueID); return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) { return false; }
//    }
//
//    public boolean requestBookReturn(String bookID) {
//        String query = "UPDATE issue SET status = 'Pending Return' WHERE bookID = ? AND status = 'Borrowed'";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, bookID); return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) { return false; }
//    }
//
//    public ObservableList<Books> getAvailableBooks() {
//        ObservableList<Books> list = FXCollections.observableArrayList();
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {
//            while (rs.next()) {
//                list.add(new Books(rs.getString("bookid"), rs.getString("bookname"), rs.getString("author"), rs.getString("category"), rs.getInt("total_copies"), rs.getInt("available_copies"), rs.getString("image_path")));
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public ObservableList<IssueInfo> getPendingReturns() {
//        ObservableList<IssueInfo> list = FXCollections.observableArrayList();
//        String query = "SELECT issue.issueID, issue.bookID, books.bookname, issue.memberID, members.name, issue.issueTime FROM issue JOIN books ON issue.bookID = books.bookid JOIN members ON issue.memberID = members.id";
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) {
//                list.add(new IssueInfo(rs.getInt("issueID"), rs.getString("bookID"), rs.getString("bookname"), rs.getString("memberID"), rs.getString("name"), rs.getString("issueTime")));
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public ObservableList<IssueInfo> getReturnRequests() {
//        ObservableList<IssueInfo> list = FXCollections.observableArrayList();
//        String query = "SELECT issue.issueID, issue.bookID, books.bookname, issue.memberID, members.name, issue.issueTime FROM issue JOIN books ON issue.bookID = books.bookid JOIN members ON issue.memberID = members.id WHERE issue.status = 'Pending Return'";
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) {
//                list.add(new IssueInfo(rs.getInt("issueID"), rs.getString("bookID"), rs.getString("bookname"), rs.getString("memberID"), rs.getString("name"), rs.getString("issueTime")));
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public boolean approveRequest(String bookID, String memberID) {
//        try (Connection conn = getConnection()) {
//            conn.setAutoCommit(false);
//            try (PreparedStatement uStmt = conn.prepareStatement("UPDATE books SET available_copies = available_copies - 1 WHERE bookid = ?");
//                 PreparedStatement dStmt = conn.prepareStatement("DELETE FROM requests WHERE bookID = ? AND memberID = ?");
//                 PreparedStatement iStmt = conn.prepareStatement("INSERT INTO issue (bookID, memberID, issueTime) VALUES (?, ?, CURRENT_TIMESTAMP)")) {
//                uStmt.setString(1, bookID); uStmt.executeUpdate();
//                dStmt.setString(1, bookID); dStmt.setString(2, memberID); dStmt.executeUpdate();
//                iStmt.setString(1, bookID); iStmt.setString(2, memberID); iStmt.executeUpdate();
//                conn.commit(); return true;
//            } catch (SQLException e) { conn.rollback(); return false; }
//        } catch (SQLException e) { return false; }
//    }
//
//    public ObservableList<BorrowedBookModel> getBorrowedBooksByMember(String memberId) {
//        ObservableList<BorrowedBookModel> list = FXCollections.observableArrayList();
//        String query = "SELECT books.bookname, books.author, books.bookid FROM issue JOIN books ON issue.bookID = books.bookid WHERE issue.memberID = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, memberId);
//            ResultSet rs = pstmt.executeQuery();
//            int serial = 1;
//            while (rs.next()) {
//                list.add(new BorrowedBookModel(String.valueOf(serial++), rs.getString("bookname"), rs.getString("bookid"), rs.getString("author")));
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    public int getTotalBooksCount() { return getCount("SELECT SUM(total_copies) FROM books"); }
//    public int getTotalMembersCount() { return getCount("SELECT COUNT(*) FROM members"); }
//    public int getTotalIssuedBooksCount() { return getCount("SELECT COUNT(*) FROM issue"); }
//    public int getPendingRequestsCount() { return getCount("SELECT COUNT(*) FROM issue WHERE status = 'Pending Return'"); }
//
//    private int getCount(String sql) {
//        try (Connection conn = getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
//            if (rs.next()) return rs.getInt(1);
//        } catch (SQLException e) { e.printStackTrace(); }
//        return 0;
//    }
//
//    public boolean isMemberIdExists(String id) {
//        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM members WHERE id = ?")) {
//            ps.setString(1, id);
//            ResultSet rs = ps.executeQuery();
//            return rs.next() && rs.getInt(1) > 0;
//        } catch (SQLException e) { return false; }
//    }
//
//    public boolean updateMemberProfileFull(String oldId, String newId, String name, String email, String dept, String batch) {
//        String query = "UPDATE members SET id=?, name=?, email=?, department=?, batch=? WHERE id=?";
//        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
//            ps.setString(1, newId);
//            ps.setString(2, name);
//            ps.setString(3, email);
//            ps.setString(4, dept);
//            ps.setString(5, batch);
//            ps.setString(6, oldId);
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { return false; }
//    }
//
//    public member getMemberById(String memberId) {
//        String query = "SELECT * FROM members WHERE id = ?";
//        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, memberId);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) return new member(rs.getString("id"), rs.getString("name"), rs.getString("department"), rs.getString("batch"), rs.getString("email"));
//        } catch (SQLException e) { e.printStackTrace(); }
//        return null;
//    }
//
//    public ObservableList<PieChart.Data> getBookCategoryData() {
//        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT category, SUM(total_copies) FROM books GROUP BY category")) {
//            while (rs.next()) data.add(new PieChart.Data(rs.getString(1), rs.getInt(2)));
//        } catch (SQLException e) { e.printStackTrace(); }
//        return data;
//    }
//
//    public XYChart.Series<String, Number> getTopBorrowersData() {
//        XYChart.Series<String, Number> series = new XYChart.Series<>();
//        series.setName("Books Borrowed");
//        String query = "SELECT members.name, COUNT(issue.issueID) as count FROM issue JOIN members ON issue.memberID = members.id GROUP BY members.id ORDER BY count DESC LIMIT 5";
//        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) series.getData().add(new XYChart.Data<>(rs.getString(1), rs.getInt(2)));
//        } catch (SQLException e) { e.printStackTrace(); }
//        return series;
//    }
//}

package com.buet.bibliotech.db;

import com.buet.bibliotech.Books;
import com.buet.bibliotech.IssueInfo;
import com.buet.bibliotech.RequestInfo;
import com.buet.bibliotech.BorrowedBookModel;
import com.buet.bibliotech.MessageModel;
import com.buet.bibliotech.member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import java.sql.*;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:bibliotech.db";
    private static Database handler = null;
    private static Connection connection = null;

    private Database() { createTables(); }

    public static Database getInstance() {
        if (handler == null) handler = new Database();
        return handler;
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed())
                connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) { e.printStackTrace(); }
        return connection;
    }

    private void createTables() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            //6 tables
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS members (id TEXT PRIMARY KEY, name TEXT NOT NULL, department TEXT, batch TEXT, email TEXT, password TEXT DEFAULT '1234');");
            stmt.execute("CREATE TABLE IF NOT EXISTS books (bookid TEXT PRIMARY KEY, bookname TEXT NOT NULL, author TEXT, category TEXT, total_copies INTEGER DEFAULT 1, available_copies INTEGER DEFAULT 1, image_path TEXT);");
            stmt.execute("CREATE TABLE IF NOT EXISTS issue (issueID INTEGER PRIMARY KEY AUTOINCREMENT, bookID TEXT, memberID TEXT, issueTime TEXT, status TEXT DEFAULT 'Borrowed', FOREIGN KEY (bookID) REFERENCES books(bookid), FOREIGN KEY (memberID) REFERENCES members(id));");
            stmt.execute("CREATE TABLE IF NOT EXISTS requests (bookID TEXT, memberID TEXT);");
            stmt.execute("CREATE TABLE IF NOT EXISTS messages (messageID INTEGER PRIMARY KEY AUTOINCREMENT, senderID TEXT, receiverID TEXT, messageText TEXT, timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");


            try { stmt.execute("ALTER TABLE books ADD COLUMN total_copies INTEGER DEFAULT 1"); } catch (SQLException e) {}
            try { stmt.execute("ALTER TABLE books ADD COLUMN available_copies INTEGER DEFAULT 1"); } catch (SQLException e) {}
            try { stmt.execute("ALTER TABLE books ADD COLUMN image_path TEXT"); } catch (SQLException e) {}
            try { stmt.execute("ALTER TABLE issue ADD COLUMN status TEXT DEFAULT 'Borrowed'"); } catch (SQLException e) {}

            stmt.execute("INSERT OR IGNORE INTO users (username, password) VALUES ('admin', '1234')");
        }
        catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage()); }
    }


    public String getLoginRole(String username, String password) {
        String adminQuery = "SELECT * FROM users WHERE LOWER(username) = LOWER(?) AND password = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(adminQuery)) {
            pstmt.setString(1, username); pstmt.setString(2, password);
            if (pstmt.executeQuery().next()) return "ADMIN";
        } catch (SQLException e) { e.printStackTrace(); }

        String memberQuery = "SELECT * FROM members WHERE LOWER(name) = LOWER(?) AND id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(memberQuery)) {
            pstmt.setString(1, username); pstmt.setString(2, password);
            if (pstmt.executeQuery().next())
                return "MEMBER";
        } catch (SQLException e) { e.printStackTrace(); }
        return "FAILED";
    }

    public boolean validateMemberLogin(String name, String id) {
        String query = "SELECT * FROM members WHERE name = ? AND id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name); pstmt.setString(2, id);
            return pstmt.executeQuery().next();
        } catch (SQLException e) { return false; }
    }


    public boolean addmember(String id, String name, String department, String batch, String email) {
        String query = "INSERT INTO members (id, name, department, batch, email) VALUES(?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement psmt = conn.prepareStatement(query)) {
            psmt.setString(1, id);
            psmt.setString(2, name);
            psmt.setString(3, department);
            psmt.setString(4, batch);
            psmt.setString(5, email);
            return psmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public ObservableList<member> getMembers() {
        ObservableList<member> list = FXCollections.observableArrayList();
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM members")) {
            while (rs.next()) {
                list.add(new member(rs.getString("id"), rs.getString("name"), rs.getString("department"), rs.getString("batch"), rs.getString("email")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean deleteMember(String memberId) {
        String findIssuedBooks = "SELECT bookID FROM issue WHERE memberID = ?";

        String incrementStock = "UPDATE books SET available_copies = available_copies + 1 WHERE bookid = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); //multiple queries handling
            try (PreparedStatement psFind = conn.prepareStatement(findIssuedBooks);
                 PreparedStatement psStock = conn.prepareStatement(incrementStock);
                 PreparedStatement psDelIssue = conn.prepareStatement("DELETE FROM issue WHERE memberID = ?");
                 PreparedStatement psDelMem = conn.prepareStatement("DELETE FROM members WHERE id = ?")) {

                psFind.setString(1, memberId);
                ResultSet rs = psFind.executeQuery();
                while (rs.next()) {
                    psStock.setString(1, rs.getString("bookID"));
                    psStock.executeUpdate();
                }
                psDelIssue.setString(1, memberId);
                psDelIssue.executeUpdate();

                psDelMem.setString(1, memberId); int affected = psDelMem.executeUpdate();
                conn.commit();
                return affected > 0;
            }
            catch (SQLException e) { conn.rollback(); return false; }
        } catch (SQLException e) { return false; }
    }

    public member getMemberById(String memberId) {
        String query = "SELECT * FROM members WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                return new member(rs.getString("id"), rs.getString("name"), rs.getString("department"), rs.getString("batch"), rs.getString("email"));
        } catch (SQLException e) {
            e.printStackTrace(); }
        return null;
    }

    public boolean isMemberIdExists(String id) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM members WHERE id = ?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean updateMemberProfileFull(String oldId, String newId, String name, String email, String dept, String batch) {
        String query = "UPDATE members SET id=?, name=?, email=?, department=?, batch=? WHERE id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newId); ps.setString(2, name); ps.setString(3, email);
            ps.setString(4, dept); ps.setString(5, batch); ps.setString(6, oldId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }


    public boolean addbook(String bookid, String bookname, String author, String category, int copies, String imagePath) {
        String query = "INSERT INTO books (bookid, bookname, author, category, total_copies, available_copies, image_path) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement psmt = conn.prepareStatement(query)) {
            psmt.setString(1, bookid); psmt.setString(2, bookname); psmt.setString(3, author);
            psmt.setString(4, category); psmt.setInt(5, copies); psmt.setInt(6, copies);
            psmt.setString(7, imagePath);
            return psmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public ObservableList<Books> getAvailableBooks() {
        ObservableList<Books> list = FXCollections.observableArrayList();
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM books")) {
            while (rs.next()) {
                list.add(new Books(rs.getString("bookid"), rs.getString("bookname"), rs.getString("author"), rs.getString("category"), rs.getInt("total_copies"), rs.getInt("available_copies"), rs.getString("image_path")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean deleteBook(String bookId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE bookid = ?")) {
            ps.setString(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false; }
    }

    public boolean isBookIdExists(String bookid) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM books WHERE bookid = ?"))
        {
            ps.setString(1, bookid);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { return false; }
    }

    public Books getBookById(String bookId) {
        String query = "SELECT * FROM books WHERE bookid = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                return new Books(rs.getString("bookid"), rs.getString("bookname"), rs.getString("author"), rs.getString("category"), rs.getInt("total_copies"), rs.getInt("available_copies"), rs.getString("image_path"));
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }


    public boolean issueBook(String bID, String mID, String date) {
        String checkStock = "SELECT available_copies FROM books WHERE bookid = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement cSt = conn.prepareStatement(checkStock)) {
                cSt.setString(1, bID);
                ResultSet rs = cSt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    try (PreparedStatement uSt = conn.prepareStatement("UPDATE books SET available_copies = available_copies - 1 WHERE bookid = ?");
                         PreparedStatement iSt = conn.prepareStatement("INSERT INTO issue (bookID, memberID, issueTime, status) VALUES(?,?,?,'Borrowed')")) {
                        uSt.setString(1, bID);
                        uSt.executeUpdate();
                        iSt.setString(1, bID);
                        iSt.setString(2, mID);
                        iSt.setString(3, date);
                        iSt.executeUpdate();
                        conn.commit();
                        return true;
                    }
                }
            } catch (SQLException e) { conn.rollback(); }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean requestBookReturn(String bookID) {
        String query = "UPDATE issue SET status = 'Pending Return' WHERE bookID = ? AND status = 'Borrowed'";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bookID); return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean approveReturn(int issueID, String bookID) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM issue WHERE issueID = ?");
                 PreparedStatement ps2 = conn.prepareStatement("UPDATE books SET available_copies = available_copies + 1 WHERE bookid = ?")) {
                ps1.setInt(1, issueID);
                ps1.executeUpdate();
                ps2.setString(1, bookID);
                ps2.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                return false; }
        } catch (SQLException e) { return false; }
    }

    public boolean rejectReturn(int issueID) {
        String query = "UPDATE issue SET status = 'Borrowed' WHERE issueID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, issueID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }


    public boolean sendMessage(String senderID, String receiverID, String text) {
        String query = "INSERT INTO messages (senderID, receiverID, messageText) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, senderID);
            pstmt.setString(2, receiverID);
            pstmt.setString(3, text);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public ObservableList<MessageModel> getChatBetween(String id1, String id2) {
        ObservableList<MessageModel> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM messages WHERE (senderID = ? AND receiverID = ?) OR (senderID = ? AND receiverID = ?) ORDER BY timestamp ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, id1);
            pstmt.setString(2, id2);
            pstmt.setString(3, id2);
            pstmt.setString(4, id1);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new MessageModel(rs.getInt("messageID"), rs.getString("senderID"), rs.getString("receiverID"), rs.getString("messageText"), rs.getString("timestamp")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public ObservableList<MessageModel> getMessagesForUser(String userID) {
        ObservableList<MessageModel> list = FXCollections.observableArrayList();
        String query = "SELECT * FROM messages WHERE senderID = ? OR receiverID = ? ORDER BY timestamp DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userID); pstmt.setString(2, userID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new MessageModel(rs.getInt("messageID"), rs.getString("senderID"), rs.getString("receiverID"), rs.getString("messageText"), rs.getString("timestamp")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }


    public ObservableList<IssueInfo> getPendingReturns() {
        ObservableList<IssueInfo> list = FXCollections.observableArrayList();

        String query = "SELECT issue.issueID, issue.bookID, books.bookname, issue.memberID, members.name, issue.issueTime FROM issue JOIN books ON issue.bookID = books.bookid JOIN members ON issue.memberID = members.id";
        //joins issue , books , members table
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next())
                list.add(new IssueInfo(rs.getInt("issueID"), rs.getString("bookID"), rs.getString("bookname"), rs.getString("memberID"), rs.getString("name"), rs.getString("issueTime")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public ObservableList<IssueInfo> getReturnRequests() {
        ObservableList<IssueInfo> list = FXCollections.observableArrayList();
        String query = "SELECT issue.issueID, issue.bookID, books.bookname, issue.memberID, members.name, issue.issueTime FROM issue JOIN books ON issue.bookID = books.bookid JOIN members ON issue.memberID = members.id WHERE issue.status = 'Pending Return'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next())
                list.add(new IssueInfo(rs.getInt("issueID"), rs.getString("bookID"), rs.getString("bookname"), rs.getString("memberID"), rs.getString("name"), rs.getString("issueTime")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public ObservableList<BorrowedBookModel> getBorrowedBooksByMember(String memberId) {
        ObservableList<BorrowedBookModel> list = FXCollections.observableArrayList();

        String query = "SELECT books.bookname, books.author, books.bookid FROM issue JOIN books ON issue.bookID = books.bookid WHERE issue.memberID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, memberId);

            ResultSet rs = pstmt.executeQuery();

            int serial = 1;

            while (rs.next())
            {
                list.add(new BorrowedBookModel(String.valueOf(serial++),
                        rs.getString("bookname"),
                        rs.getString("bookid"),
                        rs.getString("author")));
            }
        }
        catch (SQLException e) { e.printStackTrace(); }
        return list;
    }


    private int getCount(String sql) {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace(); }
        return 0;
    }

    public int getTotalBooksCount() {
        return getCount("SELECT SUM(total_copies) FROM books");
    }
    public int getTotalMembersCount() {
        return getCount("SELECT COUNT(*) FROM members");
    }
    public int getTotalIssuedBooksCount() {
        return getCount("SELECT COUNT(*) FROM issue");
    }
    public int getPendingReturnsCount() {
        return getCount("SELECT COUNT(*) FROM issue WHERE status = 'Pending Return'"); }
    public int getAvailableBooksCount() {
        return getCount("SELECT SUM(available_copies) FROM books");
    }



    public ObservableList<PieChart.Data> getBookCategoryData() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();


        String query = "SELECT category, SUM(total_copies) FROM books GROUP BY category";

      //the query groups books by category and sums the total copies of each category

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {


            while (rs.next()) {
                String categoryName = rs.getString(1); // get Category name
                int totalCount = rs.getInt(2);         // get Sum of copies

               //adding data into pie

                PieChart.Data slice = new PieChart.Data(categoryName, totalCount);
                data.add(slice);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public ObservableList<RequestInfo> getPendingRequests() {
        ObservableList<RequestInfo> list = FXCollections.observableArrayList();

        String query = """
        SELECT r.bookID, b.bookname, r.memberID, m.name 
        FROM requests r 
        JOIN books b ON r.bookID = b.bookid 
        JOIN members m ON r.memberID = m.id
        """;
  // joins the requests table with books and members to get all pending requests
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {

                list.add(new RequestInfo(
                        rs.getString("bookID"),

                        rs.getString("bookname"),

                        rs.getString("memberID"),

                        rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean approveRequest(String bookID, String memberID) {
        String updateStock = "UPDATE books SET available_copies = available_copies - 1 WHERE bookid = ?";
        String deleteReq = "DELETE FROM requests WHERE bookID = ? AND memberID = ?";
        String insertIssue = "INSERT INTO issue (bookID, memberID, issueTime, status) VALUES (?, ?, CURRENT_TIMESTAMP, 'Borrowed')";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement uStmt = conn.prepareStatement(updateStock);
                 PreparedStatement dStmt = conn.prepareStatement(deleteReq);
                 PreparedStatement iStmt = conn.prepareStatement(insertIssue)) {

                //update available books
                uStmt.setString(1, bookID);
                uStmt.executeUpdate();

                //delete his req
                dStmt.setString(1, bookID);
                dStmt.setString(2, memberID);
                dStmt.executeUpdate();

                //add issue
                iStmt.setString(1, bookID);
                iStmt.setString(2, memberID);
                iStmt.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean placeRequest(String bookID, String memberID) {
        String query = "INSERT INTO requests (bookID, memberID) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bookID);
            pstmt.setString(2, memberID);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {

            return false;
        }
    }

    public XYChart.Series<String, Number> getTopBorrowersData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Books Borrowed");
        String query = "SELECT members.name, COUNT(issue.issueID) as count FROM issue JOIN members ON issue.memberID = members.id GROUP BY members.id ORDER BY count DESC LIMIT 5";
         //joining issue and member table
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String name = rs.getString(1);   // x axis value
                int count = rs.getInt(2);        // y er value

                //create point and add
                series.getData().add(new XYChart.Data<>(name, count));
            }
        }
        catch (SQLException e) { e.printStackTrace(); }
        return series;
    }
}