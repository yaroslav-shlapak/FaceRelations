package com.voidgreen.friendsrelations;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.voidgreen.facerelations.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "FaceRelations";

    @Bind(R.id.login_button)LoginButton loginButton;
    @Bind(R.id.user_name) TextView userName;
    @Bind(R.id.editText)
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

    private ArrayList<String> albumsIds;


    private List<String> permisionsList = Arrays.asList("public_profile", "user_photos");
    private ArrayList<String> albumIds = new ArrayList<>();
    private ArrayList<String> photoIds = new ArrayList<>();
    private GridView gridView;

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
                        /*userName.setText("User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken());*/
                        //Toast.makeText(MainActivity.this, profile.getFirstName(), Toast.LENGTH_SHORT).show();

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
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {

                            if(object != null) {
                                Log.d(TAG, object.toString());
                                JSONObject newresponse = object.getJSONObject("albums");
                                //Log.d(TAG, newresponse + "");
                                JSONArray array = newresponse.getJSONArray("data");
                                //Log.d(TAG, array.length() + "");


                                //for (int i = 0; i < array.length(); i++) {
                                    JSONObject res = array.getJSONObject(0);
                                    //Log.d(TAG, res.getString("created_time"));
                                    //Log.d(TAG, res.getString("name"));
                                    Log.d(TAG, res.getString("id"));
                                    albumsIds.add(res.getString("id"));

                                    new GraphRequest(
                                            AccessToken.getCurrentAccessToken(),
                                            "/" + albumsIds.get(0) + "/photos",
                                            null,
                                            HttpMethod.GET,
                                            new GraphRequest.Callback() {
                                                public void onCompleted(GraphResponse response) {


                                                    JSONObject photosObject = response.getJSONObject();
                                                    JSONArray photosArray = null;
                                                    Log.d(TAG, photosObject.toString());
                                                    try {
                                                        photosArray = photosObject.getJSONArray("data");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    GraphRequest photoRequest;
                                                    for (int i = 0; i < photosArray.length(); i++) {
                                                        try {
                                                            JSONObject photoData = photosArray.getJSONObject(i);
                                                            String photoId = photoData.getString("id");
                                                            Log.d(TAG, photoId);



                                                            Bundle photoParameters = new Bundle();
                                                            photoParameters.putString("fields", "link");


                                                            photoRequest = new GraphRequest(
                                                                    AccessToken.getCurrentAccessToken(),
                                                                    "/" + photoId,
                                                                    null,
                                                                    HttpMethod.GET,
                                                                    new GraphRequest.Callback() {

                                                                        @Override
                                                                        public void onCompleted(GraphResponse response) {
                                                                            JSONObject data = response.getJSONObject();
                                                                            try {
                                                                                Log.d(TAG, data.getString("link"));
                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    }
                                                            );

                                                            photoRequest.setParameters(photoParameters);
                                                            photoRequest.executeAsync();

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                    ).executeAsync();

                                //}
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "albums");
        request.setParameters(parameters);
        request.executeAsync();*/



/*
        Bundle photoParameters = new Bundle();
        photoParameters.putString("fields", "id,link");
        GraphRequest photoRequest =  new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + photoId,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d(TAG, "Whoa");
                        Log.d(TAG, response.toString());

                    }
                }
        );
        photoRequest.setParameters(photoParameters);
        photoRequest.executeAsync();*/
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

                            for (int i = 0; i < data_array.length(); i++) {
                                JSONObject _pubKey = data_array
                                        .getJSONObject(i);
                                String arrayfinal = _pubKey.getString("id");
                                Log.d("FB ALbum ID ==  ", "" + arrayfinal);
                                albumIds.add(arrayfinal);

                            }
                            getAlbum_picture(albumIds); // /getting picsssss
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

    private void getAlbum_picture(ArrayList<String> albumIds) {

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

/*
    // Private method to handle Facebook login and callback
    private void onFblogin() {
        mCallbackManager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this,
                permissionNeeds);

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        System.out.println("Success");
                        GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json,
                                                            GraphResponse response) {

                                        if (response.getError() != null) {
                                            // handle error
                                            System.out.println("ERROR");
                                        } else {
                                            System.out.println("Success");
                                            try {

                                                String jsonresult = String
                                                        .valueOf(json);
                                                Log.e("Login Data", jsonresult);
                                                Log.e("loginResult 1",
                                                        loginResult + "");

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Log.d("cancel", "On cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("error login-", error.toString());
                    }
                });
    }*/

}
