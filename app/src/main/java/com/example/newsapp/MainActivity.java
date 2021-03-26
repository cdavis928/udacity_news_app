package com.example.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<Review>> {

    /**
     * URL for the guardian API, which brings in the latest reviews for all forms of media
     */
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?tag=tone/reviews&show-tags=contributor&show-fields=thumbnail&order-by=newest&api-key=test&limit=10";

    /**
     * Adapter for the list of articles
     */
    private ReviewAdapter mAdapter;

    /**
     * assign an ID for the loader
     */
    private static final int GUARDIAN_LOADER_ID = 1;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView reviewListView = (ListView) findViewById(R.id.list);

        // Create the adapter that takes in reviews as an input
        mAdapter = new ReviewAdapter(this, new ArrayList<Review>());

        //Set the adapter to the ListView
        reviewListView.setAdapter(mAdapter);

        // Item on click listener opens the full review when clicked
        reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current review that was clicked on
                Review currentReview = mAdapter.getItem(position);

                // Convert the String URL into a URI object and create a new intent to view the review
                Uri reviewUri = Uri.parse(currentReview.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, reviewUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Fetch data if there's a network connection
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader
            loaderManager.initLoader(GUARDIAN_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // Hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
            reviewListView.setEmptyView(mEmptyStateTextView);

            // Update empty state with error message
            mEmptyStateTextView.setText(R.string.no_connection);
        }

    }

    @Override
    public Loader<List<Review>> onCreateLoader(int i, Bundle bundle) {
        // URL string is passed through URI builder to pass into loader
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        return new ReviewLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Review>> loader, List<Review> reviews) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of previous earthquake data
        // This is needed to prevent the app from duplicating list items when using the
        // back button after loading a URL
        mAdapter.clear();

        // The list view will update if there is a valid list of reviews
        if (reviews != null && !reviews.isEmpty()) {
            mAdapter.addAll(reviews);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Review>> loader) {
        mAdapter.clear();
    }
}