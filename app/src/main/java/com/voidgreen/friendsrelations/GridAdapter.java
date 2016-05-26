package com.voidgreen.friendsrelations;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.voidgreen.facerelations.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private  List<String> photos;
    private  List<Album> albums;
    private ImageView imageView;
    private TextView albumName;
    private TextView albumCount;

    public GridAdapter(Context context, Map<String, Album> objects) {

        this.context = context;
        this.photos = new ArrayList<>(objects.keySet());
        this.albums = new ArrayList<>(objects.values());
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

        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_item, parent, false);
        } else {
            view = convertView;
        }

        String url = photos.get(position);
        Album album = albums.get(position);
        imageView = (SquareImageView) view.findViewById(R.id.imageView);
        albumName = (TextView) view.findViewById(R.id.albumName);
        albumCount = (TextView) view.findViewById(R.id.albumCount);

        albumName.setText(album.getName());
        albumCount.setText(Integer.toString(album.getCount()));
        //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(5, 5, 5, 5);

        /*Picasso.with(context).load(url)
                .resize(0, 400).into(imageView);*/

        Picasso.with(context)
                .load(url)
                .fit().centerCrop()
                .error(R.drawable.com_facebook_favicon_white)
                .placeholder(R.drawable.ic_file_download_black_24dp)
                .into(imageView);
        return view;
    }

}
