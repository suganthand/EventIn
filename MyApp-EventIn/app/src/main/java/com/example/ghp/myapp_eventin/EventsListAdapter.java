package com.example.ghp.myapp_eventin;

/**
 * Created by ghp on 7/11/16.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;




public class EventsListAdapter extends ArrayAdapter<Event> {

    ArrayList<Event> events;
    Context context;

    public EventsListAdapter(Activity context, ArrayList<Event> events) {
        super(context, R.layout.list_of_events, events);
        this.events = events;
        this.context = context;
    }

    public Event get(int position) {
        return events.get(position); // or maybe mList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_of_events, null, true);

        TextView Title = (TextView) row.findViewById(R.id.title);
        TextView
                Location= (TextView) row.findViewById(R.id.location);
        TextView
                StartTime= (TextView) row.findViewById(R.id.startT);


        Title.setText(events.get(position).title);
        Location.setText(events.get(position).location);
        StartTime.setText(events.get(position).date_start);

        return row;
    }
}