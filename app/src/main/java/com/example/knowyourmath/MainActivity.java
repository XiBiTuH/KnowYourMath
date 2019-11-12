package com.example.knowyourmath;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterViewAnimator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import android.os.Handler;

import org.w3c.dom.Text;

public class MainActivity extends Activity {

    private String currentDificulty = "";

    private int points;

    //Initialize database
    private DatabaseHelper mydb;

    //Global Variables
    Random random = new Random();
    int [] newValues = new int[]{};
    int max = 5;
    int min = 0;


    Map<String, int[]> difs = new HashMap<>();



    // Calculates the product of two integers
    private String CalcTwoValues(TextView x , TextView y){
        int first  = Integer.valueOf(x.getText().toString());
        int second  = Integer.valueOf(y.getText().toString());

        return String.valueOf(first *second);
    }




    // Generate Two randoms numbers
    private int[] GenerateValues(){

        int x = ThreadLocalRandom.current().nextInt(min,max +1 );
        int y = ThreadLocalRandom.current().nextInt(min,max +1 );

        return new int[] {x,y};

    }


    //Names values in interface
    private void SetNewValues(int [] values, TextView firstValue, TextView secondValue,EditText guess){
        firstValue.setText(String.valueOf(values[0]));
        secondValue.setText(String.valueOf(values[1]));
        guess.setText("");
    }


    // Create dificulties dictionary
    private Map<String, int[]> setDictionary(Map<String, int[]> m ){
        m.put("Facil",new int[]{0,5});
        m.put("Medio",new int[]{1,10});
        m.put("Especialista",new int[]{3,12});
        m.put("Epico",new int[]{3,100});
        m.put("Lendario",new int[]{10,1000});


        return m;

    }


    //Change difficulty
    private void ChangeDif(String dif,Map<String, int []> m ){
        for(Map.Entry<String, int[]> entry : m.entrySet()){
            if(entry.getKey().equals(dif)){
                min = entry.getValue()[0];
                max = entry.getValue()[1];
                break;
            }
        }



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DatabaseHelper(this);



        //Reset Points
        points = 0;



        //Variables from the xml file
        final Button answerButton = (Button) findViewById(R.id.answerButton);
        final TextView firstValue = (TextView) findViewById(R.id.firstValue);
        final TextView secondValue = (TextView) findViewById(R.id.secondValue);
        final EditText guess = (EditText) findViewById(R.id.guess);
        final ImageView correct_image = (ImageView) findViewById(R.id.correct_image);
        final ImageView wrong_image = (ImageView) findViewById(R.id.wrong_image);
        final TextView correct_text = (TextView) findViewById(R.id.correct_text);
        final TextView wrong_text = (TextView) findViewById(R.id.wrong_text);
        final TextView score = (TextView) findViewById(R.id.score_value);
        final TextView max_score = (TextView) findViewById(R.id.max_score);



        //Load data from intent que vêm do registo e do login
        Intent fromAnotherIntent = getIntent();
        final int current_id = Integer.valueOf(fromAnotherIntent.getStringExtra("ID"));
        String current_name = fromAnotherIntent.getStringExtra("Username"); // TODO-> Use to greet or something






        //Set the max score as the score in the database

        max_score.setText(String.valueOf(mydb.maxScore(current_id, currentDificulty)));



        //Score starts at 0
        score.setText(String.valueOf(points));
        //Set guess text to ""
        guess.setText("");



        //Put Dictionary keys and values
        difs = setDictionary(difs);


        //Button to submit answer
        answerButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(guess.getText().toString().equals("")){
                            //Hasn't made a choice , doesn't change anything
                            Toast.makeText(MainActivity.this, "You have to at least try !!! \n Don't give up !!! ", Toast.LENGTH_SHORT).show();
                            guess.setText("");

                        }
                        //Entered something on guess input
                        else {

                            guess.setEnabled(false);
                            int guess_value = Integer.valueOf(guess.getText().toString());
                            int x = Integer.valueOf(firstValue.getText().toString());
                            int y = Integer.valueOf(secondValue.getText().toString());
                            //Correct answer
                            if(guess_value == (x * y )){
                                correct_image.setVisibility(View.VISIBLE);
                                correct_text.setVisibility(View.VISIBLE);
                                wrong_image.setVisibility(View.INVISIBLE);
                                wrong_text.setVisibility(View.INVISIBLE);
                                points ++;
                                score.setText(String.valueOf(points));
                            }
                            //Wrong answer
                            else{


                                //Mete as imagens e texto invisiveis
                                wrong_image.setVisibility(View.VISIBLE);
                                wrong_text.setVisibility(View.VISIBLE);
                                correct_image.setVisibility(View.INVISIBLE);
                                correct_text.setVisibility(View.INVISIBLE);


                                //Check if max score was ultrapassado mete o novo score na base de dados
                                if(mydb.maxScore(current_id,currentDificulty) < points){
                                    System.out.println("Points  : " + points);
                                    mydb.InserDataIntoScores(current_id,currentDificulty,points);
                                    //DEBUG
                                    System.out.println("Score : " + String.valueOf(mydb.maxScore(current_id, currentDificulty)));

                                    max_score .setText(String.valueOf(mydb.maxScore(current_id, currentDificulty)));
                                }

                                //Reset nos pontos
                                points = 0;
                                score.setText(String.valueOf(points));




                            }

                            //Delays the result for 1 second
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    //Poe os valores a zeros e imagens e texto invisiveis
                                    newValues = GenerateValues();
                                    SetNewValues(newValues,firstValue,secondValue,guess);
                                    wrong_image.setVisibility(View.INVISIBLE);
                                    wrong_text.setVisibility(View.INVISIBLE);
                                    correct_image.setVisibility(View.INVISIBLE);
                                    correct_text.setVisibility(View.INVISIBLE);
                                    guess.setEnabled(true);
                                }
                            },1000);

                        }


                    }
                }
        );

        //Different dificulties
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.dificulty,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    //Reset Points
                    points = 0;
                    score.setText(String.valueOf(points));


                //Chage dif
                    ChangeDif((String) parent.getSelectedItem().toString(),difs);
                    currentDificulty = (String) parent.getSelectedItem().toString();
                    newValues = GenerateValues();
                    SetNewValues(newValues,firstValue,secondValue,guess);
                    wrong_image.setVisibility(View.INVISIBLE);
                    wrong_text.setVisibility(View.INVISIBLE);
                    correct_image.setVisibility(View.INVISIBLE);
                    correct_text.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }
}
