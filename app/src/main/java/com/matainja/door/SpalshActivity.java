package com.matainja.door;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.matainja.door.Database.DatabaseAdapter;

import java.util.HashMap;

public class SpalshActivity extends AppCompatActivity {

    DatabaseAdapter dbHelper;
    Context mContext;
   final long _splashTime=2000;
    Intent mainActivity;
    SessionManager sessionManager;
    String mUsername="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        mContext=this;
        // database Connection Open
        dbHelper = new DatabaseAdapter(mContext);
        dbHelper.open();

        HashMap<String, String> user = sessionManager.getUser();
        mUsername = user.get(SessionManager.KEY_USER_NAME);



        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {

                boolean isdeviceExist =dbHelper.getDeviceExist();
                HashMap<String, String> user = sessionManager.getUser();

                    mUsername = user.get(SessionManager.KEY_USER_NAME);

                if(isdeviceExist)
                {

                    mainActivity = new Intent(SpalshActivity.this, InternetDashBoard.class);

                    if(mUsername.isEmpty())
                    {
                        mainActivity = new Intent(SpalshActivity.this, RegisterActivity.class);
                    }

                }

               else
                   if(mUsername==null)
                    mainActivity = new Intent(SpalshActivity.this, RegisterActivity.class);
                   else
                       mainActivity = new Intent(SpalshActivity.this, initActivity.class);

                startActivity(mainActivity);
                finish();
                /*}else {
                    Intent i3 = new Intent(landing.this, WelcomeActivity.class);
                    i3.putExtra("callfrom", "other");
                    startActivity(i3);
                    finish();
                }*/
                /*landing.this.overridePendingTransition(R.anim.lefttoright,
                        R.anim.righttoleft);*/
            }
        }, _splashTime);


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


}
