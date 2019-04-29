package com.example.testercapstone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class descriptionGallery extends AppCompatActivity {
    ImageView selectedImageView2;
    //int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4};
    Button backBtn;
    ArrayList <Bitmap> bitmapImages = new ArrayList<Bitmap>();
    File dir = Environment.getExternalStorageDirectory();
    String data = dir.getPath() + "/DJI/dji.go.v4/CACHE_IMAGE/";
    File file = new File(data);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_gallery);
        selectedImageView2 = (ImageView) findViewById(R.id.selectedImageView2); // get the reference of ImageView
        //every new file path you will make a dronemeta object for that file
        //File test = new File("test.jpg");
        //DroneMeta meta = new DroneMeta(test);

        bitmapImages = traverse(file);

        Intent i = getIntent();
        final int b = i.getIntExtra("KEY",0);

        selectedImageView2.setImageBitmap(bitmapImages.get(b));

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

    //traverse the directory for the files
    private ArrayList<Bitmap> traverse (File d)
    {
        ArrayList<Bitmap> b = new ArrayList<Bitmap>();
        if(d.exists())
        {
            File[] files = d.listFiles();
            for(int i = 0; i < files.length; i++)
            {
                if(files[i].isFile()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(files[i]));
                    b.add(bitmap);
                }
            }
        }
        return b;
    }
}
