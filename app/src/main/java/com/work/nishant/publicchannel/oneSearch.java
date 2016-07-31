package com.work.nishant.publicchannel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.Calendar;

/**
 * Created by Nishant on 1/6/2016.
 */
public class oneSearch extends Fragment {

    String getactualDataName = "";
    String getActualDataSap = "";
    String getGetActualDataBranch = "";
    String getViews = "";


    String beginName = "";
    ListView lv;
    EditText et;
    ProgressDialog pd, pdInc;
    TextView tv;
    Boolean isSuccess = false;

    SharedPreferences sps, sDay;
    SharedPreferences.Editor edt, eDay;
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    int x = 0;
    String query = "", error = "";
    String sapidSearch[] = {}, nameSearch[] = {}, branchSearch[] = {}, viewSearch[] = {};
    double startTime = 0;
    double stopTime = 0;
    View viewm = null;
    Adapter adpt;

    //View Control.....

    String Day = "";
    String sapId = "";
    String unique = "";
    String Osap = "";
    Calendar c = null;

    Button imb;
    FloatingActionButton fab;
    Dialog dialog;

    String toIncrease = "yes";

    View view;
    //OtherProfile Data


    String OPsap = "", OPname = "", OProll = "", OPbranch = "", OPyear = "", OPemail = "", OPphone = "", OPpStatus = "";
    String OPImageUri = "";
    boolean isRetrive = false;


