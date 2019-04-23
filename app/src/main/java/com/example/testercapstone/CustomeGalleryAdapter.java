package com.example.testercapstone;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.ArrayList;

public class CustomeGalleryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Bitmap> images;

    public CustomeGalleryAdapter(Context c, ArrayList<Bitmap> images) {
        context = c;
        this.images = images;
    }

    // returns the number of images
    public int getCount() {
        return images.size();
    }

    // returns the ID of an item
    public Object getItem(int position) {
        return position;
    }

    // returns the ID of an item
    public long getItemId(int position) {
        return position;
    }

    // returns an ImageView view
    public View getView(int position, View convertView, ViewGroup parent) {

        // create a ImageView programmatically
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(images.get(position)); // set image in ImageView
        imageView.setLayoutParams(new Gallery.LayoutParams(200, 200)); // set ImageView param
        return imageView;
    }
}
