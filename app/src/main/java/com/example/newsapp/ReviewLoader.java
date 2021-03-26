package com.example.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class ReviewLoader extends AsyncTaskLoader<List<Review>> {

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link ReviewLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public ReviewLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Review> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of review articles.
        List<Review> articles = com.example.android.newsapp.QueryUtils.fetchReviews(mUrl);
        return articles;
    }
}