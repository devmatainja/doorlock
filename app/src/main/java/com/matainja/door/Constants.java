package com.matainja.door;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    public static String getDate(String ourDate)
    {
        String StringDate;
        try
        {
            long timestamp = 0;
           // Log.d("ourDates", ourDate);
            timestamp = Long.parseLong(ourDate);
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(timestamp * 1000L);

            Date d = cal.getTime();
            SimpleDateFormat formatterString = new SimpleDateFormat("EEE, d MMM yy h:mm a");
            formatterString.setTimeZone(TimeZone.getDefault());
            StringDate = formatterString.format(d);
            //Log.d("ourDate", StringDate);


            //Log.d("ourDate", ourDate);
        }
        catch (Exception e)
        {
            StringDate = "00-00-0000 00:00";
        }
        return StringDate;
    }

}
