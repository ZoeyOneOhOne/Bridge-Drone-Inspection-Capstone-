package com.example.testercapstone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;

public class picGalleryActivity extends AppCompatActivity {

    Gallery simpleGallery;
    CustomeGalleryAdapter customGalleryAdapter;
    ImageView selectedImageView;
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
    }
}
