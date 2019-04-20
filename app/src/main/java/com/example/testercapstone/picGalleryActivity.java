package com.example.testercapstone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class picGalleryActivity extends AppCompatActivity {


    Gallery simpleGallery;
    CustomeGalleryAdapter customGalleryAdapter;
    ImageView selectedImageView;
    Button readButton;
    // array of images
    int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4};
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_gallery);
        simpleGallery = (Gallery) findViewById(R.id.simpleGallery); // get the reference of Gallery
        selectedImageView = (ImageView) findViewById(R.id.selectedImageView); // get the reference of ImageView
        //customGalleryAdapter = new CustomeGalleryAdapter(getApplicationContext(), images); // initialize the adapter
        simpleGallery.setAdapter(new CustomeGalleryAdapter(getApplicationContext(), images)); // set the adapter
        simpleGallery.setSpacing(10);
        // perform setOnItemClickListener event on the Gallery
       simpleGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set the selected image in the ImageView
                Intent i = new Intent(getApplicationContext(),descriptionGallery.class);
                selectedImageView.setImageResource(images[position]);
                int j = position;
                i.putExtra("KEY", j);
                startActivity(i);

            }
        });

        Intent i = getIntent();
        final int b = i.getIntExtra("STRING",0);

        selectedImageView.setImageResource(images[b]);

        //Implementing Cole's read/write stuff


        readButton = (Button) findViewById(R.id.readButton);

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PopupActivity.class);
                startActivity(i);
            }
        });

        //Testing passing variables between activities
        int test = getIntent().getIntExtra("Inspection_ID",0);
        Log.i("Inspection ID","" + test);
    }


    public boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    //Check if external storage is writeable

    private boolean isExternalStorageWriteable (){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.i("State", "Yes, it is writeable!");
            return true;
        } else{
            return false;
        }
    }

    //Check if external storage is readable

    private boolean isExternalStorageReadable (){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
            Log.i("State", "Yes, it is readable!");
            return true;
        } else{
            return false;
        }
    }

}
