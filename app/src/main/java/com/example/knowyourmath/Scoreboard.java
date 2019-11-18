package com.example.knowyourmath;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Scoreboard extends Activity {


    DatabaseHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        mydb = new DatabaseHelper(this);

        ListView lv = (ListView) findViewById(R.id.lista);

        // TODO -> Preencher om os dados da base de dados que ainda falta criar
        String[] strings = {};



        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,strings);
        lv.setAdapter(adapter);



    }
}
