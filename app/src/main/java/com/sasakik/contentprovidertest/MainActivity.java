package com.sasakik.contentprovidertest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context appcontext = this;
        outTextAll(appcontext, "readme.txt");

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
            Log.i("Sasaki", "open miss");
            e.printStackTrace();
        }
    }
}
