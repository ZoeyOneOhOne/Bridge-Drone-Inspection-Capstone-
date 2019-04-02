package com.example.testercapstone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.io.File;

//The pop up window code was inspired by a Youtuber of the name Angga Risky: https://www.youtube.com/watch?v=eX-TdY6bLdg

public class MainActivity extends AppCompatActivity {

    Button btn_Cam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_Cam = (Button) findViewById(R.id.btn_Cam);

        btn_Cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PopupActivity.class);
                startActivity(i);
            }
        });
    }
}
