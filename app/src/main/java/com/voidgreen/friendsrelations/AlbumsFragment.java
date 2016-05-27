package com.voidgreen.friendsrelations;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlbumsFragment.OnAlbumsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlbumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumsFragment extends Fragment {

    private OnAlbumsFragmentInteractionListener mListener;

    private Map<String, Album> albumCovers = new HashMap<>();
    private ArrayList<String> albumIds = new ArrayList<>();
    private ArrayList<String> coverIds = new ArrayList<>();

    private ArrayList<Album> albumsList = new ArrayList<>();
    private GridView gridView;

    private AlbumsGridAdapter mAlbumsGridAdapter;
    private List<String> photosList;
    private View view;

    static int counter = 0;
    private Bundle parameters = new Bundle();


    public AlbumsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AlbumsFragment newInstance(String param1, String param2) {
        AlbumsFragment fragment = new AlbumsFragment();
        //Bundle args = new Bundle();

        //fragment.setArguments(args);
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

        view = inflater.inflate(R.layout.fragment_albums, container, false);
        ButterKnife.bind(this, view);

        gridView = (GridView) view.findViewById(R.id.albumGridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                int index = (int) parent.getAdapter().getItemId(position);
                Album album = new ArrayList<>(albumCovers.values()).get(index);
                mListener.onAlbumsFragmentInteraction(album);
            }
        });

        Profile profile = Profile.getCurrentProfile();

        parameters.putString("fields", "id,name,albums,updated_time,created_time,count");
        parameters.putString("limit", "1000");

        //Toast.makeText(getActivity(), "AlbumsFragment onCreateView Profile = " + profile, Toast.LENGTH_SHORT).show();
        if(profile != null) {
            getAlbumPics();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAlbumsFragmentInteractionListener) {
            mListener = (OnAlbumsFragmentInteractionListener) context;
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

    public interface OnAlbumsFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAlbumsFragmentInteraction(Album album);
    }

    public void updateAlbums() {
        Profile profile = Profile.getCurrentProfile();

        Toast.makeText(getActivity(), "AlbumsFragment onCreateView Profile = " + profile, Toast.LENGTH_SHORT).show();
        if(profile != null) {
            getAlbumPics();
        }
    }

    private void getAlbumPics() {


        final GraphRequest.Callback graphCallback = new AlbumGraphRequest();

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + Profile.getCurrentProfile().getId() + "/albums" ,
                graphCallback);
        request.setParameters(parameters);
        request.executeAsync();


    }
    private class AlbumGraphRequest implements GraphRequest.Callback {
        private boolean busy = false;
        @Override
        public void onCompleted(GraphResponse response) {

            try { // Application code
                //Log.d("response=  ", "" + response);
                JSONObject object = response.getJSONObject();
                //Log.d("object=  ", "" + object);

                JSONArray data_array = object.getJSONArray("data");
                //Log.d("data_array =  ", "" + data_array);
                //Log.d("data_array.length =  ", "" + data_array.length());
                for (int i = 0; i < data_array.length(); i++) {
                    JSONObject _pubKey = data_array
                            .getJSONObject(i);
                    String albumId = _pubKey.getString("id");
                    String albumCreationTime = _pubKey.getString("created_time");
                    String albumName = _pubKey.getString("name");
                    int count = _pubKey.getInt("count");
                    //Log.d("FB ALbum ID ==  ", "" + albumId);
                    albumIds.add(albumId);
                    albumsList.add(new Album(albumId, albumName, albumCreationTime, count));
                }

                String link = null;
                JSONObject pagingRequest = null;
                if(response != null) {
                    pagingRequest = object.optJSONObject("paging");
                    Log.d("pagingRequest=  ", "" + pagingRequest);
                    if(pagingRequest != null) {
                        link = pagingRequest.optString("next");
                        //Log.d("link=  ", "" + link);
                    }
                }

                if(!busy) {
                    getAlbums(albumsList);
                }

            } catch (JSONException E) {
                E.printStackTrace();
            }
        }
    };



    // get albums ids -> get each album by id -> get cover photo id -> get cover photo url
    private void getAlbumCover(final String coverId, final boolean flag, final Album album) {


        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(), "/" + coverId, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject object = response.getJSONObject();
                        //Log.d(MainActivity.TAG, "onCompleted: " + object);
                        try {

                            JSONArray images = object.getJSONArray("images");
                            String source = images.getJSONObject(images.length() - 1).getString("source");
                            //Log.d(MainActivity.TAG, "onCompleted: " + source);
                            album.addPhotoUrl(source);
                            coverIds.add(source);
                            albumCovers.put(source, album);
                            if(flag) {
                                albumCovers = sortAlbumByTime(albumCovers);
                                AlbumsGridAdapter adapter = new AlbumsGridAdapter(
                                        getContext(), albumCovers);
                                gridView.setAdapter(adapter);
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,images");
        parameters.putString("limit", "1000");
        request.setParameters(parameters);
        request.executeAsync();
    }




    private void getAlbums(final ArrayList<Album> albumsList) {

        for(int k = 0; k < albumsList.size(); k++) {
            final int kCopy = k;
            GraphRequest request = GraphRequest.newGraphPathRequest(
                    AccessToken.getCurrentAccessToken(), "/" + albumsList.get(k).getId(), new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            JSONObject object = response.getJSONObject();
                            //Log.d(MainActivity.TAG, " getAlbums onCompleted: " + object);
                            try {
                                final JSONObject coverPhotoObject = object.getJSONObject("cover_photo");
                                String coverId = coverPhotoObject.getString("id");
                                getAlbumCover(coverId, kCopy == albumsList.size() - 1 ? true : false, albumsList.get(kCopy));
                                //JSONObject dataObject = dataArray.getJSONObject("cover_photo");
                                //String coverUrl = dataObject.getString("url");
                                //Log.d(TAG, " getAlbumCover onCompleted: " + coverUrl);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, cover_photo");
            parameters.putString("limit", "1000");
            request.setParameters(parameters);
            request.executeAsync();
        }

    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortAlbumByTime(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e2, Map.Entry<K, V> e1) {
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
