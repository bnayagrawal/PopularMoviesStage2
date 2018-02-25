package com.example.bnayagrawal.popularmoviesstage2.content;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bnayagrawal on 2/23/2018.
 */

public class Review implements Parcelable {

    private String mId;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    public static final Creator CREATOR = new Creator() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public Review(String id, String author, String content, String url) {
        mId = id;
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    public Review(Parcel in) {
        mId = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
        mUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mAuthor);
        parcel.writeString(mContent);
        parcel.writeString(mUrl);
    }

    //Getter Methods
    public String getId() { return mId; }
    public String getAuthor() { return mAuthor; }
    public String getContent() { return mContent; }
    public String getUrl() { return mUrl; }
}
