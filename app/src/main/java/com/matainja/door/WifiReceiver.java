package com.matainja.door;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.ListView;

import com.matainja.door.Adapter.ListAdapter;
import com.matainja.door.model.WifiModel;

import java.util.ArrayList;
import java.util.List;

class WifiReceiver extends BroadcastReceiver {
    WifiManager wifiManager;
    StringBuilder sb;
    ListView wifiDeviceList;
    public WifiReceiver(WifiManager wifiManager, ListView wifiDeviceList) {
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
    }
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            sb = new StringBuilder();
            List<ScanResult> wifiList = wifiManager.getScanResults();
            ArrayList<WifiModel> deviceList = new ArrayList<WifiModel>();
            for (ScanResult scanResult : wifiList) {
                String name = scanResult.SSID;
                String rss =scanResult.BSSID;

                Log.e("wifi","wifiname="+name);
                if( !name.isEmpty()) {

                    if(!name.equals(Constants.AP_MODE_NAME))
                    {
                        WifiModel newWifi = new WifiModel(name, rss);
                        deviceList.add(newWifi);
                    }



                }
            }
// get data from the table by the ListAdapter
            ListAdapter customAdapter = new ListAdapter(context, R.layout.wifilistrow,deviceList);
           // ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, deviceList.toArray());
            wifiDeviceList.setAdapter(customAdapter);

            Intent i = new Intent("broadcastReceiver");
            // Data you need to pass to activity
            i.putExtra("message", "utpallll");

            context.sendBroadcast(i);
        }
    }
}