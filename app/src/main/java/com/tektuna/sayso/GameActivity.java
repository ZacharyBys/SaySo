package com.tektuna.sayso;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.logging.Handler;

public class GameActivity extends AppCompatActivity {

    TextView question, answer1, answer2, percentage1, percentage2;
    List<String> output = new LinkedList<String>();
    boolean refreshButton;
    int questionQuantity, currentQuestion;
    int sleepTime = 3000;
    public String[] nums;
    public int answerId;
    public int bluePercentage, yellowPercentage,total;
    public String bP,yP;
    int delaytime = 1900;
    int bluePercentageCounter,yellowPercentageCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        readquestions();

        String[] questionList = output.toArray(new String[output.size()]);
        questionQuantity = (output.size())/4;
        question = (TextView) findViewById(R.id.question);
        answer1 = (TextView) findViewById(R.id.answer1);
        answer2 = (TextView) findViewById(R.id.answer2);
        percentage1 = (TextView) findViewById(R.id.percentage1);
        percentage2 = (TextView) findViewById(R.id.percentage2);

        currentQuestion = (int) (Math.random() * (questionQuantity));

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/yummy.ttf");   //FONTS
        question.setTypeface(type);
        answer1.setTypeface(type);
        answer2.setTypeface(type);
        percentage1.setTypeface(type);
        percentage2.setTypeface(type);

        getWindow().getDecorView().setBackgroundResource(R.drawable.gamebackground);


        question.setText(questionList[currentQuestion*4+1]);
        answer1.setText(questionList[currentQuestion*4+2]);
        answer2.setText(questionList[currentQuestion*4+3]);

        final Button buttonanswer1 = (Button) findViewById(R.id.buttonanswer1);
        buttonanswer1.setOnClickListener(new View.OnClickListener() {                   //BUTTONANSWER1
            public void onClick(View v) {
                ImageView answer1tick = (ImageView) findViewById(R.id.answer1tick);
                //answer1tick.setImageResource(R.drawable.greentick);
                answerId = 1;
                //getPercentage();

                if (refreshButton){
                    refresher();
                    refreshButton = false;
                } else {
                    getWindow().getDecorView().setBackgroundResource(R.drawable.nextbackground);
                    answer1tick.setImageResource(R.drawable.greentick);
                    getPercentage();
                    refreshButton = true;
                }

                /*
                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        refresher();
                    }
                }, delaytime);
                */
            }
        });
        final Button buttonanswer2 = (Button) findViewById(R.id.buttonanswer2);
        buttonanswer2.setOnClickListener(new View.OnClickListener() {                   //BUTTONANSWER2
            public void onClick(View v) {
                ImageView answer2tick = (ImageView) findViewById(R.id.answer2tick);
                //answer2tick.setImageResource(R.drawable.greentick);
                answerId = 2;
                //getPercentage();

                android.os.Handler handler = new android.os.Handler();

                if (refreshButton){
                    refresher();
                    refreshButton = false;
                } else {
                    getWindow().getDecorView().setBackgroundResource(R.drawable.nextbackground);
                    answer2tick.setImageResource(R.drawable.greentick);
                    getPercentage();
                    refreshButton = true;
                }

                /*
                handler.postDelayed(new Runnable() {
                    public void run() {
                        refresher();
                    }
                }, delaytime);
                */
            }
        });


    }



    public void refresher(){
            // Your code here
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);


    }

    public void getPercentage(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Integer.toString(currentQuestion));
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                nums = value.split(" ");
                int answer1 = Integer.parseInt(nums[0]);
                int answer2 = Integer.parseInt(nums[1]);
                if (answerId == 1)
                    answer1++;
                if (answerId == 2)
                    answer2++;
                bluePercentage = (answer1*100)/(answer1+answer2);
                yellowPercentage = 100-bluePercentage;
                bP = String.valueOf(bluePercentage);
                yP = String.valueOf(yellowPercentage);

                StringBuilder sb = new StringBuilder();
                sb.append(answer1);
                sb.append(" ");
                sb.append(answer2);
                String strI = sb.toString();
                FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
                DatabaseReference setRef = dataBase.getReference(Integer.toString(currentQuestion));
                setRef.setValue(strI);
                //showResults();
                startCountAnimation();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void showResults(){

        percentage1.setText(bP + "%");
        percentage2.setText(yP + "%");
    }

    private void startCountAnimation() {
        bluePercentageCounter=0;
        yellowPercentageCounter=0;
        ValueAnimator animator = ValueAnimator.ofInt(0, Math.max(bluePercentage,yellowPercentage));
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                if (Integer.valueOf(animation.getAnimatedValue().toString()) <= bluePercentage)
                         percentage1.setText(animation.getAnimatedValue().toString() + "%");
                if (Integer.valueOf(animation.getAnimatedValue().toString()) <= yellowPercentage)
                        percentage2.setText(animation.getAnimatedValue().toString() + "%");

            }
        });
        animator.start();
    }

    public void readquestions(){
        InputStream input = null;
        try {
            input = getAssets().open("questions.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String str;
        try {
            while((str = reader.readLine()) != null){
                output.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}
