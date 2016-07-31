package com.work.nishant.publicchannel;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Nishant on 1/8/2016.
 */
public class AttendanceAdapter extends ArrayAdapter<String> {

    String subject[] ={};
    String classesHeld[] ={};
    String classesAttended[] ={};
    String absentDate[]={};
    String attendance[]={};
    String projection[]={};

    Context c;
    LayoutInflater inflater;

    public AttendanceAdapter(Context context, String subject[],String classesHeld[], String classesAttended[],String absentDate[],String attendance[], String projection[]) {
        super(context, R.layout.attendance_modified,subject);

        this.c = context;
        this.subject = subject;
        this.classesHeld = classesHeld;
        this.classesAttended = classesAttended;
        this.absentDate = absentDate;
        this.attendance = attendance;
        this.projection = projection;
    }

    public class ViewHolder
    {
        ProgressBar pb;
        TextView tvSubject,tvPercent,tvAttended,tvProjected,tvAbsent,tvMiss;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.attendance_modified,null);
        }

        ViewHolder holder = new ViewHolder();
        holder.tvSubject = (TextView) convertView.findViewById(R.id.textViewSubject);
        holder.tvPercent = (TextView) convertView.findViewById(R.id.textViewAttendancePercent);
        holder.tvAttended = (TextView) convertView.findViewById(R.id.textViewClassesAttended);
        holder.tvProjected = (TextView) convertView.findViewById(R.id.textViewProjectedAttendance);
        holder.tvAbsent = (TextView) convertView.findViewById(R.id.textViewDaysAbsent);
        holder.tvMiss = (TextView) convertView.findViewById(R.id.textViewSuggestion);
        holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBarAttendance);
        holder.pb.setMax(100);

        /*

        this.subject = subject;
        this.classesHeld = classesHeld;
        this.classesAttended = classesAttended;
        this.absentDate = absentDate;
        this.attendance = attendance;
        this.projection = projection;
         */

        double s = Double.parseDouble(attendance[position]);
        int x = (int)s;


        double Dgoes = Double.parseDouble(classesAttended[position].trim());
        double Dtotal = Double.parseDouble(classesHeld[position].trim());


        int can = 0;
        double chk;


        if(x>=75) {

            Dtotal++;
            chk = ((Dgoes/Dtotal)*100);

            while(chk>=75)
            {
                can++;
                Dtotal++;
                chk = ((Dgoes/Dtotal)*100);
            }

            holder.tvMiss.setText(Html.fromHtml("<font color='#1D900B'>You can safely miss <b>"+can+"</b> classes</font>"));
            holder.pb.getProgressDrawable().setColorFilter(Color.rgb(33, 160, 13), PorterDuff.Mode.SRC_IN);
            holder.tvPercent.setText(Html.fromHtml("<font color='#21A00D'>" + attendance[position] + "%</font>"));


        }else if(x>=67 && x<75)
        {
            chk = ((Dgoes/Dtotal)*100);

            while(chk<75)
            {
                can++;
                Dgoes++;
                Dtotal++;
                chk = ((Dgoes/Dtotal)*100);
            }

            holder.pb.getProgressDrawable().setColorFilter(Color.rgb(253,101,0), PorterDuff.Mode.SRC_IN);
            holder.tvPercent.setText(Html.fromHtml("<font color='#FD6500'>" + attendance[position] + "%</font>"));
            holder.tvMiss.setText(Html.fromHtml("<font color='#FD6500'>Attend <b>"+can+"</b> classes to reach 75%</font>"));

        }else{

            chk = ((Dgoes/Dtotal)*100);

            while(chk<67)
            {
                can++;
                Dgoes++;
                Dtotal++;
                chk = ((Dgoes/Dtotal)*100);
            }


            holder.pb.getProgressDrawable().setColorFilter(Color.rgb(216, 49, 30), PorterDuff.Mode.SRC_IN);
            holder.tvPercent.setText(Html.fromHtml("<font color='#D8311E'>" + attendance[position] + "%</font>"));
            holder.tvMiss.setText(Html.fromHtml("<font color='#D8311E'>Attend <b>"+can+"</b> classes to reach 67%</font>"));
        }

        holder.tvSubject.setText(subject[position]);
        holder.tvAttended.setText(Html.fromHtml("Classes attended : <b>"+classesAttended[position]+"</b>/<b>"+classesHeld[position]+"</b>"));
        holder.tvProjected.setText("Projected Attendance : "+projection[position]+"%");
        holder.tvAbsent.setText(Html.fromHtml("<b>Days Absent</b> : "+absentDate[position]));


        ObjectAnimator animation = ObjectAnimator.ofInt(holder.pb, "progress", x);
        animation.setDuration(2000); // 2 sec
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        return convertView;
    }
}