package com.matainja.door.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.matainja.door.R;
import com.matainja.door.model.LockHistoryModel;

import java.util.ArrayList;


public class HistoryListAdapter extends ArrayAdapter<LockHistoryModel> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<LockHistoryModel> mhistory;
    private int  mViewResourceId;

    public HistoryListAdapter(Context context, int tvResourceId, ArrayList<LockHistoryModel> historys){
        super(context, tvResourceId,historys);
        this.mhistory = historys;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        LockHistoryModel LockHistoryModel = mhistory.get(position);





        if (LockHistoryModel != null) {

          String door_status_val = LockHistoryModel.getDoorStatus();
            String door_operation_date_val = LockHistoryModel.getLockdatetime();
            String door_operation_by_val = LockHistoryModel.getUsername();
            Log.e("ggggh","=="+door_operation_by_val);

            TextView door_status = (TextView) convertView.findViewById(R.id.door_status);
            TextView door_operation_date = (TextView) convertView.findViewById(R.id.door_operation_date);
            TextView door_operation_by = (TextView) convertView.findViewById(R.id.door_operation_by);

            door_status.setText(door_status_val);
            door_operation_date.setText(door_operation_date_val);
            door_operation_by.setText(door_operation_by_val);
        }

        return convertView;
    }

}

