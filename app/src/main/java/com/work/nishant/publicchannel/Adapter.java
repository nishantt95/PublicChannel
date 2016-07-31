package com.work.nishant.publicchannel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Nishant on 12/22/2015.
 */
public class Adapter extends ArrayAdapter<String> {

    String names[] ={};
    String branches[] ={};
    String viewss[] ={};
    Context c;
    LayoutInflater inflater;

    public Adapter(Context context, String names[],String branches[], String viewss[]) {
        super(context, R.layout.linear_modified,names);

        this.c = context;
        this.names = names;
        this.branches = branches;
        this.viewss = viewss;
    }

    public class ViewHolder
    {
        TextView name;
        TextView branch;
        TextView views;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.linear_modified,null);
        }


        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) convertView.findViewById(R.id.textViewLinearName);
        holder.branch = (TextView) convertView.findViewById(R.id.textViewLinearBranch);
        holder.views = (TextView) convertView.findViewById(R.id.textViewLinearViews);


        holder.name.setText(names[position]);
        holder.branch.setText(branches[position]);

        String Vi = (Integer.parseInt(viewss[position])>1)?"views":"view";
        holder.views.setText(viewss[position] + " " + Vi);

        return convertView;
    }
}