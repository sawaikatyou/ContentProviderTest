package com.sasakik.contentprovidertest.provider;

import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.ProviderInfo;

import com.sasakik.contentprovidertest.provider.DBConstants;
import com.sasakik.contentprovidertest.provider.MainContentProvider;

/**
 * Created by nine_eyes on 2016/06/05.
 */
public class MyMockContentResolver extends android.test.mock.MockContentResolver {
    Context mParent;

    public MyMockContentResolver(Context context) {
        mParent = context;

        ContentProvider provider = new MainContentProvider();
        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.authority = DBConstants.AUTHORITY;
        providerInfo.enabled = true;
        providerInfo.packageName = MainContentProvider.class.getPackage().getName();
        provider.attachInfo(context, providerInfo);
        super.addProvider(DBConstants.AUTHORITY, provider);
    }
}
