package com.example.knowyourmath;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Registar extends Activity {

    DatabaseHelper mydb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);

        mydb = new DatabaseHelper(this);


        //Variables from the xml file

        final Button submit = (Button) findViewById(R.id.registar);
        final TextView username = (TextView) findViewById(R.id.username);
        final TextView password = (TextView) findViewById(R.id.password);



        //Add the user to the database
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int id = 0 ;
                String usern;

                //Search for repeated usernames
                Cursor searchRes = mydb.getAllDataFromUsers();
                boolean control = true;
                while(searchRes.moveToNext()){
                    if(searchRes.getString(1).equals(username.getText().toString())){
                        control = false;
                        Toast.makeText(Registar.this,"Username already used ! Try another one.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    id = Integer.valueOf(searchRes.getString(0));


                }

                //If everything is in control , then proceeds to try to enter the data in the database
                if(control) {
                    boolean result = mydb.insertDataIntoUsers(username.getText().toString(), password.getText().toString());
                    //If everything goes right , then add to database and switch to main activity
                    if (result) {
                        Toast.makeText(Registar.this, "User added successfully !", Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(Registar.this, MainActivity.class);
                        myIntent.putExtra("ID",String.valueOf( id + 1 ));
                        myIntent.putExtra("Username",username.getText().toString());
                        searchRes.close();
                        Registar.this.startActivity(myIntent);
                    }
                    //Error
                    else {
                        Toast.makeText(Registar.this, "User could not be added !", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });

    }

    public void gotoLogin(View view) {
        Intent login = new Intent(Registar.this,Login.class);
        this.startActivity(login);
    }
}
