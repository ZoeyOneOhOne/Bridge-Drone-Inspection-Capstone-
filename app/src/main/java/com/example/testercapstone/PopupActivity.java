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
import android.widget.EditText;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import java.io.File;
//NOTE: Alt + Enter will auto import the one you need.

public class PopupActivity extends AppCompatActivity {

    EditText title, comment;
    DroneMeta meta;
    DataHandler dh;
    String f;
    PopupActivity me = this;

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
        //params.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        getWindow().setAttributes(params);

        isExternalStorageWriteable();
        isExternalStorageReadable();

        //Cole's code to access the DJI GO 4 folder and put the images on a bitmap in an imageView.

        title = (EditText)findViewById(R.id.popupTitle);
        comment = (EditText)findViewById(R.id.popupComment);

        File dir = Environment.getExternalStorageDirectory();

        Intent i = getIntent();
        f = i.getExtras().getString("FILEKEY");
        int inspID = i.getIntExtra("Inspection_ID",0);
        int droneKey = i.getIntExtra("DRONEKEY",0);
        String droneName = String.format("DJI_%04d.jpg",droneKey);
        meta = new DroneMeta(new File(dir.getPath() + "/DJI/dji.go.v4/CACHE_IMAGE/" + f));
        dh = new DataHandler(inspID,getBaseContext());
        dh.writeDroneName(droneName,f);
        Log.d("Drone Filename", droneName);
        Bitmap bitmap = BitmapFactory.decodeFile(dir.getPath() + "/DJI/dji.go.v4/CACHE_IMAGE/" + f);
        while(bitmap == null);
        Log.i("Image Height", "" + bitmap.getHeight());
        ImageView imageView = (ImageView) this.findViewById(R.id.imageView3);
        imageView.setImageBitmap(bitmap);

        Button submit = (Button) findViewById(R.id.popupButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dh.writeTitle(title.getText().toString(),f,meta);
                dh.writeComment(comment.getText().toString(),f,meta);
                me.finish();
            }
        });
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
