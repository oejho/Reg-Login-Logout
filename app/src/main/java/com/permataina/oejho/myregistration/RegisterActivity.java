package com.permataina.oejho.myregistration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.permataina.oejho.myregistration.Config.TAG_MESSAGE;
import static com.permataina.oejho.myregistration.Config.TAG_RESPONSE;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    //Creating views
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextPhone;
    private EditText editTextConfirmOtp;

    private AppCompatButton buttonRegister;
    private AppCompatButton buttonConfirm;

    //Volley RequestQueue
    private RequestQueue requestQueue;

    //String variables to hold username password and phone
    private String username;
    private String password;
    private String confirmPassword;
    private String phone;
    private int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initializing Views
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfPassword);

        buttonRegister = (AppCompatButton) findViewById(R.id.buttonRegister);

        //Adding a listener to button
        buttonRegister.setOnClickListener(this);

        //Initializing the RequestQueue
        requestQueue = Volley.newRequestQueue(this);


    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //This method would confirm the otp
    private void confirmOtp() throws JSONException {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_confirm, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        buttonConfirm = (AppCompatButton) confirmDialog.findViewById(R.id.buttonConfirm);
        editTextConfirmOtp = (EditText) confirmDialog.findViewById(R.id.editTextOtp);

        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        final AlertDialog alertDialog = alert.create();

        //Displaying the alert dialog
        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hiding the alert dialog
                alertDialog.dismiss();

                //Displaying a progressbar
                final ProgressDialog loading = ProgressDialog.show(RegisterActivity.this, "Authenticating", "Please wait while we check the entered code", false,false);

                //Getting the user entered otp from edittext
                final String otp = editTextConfirmOtp.getText().toString().trim();

                //Creating an string request
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CONFIRM_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //if the server response is success
                                if(response.equalsIgnoreCase("success")){
                                    //dismissing the progressbar
                                    loading.dismiss();

                                    //Starting a new activity
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                }else{
                                    //Displaying a toast if the otp entered is wrong
                                    Toast.makeText(RegisterActivity.this,"Wrong OTP Please Try Again",Toast.LENGTH_LONG).show();
                                    try {
                                        //Asking user to enter otp again
                                        confirmOtp();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                alertDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        //Adding the parameters otp and username
                        params.put(Config.KEY_OTP, otp);
                        params.put(Config.KEY_USERNAME, username);
                        return params;
                    }
                };

                //Adding the request to the queue
                requestQueue.add(stringRequest);
            }
        });
    }


    //this method will register the user
    private void register() {

        //Displaying a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Registering", "Please wait...", false, false);

        //Getting user data
        password = editTextPassword.getText().toString().trim();
        confirmPassword = editTextConfirmPassword.getText().toString().trim();
        phone = editTextPhone.getText().toString().trim();

        //Again creating the string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,


                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        try {
                            //Creating the json object from the response
                            JSONObject jsonResponse = new JSONObject(response);
                            success = jsonResponse.getInt(TAG_RESPONSE);

                            //If it is success
                            //if(jsonResponse.getString(Config.TAG_RESPONSE).equalsIgnoreCase("Success")){
                            if(success == 1){
                                //Asking user to confirm otp
                                confirmOtp();
                                Toast.makeText(RegisterActivity.this,jsonResponse.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                            }else if(success == 0) {
                                //if empty field
                                Toast.makeText(RegisterActivity.this, jsonResponse.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(RegisterActivity.this, error.getMessage() ,Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put(Config.KEY_USERNAME, username);
                params.put(Config.KEY_PASSWORD, password);
                params.put(Config.KEY_CONF_PASSWORD, confirmPassword);
                params.put(Config.KEY_PHONE, phone);
                return params;
            }
        };

        //Adding request the the queue
        requestQueue.add(stringRequest);
    }


    @Override
    public void onClick(View v) {
        //Calling register method on register button click
        username = editTextUsername.getText().toString().trim();
        boolean checkEmail = isEmailValid(username);
        if (checkEmail == true){
            register();
        }else {
            Toast.makeText(RegisterActivity.this,"Wrong email format", Toast.LENGTH_SHORT).show();
        }
    }
}