package com.work.nishant.publicchannel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Nishant on 1/6/2016.
 */
public class oneMost extends Fragment {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    AdapterMost adpt;

    ProgressDialog pd;
    ListView lv;

    boolean isSuccess=false;

    String getActualDataSap="",getactualDataName="",getGetActualDataBranch="",getViews="",error="";
    String sapidMost[]={},branchMost[]={},viewMost[]={},nameMost[]={};

    String response="";

    boolean isButtonP=false;

    Button bt;

    Bitmap bms[]={};

    TextView tv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.most_searched,container,false);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Generating top 20 list...");
        pd.dismiss();

        lv = (ListView) view.findViewById(R.id.listViewMost);

        bt = (Button) view.findViewById(R.id.buttonMostRefresh);
        tv = (TextView) view.findViewById(R.id.textViewMostData);

        String setMost="As the name suggests itself, most searched is simply a list of those students who are most viewed among all. Here we display top 20 students indexed on the basis of their views.   ";

        tv.setText(Html.fromHtml(setMost));

        //new OkSearchMost().execute();

        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) view.findViewById(R.id.adViewMost);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        //admob activity.................................................................................

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isButtonP = true;
                tv.setVisibility(View.GONE);
                new OkSearchMost().execute();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return  view;
    }

    @SuppressLint("NewApi")
    public boolean login()
    {
        getActualDataSap ="";
        getactualDataName="";
        getGetActualDataBranch="";
        getViews="";
        error="";
        int x =0;
        try {


                URL url = new URL(Global.oneMostUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


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

                response =  sb.toString().trim();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;
            while (count < jsonArray.length()) {
                JSONObject rs = jsonArray.getJSONObject(count);
                getActualDataSap = getActualDataSap + rs.getString("sapid")+"---793526---";
                getactualDataName = getactualDataName + rs.getString("name")+"---793526---";
                getGetActualDataBranch = getGetActualDataBranch + rs.getString("branch")+"---793526---";
                getViews = getViews + rs.getString("views")+"---793526---";

                count++;
            }

            return true;
        }
        catch (Exception e){error = e.toString();
            return false;
        }

    }


    public class OkSearchMost extends AsyncTask<String,Void,Void>
    {

        @Override
        protected void onPreExecute() {

            if(isButtonP)
            {
                pd.show();
                isButtonP=false;
                bt.setEnabled(false);
            }

        }

        @Override
        protected Void doInBackground(String... params) {
            isSuccess = login();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            bt.setEnabled(true);
            if(isSuccess)
            {
                sapidMost = getActualDataSap.split("---793526---");
                nameMost = getactualDataName.split("---793526---");
                branchMost = getGetActualDataBranch.split("---793526---");
                viewMost = getViews.split("---793526---");

                adpt = new AdapterMost(getActivity(),nameMost,branchMost,viewMost);
                lv.setAdapter(adpt);


            }else{

                error = (error.isEmpty())?"Data Not Found!":"No Internet Connection or Server is Down";
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setMessage(error)
                        .setTitle("Error!")
                        .setCancelable(true);
                AlertDialog alert = ad.create();
                alert.show();

            }
        }
    }
}
