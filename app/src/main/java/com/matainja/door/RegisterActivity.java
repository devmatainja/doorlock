package com.matainja.door;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.matainja.door.Database.DatabaseAdapter;
import com.matainja.door.Database.FireStoreHelper;

public class RegisterActivity extends AppCompatActivity {
    private ListView wifiList;
    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    WifiReceiver receiverWifi;
    Dialog dialog ;
    Dialog dialogpopup ;

    Dialog dialogMessage ;

    Button mContinuebbtn;
    EditText mUser_name;
    EditText mEmail;
    String user_name,mSEmail;

    Context mcontext;
    TextView mErrosMsg;

    DatabaseAdapter dbHelper;
    FireStoreHelper mFireStoreHelper;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        sessionManager = new SessionManager(this);




        mcontext = RegisterActivity.this;












        mContinuebbtn = findViewById(R.id.continuebbtn);

        mUser_name =findViewById(R.id.user_name);


        mEmail =findViewById(R.id.email);
        mErrosMsg =findViewById(R.id.error_save);




        // database Connection Open
        dbHelper = new DatabaseAdapter(mcontext);
        dbHelper.open();











        mContinuebbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean save_flag=false;
                 user_name =mUser_name.getText().toString().trim();
                 mSEmail =mEmail.getText().toString().trim();



                if(user_name.isEmpty())
                {
                    mUser_name.setHintTextColor(Color.RED);
                    save_flag=true;
                }
                if(mSEmail.isEmpty())
                {
                    mEmail.setHintTextColor(Color.RED);
                    save_flag=true;
                }
                if(!mSEmail.isEmpty() && !mSEmail.matches(emailPattern))
                {
                    mErrosMsg.setText("Please enter email ");
                    save_flag=true;
                }




                if(!save_flag)
                {
                    sessionManager.StoreUser(user_name,mSEmail);

                    boolean isdeviceExist =dbHelper.getDeviceExist();

                    Intent mainActivity;
                    if(isdeviceExist)
                        mainActivity = new Intent(RegisterActivity.this, InternetDashBoard.class);
                    else
                        mainActivity = new Intent(RegisterActivity.this, initActivity.class);

                    startActivity(mainActivity);
                    finish();

                    Log.e("Save","jjj");
                }


            }
        });



    }

    private void openDialog() {

        dialog.setTitle("Custom Dialog");



        dialog.show();



    }









    @Override
    protected void onPause() {
        super.onPause();

    }













    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}
