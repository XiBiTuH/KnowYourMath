package com.example.CONTApelosDEDOSdosPES;


import android.app.Activity;

import android.content.Intent;

import android.database.Cursor;

import android.os.Bundle;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Scoreboard extends Activity {


    DatabaseHelper mydb;
    private String name;
    private String id;
    private String hits;
    private String misses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        mydb = new DatabaseHelper(this);
        final ListView lv = (ListView) findViewById(R.id.lista);
        final Button share = (Button) findViewById(R.id.share);
        final TextView share_id = (TextView) findViewById(R.id.share_id);
        final Button back = (Button) findViewById(R.id.back);


        //Recebe os valores da main activity
        Intent fromAnotherIntent = getIntent();
        name = fromAnotherIntent.getStringExtra("Username");
        id = fromAnotherIntent.getStringExtra("ID");
        misses = fromAnotherIntent.getStringExtra("hits");
        hits = fromAnotherIntent.getStringExtra("misses");


        //Mete todos os valores daquele utilizador numal ista para depois ir para a list view
        final Cursor data = mydb.getAllSessionsUser(name);
        List<String> lista = new ArrayList<>();
        String renew;
        while(data.moveToNext()){
             renew = "ID : " +  data.getString(0) + "        Name : " + data.getString(1) + "       Hits : " + data.getString(2) + "        Misses : " + data.getString(3);
            lista.add(renew);
        }

        System.out.println(lista.size());



        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,lista);
        lv.setAdapter(adapter);


        //Partilhar um score
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to_share;

                final Cursor data = mydb.getAllSessionsUser(name);
                boolean found = false;
                //Procura o id que o ut escolheu
                 while(data.moveToNext()){
                     if(data.getString(0).equals(share_id.getText().toString())) {
                         to_share = "I have answered correctly " + data.getString(2) + " times and I have missed " + data.getString(3) + " times";
                         Intent share_intent = new Intent(Intent.ACTION_SEND);
                         share_intent.setType("text/plain");
                         String subject = "Session score !!!";
                         share_intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                         share_intent.putExtra(Intent.EXTRA_TEXT, to_share);
                         Scoreboard.this.startActivity(Intent.createChooser(share_intent, "Share using"));
                         found = true;
                     }
                    }
                 //Caso não encontre
                 if (!found){

                         Toast.makeText(Scoreboard.this,"Invalid ID , please choose an ID from the table above",Toast.LENGTH_SHORT).show();

                 }

                }

        });


        //Voltar para a atividade main
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_intent  = new Intent(Scoreboard.this,MainActivity.class);
                back_intent.putExtra("Username", name);
                back_intent.putExtra("ID", id);
                //Trata do caso em que o utilizador vai á activity dos scores e volta para trás , assim não se perde a contagem dos misses e hits por sessão
                back_intent.putExtra("hits",hits);
                back_intent.putExtra("misses",misses);


                startActivity(back_intent);

            }
        });

    }



    protected  void onStop() {
        super.onStop();
        //Fechar a BD quando sai da act
        mydb.close();
    }

}
