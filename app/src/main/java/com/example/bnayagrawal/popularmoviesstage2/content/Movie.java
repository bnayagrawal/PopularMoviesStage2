package com.example.bnayagrawal.popularmoviesstage2.content;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by binay on 27/1/18.
 */

public class Movie implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private int mId;
    private String mPosterPath;

    //public constructor for this class
    public Movie(int id, String posterPath) {
        mId = id;
        mPosterPath = posterPath;
    }

    public Movie(Parcel in) {
        mId = in.readInt();
        mPosterPath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mPosterPath);
    }

    //Getter methods
    public int getId() { return mId; }

    public String getPosterPath() {
        return this.mPosterPath;
    }

    public enum Selection {
        POPULAR_MOVIES,
        TOP_RATED_MOVIES,
        FAVORITE_MOVIES
    }
}
