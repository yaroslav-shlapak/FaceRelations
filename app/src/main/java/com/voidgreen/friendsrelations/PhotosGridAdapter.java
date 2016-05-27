package com.voidgreen.friendsrelations;

/**
 * Created by yaroslav on 27.05.16.
 */
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.voidgreen.facerelations.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PhotosGridAdapter extends BaseAdapter {
    private Context context;
    private  List<String> photos = new ArrayList<>();
    private ImageView imageView;
    private int size = 0;

    public PhotosGridAdapter(Context context, List<String> objects) {

        this.context = context;
        update(objects);
        Log.d("PhotosGrid photos = ", "" + photos);

    }

    public void update(List<String> objects) {
        photos.clear();
        photos.addAll(objects);
        size = objects.size();
        notifyDataSetChanged();
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
            view = inflater.inflate(R.layout.photos_grid_item, parent, false);
        } else {
            view = convertView;
        }

        String url = photos.get(position);
        imageView = (SquareImageView) view.findViewById(R.id.photosImageView);
        imageView.setPadding(5, 5, 5, 5);

        /*Picasso.with(context).load(url)
                .resize(0, 400).into(imageView);*/

        if(position == photos.size() - 1) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            Log.d("getView url = ", "" + url);
            Log.d("getView position = ", "" + position);
            lp.setMargins(0, 0, 0, 200);
            imageView.setLayoutParams(lp);
        } else {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            Log.d("getView url = ", "" + url);
            Log.d("getView position = ", "" + position);
            imageView.setLayoutParams(lp);
        }

        //Log.d("getView url = ", "" + url);

        Picasso.with(context)
                .load(url)
                .fit().centerCrop()
                .error(R.drawable.com_facebook_favicon_white)
                .placeholder(R.drawable.ic_file_download_black_24dp)
                .into(imageView);


        return view;
    }

}