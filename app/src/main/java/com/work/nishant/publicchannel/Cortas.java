package com.work.nishant.publicchannel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nishant on 1/7/2016.
 */
public class Cortas extends AppCompatActivity {

    Document document,doc2,docTime;
    String link="";
    Connection.Response res,resTime;
    String captcha="hjd6j7",sap="",pass="";
    TextView tv;
    String unknown1="";

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Map<String, String> loginCookies;


    EditText etSap,etPass;
    Button bt;
    ProgressDialog pd,pdLogin;

    Element element = null;

    String key = "";
    String value = "";
    String error="";

    RadioGroup rg;

    String batch="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cortas_check);

        pd = new ProgressDialog(Cortas.this);
        pd.setMessage("Preparing system for login...");
        pd.dismiss();

        pdLogin = new ProgressDialog(Cortas.this);
        pdLogin.setMessage("Logging in...");
        pdLogin.dismiss();

        etSap = (EditText) findViewById(R.id.editTextSapId);
        etPass = (EditText) findViewById(R.id.editTextPassword);
        rg= (RadioGroup) findViewById(R.id.GroupTypeBatch);

        etPass.setText("password");
        etSap.setText("5000");
        bt = (Button) findViewById(R.id.button);

        sp = getApplicationContext().getSharedPreferences("cortas",0);
        ed = sp.edit();

        String cookieKey = sp.getString("cookieKey","nodata");
        String cookieValue = sp.getString("cookieValue","nodata");

        Log.e("--Key--", cookieKey);
        Log.e("--value--",cookieValue);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedLevel = rg.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(selectedLevel);

                if (rb.getText().toString().equalsIgnoreCase("B1")) {
                    batch = "B1";
                } else if (rb.getText().toString().equalsIgnoreCase("B2")) {
                    batch = "B2";
                } else if (rb.getText().toString().equalsIgnoreCase("B3")) {
                    batch = "B3";
                }else if (rb.getText().toString().equalsIgnoreCase("B4")) {
                    batch = "B4";
                }else{
                    batch = "B0";
                }

                ed.putString("batchSet","1");
                ed.commit();
            }
        });


        if(!(cookieKey.contentEquals("nodata")))
        {
            //check if rdr to attendance or timetable
            Intent it = new Intent(Cortas.this,Attendance.class);
            startActivity(it);
            finish();
        }else {

            new Connect().execute();

        }


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View  viewm = Cortas.this.getCurrentFocus();
                if (viewm != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewm.getWindowToken(), 0);
                }

                if (etSap.getText().toString().trim().isEmpty() || etPass.getText().toString().trim().isEmpty()) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(Cortas.this);
                    adb.setTitle("Something went wrong!")
                            .setMessage("All fields are required!")
                            .setCancelable(true);
                    AlertDialog alert = adb.create();
                    alert.show();
                } else if (etSap.getText().toString().trim().length() != 9 || (!etSap.getText().toString().trim().startsWith("5000"))) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(Cortas.this);
                    adb.setTitle("Something went wrong!")
                            .setMessage("Invalid data entered!")
                            .setCancelable(true);
                    AlertDialog alert = adb.create();
                    alert.show();
                }else if (batch.isEmpty()||batch==null) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(Cortas.this);
                    adb.setTitle("Something went wrong!")
                            .setMessage("Please Select the batch")
                            .setCancelable(true);
                    AlertDialog alert = adb.create();
                    alert.show();}
                else {

                    sap = etSap.getText().toString().trim();
                    pass = etPass.getText().toString().trim();
                    ed.putString("batch",batch);
                    ed.commit();

                    if(checkInternetConnection()) {
                        new Connect2().execute(captcha, sap, pass);
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
            }
        });

        }
    class Connect extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            connect();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
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


            for (Map.Entry<String, String> entry : res.cookies().entrySet()) {
                if(entry.getKey().toString().contentEquals("AWSELB")) {
                    key = key + entry.getKey().toString() + "---374587348---";
                    value = value + entry.getValue().toString() + "---374587348---";
                }
            }

            //try to store this cookie also

        }catch (Exception e){error += e.toString();};
    }

    public void login(String cap,String sap, String pass)
    {
        try{

            resTime = Jsoup.connect("https://upes.winnou.net/").ignoreHttpErrors(true)
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
                    .data("captcha", cap)
                    .timeout(0)
                    .execute();


            Log.e("--normal--",""+resTime.statusCode());




            // doc2 = res.parse();
            //index.php?option=com_base_attendancereport&Itemid=98'
            //https://upes.winnou.net/index.php?option=com_base_timetable&task=createtimetable&timetableid=1&pageview=&groupid=131&facid=0&dname=&program=&sectiondisplay=&view=&page=view&Itemid=78&scheduledate=10-11-2015

            Log.e("--Again Check--", "" + resTime.cookies());

            //Set<String> key = loginCookies.entrySet();

            doc2 = resTime.parse();
            element = null;
            element = doc2.select("li.menu_right").first();


        }catch (Exception e){error+=e.toString();}
    }

    class Connect2 extends AsyncTask<String,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            pdLogin.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                login(params[0], params[1], params[2]);
            }catch(Exception e){error +=e.toString(); }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pdLogin.dismiss();

                String name="no data";

                 if(element!=null) {
                    name = element.text();


                    Log.e("--name--", name);

                    for (Map.Entry<String, String> entry : resTime.cookies().entrySet()) {
                        key = key+ entry.getKey().toString()+"---374587348---";
                        value = value+ entry.getValue().toString()+"---374587348---";
                    }


                    Log.e("---Key---",key);
                    Log.e("---Value---",value);


                    ed.putString("ReUserId", sap);
                    ed.putString("RePass", pass);
                    ed.putString("ReCaptcha",captcha);

                    ed.putString("cookieKey", key);
                    ed.putString("cookieValue", value);
                    ed.putString("name",name);
                    ed.commit();

                    Intent it = new Intent(Cortas.this,Attendance.class);
                    startActivity(it);
                    finish();

                }else if(!error.isEmpty()){
                     AlertDialog.Builder adb = new AlertDialog.Builder(Cortas.this);
                     adb.setTitle("Something went wrong!")
                             .setMessage(error)
                             .setCancelable(false)
                             .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                     new Connect().execute();
                                 }
                             });
                     AlertDialog alert = adb.create();
                     alert.show();

            }else{

                        AlertDialog.Builder adb = new AlertDialog.Builder(Cortas.this);
                        adb.setTitle("Something went wrong!")
                                .setMessage("Incorrect sapid/password")
                                .setCancelable(false)
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new Connect().execute();
                                    }
                                });
                        AlertDialog alert = adb.create();
                        alert.show();

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

}