    String tempName = "";
    ProgressDialog pdr;//message for retriving the data

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search, container, false);

        et = (EditText) view.findViewById(R.id.editTextSearch);

        tv = (TextView) view.findViewById(R.id.textViewTotal);

        lv = (ListView) view.findViewById(R.id.listViewDisplay);

        imb = (Button) view.findViewById(R.id.buttonSearchP);

        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) view.findViewById(R.id.adViewSearch);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        //admob activity.................................................................................

        imb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startitnow();
            }
        });

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Searching...");
        pd.dismiss();

        pdInc = new ProgressDialog(getActivity());
        pdInc.setMessage("Synchronizing the data...");
        pdInc.dismiss();

        pdr = new ProgressDialog(getActivity());

        sDay = getActivity().getApplicationContext().getSharedPreferences("sDay", 0);
        eDay = sDay.edit();

        sps = getActivity().getApplicationContext().getSharedPreferences("shared", 0);
        edt = sps.edit();
        beginName = "Hi, <b>" + sps.getString("name", "Who ever this is!") + "!</b> " +
                "Welcome to the Public Channel's Search where you can search among any of the registered students. Currently we have <b>3,400+ students registered</b> and our user database is increasing day by day.<br><br>You can search students on the basis of their name (first name or last name or just a related phrase would work), Sap id and roll no. Here students will be indexed on the basis of their views.<br><br>" +
                "<b>Quick tip: -</b><br>Just type in 'abh' (without quotes) and click on search. Just swipe left or right to switch between the tabs.";
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


        return  view;

    }

    public int validate(String query) {
        if (query.startsWith("5000") && query.length() == 9) {
            return 1;
        } else if (query.startsWith("R") && query.length() == 10) {
            return 2;
        } else if (query.length() >= 3) {
            return 3;
        } else {
            return 0;
        }

    }

    @SuppressLint("NewApi")
    public boolean login(String data, String type) {
        getActualDataSap = "";
        getactualDataName = "";
        getGetActualDataBranch = "";
        getViews = "";
        try {

            URL url = new URL(Global.oneSearch1Url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String send = URLEncoder.encode("type", "UTF-8")+"="+URLEncoder.encode(type, "UTF-8")+"&"+
                    URLEncoder.encode("data", "UTF-8")+"="+URLEncoder.encode(data, "UTF-8");

            bufferedWriter.write(send);
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

            String response = sb.toString().trim();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;
            while (count < jsonArray.length()) {
                JSONObject rs = jsonArray.getJSONObject(count);
                getActualDataSap = getActualDataSap + rs.getString("sapid") + "---793526---";
                getactualDataName = getactualDataName + rs.getString("name") + "---793526---";
                getGetActualDataBranch = getGetActualDataBranch + rs.getString("branch") + "---793526---";
                getViews = getViews + rs.getString("views") + "---793526---";
                count++;
            }

            x=count;

            if(count>0)
                return true;
            else
                return false;
        } catch (Exception e) {
            error = e.toString();
            return false;
        }

    }


    public class OkSearch extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {

            imb.setEnabled(false);
            pd.show();
            startTime = System.currentTimeMillis();



        }

        @Override
        protected Void doInBackground(String... params) {
            isSuccess = login(params[0], params[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            imb.setEnabled(true);
            stopTime = System.currentTimeMillis();
            double time = (stopTime - startTime) / 1000;
            if (isSuccess) {
                sapidSearch = getActualDataSap.split("---793526---");
                nameSearch = getactualDataName.split("---793526---");
                branchSearch = getGetActualDataBranch.split("---793526---");
                viewSearch = getViews.split("---793526---");


                adpt = new Adapter(getActivity(), nameSearch, branchSearch, viewSearch);

                lv.setAdapter(adpt);

                String re = (x > 1) ? "results" : "result";
                tv.setText("About " + x + " " + re + " (" + time + " seconds)");

            } else {

                error = (error.isEmpty()) ? "Data Not Found!" : "No Internet Connection or Server is Down";
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setMessage(error)
                        .setTitle("Error!")
                        .setCancelable(true);
                AlertDialog alert = ad.create();
                alert.show();

            }
        }
    }


    public boolean retrieve(String sap) {

        try {

            URL url = new URL(Global.oneSearch2Url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String send = URLEncoder.encode("sapid", "UTF-8")+"="+URLEncoder.encode(sap, "UTF-8")+"&"+
                    URLEncoder.encode("toIncrease", "UTF-8")+"="+URLEncoder.encode(toIncrease, "UTF-8");

            bufferedWriter.write(send);
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

            String response = sb.toString().trim();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;
            while (count < jsonArray.length()) {
                JSONObject rs = jsonArray.getJSONObject(count);
                OPsap = rs.getString("sapid");
                OPname = rs.getString("name");
                OProll = rs.getString("roll");
                OPbranch = rs.getString("branch");
                OPyear = rs.getString("year");
                OPemail = rs.getString("email");
                OPphone = rs.getString("phone");
                OPpStatus = rs.getString("phoneStatus");
                OPImageUri = rs.getString("piclink");
                count++;
            }

            if (count > 0)
                return true;
            else
                return false;

        } catch (Exception e) {
            error = e.toString();
            return false;
        }


    }

    public class OtherPro extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            pdr.setMessage(Html.fromHtml("Retrieving <font color='#ec187f'><b>" + tempName + "'s</b></font> Data..."));
            pdr.show();

            c = Calendar.getInstance();

            Day = ""+c.get(Calendar.DATE)+""+c.get(Calendar.MONTH)+""+c.get(Calendar.YEAR);

            unique="";
            //Who is watching + to whom + day
            unique = sapId+Osap+Day;



            if(sDay.contains(unique))
            {
                if(Integer.parseInt(sDay.getString(unique,"0"))<3)
                {
                    eDay.putString(unique,String.valueOf(Integer.parseInt(sDay.getString(unique,"0")+1)));
                    eDay.commit();
                    toIncrease="yes";
                }else{
                    toIncrease = "no";
                }
            }else{
                eDay.putString(unique,"0");
                eDay.commit();
                toIncrease="yes";
            }

        }

        @Override
        protected Void doInBackground(String... params) {

            isRetrive = retrieve(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            pdr.dismiss();

            if (isRetrive) {
                //send data to OtherProfile.java
                Intent it = new Intent(getActivity(), OtherProfile.class);
                it.putExtra("name", OPname);
                it.putExtra("sap", OPsap);
                it.putExtra("roll", OProll);
                it.putExtra("branch", OPbranch);
                it.putExtra("phone", OPphone);
                it.putExtra("email", OPemail);
                it.putExtra("phoneStatus", OPpStatus);
                it.putExtra("year", OPyear);
                it.putExtra("imageUri", OPImageUri);

                startActivity(it);

            } else {
                //show error occurred during retrieval of data
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Something went wrong!")
                        .setMessage("Probably No internet connection or server is down")
                        .setCancelable(true);
                AlertDialog al = adb.create();
                al.show();
            }
        }
    }




    public void startitnow() {
        viewm = this.getActivity().getCurrentFocus();
        if (viewm != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewm.getWindowToken(), 0);
        }

        if (et.getText().toString().isEmpty()) {
            AlertDialog.Builder abs = new AlertDialog.Builder(getActivity());
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
                    AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                    ad.setMessage("Minimum 3 characters are required!")
                            .setTitle("Error!")
                            .setCancelable(true);
                    AlertDialog alert = ad.create();
                    alert.show();
                    break;
            }

        }
    }

}
