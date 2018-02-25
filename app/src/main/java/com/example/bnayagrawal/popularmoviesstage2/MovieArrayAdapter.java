package com.example.bnayagrawal.popularmoviesstage2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.*;
import com.example.bnayagrawal.popularmoviesstage2.content.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by binay on 27/1/18.
 */

public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    private static final String DEBUG_TAG = MovieArrayAdapter.class.getSimpleName();

    private String mPosterUrl;
    private MainActivity mActivity;

    public MovieArrayAdapter(MainActivity activity, List<Movie> movies) {
        super(activity, 0, movies);
        mActivity= activity;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (null == convertView)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_view_item, parent, false);

        final ImageView imageView = (ImageView) convertView;
        mPosterUrl = getItem(position).getPosterPath();

        //load image
        Glide.with(mActivity).load(mPosterUrl).into(imageView);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.INTENT_TAG_MOVIE_ID, getItem(position).getId());
                intent.putExtra(MovieDetailActivity.INTENT_TAG_BASE_IMAGE_URL,MainActivity.sBaseImageUrl);
                getContext().startActivity(intent);
                mActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        return convertView;
    }
}
