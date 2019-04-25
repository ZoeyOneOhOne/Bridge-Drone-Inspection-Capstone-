package com.example.testercapstone;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;

import java.io.IOException;

public class DataHandler {

    public static boolean isLoaded(String filename, DroneMeta meta){
        if(meta.getGPS() == null)
            return false;
        else return true;
    }

    public static void writeTitle(String title, String filename, DroneMeta meta){
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

        }
    }

    public static void writeComment(String comment, String filename, DroneMeta meta){
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

        }
    }

    public static int readInspID(String filename, DroneMeta meta){
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
            return 0;
        }
    }

    public static String readTitle(String filename, DroneMeta meta){
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
            return null;
        }
    }

    public static String readComment(String filename, DroneMeta meta){
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
            return null;
        }
    }
}
