package com.example.nisch100.call_a_bus;

import android.app.Activity;
import android.content.Context;
import android.view.*;
import android.widget.*;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<ListItem> {

    Context context;

    public CustomArrayAdapter(Context context, int textViewResourceId, List<ListItem> objects) {
        super(context, textViewResourceId, objects);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtDate;
        TextView txtDestination;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ListItem rowItem = getItem(position);

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

        holder.txtDate.setText(rowItem.date + ", " + rowItem.time);
        holder.txtDestination.setText("Destination: " + rowItem.destination);

        return convertView;
    }

}
