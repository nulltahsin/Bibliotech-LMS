package com.buet.bibliotech;

public class RequestInfo {
    //  names same as the PropertyValueFactory in your Controller
    private String bookID;
    private String bookName;
    private String memberID;
    private String memberName;

    public RequestInfo(String bookID, String bookName, String memberID, String memberName) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.memberID = memberID;
        this.memberName = memberName;
    }


    public String getBookID() { return bookID; }
    public String getBookName() { return bookName; }
    public String getMemberID() { return memberID; }
    public String getMemberName() { return memberName; }
}