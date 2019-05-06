package com.example.testercapstone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;

import java.io.File;
import java.io.IOException;

public class descriptionGallery extends AppCompatActivity {
    ImageView selectedImageView2;
    Button backBtn, saveButton, deleteButton;
    EditText titleText, descrText;
    DroneMeta meta;
    DataHandler dh;
    int inspID, key;
    String filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_gallery);

        key = getIntent().getIntExtra("KEY",0);
        inspID = getIntent().getIntExtra("Inspection_ID",0);
        filename = getIntent().getStringExtra("Filename");

        saveButton = (Button) findViewById(R.id.saveButton);
        deleteButton = (Button) findViewById(R.id.deleButton);

        selectedImageView2 = (ImageView) findViewById(R.id.selectedImageView2); // get the reference of ImageView
        //every new file path you will make a dronemeta object for that file
        File root = Environment.getExternalStorageDirectory();
        String path = "/DJI/dji.go.v4/CACHE_IMAGE/";
        File image = new File(root.getPath() + path + filename);

        meta = new DroneMeta(new File(root.getPath() + path + filename));
        dh = new DataHandler(inspID, getApplicationContext());

        Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());

        selectedImageView2.setImageBitmap(bitmap);

        titleText = (EditText) findViewById(R.id.titleText);
        descrText = (EditText) findViewById(R.id.descrText);

        titleText.setText(dh.readTitle(filename,meta));
        descrText.setText(dh.readComment(filename,meta));

        backBtn = (Button) findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(getApplicationContext(),picGalleryActivity.class);
                i.putExtra("STRING", key);
                startActivity(i);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dh.writeTitle(titleText.getText().toString(),filename,meta);
                dh.writeComment(descrText.getText().toString(),filename,meta);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                File root = Environment.getExternalStorageDirectory();
                String path = "/DJI/dji.go.v4/CACHE_IMAGE/";
                File image2 = new File(root.getPath() + path + filename);
                boolean deleted = image2.delete();
                Toast.makeText(getBaseContext(),deleted + "was deleted",Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(),picGalleryActivity.class);
                i.putExtra("STRING", key);
                startActivity(i);
            }
        });

    }
}
