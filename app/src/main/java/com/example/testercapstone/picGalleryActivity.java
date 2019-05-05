package com.example.testercapstone;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
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

import com.secneo.sdk.Helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.log.DJILog;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.media.DownloadListener;
import dji.sdk.media.FetchMediaTaskScheduler;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;
import dji.sdk.realname.AppActivationManager;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;


public class picGalleryActivity extends AppCompatActivity {


    Gallery simpleGallery;
    CustomeGalleryAdapter customGalleryAdapter;
    ImageView selectedImageView;
    Button readButton, downloadButton;
    // array of images
    //int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4};
    ArrayList <Bitmap> bitmapImages = new ArrayList<Bitmap>();
    ArrayList <String> imageFilenames = new ArrayList<String>();
    File dir = Environment.getExternalStorageDirectory();
    String data = dir.getPath() + "/DJI/dji.go.v4/CACHE_IMAGE/";
    File file = new File(data);

    List<MediaFile> mediaFileList = new ArrayList<MediaFile>();
    MediaManager mMediaManager;
    //MediaManager.FileListState currentFileListState = MediaManager.FileListState.UNKNOWN;
    MediaManager.FileListState currentFileListState;
    FetchMediaTaskScheduler scheduler;
    DataHandler dh;

    @Override
    protected void onRestart() {
        super.onRestart();
        bitmapImages = traverse(file);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pic_gallery);

        readButton = (Button) findViewById(R.id.readButton);
        downloadButton = (Button) findViewById(R.id.downloadButton);

        bitmapImages = traverse(file);

        //MediaManager.FileListStateListener updateFileListStateListener = new MediaManager.FileListStateListener() {
           // @Override
          //  public void onFileListStateChange(MediaManager.FileListState state) {
             //   currentFileListState = state;
           // }
        //};

