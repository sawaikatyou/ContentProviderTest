package com.sasakik.contentprovidertest;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.test.mock.MockContentResolver;

import com.sasakik.contentprovidertest.provider.DBConstants;
import com.sasakik.contentprovidertest.provider.MainContentProvider;


/**
 * Created by nine_eyes on 2016/06/05.
 */
public class MockContext extends android.test.mock.MockContext {

    Context mParent;
    public MockContentResolver mCSMockContentResolver;

    public MockContext(Context context) {
        mParent = context;
        mCSMockContentResolver = new MockContentResolver(context);
        ContentProviderClient cpc = mCSMockContentResolver.acquireContentProviderClient(DBConstants.URI_IMAGES);
        ContentProvider cp = cpc.getLocalContentProvider();
        if (cp instanceof MainContentProvider) {
            MainContentProvider target_cp = (MainContentProvider) cp;
            target_cp.setOpenHelper(MainContentProvider.createInMemoryDB(context));
        }
    }

    @Override
    public ContentResolver getContentResolver() {
        return mCSMockContentResolver;
        // return super.getContentResolver();
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String file, int mode,
                                               SQLiteDatabase.CursorFactory factory) {
        return mParent.openOrCreateDatabase(file, mode, factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String file, int mode,
                                               SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return mParent.openOrCreateDatabase(file, mode, factory, errorHandler);
    }

}
