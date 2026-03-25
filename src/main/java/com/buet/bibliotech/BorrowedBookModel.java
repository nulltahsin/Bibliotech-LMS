package com.buet.bibliotech;

public class BorrowedBookModel {
    private String serial;
    private String bookName;
    private String bookAuthor;
    private String bookId;

    public BorrowedBookModel(String serial, String bookName,String bookId , String bookAuthor) {
        this.serial = serial;
        this.bookName = bookName;
        this.bookAuthor=bookAuthor;
        this.bookId=bookId;
    }

    public String getSerial() {
        return serial;
    }
    public String getBookName() {
        return bookName;
    }

    public String getBookId() {
        return bookId;
    }


    public String getBookAuthor() {
        return bookAuthor;
    }
}