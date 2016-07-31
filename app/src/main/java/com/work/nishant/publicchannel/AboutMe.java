package com.work.nishant.publicchannel;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Nishant on 1/13/2016.
 */
public class AboutMe extends AppCompatActivity {

    ImageLoader imageLoader;
    Button bt,btHide;
    ImageView im;
    TextView tv;
    TableRow tr;

    String name="";

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutme);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(config);

        sp = getApplicationContext().getSharedPreferences("shared", 0);
        ed = sp.edit();

        bt = (Button) findViewById(R.id.buttonAboutProfile);
        btHide = (Button) findViewById(R.id.buttonAboutProfileHide);
        im = (ImageView) findViewById(R.id.imageViewAbout);
        tv = (TextView) findViewById(R.id.textViewMe);
        tr = (TableRow) findViewById(R.id.tableRowHide);


        name = sp.getString("name","there!");

        String putIt = "<PUT YOUR INFO HERE>";

        tv.setText(Html.fromHtml(putIt));

        im.setVisibility(View.GONE);
        btHide.setVisibility(View.GONE);

        tr.setVisibility(View.GONE);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tr.setVisibility(View.VISIBLE);
                bt.setVisibility(View.GONE);
                btHide.setVisibility(View.VISIBLE);
                im.setVisibility(View.VISIBLE);
                imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage("<LINK OF YOUR FB PROFILE IMAGE>", im);

            }
        });


        btHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tr.setVisibility(View.GONE);
                bt.setVisibility(View.VISIBLE);
                btHide.setVisibility(View.GONE);
                im.setVisibility(View.GONE);
            }
        });
    }
}
