package com.buet.bibliotech.db;

import com.buet.bibliotech.Books;
import com.buet.bibliotech.IssueInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.plaf.nimbus.State;
import java.sql.*;

public class Database {


    private static final String DB_URL = "jdbc:sqlite:bibliotech.db";

    private static Database handler = null;
    private static Connection connection = null;


    private Database() {

        createTables();
    }


    public static Database getInstance()
    {
        if (handler == null) {
            handler = new Database();
        }
        return handler;
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed())
            {
                connection = DriverManager.getConnection(DB_URL);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return connection;
    }


    private void createTables() {
        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL
                );
                """;
        String memberstable="""
                CREATE TABLE IF NOT EXISTS members (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    department TEXT,
                    batch TEXT,
                    email TEXT,
                  password TEXT DEFAULT '1234'
                );
            """;

        String bookstable= """
                CREATE TABLE IF NOT EXISTS books (
                 bookid TEXT PRIMARY KEY,
                 bookname TEXT NOT NULL,
                 author TEXT,
                 category TEXT
                 );
                
                """;

        String issueTable = """
                CREATE TABLE IF NOT EXISTS issue (
                    bookID TEXT PRIMARY KEY,
                    memberID TEXT,
                    issueTime TEXT,
                    FOREIGN KEY (bookID) REFERENCES books(bookid),
                    FOREIGN KEY (memberID) REFERENCES members(id)
                );
                """;
     String sql="INSERT OR IGNORE INTO users (username, password) VALUES ('admin', '1234')";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement())
        {

            stmt.execute(usersTable);
            stmt.execute(memberstable);
            stmt.execute(bookstable);
            stmt.execute(issueTable);
            stmt.execute(sql);

        }
        catch (SQLException e)
        {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    /* EDITED: Using LOWER() for case-insensitivity and better matching */
    public String getLoginRole(String username, String password) {
        // 1. Check Admin (users table)
        String adminQuery = "SELECT * FROM users WHERE LOWER(username) = LOWER(?) AND password = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(adminQuery)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            if (pstmt.executeQuery().next()) return "ADMIN";
        } catch (SQLException e) { e.printStackTrace(); }

        // 2. Check Member (Using name as username and id as password)

        String memberQuery = "SELECT * FROM members WHERE LOWER(name) = LOWER(?) AND id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(memberQuery)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            if (pstmt.executeQuery().next()) return "MEMBER";
        } catch (SQLException e) { e.printStackTrace(); }

        return "FAILED";
    }


    public boolean validateMemberLogin(String name, String id) {
        String query = "SELECT * FROM members WHERE name = ? AND id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if a match is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addmember(String id , String name , String department, String Batch, String Email){

        String query = "INSERT INTO members (id, name, department, batch, email) VALUES(?,?,?,?,?)";

        try(Connection conn = getConnection(); PreparedStatement psmt = conn.prepareStatement(query))
        {

            psmt.setString(1, id);
            psmt.setString(2, name);

            psmt.setString(3, department);

            psmt.setString(4, Batch); // This was previously slot 4 but 'batch' was missing in SQL string
            psmt.setString(5, Email); // This is now correctly slot 5

            psmt.executeUpdate();
            return true;
        }
        catch(SQLException e)
        {
            System.err.println("Error adding member: " + e.getMessage());
            return false;
        }
    }

    public boolean addbook(String bookid , String bookname , String author , String category) throws SQLException {
        String query = "INSERT INTO books (bookid, bookname, author,category) VALUES(?,?,?,?)";
        try(Connection conn=getConnection() ; PreparedStatement psmt=conn.prepareStatement(query)){
            psmt.setString(1,bookid);
            psmt.setString(2,bookname);
            psmt.setString(3,author);
            psmt.setString(4,category);
            psmt.executeUpdate();
            return true;
        }
        catch(SQLException e){
            System.err.println("Error adding book: " + e.getMessage());
            return false;
        }
    }




    public javafx.collections.ObservableList<com.buet.bibliotech.member> getMembers() {
        javafx.collections.ObservableList<com.buet.bibliotech.member> list = javafx.collections.FXCollections.observableArrayList();
        String sql = "SELECT * FROM members";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String departmentt = rs.getString("department");
                String batch = rs.getString("batch");
                String email = rs.getString("email");

                list.add(new com.buet.bibliotech.member(id, name, departmentt, batch, email));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public javafx.collections.ObservableList<com.buet.bibliotech.Books>getAvailableBooks(){
        javafx.collections.ObservableList<com.buet.bibliotech.Books> list = javafx.collections.FXCollections.observableArrayList();
        String query="SELECT * FROM books WHERE bookid NOT IN (SELECT bookID FROM issue)";
        try(Connection conn = getConnection(); Statement stmt=conn.createStatement() ;ResultSet rs= stmt.executeQuery(query)){
            while(rs.next()){
                String bookid=rs.getString("bookid");
                String bookname=rs.getString("bookname");
                String author=rs.getString("author");
                String category=rs.getString("category");

                list.add(new com.buet.bibliotech.Books(bookid, bookname, author, category));
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
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
        }
        catch (SQLException e)
        {
            return false;
        }
    }

    public ObservableList<IssueInfo> getPendingReturns() {
        ObservableList<com.buet.bibliotech.IssueInfo> list = FXCollections.observableArrayList();
        String query = """
                SELECT issue.bookID, books.bookname, issue.memberID, members.name, issue.issueTime 
                FROM issue 
                JOIN books ON issue.bookID = books.bookid 
                JOIN members ON issue.memberID = members.id
                """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new com.buet.bibliotech.IssueInfo(
                        rs.getString("bookID"), rs.getString("bookname"),
                        rs.getString("memberID"), rs.getString("name"), rs.getString("issueTime")
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
                 PreparedStatement psmtmember= conn.prepareStatement(deleteMemberSql)) {

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
    /* EDITED: Fetches a single member object based on their unique ID */
    public com.buet.bibliotech.member getMemberById(String memberId) {
        String query = "SELECT * FROM members WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new com.buet.bibliotech.member(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getString("batch"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public javafx.collections.ObservableList<String> getBorrowedBooksByMember(String memberId) {
        javafx.collections.ObservableList<String> bookNames = javafx.collections.FXCollections.observableArrayList();
        // query joins issue and books tables to get the name based on the ID in issue
        String query = "SELECT books.bookname FROM issue JOIN books ON issue.bookID = books.bookid WHERE issue.memberID = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bookNames.add(rs.getString("bookname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookNames;
    }

}
