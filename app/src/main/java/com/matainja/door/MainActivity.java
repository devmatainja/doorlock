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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.SpinKitView;
import com.matainja.door.Database.DatabaseAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView mlogout;
    Dialog dialogLoader ;
    ImageView mDoorAction;
SpinKitView mdoor_loder;
    TextView mDoor_status;
    ImageView monline;
    TextView mdevice_online;
    TextView mdevice_name;
    DatabaseAdapter dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        dialogLoader = new Dialog(this);
        dialogLoader.setContentView(R.layout.layout_dialog_popup_loader);
        mDoorAction = findViewById(R.id.door_action);
        mdoor_loder = findViewById(R.id.door_loder);
        mDoor_status = findViewById(R.id.door_status);
        monline = findViewById(R.id.online);
        mlogout =findViewById(R.id.logout);
        mdevice_online =findViewById(R.id.device_online);
        mdevice_name = findViewById(R.id.device_name);
        mlogout.setOnClickListener(this);
        mDoorAction.setOnClickListener(this);

        // database Connection Open
        dbHelper = new DatabaseAdapter(this);
        dbHelper.open();

        Cursor device = dbHelper.getDevice();

       if(device.getCount() > 0)
       {
           device.moveToFirst();
           String mName = device.getString(device.getColumnIndex("name"));

           Log.e("Device","=="+mName);

           if(!mName.isEmpty())
           {
               mdevice_name.setText(mName);
           }

       }
        //check device offline or not
        CheckOnline();
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
               Intent initIntent = new Intent(MainActivity.this, initActivity.class);

                startActivity(initIntent);
                finish();
                //LogOutDevice();
              break;
            case R.id.door_action:
                DoorAction();
                break;



        }

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
                    mdoor_loder.setVisibility(View.INVISIBLE);
                    if(status.equals("200"))
                    {
                        if(online)
                        {
                            deviceStatusOnline();
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

    private void DoorAction(){


        mdoor_loder.setVisibility(View.VISIBLE);
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
        requestQueue.add(postRequest);
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
}
