package com.example.booklister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String LogTag = "MainActivity: ";

    String topicEnteredByUser = "Graphic Designing"; //This will contain the text entered by the user while performing search
    String urlString = "https://www.googleapis.com/books/v1/volumes?q=";
//    String urlStringAfterTopic = "&maxResults=10";
    /*
    This maxResults=10 is sort of redundant because after checking three queries, even without giving the maxResults = 10, the API returned only 10 responses.
     */

    ProgressBar dataFetchingProgressBar;
    TextView emptyStateTextView;
    BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText searchEditText = (EditText) findViewById(R.id.searchEditText);
        ImageButton searchButton = findViewById(R.id.searchButton);

        ArrayList<Book> bookArrayList = new ArrayList<>();

        ListView listView = (ListView) findViewById(R.id.booksListView);

        adapter = new BookAdapter(this, bookArrayList);

        listView.setAdapter(adapter);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        dataFetchingProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        emptyStateTextView = (TextView) findViewById(R.id.emptyStateTextView);
        listView.setEmptyView(emptyStateTextView);

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i(LogTag, "Search Button Clicked");
                topicEnteredByUser = searchEditText.getText().toString();
                Log.i(LogTag, "Topic Entered by user: "+topicEnteredByUser);
                /*
                Assign the text entered by the user once the search button is clicked.
                This will help us in altering the query parameters in the URL.
                 */

                /*
//                  topicEnteredByUser = topicEnteredByUser.toLowerCase();
                    Even this is not necessary because while testing with the API, the case search term doesn't matter.
                 */

                urlString = urlString + topicEnteredByUser;
                //This will add the topic entered by user in the query params.

                if(isConnected)
                {
                    Log.i(LogTag, "Network connection is present");
                    BookAsynTask task = new BookAsynTask();
                    task.execute(urlString);
                }
                else
                {
                    emptyStateTextView.setText(R.string.empty_state_text);
                    dataFetchingProgressBar.setVisibility(View.GONE);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book currentBook = adapter.getItem(i);

                Uri bookUri = Uri.parse(currentBook.getBookURL());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

    }

    class BookAsynTask extends AsyncTask<String, Void, List<Book>>
    {
        String LogTag = "BookAsynTask: ";
        @Override
        protected List<Book> doInBackground(String... strings) {
            Log.i(LogTag, "Inside doInBackground");
            if(strings.length<1 || strings[0]==null)
            {
                return null;
            }
            ArrayList<Book> bookArrayList = QueryUtils.getBookData(urlString);
            return bookArrayList;
        }

        protected void onPostExecute(List<Book> bookArrayList){
            adapter.clear();
            Log.i(LogTag, "Inside onPostExecute");
            dataFetchingProgressBar.setVisibility(View.GONE);

            if(bookArrayList != null && !bookArrayList.isEmpty()){
                Log.i(LogTag, "Adding the ArrayList to Adapter. ");
                adapter.addAll(bookArrayList);
            }

            emptyStateTextView.setText(R.string.empty_data_text);
        }
    }

}