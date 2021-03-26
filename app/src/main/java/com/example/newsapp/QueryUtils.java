package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import com.example.newsapp.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Query the Guardian dataset and return a list of {@link Review} objects.
     */
    public static List<Review> fetchReviews(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of reviews
        List<Review> reviews = extractArticleFromJson(jsonResponse);

        // Return the list of reviews
        return reviews;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Review} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Review> extractArticleFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding reviews to
        List<Review> reviews = new ArrayList<>();

        // Try: parse the JSON response string
        // Catch: if the JSON isn't formatted properly. Print the error message
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            //Create the 'response' object to go down one level in the JSON hierarchy
            // and then extract the results array from that object
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");

            // For each article in the resultsArray, create a {@link News} object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single review at position i within the list of reviews
                JSONObject currentNews = resultsArray.getJSONObject(i);

                // Extract the value for the key called "headline"
                String headline = currentNews.getString("webTitle");

                // Extract the value for the key called "publicationDate"
                String publicationDate = currentNews.getString("webPublicationDate");

                // Extract the value for the key called "url"
                String url = currentNews.getString("webUrl");

                //Extract the section name for the key called "sectionName"
                String sectionName = currentNews.getString("sectionName");

                // Get the thumbnail url string from the fields object
                JSONObject fieldsObject = currentNews.getJSONObject("fields");
                String thumbnailUrl = fieldsObject.getString("thumbnail");

                // The contributor name must be pulled from the "tags" array */
                //Extract this "tags" array which is nested one level underneath the results
                JSONArray tagsArray = currentNews.getJSONArray("tags");

                // A separate object has to be created so we can pull from the "tags" array
                JSONObject tagsObject = tagsArray.getJSONObject(0);

                // Extract the contributor name, which comes from the "tags" array
                String contributor = tagsObject.getString("webTitle");

                // Create a new review object with the headline, contributor,
                // publication date, and url
                Review reviewItem = new Review(headline, contributor, publicationDate, url, thumbnailUrl, sectionName);
                // Add the new review to the list of reviews
                reviews.add(reviewItem);
            }

        } catch (JSONException e) {
            // Print a log message if the above try fails
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }

        // Return the list of reviews
        return reviews;
    }

}