        simpleGallery = (Gallery) findViewById(R.id.simpleGallery); // get the reference of Gallery
        selectedImageView = (ImageView) findViewById(R.id.selectedImageView); // get the reference of ImageView
        //customGalleryAdapter = new CustomeGalleryAdapter(getApplicationContext(), images); // initialize the adapter
        simpleGallery.setAdapter(new CustomeGalleryAdapter(getApplicationContext(), bitmapImages)); // set the adapter
        simpleGallery.setSpacing(10);
        // perform setOnItemClickListener event on the Gallery
       simpleGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set the selected image in the ImageView
                Intent i = new Intent(getApplicationContext(),descriptionGallery.class);
                selectedImageView.setImageBitmap(bitmapImages.get(position));
                String filename = imageFilenames.get(position);
                i.putExtra("KEY", position);
                i.putExtra("Filename", filename);
                startActivity(i);

            }
        });

       downloadButton.setOnClickListener(new View.OnClickListener(){

           @Override
           public void onClick(View v) {
               DJISDKManager.SDKManagerCallback mDJISDKManagerCallback = new DJISDKManager.SDKManagerCallback() {

                   //Listens to the SDK registration result
                   @Override
                   public void onRegister(DJIError error) {

                       if (error == DJISDKError.REGISTRATION_SUCCESS) {

                           Handler handler = new Handler(Looper.getMainLooper());
                           handler.post(new Runnable() {
                               @Override
                               public void run() {
                                   Toast.makeText(getApplicationContext(), "Register Success", Toast.LENGTH_LONG).show();
                               }
                           });

                           DJISDKManager.getInstance().startConnectionToProduct();

                       } else {

                           Handler handler = new Handler(Looper.getMainLooper());
                           handler.post(new Runnable() {

                               @Override
                               public void run() {
                                   Toast.makeText(getApplicationContext(), "Register sdk fails, check network is available", Toast.LENGTH_LONG).show();
                               }
                           });

                       }
                       Log.e("TAG", error.toString());
                   }

                   @Override
                   public void onProductDisconnect() {
                       Log.d("TAG", "onProductDisconnect");
                   }

                   @Override
                   public void onProductConnect(BaseProduct baseProduct) {
                       Log.d("TAG", String.format("onProductConnect newProduct:%s", baseProduct));

                   }

                   @Override
                   public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent,
                                                 BaseComponent newComponent) {
                       if (newComponent != null) {
                           newComponent.setComponentListener(new BaseComponent.ComponentListener() {

                               @Override
                               public void onConnectivityChange(boolean isConnected) {
                                   Log.d("TAG", "onComponentConnectivityChanged: " + isConnected);
                               }
                           });
                       }

                       Log.d("TAG",
                               String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s",
                                       componentKey,
                                       oldComponent,
                                       newComponent));

                   }
               };
               DJISDKManager.getInstance().registerApp(getApplicationContext(), mDJISDKManagerCallback);
               if (DJISDKManager.getInstance().startConnectionToProduct()) {
                   if (DJIApplication.getProductInstance() == null) {
                       mediaFileList.clear();
                       Toast.makeText(getBaseContext(), "Drone Disconnected", Toast.LENGTH_LONG).show();
                       return;
                   } else {
                       if (null != DJIApplication.getCameraInstance() && DJIApplication.getCameraInstance().isMediaDownloadModeSupported()) {
                           mMediaManager = DJIApplication.getCameraInstance().getMediaManager();
                       } else if (null != DJIApplication.getCameraInstance()
                               && !DJIApplication.getCameraInstance().isMediaDownloadModeSupported()) {
                           Toast.makeText(getBaseContext(), "Download not supported", Toast.LENGTH_LONG).show();
                           return;
                       }
                   }
                   if (mMediaManager != null) {

                       if ((currentFileListState == MediaManager.FileListState.SYNCING) || (currentFileListState == MediaManager.FileListState.DELETING)) {
                           Toast.makeText(getBaseContext(), "Media Manager is busy.", Toast.LENGTH_LONG).show();
                       } else {

                           mMediaManager.refreshFileListOfStorageLocation(SettingsDefinitions.StorageLocation.SDCARD, new CommonCallbacks.CompletionCallback() {

                               @Override
                               public void onResult(DJIError djiError) {
                                   if (null == djiError) {
                                       //Reset data
                                   /*if (currentFileListState != MediaManager.FileListState.INCOMPLETE) {
                                       mediaFileList.clear();
                                   }*/

                                       mediaFileList = mMediaManager.getSDCardFileListSnapshot();
                                       Collections.sort(mediaFileList, new Comparator<MediaFile>() {
                                           @Override
                                           public int compare(MediaFile lhs, MediaFile rhs) {
                                               if (lhs.getTimeCreated() < rhs.getTimeCreated()) {
                                                   return 1;
                                               } else if (lhs.getTimeCreated() > rhs.getTimeCreated()) {
                                                   return -1;
                                               }
                                               return 0;
                                           }
                                       });
                                       scheduler.resume(new CommonCallbacks.CompletionCallback() {
                                           @Override
                                           public void onResult(DJIError error) {
                                               if (error == null) {
                                               }
                                           }
                                       });
                                   } else {

                                   }
                               }
                           });
                           File imagePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                           for (MediaFile file : mediaFileList) {
                               TempStor ts = new TempStor(getApplicationContext());
                               MetaData[] files = ts.getByLocation(file.getFileName());
                               if (files.length > 0) {
                                   file.fetchFileData(new File(imagePath.getPath() + "/Capstone"), file.getFileName(), new DownloadListener<String>() {
                                       @Override
                                       public void onStart() {

                                       }

                                       @Override
                                       public void onRateUpdate(long l, long l1, long l2) {

                                       }

                                       @Override
                                       public void onProgress(long l, long l1) {

                                       }

                                       @Override
                                       public void onSuccess(String s) {

                                       }

                                       @Override
                                       public void onFailure(DJIError djiError) {

                                       }
                                   });
                               }
                           }
                       }
                   }
                   DJISDKManager.getInstance().stopConnectionToProduct();
               }
           }
       });

        Intent i = getIntent();
        final int b = i.getIntExtra("STRING",0);

        selectedImageView.setImageBitmap(bitmapImages.get(b));

        //Implementing Cole's read/write stuff




        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PopupActivity.class);
                startActivity(i);
            }
        });

        //Testing passing variables between activities
        int test = getIntent().getIntExtra("Inspection_ID",0);
        dh = new DataHandler(test,getApplicationContext());
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
//traverse the directory for the files
    private ArrayList<Bitmap> traverse (File d)
    {
        ArrayList<Bitmap> b = new ArrayList<Bitmap>();
        if(d.exists())
        {
            File[] files = d.listFiles();
            for(int i = 0; i < files.length; i++)
            {
                imageFilenames.add(files[i].getName());
                Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(files[i]));
                b.add(bitmap);
            }
        }
        return b;
    }

}
