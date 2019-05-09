package com.example.testercapstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.media.FetchMediaTaskScheduler;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;
import dji.sdk.sdkmanager.DJISDKManager;

public class MainActivity extends AppCompatActivity {

    Button loginbtn;
    EditText inspText;
    private static final String TAG = "mainActivity";

    List<MediaFile> mediaFileList = new ArrayList<MediaFile>();
    MediaManager mMediaManager;
    //MediaManager.FileListState currentFileListState = MediaManager.FileListState.UNKNOWN;
    MediaManager.FileListState currentFileListState;
    FetchMediaTaskScheduler scheduler;
    DataHandler dh;
    MetaData md;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

                    loginbtn = (Button) findViewById(R.id.loginbtn);
                    inspText = (EditText) findViewById(R.id.inspText);

                    loginbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
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
                                                MediaFile lastFile = mediaFileList.get(0);

                                                String prevFile = lastFile.getFileName();
                                                Log.d("Previous File", prevFile);
                                                int last = Integer.parseInt(prevFile.substring(4, prevFile.length() - 4));
                                                Log.d("Index", last + "");
                                                Intent s = new Intent(getBaseContext(), MediaListenerService.class);
                                                s.putExtra("Inspection_ID", Integer.parseInt(inspText.getText().toString())); //Attach the Inspection ID
                                                s.putExtra("DRONEKEY", last);
                                                startService(s);
                                                Intent i = new Intent(getApplicationContext(), picGalleryActivity.class);
                                                i.putExtra("Inspection_ID", Integer.parseInt(inspText.getText().toString())); //Attach the Inspection ID
                                                startActivity(i);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });

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
        DJISDKManager.getInstance().registerApp(MainActivity.this.getApplicationContext(), mDJISDKManagerCallback);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}