package com.rahoria.nitin.poketassistant.Provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by nitin on 4/7/2016.
 */
public class MsgProvider extends ContentProvider{
    public static final String PROVIDER_NAME = "com.rahoria.nitin.poketassistant";
    public static final String URL = "content://" + PROVIDER_NAME + "/msg_scheduler";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String MSG_SCHEDULER_COLUMN_ID = "id";
    public static final String MSG_SCHEDULER_COLUMN_TYPE = "type";
    public static final String MSG_SCHEDULER_COLUMN_STATUS = "status";
    public static final String MSG_SCHEDULER_COLUMN_SENDING_TO_NAME ="sending_to_name";
    public static final String MSG_SCHEDULER_COLUMN_SENDING_TO = "sending_to";
    public static final String MSG_SCHEDULER_COLUMN_CC = "cc";
    public static final String MSG_SCHEDULER_COLUMN_BCC = "bcc";
    public static final String MSG_SCHEDULER_COLUMN_MESSAGE_SUBJECT = "message_subject";
    public static final String MSG_SCHEDULER_COLUMN_MESSAGE_BODY = "message_body";
    public static final String MSG_SCHEDULER_COLUMN_SENDING_AT = "sending_at";
    public static final String MSG_SCHEDULER_COLUMN_SENT_AT = "sent_at";
    public static final String MSG_SCHEDULER_COLUMN_CREATED_AT = "created_at";

    public static final int MSG_SCHEDULER_ID_INDEX = 0;
    public static final int MSG_SCHEDULER_TYPE_INDEX = 1;
    public static final int MSG_SCHEDULER_STATUS_INDEX = 2;
    public static final int MSG_SCHEDULER_SENDING_TO_INDEX = 3;
    public static final int MSG_SCHEDULER_SENDING_TO_NAME_INDEX = 4;
    public static final int MSG_SCHEDULER_CC_INDEX = 5;
    public static final int MSG_SCHEDULER_BCC_INDEX = 6;
    public static final int MSG_SCHEDULER_MESSAGE_SUBJECT_INDEX = 7;
    public static final int MSG_SCHEDULER_MESSAGE_BODY_INDEX = 8;
    public static final int MSG_SCHEDULER_SENDING_AT_INDEX = 9;
    public static final int MSG_SCHEDULER_SENT_AT_INDEX = 10;
    public static final int MSG_SCHEDULER_CREATED_AT_INDEX = 11;

    /* msg types */
    //public static final String MSG_SCHEDULER_TYPE_DRAFT = "Draft";
    public static final String MSG_SCHEDULER_TYPE_SMS = "SMS";
    public static final String MSG_SCHEDULER_TYPE_EMAIL = "Email";
    public static final String MSG_SCHEDULER_TYPE_WHATSAPP = "WhatsApp";

    /* msg status */
    public static final String MSG_STATUS_DRAFT = "Draft";
    public static final String MSG_STATUS_SENT = "Sent";
    public static final String MSG_STATUS_SENDING = "Sending";
    public static final String MSG_STATUS_FAILED = "Failed";
    public static final String MSG_STATUS_SEND_PENDING = "Pending";

    static final int SCHEDULED_MSGS = 1;
    static final int SCHEDULED_MSG_ID = 2;

    private static HashMap<String, String> MSGS_PROJECTION_MAP;


    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    public static final String DATABASE_NAME = "PoketAssistant.db";
    public static final String MSG_SCHEDULER_TABLE_NAME = "msg_scheduler";
    static final int DATABASE_VERSION = 2;
    static final String CREATE_DB_TABLE =
            "create table "+ MSG_SCHEDULER_TABLE_NAME +
                    "(" + MSG_SCHEDULER_COLUMN_ID + " integer primary key autoincrement, " +
                    MSG_SCHEDULER_COLUMN_TYPE + " text, " + MSG_SCHEDULER_COLUMN_STATUS + " text, " +
                    MSG_SCHEDULER_COLUMN_SENDING_TO + " text, " + MSG_SCHEDULER_COLUMN_SENDING_TO_NAME + " text, " +
                    MSG_SCHEDULER_COLUMN_CC + " text, " + MSG_SCHEDULER_COLUMN_BCC + " text," +
                    MSG_SCHEDULER_COLUMN_MESSAGE_SUBJECT + " text, " + MSG_SCHEDULER_COLUMN_MESSAGE_BODY + " text," +
                    MSG_SCHEDULER_COLUMN_SENDING_AT + " text, " + MSG_SCHEDULER_COLUMN_SENT_AT + " text," +
                    MSG_SCHEDULER_COLUMN_CREATED_AT + " text)";

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "msg_scheduler", SCHEDULED_MSGS);
        uriMatcher.addURI(PROVIDER_NAME, "msg_scheduler/#", SCHEDULED_MSG_ID);
    }


    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  MSG_SCHEDULER_TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MSG_SCHEDULER_TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case SCHEDULED_MSGS:
                qb.setProjectionMap(MSGS_PROJECTION_MAP);
                break;

            case SCHEDULED_MSG_ID:
                qb.appendWhere( MSG_SCHEDULER_COLUMN_ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on student names
             */
            sortOrder = MSG_SCHEDULER_COLUMN_ID;
        }
        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);

        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all scheduled msgs records
             */
            case SCHEDULED_MSGS:
                return "vnd.android.cursor.dir/vnd.example.msg_scheduler";

            /**
             * Get a particular scheduled msg
             */
            case SCHEDULED_MSG_ID:
                return "vnd.android.cursor.item/vnd.example.msg_scheduler";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new scheduled msg record
         */
        long rowID = db.insert(	MSG_SCHEDULER_TABLE_NAME, "", values);

        /**
         * If record is added successfully
         */

        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case SCHEDULED_MSGS:
                count = db.delete(MSG_SCHEDULER_TABLE_NAME, selection, selectionArgs);
                break;

            case SCHEDULED_MSG_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( MSG_SCHEDULER_TABLE_NAME, MSG_SCHEDULER_COLUMN_ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case SCHEDULED_MSGS:
                count = db.update(MSG_SCHEDULER_TABLE_NAME, values, selection, selectionArgs);
                break;

            case SCHEDULED_MSG_ID:
                count = db.update(MSG_SCHEDULER_TABLE_NAME, values, MSG_SCHEDULER_COLUMN_ID + " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
