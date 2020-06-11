package com.matainja.door;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.matainja.door.Database.DatabaseAdapter;
import com.matainja.door.Database.FireStoreHelper;
import com.matainja.door.model.Devices;

public class initActivity extends AppCompatActivity  implements View.OnClickListener {
    private ListView wifiList;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    WifiReceiver receiverWifi;
    private Button mButton;
    Context mContext;
LinearLayout mWifiRouter;
    LinearLayout mwificonfiguredlayout;
    Dialog DeviceNamedialog ;
    EditText mDeviceEditName,mdevice_sl_no;
    DatabaseAdapter dbHelper;
    FirebaseFirestore db;
    FireStoreHelper mFireStoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_init);
        DeviceNamedialog = new Dialog(initActivity.this);

        DeviceNamedialog.setContentView(R.layout.layout_dialog_device_name);

        // database Connection Open
        dbHelper = new DatabaseAdapter(this);
        dbHelper.open();

        mFireStoreHelper =new FireStoreHelper();

        mWifiRouter = (LinearLayout)findViewById(R.id.wifiSettingbtnlayout);
        mwificonfiguredlayout =(LinearLayout)findViewById(R.id.wificonfiguredlayout);

        mButton = DeviceNamedialog.findViewById(R.id.savebttn);

        mDeviceEditName =DeviceNamedialog.findViewById(R.id.device_name);
        mdevice_sl_no=DeviceNamedialog.findViewById(R.id.device_sl_no);
        mWifiRouter.setOnClickListener(this);
        mwificonfiguredlayout.setOnClickListener(this);
        mButton.setOnClickListener(this);
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
    protected void onPostResume() {
        super.onPostResume();

    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    private  String getSSid(Context context)
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid;


       // wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = wifiInfo.getSSID();
            ssid= ssid.replace("\"", "");
            //ssid= " Conected";
        }
        else
        {
            ssid= "not Conected";
        }


        return ssid;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.wifiSettingbtnlayout:
                // Do something
                Intent mainActivitysetting = new Intent(initActivity.this, initSettingActivity.class);

                startActivity(mainActivitysetting);
                break;

            case R.id.wificonfiguredlayout:
                // Do something
                DeviceNamedialog.show();
                break;
            case R.id.savebttn:
                // Do something
                boolean save_flag=false;
                String device_name =mDeviceEditName.getText().toString().trim();
                String sl_no =mdevice_sl_no.getText().toString().trim();
                if(device_name.isEmpty())
                {
                    mDeviceEditName.setHintTextColor(Color.RED);
                    save_flag=true;
                }
                if(sl_no.isEmpty())
                {
                    mdevice_sl_no.setHintTextColor(Color.RED);
                    save_flag=true;
                }

                if(!save_flag)
                {
                    DeviceNamedialog.cancel();
                    Devices device =new Devices();
                    device.setName(device_name);
                    device.setDevice_serial(sl_no);
                    device.setDevice_mac_address("YB:YU:IH:LS");
                    device.setDevice_wifi_name("");
                    device.setDevice_wifi_password("");
                    device.setOnline("online");
                    device.setJoin_date("14-04-2020");
                    dbHelper.DeviceRegister(device);

                   // mFireStoreHelper.SaveDevice(device,this);
                    mFireStoreHelper.getDevice(sl_no,device);

                    Intent mainActivity = new Intent(initActivity.this, InternetDashBoard.class);

                    startActivity(mainActivity);
                    finish();
                    Log.e("Save","jjj");
                }

                break;


        }
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(initActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }




}
