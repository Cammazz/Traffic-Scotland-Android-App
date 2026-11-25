package com.example.corke_cameron_s2327100;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;

import androidx.lifecycle.ViewTreeLifecycleOwner;

import java.util.ArrayList;

public class SituationAdapter extends ArrayAdapter<Situation> {
    private Context context;
    private ArrayList<Situation> situations;
    public SituationAdapter(Context context, ArrayList<Situation> situations) {
        super(context, 0, situations);
        this.context = context;
        this.situations = situations;
    }
    @Override
            public View getView(int position, View convertView, ViewGroup parent) {
        Situation situation = situations.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.situation_list_item, parent, false);

        }
        TextView typeText = convertView.findViewById(R.id.situationType);
        TextView descriptionText = convertView.findViewById(R.id.situationDesc);
        ImageView icon = convertView.findViewById(R.id.situationIcon);

        String type = situation.getSituationRecord();
        if ("MaintenanceWorks".equals(type)) {
            icon.setImageResource(R.drawable.boxicons__traffic_cone);
        } else if ("RoadOrCarriagewayOrLaneManagement".equals(type)) {
            icon.setImageResource(R.drawable.uix_sharp__lane_ends);
        } else if ("GeneralObstruction".equals(type)) {
            icon.setImageResource(R.drawable.obstruction);
        } else if ("AbnormalTraffic".equals(type)) {
            icon.setImageResource(R.drawable.carbon__traffic_event);
        } else if ("Accident".equals(type)) {
            icon.setImageResource(R.drawable.tabler__car_crash);
        } else if ("VehicleObstruction".equals(type)) {
            icon.setImageResource(R.drawable.ic__outline_car_crash);
        }
        String delay = situation.getDelaysType();
        if (delay == null) {
            convertView.setBackgroundColor(Color.parseColor("#f5f4f4"));
        } else if (delay.equals("delays")) {
            convertView.setBackgroundColor(Color.parseColor("#FFF9C4"));
        } else if (delay.equals("longDelays")) {
            convertView.setBackgroundColor(Color.parseColor("#FFE0B2"));
        } else if (delay.equals("veryLongDelays")) {
            convertView.setBackgroundColor(Color.parseColor("#FFCDD2"));
        } else { convertView.setBackgroundColor(Color.parseColor("#f5f4f4")); }


        typeText.setText(situation.getSituationRecord());
        descriptionText.setText(situation.getGeneralPublicComment());

        convertView.setOnClickListener(v -> {
            String details = "Situation: " + situation.getSituationRecord()
                    + "\nRoad: " + (situation.getDescriptor() == null ? "Not Specified" : situation.getDescriptor())
                    + "\nDescription: " + situation.getGeneralPublicComment()
                    + "\nStart Time: " + situation.getOverallStartTime()
                + "\nEnd Time: " + (situation.getOverallEndTime() == null ? "Not Specified" : situation.getOverallEndTime());

            new AlertDialog.Builder(context)
                    .setTitle("Situation Details")
                    .setMessage(details)
                    .setPositiveButton("Close", null)
                    .show();
        });

        return convertView;
    }
}
