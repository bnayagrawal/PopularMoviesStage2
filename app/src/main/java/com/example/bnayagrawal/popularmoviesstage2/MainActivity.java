package com.example.bnayagrawal.popularmoviesstage2;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bnayagrawal.popularmoviesstage2.content.Configuration;
import com.example.bnayagrawal.popularmoviesstage2.content.Movie;
import com.example.bnayagrawal.popularmoviesstage2.data.FavoriteMoviesContract;
import com.example.bnayagrawal.popularmoviesstage2.util.NetworkUtils;
import com.example.bnayagrawal.popularmoviesstage2.util.TheMovieDbJsonUtils;
import com.example.bnayagrawal.popularmoviesstage2.util.VolleyNetworkHandler;

import org.json.JSONObject;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String INS_TAG_MOVIES = "MOVIES";
    private static final String INS_TAG_CONFIGURATION = "CONFIGURATION";
    private static final String INS_TAG_CHECKED_RADIO_BUTTON = "CHECKED_RADIO_BUTTON";

    public static final String[] FAVORITE_MOVIES_PROJECTION = {
            FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID,
            FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_POSTER_PATH,
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_POSTER_PATH = 1;

    private static final String VOLLEY_REQUEST_TAG = "volley-request-tag";

    public static String sBaseImageUrl;

    private Context mApplicationContext;
    private Configuration mConfiguration;

    private ArrayList<Movie> mMovies;
    private MovieArrayAdapter mMovieArrayAdapter;

    private int mCheckedRadioButtonId;
    private Animation mFadeInAnimation, mFadeOutAnimation;

    @BindView(R.id.grid_view_movies)
    GridView mGridView;
    @BindView(R.id.layout_progress_view)
    LinearLayout mLayoutProgressView;
    @BindView(R.id.layout_error_view)
    LinearLayout mLayoutErrorView;

    @BindView(R.id.tv_error_message)
    TextView mTextViewErrorMessage;
    @BindView(R.id.btn_retry)
    Button mButtonRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mApplicationContext = getApplicationContext();
        mFadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
        mFadeOutAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);

        if (null != savedInstanceState) {
            mMovies = savedInstanceState.getParcelableArrayList(INS_TAG_MOVIES);
            mConfiguration = savedInstanceState.getParcelable(INS_TAG_CONFIGURATION);
            mCheckedRadioButtonId = savedInstanceState.getInt(INS_TAG_CHECKED_RADIO_BUTTON);
        } else {
            mMovies = new ArrayList<>();
            mCheckedRadioButtonId = R.id.popular_movies;
            String url = NetworkUtils.buildMovieSelectionUrl(NetworkUtils.MovieSelection.POPULAR);
            fetchMovies(url);
        }

        mMovieArrayAdapter = new MovieArrayAdapter(this, mMovies);
        mGridView.setAdapter(mMovieArrayAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(INS_TAG_MOVIES, mMovies);
        outState.putParcelable(INS_TAG_CONFIGURATION, mConfiguration);
        outState.putInt(INS_TAG_CHECKED_RADIO_BUTTON, mCheckedRadioButtonId);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(mCheckedRadioButtonId).setChecked(true);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //update favorite_movie list
        if(mCheckedRadioButtonId == R.id.favorite_movies) {
            loadFavoriteMovies();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        VolleyNetworkHandler.getInstance(mApplicationContext).getRequestQueue().cancelAll(VOLLEY_REQUEST_TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mCheckedRadioButtonId = item.getItemId();
        switch (item.getItemId()) {
            case R.id.popular_movies:
                if (item.isChecked())
                    item.setChecked(false);
                else {
                    item.setChecked(true);
                    VolleyNetworkHandler.getInstance(mApplicationContext).getRequestQueue().cancelAll(VOLLEY_REQUEST_TAG);
                    fetchMovies(NetworkUtils.buildMovieSelectionUrl(NetworkUtils.MovieSelection.POPULAR));
                }
            case R.id.top_rated_movies:
                if (item.isChecked())
                    item.setChecked(false);
                else {
                    item.setChecked(true);
                    VolleyNetworkHandler.getInstance(mApplicationContext).getRequestQueue().cancelAll(VOLLEY_REQUEST_TAG);
                    fetchMovies(NetworkUtils.buildMovieSelectionUrl(NetworkUtils.MovieSelection.TOP_RATED));
                }
            case R.id.favorite_movies:
                if (item.isChecked())
                    item.setChecked(false);
                else {
                    item.setChecked(true);
                    VolleyNetworkHandler.getInstance(mApplicationContext).getRequestQueue().cancelAll(VOLLEY_REQUEST_TAG);
                    loadFavoriteMovies();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void fetchMovies(final String url) {
        hiderErrorView();
        toggleProgressView(true);
        if (!isOnline()) {
            showErrorView(getString(R.string.network_error));
            setRetryButtonAction(url);
            return;
        } else if (null == mConfiguration) {
            fetchConfiguration();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mMovies = TheMovieDbJsonUtils.getMovieListFromJson(response, mConfiguration.getBaseUrl(),getString(R.string.movie_poster_size));
                        mMovieArrayAdapter.clear();
                        mMovieArrayAdapter.addAll(mMovies);
                        mMovieArrayAdapter.notifyDataSetChanged();
                        toggleProgressView(false);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        toggleProgressView(false);
                        showErrorView(error.getMessage());
                        setRetryButtonAction(url);
                        error.printStackTrace();
                    }
                });

        jsObjRequest.setTag(VOLLEY_REQUEST_TAG);
        VolleyNetworkHandler.getInstance(mApplicationContext).addToRequestQueue(jsObjRequest);
    }

    private void fetchConfiguration() {
        hiderErrorView();
        if (!isOnline()) {
            showErrorView(getString(R.string.network_error));
            setRetryButtonAction(null);
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, NetworkUtils.buildConfigUrl(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mConfiguration = TheMovieDbJsonUtils.getConfigurationFromJson(response);
                        sBaseImageUrl = mConfiguration.getBaseUrl();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showErrorView(error.getMessage());
                        setRetryButtonAction(null);
                        error.printStackTrace();
                    }
                });

        VolleyNetworkHandler.getInstance(mApplicationContext).addToRequestQueue(jsObjRequest);
    }

    private void loadFavoriteMovies() {
        hiderErrorView();
        new Runnable() {
            @Override
            public void run() {
                toggleProgressView(true);
                try {
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = contentResolver.query(
                            FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI,
                            FAVORITE_MOVIES_PROJECTION,
                            null,
                            null,
                            null
                    );

                    if (null == cursor || cursor.getCount() == 0) {
                        Toast.makeText(MainActivity.this, getString(R.string.empty_database), Toast.LENGTH_LONG).show();
                        toggleProgressView(false);
                        mMovies.clear();
                        mMovieArrayAdapter.clear();
                        mMovieArrayAdapter.notifyDataSetChanged();
                        return;
                    }

                    ArrayList<Movie> movies = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        movies.add(new Movie(
                                cursor.getInt(INDEX_MOVIE_ID),
                                cursor.getString(INDEX_MOVIE_POSTER_PATH)
                        ));
                    }

                    cursor.close();
                    mMovieArrayAdapter.clear();
                    mMovieArrayAdapter.addAll(movies);
                    mMovieArrayAdapter.notifyDataSetChanged();
                    toggleProgressView(false);

                } catch (Exception ex) {
                    toggleProgressView(false);
                    showErrorView(ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }.run();
    }

    private void toggleProgressView(boolean show) {
        if (show) {
            //Hide movies
            mGridView.setVisibility(View.GONE);
            mGridView.startAnimation(mFadeOutAnimation);
            //Show loading
            mLayoutProgressView.setVisibility(View.VISIBLE);
            mLayoutProgressView.startAnimation(mFadeInAnimation);
        } else {
            //Hide loading
            mLayoutProgressView.setVisibility(View.GONE);
            mLayoutProgressView.startAnimation(mFadeOutAnimation);
            //Show movies
            mGridView.setVisibility(View.VISIBLE);
            mGridView.startAnimation(mFadeInAnimation);
        }
    }

    private void showErrorView(String message) {
        //Hide movies
        mGridView.setVisibility(View.GONE);
        mGridView.startAnimation(mFadeOutAnimation);
        //Hide progressView
        if(mLayoutProgressView.getVisibility() == View.VISIBLE) {
            mLayoutProgressView.setVisibility(View.GONE);
            mLayoutProgressView.startAnimation(mFadeOutAnimation);
        }
        //Set error message
        mTextViewErrorMessage.setText(message);
        //show error view
        mLayoutErrorView.setVisibility(View.VISIBLE);
        mLayoutErrorView.startAnimation(mFadeInAnimation);
    }

    private void hiderErrorView() {
        //Hide error view
        mLayoutErrorView.setVisibility(View.GONE);
        mLayoutErrorView.startAnimation(mFadeOutAnimation);
        //Hide progressView
        if(mLayoutProgressView.getVisibility() == View.VISIBLE) {
            mLayoutProgressView.setVisibility(View.GONE);
            mLayoutProgressView.startAnimation(mFadeOutAnimation);
        }
        //Show movies
        mGridView.setVisibility(View.VISIBLE);
        mGridView.startAnimation(mFadeInAnimation);
    }

    private void setRetryButtonAction(final String url) {
        mButtonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null == url)
                    fetchConfiguration();
                else
                    fetchMovies(url);
            }
        });
    }
}
