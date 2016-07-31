package com.work.nishant.publicchannel;

import android.annotation.SuppressLint;
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
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    //EditText et;
    Button att;//,btme;
    String sap="",error="",emailData="",name="",skillSet="";
    String roll="",bch="",pas="",phoneStatus="";
    String imageUri="";

    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    ProgressDialog pd;

    boolean login = false;
    boolean validation = false;

    String userDataJson="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //et = (EditText) findViewById(R.id.editTextSapId);
        //bt = (Button) findViewById(R.id.buttonSap);
        att = (Button) findViewById(R.id.buttonDirect);
        //btme = (Button) findViewById(R.id.buttonMe);


        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Logging In...");
        pd.dismiss();

        sp = getApplicationContext().getSharedPreferences("shared", 0);
        ed = sp.edit();


        if(sp.contains("sapid") && sp.contains("passwordEntered"))
        {
            //password loading time here...
            Intent it = new Intent(MainActivity.this,StartHere.class);
            startActivity(it);
            finish();
        }



/*        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        //admob activity.................................................................................*/


/*

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View viewm = MainActivity.this.getCurrentFocus();
                if (viewm != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewm.getWindowToken(), 0);
                }

                error="";

                ed.clear();
                ed.commit();

                if (et.getText().toString().isEmpty())
                    et.setError("Can't Be Empty");
                else {
                    sap = et.getText().toString();

                    validation = validate(sap);

                    if(validation) {
                        new Progress().execute(sap);
                    }else {

                        // at.setText("Invalid SapId");
                        //inValid sap
                        et.setError("Invalid SapId!");
                    }
                }
            }
        });
*/

        att.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,Cortas.class);
                startActivity(it);
            }
        });

        /*

        btme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,AboutMe.class);
                startActivity(it);
            }
        });

*/
        //internetConnection();

    }


    //Checking whether student is registered or not

    @SuppressLint("NewApi")
    public boolean login(String sap)
    {

        try {

            URL url = new URL(Global.mainActivity1Url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String data = URLEncoder.encode("sapid", "UTF-8")+"="+URLEncoder.encode(sap, "UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream is = httpURLConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line="";

            StringBuilder sb = new StringBuilder();

            while((line=br.readLine())!=null)
            {
                sb.append(line+"\n");
            }

            is.close();
            br.close();
            httpURLConnection.disconnect();

            String temp[] = sb.toString().trim().split("##-67584-##");
            userDataJson = temp[0];
            if(temp.length==2) {
                skillSet = temp[1];
            }

            return true;

        }
        catch (Exception e){error = e.toString();return false;}

    }

    // validating the Sap Id
    public boolean validate(String s)
    {

        if(s.length()==9) {
            String k = s.substring(0, 4);
            if(k.contentEquals("5000"))
                return true;
            else
                return false;
        }
        else
            return false;
    }

    public class Progress extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        @SuppressLint("NewApi")
        protected Void doInBackground(String... params) {


            login = login(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


            pd.dismiss();

            if(!error.isEmpty()) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setMessage("No Internet Connection\n\n"+error)
                        .setTitle("Error!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(false);
                AlertDialog alert = ad.create();
                alert.show();
            }
            else {

                    //login is true but not identifying if user is new ir existing--

                    if (login) {

                        //execute here to check if emailid is there or not
                    try {
                        JSONObject jsonObject = new JSONObject(userDataJson);
                        JSONArray jsonArray = jsonObject.getJSONArray("response");
                        int count = 0;
                        while (count < jsonArray.length()) {
                            JSONObject rs = jsonArray.getJSONObject(count);
                            emailData = (rs.getString("email") == null) ? "" : rs.getString("email").toString();
                            name = rs.getString("name").toString();
                            roll = rs.getString("roll").toString();
                            bch = rs.getString("branch").toString();
                            pas = (rs.getString("password") == null) ? "" : rs.getString("password").toString();
                            phoneStatus = rs.getString("phoneStatus");
                            imageUri = (rs.getString("piclink") == null) ? "" : rs.getString("piclink").toString();

                            count++;
                        }
                    }catch(Exception e){}

                        if(name.isEmpty()||name==null)
                        {
                            ed.putString("sapid",sap);
                            ed.commit();
                            Intent it = new Intent(MainActivity.this, NonExisting.class);
                            startActivity(it);
                            finish();
                        }

                        else if(emailData.isEmpty()) {
                            //sap registered

                            ed.putString("sapid", sap);
                            ed.putString("name",name);
                            ed.commit();
                            Intent it = new Intent(MainActivity.this, Existing.class);
                            startActivity(it);
                            finish();
                        }else{

                            Log.e("image",imageUri);

                            if(imageUri.isEmpty() || imageUri==null || imageUri.equalsIgnoreCase("null"))
                            {
                                ed.putString("facebook","no");
                            }else{
                                ed.putString("facebook","yes");
                                ed.putString("imageUri",imageUri);
                            }

                            ed.putString("sapid", sap);
                            ed.putString("name",name);
                            ed.putString("roll",roll);
                            ed.putString("branch",bch);
                            ed.putString("email",emailData);
                            ed.putString("password",pas);
                            ed.putString("phoneStatus",phoneStatus);
                            ed.putString("skillset",skillSet);
                            ed.commit();
                            Intent it = new Intent(MainActivity.this,Password.class);
                            startActivity(it);
                            finish();
                        }

                    } else {
                        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                        ad.setMessage("No Internet Connection\n\n#MAINACTIVITY")
                                .setTitle("Error!")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setCancelable(false);
                        AlertDialog alert = ad.create();
                        alert.show();

                    }


            }

        }
    }




}
