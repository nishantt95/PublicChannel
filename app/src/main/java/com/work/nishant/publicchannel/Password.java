package com.work.nishant.publicchannel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Nishant on 12/20/2015.
 */
public class Password extends AppCompatActivity {

    Button bt,forgotb;
    EditText et;
    TextView tv;
    String name="",sap="",email="";
    String pas="";
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);


        et = (EditText) findViewById(R.id.editTextPassword);
        bt = (Button) findViewById(R.id.buttonPasswordLogin);
        forgotb = (Button) findViewById(R.id.buttonForget);
        tv = (TextView) findViewById(R.id.textViewPassword);

        sp = getApplicationContext().getSharedPreferences("shared", 0);
        ed = sp.edit();

        name = sp.getString("name", "Who's This?");
        sap = sp.getString("sapid", "unknownlsdhjdsjfj");
        pas = sp.getString("password","unknownsdkhfsdnjhds");
        email = sp.getString("email","unknown@gmail.com");

        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);




        //admob activity.................................................................................

        pd = new ProgressDialog(Password.this);
        pd.setMessage("Sending E-Mail...");
        pd.dismiss();

        tv.setText("Hi, " + name + "!");

        forgotb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Recovery().execute();
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View  viewm = Password.this.getCurrentFocus();
                if (viewm != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewm.getWindowToken(), 0);
                }

                String ePass = et.getText().toString();
                if(ePass.contentEquals(pas)) {
                    ed.putString("passwordEntered",pas);
                    ed.commit();
                    Intent it = new Intent(Password.this, StartHere.class);
                    startActivity(it);
                    finish();
                }else{
                    AlertDialog.Builder adb = new AlertDialog.Builder(Password.this);
                    adb.setTitle("Something went wrong!")
                        .setMessage("Incorrect SapId/Password\nPlease Try Again!")
                                .setCancelable(true);
                    AlertDialog al = adb.create();
                    al.show();
                }
            }
        });

    }

    class Recovery extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        @SuppressWarnings("deprecation")
        protected Void doInBackground(Void... params) {

            final String url = "<YOUR DOMAIN>/emailPassRecovery.php?specialRequest="+email+"&specialKey="+pas;
                    try {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpResponse response = httpclient.execute(new HttpGet(url));
                    }
                    catch (ClientProtocolException e)
                    {

                    }
                    catch (IOException e)
                    {

                    }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            pd.dismiss();

            AlertDialog.Builder adb = new AlertDialog.Builder(Password.this)
                    .setTitle("Request Received!")
                    .setMessage("An email containing password is sent to that email id which is associated with sapid : " + sap + ".\n\nIf you have connected your account with facebook then mail is sent to that email which is connected with your facebook account.\n\nIt can take upto 30 minutes to receive the mail and don't forget to check on spam folder.")
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog al = adb.create();
            al.show();
        }
    }
}
