package com.example.booklister;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class QueryUtils {

    private static String LOG_TAG = "QueryUtils: ";

    private QueryUtils(){
    }

    private static URL createURL(String urlString){
        URL urlObject = null;
        try {
            urlObject = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return urlObject;
    }

    private static String readFromStream(InputStream inputStream)
    {
        Log.i(LOG_TAG, "Inside readFromStream");
        StringBuilder stringBuilder = new StringBuilder();

        if(inputStream!=null)
        {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String Line = bufferedReader.readLine();
                while(Line!=null)
                {
                    stringBuilder.append(Line);
                    Line = bufferedReader.readLine();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }

    private static Bitmap getBitmapFromURL(String urlString) throws IOException {
        Bitmap bitmap = null;
        URL imageURLObject = createURL(urlString);
        HttpURLConnection urlConnection = null;
        InputStream bitmapInputStream = null;
        try {
            urlConnection = (HttpURLConnection) imageURLObject.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            bitmapInputStream = urlConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(bitmapInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(bitmapInputStream!=null)
            {
                bitmapInputStream.close();
            }

            if(urlConnection!=null)
            {
                urlConnection.disconnect();
            }
        }
        return bitmap;
    }

    private static String makeHttpRequest(URL urlObject) throws IOException {
        Log.i(LOG_TAG, "Inside makeHttpRequest");
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream jsonInputStream = null;
        try {
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);

            jsonInputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(jsonInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(jsonInputStream!=null)
            {
                jsonInputStream.close();
            }

            if(urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }
        return jsonResponse;
    }

    public static ArrayList<Book> getBookData(String urlString)
    {
        Log.i(LOG_TAG, "Inside getBookData");
        URL bookURLObject = createURL(urlString);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(bookURLObject);
            Log.i(LOG_TAG, "JSON Response: "+jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Book> bookArrayList = getBooksFromJSON(jsonResponse);

        return bookArrayList;
    }

    public static ArrayList<Book> getBooksFromJSON(String JSONResponse){
        Log.i(LOG_TAG, "Inside getBookFromJSON");
        if(TextUtils.isEmpty(JSONResponse))
        {
            return null;
        }

        ArrayList<Book> bookArrayList = new ArrayList<>();

        try {
            JSONObject rootJSONObject = new JSONObject(JSONResponse);
            JSONArray itemsJSONArray = rootJSONObject.getJSONArray("items");

            for(int i=0;i<itemsJSONArray.length();i++)
            {
                Log.i(LOG_TAG, "Parsing JSON for Book Number: "+(i+1));
                JSONObject bookJSONObject = itemsJSONArray.getJSONObject(i);
//                This is to get the first item of the items json array

                JSONObject volumeInfo = bookJSONObject.getJSONObject("volumeInfo");
                JSONObject accessInfo = bookJSONObject.getJSONObject("accessInfo");
//                This gets the volumeInfo JSON object, which is present inside every item.

                String bookName = volumeInfo.getString("title");
                Log.i(LOG_TAG, "Book Name: "+bookName);
//                This gets the Name of the book which is a string available with the key title.

                JSONArray authorsJSONArray = volumeInfo.getJSONArray("authors");
                String[] authorsStringArray = new String[authorsJSONArray.length()];
                for(int j=0;j<authorsJSONArray.length();j++)
                {
                    authorsStringArray[j]= authorsJSONArray.getString(j);
                }
                String authorName = authorsStringArray[0];
                Log.i(LOG_TAG, "Author Name: "+authorName);
//                Since a book can be authored more than one, the author name is given in an array of strings with the key authors.
//                But to make things simple, show only one of the author.
//                first, we get the JSON array which contains the strings. Then we create a string array of the same length of the JSONArray.
//                Then, we iterate through the JSONArray and then fetch the string and then set them into the string array.
//                After this, we assign the value of author name to be the first name inside the string array.
//                Yes, this method is not efficient if we just want to get the name of the first author in the JSONArray. we could have directly got the name of the array
//                authorName = authorsJSONArray.getString(0). This would have worked perfectly. but just in case if we wanted to store all the authors, just in case we would like to expand the application in future, this method will work as now, we have access the names of all the authors of the book.

                String bookURL = volumeInfo.getString("previewLink");
                Log.i(LOG_TAG, "Book URL: "+bookURL);

                int pageCount = volumeInfo.getInt("pageCount");
                Log.i(LOG_TAG, "Pages Count: "+pageCount);

                double averageRating = volumeInfo.getDouble("averageRating");
                Log.i(LOG_TAG, "Average Rating: "+averageRating);

                JSONObject pdfObject = accessInfo.getJSONObject("pdf");
                Boolean isPDFAvailable = pdfObject.getBoolean("isAvailable");
                Log.i(LOG_TAG, "isPDFAvailable: "+ isPDFAvailable);

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks"); //Here is the mistake!!!!!!!!!!!!!!!!!!
                String imageURL = imageLinks.getString("smallThumbnail");
                Log.i(LOG_TAG, "Image URL: "+imageURL);

//                There is a problem with creating ImageView. So, we are storing Bitmap inside the Book.java and set the bitmap to the ImageView inside the Adapter.
//                ImageView bookImageView = new ImageView();
                Bitmap bookImageBitmap = null;
                try {
                    bookImageBitmap = getBitmapFromURL(imageURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                bookArrayList.add(new Book(bookName, authorName, bookURL, pageCount, averageRating,isPDFAvailable, bookImageBitmap));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bookArrayList;
    }
}
