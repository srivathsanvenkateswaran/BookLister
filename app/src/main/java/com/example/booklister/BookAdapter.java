package com.example.booklister;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(@NonNull Context context, @NonNull ArrayList<Book> objects) {
        super(context, 0, objects);
    }

    private int getReviewColor(double review){
        int reviewColor;
        if(review >= 4.0){
            //green color #00a170
            reviewColor = R.color.greenReview;
        }
        else if(review <4.0 && review >= 3.0){
            //saffron color #FF9529
            reviewColor = R.color.saffronReview;
        }
        else if(review <3.0 && review >= 2.0){
            //red color #ff4433
            reviewColor = R.color.redReview;
        }
        else{
            //dark red color #800b00
            reviewColor = R.color.darkRedReview;
        }
        return ContextCompat.getColor(getContext(), reviewColor);
    }
    //First add the required color to the colors.xml file.

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
        }

        Book currentBook = getItem(position);

        //-----------------------Ratings-----------------------------
        TextView reviewTextView = (TextView) listItemView.findViewById(R.id.reviewTextView);
        reviewTextView.setText(String.valueOf(currentBook.getAverageRating()));

        //-----------------------Ratings Color-----------------------------
        GradientDrawable reviewColor = (GradientDrawable) reviewTextView.getBackground();
        int reviewColorID = getReviewColor(currentBook.getAverageRating());
        reviewColor.setColor(reviewColorID);

        //-----------------------Book Name-----------------------------
        TextView bookName = (TextView) listItemView.findViewById(R.id.bookName);
        bookName.setText(currentBook.getName());

        //-----------------------Author Name-----------------------------
        TextView authorName = (TextView) listItemView.findViewById(R.id.authorName);
        authorName.setText(currentBook.getAuthorName());

        //-----------------------Page Count-----------------------------
        TextView pageCount = (TextView) listItemView.findViewById(R.id.numberOfPagesTextView);
        pageCount.setText(String.valueOf(currentBook.getPageCount()));

        //-----------------------PDF Availability-----------------------------
        ImageView PDFAvailability = (ImageView) listItemView.findViewById(R.id.pdfAvailabilityImage);
        if(currentBook.isPDFAvailable()){
            PDFAvailability.setImageResource(R.drawable.pdf_green);
        }
        else {
            PDFAvailability.setImageResource(R.drawable.pdf_red);
        }

        //-----------------------Book Image Thumbnail-----------------------------
        ImageView bookImage = (ImageView) listItemView.findViewById(R.id.bookImage);
        Bitmap bookImageBitmap = currentBook.getBookImageBitmap();
        bookImage.setImageBitmap(bookImageBitmap);

        return listItemView;
    }
}
