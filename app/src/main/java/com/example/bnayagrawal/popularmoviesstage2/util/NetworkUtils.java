package com.example.bnayagrawal.popularmoviesstage2.util;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bnayagrawal.popularmoviesstage2.BuildConfig;

import org.json.JSONObject;

/**
 * Created by bnayagrawal on 2/23/2018.
 */

public final class NetworkUtils {
    //URLS as per theMovieDb
    private static final String TMDB_BASE_API_URL = "https://api.themoviedb.org/3/";
    //YouTube base url
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/";

    //Paths
    private static final String TMDB_CONFIGURATION_PATH = "configuration";
    private static final String TMDB_POPULAR_MOVIES_PATH = "movie/popular";
    private static final String TMDB_TOP_RATED_MOVIES_PATH = "movie/top_rated";

    private static final String TMDB_MOVIE_PATH = "movie";
    private static final String TMDB_VIDEOS_PATH = "videos";
    private static final String TMDB_REVIEWS_PATH = "reviews";

    //YouTube video path
    private static final String YOUTUBE_VIDEO_PATH = "watch";

    //Params
    private static final String PARAM_API_KEY = "api_key";
    //YouTube search param
    private static final String PARAM_YOUTUBE_VIDEO_ID = "v";

    public static String buildMovieSelectionUrl(MovieSelection movieSelection) {
        Uri uri = Uri.parse(TMDB_BASE_API_URL + (
                (movieSelection == MovieSelection.POPULAR) ? TMDB_POPULAR_MOVIES_PATH : TMDB_TOP_RATED_MOVIES_PATH))
                .buildUpon()
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .build();
        return uri.toString();
    }

    public static String buildMovieInfoUrl(MovieInfo movieInfo,int movieId) {
        Uri uri;
        switch (movieInfo) {
            case DETAILS:
                uri = Uri.parse(TMDB_BASE_API_URL)
                        .buildUpon()
                        .appendPath(TMDB_MOVIE_PATH)
                        .appendPath(String.valueOf(movieId))
                        .appendQueryParameter(PARAM_API_KEY, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                        .build();
                break;
            case VIDEOS:
                uri = Uri.parse(TMDB_BASE_API_URL)
                        .buildUpon()
                        .appendPath(TMDB_MOVIE_PATH)
                        .appendPath(String.valueOf(movieId))
                        .appendPath(TMDB_VIDEOS_PATH)
                        .appendQueryParameter(PARAM_API_KEY, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                        .build();
                break;
            case REVIEWS:
                uri = Uri.parse(TMDB_BASE_API_URL)
                        .buildUpon()
                        .appendPath(TMDB_MOVIE_PATH)
                        .appendPath(String.valueOf(movieId))
                        .appendPath(TMDB_REVIEWS_PATH)
                        .appendQueryParameter(PARAM_API_KEY, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                        .build();
                break;
            default:
                throw new UnsupportedOperationException("Unknown Info");
        }
        return uri.toString();
    }

    public static String buildConfigUrl() {
        Uri uri = Uri.parse(TMDB_BASE_API_URL + TMDB_CONFIGURATION_PATH)
                .buildUpon()
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .build();
        return uri.toString();
    }

    public static String buildYoutubeVideoUrl(String videoId) {
        Uri uri = Uri.parse(YOUTUBE_BASE_URL + YOUTUBE_VIDEO_PATH)
                .buildUpon()
                .appendQueryParameter(PARAM_YOUTUBE_VIDEO_ID, videoId)
                .build();
        return uri.toString();
    }

    public enum MovieSelection {
        POPULAR,
        TOP_RATED
    }

    public enum MovieInfo {
        DETAILS,
        VIDEOS,
        REVIEWS
    }
}
