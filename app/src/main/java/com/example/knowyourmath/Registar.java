package com.example.knowyourmath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Registar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);
    }

    public void goToMainPage(View view) {
        Intent myIntent = new Intent(Registar.this,MainActivity.class);
        Registar.this.startActivity(myIntent);
    }
}
