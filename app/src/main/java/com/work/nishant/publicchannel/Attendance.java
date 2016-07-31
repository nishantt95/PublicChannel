package com.work.nishant.publicchannel;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.work.nishant.publicchannel.R.id.attendanceXml;

/**
 * Created by Nishant on 1/7/2016.
 */
public class Attendance extends AppCompatActivity {

    Button bt,btLogout,btTime;
    TextView tvShow;
    Double c;
    String nameShow="";
    String data;
    String subject[]={};
    String classesHeld[] ={};
    String classesAttended[]={};
    String absentDates[]={};
    String attendance[]={};
    String projection[]={};

    String subject1="";
    String classesHeld1="";
    String classesAttended1="";
    String absentDates1="";
    String attendance1="";
    String projection1="";


    String totalPercentage="";
    String totalClassesHeld="";
    String totalClassesAttended="";
    String Username="";
    String projectionHere="";

    String keyArray[] = {};
    String valueArray[] = {};

    String batchSet="";

    Document document,doc2,docTime;
    Connection.Response res,resTime;

    ListView lv;
    AttendanceAdapter adpt;

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    ProgressDialog pd,pdRe1,pdRe2;
    TextView tvName,tvProjection,tvActualAtt;
    ProgressBar pbTotal;

    boolean fir = true;

    Map<String, String> loginCookies;


    //---------------ReConnect Attributes-------------------

    String unknown1="";
    String key="";
    String value="";

