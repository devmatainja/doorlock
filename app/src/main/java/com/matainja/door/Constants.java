package com.matainja.door;

public class Constants {

      public static String AP_MODE_URL = "http://192.168.4.1";
      public static String STA_MODE_URL="http://192.168.1.10";
  //
   // public static String BASE_URL = "http://dev.hdsapp.com/api/";

/*
dev.hdsapp.com
demo.hdsapp.com
http://43.247.37.202:3000/
 */
    public static String AP_MODE_NAME = "MatainjaDoorLock";
    public static String AP_MODE_ONLINE = AP_MODE_URL+"/";

    public static String AP_MODE_SETTING_SAVE = AP_MODE_URL+"/setting?";

    public static String DEVICE_LOGOUT = STA_MODE_URL+"/logout?";

    public static String DEVICE_DOOR_ACTION = STA_MODE_URL+"/door?";
    public static String DEVICE_DOOR_STATUS = STA_MODE_URL+"/?";



}
