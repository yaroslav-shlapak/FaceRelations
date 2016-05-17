package com.voidgreen.friendsrelations;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ProfilePictureView;
import com.voidgreen.facerelations.R;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener, AlbumsFragment.OnFragmentInteractionListener {
    public static final String TAG = "FaceRelations";

    private LoginFragment loginFragment;
    private AlbumsFragment albumsFragment;

    private ProfilePictureView profilePictureView;;
    private TextView drawerUserName;

    private DrawerLayout drawerLayout;
    private View content;

    private CallbackManager callbackManager;

    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private View headerView;

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

        //handle DrawerLayout
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

        headerView = view.inflateHeaderView(R.layout.drawer_header);
        profilePictureView = (ProfilePictureView) headerView.findViewById(R.id.profilePicture);
        drawerUserName = (TextView) headerView.findViewById(R.id.userName);

        final Bundle bundle = savedInstanceState;
        profilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoginFragment();
                drawerLayout.closeDrawers();

            }
        });

        accessToken = AccessToken.getCurrentAccessToken();
        //handle fragments
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup

            // If the access token is available already assign it.

            if(accessToken != null) {
                albumsFragment = new AlbumsFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_placeholder, albumsFragment).commit();
            } else {
                loginFragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_placeholder, loginFragment).commit();
            }

        } else {
            if(accessToken != null) {
                loginFragment = (LoginFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_placeholder);
            } else {
                albumsFragment = (AlbumsFragment)  getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_placeholder);
            }

            // Or set the fragment from restored state info

        }

        //handle facebook authentification
        Profile profile = Profile.getCurrentProfile();
        if(profile != null) {
            drawerUserName.setText(profile.getFirstName() + " " + profile.getLastName());
            profilePictureView.setProfileId(profile.getId());
        } else {
            LoginManager loginManager = LoginManager.getInstance();

            loginManager.registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code

                            //Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                            Profile profile = Profile.getCurrentProfile();
                            if (profile != null) {
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
        }

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
        //accessTokenTracker.stopTracking();
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


    @Override
    public void onFragmentInteraction(Profile profile) {
        if(profile != null) {
            drawerUserName.setText(profile.getFirstName() + " " + profile.getLastName());
            profilePictureView.setProfileId(profile.getId());
            showAlbumsFragment();

        } else {
            Toast.makeText(MainActivity.this, "onFragmentInteraction", Toast.LENGTH_SHORT).show();

            drawerUserName.setText("");
            profilePictureView.setProfileId(null);
            showLoginFragment();

        }
    }

    @Override
    public void onFragmentInteraction() {

    }

    private void showLoginFragment() {
        loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, loginFragment).commit();
    }

    private void showAlbumsFragment() {
        albumsFragment = new AlbumsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, albumsFragment).commit();
    }
}
