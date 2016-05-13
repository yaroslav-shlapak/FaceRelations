package com.voidgreen.friendsrelations;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

//import com.agileengine.testgallery.R;
//import com.agileengine.testgallery.data.Photo;
//import com.squareup.picasso.Picasso;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<String> photos;
    private ImageView imageView;

    public GridAdapter(Context context, List<String> objects) {

        this.context = context;
        this.photos = objects;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        /*
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_file, parent, false);
        } else {
            view = convertView;
        }

        Photo photo = photos.get(position);
        imageView = (ImageView) view.findViewById(R.id.img);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(5, 5, 5, 5);

        Picasso.with(context).load(photo.getImage_url()).into(imageView);
*/
        return view;
    }

}
