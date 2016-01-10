package com.yaroslav.translatednet_client;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import static com.yaroslav.translatednet_client.Constants.AUTHORITY;
import static com.yaroslav.translatednet_client.Constants.DB_NAME;
import static com.yaroslav.translatednet_client.Constants.DB_VERSION;
import static com.yaroslav.translatednet_client.Constants.LANG_FROM;
import static com.yaroslav.translatednet_client.Constants.LANG_TO;
import static com.yaroslav.translatednet_client.Constants.LOG_TAG;
import static com.yaroslav.translatednet_client.Constants.TRANSL_FROM;
import static com.yaroslav.translatednet_client.Constants.TRANSL_ID;
import static com.yaroslav.translatednet_client.Constants.TRANSL_PATH;
import static com.yaroslav.translatednet_client.Constants.TRANSL_TABLE;
import static com.yaroslav.translatednet_client.Constants.TRANSL_TO;

public class TranslateProvider extends ContentProvider {

    static final String DB_CREATE = "create table " + TRANSL_TABLE + "("
            + TRANSL_ID + " integer primary key autoincrement, "
            + TRANSL_FROM + " text, " +TRANSL_TO + " text, " + LANG_FROM + " text, " + LANG_TO + " text" + ");";

    public static final Uri TRANSL_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TRANSL_PATH );


    // I don't know for what this
    static final String TRANSL_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + TRANSL_PATH;

    // this too
    static final String TRANSL_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + TRANSL_PATH;


    // common Uri
    static final int URI_TRANSL = 1;

    // Uri with known ID
    static final int URI_TRANSL_ID = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,TRANSL_PATH,URI_TRANSL);
        uriMatcher.addURI(AUTHORITY,TRANSL_PATH + "/#",URI_TRANSL_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase database;

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_TRANSL:
                Log.d(LOG_TAG, "URI_CONTACTS");
                break;
            case URI_TRANSL_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = TRANSL_ID + " = " + id;
                } else {
                    selection = selection + " AND " + TRANSL_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        database = dbHelper.getWritableDatabase();
        int cnt = database.delete(TRANSL_TABLE, selection, selectionArgs);
        // informing ContentResolver, that data in resultUri have been changed
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(LOG_TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_TRANSL:
                return TRANSL_CONTENT_TYPE;
            case URI_TRANSL_ID:
                return TRANSL_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_TRANSL)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        database = dbHelper.getWritableDatabase();
        long rowID = database.insert(TRANSL_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(TRANSL_CONTENT_URI, rowID);
        // informing ContentResolver, that data in resultUri have been changed
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        Log.d(LOG_TAG,"db created");
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)){
            case URI_TRANSL://common Uri
                Log.d(LOG_TAG,"URI for all");
                //if no sort order, set our order
                if (TextUtils.isEmpty(sortOrder)){
                    sortOrder = TRANSL_FROM + " ASC";
                }
                break;
            case URI_TRANSL_ID://simgle Uri
                Log.d(LOG_TAG,"single Uri");
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = TRANSL_ID + " = " + id;
                } else {
                    selection = selection + " AND " + TRANSL_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        database = dbHelper.getWritableDatabase();
        //Cursor cursor = database.query(TRANSL_TABLE, projection, selection,
                //selectionArgs, null, null, sortOrder);
        Cursor cursor = database.query(TRANSL_TABLE, projection, selection,
                selectionArgs, null, null, sortOrder);


        // set notification for this cursor, if data were changed in CONTACT_CONTENT_URI
        cursor.setNotificationUri(getContext().getContentResolver(),
                TRANSL_CONTENT_URI);

        return cursor;


    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(LOG_TAG, "update, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_TRANSL:
                Log.d(LOG_TAG, "URI_TRANSL");

                break;
            case URI_TRANSL_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_TRANSL_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = TRANSL_ID + " = " + id;
                } else {
                    selection = selection + " AND " + TRANSL_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        database = dbHelper.getWritableDatabase();
        int cnt = database.update(TRANSL_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
