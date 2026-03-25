package com.buet.bibliotech;

public class BorrowedBookModel {
    private String serial;
    private String bookName;

    public BorrowedBookModel(String serial, String bookName) {
        this.serial = serial;
        this.bookName = bookName;
    }

    public String getSerial() { return serial; }
    public String getBookName() { return bookName; }
}