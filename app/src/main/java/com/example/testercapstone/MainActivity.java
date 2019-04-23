package com.example.testercapstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.FileObserver;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Button loginbtn;
    EditText inspText;
    private static final String TAG = "mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginbtn = (Button) findViewById(R.id.loginbtn);
        inspText = (EditText) findViewById(R.id.inspText);
        startService(new Intent(getBaseContext(), MediaListenerService.class));

        loginbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(getApplicationContext(),picGalleryActivity.class);
                i.putExtra("Inspection_ID",Integer.parseInt(inspText.getText().toString())); //Attach the Inspection ID
                startActivity(i);
            }
        });
    }

}
