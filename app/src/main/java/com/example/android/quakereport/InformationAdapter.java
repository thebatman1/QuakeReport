package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by HP on 2/26/2017.
 */

public class InformationAdapter extends ArrayAdapter<Information> {

    public InformationAdapter(Context context , ArrayList<Information> informations) {
        super(context , 0 , informations);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent , false);

            holder = new ViewHolder();
            holder.magnitude = (TextView) convertView.findViewById(R.id.magnitude);
            holder.locationoffset = (TextView) convertView.findViewById(R.id.locationoffset);
            holder.location = (TextView) convertView.findViewById(R.id.location);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        Information info = getItem(position);
        double mag = Double.parseDouble(info.getMagnitude());
        DecimalFormat formatter = new DecimalFormat("0.0");
        holder.magnitude.setText(formatter.format(mag));


        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.magnitude.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(info.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        String sp = info.getLocation();
        if(sp.contains("of")) {
            holder.locationoffset.setText(sp.substring(0,sp.indexOf("of")+2));
            holder.location.setText(sp.substring(sp.indexOf("of")+3));
        }
        else {
            holder.locationoffset.setText("Near the");
            holder.location.setText(sp);
        }

        String[] date = info.getTimestamp().split("\n");
        holder.date.setText(date[0]);
        holder.time.setText(date[1]);
        return convertView;
    }

    public static class ViewHolder {
        TextView magnitude , locationoffset , location , date , time;
    }

    private int getMagnitudeColor(String magnitude) {
        double mag = Double.parseDouble(magnitude);
        int magint = (int) Math.floor(mag);
        switch(magint) {
            case 0:
            case 1: return ContextCompat.getColor(getContext() , R.color.magnitude1);
            case 2: return ContextCompat.getColor(getContext() , R.color.magnitude2);
            case 3: return ContextCompat.getColor(getContext() , R.color.magnitude3);
            case 4: return ContextCompat.getColor(getContext() , R.color.magnitude4);
            case 5: return ContextCompat.getColor(getContext() , R.color.magnitude5);
            case 6: return ContextCompat.getColor(getContext() , R.color.magnitude6);
            case 7: return ContextCompat.getColor(getContext() , R.color.magnitude7);
            case 8: return ContextCompat.getColor(getContext() , R.color.magnitude8);
            case 9: return ContextCompat.getColor(getContext() , R.color.magnitude9);
            default:return ContextCompat.getColor(getContext() , R.color.magnitude10plus);
        }
    }
}
