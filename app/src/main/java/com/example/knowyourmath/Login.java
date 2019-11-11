package com.example.knowyourmath;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }



    //Go to the main page logged in
    public void goToMainPage(View view) {
        Intent myIntent = new Intent(Login.this,MainActivity.class);
        Login.this.startActivity(myIntent);
    }


    //  Go to the register page to register
    public void goToRegister(View view) {
        Intent myIntent = new Intent(Login.this,Registar.class);
        Login.this.startActivity(myIntent);
    }
}
