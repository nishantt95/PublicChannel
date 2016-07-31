package com.work.nishant.publicchannel;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nishant on 1/24/2016.
 */
public class TimetableOffline extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Map<String, String> loginCookies;

    String keyArray[] = {};
    String valueArray[] = {};

    Document document,doc2,docTime;
    Connection.Response res,resTime;
    TextView tv,tvSync;

    ProgressDialog pd;

    Button mon,tue,wed,thu,fri,sat;
    String Mon="",Tue="",Wed="",Thu="",Fri="",Sat="";

    String lastSync="";


    Element getName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_offline);

        mon = (Button) findViewById(R.id.buttonMonOff);
        tue = (Button) findViewById(R.id.buttonTueOff);
        wed = (Button) findViewById(R.id.buttonWedOff);
        thu = (Button) findViewById(R.id.buttonThuOff);
        fri = (Button) findViewById(R.id.buttonFriOff);
        sat = (Button) findViewById(R.id.buttonSatOff);


        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) findViewById(R.id.adViewTimetableOff);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        //admob activity.................................................................................


        sp = getApplicationContext().getSharedPreferences("cortas", 0);
        ed = sp.edit();

        tv = (TextView) findViewById(R.id.textViewTimeOff);
        tvSync = (TextView) findViewById(R.id.textViewSyncTime);

        if(!(sp.contains("MondayTime")))
        {
            tv.setText("Please Sync your timetable at least once to enable offline feature.");
            tvSync.setText("Offline");

        }else{

            Mon = sp.getString("MondayTime","--No Data--");
            Tue = sp.getString("TuesdayTime","--No Data--");
            Wed = sp.getString("WednesdayTime","--No Data--");
            Thu = sp.getString("ThursdayTime","--No Data--");
            Fri = sp.getString("FridayTime","--No Data--");
            Sat = sp.getString("SaturdayTime","--No Data--");
            lastSync = sp.getString("lastSync","--No Data--");

            tvSync.setText("Offline [Last Sync - "+lastSync+"]");

        mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tv.setText(Html.fromHtml(Mon));

            }
        });

        tue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(Html.fromHtml(Tue));
            }
        });

        wed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(Html.fromHtml(Wed));
            }
        });

        thu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(Html.fromHtml(Thu));
            }
        });

        fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(Html.fromHtml(Fri));
            }
        });

        sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(Html.fromHtml(Sat));
            }
        });

        }

    }
}
