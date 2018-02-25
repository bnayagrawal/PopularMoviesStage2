package com.example.bnayagrawal.popularmoviesstage2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.bnayagrawal.popularmoviesstage2.content.MovieDetails;
import com.example.bnayagrawal.popularmoviesstage2.content.Review;
import com.example.bnayagrawal.popularmoviesstage2.content.Video;
import com.example.bnayagrawal.popularmoviesstage2.data.FavoriteMoviesContract;
import com.example.bnayagrawal.popularmoviesstage2.util.NetworkUtils;
import com.example.bnayagrawal.popularmoviesstage2.util.TheMovieDbJsonUtils;
import com.example.bnayagrawal.popularmoviesstage2.util.VolleyNetworkHandler;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.bnayagrawal.popularmoviesstage2.data.FavoriteMoviesContract.FavoriteMoviesEntry;

public class MovieDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String INTENT_TAG_MOVIE_ID = "movie_id";
    public static final String INTENT_TAG_BASE_IMAGE_URL = "base_image_url";

    private static final String INS_TAG_MOVIE_ID = "movie_id";
    private static final String INS_TAG_MOVIE_DETAILS = "movie_details";
    private static final String INS_TAG_BASE_IMAGE_URL = "base_image_url";
    private static final String INS_TAG_REVIEWS = "reviews";
    private static final String INS_TAG_VIDEOS = "videos";

    private static final String[] FAVORITE_MOVIES_PROJECTION = {
            FavoriteMoviesEntry.COLUMN_MOVIE_ID,
            FavoriteMoviesEntry.COLUMN_TITLE,
            FavoriteMoviesEntry.COLUMN_OVERVIEW,
            FavoriteMoviesEntry.COLUMN_RELEASE_DATE,
            FavoriteMoviesEntry.COLUMN_VOTE_AVERAGE,
            FavoriteMoviesEntry.COLUMN_POSTER_PATH,
            FavoriteMoviesEntry.COLUMN_RUNTIME,
            FavoriteMoviesEntry.COLUMN_BACKDROP_PATH
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_OVERVIEW = 2;
    public static final int INDEX_MOVIE_RELEASE_DATE = 3;
    public static final int INDEX_MOVIE_VOTE_AVERAGE = 4;
    public static final int INDEX_MOVIE_POSTER_PATH = 5;
    public static final int INDEX_MOVIE_RUNTIME = 6;
    public static final int INDEX_MOVIE_BACKDROP_PATH = 7;

    private static final String VOLLEY_REQUEST_MOVIE_DETAILS_TAG = "request-movie-details";
    private static final String VOLLEY_REQUEST_MOVIE_VIDEOS_TAG = "request-movie-videos";
    private static final String VOLLEY_REQUEST_MOVIE_REVIEWS_TAG = "request-movie-reviews";

    private static final int OFFLINE_AVAILABILITY_CHECK_LOADER_TASK_ID = 0;
    private static final int GET_OFFLINE_DATA_LOADER_TASK_ID = 1;

    private Context mApplicationContext;
    private Animation mAnimation;

    private int mMovieId;
    private String mBaseImageUrl;

    private MovieDetails mMovieDetails;
    private ArrayList<Review> mReviews;
    private ArrayList<Video> mVideos;

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_overview)
    TextView mTvOverview;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.tv_duration)
    TextView mTvDuration;
    @BindView(R.id.tv_rating)
    TextView mTvRating;
    @BindView(R.id.img_backdrop)
    ImageView mImgBackdrop;
    @BindView(R.id.img_poster)
    ImageView mImgPoster;
    @BindView(R.id.btn_favorite)
    Button mButtonFavorite;

    @BindView(R.id.tv_label_trailer)
    TextView mTvLabelTrailer;
    @BindView(R.id.tv_label_review)
    TextView mTvLabelReview;

    //Trailer or videos
    @BindView(R.id.trailer_list_container)
    LinearLayout mTrailerListContainer;
    private TextView mTvTrailerName;

    //Reviews
    @BindView(R.id.review_list_container)
    LinearLayout mReviewListContainer;
    private TextView mTvReviewAuthor;
    private TextView mTvReviewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        mApplicationContext = getApplicationContext();
        mAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        if (null != savedInstanceState) {
            loadDataFromBundle(savedInstanceState);
        } else {
            Intent intent = getIntent();
            if (intent.hasExtra(INTENT_TAG_MOVIE_ID))
                mMovieId = intent.getIntExtra(INTENT_TAG_MOVIE_ID, -1);
            if (intent.hasExtra(INTENT_TAG_BASE_IMAGE_URL))
                mBaseImageUrl = intent.getStringExtra(INTENT_TAG_BASE_IMAGE_URL);
            if (mMovieId != -1) {
                fetchMovieDetails();
                fetchMovieVideos();
                fetchMovieReviews();
            } else {
                Toast.makeText(mApplicationContext, getString(R.string.movie_not_found), Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        }

        getSupportLoaderManager().initLoader(OFFLINE_AVAILABILITY_CHECK_LOADER_TASK_ID, null, MovieDetailActivity.this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(INS_TAG_MOVIE_DETAILS, mMovieDetails);
        outState.putInt(INS_TAG_MOVIE_ID, mMovieId);
        outState.putString(INS_TAG_BASE_IMAGE_URL, mBaseImageUrl);
        outState.putParcelableArrayList(INS_TAG_REVIEWS, mReviews);
        outState.putParcelableArrayList(INS_TAG_VIDEOS, mVideos);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleyNetworkHandler.getInstance(mApplicationContext).getRequestQueue().cancelAll(VOLLEY_REQUEST_MOVIE_DETAILS_TAG);
        VolleyNetworkHandler.getInstance(mApplicationContext).getRequestQueue().cancelAll(VOLLEY_REQUEST_MOVIE_REVIEWS_TAG);
        VolleyNetworkHandler.getInstance(mApplicationContext).getRequestQueue().cancelAll(VOLLEY_REQUEST_MOVIE_VIDEOS_TAG);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void loadDataFromBundle(Bundle savedInstanceState) {
        mMovieId = savedInstanceState.getInt(INS_TAG_MOVIE_ID);
        mBaseImageUrl = savedInstanceState.getString(INS_TAG_BASE_IMAGE_URL);
        mMovieDetails = savedInstanceState.getParcelable(INS_TAG_MOVIE_DETAILS);
        mReviews = savedInstanceState.getParcelableArrayList(INS_TAG_REVIEWS);
        mVideos = savedInstanceState.getParcelableArrayList(INS_TAG_VIDEOS);

        if (!isOnline() && mMovieDetails != null) {
            bindDataToViews();
        } else {
            bindDataToViews();
            inflateReviews();
            inflateVideos();
        }
    }

    private void setLoading() {
        mTvTitle.setText(R.string.loading);
        mTvOverview.setText(R.string.loading);
        mTvDuration.setText(R.string.loading);
        mTvDate.setText(R.string.loading);
        mTvRating.setText(R.string.loading);
        mTvLabelReview.setText(R.string.loading);
        mTvLabelTrailer.setText(R.string.loading);
    }

    private void bindDataToViews() {
        mTvTitle.setText(mMovieDetails.getTitle());
        mTvOverview.setText(mMovieDetails.getOverview());
        mTvDate.setText(mMovieDetails.getReleaseDate());
        mTvDuration.setText(String.valueOf(mMovieDetails.getRuntime()).concat(getString(R.string.minutes)));
        mTvRating.setText(String.valueOf(mMovieDetails.getVoteAverage()).concat("/10"));

        Glide.with(this).load(mMovieDetails.getBackdropPath()).into(mImgBackdrop);
        mImgBackdrop.startAnimation(mAnimation);

        Glide.with(this).load(mMovieDetails.getPosterPath()).into(mImgPoster);
        mImgPoster.startAnimation(mAnimation);
    }

    private void bindDataToViews(Cursor cursor) {
        if (null == cursor || cursor.getCount() < 1) {
            Toast.makeText(mApplicationContext, getString(R.string.unavailable_offline), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        cursor.moveToFirst();
        mMovieDetails = new MovieDetails(
                cursor.getInt(INDEX_MOVIE_ID),
                cursor.getString(INDEX_MOVIE_TITLE),
                cursor.getString(INDEX_MOVIE_POSTER_PATH),
                cursor.getString(INDEX_MOVIE_OVERVIEW),
                cursor.getDouble(INDEX_MOVIE_VOTE_AVERAGE),
                cursor.getString(INDEX_MOVIE_RELEASE_DATE),
                cursor.getInt(INDEX_MOVIE_RUNTIME),
                cursor.getString(INDEX_MOVIE_BACKDROP_PATH)
        );
        cursor.close();
        bindDataToViews();
        Toast.makeText(mApplicationContext, getString(R.string.offline_data), Toast.LENGTH_SHORT).show();
        mTvLabelTrailer.setText(R.string.offline);
        mTvLabelReview.setText(R.string.offline);
    }

    private void fetchMovieDetails() {
        if (!isOnline()) {
            loadOfflineData();
            return;
        } else if (mMovieId == -1) {
            return;
        }

        setLoading();
        String url = NetworkUtils.buildMovieInfoUrl(NetworkUtils.MovieInfo.DETAILS, mMovieId);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mMovieDetails = TheMovieDbJsonUtils.getMovieDetailsFromJson(
                                response,
                                mBaseImageUrl,
                                getString(R.string.movie_poster_size),
                                getString(R.string.movie_backdrop_size));
                        bindDataToViews();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        jsObjRequest.setTag(VOLLEY_REQUEST_MOVIE_DETAILS_TAG);
        VolleyNetworkHandler.getInstance(mApplicationContext).addToRequestQueue(jsObjRequest);
    }

    private void fetchMovieVideos() {
        if (!isOnline()) {
            return;
        } else if (mMovieId == -1) {
            return;
        }

        String url = NetworkUtils.buildMovieInfoUrl(NetworkUtils.MovieInfo.VIDEOS, mMovieId);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mVideos = TheMovieDbJsonUtils.getVideoListFromJson(response);
                        inflateVideos();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        jsObjRequest.setTag(VOLLEY_REQUEST_MOVIE_VIDEOS_TAG);
        VolleyNetworkHandler.getInstance(mApplicationContext).addToRequestQueue(jsObjRequest);
    }

    private void inflateVideos() {
        new Runnable() {
            @Override
            public void run() {
                if (mVideos.size() < 1) {
                    mTvLabelTrailer.setText(R.string.trailers_not_available);
                    return;
                }

                View view;
                LayoutInflater layoutInflater = getLayoutInflater();
                for (final Video video : mVideos) {
                    view = layoutInflater.inflate(R.layout.trailer_list_item, mTrailerListContainer, false);
                    mTvTrailerName = view.findViewById(R.id.tv_trailer_name);
                    mTvTrailerName.setText(video.getName());

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = NetworkUtils.buildYoutubeVideoUrl(video.getKey());
                            Intent openVideoIntent = new Intent(Intent.ACTION_VIEW);
                            openVideoIntent.setData(Uri.parse(url));
                            startActivity(openVideoIntent);
                        }
                    });

                    mTrailerListContainer.addView(view);
                }

                mTvLabelTrailer.setText(
                        getString(R.string.label_trailer)
                                .concat(" ")
                                .concat(String.valueOf(mVideos.size())
                                )
                );
            }
        }.run();
    }

    private void fetchMovieReviews() {
        if (!isOnline()) {
            return;
        } else if (mMovieId == -1) {
            return;
        }

        String url = NetworkUtils.buildMovieInfoUrl(NetworkUtils.MovieInfo.REVIEWS, mMovieId);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mReviews = TheMovieDbJsonUtils.getReviewListFromJson(response);
                        inflateReviews();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        jsObjRequest.setTag(VOLLEY_REQUEST_MOVIE_REVIEWS_TAG);
        VolleyNetworkHandler.getInstance(mApplicationContext).addToRequestQueue(jsObjRequest);
    }

    private void inflateReviews() {
        new Runnable() {
            @Override
            public void run() {
                if (mReviews.size() < 1) {
                    mTvLabelReview.setText(R.string.reviews_not_available);
                    return;
                }

                View view;
                LayoutInflater layoutInflater = getLayoutInflater();
                for (Review review : mReviews) {
                    view = layoutInflater.inflate(R.layout.review_list_item, mReviewListContainer, false);
                    mTvReviewAuthor = view.findViewById(R.id.tv_review_author);
                    mTvReviewContent = view.findViewById(R.id.tv_review_content);
                    mTvReviewAuthor.setText(review.getAuthor());
                    mTvReviewContent.setText(("\"").concat(review.getContent()).concat("\""));
                    mReviewListContainer.addView(view);
                }

                mTvLabelReview.setText(
                        getString(R.string.label_reviews)
                                .concat(" ")
                                .concat(String.valueOf(mReviews.size())
                                )
                );
            }
        }.run();
    }

    private void saveToDatabase() {
        mButtonFavorite.setText(getString(R.string.please_wait));
        mButtonFavorite.setOnClickListener(null);
        new Runnable() {
            @Override
            public void run() {
                ContentValues contentValues = new ContentValues();
                contentValues.put(FavoriteMoviesEntry.COLUMN_MOVIE_ID, mMovieDetails.getId());
                contentValues.put(FavoriteMoviesEntry.COLUMN_TITLE, mMovieDetails.getTitle());
                contentValues.put(FavoriteMoviesEntry.COLUMN_OVERVIEW, mMovieDetails.getOverview());
                contentValues.put(FavoriteMoviesEntry.COLUMN_RELEASE_DATE, mMovieDetails.getReleaseDate());
                contentValues.put(FavoriteMoviesEntry.COLUMN_VOTE_AVERAGE, mMovieDetails.getVoteAverage());
                contentValues.put(FavoriteMoviesEntry.COLUMN_POSTER_PATH, mMovieDetails.getPosterPath());
                contentValues.put(FavoriteMoviesEntry.COLUMN_RUNTIME, mMovieDetails.getRuntime());
                contentValues.put(FavoriteMoviesEntry.COLUMN_BACKDROP_PATH, mMovieDetails.getBackdropPath());

                ContentResolver contentResolver = getContentResolver();
                Uri uri = contentResolver.insert(
                        FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI,
                        contentValues
                );

                if (null == uri) {
                    Toast.makeText(mApplicationContext, getString(R.string.db_insert_error), Toast.LENGTH_SHORT).show();
                    setFavoriteButtonBehaviour(false);
                } else {
                    Toast.makeText(mApplicationContext, getString(R.string.db_insert_success), Toast.LENGTH_LONG).show();
                    setFavoriteButtonBehaviour(true);
                }
            }
        }.run();
    }

    private void deleteFromDatabase() {
        mButtonFavorite.setText(getString(R.string.please_wait));
        mButtonFavorite.setOnClickListener(null);
        new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = getContentResolver();
                Uri uri = FavoriteMoviesEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(String.valueOf(mMovieId)).build();

                int rows_deleted = contentResolver.delete(uri, null, null);
                if (rows_deleted > 0) {
                    Toast.makeText(mApplicationContext, getString(R.string.db_delete_success), Toast.LENGTH_SHORT).show();
                    setFavoriteButtonBehaviour(false);
                } else {
                    Toast.makeText(mApplicationContext, getString(R.string.db_delete_error), Toast.LENGTH_SHORT).show();
                    setFavoriteButtonBehaviour(true);
                }
            }
        }.run();
    }

    private void loadOfflineData() {
        getSupportLoaderManager().initLoader(GET_OFFLINE_DATA_LOADER_TASK_ID, null, MovieDetailActivity.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mCursor;

            @Override
            protected void onStartLoading() {
                if (null != mCursor) {
                    deliverResult(mCursor);
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                try {
                    Uri uri = FavoriteMoviesEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(String.valueOf(mMovieId)).build();
                    return getContentResolver().query(
                            uri,
                            FAVORITE_MOVIES_PROJECTION,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable Cursor data) {
                mCursor = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case OFFLINE_AVAILABILITY_CHECK_LOADER_TASK_ID:
                setFavoriteButtonBehaviour((data != null && data.getCount() > 0));
                break;
            case GET_OFFLINE_DATA_LOADER_TASK_ID:
                bindDataToViews(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //No use so far
    }

    private void setFavoriteButtonBehaviour(boolean availableOffline) {
        if (availableOffline) {
            mButtonFavorite.setText(getString(R.string.button_remove_favorite));
            mButtonFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFromDatabase();
                }
            });
        } else {
            mButtonFavorite.setText(getString(R.string.button_favorite));
            mButtonFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveToDatabase();
                }
            });
        }
    }
}
