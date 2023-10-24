package com.example.stylistiq.Session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.stylistiq.DashBoard.DashBoard;
import com.example.stylistiq.Login.Login;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    public static  final String SESSION_USERSESSION = "userLoginSession";
    public static  final String SESSION_REMEMBERME = "rememberMe";

    public static  final String SESSION_ALL_DETAILS = "allUserDetails";

    //USER SESSION VARIABLES
    public static final String IS_LOGIN="IsLoggedIn";
    public static final String KEY_FULLNAME = "fullname";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONENUMBER = "phoneNumber";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_PASSWORD = "password";

    //REMEMBER ME VARIABLES
    public static final String IS_REMEMBERME = "IsRememberMe";
    public static final String KEY_SESSION_PHONENUMBER = "phoneNumber";
    public static final String KEY_SESSION_PASSWORD = "password";


    public SessionManager(Context _context, String sessionName){
        context = _context;
        usersSession = context.getSharedPreferences(sessionName,Context.MODE_PRIVATE);
        editor = usersSession.edit();
    }

    public void createLoginSession(String fullName, String email, String phoneNumber, String gender, String password){

        editor.putBoolean(IS_LOGIN,true);

        editor.putString(KEY_FULLNAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONENUMBER, phoneNumber);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    public HashMap<String, String> getUserDetailsFromSession(){
        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(IS_LOGIN, String.valueOf(usersSession.getBoolean(IS_LOGIN, false)));
        userData.put(KEY_FULLNAME, usersSession.getString(KEY_FULLNAME, null));

        userData.put(KEY_EMAIL, usersSession.getString(KEY_EMAIL, null));
        userData.put(KEY_PHONENUMBER, usersSession.getString(KEY_PHONENUMBER, null));
        userData.put(KEY_GENDER, usersSession.getString(KEY_GENDER, null));
        userData.put(KEY_PASSWORD, usersSession.getString(KEY_PASSWORD, null));

        return userData;
    }

    public boolean checkLogin(){
        if (usersSession.getBoolean(IS_LOGIN,false)){
            return true;
        }
        else{
            return false;
        }
    }

    public void logoutUserFromSession(){
        editor.clear();
        editor.commit();
    }

    //    Remember Me Session Functions
    public void createRememberMeSession(String phoneNumber, String password){

        editor.putBoolean(IS_REMEMBERME,true);
        editor.putString(KEY_SESSION_PHONENUMBER, phoneNumber);
        editor.putString(KEY_SESSION_PASSWORD, password);
        editor.commit();
    }

    public HashMap<String, String> getRememberMeDetailsFromSession(){
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_SESSION_PHONENUMBER, usersSession.getString(KEY_SESSION_PHONENUMBER, null));
        userData.put(KEY_SESSION_PASSWORD, usersSession.getString(KEY_SESSION_PASSWORD, null));

        return userData;
    }

//    public HashMap<String, String> getUsersAllDetails(){
//        HashMap<String, String> allUserData = new HashMap<String, String>();
//
//        allUserData.put(KEY_FULLNAME, usersSession.getString(KEY_FULLNAME, null));
//        allUserData.put(KEY_EMAIL, usersSession.getString(KEY_EMAIL, null));
//        allUserData.put(KEY_PHONENUMBER, usersSession.getString(KEY_PHONENUMBER, null));
//        allUserData.put(KEY_PASSWORD, usersSession.getString(KEY_PASSWORD, null));
//
//        return allUserData;
//    }

    public boolean checkRememberMe(){
        if (usersSession.getBoolean(IS_REMEMBERME,false)){
            return true;
        }
        else{
            return false;
        }
    }
}
