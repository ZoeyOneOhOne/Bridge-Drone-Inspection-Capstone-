package com.example.testercapstone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class descriptionGallery extends AppCompatActivity {
    ImageView selectedImageView2;
    int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_gallery);
        selectedImageView2 = (ImageView) findViewById(R.id.selectedImageView2); // get the reference of ImageView
        //every new file path you will make a dronemeta object for that file
        File test = new File("test.jpg");
        DroneMeta meta = new DroneMeta(test);

        Intent i = getIntent();
        int b = i.getIntExtra("KEY",0);

        selectedImageView2.setImageResource(images[b]);

    }
}
