package com.example.krich.unifinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    protected void loginMethod(View v) {
        Intent loginPage = new Intent(this ,LoginActivity.class);
        this.startActivity(loginPage);
    }

    protected void regMethod(View v) {
        Intent regPage = new Intent(this , RegisterActivity.class);
        this.startActivity(regPage);
    }
}
