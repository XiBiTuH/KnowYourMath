package com.example.knowyourmath;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterViewAnimator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends Activity {

    //Global Variables
    Random random = new Random();
    int [] newValues = new int[]{};
    int max = 5;
    int min = 0;


    Map<String, int[]> difs = new HashMap<>();



    // Calculates the product of two integers
    // Input: x, y
    // Return : Product of x and y
    private String CalcTwoValues(TextView x , TextView y){
        int first  = Integer.valueOf(x.getText().toString());
        int second  = Integer.valueOf(y.getText().toString());

        return String.valueOf(first *second);
    }




    // Generate Two randoms numbers
    // Input : difficulty
    // Return : Two numbers
    private int[] GenerateValues(){

        int x = ThreadLocalRandom.current().nextInt(min,max );
        int y = ThreadLocalRandom.current().nextInt(min,max);

        return new int[] {x,y};

    }


    //Names values in interface
    private void SetNewValues(int [] values, TextView firstValue, TextView secondValue,EditText guess){
        firstValue.setText(String.valueOf(values[0]));
        secondValue.setText(String.valueOf(values[1]));
        guess.setText(" ");
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

        System.out.println(min);
        System.out.println(max);



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button answerButton = (Button) findViewById(R.id.answerButton);
        final TextView firstValue = (TextView) findViewById(R.id.firstValue);
        final TextView secondValue = (TextView) findViewById(R.id.secondValue);
        final EditText guess = (EditText) findViewById(R.id.guess);


        //Put Dictionary keys and values
        difs = setDictionary(difs);


        //Button to submit answer
        answerButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(guess.getText().toString().equals(" ")){
                            //Hasn't made a choice , doens't change anything
                            Toast.makeText(MainActivity.this, "You have to at least try !!! \n Don't give up !!! ", Toast.LENGTH_SHORT).show();
                        }
                        //Entered something on guess input
                        else {
                            newValues = GenerateValues();
                            SetNewValues(newValues,firstValue,secondValue,guess);

                        }


                    }
                }
        );

        //Different dificulties
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this,R.array.dificulty,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ChangeDif((String) parent.getSelectedItem().toString(),difs);
                    newValues = GenerateValues();
                    SetNewValues(newValues,firstValue,secondValue,guess);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }
}
