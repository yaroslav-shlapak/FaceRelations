package com.voidgreen.friendsrelations;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.voidgreen.facerelations.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "FaceRelations";

    @Bind(R.id.login_button)LoginButton loginButton;
    @Bind(R.id.user_name) TextView userName;
    EditText albumNumber;
    //@Bind(R.id.profilePicture)
    private ProfilePictureView profilePictureView;;
    //@Bind(R.id.userName)
    private TextView drawerUserName;

    private DrawerLayout drawerLayout;
    private View content;

    @Bind(R.id.get_album) Button getAlbum;
    private Button updateStatusButton;

    private CallbackManager callbackManager;

    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    private List<String> permisionsList = Arrays.asList("public_profile", "user_photos");
    private ArrayList<String> albumIds = new ArrayList<>();
    private ArrayList<String> photoIds = new ArrayList<>();
    private ArrayList<String> coverIds = new ArrayList<>();

    private ArrayList<Album> albumsList = new ArrayList<>();
    private GridView gridView;

    private GridAdapter gridAdapter;
    private List<String> photosList;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView view = (NavigationView) findViewById(R.id.navigation_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Snackbar.make(content, menuItem.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });


        View headerView = view.inflateHeaderView(R.layout.drawer_header);
        profilePictureView= (ProfilePictureView) headerView.findViewById(R.id.profilePicture);
        drawerUserName = (TextView) headerView.findViewById(R.id.userName);
        //loginButton.setReadPermissions("user_friends");
        //loginButton.setReadPermissions(Arrays.asList("user_relationships"));

        loginButton.setReadPermissions(permisionsList/*Arrays.asList("user_friends")*/);
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        //Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                        Profile profile = Profile.getCurrentProfile();
                        if(profile != null) {
                            userName.setText(profile.getFirstName() + " " + profile.getLastName());
                            drawerUserName.setText(profile.getFirstName() + " " + profile.getLastName());
                            profilePictureView.setProfileId(profile.getId());
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Toast.makeText(MainActivity.this, "cancel", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });

        Profile profile = Profile.getCurrentProfile();
        if(profile != null) {
            userName.setText(profile.getFirstName() + " " + profile.getLastName());
            drawerUserName.setText(profile.getFirstName() + " " + profile.getLastName());
            profilePictureView.setProfileId(profile.getId());
        }

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                accessToken = currentAccessToken;
                //Log.d(TAG, "permitions: " + accessToken.getPermissions());
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null) {
            Log.d(TAG, "permitions: " + accessToken.getPermissions());
        } else {
            Log.d(TAG, "accessToken: is null ");
        }


        gridView = (GridView) findViewById(R.id.gridView);

        getAlbum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getAlbumPics();

            }
        });
/*

        pageNumber = photosList.size() / tempList.size() + 1;
        loadingFlag = false;*/



    }

    @Override
    protected void onResume() {
        super.onResume();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
        // launched into.

    }

    @Override
    public void onPause() {
        super.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void getAlbumPics() {

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
        parameters.putString("fields",
                "id,name,email,gender, birthday, friends,albums");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void getAlbumPictures(ArrayList<String> albumIds) {

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(), "/" + albumIds.get(Integer.parseInt(albumNumber.getText().toString()))
                        + "/photos/", new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject object = response.getJSONObject();
                        Log.d(TAG, "onCompleted: " + object);
                        try {
                            final JSONArray data_array1 = object.getJSONArray("data");

                            for (int i = 0; i < data_array1.length(); i++) {

                                JSONObject _pubKey = data_array1
                                        .getJSONObject(i);
                                String photoID = _pubKey.getString("id");
                                JSONArray images = _pubKey.getJSONArray("images");
                                Log.d(TAG, "onCompleted: " + images);
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
                            DetailAdapter adapter = new DetailAdapter(
                                    MainActivity.this, R.layout.grid_item,
                                    photoIds);
                            gridView.setAdapter(adapter);

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
                        Log.d(TAG, "onCompleted: " + object);
                        try {

                                JSONArray images = object.getJSONArray("images");
                                String source = images.getJSONObject(0).getString("source");
                                Log.d(TAG, "onCompleted: " + source);
                                album.addPhotoUrl(source);
                                coverIds.add(source);
                            if(flag) {
                                GridAdapter adapter = new GridAdapter(
                                        MainActivity.this, coverIds);
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
                            Log.d(TAG, " getAlbums onCompleted: " + object);
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