    Element element = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance);

        bt = (Button) findViewById(R.id.buttonRefreshAttendance);
        btLogout = (Button) findViewById(R.id.buttonLogoutAttendance);
        btTime = (Button) findViewById(R.id.buttontoTimetable);


        tvName = (TextView) findViewById(R.id.textViewNameAttendance);
        tvProjection = (TextView) findViewById(R.id.textViewAllAttendance);
        tvActualAtt = (TextView) findViewById(R.id.textViewTotalAttendance);
        pbTotal = (ProgressBar) findViewById(R.id.progressBarTotalAttedance);

        pbTotal.setVisibility(View.INVISIBLE);


        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) findViewById(R.id.adViewAttendance);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        //admob activity.................................................................................


        loginCookies = new HashMap<String,String>();

        sp = getApplicationContext().getSharedPreferences("cortas", 0);
        ed = sp.edit();

        batchSet = sp.getString("batchSet", "0");

        if(batchSet.equalsIgnoreCase("0"))
        {
            AlertDialog.Builder adb = new AlertDialog.Builder(Attendance.this);
            adb.setTitle("Please Wait!")
                    .setMessage("We have made batch wise separation and also the prediction of whole day.\n\nPlease Login again at cortas to use new features.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ed.clear();
                            ed.commit();
                            Intent it = new Intent(Attendance.this,Cortas.class);
                            startActivity(it);
                        }
                    })
                    .setCancelable(false);
            AlertDialog alertDialog = adb.create();
            alertDialog.show();
        }

        pdRe1 = new ProgressDialog(Attendance.this);
        pdRe1.setMessage("Session Expired!\nGenerating new session id...");
        pdRe1.dismiss();

        pdRe2 = new ProgressDialog(Attendance.this);
        pdRe2.setMessage("Please have patience\nThis could take a while...");
        pdRe2.dismiss();

        lv = (ListView) findViewById(R.id.listViewAttendance);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                int diff=0;

                diff = (subject[position].contains("Lab"))?(2):(1);

                double dp = (((Double.parseDouble(classesAttended[position].trim()))/(Double.parseDouble(classesHeld[position].trim())))*100);
                double d1 = (((Double.parseDouble(classesAttended[position].trim())+diff)/(Double.parseDouble(classesHeld[position].trim())+diff))*100);
                double d2 = (((Double.parseDouble(classesAttended[position].trim()))/(Double.parseDouble(classesHeld[position].trim())+diff))*100);

                String dps=""+dp;
                String d1s=""+d1;
                String d2s=""+d2;

                dps = (dps.length()>5)?(dps.substring(0,5)):(dps);
                d1s = (d1s.length()>5)?(d1s.substring(0,5)):(d1s);
                d2s = (d2s.length()>5)?(d2s.substring(0,5)):(d2s);


                AlertDialog.Builder adb = new AlertDialog.Builder(Attendance.this);
                adb.setTitle(subject[position])
                        .setCancelable(false)
                        .setPositiveButton("Get Details", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent it = new Intent(Attendance.this,Attendance_Detail.class);
                                it.putExtra("subject",subject[position]);
                                it.putExtra("classesHeld",classesHeld[position]);
                                it.putExtra("classesAttended",classesAttended[position]);
                                it.putExtra("attendance",attendance[position]);
                                startActivity(it);
                            }
                        })
                        .setNegativeButton("That's Fine", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setMessage(Html.fromHtml("<b>Current Attendance</b> : " + dps + "<br><br>On <font color='#1D900B'><b>Attending</b></font> next class : " + d1s + "<br><br>On <font color='#D8311E'><b>Leaving</b></font> next class : " + d2s));
                AlertDialog alert = adb.create();
                alert.show();
            }
        });




        pd = new ProgressDialog(Attendance.this);
        pd.setMessage("Synchronizing attendance...");
        pd.dismiss();

        Username = sp.getString("name","Unknown!");

        String cookieKey = sp.getString("cookieKey","nodata");
        String cookieValue = sp.getString("cookieValue","nodata");


        keyArray = cookieKey.split("---374587348---");
        valueArray = cookieValue.split("---374587348---");




        if(!(cookieKey.contentEquals("nodata")))
        {
            for(int i=0;i<keyArray.length;i++) {
                loginCookies.put(keyArray[i], valueArray[i]);
            }
        }

        Log.e("cookies provided-----",loginCookies.toString());


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetConnection()) {
                    new Logged().execute();
                }else{

                    Snackbar snackbar = Snackbar.make(v, Html.fromHtml("<b>No Internet Connection</b>"),Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                    snackbar.show();
                }
            }
        });

        LinearLayout ll = (LinearLayout) findViewById(R.id.attendanceXml);


        if(sp.contains("subject1"))
        {

            try {

                subject1 = sp.getString("subject1", "0");
                classesHeld1 = sp.getString("classesHeld1", "0");
                classesAttended1 = sp.getString("classesAttended1", "0");
                absentDates1 = sp.getString("absentDates1", "0");
                attendance1 = sp.getString("attendance1", "0");
                projection1 = sp.getString("projection1", "0");
                totalPercentage = sp.getString("totalPercentage1", "0");
                projectionHere = sp.getString("projectionHere1", "0");


                subject = subject1.split("-----84759843-----");
                classesHeld = classesHeld1.split("-----84759843-----");
                classesAttended = classesAttended1.split("-----84759843-----");
                absentDates = absentDates1.split("-----84759843-----");
                attendance = attendance1.split("-----84759843-----");
                projection = projection1.split("-----84759843-----");


                // use username for name


                try {
                    c = Double.parseDouble(totalPercentage);
                }catch (Exception e){

                        showError("Something went wrong!","Your data is not available on college website.\nComeback when your attendance gets live.");

                }

                int z = c.intValue();

                //TextView tvName,tvProjection,tvActualAtt;
                tvName.setText(Username);
                tvProjection.setText("Projected : " + projectionHere);

                if (z >= 75) {

                    pbTotal.getProgressDrawable().setColorFilter(Color.rgb(33, 160, 13), PorterDuff.Mode.SRC_IN);
                    tvActualAtt.setText(Html.fromHtml("Total Attendance : <font color='#21A00D'>" + totalPercentage + "%</font>"));
                } else if (z >= 67 && z < 75) {
                    pbTotal.getProgressDrawable().setColorFilter(Color.rgb(253, 101, 0), PorterDuff.Mode.SRC_IN);
                    tvActualAtt.setText(Html.fromHtml("Total Attendance : <font color='#FD6500'>" + totalPercentage + "%</font>"));
                } else {
                    pbTotal.getProgressDrawable().setColorFilter(Color.rgb(216, 49, 30), PorterDuff.Mode.SRC_IN);
                    tvActualAtt.setText(Html.fromHtml("Total Attendance : <font color='#D8311E'>" + totalPercentage + "%</font>"));
                }

                pbTotal.setVisibility(View.VISIBLE);

                ObjectAnimator animation = ObjectAnimator.ofInt(pbTotal, "progress", z);
                animation.setDuration(2000); // 2 sec
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();


                adpt = new AttendanceAdapter(Attendance.this, subject, classesHeld, classesAttended, absentDates, attendance, projection);

                lv.setAdapter(adpt);
            }catch (Exception e){
                showError("Something went wrong!","Your data is not available on college website.\nComeback when your attendance gets live.");
            }
        }else {

            //calling all...
            if (fir) {
                fir = false;

                if (checkInternetConnection()) {
                    new Logged().execute();
                } else {

                    Snackbar snackbar = Snackbar.make(ll, Html.fromHtml("<b>No Internet Connection</b>"), Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                    snackbar.show();
                }

            }

            //calling all ends...
        }

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed.clear();
                ed.commit();
                Intent it = new Intent(Attendance.this,Cortas.class);
                startActivity(it);
                finish();
            }
        });

        btTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Attendance.this,Timetable.class);
                startActivity(it);
            }
        });


    }

    class Logged extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {

            if(loginCookies.isEmpty())
            {
                String cookieKey = sp.getString("cookieKey","nodata");
                String cookieValue = sp.getString("cookieValue","nodata");


                keyArray = cookieKey.split("---374587348---");
                valueArray = cookieValue.split("---374587348---");

                if(!(cookieKey.contentEquals("nodata")))
                {
                    for(int i=0;i<keyArray.length;i++) {
                        loginCookies.put(keyArray[i], valueArray[i]);
                    }
                }
            }

            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                resTime = Jsoup.connect("https://upes.winnou.net/index.php?option=com_base_attendancereport&Itemid=98")
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36")
                        .header("Connection", "keep-alive")
                        .header("Referer", "https://upes.winnou.net/index.php")
                        .cookies(loginCookies)
                        .followRedirects(true)
                        .timeout(0)
                        .execute();

                docTime = resTime.parse();

                //Log.e("--Cookie while loading-",resTime.cookies().toString());

            }catch (IOException e){

                Log.e("--Exception-back-",e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            Element getName=null;

            try {

                getName = docTime.select("li.menu_right").first();
                String x[] = getName.text().split(",");
                nameShow = x[1];
            }catch(Exception e){}


            if(getName==null)
            {
                new Connect().execute();
            }
            else{

            if(docTime != null) {

                //if nothing is working then use getName for verification and login again

                Elements eles=null;
                Element total=null;

                try {
                     eles = docTime.select("table[width=90%]").select("tr").not("tr:has(th)");
                     total = docTime.select("table[width=90%]").select("tr").last();
                }catch (Exception e){}

                if(eles!=null) {

                    data = "";
                    subject1 = "";
                    classesHeld1 = "";
                    classesAttended1 = "";
                    absentDates1 = "";
                    attendance1 = "";
                    projection1 = "";
                    totalPercentage = "";

                    for (Element x : eles) {
                        subject1 = subject1 + x.child(1).text().toString() + "-----84759843-----";
                        classesHeld1 = classesHeld1 + x.child(2).text().toString() + "-----84759843-----";
                        classesAttended1 = classesAttended1 + x.child(3).text().toString() + "-----84759843-----";
                        absentDates1 = absentDates1 + x.child(4).select("div[data-original-title]").attr("data-original-title").toString() + " -----84759843-----";
                        attendance1 = attendance1 + x.child(5).text().toString() + "-----84759843-----";
                        projection1 = projection1 + x.child(6).text().toString() + "-----84759843-----";

                        Log.e("Date Test-"+x.child(1).text().toString(), x.child(4).select("div[data-original-title]").attr("data-original-title").toString());
                        //Log.e("sub1----",subject1);
                    }
                }


                if(total!=null) {
                    try {

                        totalClassesHeld =total.child(1).text();;
                        totalClassesAttended=total.child(2).text();;
                        totalPercentage = total.child(4).text();
                        projectionHere = total.child(5).text();

                        if(totalPercentage==null || totalPercentage.isEmpty() || totalPercentage.contentEquals("")){
                            showError("Sorry, "+nameShow+"!","Your data is not available on college website. Comeback when your attendance gets live.");
                            return;
                        }

                    } catch (Exception e) {
                        Toast.makeText(Attendance.this, "No Data Received!", Toast.LENGTH_LONG).show();
                    }
                }

                if(eles==null||total==null)
                {
                    AlertDialog.Builder adb = new AlertDialog.Builder(Attendance.this);
                    adb.setTitle("Something went wrong!")
                            .setMessage("Null value received from the server!\nPlease Try Again")
                            .setCancelable(true);
                    AlertDialog alert = adb.create();
                    alert.show();
                }

                ed.putString("subject1",subject1);
                ed.putString("classesHeld1",classesHeld1);
                ed.putString("classesAttended1",classesAttended1);
                ed.putString("absentDates1",absentDates1);
                ed.putString("attendance1",attendance1);
                ed.putString("projection1",projection1);
                ed.putString("totalPercentage1",totalPercentage);
                ed.putString("projectionHere1",projectionHere);
                ed.putString("totalClassesHeld1",totalClassesHeld);
                ed.putString("totalClassesAttended1",totalClassesAttended);
                ed.commit();


                // classAttendedHere = total.child(2).text()+"/"+total.child(1).text();

                subject = subject1.split("-----84759843-----");
                classesHeld = classesHeld1.split("-----84759843-----");
                classesAttended = classesAttended1.split("-----84759843-----");
                absentDates = absentDates1.split("-----84759843-----");
                attendance = attendance1.split("-----84759843-----");
                projection = projection1.split("-----84759843-----");


                // use username for name

                if(totalPercentage==null || totalPercentage.isEmpty() || totalPercentage.contentEquals("")){
                    showError("Sorry, "+nameShow+"!","Your data is not available on college website. Comeback when your attendance gets live.");
                    return;
                }

                    c = Double.parseDouble(totalPercentage);


                if(c==null){
                    showError("Something went wrong!","Your data is not available on college website.\nComeback when your attendance gets live.");
                }

                int z = c.intValue();

                //TextView tvName,tvProjection,tvActualAtt;
                tvName.setText(Username);
                tvProjection.setText("Projected : " + projectionHere);

                if (z >= 75) {

                    pbTotal.getProgressDrawable().setColorFilter(Color.rgb(33, 160, 13), PorterDuff.Mode.SRC_IN);
                    tvActualAtt.setText(Html.fromHtml("Total Attendance : <font color='#21A00D'>" + totalPercentage + "%</font>"));
                } else if (z >= 67 && z < 75) {
                    pbTotal.getProgressDrawable().setColorFilter(Color.rgb(253, 101, 0), PorterDuff.Mode.SRC_IN);
                    tvActualAtt.setText(Html.fromHtml("Total Attendance : <font color='#FD6500'>" + totalPercentage + "%</font>"));
                } else {
                    pbTotal.getProgressDrawable().setColorFilter(Color.rgb(216, 49, 30), PorterDuff.Mode.SRC_IN);
                    tvActualAtt.setText(Html.fromHtml("Total Attendance : <font color='#D8311E'>" + totalPercentage + "%</font>"));
                }

                pbTotal.setVisibility(View.VISIBLE);

                ObjectAnimator animation = ObjectAnimator.ofInt(pbTotal, "progress", z);
                animation.setDuration(2000); // 2 sec
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();


                adpt = new AttendanceAdapter(Attendance.this, subject, classesHeld, classesAttended, absentDates, attendance, projection);

                lv.setAdapter(adpt);

            }

                pd.dismiss();
            }

        }
    }

    @SuppressWarnings("deprecation")
    public boolean checkInternetConnection()
    {
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null)
        {
            NetworkInfo[] inf = connectivity.getAllNetworkInfo();
            if (inf != null)
                for (int i = 0; i < inf.length; i++)
                    if (inf[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }



    //-------------------------------------------ReConnect-------------------------------------------------------------------------


    class Connect extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            pdRe1.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            connect();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pdRe1.dismiss();
            String sap=sp.getString("ReUserId","nope");
            String pass=sp.getString("RePass","nope");
            String captcha=sp.getString("ReCaptcha","nope");
            new Connect2().execute(captcha,sap,pass);
        }
    }

    public void connect()
    {
        try {
            res = Jsoup.connect("https://upes.winnou.net/index.php").ignoreHttpErrors(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Accept-Language", "en-US,en;q=0.8")
                    .header("Accept-Encoding", "gzip, deflate, sdch")
                    .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .method(Connection.Method.GET)
                    .timeout(0)
                    .execute();

            document = res.parse();

            Elements hidden = document.select("input[type=hidden]");
            for(Element xy:hidden)
            {
                if(xy.attr("value").contentEquals("1"))
                {
                    unknown1 = xy.attr("name");
                }
            }

            Log.e("--ActualCookie--",res.cookies().toString());


            key="";
            value="";

            for (Map.Entry<String, String> entry : res.cookies().entrySet()) {
                if(entry.getKey().toString().contentEquals("AWSELB")) {
                    key = key + entry.getKey().toString() + "---374587348---";
                    value = value + entry.getValue().toString() + "---374587348---";
                }
            }

            /*

            for (Map.Entry<String, String> entry : res.cookies().entrySet()) {
                key = key+ entry.getKey().toString()+"---374587348---";
                value = value+ entry.getValue().toString()+"---374587348---";
            }

            */

            //try to store this cookie also

        }catch (IOException e){};
    }


    public void login(String cap,String sap, String pass)
    {
        try{

            resTime = Jsoup.connect("https://upes.winnou.net").ignoreHttpErrors(true)
                    .userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0")
                    .header("Connection", "keep-alive")
                    .header("Accept-Language", "en-US,en;q=0.8")
                    .header("Accept-Encoding", "gzip, deflate, sdch")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Upgrade-Insecure-Requests", "1")
                    .cookies(res.cookies())
                    .followRedirects(true)
                    .method(Connection.Method.POST)
                    .data("username", sap)
                    .data("passwd", pass)
                    .timeout(0)
                    .execute();


            Log.e("--normal--",""+resTime.statusCode());

            // doc2 = res.parse();
            //index.php?option=com_base_attendancereport&Itemid=98'
            //https://upes.winnou.net/index.php?option=com_base_timetable&task=createtimetable&timetableid=1&pageview=&groupid=131&facid=0&dname=&program=&sectiondisplay=&view=&page=view&Itemid=78&scheduledate=10-11-2015

            Log.e("--Again Check--", "" + resTime.cookies());

            //Set<String> key = loginCookies.entrySet();
            element = null;
            doc2 = resTime.parse();
            element = doc2.select("li.menu_right").first();


        }catch (IOException e){}
    }

    class Connect2 extends AsyncTask<String,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            pdRe2.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            login(params[0], params[1], params[2]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pdRe2.dismiss();

                String name="no data";

                if(element!=null) {
                    name = element.text();


                    Log.e("--name--", name);

                    for (Map.Entry<String, String> entry : resTime.cookies().entrySet()) {
                        key = key+ entry.getKey().toString()+"---374587348---";
                        value = value+ entry.getValue().toString()+"---374587348---";
                    }

                    String sap=sp.getString("ReUserId","nope");
                    String pass=sp.getString("RePass","nope");
                    String captcha=sp.getString("ReCaptcha","nope");

                    Log.e("---Key---", key);
                    Log.e("---Value---", value);

                    ed.putString("ReUserId", sap);
                    ed.putString("RePass", pass);
                    ed.putString("ReCaptcha", captcha);

                    ed.putString("cookieKey", key);
                    ed.putString("cookieValue", value);
                    ed.putString("name", name);
                    ed.commit();

                    //execute again data...

                    String cookieKey = sp.getString("cookieKey","nodata");
                    String cookieValue = sp.getString("cookieValue","nodata");

                    keyArray = cookieKey.split("---374587348---");
                    valueArray = cookieValue.split("---374587348---");


                    if(!loginCookies.isEmpty()) {
                        loginCookies.clear();
                    }

                    if(!(cookieKey.contentEquals("nodata")))
                    {
                        for(int i=0;i<keyArray.length;i++) {
                            loginCookies.put(keyArray[i], valueArray[i]);
                        }
                    }

                    new Logged().execute();

                }else{

                    showError("Something went wrong!","Your data is not available on college website.\nComeback when your attendance gets live.");

                }

        }

    }

    void showError(String title, String msg){
        AlertDialog.Builder adb = new AlertDialog.Builder(Attendance.this);
        adb.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ed.clear();
                        ed.commit();
                        Intent it = new Intent(Attendance.this,Cortas.class);
                        startActivity(it);
                        finish();
                    }
                });
        AlertDialog alert = adb.create();
        alert.show();
    }


}
