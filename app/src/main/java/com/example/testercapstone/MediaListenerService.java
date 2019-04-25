package com.example.testercapstone;

import android.app.Service;
import android.content.Intent;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import java.io.File;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.getIntent;

public class MediaListenerService extends Service {

    public static FileObserver observer;
    int inspID;

    public MediaListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        inspID = intent.getIntExtra("Inspection_ID",0);
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startWatching();
    }

    private void startWatching() {

        //The desired path to watch or monitor
        final String pathToWatch = android.os.Environment.getExternalStorageDirectory().toString() + "/DJI/dji.go.v4/CACHE_IMAGE";
        Toast.makeText(this, "My Service Started and trying to watch " + pathToWatch, Toast.LENGTH_LONG).show();
        final String[] f = new String[1];


        observer = new FileObserver(pathToWatch, FileObserver.ALL_EVENTS) { // set up a file observer to watch this directory
            @Override
            public void onEvent(int event, final String file) {
                if (event == FileObserver.CREATE) {
                    f[0] =file;
                    Log.d("MediaListenerService", "File created [" + pathToWatch + file + "]");

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), file + " was saved!", Toast.LENGTH_LONG).show();

                        }
                    });
                    Intent i = new Intent(getApplicationContext(), PopupActivity.class);

                    i.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    i.putExtra("FILEKEY",f[0]);
                    i.putExtra("Inspection_ID",inspID);

                    startActivity(i);
                }

            }
        };
        observer.startWatching();
    }
}
