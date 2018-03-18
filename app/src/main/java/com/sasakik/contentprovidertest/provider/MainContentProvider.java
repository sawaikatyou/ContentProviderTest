package com.sasakik.contentprovidertest.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.sasakik.contentprovidertest.provider.DBConstants.TABLE;
import com.sasakik.contentprovidertest.provider.DBConstants.Columns;
import com.sasakik.contentprovidertest.settings.Setting.DBGSetting;

import java.io.FileNotFoundException;

/**
 * Created by nine_eyes on 2018/03/18.
 */
public class MainContentProvider extends ContentProvider {
    private static final String TAG = "MainContentProvider";

    public static CSDBOpenHelper createInMemoryDB(Context context) {
        return new CSDBOpenHelper(context, null, null);
    }

    /**
     * 専用DBの操作ヘルパークラス
     */
    public static class CSDBOpenHelper extends SQLiteOpenHelper {
        private Context mContext;
        private MainContentProvider mParent;

        public CSDBOpenHelper(Context context, MainContentProvider parent) {
            super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION, null);
            mContext = context;
            mParent = parent;
        }

        public CSDBOpenHelper(Context context, String name, MainContentProvider parent) {
            super(context, name, null, DBConstants.DATABASE_VERSION, null);
            mContext = context;
            mParent = parent;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE.IMAGES.createTableSQLStatement());
            db.execSQL(TABLE.DISHES.createTableSQLStatement());
            db.execSQL(TABLE.AREAS.createTableSQLStatement());
            db.execSQL(TABLE.IMAGES_TO_TAGS.createTableSQLStatement());
            db.execSQL(TABLE.TAGS.createTableSQLStatement());
            if (mParent != null) mParent.insertPresetsAndTrigger(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // no-op
        }
    }

    void insertPresetsAndTrigger(SQLiteDatabase db) {
        db.execSQL("INSERT INTO TAGS(label, description) VALUES('breakfast', 'breakfast');");
        db.execSQL("INSERT INTO TAGS(label, description) VALUES('lunch', 'lunch');");
        db.execSQL("INSERT INTO TAGS(label, description) VALUES('dinner', 'dinner');");
// -----------------------------------------------------------------------
// 紐ついているすべての IMAGES が消えたら TAGS のエントリも消すトリガー
// 現時点では朝食／昼食／夕食のカテゴリしか TAGS に存在しないのでトリガーを使うまでもない
// -----------------------------------------------------------------------
//        db.execSQL("create trigger delete_tags delete on " + TABLE.IMAGES.toString() + " " +
//                "begin" +
//                "  delete from " + TABLE.IMAGES_TO_TAGS.toString() + " where " + Columns._IMAGES_ID + " = old._id;" +
//                "end;");
    }

    /**
     * SQL操作時の構文をひとまとめにするための抽象クラス
     */
    static class SqlArguments {
        String table;
        public final String where;
        public final String[] args;

        SqlArguments(Uri url, String where, String[] args) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = where;
                this.args = args;
            } else if (url.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + url);
            } else if (!TextUtils.isEmpty(where)) {
                throw new UnsupportedOperationException("WHERE clause not supported: " + url);
            } else {
                this.table = url.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(url);
                this.args = null;
            }
        }

        SqlArguments(Uri url) {
            if (url.getPathSegments().size() == 1) {
                table = url.getPathSegments().get(0);
                where = null;
                args = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }

    CSDBOpenHelper mOpenHelper;

    public MainContentProvider() {
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        if (DBGSetting.FORCE_INMEMORYDB) {
            mOpenHelper = new CSDBOpenHelper(context, null, this);
        } else {
            mOpenHelper = new CSDBOpenHelper(context, this);
        }
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.delete(args.table, args.where, args.args);
        if (count > 0) sendNotify(uri);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        SqlArguments args = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        } else {
            return "vnd.android.cursor.item/" + args.table;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        Log.d(TAG, "start insert()");
        Log.d(TAG, "uri scheme = " + uri.getScheme());
        SqlArguments args = new SqlArguments(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final long rowId = dbInsertAndCheck(mOpenHelper, db, args.table, null, initialValues);
        if (rowId <= 0) return null;

        uri = ContentUris.withAppendedId(uri, rowId);
        sendNotify(uri);

        return uri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SqlArguments args = new SqlArguments(uri);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            int numValues = values.length;
            for (int i = 0; i < numValues; i++) {
                if (dbInsertAndCheck(mOpenHelper, db, args.table, null, values[i]) < 0) {
                    return 0;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        sendNotify(uri);
        return values.length;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        if (args.table != null && args.table.equals("IMAGES_AND_TAGS")) {
            StringBuilder builder = new StringBuilder();
            String img = TABLE.IMAGES.toString();
            String i2t = TABLE.IMAGES_TO_TAGS.toString();

            builder.append(img + " INNER JOIN " + i2t + " ");
            builder.append("ON " + img + "." + Columns._ID + " = " + i2t + "." + Columns._IMAGES_ID);
            String images_id = TABLE.IMAGES.toString() + "." + DBConstants.Columns._ID;
            args.table = builder.toString();
        }

        qb.setTables(args.table);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor result = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
        result.setNotificationUri(getContext().getContentResolver(), uri);

        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.update(args.table, values, args.where, args.args);
        if (count > 0) sendNotify(uri);

        return count;
    }

    @Nullable
    @Override
    public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
        return super.getStreamTypes(uri, mimeTypeFilter);
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        return openFileHelper(uri, mode);
    }


    public void setOpenHelper(CSDBOpenHelper newvalue) {
        mOpenHelper = newvalue;
    }

    long dbInsertAndCheck(CSDBOpenHelper helper,
                          SQLiteDatabase db, String table, String nullColumnHack, ContentValues values) {
        if (values == null) {
            throw new RuntimeException("Error: attempting to insert null values");
        }
        if (table != null && table.equals("_INITDATAMAKE")) {
            db.execSQL("INSERT INTO TAGS(_id, label, description) VALUES(0, 'breakfast', 'breakfast');");
            db.execSQL("INSERT INTO TAGS(_id, label, description) VALUES(1, 'lunch', 'lunch');");
            db.execSQL("INSERT INTO TAGS(_id, label, description) VALUES(2, 'dinner', 'dinner');");
            return 1;
        }

        return db.insert(table, nullColumnHack, values);
    }

    void sendNotify(Uri uri) {
        String notify = uri.getQueryParameter(DBConstants.PARAMETER_NOTIFY);
        if (notify == null || "true".equals(notify)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

//        if (mListener != null) {
//            mListener.onLauncherProviderChange();
//        }
    }


}
