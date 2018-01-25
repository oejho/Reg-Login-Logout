package com.permataina.oejho.myregistration;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.permataina.oejho.myregistration.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.permataina.oejho.myregistration.Config.LOGIN_URL;
import static com.permataina.oejho.myregistration.Config.MY_SHARED_PREVERENCES;
import static com.permataina.oejho.myregistration.Config.SESSION_STATUS;
import static com.permataina.oejho.myregistration.Config.TAG_ID;
import static com.permataina.oejho.myregistration.Config.TAG_MESSAGE;
import static com.permataina.oejho.myregistration.Config.TAG_RESPONSE;
import static com.permataina.oejho.myregistration.Config.TAG_USERNAME;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    //Creating views
    private EditText editTextPhone;
    private EditText editTextPassword;
    private TextView textViewRegister;
    private ProgressDialog pDialog;

    private AppCompatButton buttonLogin;

    //Volley RequestQueue
    private RequestQueue requestQueue;

    //String variables to hold username password and phone
    private String email;
    private String password;
    private String id;
    private String username;

    private int success;
    SharedPreferences sharedPreferences;
    Boolean session = false;
    ConnectivityManager conMgr;
    String tag_json_obj = "json_obj_req";
    private static final String TAG = LoginActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            }else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        }

        editTextPhone = (EditText)findViewById(R.id.editTextPhone);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        textViewRegister = (TextView)findViewById(R.id.linkRegister);
        buttonLogin = (AppCompatButton) findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);

        // Cek session login jika TRUE maka langsung buka WorkerActivity
        sharedPreferences = getSharedPreferences(MY_SHARED_PREVERENCES, Context.MODE_PRIVATE);
        session = sharedPreferences.getBoolean(SESSION_STATUS, false);
        id = sharedPreferences.getString(TAG_ID, null);
        username = sharedPreferences.getString(TAG_USERNAME, null);

        if (session) {
            Intent intent = new Intent(LoginActivity.this, WorkerActivity.class);
            intent.putExtra(TAG_ID, id);
            intent.putExtra(TAG_USERNAME, username);
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View view) {

        if(view == buttonLogin){
            String username = editTextPhone.getText().toString();
            String password = editTextPassword.getText().toString();

            // mengecek kolom yang kosong
            if(username.trim().length() > 0 && password.trim().length() > 0){
                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()){
                    checkLogin(username, password);
                }else{
                    Toast.makeText(getApplicationContext() ,"No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(getApplicationContext() ,"Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
            }


        }
        if(view == textViewRegister){
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void checkLogin(final String username, final String password){
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_RESPONSE);

                    // Check for error node in json
                    if (success == 1) {
                        String username = jObj.getString(TAG_USERNAME);
                        String id = jObj.getString(TAG_ID);

                        Log.e("Successfully Login!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        // menyimpan login ke session
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(SESSION_STATUS, true);
                        editor.putString(TAG_ID, id);
                        editor.putString(TAG_USERNAME, username);
                        editor.commit();

                        // Memanggil main activity
                        Intent intent = new Intent(LoginActivity.this, WorkerActivity.class);
                        intent.putExtra(TAG_ID, id);
                        intent.putExtra(TAG_USERNAME, username);
                        finish();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };
        //Adding the request to the queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
