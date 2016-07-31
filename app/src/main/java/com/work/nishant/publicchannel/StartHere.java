package com.work.nishant.publicchannel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@SuppressWarnings("deprecation")
public class StartHere extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView na,sap,roll,bch,em;
    ImageView im;
    SharedPreferences sp,co;
    SharedPreferences.Editor ed,eco;
    TabHost tb;
    TabHost.TabSpec tab1,tab2,tab3,tab4;

    String imageUri=null;
    Bitmap bm = null;

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    //change here as version changes

    int versionControl=2;

    int versionReceived=0;
    int versionMsg=0;

    String updateMsg="";

    boolean executedOnce=false;

    //change here as version changes

    TabLayout tabLayout;
    ViewPager viewPager;

    ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_here);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(config);

        Toolbar toolbars = (Toolbar) findViewById(R.id.tab_anim);
        setSupportActionBar(toolbars);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager(),getApplicationContext()));

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        //internetConnection();


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });


        na = (TextView) findViewById(R.id.textViewName);
        sap = (TextView) findViewById(R.id.textViewSapId);
        roll = (TextView) findViewById(R.id.textViewRollNo);
        bch = (TextView) findViewById(R.id.textViewBranch);
        em = (TextView) findViewById(R.id.textViewEmail);
        im = (ImageView) findViewById(R.id.imageViewSlider);

        sp = getApplicationContext().getSharedPreferences("shared", 0);
        ed = sp.edit();

        co = getApplicationContext().getSharedPreferences("cortas", 0);
        eco = co.edit();


        if(sp.getString("facebook","no").contentEquals("yes")) {
            imageUri = sp.getString("imageUri", "null");
            //new Get().execute();

            /*
            https://graph.facebook.com/1061026247265354/picture?height=400&width=400&migration_overrides=%7Boctober_2012%3Atrue%7D
             */

            imageUri = imageUri.replace("height=400&width=400","height=100&width=100");
            imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(imageUri,im);

        }


        na.setText(sp.getString("name","N/A"));
        sap.setText(sp.getString("sapid","N/A"));
        roll.setText(sp.getString("roll","N/A"));
        bch.setText(sp.getString("branch","N/A"));
        em.setText(sp.getString("email","N/A"));



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbars, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Work here for all clicks

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_editProfile) {

            Intent it = new Intent(StartHere.this,Profile.class);
            startActivity(it);

        } else if (id == R.id.nav_fb) {

            sp = getApplicationContext().getSharedPreferences("shared", 0);
            ed = sp.edit();

            if(sp.getString("facebook","no").contentEquals("yes"))
            {
                Intent it = new Intent(StartHere.this,FbDone.class);
                startActivity(it);
            }else{

                Intent it = new Intent(StartHere.this,FbLogin.class);
                startActivity(it);
            }

        } else if (id == R.id.nav_Skill) {
            Intent it = new Intent(StartHere.this,SkillPost.class);
            startActivity(it);

        } else if (id == R.id.nav_developer) {
            Intent it = new Intent(StartHere.this,AboutMe.class);
            startActivity(it);

        }  else if (id == R.id.nav_Attendance) {

            Intent it = new Intent(StartHere.this,Attendance.class);
            startActivity(it);

        } else if (id == R.id.nav_share) {

            try
            { Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Public Channel");
                String sAux = "\nAn android app for UPES students to check their attendance, timetable and search other students\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.work.nishant.publicchannel \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            }
            catch(Exception e)
            { //e.toString();
            }

        } else if(id==R.id.nav_logout)
        {
            ed.clear();
            ed.commit();

            eco.clear();
            eco.commit();

            Intent it = new Intent(StartHere.this, MainActivity.class);
            startActivity(it);
            finish();
        }
        else if (id == R.id.nav_opensource) {

            WebView view = (WebView) LayoutInflater.from(this).inflate(R.layout.license, null);
            view.loadUrl("file:///android_asset/open.html");
            view.getSettings().setUseWideViewPort(true);
            view.getSettings().setBuiltInZoomControls(true);
            AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                    .setTitle("Open Source License")
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog alert = mAlertDialog.create();
            alert.show();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    class Get extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            if(imageUri==null || imageUri.isEmpty())
            {
                //default pic
            }
            else if(bm!=null)
                im.setImageBitmap(bm);


        }


    }

    private class CustomAdapter extends FragmentPagerAdapter {

        private String fragments[] = {"Cortas","Search","Skill Search","Most Searched Students"};

        public CustomAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return new oneCortas();
                case 1:
                    return new oneSearch();
                case 2:
                    return new oneSkill();
                case 3:
                    return new oneMost();
                default:
                    return  null;
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position];
        }
    }

}
