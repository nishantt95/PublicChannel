package com.work.nishant.publicchannel;

import android.annotation.SuppressLint;
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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

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
 * Created by Nishant on 12/24/2015.
 */
public class Profile extends AppCompatActivity {

    EditText pEmail, pPhone, pPassword;
    String Email="", Phone="", Password="", Status="0",Sap="";


    RadioButton rb;
    Button btUpdate;

    View viewm=null;

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    SharedPreferences sp;
    SharedPreferences.Editor ed;
    ProgressDialog pd,cd;
    String error="";

    boolean login=false;
    String isUpdate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        pEmail = (EditText) findViewById(R.id.profileEmail);
        pPhone = (EditText) findViewById(R.id.profilePhone);
        pPassword = (EditText) findViewById(R.id.profilePassword);
        rb = (RadioButton) findViewById(R.id.radioButtonProfileStatus);
        btUpdate = (Button) findViewById(R.id.buttonProfileUpdate);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabProfile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        sp = getApplicationContext().getSharedPreferences("shared", 0);
        ed = sp.edit();

        Sap = sp.getString("sapid", "unknown");
        Status = sp.getString("phoneStatus","0");

        if(sp.getString("facebook","no").contentEquals("yes"))
        {
            pEmail.setVisibility(View.GONE);
        }


        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        //admob activity.................................................................................


        pd = new ProgressDialog(Profile.this);
        pd.setMessage("Updating Data...");
        pd.dismiss();

        cd = new ProgressDialog(Profile.this);
        cd.setMessage("Authenticating User...");
        cd.dismiss();



        if(Status.contentEquals("1"))
        {
            rb.setVisibility(View.INVISIBLE);
            Status="1";
        }else{
            rb.setText(Html.fromHtml("Set phone number to<font color='red'> Private</font>"));
        }

        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rb.isChecked())
                {
                    AlertDialog.Builder adc = new AlertDialog.Builder(Profile.this);
                    adc.setMessage("If you will keep your phone number private then you will not be allowed to see other's phone number.")
                            .setTitle("Are you sure?")
                            .setPositiveButton("Yes, I am", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Status = "1";
                                }
                            })
                            .setNegativeButton("No, Cancel it", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Status = "0";
                                    rb.setChecked(false);
                                }
                            });
                    AlertDialog al = adc.create();
                    al.show();

                }else{
                    Status="0";
                }
            }
        });



        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewm = Profile.this.getCurrentFocus();
                if (viewm != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewm.getWindowToken(), 0);
                }

                if(sp.getString("facebook","no").contentEquals("yes"))
                {
                    Email = sp.getString("email","unknown@mail.com");
                }else {

                    if (pEmail.getText().toString().isEmpty())
                        pEmail.setError("Can't be empty");

                    Email = pEmail.getText().toString();
                }


                if(pPhone.getText().toString().isEmpty())
                    pPhone.setError("Can't be empty");
                else if(pPassword.getText().toString().isEmpty())
                    pPassword.setError("Can't be empty");
                else {

                    Phone = pPhone.getText().toString();
                    Password = pPassword.getText().toString();

                    if (validation(Email, Password, Phone)) {
                        //String email, String phone, String password, String sapid,String phSt
                        //String e, String p
                        new UpdateIt().execute(Email,Phone,Password,Sap,Status);
                    }
                }

            }
        });

    }


                                    //email, password, phone
    public boolean validation(String em,String pa,String ph)
    {
        if(!em.endsWith(".com"))
        {
            pEmail.setError("Invalid EmailId Format!");
            return false;
        }
        if(!em.contains("@"))
        {
            pEmail.setError("Invalid EmailId Format!");
            return false;
        }
        if(!(pa.length()>=6))
        {
            pPassword.setError("Password should be greater than 6 chars");
            return false;
        }
        if(ph.length()!=10)
        {
            pPhone.setError("Strictly 10 digits");
            return false;
        }
        if(!(ph.charAt(0)=='9'||ph.charAt(0)=='8'||ph.charAt(0)=='7'))
        {
            pPhone.setError("Phone number seems to be invalid");
            return false;
        }

        return true;
    }



    @SuppressLint("NewApi")
    public String update(String email, String phone, String password, String sapid,String phSt)
    {
        try {

            URL url = new URL(Global.profile1Url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String data = URLEncoder.encode("sapid", "UTF-8")+"="+URLEncoder.encode(sapid,"UTF-8")+"&"+
                    URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(email, "UTF-8")+"&"+
                    URLEncoder.encode("phone", "UTF-8")+"="+URLEncoder.encode(phone, "UTF-8")+"&"+
                    URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(password, "UTF-8")+"&"+
                    URLEncoder.encode("phoneStatus", "UTF-8")+"="+URLEncoder.encode(phSt, "UTF-8");

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


    class UpdateIt extends AsyncTask<String,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            isUpdate = update(params[0], params[1], params[2], params[3], params[4]);
            isUpdate = String.valueOf(isUpdate.charAt(0));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();

            Log.e("Profile php",isUpdate);

            if(isUpdate.equalsIgnoreCase("1"))
            {
                ed.putString("email",Email);
                ed.putString("password",Password);
                ed.putString("phoneStatus", Status);
                ed.commit();

                AlertDialog.Builder ad = new AlertDialog.Builder(Profile.this);
                ad.setMessage("Your changes have been saved")
                        .setTitle("Updated Successfully!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent it = new Intent(Profile.this,StartHere.class);
                                startActivity(it);
                                finish();
                            }
                        })
                        .setCancelable(false);
                AlertDialog alert = ad.create();
                alert.show();
            }else if(isUpdate.equalsIgnoreCase("0") ||isUpdate.equalsIgnoreCase("#")){
                AlertDialog.Builder ad = new AlertDialog.Builder(Profile.this);
                ad.setMessage("Something went wrong!\nThis data is already in use or no internet connection!")
                        .setTitle("Update Failed")
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
