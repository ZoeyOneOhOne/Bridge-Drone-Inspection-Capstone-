package com.example.testercapstone;

import android.content.Intent;
import android.os.FileObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Button loginbtn, toWritePage;
    private static final String TAG = "mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toWritePage = (Button) findViewById(R.id.toWritePage);

        toWritePage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(getApplicationContext(),Writefile.class);
                startActivity(i);
            }
        });

        //this is to try and listen to the file and then pull up the opop up activity wiht the intent
        FileObserver observer = new FileObserver(android.os.Environment.getExternalStorageDirectory().toString() + "/DJI/dji.go.v4/CACHE_IMAGE") { // set up a file observer to watch this directory on sd card
            @Override
            public void onEvent(int event, String file) {
                if(event == FileObserver.CREATE){ // check if its a "create"
                    Log.d(TAG, "File created [" + android.os.Environment.getExternalStorageDirectory().toString() + "/DCIM/100MEDIA/" + file + "]");
                    Intent i = new Intent(getApplicationContext(), PopupActivity.class);
                    startActivity(i);
                }
            }
        };


        loginbtn = (Button) findViewById(R.id.loginbtn);

        loginbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(getApplicationContext(),picGalleryActivity.class);
                startActivity(i);
            }
        });
    }

}
