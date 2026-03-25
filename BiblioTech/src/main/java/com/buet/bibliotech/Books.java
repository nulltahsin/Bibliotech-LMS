package com.buet.bibliotech;

public class Books {
    private String BookID;
    private String BookName;
    private String Author;
    private String Category;


    public Books(String bookid, String bookname , String author , String category){
        this.BookID=bookid;
        this.BookName=bookname;
        this.Author=author;
        this.Category=category;

    }
    public String getBookID(){
        return BookID;
    }
    public String getBookName(){
        return BookName;
    }
    public String getAuthor(){
        return Author;
    }
    public String getCategory(){
        return Category;
    }

}
