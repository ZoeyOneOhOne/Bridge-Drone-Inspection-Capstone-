package com.example.testercapstone;

import android.content.Context;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;

import java.io.IOException;

public class DataHandler {

    int inspID;
    private TempStor ts;

    public DataHandler(int inspID, Context context){
        ts = new TempStor(context);
        this.inspID = inspID;
    }

    public boolean isLoaded(String filename, DroneMeta meta){
        if(meta.getGPS() == null)
            return false;
        else return true;
    }

    public void writeDroneName(String droneName, String filename){

            //Access db
            if(ts.getByLocation(filename).length == 0){
                ts.add(filename,inspID,"","",droneName);
            }else {
                int photoID = ts.getByLocation(filename)[0].photoID;
                ts.editDroneName(droneName, photoID);
            }
    }

    public void writeTitle(String title, String filename, DroneMeta meta){
        if(isLoaded(filename,meta)){
            //Access metadata
            try {
                meta.writeTag("title",title);
            } catch (ImageReadException e) {
                e.printStackTrace();
            } catch (ImageWriteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            //Access db
            if(ts.getByLocation(filename).length == 0){
                ts.add(filename,inspID,title,"","");
            }else{
                int photoID = ts.getByLocation(filename)[0].photoID;
                ts.editTitle(title,photoID);
            }
        }
    }

    public void writeComment(String comment, String filename, DroneMeta meta){
        if(isLoaded(filename,meta)){
            //Access metadata
            try {
                meta.writeComment(comment);
            } catch (ImageReadException e) {
                e.printStackTrace();
            } catch (ImageWriteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            //Access db
            if(ts.getByLocation(filename).length == 0){
                ts.add(filename,inspID,"",comment,"");
            }else{
                int photoID = ts.getByLocation(filename)[0].photoID;
                ts.editComment(comment,photoID);
            }
        }
    }

    public int readInspID(String filename, DroneMeta meta){
        if(isLoaded(filename,meta)){
            //Access metadata
            try {
                return meta.readInspID();
            } catch (ImageReadException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }else{
            //Access db
            MetaData[] data = ts.getByLocation(filename);
            if(data.length > 0)
                return data[0].inspID;
            else return -1;
        }
    }

    public String readTitle(String filename, DroneMeta meta){
        if(isLoaded(filename,meta)){
            //Access metadata
            try {
                return meta.readTag("title");
            } catch (ImageReadException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }else{
            //Access db
            MetaData[] data = ts.getByLocation(filename);
            if(data.length > 0)
                return data[0].title;
            else return null;
        }
    }

    public String readComment(String filename, DroneMeta meta){
        if(isLoaded(filename,meta)){
            //Access metadata
            try {
                return meta.readComment();
            } catch (ImageReadException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }else{
            //Access db
            MetaData[] data = ts.getByLocation(filename);
            if(data.length > 0)
                return data[0].comment;
            else return null;
        }
    }
}
