package com.example.booklister;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class Book {

    private String Name;
    private String AuthorName;
    private String BookURL;
    private int pageCount;
    private double averageRating;
    private boolean isPDFAvailable;
    private Bitmap bookImageBitmap;

    Book(String Name, String AuthorName, String BookURL, int pageCount, double averageRating, boolean isPDFAvailable, Bitmap bookImageBitmap){
        this.Name = Name;
        this.AuthorName = "Author: "+AuthorName;
        this.BookURL = BookURL;
        this.pageCount = pageCount;
        this.averageRating = averageRating;
        this.isPDFAvailable = isPDFAvailable;
        this.bookImageBitmap = bookImageBitmap;
    }

    public String getName(){
        return Name;
    }
    public String getAuthorName(){
        return AuthorName;
    }
    public String getBookURL(){
        return BookURL;
    }
    public int getPageCount(){
        return pageCount;
    }
    public double getAverageRating(){
        return averageRating;
    }
    public boolean isPDFAvailable(){
        return isPDFAvailable;
    }
    public Bitmap getBookImageBitmap(){
        return bookImageBitmap;
    }
}
