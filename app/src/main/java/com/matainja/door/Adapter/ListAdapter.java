package com.matainja.door.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.matainja.door.R;
import com.matainja.door.model.WifiModel;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<WifiModel> {

    private int resourceLayout;
    private Context mContext;

    public ListAdapter(Context context, int resource, ArrayList<WifiModel> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        WifiModel p = getItem(position);

        if (p != null) {

            TextView wifi_name = (TextView) v.findViewById(R.id.wifi_name);
            wifi_name.setText(p.getName());

        }

        return v;
    }
}