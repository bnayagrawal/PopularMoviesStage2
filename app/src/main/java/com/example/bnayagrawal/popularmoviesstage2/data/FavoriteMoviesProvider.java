package com.example.bnayagrawal.popularmoviesstage2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.bnayagrawal.popularmoviesstage2.data.FavoriteMoviesContract.*;

/**
 * Created by bnayagrawal on 2/23/2018.
 */

public class FavoriteMoviesProvider extends ContentProvider {

    public static final int CODE_MOVIE = 101;
    public static final int CODE_MOVIE_WITH_ID = 102;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoriteMoviesDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoriteMoviesContract.CONTENT_AUTHORITY, FavoriteMoviesContract.PATH_FAVORITE_MOVIES, CODE_MOVIE);
        uriMatcher.addURI(FavoriteMoviesContract.CONTENT_AUTHORITY, FavoriteMoviesContract.PATH_FAVORITE_MOVIES + "/#", CODE_MOVIE_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FavoriteMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] s0, @Nullable String s1) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMoviesEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null
                );
                break;
            case CODE_MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String[] selectionArgs = new String[]{id};

                cursor = mOpenHelper.getReadableDatabase().query(
                        FavoriteMoviesEntry.TABLE_NAME,
                        projection,
                        FavoriteMoviesEntry.COLUMN_MOVIE_ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();

        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                long id = sqLiteDatabase.insert(FavoriteMoviesEntry.TABLE_NAME,null,contentValues);
                if(id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMoviesEntry.CONTENT_URI,id);
                } else {
                    throw new SQLiteException("Failed to insert movie record!");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase sqLiteDatabase = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                rowsDeleted = sqLiteDatabase.delete(FavoriteMoviesEntry.TABLE_NAME,FavoriteMoviesEntry.COLUMN_MOVIE_ID + "=?",new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
