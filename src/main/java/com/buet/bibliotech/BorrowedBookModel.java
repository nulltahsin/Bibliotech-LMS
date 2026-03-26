package com.buet.bibliotech;

import javafx.scene.control.CheckBox;

import javax.swing.*;

public class BorrowedBookModel {
    private String serial;
    private String bookName;
    private String bookAuthor;
    private String bookId;
    private CheckBox selectBox;

    public BorrowedBookModel(String serial, String bookName,String bookId , String bookAuthor) {
        this.serial = serial;
        this.bookName = bookName;
        this.bookAuthor=bookAuthor;
        this.bookId=bookId;
        this.selectBox=new CheckBox();
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

    public CheckBox getSelectBox() { return selectBox; }
}