package com.sasakik.contentprovidertest;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;

import com.sasakik.contentprovidertest.provider.DBConstants;
import com.sasakik.contentprovidertest.provider.MainContentProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nine_eyes on 2016/06/05.
 */
public class TestUtilities {

    public static void setupInmemoryCSDB(Context context) {
        if (context != null) {
            ContentResolver cr = context.getContentResolver();
            ContentProviderClient cpc = cr.acquireContentProviderClient(DBConstants.TABLE.IMAGES.getUri());
            ContentProvider cp = cpc.getLocalContentProvider();
            if (cp instanceof MainContentProvider) {
                MainContentProvider target_cp = (MainContentProvider) cp;
                target_cp.setOpenHelper(MainContentProvider.createInMemoryDB(context));
            }
        }
    }


    public static byte[] getAssetsContents(Context context, String targetpath) {
        byte[] result = null;
        if (context != null && targetpath != null) {
            AssetManager am = context.getAssets();
            InputStream is = null;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte [] buffer = new byte[1024];
            try {
                is = am.open(targetpath);
                if(is != null) {
                    while(true) {
                        int len = is.read(buffer);
                        if(len < 0) {
                            break;
                        }
                        bout.write(buffer, 0, len);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            result =  bout.toByteArray();
        }

        return result;
    }
}
