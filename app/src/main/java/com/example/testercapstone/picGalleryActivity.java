package com.example.testercapstone;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.media.DownloadListener;
import dji.sdk.media.FetchMediaTaskScheduler;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;
import dji.sdk.sdkmanager.DJISDKManager;


public class picGalleryActivity extends AppCompatActivity {


    Gallery simpleGallery;
    CustomeGalleryAdapter customGalleryAdapter;
    ImageView selectedImageView;
    Button readButton, downloadButton;
    // array of images
    //int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4};
    ArrayList<Bitmap> bitmapImages = new ArrayList<Bitmap>();
    ArrayList<String> imageFilenames = new ArrayList<String>();
    File dir = Environment.getExternalStorageDirectory();
    String data = dir.getPath() + "/DJI/dji.go.v4/CACHE_IMAGE/";
    File file = new File(data);

    final File imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    final File path = new File(imagePath.getAbsolutePath() + "/Capstone");

    List<MediaFile> mediaFileList = new ArrayList<MediaFile>();
    MediaManager mMediaManager;
    //MediaManager.FileListState currentFileListState = MediaManager.FileListState.UNKNOWN;
    MediaManager.FileListState currentFileListState;
    FetchMediaTaskScheduler scheduler;
    DataHandler dh;
    TempStor ts;

    class MyDownloadListener implements DownloadListener<String>{
        String downloadfile;

        public MyDownloadListener(String file){
            downloadfile = file;
        }

