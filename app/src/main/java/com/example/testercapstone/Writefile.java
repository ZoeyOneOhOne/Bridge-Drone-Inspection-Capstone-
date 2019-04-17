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

public class Writefile extends AppCompatActivity {

    EditText fileName;
    EditText text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writefile);

        fileName = findViewById(R.id.fileName);
        text = findViewById(R.id.text);
    }

    public void writeFile(View v){
        if(isExternalStorageWriteable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            File textFile = new File(Environment.getExternalStorageDirectory(), fileName.getText().toString());

            try {
                FileOutputStream fos = new FileOutputStream(textFile);
                fos.write(text.getText().toString().getBytes());
                fos.close();

                Toast.makeText(this, "File saved", Toast.LENGTH_SHORT).show();
            }catch (IOException e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Cannot write to external storage", Toast.LENGTH_SHORT).show();
        }
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

