package com.work.nishant.publicchannel;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.webkit.WebView;

/**
 * Created by Nishant on 1/11/2016.
 */
public class Open extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.license);
        WebView wv;
        wv = (WebView) findViewById(R.id.webView);
        wv.loadUrl("file:///android_asset/open.html");

    }
}
