package com.permataina.oejho.myregistration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WorkerActivity extends AppCompatActivity implements View.OnClickListener{
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        buttonLogout = (Button)findViewById(R.id.button_logout);
        buttonLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogout){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
