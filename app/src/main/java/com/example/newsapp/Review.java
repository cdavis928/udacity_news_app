package com.example.newsapp;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.format.DateTimeFormatter;

public class Review {

    // Headline for the review
    private String mHeadline;

    // Name of the contributor for the review;
    private String mContributor;

    // Date of publication
    private String mPublicationDate;

    // URL to the review
    private String mUrl;

    // URL for the image thumbnail
    private String mThumbnailUrl;

    // Section name
    private String mSection;

    /**
     * @param headline
     * @param contributor
     * @param publicationDate of publication
     * @param url             of the article
     */
    public Review(String headline, String contributor, String publicationDate, String url, String thumbnailUrl, String section) {
        mHeadline = headline;
        mContributor = contributor;
        mPublicationDate = publicationDate;
        mUrl = url;
        mThumbnailUrl = thumbnailUrl;
        mSection = section;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getContributor() {
        return mContributor;
    }

    public String getPublicationDate() {
        return mPublicationDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public String getSection() {
        return mSection;
    }
}
