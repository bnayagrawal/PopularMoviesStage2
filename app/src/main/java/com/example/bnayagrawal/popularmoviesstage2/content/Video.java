package com.example.bnayagrawal.popularmoviesstage2.content;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bnayagrawal on 2/23/2018.
 */

public class Video implements Parcelable{

    private String mId;
    private String mKey;
    private String mName;
    private String mSite;
    private int mSize;
    private String mType;

    public static final Creator CREATOR = new Creator() {
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public Video(String id, String key, String name, String site, int size, String type) {
        mId = id;
        mKey = key;
        mName = name;
        mSite = site;
        mSize = size;
        mType = type;
    }

    public Video(Parcel in) {
        mId = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mSite = in.readString();
        mSize = in.readInt();
        mType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mKey);
        parcel.writeString(mName);
        parcel.writeString(mSite);
        parcel.writeInt(mSize);
        parcel.writeString(mType);
    }

    //Getter methods
    public String getId() { return mId; }
    public String getKey() { return mKey; }
    public String getName() { return mName; }
    public String getmSite() { return mSite; }
    public int getSize() { return mSize; }
    public String getType() { return mType; }
}
