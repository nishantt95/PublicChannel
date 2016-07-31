package com.work.nishant.publicchannel;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Nishant on 1/2/2016.
 */
public class SkillAdapter extends ArrayAdapter<String> {

    String names[] ={};
    String paids[] ={};
    String levels[] ={};
    String sapids[]={};
    String skillVerifieds[]={};

    String setLevel="",setPaid="";

    int paid=0;
    int level=0;
    Context c;
    LayoutInflater inflater;

    public SkillAdapter(Context context, String names[], String paids[], String levels[], String sapids[],String skillVerifieds[]) {
        super(context, R.layout.skill_modified,names);

        this.c = context;
        this.names = names;
        this.paids = paids;
        this.levels = levels;
        this.sapids=sapids;
        this.skillVerifieds = skillVerifieds;
    }

    public class ViewHolder
    {
        TextView name;
        TextView paid;
        TextView level;
        ImageView im;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.skill_modified,null);
        }


        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) convertView.findViewById(R.id.textViewNameSkill);
        holder.paid = (TextView) convertView.findViewById(R.id.textViewPaidSkill);
        holder.level = (TextView) convertView.findViewById(R.id.textViewSkillLevel);
        holder.im = (ImageView) convertView.findViewById(R.id.imageViewVerified);

        paid = Integer.parseInt(paids[position]);
        level = Integer.parseInt(levels[position]);

        switch(paid)
        {
            case 0:
                setPaid ="<font color='#21A00D'><b>Free</b></font>";
                break;
            case 1:
                setPaid="<font color='#e15242'><b>Paid</b></font>";
        }

        switch(level)
        {
            case 0:
                setLevel="<font color='#FD6500'>Newbie</font>";
                break;
            case 1:
                setLevel="<font color='#21A00D'>Intermediate</font>";
                break;
            case 2:
                setLevel="<font color='#e15242'>Expert</font>";
                break;
        }

        holder.im.setVisibility(View.INVISIBLE);

        if(skillVerifieds[position].length()>5)
        {
            holder.im.setVisibility(View.VISIBLE);
        }

        holder.name.setText(names[position]);
        holder.level.setText(Html.fromHtml(setLevel));
        holder.paid.setText(Html.fromHtml(setPaid));




        return convertView;
    }
}
