package com.example.bnayagrawal.popularmoviesstage2.content;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by binay on 27/1/18.
 */

public class Configuration implements Parcelable {
    public static final Creator CREATOR = new Creator() {
        public Configuration createFromParcel(Parcel in) {
            return new Configuration(in);
        }

        public Configuration[] newArray(int size) {
            return new Configuration[size];
        }
    };
    private String base_url;
    private String secure_base_url;
    private String[] backdropSizes, logoSizes, posterSizes, profile_sizes, stillSizes;

    public Configuration(String base_url,
                         String secure_base_url,
                         String[] backdropSizes,
                         String[] logoSizes,
                         String[] posterSizes,
                         String[] profile_sizes,
                         String[] stillSizes) {
        this.base_url = base_url;
        this.secure_base_url = secure_base_url;
        this.backdropSizes = backdropSizes;
        this.logoSizes = logoSizes;
        this.posterSizes = posterSizes;
        this.profile_sizes = profile_sizes;
        this.stillSizes = stillSizes;
    }

    public Configuration(Parcel in) {
        this.base_url = in.readString();
        this.secure_base_url = in.readString();
        this.backdropSizes = in.createStringArray();
        this.logoSizes = in.createStringArray();
        this.posterSizes = in.createStringArray();
        this.profile_sizes = in.createStringArray();
        this.stillSizes = in.createStringArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.base_url);
        parcel.writeString(this.secure_base_url);
        parcel.writeStringArray(this.backdropSizes);
        parcel.writeStringArray(this.logoSizes);
        parcel.writeStringArray(this.posterSizes);
        parcel.writeStringArray(this.profile_sizes);
        parcel.writeStringArray(this.stillSizes);
    }

    public String getBaseUrl() {
        return this.base_url;
    }

    public String getSecureBaseUrl() {
        return this.secure_base_url;
    }

    public String[] getBackdropSizes() {
        return this.backdropSizes;
    }

    public String[] getLogoSizes() {
        return this.logoSizes;
    }

    public String[] getPosterSizes() {
        return this.posterSizes;
    }

    public String[] getProfileSizes() {
        return this.profile_sizes;
    }

    public String[] getStillSizes() {
        return this.stillSizes;
    }
}
