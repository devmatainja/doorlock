package com.matainja.door;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.matainja.door.Database.DatabaseAdapter;
import com.matainja.door.model.Devices;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {
    private ListView wifiList;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    WifiReceiver receiverWifi;
    Dialog dialog ;
    Dialog dialogpopup ;

    Dialog dialogMessage ;
    EditText mDeviceEditText;
    Button mContinuebbtn;
    EditText mDeviceEditName;
    EditText mDeviceEditspassword;
    String device_name;
    String device_ssid;
    String device_password;
    Context mcontext;
    TextView mErrosMsg;
    Button mOk;
    String msg = "Please check Wifi Connected with Device & disbale Mobile Internet ";
    DatabaseAdapter dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        dialog = new Dialog(SettingActivity.this);
        dialogpopup= new Dialog(SettingActivity.this);
        dialogMessage = new Dialog(SettingActivity.this);



        dialogpopup.setCancelable(false);
        dialogMessage.setCancelable(false);

        mcontext =SettingActivity.this;
        // Include dialog.xml file
        dialog.setContentView(R.layout.layout_dialog_wifi);


        dialogpopup.setContentView(R.layout.layout_dialog_popup_loader);
        dialogMessage.setContentView(R.layout.layout_dialog_popup_message);


         wifiList =dialog.findViewById(R.id.mylist);
        mOk = dialogMessage.findViewById(R.id.okbttn);


        mDeviceEditText =findViewById(R.id.device_ssid);
        mContinuebbtn = findViewById(R.id.continuebbtn);
        mDeviceEditName =findViewById(R.id.device_name);
        mDeviceEditspassword =findViewById(R.id.device_password);
        mErrosMsg =findViewById(R.id.error_save);


        toggleMobileDataConnection(false);

        // database Connection Open
        dbHelper = new DatabaseAdapter(mcontext);
        dbHelper.open();

        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {


                TextView tv = view.findViewById(R.id.wifi_name);
                String wifiName =tv.getText().toString();
                mDeviceEditText.setText(wifiName);
                dialog.cancel();

            }
        });



        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingActivity.this,MainActivity.class);

                startActivity(i);
                finish();
            }
        });



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiverWifi = new WifiReceiver(wifiManager, wifiList);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(receiverWifi, intentFilter);
                getWifi();
                openDialog();
            }
        });

        mContinuebbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean save_flag=false;
                 device_name =mDeviceEditName.getText().toString().trim();
                 device_ssid =mDeviceEditText.getText().toString().trim();
                 device_password =mDeviceEditspassword.getText().toString();


                if(device_name.isEmpty())
                {
                    mDeviceEditName.setHintTextColor(Color.RED);
                    save_flag=true;
                }

                if(device_ssid.isEmpty()) {

                    mDeviceEditText.setHintTextColor(Color.RED);
                    save_flag=true;
                }
                if(device_password.isEmpty()) {
                    mDeviceEditspassword.setHintTextColor(Color.RED);
                    save_flag=true;
                }

                if(!save_flag)
                {

                    SaveWifiConfig();
                    Log.e("Save","jjj");
                }


            }
        });

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();

        }else
        {
            checkPermission();
        }

        GetOnlineDevice();

        openDialog();

    }

    private void openDialog() {

        dialog.setTitle("Custom Dialog");



        dialog.show();



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

    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(this, "version> = marshmallow", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "location turned off", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                Toast.makeText(this, "location turned on", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            }
        } else {
            Toast.makeText(this, "scanning", Toast.LENGTH_SHORT).show();
            wifiManager.startScan();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        receiverWifi = new WifiReceiver(wifiManager, wifiList);
        IntentFilter intentFilter = new IntentFilter("broadcastReceiver");
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, intentFilter);
        getWifi();
    }

    BroadcastReceiver broadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle b = intent.getExtras();

            String message = b.getString("message");

            Log.e("newmesage", "" + message);
        }
    };

    public  void checkPermission()
    {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
    } else {
        wifiManager.startScan();
    }

}
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            } else {
                Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }
            break;
        }
    }

    private void GetOnlineDevice(){



        StringRequest postRequest = new StringRequest(Request.Method.GET, Constants.AP_MODE_ONLINE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Error", "-----Response Result----: "+response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Log.e("Error", "-----VollyError----: "+response);
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.e("Error", "-----responseerror----: ");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "-----VollyError----: "+error.getMessage());

                Toast.makeText(getApplicationContext(), "Sorry ! Could not connect to server ", Toast.LENGTH_SHORT).show();
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
                // params.put("device_type",deviceType);
                // // params.put("device_no",simNo.getText().toString());
                // params.put("device_name",name.getText().toString());
                // params.put("email",emailaddress.getText().toString());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(postRequest);
    }

    private void SaveWifiConfig(){

String QueryS = "ssid="+device_ssid+"&pass="+device_password;
String urlget = Constants.AP_MODE_SETTING_SAVE+QueryS;
        dialogpopup.show();
        mErrosMsg.setText("");

        StringRequest postRequest = new StringRequest(Request.Method.GET, urlget, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Error", "-----Response Result----: "+response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String status = jsonObject.getString("status");

                    Log.e("Setting","==="+status);
                    dialogpopup.cancel();
                    if(status.equals("200"))
                    {

                        Devices device =new Devices();
                        device.setName(device_name);
                        device.setDevice_serial("mata12345");
                        device.setDevice_mac_address("YB:YU:IH:LS");
                        device.setDevice_wifi_name(device_ssid);
                        device.setDevice_wifi_password(device_password);
                        device.setOnline("online");
                        device.setJoin_date("14-04-2020");
                        dbHelper.DeviceRegister(device);
                        dialogMessage.show();
                    }


                }catch (JSONException e){
                    e.printStackTrace();

                    dialogpopup.cancel();
                    mErrosMsg.setText(msg);
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialogpopup.cancel();
                mErrosMsg.setText(msg);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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
                 params.put("ssid",device_ssid);
                // // params.put("device_no",simNo.getText().toString());
                 params.put("pass",device_password);
                // params.put("email",emailaddress.getText().toString());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(postRequest);
    }




    /**
     * An AsyncTask is needed to execute HTTP requests in the background so that they do not
     * block the user interface.
     */
   /* private class HttpRequestAsyncTask extends AsyncTask<Void, Void, Void> {

        // declare variables needed
        private String requestReply,ipAddress, portNumber;
        private Context context;

        private String parameter;
        private String parameterValue;

        *//**
         * Description: The asyncTask class constructor. Assigns the values used in its other methods.
         * @param context the application context, needed to create the dialog
         * @param parameterValue the pin number to toggle
         * @param ipAddress the ip address to send the request to
         * @param portNumber the port number of the ip address
         *//*
        public HttpRequestAsyncTask(Context context, String parameterValue, String ipAddress, String portNumber, String parameter)
        {
            this.context = context;



            this.ipAddress = ipAddress;
            this.parameterValue = parameterValue;
            this.portNumber = portNumber;
            this.parameter = parameter;
        }

        *//**
         * Name: doInBackground
         * Description: Sends the request to the ip address
         * @param voids
         * @return
         *//*
        @Override
        protected Void doInBackground(Void... voids) {

            requestReply = sendRequest(parameterValue,ipAddress,portNumber, parameter);
            return null;
        }

        *//**
         * Name: onPostExecute
         * Description: This function is executed after the HTTP request returns from the ip address.
         * The function sets the dialog's message with the reply text from the server and display the dialog
         * if it's not displayed already (in case it was closed by accident);
         * @param aVoid void parameter
         *//*
        @Override
        protected void onPostExecute(Void aVoid) {

        }

        *//**
         * Name: onPreExecute
         * Description: This function is executed before the HTTP request is sent to ip address.
         * The function will set the dialog's message and display the dialog.
         *//*
        @Override
        protected void onPreExecute() {

        }

    }*/


        public boolean toggleMobileDataConnection(boolean ON) {
        try {
            // create instance of connectivity manager and get system service

            final ConnectivityManager conman = (ConnectivityManager) this
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            // define instance of class and get name of connectivity manager
            // system service class
            final Class conmanClass = Class
                    .forName(conman.getClass().getName());
            // create instance of field and get mService Declared field
            final Field iConnectivityManagerField = conmanClass
                    .getDeclaredField("mService");
            // Attempt to set the value of the accessible flag to true
            iConnectivityManagerField.setAccessible(true);
            // create instance of object and get the value of field conman
            final Object iConnectivityManager = iConnectivityManagerField
                    .get(conman);
            // create instance of class and get the name of iConnectivityManager
            // field
            final Class iConnectivityManagerClass = Class
                    .forName(iConnectivityManager.getClass().getName());
            // create instance of method and get declared method and type
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            // Attempt to set the value of the accessible flag to true
            setMobileDataEnabledMethod.setAccessible(true);
            // dynamically invoke the iConnectivityManager object according to
            // your need (true/false)
            setMobileDataEnabledMethod.invoke(iConnectivityManager, ON);
        } catch (Exception e) {
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
