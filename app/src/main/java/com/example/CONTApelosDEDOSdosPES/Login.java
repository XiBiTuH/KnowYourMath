package com.example.CONTApelosDEDOSdosPES;

import android.app.Activity;

import android.content.Intent;

import android.database.Cursor;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {


    DatabaseHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mydb  = new DatabaseHelper(this);



        //Variables from the xml file
        final Button submit = (Button) findViewById(R.id.submit);
        final TextView username = (TextView) findViewById(R.id.username);
        final TextView password = (TextView) findViewById(R.id.password);



        // Verifica se está tudo em ordem e se sim então insere na base de dados e vai para a main act
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor searchRes = mydb.getAllDataFromUsers();

                String username_s = username.getText().toString();
                String passoword_s = password.getText().toString();
                boolean found = false;
                //Search for the correct credentials
                while (searchRes.moveToNext()){
                        if(searchRes.getString(1).equals(username_s) && searchRes.getString(2).equals(passoword_s)){
                            Intent myIntent = new Intent(Login.this,MainActivity.class);
                            myIntent.putExtra("ID",searchRes.getString(0));
                            myIntent.putExtra("Username",searchRes.getString(1));
                            myIntent.putExtra("hits",String.valueOf(0));
                            myIntent.putExtra("misses",String.valueOf(0));

                            found = true;
                            Login.this.startActivity(myIntent);
                            Toast.makeText(Login.this,"Welcome back " + searchRes.getString(1) +  " !", Toast.LENGTH_LONG).show();

                        }
                }

                if (!found){
                    Toast.makeText(Login.this,"Wrong credentials !", Toast.LENGTH_LONG).show();
                }
            }
        });



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



    protected  void onStop() {
        super.onStop();
        //Fechar a BD quando sai da act
        mydb.close();
    }
}
