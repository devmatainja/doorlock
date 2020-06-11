package com.matainja.door;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

public class SessionManager {

    private static SharedPreferences pref;
    private SharedPreferences.Editor editor,tempEditor;
    private Context _context;
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Loca360";      //fileName
    private static final String Is_LogIn ="ISLoggedIn";

    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_NAME = "username";
    public static final String KEY_USER_email = "email";
    public SessionManager(Context context){
        this._context = context;
        pref=_context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
        editor.commit();

        tempEditor = pref.edit();
        tempEditor.apply();
        tempEditor.commit();
    }

    /************************************************************************************************* */

    public void StoreUser(String name, String email){

        tempEditor.putString(KEY_USER_NAME,name);
        tempEditor.putString(KEY_USER_email,email);

        tempEditor.commit();
    }

    /**
     * for LogOut User
     * **/
    public void logOutUser(){
        Log.e("Session Manager ","--***-- "+editor);
        editor.clear();
        editor.commit();
        Log.e("Session Manager ","--***-- "+editor);
    }











    public HashMap<String,String> getUser(){

        HashMap<String,String> User = new HashMap<String, String>();

        User.put(KEY_USER_NAME,pref.getString(KEY_USER_NAME,null));
        User.put(KEY_USER_email,pref.getString(KEY_USER_email,null));

        return User;
    }









}
