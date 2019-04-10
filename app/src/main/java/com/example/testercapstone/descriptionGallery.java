package com.example.testercapstone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

public class descriptionGallery extends AppCompatActivity {
    ImageView selectedImageView2;
    int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4};
    Button backBtn;
    EditText editText2;
    EditText editText5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_gallery);
        selectedImageView2 = (ImageView) findViewById(R.id.selectedImageView2); // get the reference of ImageView
        //every new file path you will make a dronemeta object for that file
        File test = new File("test.jpg");
        DroneMeta meta = new DroneMeta(test);

        TempStor stor = new TempStor(getApplicationContext());

        stor.add(test.getPath(),0,"Test Title","This is a test comment.");

        MetaData imgMeta = stor.getByLocation(test.getPath());
        editText2.setText(imgMeta.title);
        editText5.setText(imgMeta.comment);

        Intent i = getIntent();
        final int b = i.getIntExtra("KEY",0);

        selectedImageView2.setImageResource(images[b]);

        backBtn = (Button) findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(getApplicationContext(),picGalleryActivity.class);
                i.putExtra("STRING", b);
                startActivity(i);
            }
        });

    }
}
