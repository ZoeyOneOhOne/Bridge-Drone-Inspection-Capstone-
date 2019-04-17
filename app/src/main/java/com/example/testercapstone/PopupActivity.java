package com.example.testercapstone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.widget.ImageView;
//NOTE: Alt + Enter will auto import the one you need.

public class PopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*.7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        isExternalStorageWriteable();
        isExternalStorageReadable();

        //Cole's code to access the DJI GO 4 folder and put the images on a bitmap in an imageView.

        Bitmap bitmap = BitmapFactory.decodeFile("/My Files/Device Storage/DJI/dji.go.v4/CACHE_IMAGE");
        ImageView imageView = (ImageView) this.findViewById(R.id.imageView3);
        imageView.setImageBitmap(BitmapFactory.decodeFile("/My Files/Device Storage/DJI/dji.go.v4/CACHE_IMAGE/screen_77c8d7e08d7dc8ba_1555445872689"));
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
