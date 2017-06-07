package com.tektuna.sayso;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TitleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        ///////////////////////////////////////////////////////////////////////////////////
        TextView title = (TextView) findViewById(R.id.title);
        TextView subtitle = (TextView) findViewById(R.id.subtitle);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/yummy.ttf");   //FONTS
        title.setTypeface(type);
        subtitle.setTypeface(type);
        ///////////////////////////////////////////////////////////////////////////////////


        final Button button = (Button) findViewById(R.id.playbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(TitleActivity.this, GameActivity.class));
            }
        });
    }
}
