package com.sasakik.contentprovidertest;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.sasakik.contentprovidertest", appContext.getPackageName());
    }

    @Test
    public void testAssets() {
        Context appcontext = InstrumentationRegistry.getTargetContext();
        outTextAll(appcontext, "readme_2.txt");
    }

    public void outTextAll(Context context, String assets_filename) {
        try {
            InputStream is = context.getAssets().open(assets_filename);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br;
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) { //１行ごとに読み込む
                Log.i("Sasaki", line);
            }
            br.close();
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }
}
