package com.example.testercapstone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

public class descriptionGallery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_gallery);
        //every new file path you will make a dronemeta object for that file
        File test = new File("test.jpg");
        DroneMeta meta = new DroneMeta(test);
    }
}
