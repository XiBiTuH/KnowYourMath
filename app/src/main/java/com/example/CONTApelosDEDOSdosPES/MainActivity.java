package com.example.CONTApelosDEDOSdosPES;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.os.Handler;


public class MainActivity extends Activity {

    private String currentDificulty = "";

    private int points;

    //Initialize database
    private DatabaseHelper mydb;

    //Global Variables
    private Random random = new Random();
    private int [] newValues = new int[]{};
    private int max = 5;
    private int min = 0;
    private int accumulated_hits;
    private int accumulated_misses;
    private int session_hits;
    private int session_misses;
    private String current_name;
    private int current_id;


    private boolean prevention;

    Map<String, int[]> difs = new HashMap<>();


    // Generate Two randoms numbers
    private int[] GenerateValues(){
        Random rand = new Random();


        int x =  rand.nextInt((max - min) + 1) + min;
        int y = rand.nextInt((max - min) + 1) + min;

        return new int[] {x,y};

    }


    //Names values in interface
    private void SetNewValues(int [] values, TextView firstValue, TextView secondValue,EditText guess){
        firstValue.setText(String.valueOf(values[0]));
        secondValue.setText(String.valueOf(values[1]));
        guess.setText("");
    }


    // Dictionary das dificuldades com os valores respetivos
    private Map<String, int[]> setDictionary(Map<String, int[]> m ){
        m.put("Easy",new int[]{0,5});
        m.put("Medium",new int[]{1,10});
        m.put("Especialist",new int[]{3,12});
        m.put("Epic",new int[]{3,100});
        m.put("Legendary",new int[]{10,1000});


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

    //Devolve a dificulçdade a seguir ou antes ( dependendo do parametro x )
    private String GetStringDiff(Map<String,int []> m , int x,int current_dif) {
        int count = 0;
        for (Map.Entry<String, int[]> entry : m.entrySet()) {
            if (count == current_dif + x) {

                return entry.getKey();
            }
            count++;
        }

        return "Not Found";

    }


    //devolve a string com o numero correspondente á String
    public String ConvertDiffStringToInt(String dif){
        if(dif.equals("Easy")){
            return "0";
        }
        if(dif.equals("Medium")){
            return "1";
        }
        if(dif.equals("Especialist")){
            return "2";
        }
        if(dif.equals("Epic")){
            return "3";
        }
        if(dif.equals("Legendary")){
            return "4";
        }
        return "0";
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prevention = true;

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
        final Button go_scores = (Button) findViewById(R.id.check_session);
        final ImageButton info_proMode = (ImageButton) findViewById(R.id.info_proMode);



        //Load data from intent que vêm do registo e do login
        Intent fromAnotherIntent = getIntent();
        current_id = Integer.valueOf(fromAnotherIntent.getStringExtra("ID"));
        current_name = fromAnotherIntent.getStringExtra("Username");
        //Mais uma vez , garantir que nao perdemos os hits e os misses
        session_misses = Integer.valueOf(fromAnotherIntent.getStringExtra("misses"));
        session_hits = Integer.valueOf(fromAnotherIntent.getStringExtra("hits"));
        //DEBUG
        System.out.println(session_hits);



        //Botão de ajuda para o pro mode
        info_proMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Pro mode : If you get the correct answer 10 times in a row, you will pass to the next level. On the other side, if you miss the answer 5 times in a row you will be dropped to the level below.",Toast.LENGTH_LONG).show();
            }
        });


        //Guarda o score e dá reset nos pontos e começa a contar os misses e hits da
        pro_moode.setOnCheckedChangeListener((new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mydb.maxScore(current_id,Integer.valueOf(currentDificulty)) < points){
                    mydb.InserDataIntoScores(current_id,Integer.valueOf(currentDificulty),points);
                    //DEBUG , apgar
                    max_score .setText(String.valueOf(mydb.maxScore(current_id, Integer.valueOf(currentDificulty))));
                }
                accumulated_hits = 0;
                accumulated_misses = 0;
                points = 0;
                score.setText(String.valueOf(points));


            }
        }));


        //Passa para a activity dos session scores
        go_scores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goScores = new Intent(MainActivity.this,Scoreboard.class);
                goScores.putExtra("Username",current_name);
                goScores.putExtra("ID",String.valueOf(current_id));
                //Garantir que os accumulated hits e misses não se perdem
                goScores.putExtra("misses",String.valueOf(session_hits));
                goScores.putExtra("hits",String.valueOf(session_misses));

                prevention = false;


                startActivity(goScores);
            }
        });








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
                            //Hasn't made a choice , nao muda nada
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
                                session_hits ++;
                                //Mete imagens e texto invisível
                                correct_image.setVisibility(View.VISIBLE);
                                correct_text.setVisibility(View.VISIBLE);
                                wrong_image.setVisibility(View.INVISIBLE);
                                wrong_text.setVisibility(View.INVISIBLE);

                                if(pro_moode.isChecked()){

                                    accumulated_hits++;
                                    accumulated_misses = 0;
                                }

                                //Utilizador passou o pro mode , next level
                                if(pro_moode.isChecked() && accumulated_hits== 10){
                                    if(mydb.maxScore(current_id,Integer.valueOf(currentDificulty)) < points){
                                        mydb.InserDataIntoScores(current_id,Integer.valueOf(currentDificulty),points);
                                        //DEBUG

                                        max_score .setText(String.valueOf(mydb.maxScore(current_id, Integer.valueOf(currentDificulty))));
                                    }
                                    if(currentDificulty.equals("4")){
                                        Toast.makeText(MainActivity.this,"You are killing it , there are no more levels left. You are the best ! ", Toast.LENGTH_LONG).show();
                                        accumulated_hits = 0;


                                    }
                                    else {
                                        //Muda para a próxima dificulty
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

                                session_misses ++;

                                //Mete as imagens e texto invisiveis
                                wrong_image.setVisibility(View.VISIBLE);
                                wrong_text.setVisibility(View.VISIBLE);
                                correct_image.setVisibility(View.INVISIBLE);
                                correct_text.setVisibility(View.INVISIBLE);


                                //Check if max score was ultrapassado mete o novo score na base de dados
                                if(mydb.maxScore(current_id,Integer.valueOf(currentDificulty)) < points){
                                    mydb.InserDataIntoScores(current_id,Integer.valueOf(currentDificulty),points);
                                    //DEBUG

                                    max_score .setText(String.valueOf(mydb.maxScore(current_id, Integer.valueOf(currentDificulty))));
                                }

                                //Caso o pro mode esteja ligado começa a contar as vezes que errou seguidas
                                if(pro_moode.isChecked()){
                                    accumulated_misses++;
                                    accumulated_hits = 0;

                                }


                                //Caso o pro mode esteja ligado e tiver chegado ao ponto estabelecido parar descer de nivel
                                if(pro_moode.isChecked() && accumulated_misses == 5){

                                    //Não pode descer mais de nivel
                                    if(currentDificulty.equals("0")){
                                        Toast.makeText(MainActivity.this,"You can't go lower than this :( You have to TRY HARDER!", Toast.LENGTH_LONG).show();
                                        accumulated_misses = 0;


                                    }
                                    //Desce a dificulty
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
                                        //Todas as imagens e textos invisiveis
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

                            //Atrasa o resultado durante 1 segundo
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
                                    //Volta a poder ser introduzido um novo input
                                    guess.setEnabled(true);
                                }
                            },1000);

                        }


                    }
                }
        );

        //Dificuldades diferentes
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
                    //Mudar o max score depois de a dificuldade ser mudada
                    try {
                        max_score.setText(String.valueOf(mydb.maxScore(current_id, Integer.valueOf(currentDificulty))));
                    }
                    catch (Exception e){
                        max_score.setText(String.valueOf(mydb.maxScore(current_id, 0)));

                    }
                    newValues = GenerateValues();
                    SetNewValues(newValues,firstValue,secondValue,guess);
                    //Poe todas as imagens invisiveis
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

    @Override
    protected  void onStop(){
        super.onStop();
        if(prevention) {
            mydb.InsertSessionScores(session_misses, session_hits, current_name);
        }
        mydb.close();
    }



}
