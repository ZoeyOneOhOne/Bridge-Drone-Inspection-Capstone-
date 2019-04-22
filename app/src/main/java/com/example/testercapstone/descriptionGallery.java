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

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;

import java.io.File;
import java.io.IOException;

public class descriptionGallery extends AppCompatActivity {
    ImageView selectedImageView2;
    Button backBtn, saveButton;
    EditText titleText, descrText;
    DroneMeta meta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_gallery);

        saveButton = (Button) findViewById(R.id.saveButton);

        selectedImageView2 = (ImageView) findViewById(R.id.selectedImageView2); // get the reference of ImageView
        //every new file path you will make a dronemeta object for that file
        File root = Environment.getExternalStorageDirectory();
        String path = "/DJI/dji.go.v4/CACHE_IMAGE/";
        String filename = "test.jpg";
        File image = new File(root.getPath() + path + filename);
        meta = new DroneMeta(image);

        Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());
        Intent i = getIntent();
        final int b = i.getIntExtra("KEY",0);

        selectedImageView2.setImageBitmap(bitmap);

        titleText = (EditText) findViewById(R.id.titleText);
        descrText = (EditText) findViewById(R.id.descrText);

        try {
            titleText.setText(meta.readTag("title"));
            descrText.setText(meta.readComment());
        } catch (ImageReadException e) {
            Log.e("MetaData Error",e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        backBtn = (Button) findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(getApplicationContext(),picGalleryActivity.class);
                i.putExtra("STRING", b);
                startActivity(i);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try {
                    meta.writeTag("title",titleText.getText().toString());
                    meta.writeComment(descrText.getText().toString());
                } catch (ImageReadException e) {
                    e.printStackTrace();
                } catch (ImageWriteException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
