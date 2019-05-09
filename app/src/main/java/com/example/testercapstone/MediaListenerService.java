package com.example.testercapstone;

import android.app.Service;
import android.content.Intent;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
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
import dji.sdk.media.FetchMediaTaskScheduler;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MediaListenerService extends Service {

    public static FileObserver observer;
    int inspID;
    int last;


    public MediaListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        inspID = intent.getIntExtra("Inspection_ID", 0);
        last = intent.getIntExtra("DRONEKEY", 0);
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        inspID = intent.getIntExtra("Inspection_ID", 0);
        last = intent.getIntExtra("DRONEKEY", 0);
        Log.d("DRONEKEY Received", last + "");
        startWatching();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void startWatching() {
        //The desired path to watch or monitor
        final String pathToWatch = android.os.Environment.getExternalStorageDirectory().toString() + "/DJI/dji.go.v4/CACHE_IMAGE";


        Toast.makeText(getBaseContext(), "My Service Started and trying to watch " + pathToWatch, Toast.LENGTH_LONG).show();
        final String[] f = new String[1];

        Log.d("Last Photo", last + "");

        observer = new FileObserver(pathToWatch, FileObserver.ALL_EVENTS) { // set up a file observer to watch this directory
            int current = last + 1;

            @Override
            public void onEvent(int event, final String file) {
                if (event == FileObserver.CREATE) {
                    f[0] = file;
                    File path = new File(pathToWatch);
                    int count = path.list().length - 1;
                    File bla = new File(pathToWatch + "/" + file);
                    DroneMeta meta = new DroneMeta(bla);
                    try {
                        meta.writeTag("tag", count + "");
                    } catch (ImageReadException e) {
                        e.printStackTrace();
                    } catch (ImageWriteException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("MediaListenerService", "File created [" + pathToWatch + "/" + file + "]");

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), file + " was saved!", Toast.LENGTH_LONG).show();

                        }
                    });
                    if (file.matches(".*\\.[jJ][pP][gG]")) {

                        Intent i = new Intent(getApplicationContext(), PopupActivity.class);

                        i.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        i.putExtra("FILEKEY", f[0]);
                        i.putExtra("DRONEKEY", current++);
                        i.putExtra("Inspection_ID", inspID);

                        startActivity(i);
                    }
                }


            }
        };

        observer.startWatching();

    }
}

