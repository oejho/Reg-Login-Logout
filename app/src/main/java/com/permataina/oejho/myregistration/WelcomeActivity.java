package com.permataina.oejho.myregistration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageViewWorker;
    private ImageView imageViewCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        imageViewWorker = (ImageView)findViewById(R.id.image_worker);
        imageViewCompany = (ImageView)findViewById(R.id.image_company);
        imageViewWorker.setOnClickListener(this);
        imageViewCompany.setOnClickListener(this);

        Glide.with(this).load(R.drawable.group).into(imageViewWorker);
        Glide.with(this).load(R.drawable.cityscape).into(imageViewCompany);


    }

    @Override
    public void onClick(View view) {
        if (view == imageViewWorker){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
