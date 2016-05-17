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

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.voidgreen.facerelations.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlbumsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlbumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ArrayList<String> albumIds = new ArrayList<>();
    private ArrayList<String> photoIds = new ArrayList<>();
    private ArrayList<String> coverIds = new ArrayList<>();

    private ArrayList<Album> albumsList = new ArrayList<>();
    private GridView gridView;

    private GridAdapter gridAdapter;
    private List<String> photosList;
    private View view;


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

        gridView = (GridView) view.findViewById(R.id.gridView);

        getAlbumPics();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }

    private void getAlbumPics() {

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,
                                            GraphResponse response) {
                        try { // Application code
                            JSONObject albums = new JSONObject(object
                                    .getString("albums"));

                            JSONArray data_array = albums.getJSONArray("data");
                            Log.d("data_array ==  ", "" + data_array);
                            for (int i = 0; i < data_array.length(); i++) {
                                JSONObject _pubKey = data_array
                                        .getJSONObject(i);
                                String albumId = _pubKey.getString("id");
                                String albumName = _pubKey.getString("name");
                                Log.d("FB ALbum ID ==  ", "" + albumId);
                                albumIds.add(albumId);
                                albumsList.add(new Album(albumId, albumName));

                            }
                            //getAlbumPictures(albumIds); // /getting picsssss
                            //getAlbumCover(albumIds);
                            getAlbums(albumsList);
                        } catch (JSONException E) {
                            E.printStackTrace();
                        }

                    }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields",  "id,name,albums");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void getAlbumPictures(ArrayList<String> albumIds) {

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(), "/" + 1
                        + "/photos/", new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject object = response.getJSONObject();
                        Log.d(MainActivity.TAG, "onCompleted: " + object);
                        try {
                            final JSONArray data_array1 = object.getJSONArray("data");

                            for (int i = 0; i < data_array1.length(); i++) {

                                JSONObject _pubKey = data_array1
                                        .getJSONObject(i);
                                String photoID = _pubKey.getString("id");
                                JSONArray images = _pubKey.getJSONArray("images");
                                Log.d(MainActivity.TAG, "onCompleted: " + images);
                                photoIds.add(images.getJSONObject(0).getString("source"));
                                //String photoImages = _pubKey.get("images");

                                //Bundle photoParameters = new Bundle();
                                //photoParameters.putString("fields", "source");
                                //photoParameters.putString("limit", "100");
                                /*GraphRequest photoSourceRequest = new GraphRequest(
                                        AccessToken.getCurrentAccessToken(),
                                        photoImages + "?fields=source",
                                        null,
                                        HttpMethod.GET,
                                        new GraphRequest.Callback() {
                                            public void onCompleted(GraphResponse response) {
                                                Log.d(TAG, "onCompleted: " + response);
                                                   JSONObject object = response.getJSONObject();
                                                try {
                                                    String source = object.getString("source");
                                                    photoIds.add(source);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                );*/
                                //photoSourceRequest.setParameters(photoParameters);
                                //photoSourceRequest.executeAsync();
                                Log.d("pics id == ", "" + photoID);


                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,images");
        parameters.putString("limit", "100");
        request.setParameters(parameters);
        request.executeAsync();

    }
    // get albums ids -> get each album by id -> get cover photo id -> get cover photo url
    private void getAlbumCover(final String coverId, final boolean flag, final Album album) {


        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(), "/" + coverId, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject object = response.getJSONObject();
                        Log.d(MainActivity.TAG, "onCompleted: " + object);
                        try {

                            JSONArray images = object.getJSONArray("images");
                            String source = images.getJSONObject(0).getString("source");
                            Log.d(MainActivity.TAG, "onCompleted: " + source);
                            album.addPhotoUrl(source);
                            coverIds.add(source);
                            if(flag) {
                                GridAdapter adapter = new GridAdapter(
                                        getContext(), coverIds);
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
        parameters.putString("limit", "100");
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
                            Log.d(MainActivity.TAG, " getAlbums onCompleted: " + object);
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
            request.setParameters(parameters);
            request.executeAsync();
        }

    }

}
