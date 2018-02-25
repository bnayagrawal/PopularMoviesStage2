package com.example.bnayagrawal.popularmoviesstage2.util;

import android.util.Log;

import com.example.bnayagrawal.popularmoviesstage2.content.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bnayagrawal on 2/23/2018.
 */

public final class TheMovieDbJsonUtils {
    private static final String DEBUG_TAG = TheMovieDbJsonUtils.class.getName();

    //result attribute
    private static final String RESULTS = "results";

    //movie attributes
    private static final String MOVIE_ID = "id";
    private static final String MOVIE_TITLE = "title";
    private static final String MOVIE_VOTE_AVERAGE = "vote_average";
    private static final String MOVIE_POSTER_PATH = "poster_path";
    private static final String MOVIE_OVERVIEW = "overview";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MOVIE_RUNTIME = "runtime";
    private static final String MOVIE_BACKDROP_PATH = "backdrop_path";

    //Review attributes
    private static final String REVIEW_ID = "id";
    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_CONTENT = "content";
    private static final String REVIEW_URL = "url";

    //Video attributes
    private static final String VIDEO_ID = "id";
    private static final String VIDEO_KEY = "key";
    private static final String VIDEO_NAME = "name";
    private static final String VIDEO_SITE = "site";
    private static final String VIDEO_SIZE = "size";
    private static final String VIDEO_TYPE = "type";

    //Configuration attributes
    private static final String CONFIG_IMAGES = "images";
    private static final String CONFIG_BASE_URL = "base_url";
    private static final String CONFIG_SECURE_BASE_URL = "secure_base_url";
    private static final String CONFIG_BACKDROP_SIZES = "backdrop_sizes";
    private static final String CONFIG_LOGO_SIZES = "logo_sizes";
    private static final String CONFIG_POSTER_SIZES = "poster_sizes";
    private static final String CONFIG_PROFILE_SIZES = "profile_sizes";
    private static final String CONFIG_STILL_SIZES = "still_sizes";

    public static Configuration getConfigurationFromJson(JSONObject jsonObject) {
        Configuration configuration;
        try {
            String base_url, secure_base_url;
            HashMap<String, String[]> size_map = new HashMap<>();
            String[] size_names = {CONFIG_BACKDROP_SIZES, CONFIG_LOGO_SIZES, CONFIG_POSTER_SIZES, CONFIG_PROFILE_SIZES, CONFIG_STILL_SIZES};

            JSONObject images = jsonObject.getJSONObject(CONFIG_IMAGES);
            base_url = images.getString(CONFIG_BASE_URL);
            secure_base_url = images.getString(CONFIG_SECURE_BASE_URL);

            String[] sizes;
            JSONArray array;
            for (String sizeName : size_names) {
                array = images.getJSONArray(sizeName);
                sizes = new String[array.length()];
                for (int j = 0; j < array.length(); j++)
                    sizes[j] = array.getString(j);
                size_map.put(sizeName, sizes);
            }

            configuration = new Configuration(base_url,
                    secure_base_url,
                    size_map.get(CONFIG_BACKDROP_SIZES),
                    size_map.get(CONFIG_LOGO_SIZES),
                    size_map.get(CONFIG_POSTER_SIZES),
                    size_map.get(CONFIG_PROFILE_SIZES),
                    size_map.get(CONFIG_STILL_SIZES)
            );
        } catch (JSONException jsonException) {
            configuration = null;
            Log.d(DEBUG_TAG, jsonException.getMessage());
            jsonException.printStackTrace();
        }
        return configuration;
    }

    public static ArrayList<Movie> getMovieListFromJson(JSONObject jsonObject, String baseImageUrl, String posterSize) {
        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONArray results = jsonObject.getJSONArray(RESULTS);
            int id;
            String poster_path;
            JSONObject item;

            for (int i = 0; i < results.length(); i++) {
                item = results.getJSONObject(i);
                id = item.getInt(MOVIE_ID);

                //build poster path
                poster_path = baseImageUrl + posterSize + item.getString(MOVIE_POSTER_PATH);

                movies.add(new Movie(
                        id, poster_path
                ));
            }
        } catch (JSONException jsonException) {
            movies = null;
            Log.d(DEBUG_TAG, jsonException.getMessage());
            jsonException.printStackTrace();
        }
        return movies;
    }

    public static MovieDetails getMovieDetailsFromJson(JSONObject jsonObject, String baseImageUrl, String posterSize, String backdropSize) {
        MovieDetails movieDetails;
        try {
            int id, runtime;
            double voteAverage;
            String title, posterPath, overview, releaseDate, backdropPath;

            id = jsonObject.getInt(MOVIE_ID);
            title = jsonObject.getString(MOVIE_TITLE);
            posterPath = baseImageUrl + posterSize + jsonObject.getString(MOVIE_POSTER_PATH);
            overview = jsonObject.getString(MOVIE_OVERVIEW);
            voteAverage = jsonObject.getDouble(MOVIE_VOTE_AVERAGE);
            releaseDate = jsonObject.getString(MOVIE_RELEASE_DATE);
            runtime = jsonObject.getInt(MOVIE_RUNTIME);
            backdropPath = baseImageUrl + backdropSize + jsonObject.getString(MOVIE_BACKDROP_PATH);

            movieDetails = new MovieDetails(
                    id, title, posterPath, overview, voteAverage, releaseDate, runtime, backdropPath
            );

        } catch (JSONException jsonException) {
            movieDetails = null;
            Log.d(DEBUG_TAG, jsonException.getMessage());
            jsonException.printStackTrace();
        }
        return movieDetails;
    }

    public static ArrayList<Video> getVideoListFromJson(JSONObject jsonObject) {
        ArrayList<Video> videos = new ArrayList<>();
        try {
            JSONArray results = jsonObject.getJSONArray(RESULTS);
            String id, key, name, site, type;
            int size;
            JSONObject item;

            for (int i = 0; i < results.length(); i++) {
                item = results.getJSONObject(i);
                id = item.getString(VIDEO_ID);
                key = item.getString(VIDEO_KEY);
                name = item.getString(VIDEO_NAME);
                site = item.getString(VIDEO_SITE);
                size = item.getInt(VIDEO_SIZE);
                type = item.getString(VIDEO_TYPE);

                videos.add(new Video(id, key, name, site, size, type));
            }
        } catch (JSONException jsonException) {
            videos = null;
            Log.d(DEBUG_TAG, jsonException.getMessage());
            jsonException.printStackTrace();
        }
        return videos;
    }

    public static ArrayList<Review> getReviewListFromJson(JSONObject jsonObject) {
        ArrayList<Review> reviews = new ArrayList<>();
        try {
            JSONArray results = jsonObject.getJSONArray(RESULTS);
            String id, author, content, url;
            JSONObject item;

            for (int i = 0; i < results.length(); i++) {
                item = results.getJSONObject(i);
                id = item.getString(REVIEW_ID);
                author = item.getString(REVIEW_AUTHOR);
                content = item.getString(REVIEW_CONTENT);
                url = item.getString(REVIEW_URL);

                reviews.add(new Review(id, author, content, url));
            }
        } catch (JSONException jsonException) {
            reviews = null;
            Log.d(DEBUG_TAG, jsonException.getMessage());
            jsonException.printStackTrace();
        }
        return reviews;
    }
}
