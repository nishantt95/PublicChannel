package com.work.nishant.publicchannel;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

public class Attendance_Detail extends AppCompatActivity {

    NumberPicker numberPicker;
    TextView subName,atShow,cla,curAtt;
    RadioGroup rg;
    int difference=0;

    String which="attend";

    String subject="";
    double classesHeld=0,classesAttended=0,attendance=0;

    TableRow tr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance__detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        subName = (TextView) findViewById(R.id.textViewSubjectD);
        rg = (RadioGroup) findViewById(R.id.radioGroupDetail);
        atShow = (TextView) findViewById(R.id.textViewAttendanceShow);
        cla = (TextView) findViewById(R.id.textViewss);
        tr = (TableRow) findViewById(R.id.trSubRow);
        curAtt = (TextView) findViewById(R.id.textViewCurrentAttendance);
        cla.setText("Class");


        subject = getIntent().getExtras().getString("subject");
        classesHeld = Double.parseDouble(getIntent().getExtras().getString("classesHeld"));
        classesAttended = Double.parseDouble(getIntent().getExtras().getString("classesAttended"));
        attendance = Double.parseDouble(getIntent().getExtras().getString("attendance"));



        if(attendance>=75)
        {
            atShow.setText(Html.fromHtml("<font color='#1D900B'>Your attendance is : "+attendance+"</font>"));
            subName.setText(subject);
            tr.setBackgroundColor(Color.rgb(33, 160, 13));
            curAtt.setText(Html.fromHtml("<font color='#1D900B'>Your current attendance is : " + attendance + "</font>"));
        }else if(attendance>=67 && attendance<75)
        {
            atShow.setText(Html.fromHtml("<font color='#FD6500'>Your attendance is : "+attendance+"</font>"));
            subName.setText(subject);
            tr.setBackgroundColor(Color.rgb(253, 101, 0));
            curAtt.setText(Html.fromHtml("<font color='#1D900B'>Your current attendance is : " + attendance + "</font>"));
        }else{
            atShow.setText(Html.fromHtml("<font color='#D8311E'>Your attendance is : "+attendance+"</font>"));
            subName.setText(subject);
            tr.setBackgroundColor(Color.rgb(216, 49, 30));
            curAtt.setText(Html.fromHtml("<font color='#1D900B'>Your current attendance is : " + attendance + "</font>"));
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedLevel = rg.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(selectedLevel);

                if (rb.getText().toString().equalsIgnoreCase("On Attending")) {
                    which = "attend";
                } else if (rb.getText().toString().equalsIgnoreCase("On Leaving")) {
                    which = "leave";
                }
                calculate(which,difference);
            }
        });

        numberPicker.setMaxValue(7);
        numberPicker.setMinValue(0);

        numberPicker.setWrapSelectorWheel(false);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                difference = newVal;

                calculate(which,difference);
            }
        });

    }

    public void calculate(String which,int difference)
    {
        String cuAt = ""+attendance;
        String show = "";
        Double x=0.0;
        String attach="";

        if(difference>1)
            cla.setText("Classes");
        else
            cla.setText("Class");

        if(difference==0)
        {
            show = cuAt;
            x = attendance;
            attach = "Your attendance is : ";
        }else{
            attach = "Your attendance will be : ";
            if(which.equalsIgnoreCase("attend"))
            {
                Double at = classesAttended+difference;
                Double he = classesHeld+difference;
                x = ((at*100)/he);
                show = ""+x;

            }else{
                Double at = classesAttended;
                Double he = classesHeld+difference;
                x = ((at*100)/he);
                show = ""+x;
            }
        }

        show = (show.length()>5)?(show.substring(0,5)):(show);
        if(x>=75)
        {
            atShow.setText(Html.fromHtml("<font color='#1D900B'>"+attach+show+"</font>"));
            subName.setText(subject);
            tr.setBackgroundColor(Color.rgb(33, 160, 13));
        }else if(x>=67 && x<75)
        {
            atShow.setText(Html.fromHtml("<font color='#FD6500'>"+attach+show+"</font>"));
            subName.setText(subject);
            tr.setBackgroundColor(Color.rgb(253, 101, 0));
        }else{
            atShow.setText(Html.fromHtml("<font color='#D8311E'>"+attach+show+"</font>"));
            subName.setText(subject);
            tr.setBackgroundColor(Color.rgb(216, 49, 30));
        }

    }

}
