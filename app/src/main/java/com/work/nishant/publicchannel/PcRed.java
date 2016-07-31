package com.work.nishant.publicchannel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

/**
 * Created by Nishant on 12/28/2015.
 */
public class PcRed extends AppCompatActivity {

    String getactualDataName="";
    String getActualDataSap="";
    String getGetActualDataBranch="";
    String getViews="";


    String beginName="";
    ListView lv;
    EditText et;
    ProgressDialog pd,pdInc;
    TextView tv,tvi;
    Boolean isSuccess=false;

    SharedPreferences sps,sDay;
    SharedPreferences.Editor edt,eDay;
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    int x = 0;
    String query="",error="";
    String sapidSearch[]={},nameSearch[]={},branchSearch[]={},viewSearch[]={};
    double startTime=0;
    double stopTime=0;
    View viewm=null;
    Adapter adpt;

    //View Control.....

    String Day="";
    String sapId="";
    String unique="";
    String Osap="";
    Calendar c=null;
    //OtherProfile Data


    String OPsap="",OPname="",OProll="",OPbranch="",OPyear="",OPemail="",OPphone="",OPpStatus="";
    String OPImageUri="";
    boolean isRetrive=false;

    String tempName="";
    ProgressDialog pdr;//message for retriving the data

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcred);


            et = (EditText) findViewById(R.id.editTextSearchRed);

            tv = (TextView) findViewById(R.id.textViewTotalRed);
            tvi = (TextView) findViewById(R.id.textViewIntroRed);

            lv = (ListView) findViewById(R.id.listViewDisplayRed);

            pd = new ProgressDialog(PcRed.this);
            pd.setMessage("Searching...");
            pd.dismiss();

            pdInc = new ProgressDialog(PcRed.this);
            pdInc.setMessage("Synchronizing the data...");
            pdInc.dismiss();

        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        //admob activity.................................................................................

            pdr = new ProgressDialog(PcRed.this);

            sDay = getApplication().getSharedPreferences("sDay", 0);
            eDay = sDay.edit();

            sps = getApplication().getSharedPreferences("shared", 0);
            edt = sps.edit();
            beginName = "Hi, <b>"+sps.getString("name","Who ever this is!")+"!</b> enter Name, Sapid or Roll number to search";
            tv.setText(Html.fromHtml(beginName));

            sapId = sps.getString("sapid", "0");


            c = Calendar.getInstance();




            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(Search.this,sapidSearch[position],Toast.LENGTH_LONG).show();

                    tempName = nameSearch[position];
                    Osap = sapidSearch[position];
                    new OtherPro().execute(sapidSearch[position]);

                }
            });


            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabStartSearchRed);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    tvi.setText("");
                    tvi.setVisibility(View.VISIBLE);

                    viewm = PcRed.this.getCurrentFocus();
                    if (viewm != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(viewm.getWindowToken(), 0);
                    }

                    if (et.getText().toString().isEmpty()) {
                        AlertDialog.Builder abs = new AlertDialog.Builder(PcRed.this);
                        abs.setMessage("For example: Just enter 'nis'(without quotes) and you will see a list of all those contains nis in their name")
                                .setTitle("Can't be Empty")
                                .setCancelable(true);
                        AlertDialog ad = abs.create();
                        ad.show();
                    } else {
                        query = et.getText().toString().trim();

                        //Now validation : 0 for false, 1 for sapid, 2 for rollno, 3 for name

                        switch (validate(query)) {
                            case 1:
                                new OkSearch().execute(query, "sap");
                                break;
                            case 2:
                                new OkSearch().execute(query, "roll");
                                break;
                            case 3:
                                new OkSearch().execute(query, "name");
                                break;
                            default:
                                AlertDialog.Builder ad = new AlertDialog.Builder(PcRed.this);
                                ad.setMessage("Minimum 3 characters are required!")
                                        .setTitle("Error!")
                                        .setCancelable(true);
                                AlertDialog alert = ad.create();
                                alert.show();
                                break;
                        }
                    }
                }
            });






        }

    public int validate(String query)
    {
        if(query.startsWith("5000") && query.length()==9)
        {
            return 1;
        }
        else if(query.startsWith("R") && query.length()==10)
        {
            return 2;
        }
        else if(query.length()>=3)
        {
            return 3;
        }else{
            return 0;
        }

    }

    @SuppressLint("NewApi")
    public boolean login(String query,String type)
    {
        String inQuery="";
        getActualDataSap ="";
        getactualDataName="";
        getGetActualDataBranch="";
        getViews="";



        if(type.contentEquals("sap"))
        {
            inQuery = "sapid ='"+query+"'";
        }else if(type.contentEquals("roll"))
        {
            inQuery = "roll ='"+query+"'";

        }else if(type.contentEquals("name"))
        {
            inQuery = "name like '%"+query+"%'";
        }


        x =0;
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);



            Class.forName("com.mysql.jdbc.Driver").newInstance();

            con = DriverManager.getConnection("<ALTERNATE DOMAIN WITH JDBC CONNECTION>");//not a PHP Doamin

            ps = con.prepareStatement("select sapid, name, branch, views from stu where " + inQuery +" order by views desc");

            rs = ps.executeQuery();



            while(rs.next())
            {
                getActualDataSap = getActualDataSap + rs.getString("sapid")+"---793526---";
                getactualDataName = getactualDataName + rs.getString("name")+"---793526---";
                getGetActualDataBranch = getGetActualDataBranch + rs.getString("branch")+"---793526---";
                getViews = getViews + rs.getString("views")+"---793526---";;
                x++;
            }

            con.close();
        }
        catch (Exception e){error = e.toString();
            return false;
        }

        if(x>0)
            return true;
        else
            return false;
    }

    public class OkSearch extends AsyncTask<String,Void,Void>
    {

        @Override
        protected void onPreExecute() {

            pd.show();
            startTime = System.currentTimeMillis();
        }

        @Override
        protected Void doInBackground(String... params) {
            isSuccess = login(params[0],params[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            stopTime = System.currentTimeMillis();
            double time = (stopTime - startTime)/1000;
            if(isSuccess)
            {
                sapidSearch = getActualDataSap.split("---793526---");
                nameSearch = getactualDataName.split("---793526---");
                branchSearch = getGetActualDataBranch.split("---793526---");
                viewSearch = getViews.split("---793526---");


                adpt = new Adapter(PcRed.this,nameSearch,branchSearch,viewSearch);

                lv.setAdapter(adpt);

                String re = (x>1)?"results":"result";
                tv.setText("About "+x+" "+re+" ("+time+" seconds)");

            }else{

                error = (error.isEmpty())?"Data Not Found!":"No Internet Connection or Server is Down";
                AlertDialog.Builder ad = new AlertDialog.Builder(PcRed.this);
                ad.setMessage(error)
                        .setTitle("Error!")
                        .setCancelable(true);
                AlertDialog alert = ad.create();
                alert.show();

            }
        }
    }


    public boolean retrive(String sap)
    {
        int n = 0;
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);



            Class.forName("com.mysql.jdbc.Driver").newInstance();

            con = DriverManager.getConnection(Global.MainURL,Global.MainUser,Global.MainPassword);

            ps = con.prepareStatement("select * from stu where sapid ='"+sap+"' ");

            rs = ps.executeQuery();



            while(rs.next())
            {
                OPsap= rs.getString("sapid");
                OPname=rs.getString("name");
                OProll=rs.getString("roll");
                OPbranch=rs.getString("branch");
                OPyear=rs.getString("year");
                n++;
            }

            con.close();
        }
        catch (Exception e){error = e.toString();
            return false;
        }

        if(n>0)
            return true;
        else
            return false;
    }

    public class OtherPro extends AsyncTask<String,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            pdr.setMessage(Html.fromHtml("Retrieving <font color='#ec187f'><b>"+tempName+"'s</b></font> Data..."));
            pdr.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            isRetrive =  retrive(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            pdr.dismiss();
            if(isRetrive)
            {

                AlertDialog.Builder adb = new AlertDialog.Builder(PcRed.this);
                adb.setCancelable(true)
                        .setMessage("Name :"+OPname+"\n"+
                                        "Sap :"+OPsap+"\n"+
                                        "Roll :"+ OProll+"\n"+
                                        "Branch :"+ OPbranch+"\n"+
                                "Year :"+ OPyear)
                        .setTitle("Details of "+OPname);

                AlertDialog alert = adb.create();
                alert.show();


            }else{
                //show error occurred during retrieval of data
                AlertDialog.Builder adb = new AlertDialog.Builder(PcRed.this);
                adb.setTitle("Something went wrong!")
                        .setMessage("Probably No internet connection or server is down")
                        .setCancelable(true);
                AlertDialog al = adb.create();
                al.show();
            }
        }
    }

}

