package com.example.bnayagrawal.popularmoviesstage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.bnayagrawal.popularmoviesstage2.data.FavoriteMoviesContract.FavoriteMoviesEntry;

/**
 * Created by bnayagrawal on 2/23/2018.
 */

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorite_movies.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE =
                "CREATE TABLE " + FavoriteMoviesEntry.TABLE_NAME + " (" +
                        FavoriteMoviesEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY, " +
                        FavoriteMoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        FavoriteMoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                        FavoriteMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        FavoriteMoviesEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                        FavoriteMoviesEntry.COLUMN_POSTER_PATH + " TEXT, " +
                        FavoriteMoviesEntry.COLUMN_RUNTIME + " INT, " +
                        FavoriteMoviesEntry.COLUMN_BACKDROP_PATH + " TEXT" +
                        ");";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
