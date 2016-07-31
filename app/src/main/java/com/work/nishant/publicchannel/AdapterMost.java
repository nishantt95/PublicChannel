package com.work.nishant.publicchannel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

/**
 * Created by Nishant on 1/3/2016.
 */
public class AdapterMost extends ArrayAdapter<String> {

    String names[] = {};
    String branches[] = {};
    String viewss[] = {};

    Context c;
    LayoutInflater inflater;

    public AdapterMost(Context context, String names[], String branches[], String viewss[]) {
        super(context, R.layout.most_modified, names);

        this.c = context;
        this.names = names;
        this.branches = branches;
        this.viewss = viewss;

    }

    public class ViewHolder {
        TextView name;
        TextView branch;
        TextView views;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        if (convertView == null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.most_modified, null);
        }


        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) convertView.findViewById(R.id.textViewMostName);
        holder.branch = (TextView) convertView.findViewById(R.id.textViewMostBranch);
        holder.views = (TextView) convertView.findViewById(R.id.textViewMostViews);



        holder.name.setText(names[position]);
        holder.branch.setText(branches[position]);

        if(viewss[position].isEmpty()||viewss[position].contentEquals(""))
            viewss[position]="0";

        String Vi = (Integer.parseInt(viewss[position]) > 1) ? "views" : "view";
        holder.views.setText(viewss[position] + " " + Vi);

        return convertView;
    }

}