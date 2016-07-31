package com.work.nishant.publicchannel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
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
public class oneSkill extends Fragment {

    EditText et;
    Button bt,btEnable;
    ListView lv;

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    TextView tv;

    View viewm=null;

    String OPsap="",OPname="",OPskillSet="",OPpaidType="",OPskillType="",OPdescription="";
    String Osap="",tempName="",Day="",unique="",sapId="";

    ProgressDialog pd,pdInc,pdr;
    boolean isRetrieved=false;
    SkillAdapter adpt;
    int x =0;

    Calendar c;

   String toIncrease = "yes";

    SharedPreferences sps,sDay;
    SharedPreferences.Editor edt,eDay;

    String skillSap="",skillPaidType="",skillName="",skillType="",skillVerified="",error="",searchSkill="";

    String skillSaps[]={},skillPaidTypes[]={},skillNames[]={},skillTypes[]={},skillVerifieds[]={};

    boolean isRetrive=false;

    String userSkillSet="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.skill,container,false);

        et = (EditText) view.findViewById(R.id.editTextSkillSearch);
        bt = (Button) view.findViewById(R.id.buttonSearchSkillP);
        lv = (ListView) view.findViewById(R.id.listViewDisplaySkill);

        btEnable = (Button) view.findViewById(R.id.buttonSkillEnable);

        tv = (TextView) view.findViewById(R.id.textViewTotalSkill);
        String setIt = "Skill search is a feature of searching students on the basis of their skills. Whether you are looking for students who know C, C++, Java, Android, Web, etc (technical skills) or students who have some management skills or students with different talents then this is a right place to start your search.<br><br>"
                 +
                "For an individual, this is right place to show off your skills and talents. Just update your profile and wait to get searched. An individual will receive 2 views per search if he/she will be searched here at skill search.<br><br>";
        tv.setText(Html.fromHtml(setIt));

        sDay = getActivity().getApplication().getSharedPreferences("sDay", 0);
        eDay = sDay.edit();

        sps = getActivity().getApplication().getSharedPreferences("shared", 0);
        edt = sps.edit();

        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) view.findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        //admob activity.................................................................................

        userSkillSet = sps.getString("skillset","");

        if(userSkillSet.isEmpty())
        {
            //hide the search button here..
            bt.setVisibility(View.GONE);
            btEnable.setVisibility(View.VISIBLE);

        }else{
            bt.setVisibility(View.VISIBLE);
            btEnable.setVisibility(View.GONE);
        }


        btEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(),SkillPost.class);
                startActivity(it);
            }
        });

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Searching skill...");
        pd.dismiss();

        pdInc = new ProgressDialog(getActivity());
        pdInc.setMessage("Synchronizing data...");
        pdInc.dismiss();

        sapId = sps.getString("sapid", "0");

        pdr = new ProgressDialog(getActivity());

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewm = getActivity().getCurrentFocus();
                if (viewm != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewm.getWindowToken(), 0);
                }

                if(et.getText().toString().isEmpty())
                {
                    AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                    ad.setMessage("Can't be empty!")
                            .setTitle("Error!")
                            .setCancelable(true);
                    AlertDialog alert = ad.create();
                    alert.show();

                }else{
                    searchSkill = et.getText().toString();
                    new FindSkill().execute(searchSkill);
                }
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(Search.this,sapidSearch[position],Toast.LENGTH_LONG).show();

                tempName = skillNames[position];
                Osap = skillSaps[position];
                new OtherPro().execute(Osap);

            }
        });


        return view;
    }


    @SuppressLint("NewApi")
    public boolean login(String skill)
    {
        skillSap="";
        skillPaidType="";
        skillName="";
        skillType="";
        skillVerified="";
        error="";



        x=0;
        try {

            URL url = new URL(Global.oneSkill1Url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String send = URLEncoder.encode("skill", "UTF-8")+"="+URLEncoder.encode(skill, "UTF-8");

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
                skillSap = skillSap + rs.getString("sapid")+"---793526---";
                skillName = skillName + rs.getString("name")+"---793526---";
                skillPaidType = skillPaidType + rs.getString("paidtype")+"---793526---";
                skillType = skillType + rs.getString("skilltype")+"---793526---";
                skillVerified =(rs.getString("piclink")==null||rs.getString("piclink").isEmpty())?(skillVerified+" ---793526---"):(skillVerified+rs.getString("piclink")+"---793526---");
                count++;
            }

            x=count;

            if(count>0) {
                return true;
            }
            else {
                return false;

            }

        }
        catch (Exception e){error = e.toString();
            return false;


        }
    }

    class FindSkill extends AsyncTask<String,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            pd.show();
            bt.setEnabled(false);
        }

        @Override
        protected Void doInBackground(String... params) {
            isRetrieved = login(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            bt.setEnabled(true);
            if(isRetrieved)
            {
                skillSaps = skillSap.split("---793526---");
                skillPaidTypes=skillPaidType.split("---793526---");
                skillNames=skillName.split("---793526---");
                skillTypes=skillType.split("---793526---");
                skillVerifieds = skillVerified.split("---793526---");

                Log.e("skillverified------", skillVerified);

                adpt = new SkillAdapter(getActivity(),skillNames,skillPaidTypes,skillTypes,skillSaps,skillVerifieds);
                lv.setAdapter(adpt);

                tv.setText(Html.fromHtml("<b>" + x + "</b> people have <b>" + searchSkill + "</b> skills"));

            }else{
                //error = (error.isEmpty())?"No data available":"Server is down";
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
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

            URL url = new URL(Global.oneSkill2Url);
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
                OPsap= rs.getString("sapid");
                OPname=rs.getString("name");
                OPdescription = (rs.getString("description")==null || rs.getString("description").isEmpty())?"No Description":rs.getString("description");
                OPpaidType=rs.getString("paidtype");
                OPskillSet=rs.getString("skillset");
                OPskillType = rs.getString("skilltype");
                count++;
            }

            if(count>0)
                return true;
            else
                return false;

        }
        catch (Exception e){error = e.toString();
            return false;
        }


    }

    public class OtherPro extends AsyncTask<String,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            pdr.setMessage(Html.fromHtml("Retrieving <font color='#ec187f'><b>"+tempName+"'s</b></font> Data..."));
            pdr.show();

            c = Calendar.getInstance();

            Day = ""+c.get(Calendar.DATE)+""+c.get(Calendar.MONTH)+""+c.get(Calendar.YEAR);

            unique="";
            //Who is watching + to whom + day
            unique = sapId+Osap+Day;

            Log.e("Unique is:-", unique);

            if(sDay.contains(unique))
            {
                if(Integer.parseInt(sDay.getString(unique,"0"))<3)
                {
                    eDay.putString(unique,String.valueOf(Integer.parseInt(sDay.getString(unique,"0")+1)));
                    eDay.commit();
                    toIncrease = "yes";
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

            isRetrive =  retrive(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            pdr.dismiss();

            if(isRetrive)
            {
                //String OPsap="",OPname="",OPskillSet="",OPpaidType="",OPskillType="",OPdescription="";

                //send data to OtherProfile.java
                Intent it = new Intent(getActivity(),SkillProfile.class);
                it.putExtra("name",OPname);
                it.putExtra("sap",OPsap);
                it.putExtra("skillSet",OPskillSet);
                it.putExtra("paidType",OPpaidType);
                it.putExtra("skillType",OPskillType);
                it.putExtra("description",OPdescription);
                startActivity(it);

            }else{
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

}
