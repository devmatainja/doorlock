package com.matainja.door;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.matainja.door.Database.DatabaseAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class InternetDashBoard extends AppCompatActivity implements View.OnClickListener {
    ImageView mlogout;
    Dialog dialogLoader ;
    ImageView mDoorAction;
SpinKitView mdoor_loder;
    TextView mDoor_status;
    ImageView monline;
    TextView mdevice_online;
    TextView mdevice_name;
    DatabaseAdapter dbHelper;
    String TAG="Error";
    String mSerialno;
    TextView door_open_time_value,door_open_is_value,unlock_user_by;
    String mDoorStatus,mDoorOpenStatus;
    static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    int delay = 5000; // delay for 5 sec.
    int period = 300000; //  5 min repeat every sec.
    int isOffline=1;
    CardView device_history_layout;
    SessionManager sessionManager;
    String mUsername;
    FirebaseDatabase database;
    Map<String, String> TIMESTAMP;
    public Object creationDate = ServerValue.TIMESTAMP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        // Create Instance of realtimeDatabase

        database = FirebaseDatabase.getInstance();



        dialogLoader = new Dialog(this);
        dialogLoader.setContentView(R.layout.layout_dialog_popup_loader);


        mDoorAction = findViewById(R.id.door_action);
        mdoor_loder = findViewById(R.id.door_loder);
        mDoor_status = findViewById(R.id.door_status);
        monline = findViewById(R.id.online);
        mlogout =findViewById(R.id.logout);
        mdevice_online =findViewById(R.id.device_online);
        mdevice_name = findViewById(R.id.device_name);
        door_open_time_value =findViewById(R.id.door_open_time_value);
        door_open_is_value = findViewById(R.id.door_open_is_value);
        unlock_user_by =findViewById(R.id.unlock_user_by);
        device_history_layout =findViewById(R.id.device_history_layout);

        mlogout.setOnClickListener(this);
        mDoorAction.setOnClickListener(this);
        device_history_layout.setOnClickListener(this);
        // database Connection Open
        dbHelper = new DatabaseAdapter(this);
        dbHelper.open();
        sessionManager = new SessionManager(this);
        Cursor device = dbHelper.getDevice();
        FirebaseTime();
       if(device.getCount() > 0)
       {
           device.moveToFirst();
           String mName = device.getString(device.getColumnIndex("name"));
           mSerialno =device.getString(device.getColumnIndex("serial_no"));
           Log.e("Device","=="+mName);

           if(!mName.isEmpty())
           {
               mdevice_name.setText(mName);
           }

           HashMap<String, String> user = sessionManager.getUser();
            mUsername = user.get(SessionManager.KEY_USER_NAME);

       }
        //check device offline or not
       // CheckOnline();


        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                Log.e("GGG","timer");
                Log.e("time",ServerValue.TIMESTAMP.toString());
                if(isOffline==0)
                {
                    eDeviceUpdate("offline");

                    Log.e("GGG","offline");
                    isOffline=1;
                }
            }

        }, delay, period);







        OnlieCheckDatabase();
        CheckRealTimeDataBase();
        ReadLastOpenDtate();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.logout:
                // Do something
                //dialogLoader.show();
                dbHelper.emptyDevice();
                sessionManager.logOutUser();

               Intent initIntent = new Intent(InternetDashBoard.this, RegisterActivity.class);

                startActivity(initIntent);
                finish();
                //LogOutDevice();
              break;
            case R.id.door_action:
                DoorAction();
                break;

            case R.id.device_history_layout:
                getHistory();
                break;





        }

    }

        private void getHistory()
        {
            Log.d(TAG, "getHistory is: " + mSerialno);
            Intent initIntent = new Intent(InternetDashBoard.this, HistoryLock.class);

            startActivity(initIntent);
        }
    private void CheckOnline(){



        StringRequest postRequest = new StringRequest(Request.Method.GET, Constants.DEVICE_DOOR_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Error", "-----Response Result----: "+response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    boolean online = jsonObject.getBoolean("online");
                    String door_status = jsonObject.getString("door");
                  //  mdoor_loder.setVisibility(View.INVISIBLE);
                    if(status.equals("200"))
                    {
                        if(online)
                        {
                            deviceStatusOnline();

                            if(door_status.equals("high"))
                            {
                                mDoorAction.setImageResource(R.drawable.door_close_action);
                                mDoor_status.setText("Locked");
                            }
                            else
                            {
                                mDoorAction.setImageResource(R.drawable.door_open_action);
                                mDoor_status.setText("Unlock");
                            }
                        }else
                        {
                            deviceStatusOffline();
                        }

                    }
                    //Toast.makeText(getApplicationContext(), "error"+jsonObject.toString(), Toast.LENGTH_LONG).show();



                }catch (JSONException e){
                    e.printStackTrace();


                    monline.setImageResource(R.drawable.ic_wifi_red);
                    mdevice_online.setText("Offline");
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                monline.setImageResource(R.drawable.ic_wifi_red);
                mdevice_online.setText("Offline");
                Toast.makeText(getApplicationContext(), "Device is offline, Please off Mobile data And connect to wifi", Toast.LENGTH_LONG).show();
                Log.e("Error", "-----VollyError----: "+error.getMessage());

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                //params.put("Token-Access",session_token);
                // params.put("Content-Type",Constraints.CONTENT_TYPE);
                Log.e("Send Data: ","**-Head-*-*--: "+params);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                // params.put("ssid",device_ssid);
                // // params.put("device_no",simNo.getText().toString());
                // params.put("pass",device_password);
                // params.put("email",emailaddress.getText().toString());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(postRequest);
    }


    private void eDeviceUpdate(String onlineStatus)
    {

        Log.d(TAG, "Value is: " + mSerialno);
        if(mSerialno.isEmpty())
            return;


        DatabaseReference myRef = database.getReference().child("doorAction").child(mSerialno).child("doorOnline").child("device_online");

        if(myRef==null)
            return;

   myRef.setValue(onlineStatus);

    }


    private void FirebaseTime()
    {



// Read from the database
       /* myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.e(TAG, "online to read value."+value);




            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/
    }

    //var sessionsRef = firebase.database().ref("sessions");
    private void OnlieCheckDatabase()
    {

        Log.d(TAG, "Value is: " + mSerialno);
        if(mSerialno.isEmpty())
            return;


        DatabaseReference myRef = database.getReference().child("doorAction").child(mSerialno).child("doorOnline").child("device_online");

        if(myRef==null)
            return;


// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.e(TAG, "online to read value."+value);
                if(value==null)
                    return;

                String DeviceStatus = value.toString();

                if(DeviceStatus.equalsIgnoreCase("online"))
                {
                    deviceStatusOnline();
                    isOffline=0;
                }
                else
                {
                    deviceStatusOffline();
                }



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    private void ReadLastOpenDtate()
    {

        Log.d(TAG, "ReadLastOpenDtate is: " + mSerialno);
        if(mSerialno.isEmpty())
            return;




        DatabaseReference doorinfo = database.getReference().child("doorAction").child(mSerialno).child("doorinfo");

        if(doorinfo==null)
            return;


// Read from the database
        doorinfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue(true);
             if(value==null)
                 return;

                Gson gson = new Gson();
                String response = gson.toJson(value);
                JSONObject doorOperation;
                try {
                    String datetime="";
                    String open_user_by="";

                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG, "ReadLastOpenDtate is: " + jsonObject.toString());

                    if(jsonObject.has("datetime"))
                    {
                        datetime= jsonObject.getString("datetime");

                        datetime=Constants.getDate(datetime);
                        door_open_time_value.setText(datetime);
                    }

                    if(jsonObject.has("open_user_by"))
                    {
                         open_user_by= jsonObject.getString("open_user_by");


                        unlock_user_by.setText(open_user_by);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Value is: Parse error" );
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void CheckRealTimeDataBase()
    {

        Log.d(TAG, "CheckRealTimeDataBase is: " + mSerialno);
        if(mSerialno.isEmpty())
            return;




        DatabaseReference myRef = database.getReference().child("doorAction").child(mSerialno).child("doorOperation");

        if(myRef==null)
            return;


// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue(true);
                if(value==null)
                    return;

                Gson gson = new Gson();
                String response = gson.toJson(value);
                JSONObject doorOperation;
                try {
                    String device_online="";
                    String today_time_last_operaion="";
                    String door_lock_status="";
                    String operation = "true";
                     doorOperation = new JSONObject(response);








                    if(doorOperation.has("door_lock_status"))
                        door_lock_status = doorOperation.getString("door_lock_status");

                    String door_window_status="Open";
                    if(doorOperation.has("door_window_status"))
                        door_window_status = doorOperation.getString("door_window_status");

                    if(doorOperation.has("operation"))
                        operation = doorOperation.getString("operation");


                    if(operation.equalsIgnoreCase("false")) {

                        eDeviceUpdate("online");
                        DoorIconChange(door_lock_status);
                        // mdoor_loder.setVisibility(View.INVISIBLE);
                    }


                    door_open_is_value.setText(door_window_status);




                    Log.d(TAG, "CheckRealTimeDataBase is: " +device_online+"=="+doorOperation.toString());

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
    public static String GetUTCdatetimeAsString()
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }



    private void DoorAction(){

        //mdoor_loder.setVisibility(View.VISIBLE);
        mDoor_status.setText("Please wait...");


        database.getReference().child("doorAction").child(mSerialno).child("doorOperation").setValue(new KKModel());




       /* Map<String, String> users = new HashMap<>();

        users.put("door_lock_status",mDoorStatus);
        users.put("device_online","online");
        users.put("operation", "true");

        users.put("door_window_status", mDoorOpenStatus);
        users.put("user_id", "2");
        users.put("username", mUsername);
        users.put("deviceid", "Utpal");
        myRef.setValue(users);


       // myRef.setValue("dooropen");
        Log.e("Error", "-----firbase realtime Result----: "+myRef.toString());
       // myRef.setValue("dooropen");*/






    /*    mdoor_loder.setVisibility(View.VISIBLE);
        StringRequest postRequest = new StringRequest(Request.Method.GET, Constants.DEVICE_DOOR_ACTION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Error", "-----Response Result----: "+response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");
                    String command = jsonObject.getString("command");
                    mdoor_loder.setVisibility(View.INVISIBLE);
                    if(status.equals("200"))
                    {
                        deviceStatusOnline();
                        if(command.equals("high"))
                        {
                            mDoorAction.setImageResource(R.drawable.door_close_action);
                            mDoor_status.setText("Locked");
                        }else
                        {
                            mDoorAction.setImageResource(R.drawable.door_open_action);
                            mDoor_status.setText("Unlock");
                        }

                    }
                    //Toast.makeText(getApplicationContext(), "error"+jsonObject.toString(), Toast.LENGTH_LONG).show();



                }catch (JSONException e){
                    e.printStackTrace();

                    mdoor_loder.setVisibility(View.INVISIBLE);
                    deviceStatusOffline();
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mdoor_loder.setVisibility(View.INVISIBLE);
                deviceStatusOffline();
                Toast.makeText(getApplicationContext(), "Device is offline, Please off Mobile data And connect to wifi", Toast.LENGTH_LONG).show();
                Log.e("Error", "-----VollyError----: "+error.getMessage());

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                //params.put("Token-Access",session_token);
                // params.put("Content-Type",Constraints.CONTENT_TYPE);
                Log.e("Send Data: ","**-Head-*-*--: "+params);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                // params.put("ssid",device_ssid);
                // // params.put("device_no",simNo.getText().toString());
                // params.put("pass",device_password);
                // params.put("email",emailaddress.getText().toString());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(postRequest);*/
    }

    private void DoorIconChange(String command)
    {
Log.e("door","=="+command);
        if(command.equalsIgnoreCase("locked"))
        {
            mDoorAction.setImageResource(R.drawable.door_close_action);
            mDoor_status.setText("Locked");
            mDoorStatus="Unlock";
            mDoorOpenStatus="OPEN";
        }else
        {
            mDoorAction.setImageResource(R.drawable.door_open_action);
            mDoor_status.setText("Unlock");
            mDoorStatus="Locked";
            mDoorOpenStatus="CLOSED";
        }
    }
    public void deviceStatusOnline()
    {
        monline.setImageResource(R.drawable.ic_wifi_connect);
        mdevice_online.setText("Online");
    }

    public void deviceStatusOffline()
    {
        monline.setImageResource(R.drawable.ic_wifi_red);
        mdevice_online.setText("Offline");
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    class KKModel{

        public Object operation_date = ServerValue.TIMESTAMP;
        public String door_lock_status =mDoorStatus;
        public String device_online= "online";
        public String operation="true";

        public String door_window_status= mDoorOpenStatus;
        public String user_id="2";
        public String username=mUsername;
        public String deviceid="Utpal";
        public String operation_date_time;
        KKModel()
        {
            this.operation_date_time =String.valueOf(operation_date);
        }

        public String creationDate() {
            return SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.US).format(creationDate);
        }
    }
}
