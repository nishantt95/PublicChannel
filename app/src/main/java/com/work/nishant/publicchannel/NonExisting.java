package com.work.nishant.publicchannel;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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

/**
 * Created by Nishant on 12/19/2015.
 */

public class NonExisting extends AppCompatActivity {

    Button bt;

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    EditText ename,eroll,ebranch,eyear;

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    String name="",roll="",branch="",year="",sap="";
    Boolean validate=false,isThere=false,isUpdated=false;
    String error="";
    String response="";

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nonexisting);

        ename = (EditText) findViewById(R.id.editTextNameNonExisting);
        eroll = (EditText) findViewById(R.id.editTextRollNonExisting);
        ebranch = (EditText) findViewById(R.id.editTextBranchNonExisting);
        eyear = (EditText) findViewById(R.id.editTextYearNonExisting);

        bt = (Button) findViewById(R.id.buttonNonExisting);

        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        //admob activity.................................................................................

        sp = getApplicationContext().getSharedPreferences("shared", 0);
        ed = sp.edit();

        sap = sp.getString("sapid", "Unknown!!!!");

        pd = new ProgressDialog(NonExisting.this);
        pd.setMessage("Creating new profile...");
        pd.dismiss();


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = ename.getText().toString();
                roll = eroll.getText().toString();
                branch = ebranch.getText().toString();
                year = eyear.getText().toString();

                View  viewm = NonExisting.this.getCurrentFocus();
                if (viewm != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewm.getWindowToken(), 0);
                }

                validate = validate(name,roll,branch,year);

                if(validate)
                {
                    //check if rollno already exist then create new user..
                    new PutData().execute(name,roll,branch,year,sap);

                }

            }
        });

    }

    public boolean validate(String name,String roll, String branch, String year)
    {
        if(name.isEmpty())
        {
            ename.setError("Name can't be empty!");
            return false;
        }

        if(roll.isEmpty())
        {
            eroll.setError("Roll no can't be empty!");
            return false;
        }

        if(branch.isEmpty())
        {
            ename.setError("Name can't be empty!");
            return false;
        }

        if(year.isEmpty())
        {
            eroll.setError("Roll no can't be empty!");
            return false;
        }

        if(year.length()>1 || Integer.parseInt(year)<1 || Integer.parseInt(year)>6 )
        {
            eyear.setError("Just enter single char eg: 1 for 1st year");
            return false;
        }

        if(roll.length()!=10 || !(roll.startsWith("R")))
        {
            eroll.setError("Roll no. starting with R");
            return false;
        }

        return true;
    }

    public class PutData extends AsyncTask<String,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            response = createIt(params[0], params[1], params[2], params[3], params[4]);
            response = String.valueOf(response.charAt(0));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            if(response.equalsIgnoreCase("#"))
            {
                AlertDialog.Builder adb = new AlertDialog.Builder(NonExisting.this);
                adb.setMessage(error)
                        .setTitle("Exception!")
                        .setCancelable(true);
                AlertDialog al = adb.create();
                al.show();
            }else if(response.equalsIgnoreCase("0"))
            {
                AlertDialog.Builder adb = new AlertDialog.Builder(NonExisting.this);
                adb.setMessage("Roll number already exists!")
                        .setTitle("Error!")
                        .setCancelable(true);
                AlertDialog al = adb.create();
                al.show();
            }
            else if(response.equalsIgnoreCase("1")){
                //to existing page
                AlertDialog.Builder adb = new AlertDialog.Builder(NonExisting.this);
                adb.setMessage("New user created successfully!")
                        .setTitle("Successful")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ed.putString("sapid",sap);
                                ed.putString("name",name);
                                ed.commit();
                                Intent it = new Intent(NonExisting.this,Existing.class);
                                startActivity(it);
                                finish();
                            }
                        })
                        .setCancelable(false);
                AlertDialog al = adb.create();
                al.show();
            }else{
                AlertDialog.Builder adb = new AlertDialog.Builder(NonExisting.this);
                adb.setMessage("#NON_EXISTING_DEFAULT\n\nProbably this roll number already exists")
                        .setTitle("Unknown Error")
                        .setCancelable(true);
                AlertDialog al = adb.create();
                al.show();
            }
        }
    }



    public String createIt(String name,String roll, String branch, String year,String sap)
    {
        try {

            URL url = new URL(Global.nonExistingUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String data = URLEncoder.encode("sapid", "UTF-8")+"="+URLEncoder.encode(sap,"UTF-8")+"&"+
                    URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"+
                    URLEncoder.encode("roll","UTF-8")+"="+URLEncoder.encode(roll,"UTF-8")+"&"+
                    URLEncoder.encode("branch","UTF-8")+"="+URLEncoder.encode(branch,"UTF-8")+"&"+
                    URLEncoder.encode("year","UTF-8")+"="+URLEncoder.encode(year,"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"ISO-8859-1"));

            String response ="";

            String line="";

            while((line=bufferedReader.readLine())!=null)
            {
                response+=line;
            }

            inputStream.close();

            bufferedReader.close();

            return response;


        } catch (Exception v) {
            error = v.toString();
            return "#";
        }

    }


}
