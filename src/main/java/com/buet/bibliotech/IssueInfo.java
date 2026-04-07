package com.buet.bibliotech;

public class IssueInfo {

    private int issueID;
    private String bookID, bookName, memberID, memberName, issueTime;


    public IssueInfo(int issueID, String bookID, String bookName, String memberID, String memberName, String issueTime) {
        this.issueID = issueID;
        this.bookID = bookID;
        this.bookName = bookName;
        this.memberID = memberID;
        this.memberName = memberName;
        this.issueTime = issueTime;
    }


    public int getIssueID() {
        return issueID;
    }

    public String getBookID() {
        return bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getIssueTime() {
        return issueTime;
    }
}