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

import java.util.ArrayList;

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotosFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        updatePhotos();
        return view;


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPhotosFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void updatePhotos() {
        Profile profile = Profile.getCurrentProfile();

        Toast.makeText(getActivity(), "AlbumsFragment onCreateView Profile = " + profile, Toast.LENGTH_SHORT).show();
        if(profile != null && mAlbum != null) {
            getImagesFromAlbum(mAlbum);
        }
    }

    private void getImagesFromAlbum(Album album) {

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(), "/" + album.getId()
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
                                String url = images.getJSONObject(0).getString("source");
                                photoIds.add(url);
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
                                Log.d("url = ", "" + url);

                            }

                            PhotosGridAdapter adapter = new PhotosGridAdapter(
                                    getContext(), photoIds);
                            gridView.setAdapter(adapter);

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
}
