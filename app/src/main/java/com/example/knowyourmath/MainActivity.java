package com.example.knowyourmath;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterViewAnimator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
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
import java.util.Random;

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
    int accumulated_hits;
    int accumulated_misses;


    Map<String, int[]> difs = new HashMap<>();



    // Calculates the product of two integers
    private String CalcTwoValues(TextView x , TextView y){
        int first  = Integer.valueOf(x.getText().toString());
        int second  = Integer.valueOf(y.getText().toString());

        return String.valueOf(first *second);
    }




    // Generate Two randoms numbers
    private int[] GenerateValues(){
        Random rand = new Random();


        int x = rand.nextInt((max - min) + min);
        int y = rand.nextInt((max - min) + min);

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
        m.put("Fácil",new int[]{0,5});
        m.put("Médio",new int[]{1,10});
        m.put("Especialista",new int[]{3,12});
        m.put("Épico",new int[]{3,100});
        m.put("Lendário",new int[]{10,1000});


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

    private String GetStringDiff(Map<String,int []> m , int x,int current_dif) {
        int count = 0;
        for (Map.Entry<String, int[]> entry : m.entrySet()) {
            if (count == current_dif + x) {
                System.out.println(entry.getKey());

                return entry.getKey();
            }
            count++;
        }

        return "Not Found";

    }


    public String ConvertDiffStringToInt(String dif){
        if(dif.equals("Fácil")){
            return "0";
        }
        if(dif.equals("Médio")){
            return "1";
        }
        if(dif.equals("Especialista")){
            return "2";
        }
        if(dif.equals("Épico")){
            return "3";
        }
        if(dif.equals("Lendário")){
            return "4";
        }
        return "0";
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
        final Switch pro_moode = (Switch) findViewById(R.id.pro_mode);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);



        //Load data from intent que vêm do registo e do login
        Intent fromAnotherIntent = getIntent();
        final int current_id = Integer.valueOf(fromAnotherIntent.getStringExtra("ID"));
        String current_name = fromAnotherIntent.getStringExtra("Username"); // TODO-> Use to greet or something



        pro_moode.setOnCheckedChangeListener((new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mydb.maxScore(current_id,Integer.valueOf(currentDificulty)) < points){
                    System.out.println("Points  : " + points);
                    mydb.InserDataIntoScores(current_id,Integer.valueOf(currentDificulty),points);
                    //DEBUG
                    System.out.println("Score : " + String.valueOf(mydb.maxScore(current_id, Integer.valueOf(currentDificulty))));

                    max_score .setText(String.valueOf(mydb.maxScore(current_id, Integer.valueOf(currentDificulty))));
                }
                accumulated_hits = 0;
                accumulated_misses = 0;
                points = 0;
                score.setText(String.valueOf(points));


            }
        }));









        //Set the max score as the score in the database
        try {
            max_score.setText(String.valueOf(mydb.maxScore(current_id, Integer.valueOf(currentDificulty))));
        }
        catch (Exception e){
            max_score.setText(String.valueOf(mydb.maxScore(current_id, 0)));

        }


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

                                //Mete imagens e texto invisível
                                correct_image.setVisibility(View.VISIBLE);
                                correct_text.setVisibility(View.VISIBLE);
                                wrong_image.setVisibility(View.INVISIBLE);
                                wrong_text.setVisibility(View.INVISIBLE);

                                if(pro_moode.isChecked()){

                                    accumulated_hits++;
                                    accumulated_misses = 0;
                                }
                                System.out.println(pro_moode.isChecked());
                                System.out.println(accumulated_hits);
                                if(pro_moode.isChecked() && accumulated_hits== 2){
                                    if(currentDificulty.equals("4")){
                                        Toast.makeText(MainActivity.this,"You are killing it , there are no more levels left. You are the best ! ", Toast.LENGTH_LONG).show();
                                        accumulated_hits = 0;


                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "You've passed to next level", Toast.LENGTH_LONG).show();
                                        ChangeDif(GetStringDiff(difs, 1, Integer.valueOf(ConvertDiffStringToInt(currentDificulty))), difs);
                                        spinner.setSelection(Integer.valueOf(currentDificulty) + 1);
                                        currentDificulty = GetStringDiff(difs, 1, Integer.valueOf(ConvertDiffStringToInt(currentDificulty)));

                                        //Mudar o max score depois de a dificuldade ser mudada
                                        try {
                                            max_score.setText(String.valueOf(mydb.maxScore(current_id, Integer.valueOf(currentDificulty))));
                                        } catch (Exception e) {
                                            max_score.setText(String.valueOf(mydb.maxScore(current_id, 0)));

                                        }
                                        newValues = GenerateValues();
                                        SetNewValues(newValues, firstValue, secondValue, guess);
                                        wrong_image.setVisibility(View.INVISIBLE);
                                        wrong_text.setVisibility(View.INVISIBLE);
                                        correct_image.setVisibility(View.INVISIBLE);
                                        correct_text.setVisibility(View.INVISIBLE);
                                        accumulated_hits = 0;
                                    }
                                }

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
                                if(mydb.maxScore(current_id,Integer.valueOf(currentDificulty)) < points){
                                    System.out.println("Points  : " + points);
                                    mydb.InserDataIntoScores(current_id,Integer.valueOf(currentDificulty),points);
                                    //DEBUG
                                    System.out.println("Score : " + String.valueOf(mydb.maxScore(current_id, Integer.valueOf(currentDificulty))));

                                    max_score .setText(String.valueOf(mydb.maxScore(current_id, Integer.valueOf(currentDificulty))));
                                }

                                if(pro_moode.isChecked()){
                                    accumulated_misses++;
                                    accumulated_hits = 0;

                                }

                                if(pro_moode.isChecked() && accumulated_misses == 2){

                                    if(currentDificulty.equals("0")){
                                        Toast.makeText(MainActivity.this,"You've can't go lower than this :(", Toast.LENGTH_LONG).show();
                                        accumulated_misses = 0;


                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "You've returned to past level", Toast.LENGTH_LONG).show();
                                        ChangeDif(GetStringDiff(difs, -1, Integer.valueOf(ConvertDiffStringToInt(currentDificulty))), difs);
                                        spinner.setSelection(Integer.valueOf(currentDificulty) - 1);

                                        currentDificulty = GetStringDiff(difs, -1, Integer.valueOf(ConvertDiffStringToInt(currentDificulty)));

                                        //Mudar o max score depois de a dificuldade ser mudada
                                        try {
                                            max_score.setText(String.valueOf(mydb.maxScore(current_id, Integer.valueOf(currentDificulty))));
                                        } catch (Exception e) {
                                            max_score.setText(String.valueOf(mydb.maxScore(current_id, 0)));

                                        }
                                        newValues = GenerateValues();
                                        SetNewValues(newValues, firstValue, secondValue, guess);
                                        wrong_image.setVisibility(View.INVISIBLE);
                                        wrong_text.setVisibility(View.INVISIBLE);
                                        correct_image.setVisibility(View.INVISIBLE);
                                        correct_text.setVisibility(View.INVISIBLE);

                                        accumulated_misses = 0;
                                    }
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.dificulty,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    //Reset Points
                    points = 0;
                    score.setText(String.valueOf(points));





                     //Change dif
                    ChangeDif((String) parent.getSelectedItem().toString(),difs);

                    currentDificulty = ConvertDiffStringToInt((String) parent.getSelectedItem().toString());
                    System.out.println(currentDificulty);
                    //Mudar o max score depois de a dificuldade ser mudada
                    System.out.println(parent.getSelectedItem().toString());
                    try {
                        max_score.setText(String.valueOf(mydb.maxScore(current_id, Integer.valueOf(currentDificulty))));
                    }
                    catch (Exception e){
                        max_score.setText(String.valueOf(mydb.maxScore(current_id, 0)));

                    }
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

    public void logOut(View view) {

        Intent out = new Intent(MainActivity.this, Login.class);
        MainActivity.this.startActivity(out);
        
    }
}
