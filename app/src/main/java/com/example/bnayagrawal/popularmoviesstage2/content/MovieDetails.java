package com.example.bnayagrawal.popularmoviesstage2.content;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bnayagrawal on 2/23/2018.
 */

public class MovieDetails implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    private int mId;
    private String mTitle;
    private String mPosterPath;
    private String mOverview;
    private double mVoteAverage;
    private String mReleaseDate;
    private int mRuntime;
    private String mBackdropPath;

    //public constructor for this class
    public MovieDetails(int id, String title, String posterPath, String overview, double voteAverage, String releaseDate, int runtime, String backdropPath) {
        mId = id;
        mTitle = title;
        mPosterPath = posterPath;
        mOverview = overview;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
        mRuntime = runtime;
        mBackdropPath = backdropPath;
    }

    public MovieDetails(Parcel in) {
        mId = in.readInt();
        mTitle = in.readString();
        mPosterPath = in.readString();
        mOverview = in.readString();
        mVoteAverage = in.readDouble();
        mReleaseDate = in.readString();
        mRuntime = in.readInt();
        mBackdropPath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mPosterPath);
        parcel.writeString(mOverview);
        parcel.writeDouble(mVoteAverage);
        parcel.writeString(mReleaseDate);
        parcel.writeInt(mRuntime);
        parcel.writeString(mBackdropPath);
    }

    //Getter methods
    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public int getRuntime() {
        return mRuntime;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }
}