        @Override
        public void onStart() {
            Toast.makeText(getBaseContext(), "File downloading...", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRateUpdate(long l, long l1, long l2) {
            Toast.makeText(getBaseContext(), l1 / l * 100 + " downloaded", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProgress(long l, long l1) {

        }

        @Override
        public void onSuccess(String s) {
            String thisFile = downloadfile;
            Toast.makeText(getBaseContext(), thisFile + " downloaded!", Toast.LENGTH_LONG).show();
            Log.i("Downloaded file", thisFile);
            DroneMeta dm = new DroneMeta(new File(path.getAbsolutePath() + "/" + thisFile));
            MetaData thisMeta = ts.getByDroneName(thisFile)[0];
            try {
                dm.writeInspID(thisMeta.inspID);
                Log.i("Written Inspection ID", thisMeta.inspID + "");
                dm.writeTag("title", thisMeta.title);
                Log.i("Written Title", thisMeta.title);
                dm.writeComment(thisMeta.comment);
                Log.i("Written Comment", thisMeta.comment);
            } catch (ImageReadException e) {
                e.printStackTrace();
            } catch (ImageWriteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(DJIError djiError) {
            Toast.makeText(getBaseContext(), "Download failed", Toast.LENGTH_LONG).show();
            Log.e("Download Error", djiError.getDescription());
        }
    }

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
                Intent i = new Intent(getApplicationContext(), descriptionGallery.class);
                selectedImageView.setImageBitmap(bitmapImages.get(position));
                String filename = imageFilenames.get(position);
                i.putExtra("KEY", position);
                i.putExtra("Filename", filename);
                startActivity(i);

            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


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
                    Toast.makeText(getBaseContext(), "Media Manager found!", Toast.LENGTH_LONG).show();
                    if ((currentFileListState == MediaManager.FileListState.SYNCING) || (currentFileListState == MediaManager.FileListState.DELETING)) {
                        Toast.makeText(getBaseContext(), "Media Manager is busy.", Toast.LENGTH_LONG).show();
                    } else {
                        scheduler = mMediaManager.getScheduler();
                        mMediaManager.refreshFileListOfStorageLocation(SettingsDefinitions.StorageLocation.INTERNAL_STORAGE, new CommonCallbacks.CompletionCallback() {

                            @Override
                            public void onResult(DJIError djiError) {
                                if (null == djiError) {
                                    //Reset data
                                   /*if (currentFileListState != MediaManager.FileListState.INCOMPLETE) {
                                       mediaFileList.clear();
                                   }*/
                                    mediaFileList = mMediaManager.getInternalStorageFileListSnapshot();
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

                                    Log.i("Image Path", path.getAbsolutePath());
                                    if (!path.exists())
                                        path.mkdirs();
                                    final String pathToWatch = android.os.Environment.getExternalStorageDirectory().toString() + "/DJI/dji.go.v4/CACHE_IMAGE";
                                    final File tempPath = new File(pathToWatch);
                                    ts = new TempStor(getApplicationContext());
                                    /*for(File file : tempPath.listFiles()){
                                        if(file.getName().matches(".*\\.jpg")){
                                            DroneMeta meta = new DroneMeta(file);
                                            try {
                                                int tag = Integer.parseInt(meta.readTag("tag"));
                                                md = ts.getByLocation(file.getName())[0];
                                                mediaFileList.get(tag).fetchFileData(path, null, new DownloadListener<String>() {
                                                    @Override
                                                    public void onStart() {
                                                        Toast.makeText(getBaseContext(), "File downloading...", Toast.LENGTH_LONG).show();
                                                    }

                                                    @Override
                                                    public void onRateUpdate(long l, long l1, long l2) {
                                                        Toast.makeText(getBaseContext(), l1 / l * 100 + " downloaded", Toast.LENGTH_LONG).show();
                                                    }

                                                    @Override
                                                    public void onProgress(long l, long l1) {

                                                    }

                                                    @Override
                                                    public void onSuccess(String s) {
                                                        Toast.makeText(getBaseContext(), s + " downloaded!", Toast.LENGTH_LONG).show();
                                                        File newFile = new File(path.getPath() + "/" + s);
                                                        DroneMeta newMeta = new DroneMeta(newFile);
                                                        try {
                                                            newMeta.writeInspID(md.inspID);
                                                            newMeta.writeTag("title",md.title);
                                                            newMeta.writeComment(md.comment);
                                                        } catch (ImageReadException e) {
                                                            e.printStackTrace();
                                                        } catch (ImageWriteException e) {
                                                            e.printStackTrace();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(DJIError djiError) {
                                                        Toast.makeText(getBaseContext(), "Download failed", Toast.LENGTH_LONG).show();
                                                        Log.e("Download Error", djiError.getDescription());
                                                    }
                                                });
                                            } catch (ImageReadException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }*/
                                    for (MediaFile file : mediaFileList) {
                                        if (file.getMediaType() != MediaFile.MediaType.JPEG)
                                            continue;
                                        Log.i("Current file", file.getFileName());
                                        MetaData[] files = ts.getByDroneName(file.getFileName());
                                        if (files.length > 0) {
                                            String downloadfile = file.getFileName();
                                            Toast.makeText(getBaseContext(), "Fetching " + file.getFileName(), Toast.LENGTH_LONG).show();
                                            file.fetchFileData(path, null, new MyDownloadListener(downloadfile));
                                        }
                                    }
                                } else {

                                }
                            }
                        });

                    }
                }
                DJISDKManager.getInstance().stopConnectionToProduct();

            }
        });

        Intent i = getIntent();
        final int b = i.getIntExtra("STRING", 0);
        if(bitmapImages.size() > 0)
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
        int test = getIntent().getIntExtra("Inspection_ID", 0);
        dh = new DataHandler(test, getApplicationContext());
    }


    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    //Check if external storage is writeable

    private boolean isExternalStorageWriteable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.i("State", "Yes, it is writeable!");
            return true;
        } else {
            return false;
        }
    }

    //Check if external storage is readable

    private boolean isExternalStorageReadable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
            Log.i("State", "Yes, it is readable!");
            return true;
        } else {
            return false;
        }
    }

    //traverse the directory for the files
    private ArrayList<Bitmap> traverse(File d) {
        ArrayList<Bitmap> b = new ArrayList<Bitmap>();
        if (d.exists()) {
            File[] files = d.listFiles();
            for (int i = 0; i < files.length; i++) {
                imageFilenames.add(files[i].getName());
                Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(files[i]));
                b.add(bitmap);
            }
        }
        return b;
    }

}
