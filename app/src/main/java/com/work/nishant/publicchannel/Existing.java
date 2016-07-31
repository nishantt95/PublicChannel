package com.work.nishant.publicchannel;


import android.annotation.SuppressLint;
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
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

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
public class Existing extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    TextView tv1;
    EditText email,phone,pass;
    String em="",ph="",pa="",error="",sap="";
    Button bt;
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    ProgressDialog pd,cd;

    String phoneStatus="0";

    boolean login=false;
    boolean validation=false;
    boolean isUpdate=false;

    String response="";

    RadioButton rb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.existing);

        email = (EditText) findViewById(R.id.editTextEmailExisting);
        phone = (EditText) findViewById(R.id.editTextPhoneExisting);
        pass = (EditText) findViewById(R.id.editTextPasswordExisting);
        tv1 = (TextView) findViewById(R.id.textViewUserName);

        bt = (Button) findViewById(R.id.buttonExisting);
        pd = new ProgressDialog(Existing.this);
        pd.setMessage("Updating Data...");
        pd.dismiss();

        cd = new ProgressDialog(Existing.this);
        cd.setMessage("Authenticating User...");
        cd.dismiss();

        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        //admob activity.................................................................................


        rb = (RadioButton) findViewById(R.id.radioButtonExisting);
        rb.setText(Html.fromHtml("Keep my phone number <font color='red'>Private</font>"));

        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rb.isChecked())
                {
                    AlertDialog.Builder adc = new AlertDialog.Builder(Existing.this);
                    adc.setMessage("If you will keep your phone number private then you will not be allowed to see other's phone number.")
                            .setTitle("Are you sure?")
                            .setPositiveButton("Yes, I am", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    phoneStatus = "1";
                                }
                            })
                            .setNegativeButton("No, Cancel it", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    phoneStatus = "0";
                                    rb.setChecked(false);
                                }
                            });
                    AlertDialog al = adc.create();
                    al.show();

                }else{
                    phoneStatus="0";
                }
            }
        });



        // getting the sapid of user
        sp = getApplicationContext().getSharedPreferences("shared", 0);
        ed = sp.edit();

        sap = sp.getString("sapid", "unknown");

        tv1.setText("Hi, "+sp.getString("name","Who are you?")+"!");

        if(sap.contentEquals("unknown"))
        {
            //tv.setText("Not Possible");
            Intent it = new Intent(Existing.this,MainActivity.class);
            startActivity(it);
            finish();
        }


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phone.setError(null);
                pass.setError(null);
                email.setError(null);

                View  viewm = Existing.this.getCurrentFocus();
                if (viewm != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewm.getWindowToken(), 0);
                }


                if(email.getText().toString().isEmpty())
                {
                    email.setError("Email can't be empty");
                }else if(phone.getText().toString().isEmpty())
                {
                    phone.setError("Number can't be empty");
                }else if(pass.getText().toString().isEmpty())
                {
                    pass.setError("Password can't be empty");
                }else
                {
                    //now the code goes here
                    em = email.getText().toString();
                    ph = phone.getText().toString();
                    pa = pass.getText().toString();



                    validation = validation();

                    if(validation)
                    {
                        new Proceed().execute(em,ph,pa,phoneStatus,sap);
                    }
                }
            }
        });

    }

    @SuppressLint("NewApi")
    public String existing(String email, String phone, String password, String phoneStatus, String sapid)
    {

        try {

            URL url = new URL(Global.existingUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+
                    URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(phone,"UTF-8")+"&"+
                    URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"+
                    URLEncoder.encode("phoneStatus","UTF-8")+"="+URLEncoder.encode(phoneStatus,"UTF-8")+"&"+
                    URLEncoder.encode("sapid","UTF-8")+"="+URLEncoder.encode(sapid,"UTF-8");

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
            return "0";
        }

    }


    public boolean validation()
    {
        if(!em.endsWith(".com"))
        {
            email.setError("Invalid EmailId Format!");
            return false;
        }
        if(!em.contains("@"))
        {
            email.setError("Invalid EmailId Format!");
            return false;
        }
        if(!(pa.length()>=6))
        {
            pass.setError("Password should be greater than 6 chars");
            return false;
        }
        if(ph.length()!=10)
        {
            phone.setError("Strictly 10 digits");
            return false;
        }
        if(!(ph.charAt(0)=='9'||ph.charAt(0)=='8'||ph.charAt(0)=='7'))
        {
            phone.setError("Phone number seems to be invalid");
            return false;
        }
        return true;
    }


    public class Proceed extends AsyncTask<String, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            cd.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            response = existing(params[0], params[1], params[2], params[3], params[4]);
            response = String.valueOf(response.charAt(0));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cd.dismiss();

            if(response.equalsIgnoreCase("1"))
            {
                AlertDialog.Builder ab = new AlertDialog.Builder(Existing.this);
                ab.setCancelable(false)
                        .setMessage("Now you will be redirected to login page\n\nSapid:" + sap + "\n\nPassword:" + pa)
                        .setTitle("Update Successful!")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent it = new Intent(Existing.this, MainActivity.class);
                                startActivity(it);
                                finish();
                            }
                        });
                AlertDialog alert = ab.create();
                alert.show();
            }else if(response.equalsIgnoreCase("0"))
            {
                AlertDialog.Builder ab = new AlertDialog.Builder(Existing.this);
                ab.setCancelable(true)
                        .setMessage("We can't update your data at this moment please try again after some time.\n\nProbably the data entered is already in use.")
                        .setTitle("Something went wrong!");
                AlertDialog alert = ab.create();
                alert.show();
            }else{
                AlertDialog.Builder ab = new AlertDialog.Builder(Existing.this);
                ab.setCancelable(true)
                        .setMessage("An unknown error has occurred #EXISTING")
                        .setTitle("Something went wrong!");
                AlertDialog alert = ab.create();
                alert.show();
            }
        }
    }
}


