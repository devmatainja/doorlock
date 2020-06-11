package com.matainja.door;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.matainja.door.Adapter.HistoryListAdapter;
import com.matainja.door.Database.DatabaseAdapter;
import com.matainja.door.model.LockHistoryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class HistoryLock extends AppCompatActivity  implements View.OnClickListener {


    private static final String TAG ="HistoryLock" ;
    Dialog DeviceNamedialog ;

    DatabaseAdapter dbHelper;
    String mSerialno;
    ListView lockHistory;
    HistoryListAdapter mHistoryListAdapter;
    public ArrayList<LockHistoryModel> mArrayHistory = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);
        DeviceNamedialog = new Dialog(HistoryLock.this);

        DeviceNamedialog.setContentView(R.layout.layout_dialog_device_name);
        lockHistory = (ListView) findViewById(R.id.lockhistory);
        // database Connection Open
        dbHelper = new DatabaseAdapter(this);
        dbHelper.open();

        Cursor device = dbHelper.getDevice();

        if(device.getCount() > 0)
        {
            device.moveToFirst();
            String mName = device.getString(device.getColumnIndex("name"));
            mSerialno =device.getString(device.getColumnIndex("serial_no"));
            Log.e("Device","=="+mName);



        }

        CheckRealTimeDataBase();

         mHistoryListAdapter = new HistoryListAdapter(this, R.layout.lockhistory_adapter_view, mArrayHistory);
        lockHistory.setAdapter(mHistoryListAdapter);
    }


    int isHistoryKeyExist(ArrayList<LockHistoryModel> list, String key) {

        Log.e("Marker", "----MarkerModel list--??---**: " + list);
        for (LockHistoryModel item : list) {
            if (item.getKey().equals(key)) {
                return list.indexOf(item);
            }
        }
        return -1;
    }

    private void CheckRealTimeDataBase()
    {

        Log.d(TAG, "CheckRealTimeDataBase is: " + mSerialno);
        if(mSerialno.isEmpty())
            return;



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("doorAction").child(mSerialno).child("doorhistory");

        if(myRef==null)
            return;
        Query myTopPostsQuery = myRef.orderByChild("door_window_status").limitToLast(1000);

// Read from the database
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue(true);
                if(value==null)
                    return;

                Gson gson = new Gson();
                String response = gson.toJson(value);
                JSONObject doorHistory;
                JSONObject eachHistory;
                JSONArray history;
                try {

                    doorHistory = new JSONObject(response);
             int modifyAdapter=0;
                    Iterator x = doorHistory.keys();
                    JSONArray jsonArray = new JSONArray();
                    history =doorHistory.toJSONArray(doorHistory.names());
                    int i =0;
                    while (x.hasNext()){
                        String key = (String) x.next();

                        LockHistoryModel ItemHistory =new LockHistoryModel();

                        eachHistory = history.getJSONObject(i);
                        int posHistoryIndex=0;

                        //jsonArray.put(doorHistory.get(key));

                        if(!key.isEmpty())
                        {
                            ItemHistory.setKey(key);

                            posHistoryIndex = isHistoryKeyExist(mArrayHistory,key);
                        }
/// if history already Exist
                        if(posHistoryIndex == -1) {
                            modifyAdapter=1;
                            if (eachHistory.has("datetime")) {
                                String datetime = eachHistory.getString("datetime");

                                Log.e(TAG, key + " =History= " + datetime);
                                datetime=Constants.getDate(datetime);
                                ItemHistory.setLockdatetime(datetime);
                            }

                            if (eachHistory.has("door_window_lock_status")) {
                                String door_window_lock_status = eachHistory.getString("door_window_lock_status");


                                ItemHistory.setDoorStatus(door_window_lock_status);
                            }
                            if (eachHistory.has("door_window_status")) {
                                String door_window_status = eachHistory.getString("door_window_status");


                                ItemHistory.setWindowStatus(door_window_status);
                            }

                            if (eachHistory.has("open_user_by")) {
                                String open_user_by = eachHistory.getString("open_user_by");


                                ItemHistory.setUsername(open_user_by);
                            }

                            if (eachHistory.has("open_user_device_id")) {
                                String open_user_device_id = eachHistory.getString("open_user_device_id");


                                ItemHistory.setUserId(open_user_device_id);
                            }
                            mArrayHistory.add(ItemHistory);
                        }
                        i++;


                    }


                    /*

                    //history = doorHistory.getJSONArray();
                    for (int i=0;i<history.length();i++) {




                        Log.e(TAG,"Door History"+doorHistory.toString());

                        JSONObject resultObj=history.getJSONObject(i);

                        LockHistoryModel ItemHistory =new LockHistoryModel();

                        if(resultObj.has("datetime"))
                        {
                            String datetime= resultObj.getString("datetime");
                            ItemHistory.setLockdatetime(datetime);

                        }

                        if(resultObj.has("datetime"))
                        {
                            String datetime= resultObj.getString("datetime");
                            ItemHistory.setLockdatetime(datetime);

                        }


                       // Log.e(TAG,"Door History"+resultObj.toString());

                    }
*/







                    if(modifyAdapter == 1)

                    mHistoryListAdapter.notifyDataSetChanged();



                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "CheckRealTimeDataBase is: Parse error" );
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }


    @Override
    protected void onPause() {
        super.onPause();

    }



    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.wifiSettingbtnlayout:

                break;


        }
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;









}
