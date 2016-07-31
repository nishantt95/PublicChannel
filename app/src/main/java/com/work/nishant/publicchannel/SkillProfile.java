package com.work.nishant.publicchannel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by Nishant on 1/2/2016.
 */
public class SkillProfile extends AppCompatActivity {

    String name="",sap="",skillSet="",skillType="",paidType="",description="";

    TextView tvName,tvSkillType,tvSap,tvSkillSet,tvPaidType,tvDescription;

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    Button bt,btp;

    int paidP=0;
    int levelP=0;
    String setPaidP="",setLevelP="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skill_profile);

        tvName = (TextView) findViewById(R.id.SkillProfileName);
        tvSkillType = (TextView) findViewById(R.id.SkillProfileSkillType);
        tvSap   =   (TextView) findViewById(R.id.SkillProfileSap);
        tvSkillSet  =   (TextView) findViewById(R.id.SkillProfileSkillSet);
        tvPaidType  =   (TextView) findViewById(R.id.SkillProfilePaidType);
        tvDescription   =   (TextView) findViewById(R.id.SkillProfileDescription);
        bt = (Button) findViewById(R.id.buttonFbSkill);

        //admob activity...............................................................................

        AdView mAdView;
        AdRequest adRequest;

        mAdView = (AdView) findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        //admob activity.................................................................................

        btp = (Button) findViewById(R.id.buttonToSkill);

        sp = getApplicationContext().getSharedPreferences("shared", 0);
        ed =sp.edit();

        if(sp.getString("facebook","no").contentEquals("yes"))
        {
            bt.setVisibility(View.INVISIBLE);
        }

        /*
                it.putExtra("name",OPname);
                it.putExtra("sap",OPsap);
                it.putExtra("skillSet",OPskillSet);
                it.putExtra("paidType",OPpaidType);
                it.putExtra("skillType",OPskillType);
                it.putExtra("description",OPdescription);
         */



        name = getIntent().getStringExtra("name");
        sap = getIntent().getStringExtra("sap");
        skillSet = getIntent().getStringExtra("skillSet");
        paidType = getIntent().getStringExtra("paidType");
        skillType = getIntent().getStringExtra("skillType");
        description = getIntent().getStringExtra("description");


        paidP = Integer.parseInt(paidType);
        levelP = Integer.parseInt(skillType);

        switch(paidP)
        {
            case 0:
                setPaidP ="<b>$$</b> : <font color='#21A00D'><b>Free</b></font>";
                break;
            case 1:
                setPaidP="<b>$$</b> : <font color='#e15242'><b>Paid</b></font>";
        }

        switch(levelP)
        {
            case 0:
                setLevelP="<b>Experience</b> : <font color='#FD6500'>Newbie</font>";
                break;
            case 1:
                setLevelP="<b>Experience</b> : <font color='#21A00D'>Intermediate</font>";
                break;
            case 2:
                setLevelP="<b>Experience</b> : <font color='#e15242'>Expert</font>";
                break;
        }

        tvName.setText(name);
        tvSap.setText(Html.fromHtml("<b>SapID</b> :"+sap));
        tvSkillType.setText(Html.fromHtml(setLevelP));
        tvPaidType.setText(Html.fromHtml(setPaidP));
        tvSkillSet.setText(Html.fromHtml("<b>Knows</b> : <font color='black'>"+skillSet+"</font>"));
        tvDescription.setText(Html.fromHtml("<b>Description:</b><br><br>"+description));

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(SkillProfile.this, FbLogin.class);
                startActivity(it);
            }
        });

        btp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(SkillProfile.this, SkillPost.class);
                startActivity(it);
            }
        });


    }
}
