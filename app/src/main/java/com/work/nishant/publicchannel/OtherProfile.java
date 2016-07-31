package com.work.nishant.publicchannel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.w3c.dom.Text;

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
 * Created by Nishant on 12/23/2015.
 */
public class OtherProfile extends AppCompatActivity {

    String name="",sap="",roll="",branch="",phone="",email="",phoneStatus="",year="";

    TextView eName,eSap,eRoll,eBranch,ePhone,eEmail;

    String error="";

    Button bt,btfb;
    String currentSap="";
    //TableRow tbSap;

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    String imageUri=null;
    Bitmap bm=null;
    ImageView im=null;

    ProgressDialog pd,pdi;

    String isUpdated= "";

    String UserPhoneStatus="";

    int secrete=0;

    ProgressBar pImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otherprofile);
/*
        it.putExtra("name",OPname);
        it.putExtra("sap",OPsap);
        it.putExtra("roll",OProll);
        it.putExtra("branch",OPbranch);
        it.putExtra("phone",OPphone);
        it.putExtra("email",OPemail);
        it.putExtra("phoneStatus",OPpStatus);
        it.putExtra("year",OPyear);
        it.putExtra("imageUri",OPImageUri);

        */

        im = (ImageView) findViewById(R.id.imageViewPic);

        pd = new ProgressDialog(OtherProfile.this);
        pd.setMessage(Html.fromHtml("Changing phone number to <font color='green'>Public</font>..."));
        pd.dismiss();

        pdi = new ProgressDialog(OtherProfile.this);
        pdi.setMessage(Html.fromHtml("Retrieving Image..."));
        pdi.dismiss();

        sp = getApplicationContext().getSharedPreferences("shared", 0);
        ed =sp.edit();

        pImage = (ProgressBar) findViewById(R.id.progressBarImageLoader);


        currentSap = sp.getString("sapid", "N/A");

        bt = (Button) findViewById(R.id.buttonPhonePublic);
        bt.setVisibility(View.GONE);


        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        //admob activity.................................................................................

        eName = (TextView) findViewById(R.id.otherProfileName);
        eSap = (TextView) findViewById(R.id.otherProfileSap);
        eRoll = (TextView) findViewById(R.id.otherProfileRoll);
        eBranch = (TextView) findViewById(R.id.otherProfileBranch);
        ePhone = (TextView) findViewById(R.id.otherProfilePhone);
        eEmail = (TextView) findViewById(R.id.otherProfileEmail);

        btfb = (Button) findViewById(R.id.buttonFbConnect);

        name = getIntent().getStringExtra("name");
        sap = getIntent().getStringExtra("sap");
        roll = getIntent().getStringExtra("roll");
        branch = getIntent().getStringExtra("branch");
        phone = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");
        phoneStatus = getIntent().getStringExtra("phoneStatus");
        year = getIntent().getStringExtra("year");
        imageUri = getIntent().getStringExtra("imageUri");



        if(sp.getString("facebook","no").contentEquals("yes"))
        {
            btfb.setVisibility(View.GONE);
        }else{
            im.setVisibility(View.GONE);
            pImage.setVisibility(View.GONE);
        }

        secrete = 0;
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secrete++;
                if(secrete==30)
                {
                    AlertDialog.Builder adb = new AlertDialog.Builder(OtherProfile.this);
                    adb.setCancelable(true)
                            .setMessage("Do you wanna go there?")
                            .setTitle("Okay! So, You have unlocked the Public Channel Red!.")
                            .setPositiveButton("Yeah, let me in", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent it = new Intent(OtherProfile.this, PcRed.class);
                                    startActivity(it);
                                    finish();
                                }
                            });

                    AlertDialog alert = adb.create();
                    alert.show();

                }
            }
        });

        UserPhoneStatus = sp.getString("phoneStatus", "0");


        btfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(OtherProfile.this,FbLogin.class);
                startActivity(it);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabOtherProfile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });



        if(Integer.parseInt(UserPhoneStatus)==1)
        {
            ePhone.setText(Html.fromHtml("<b>Phone :</b> <font color='red'>Your phone number is private.</font> Change it to public to see others phone number."));
            bt.setVisibility(View.VISIBLE);
        }else if(Integer.parseInt(phoneStatus)==1)
        {
            ePhone.setText(Html.fromHtml("<b>Phone :</b> <font color='red'>Private</font>"));
        }else{
            ePhone.setText(Html.fromHtml("<b>Phone :</b> "+phone));
        }

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeStatus().execute(currentSap);
            }
        });

        eName.setText(Html.fromHtml("<b>Name :</b> " + name));
        eSap.setText(Html.fromHtml("<b>Sap Id :</b> "+sap));
        eRoll.setText(Html.fromHtml("<b>Roll No :</b> "+roll));
        eBranch.setText(Html.fromHtml("<b>Branch :</b> "+branch+" "+year+" Year"));
        eEmail.setText(Html.fromHtml("<b>Email :</b> " + email));

        if(sp.getString("facebook","no").contentEquals("yes")) {
            new Get().execute();
        }

    }

    @SuppressLint("NewApi")
    public String change(String sap)
    {

        try {

            URL url = new URL(Global.otherProfileUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String data = URLEncoder.encode("sapid","UTF-8")+"="+URLEncoder.encode(sap,"UTF-8");

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

    class ChangeStatus extends AsyncTask<String,Void,Void>
    {
        @Override
        protected void onPreExecute() {

            pd.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            isUpdated = change(params[0]);
            isUpdated = String.valueOf(isUpdated.charAt(0));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            if(isUpdated.equalsIgnoreCase("1"))
            {
                ed.putString("phoneStatus","0");
                ed.commit();
                AlertDialog.Builder adb = new AlertDialog.Builder(OtherProfile.this);
                adb.setTitle("Updated Successfully!")
                        .setCancelable(false)
                        .setMessage("Your phone number status is changed from private to public")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog al = adb.create();
                al.show();
            }else if(isUpdated.equalsIgnoreCase("#")){
                AlertDialog.Builder adb = new AlertDialog.Builder(OtherProfile.this);
                adb.setTitle("Update Failed!")
                        .setMessage("No internet connection or server is down\n#ExceptionOther")
                        .setCancelable(true);

                AlertDialog al = adb.create();
                al.show();
            }else{//including 0 case
                AlertDialog.Builder adb = new AlertDialog.Builder(OtherProfile.this);
                adb.setTitle("Update Failed!")
                        .setMessage("No internet connection or server is down\n#OTHERPROFILE")
                        .setCancelable(true);
                AlertDialog al = adb.create();
                al.show();
            }

        }
    }

    class Get extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            pImage.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL image_value = new URL(imageUri);
                bm = BitmapFactory.decodeStream(image_value.openConnection().getInputStream());
            }catch (Exception e){}

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            pImage.setVisibility(View.GONE);

            if(imageUri==null || imageUri.isEmpty())
            {
                //default pic
            }
            else if(bm!=null)
                im.setImageBitmap(bm);


        }


    }




}
