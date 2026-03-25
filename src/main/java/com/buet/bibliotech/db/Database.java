package com.buet.bibliotech.db;

import com.buet.bibliotech.Books;
import com.buet.bibliotech.IssueInfo;
import com.buet.bibliotech.RequestInfo;
import com.buet.bibliotech.member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:bibliotech.db";
    private static Database handler = null;
    private static Connection connection = null;

    private Database() {
        createTables();
    }

    public static Database getInstance() {
        if (handler == null) {
            handler = new Database();
        }
        return handler;
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void createTables() {
        String usersTable = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL);";
        String membersTable = "CREATE TABLE IF NOT EXISTS members (id TEXT PRIMARY KEY, name TEXT NOT NULL, department TEXT, batch TEXT, email TEXT, password TEXT DEFAULT '1234');";
        String booksTable = "CREATE TABLE IF NOT EXISTS books (bookid TEXT PRIMARY KEY, bookname TEXT NOT NULL, author TEXT, category TEXT);";
        String issueTable = "CREATE TABLE IF NOT EXISTS issue (bookID TEXT PRIMARY KEY, memberID TEXT, issueTime TEXT, FOREIGN KEY (bookID) REFERENCES books(bookid), FOREIGN KEY (memberID) REFERENCES members(id));";


        String requestTable = "CREATE TABLE IF NOT EXISTS requests (bookID TEXT PRIMARY KEY, memberID TEXT, requestTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (bookID) REFERENCES books(bookid), FOREIGN KEY (memberID) REFERENCES members(id));";

        String adminUser = "INSERT OR IGNORE INTO users (username, password) VALUES ('admin', '1234')";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(membersTable);
            stmt.execute(booksTable);
            stmt.execute(issueTable);
            stmt.execute(requestTable);
            stmt.execute(adminUser);
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    public String getLoginRole(String username, String password) {
        String adminQuery = "SELECT * FROM users WHERE LOWER(username) = LOWER(?) AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(adminQuery)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            if (pstmt.executeQuery().next()) return "ADMIN";
        } catch (SQLException e) { e.printStackTrace(); }

        String memberQuery = "SELECT * FROM members WHERE LOWER(name) = LOWER(?) AND id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(memberQuery)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            if (pstmt.executeQuery().next()) return "MEMBER";
        } catch (SQLException e) { e.printStackTrace(); }

        return "FAILED";
    }

    public boolean addmember(String id, String name, String department, String Batch, String Email) {
        String query = "INSERT INTO members (id, name, department, batch, email) VALUES(?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement psmt = conn.prepareStatement(query)) {
            psmt.setString(1, id);
            psmt.setString(2, name);
            psmt.setString(3, department);
            psmt.setString(4, Batch);
            psmt.setString(5, Email);
            psmt.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public boolean addbook(String bookid, String bookname, String author, String category) {
        String query = "INSERT INTO books (bookid, bookname, author, category) VALUES(?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement psmt = conn.prepareStatement(query)) {
            psmt.setString(1, bookid);
            psmt.setString(2, bookname);
            psmt.setString(3, author);
            psmt.setString(4, category);
            psmt.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public ObservableList<member> getMembers() {
        ObservableList<member> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM members";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new member(rs.getString("id"), rs.getString("name"), rs.getString("department"), rs.getString("batch"), rs.getString("email")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public ObservableList<Books> getAvailableBooks() {
        ObservableList<Books> list = FXCollections.observableArrayList();

        String query = "SELECT * FROM books WHERE bookid NOT IN (SELECT bookID FROM issue) AND bookid NOT IN (SELECT bookID FROM requests)";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Books(rs.getString("bookid"), rs.getString("bookname"), rs.getString("author"), rs.getString("category")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean issueBook(String bID, String mID, String date) {
        String query = "INSERT INTO issue (bookID, memberID, issueTime) VALUES(?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement psmt = conn.prepareStatement(query)) {
            psmt.setString(1, bID);
            psmt.setString(2, mID);
            psmt.setString(3, date);
            psmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public ObservableList<IssueInfo> getPendingReturns() {
        ObservableList<IssueInfo> list = FXCollections.observableArrayList();
        String query = "SELECT issue.bookID, books.bookname, issue.memberID, members.name, issue.issueTime FROM issue JOIN books ON issue.bookID = books.bookid JOIN members ON issue.memberID = members.id";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new IssueInfo(rs.getString("bookID"), rs.getString("bookname"), rs.getString("memberID"), rs.getString("name"), rs.getString("issueTime")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public member getMemberById(String memberId) {
        String query = "SELECT * FROM members WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new member(rs.getString("id"), rs.getString("name"), rs.getString("department"), rs.getString("batch"), rs.getString("email"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // --- REQUEST SYSTEM ---

    public boolean placeRequest(String bookID, String memberID) {
        String query = "INSERT INTO requests (bookID, memberID) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bookID);
            pstmt.setString(2, memberID);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public ObservableList<RequestInfo> getPendingRequests() {
        ObservableList<RequestInfo> list = FXCollections.observableArrayList();
        String query = "SELECT r.bookID, b.bookname, r.memberID, m.name FROM requests r JOIN books b ON r.bookID = b.bookid JOIN members m ON r.memberID = m.id";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new RequestInfo(rs.getString("bookID"), rs.getString("bookname"), rs.getString("memberID"), rs.getString("name")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean approveRequest(String bookID, String memberID) {
        String deleteReq = "DELETE FROM requests WHERE bookID = ?";
        String insertIssue = "INSERT INTO issue (bookID, memberID, issueTime) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement dStmt = conn.prepareStatement(deleteReq);
                 PreparedStatement iStmt = conn.prepareStatement(insertIssue)) {
                dStmt.setString(1, bookID);
                dStmt.executeUpdate();
                iStmt.setString(1, bookID);
                iStmt.setString(2, memberID);
                iStmt.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) { return false; }
    }

    public javafx.collections.ObservableList<com.buet.bibliotech.BorrowedBookModel> getBorrowedBooksByMember(String memberId) {
        javafx.collections.ObservableList<com.buet.bibliotech.BorrowedBookModel> list = javafx.collections.FXCollections.observableArrayList();
        // query joins issue and books tables to get the name based on the ID in issue
        String query = "SELECT books.bookname, books.author, books.bookid FROM issue JOIN books ON issue.bookID = books.bookid WHERE issue.memberID = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            int serial = 1;
            while (rs.next()) {
                String bookID=rs.getString("bookid");
                String bookname=rs.getString("bookname");
                String author=rs.getString("author");
                serial=serial+1;
                list.add(new com.buet.bibliotech.BorrowedBookModel(
                        String.valueOf(serial),
                        bookname,
                        bookID,
                        author
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteMember(String memberId) {
        String deleteIssueSql = "DELETE FROM issue WHERE memberID = ?";
        String deleteMemberSql = "DELETE FROM members WHERE id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // start transaction

            try (PreparedStatement psmtissue = conn.prepareStatement(deleteIssueSql);
                 PreparedStatement psmtmember = conn.prepareStatement(deleteMemberSql)) {

                // 1. Delete from issue table (This "returns" the books to available)
                psmtissue.setString(1, memberId);
                psmtissue.executeUpdate();

                // 2. Delete the member
                psmtmember.setString(1, memberId);
                int affectedRows = psmtmember.executeUpdate();

                conn.commit(); // Save changes
                return affectedRows > 0;

            } catch (SQLException e) {
                conn.rollback(); // Undo changes if there's an error
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public int getTotalBooksCount(){
        String query="SELECT COUNT(*) FROM books";
        try(Connection conn=getConnection();Statement stmt=conn.createStatement(); ResultSet rs=stmt.executeQuery(query)) {
            if(rs.next()) {
               return  rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getAvailableBooksCount(){
        String query= "SELECT COUNT(*) FROM books " +
                "WHERE bookid NOT IN (SELECT bookID FROM issue) " +
                "AND bookid NOT IN (SELECT bookID FROM requests)";
        try(Connection conn=getConnection();Statement stmt=conn.createStatement(); ResultSet rs=stmt.executeQuery(query)) {
            if(rs.next()) {
                return  rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalMembersCount(){
        String query="SELECT COUNT(*) FROM members";
        try(Connection conn=getConnection();Statement stmt=conn.createStatement(); ResultSet rs=stmt.executeQuery(query)) {
            if(rs.next()) {
                return  rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalIssuedBooksCount(){
        String query="SELECT COUNT(*) FROM issue";
        try(Connection conn=getConnection();Statement stmt=conn.createStatement(); ResultSet rs=stmt.executeQuery(query)) {
            if(rs.next()) {
                return  rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPendingRequestsCount(){
        String query="SELECT COUNT(*) FROM requests";
        try(Connection conn=getConnection();Statement stmt=conn.createStatement(); ResultSet rs=stmt.executeQuery(query)) {
            if(rs.next()) {
                return  rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isBookIdExists(String bookid) {
        String query = "SELECT COUNT(*) FROM books WHERE bookid = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query))
        {
            pstmt.setString(1, bookid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isMemberIdExists(String memberid) {
        String query="SELECT COUNT(*) FROM members WHERE memberid =? ";
        try(Connection conn=getConnection() ;
        PreparedStatement psmt=conn.prepareStatement(query)){
            psmt.setString(1,memberid);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    }