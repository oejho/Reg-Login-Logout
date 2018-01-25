package com.permataina.oejho.myregistration;

/**
 * Created by Raisa on 18/01/2018.
 */

public class Config {
    //URLs to register.php and confirm.php file
    public static final String REGISTER_URL = "http://ronny.permataindonesia.com/registerOtp.php";
    public static final String CONFIRM_URL = "http://ronny.permataindonesia.com/confirm.php";
    public static final String LOGIN_URL = "http://ronny.permataindonesia.com/login.php";


    //Keys to send username, password, phone and otp
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CONF_PASSWORD = "confirm_password";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_OTP = "otp";

    //JSON Tag from response from server
    public static final String TAG_RESPONSE = "success";
    public static final String TAG_MESSAGE= "message";
    public static final String MY_SHARED_PREVERENCES = "my_shared_preferences";
    public static final String SESSION_STATUS = "session_status";
    public final static String TAG_ID = "id";
    public final static String TAG_USERNAME = "username";
}
