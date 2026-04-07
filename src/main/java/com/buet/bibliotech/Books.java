package com.buet.bibliotech;

public class Books {
    private String BookID, BookName, Author, Category, imagePath;
    private int totalCopies, availableCopies;

    public Books(String bookid, String bookname, String author, String category, int totalCopies, int availableCopies, String imagePath) {
        this.BookID = bookid;
        this.BookName = bookname;
        this.Author = author;
        this.Category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.imagePath = imagePath;
    }


    public String getBookID() { return BookID; }
    public String getBookName() { return BookName; }
    public String getAuthor() { return Author; }
    public String getCategory() { return Category; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public String getImagePath() { return imagePath; }
}