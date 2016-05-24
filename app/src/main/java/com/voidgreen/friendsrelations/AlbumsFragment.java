package com.voidgreen.friendsrelations;

import android.content.Context;
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

import java.net.MalformedURLException;
import java.net.URL;
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

        gridView = (GridView) view.findViewById(R.id.gridView);

        Profile profile = Profile.getCurrentProfile();

        parameters.putString("fields", "id,name,albums");
        parameters.putString("limit", "100");

        Toast.makeText(getActivity(), "AlbumsFragment onCreateView Profile = " + profile, Toast.LENGTH_SHORT).show();
        if(profile != null) {
            getAlbumPics();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
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
                "/" + Profile.getCurrentProfile().getId(),
                graphCallback);

        request.setParameters(parameters);
        request.executeAsync();


    }
    private class AlbumGraphRequest implements GraphRequest.Callback {
        private boolean busy = false;
        @Override
        public void onCompleted(GraphResponse response) {

            try { // Application code
                Log.d("response=  ", "" + response);
                JSONObject object = response.getJSONObject();
                Log.d("object=  ", "" + object);
                JSONObject albums = new JSONObject(object
                        .getString("albums"));
                Log.d("albums=  ", "" + albums);

                JSONArray data_array = albums.getJSONArray("data");
                Log.d("data_array =  ", "" + data_array);
                Log.d("data_array.length =  ", "" + data_array.length());
                for (int i = 0; i < data_array.length(); i++) {
                    JSONObject _pubKey = data_array
                            .getJSONObject(i);
                    String albumId = _pubKey.getString("id");
                    String albumName = _pubKey.getString("name");
                    Log.d("FB ALbum ID ==  ", "" + albumId);
                    albumIds.add(albumId);
                    albumsList.add(new Album(albumId, albumName));

                }

                String link = null;
                JSONObject pagingRequest = null;
                if(response != null) {
                    pagingRequest = albums.optJSONObject("paging");
                    Log.d("pagingRequest=  ", "" + pagingRequest);
                    if(pagingRequest != null) {
                        link = pagingRequest.optString("next");
                        Log.d("link=  ", "" + link);

                    }
                }
                //GraphRequest nextRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), new URL(link));
                GraphRequest nextRequest = new GraphRequest();
                //Log.d("nextRequest=  ", "" + nextRequest);
                if (pagingRequest != null) {
                    busy = true;
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,albums");
                    //parameters.putString("limit", "100");
                    String after =  pagingRequest.optJSONObject("cursors").optString("after");
                    Log.d("after=  ", "" + after);
                    parameters.putString("after", after);

                    nextRequest.setAccessToken(AccessToken.getCurrentAccessToken());
                    //nextRequest.setGraphPath(link);

                    nextRequest.setGraphPath("/" + Profile.getCurrentProfile().getId());
                    nextRequest.setCallback(new AlbumGraphRequest());
                    nextRequest.setParameters(parameters);
                    Log.d("GraphPath ", "" + nextRequest.getGraphPath());
                    if(counter < 1) {
                        nextRequest.executeAsync();
                        busy = false;
                    }
                    counter++;
                } else {
                    busy = false;
                }

                //getAlbumPictures(albumIds); // /getting picsssss
                //getAlbumCover(albumIds);
                if(!busy) {
                    getAlbums(albumsList);
                }

            } catch (JSONException E) {
                E.printStackTrace();
            }
        }

        public boolean isBusy() {
            return busy;
        }
    };

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
        parameters.putString("limit", "0");
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
        parameters.putString("limit", "0");
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
            parameters.putString("limit", "100");
            request.setParameters(parameters);
            request.executeAsync();
        }

    }

}
