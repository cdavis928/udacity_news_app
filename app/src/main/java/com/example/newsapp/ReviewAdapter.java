package com.example.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(@NonNull Context context, ArrayList<Review> reviews) {
        super(context, 0, reviews);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Review currentReview = getItem(position);

        //Find the text view for the headline from the list and then set it .
        TextView headlineView = (TextView) listItemView.findViewById(R.id.list_headline);
        headlineView.setText(currentReview.getHeadline());

        //Find the text view for the contributor from the list and then set it
        TextView contributorView = (TextView) listItemView.findViewById(R.id.list_contributor);
        contributorView.setText(currentReview.getContributor());

        // Find the text view for the section name and set it
        TextView sectionNameView = (TextView) listItemView.findViewById(R.id.list_section);
        sectionNameView.setText(currentReview.getSection());

/**
 * Solution for formatting the datetime string was created with help from:
 * https://stackoverflow.com/questions/51234171/parsing-datetime-in-java-for-previous-os-versions
 */
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'");

        Date myDate = null;
        try {
            myDate = dateFormat.parse(currentReview.getPublicationDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Define a new SimpleDateFormat object to reconstruct the date into the desired format.
        DateFormat newDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        // Convert the Date object into a String.
        String formattedDate = newDateFormat.format(myDate);

        TextView publicationDateView = (TextView) listItemView.findViewById(R.id.list_publication_date);
        publicationDateView.setText(formattedDate);

        //Find the image view and pass the thumbnail URL into it with help from DownloadImageTask
        ImageView image = listItemView.findViewById(R.id.list_thumbnail);

        if (currentReview != null) {
            new DownloadImageTask(image).execute(currentReview.getThumbnailUrl());
        }

        return listItemView;
    }

    /**
     * DownloadImageTask and the above if statement are borrowed with help from:
     * https://medium.com/@crossphd/android-image-loading-from-a-string-url-6c8290b82c5e
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}