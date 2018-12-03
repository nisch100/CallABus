package com.example.nisch100.call_a_bus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<ListItem> {

    Context context;
    ArrayList<Bus> buses;
    int type; // used to difference between past or upcoming bus

    public CustomArrayAdapter(Context context, int textViewResourceId, List<ListItem> objects,
                              ArrayList<Bus> buses, int type) {
        super(context, textViewResourceId, objects);

        this.context = context;
        this.buses = buses;
        this.type = type;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtDate;
        TextView txtDestination;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ListItem rowItem = getItem(position);
        final Bus bus = buses.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.txtDate = (TextView) convertView.findViewById(R.id.date_time);
            holder.txtDestination = (TextView) convertView.findViewById(R.id.destination);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtDate.setText(rowItem.date.toString());
        holder.txtDestination.setText("Destination: " + rowItem.destination);
        Log.d("TAG", "Date: " + rowItem.date.toString() + " Destination: " + rowItem.destination);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == 0) { // past bus
                    Intent intent = new Intent(view.getContext(), PastBusSummaryActivity.class); //2nd param temp
                    intent.putExtra("bus", bus);
                    intent.putExtra("busID", position);
                    context.startActivity(intent);
                } else { // upcoming bus
                    Intent intent = new Intent(view.getContext(), UpcomingBusSummaryActivity.class); //2nd param temp
                    intent.putExtra("bus", bus);
                    intent.putExtra("busID", position);
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

}
