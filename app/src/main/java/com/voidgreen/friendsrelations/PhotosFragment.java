package com.voidgreen.friendsrelations;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.voidgreen.facerelations.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPhotosFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotosFragment extends Fragment {


    private ArrayList<String> photoIds = new ArrayList<>();
    private Map<String, Long> photosMap;

    private GridView gridView;

    private PhotosGridAdapter mPhotosGridAdapter;

    public void setAlbum(Album album) {
        mAlbum = album;
    }

    private Album mAlbum;

    private OnPhotosFragmentInteractionListener mListener;

    public PhotosFragment() {
        // Required empty public constructor
    }

    public static PhotosFragment newInstance(String param1, String param2) {
        PhotosFragment fragment = new PhotosFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmentv
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        gridView = (GridView) view.findViewById(R.id.photosGridView);

        photosMap = new HashMap<>();

        updatePhotos();
        return view;


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotosFragmentInteractionListener) {
            mListener = (OnPhotosFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPhotosFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void cleanUp() {
        if(photoIds != null) {
            photoIds.clear();
            //Log.d(MainActivity.TAG, "photoIds.clear()");
        }
        if(mPhotosGridAdapter != null) {
            mPhotosGridAdapter.notifyDataSetChanged();
            //Log.d(MainActivity.TAG, "mPhotosGridAdapter.notifyDataSetChanged();");
        }
    }


    public interface OnPhotosFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void updatePhotos() {
        Profile profile = Profile.getCurrentProfile();

        mPhotosGridAdapter = new PhotosGridAdapter(getContext(), photoIds);
        gridView.setAdapter(mPhotosGridAdapter);
        cleanUp();

        //Toast.makeText(getActivity(), "AlbumsFragment onCreateView Profile = " + profile, Toast.LENGTH_SHORT).show();
        if(profile != null && mAlbum != null) {
            getImagesFromAlbum(mAlbum);
        }
    }

    private void getImagesFromAlbum(Album album) {

        photosMap.clear();
        final Map<String, Long> tempMap = new HashMap();

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(), "/" + album.getId()
                        + "/photos/", new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject object = response.getJSONObject();
                        //Log.d(MainActivity.TAG, "onCompleted: " + object);
                        try {
                            final JSONArray dataArray = object.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {

                                JSONObject dataElement = dataArray
                                        .getJSONObject(i);

                                //Log.d(MainActivity.TAG, "onCompleted dataElement: " + dataElement);
                                String photoID = dataElement.getString("id");
                                String updatedTime = dataElement.getString("created_time");

                                SimpleDateFormat updatedTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
                                Date date = updatedTimeFormat.parse(updatedTime);

                                JSONArray images = dataElement.getJSONArray("images");

                                JSONObject image = images.getJSONObject(images.length() - 1);
                                String url = image.getString("source");

                                tempMap.put(url, date.getTime());

                                Log.d(MainActivity.TAG, "onCompleted: " + photoID + " " + date.getTime() + " " + updatedTime +" " + url);
                                photoIds.add(url);
                                //Log.d("pics id == ", "" + photoID);
                                //Log.d("url = ", "" + url);

                            }

                            //photosMap.putAll(sortByValue(tempMap));
                            photosMap.putAll(tempMap);
                            Log.d("tempMap = ", "" + tempMap);

                            mPhotosGridAdapter.update(photoIds);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,images,updated_time, created_time");
        parameters.putString("limit", "1000");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                return (e1.getValue()).compareTo(e2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
