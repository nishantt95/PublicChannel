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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
 * Created by Nishant on 1/2/2016.
 */
public class SkillPost extends AppCompatActivity {

    RadioGroup rgType=null,rgLevel=null;
    RadioButton rbType=null,rbLevel=null;

    int selectedType=0,selectedLevel=0;

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    String Type="0",Level="0",skills="",description="";

    EditText etSkills,etDescription;

    Button btUpdate;

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    String error="",userSap="",query="";

    String isUpdate="";
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skill_post);

        rgType = (RadioGroup) findViewById(R.id.GroupType);
        rgLevel = (RadioGroup) findViewById(R.id.GroupLevel);

        etSkills = (EditText) findViewById(R.id.PostSkillSet);
        etDescription = (EditText) findViewById(R.id.PostDescription);
        btUpdate = (Button) findViewById(R.id.buttonSkillUpdate);

        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        //admob activity.................................................................................

        pd = new ProgressDialog(SkillPost.this);
        pd.setMessage("Updating your skills...");
        pd.dismiss();

        sp = getApplicationContext().getSharedPreferences("shared",0);
        ed = sp.edit();

        userSap = sp.getString("sapid","unknown");

        rgLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedLevel = rgLevel.getCheckedRadioButtonId();
                rbLevel = (RadioButton) findViewById(selectedLevel);

                if(rbLevel==null || rbLevel.getText().toString().equalsIgnoreCase("Newbie"))
                {
                    Level="0";
                }else if(rbLevel.getText().toString().equalsIgnoreCase("Intermediate"))
                {
                    Level="1";
                }else if(rbLevel.getText().toString().equalsIgnoreCase("Expert"))
                {
                    Level="2";
                }
            }
        });

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedType = rgType.getCheckedRadioButtonId();
                rbType = (RadioButton) findViewById(selectedType);

                if(rbType==null || rbType.getText().toString().equalsIgnoreCase("Free"))
                {
                    Type="0";
                }else if(rbType.getText().toString().equalsIgnoreCase("Paid"))
                {
                    Type="1";
                }

            }
        });





        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View  viewm = SkillPost.this.getCurrentFocus();
                if (viewm != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewm.getWindowToken(), 0);
                }

                if(etSkills.getText().toString().isEmpty())
                {
                    //error skill empty
                    AlertDialog.Builder ad = new AlertDialog.Builder(SkillPost.this);
                    ad.setMessage("Skills can't be empty")
                            .setTitle("Please Wait!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setCancelable(false);
                    AlertDialog alert = ad.create();
                    alert.show();

                }else{
                    if(etDescription.getText().toString().isEmpty())
                    {
                        skills=etSkills.getText().toString();
                        if(skills.split(" ").length<=10)
                        {
                            //String skills,String type, String level,String description,String userSap
                            description="";
                            new UpdateItSkill().execute(skills,Type,Level,description,userSap);
                        }else{
                            AlertDialog.Builder ad = new AlertDialog.Builder(SkillPost.this);
                            ad.setMessage("Skills should not be more than 10 words")
                                    .setTitle("Limit Exceeded!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setCancelable(false);
                            AlertDialog alert = ad.create();
                            alert.show();
                        }
                    }else{

                        skills=etSkills.getText().toString();
                        description = etDescription.getText().toString();
                        if(skills.split(" ").length<=10 && description.split(" ").length<=100)
                        {
                            new UpdateItSkill().execute(skills,Type,Level,description,userSap);
                        }else{
                            //word limit 10 & 100
                            AlertDialog.Builder ad = new AlertDialog.Builder(SkillPost.this);
                            ad.setMessage("Skills should not be more than 10 words\nDescription should not be more than 100 words.")
                                    .setTitle("Limit Exceeded!")
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
        });

    }

    @SuppressLint("NewApi")
    public String updateSkill(String skills,String type, String level,String description,String userSap)
    {

        int x=0;
        try {

            URL url = new URL(Global.skillPostUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String data = URLEncoder.encode("sapid", "UTF-8")+"="+URLEncoder.encode(userSap,"UTF-8")+"&"+
                    URLEncoder.encode("skillset", "UTF-8")+"="+URLEncoder.encode(skills, "UTF-8")+"&"+
                    URLEncoder.encode("paidtype", "UTF-8")+"="+URLEncoder.encode(type, "UTF-8")+"&"+
                    URLEncoder.encode("skilltype", "UTF-8")+"="+URLEncoder.encode(level, "UTF-8")+"&"+
                    URLEncoder.encode("description", "UTF-8")+"="+URLEncoder.encode(description, "UTF-8");

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


    class UpdateItSkill extends AsyncTask<String,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            isUpdate = updateSkill(params[0],params[1],params[2],params[3],params[4]);
            isUpdate = String.valueOf(isUpdate.charAt(0));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();

            Log.e("Skill php", isUpdate);

            if(isUpdate.equalsIgnoreCase("1"))
            {

                ed.putString("skillset",skills);
                ed.commit();

                AlertDialog.Builder ad = new AlertDialog.Builder(SkillPost.this);
                ad.setMessage("Your changes have been saved")
                        .setTitle("Updated Successfully!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent it = new Intent(SkillPost.this,StartHere.class);
                                startActivity(it);
                                finish();
                            }
                        })
                        .setCancelable(false);
                AlertDialog alert = ad.create();
                alert.show();



            }else if(isUpdate.equalsIgnoreCase("#")){
                AlertDialog.Builder ad = new AlertDialog.Builder(SkillPost.this);
                ad.setMessage("Exception #SKILLPOST")
                        .setTitle("Update Failed")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(false);
                AlertDialog alert = ad.create();
                alert.show();
            }else if(isUpdate.equalsIgnoreCase("0")){
                AlertDialog.Builder ad = new AlertDialog.Builder(SkillPost.this);
                ad.setMessage("No Internet connection OR Server is down.")
                        .setTitle("Update Failed")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(false);
                AlertDialog alert = ad.create();
                alert.show();
            }else{

                //Log.e("update :",isUpdate);

                AlertDialog.Builder ad = new AlertDialog.Builder(SkillPost.this);
                ad.setMessage("No Internet connection OR Server is down.")
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
