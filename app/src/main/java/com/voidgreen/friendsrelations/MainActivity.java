package com.voidgreen.friendsrelations;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

public class MainActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener, AlbumsFragment.OnFragmentInteractionListener, PhotosFragment.OnPhotosFragmentInteractionListener {
    public static final String TAG = "FaceRelations";

    private static final int LOGIN = 0;
    private static final int ALBUMS_GRID = 1;
    private static final int PHOTOS_GRID = 2;
    private static final int FRAGMENT_COUNT = PHOTOS_GRID + 1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private boolean isResumed = false;

    private Fragment loginFragment;
    private AlbumsFragment albumsFragment;

    private DrawerLayout drawerLayout;
    private View content;
    private View headerView;

    private ProfilePictureView profilePictureView;
    private TextView drawerUserName;

    private AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;
    private AccessToken accessToken;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //https://github.com/facebook/facebook-android-sdk/blob/b384c0655fe96db71229bfdcb981a522f3f1e675/samples/Scrumptious/src/com/facebook/scrumptious/MainActivity.java#L62
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set up facebook
        FacebookSdk.sdkInitialize(getApplicationContext());

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


        FragmentManager fm = getSupportFragmentManager();
        loginFragment = (LoginFragment) fm.findFragmentById(R.id.loginFragment);
        fragments[LOGIN] = (LoginFragment) loginFragment;
        albumsFragment = (AlbumsFragment) fm.findFragmentById(R.id.albumsFragment);
        fragments[ALBUMS_GRID] = albumsFragment;
        fragments[PHOTOS_GRID] = fm.findFragmentById(R.id.photosFragment);

        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();


        // If the access token is available already assign it.
        //accessToken = AccessToken.getCurrentAccessToken();

        /*if(accessToken != null) {
            albumsFragment = new AlbumsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_placeholder, albumsFragment).commit();
        } else {
            loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_placeholder, loginFragment).commit();
        }*/

        //handle facebook authentification
        Profile profile = Profile.getCurrentProfile();
        if(profile != null) {
            drawerUserName.setText(profile.getFirstName() + " " + profile.getLastName());
            profilePictureView.setProfileId(profile.getId());
        }

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if (isResumed) {
                    FragmentManager manager = getSupportFragmentManager();
                    int backStackSize = manager.getBackStackEntryCount();
                    for (int i = 0; i < backStackSize; i++) {
                        manager.popBackStack();
                    }

                    if (currentAccessToken != null) {
                        Profile profile = Profile.getCurrentProfile();
                        drawerUserName.setText(profile.getFirstName() + " " + profile.getLastName());
                        profilePictureView.setProfileId(profile.getId());
                        showFragment(ALBUMS_GRID, false);
                        albumsFragment.updateAlbums();
                    } else {

                        drawerUserName.setText("Username");
                        profilePictureView.setProfileId(null);
                        showFragment(LOGIN, false);
                    }
                }
            }
        };



        /*Profile profile = Profile.getCurrentProfile();
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
        }*/

    }

    @Override
    protected void onResume() {
        super.onResume();

        isResumed = true;

        if (AccessToken.getCurrentAccessToken() != null) {
            // if the user already logged in, try to show the albums fragment
            showFragment(ALBUMS_GRID, false);
        } else {
            // otherwise ask the user to login,
            showFragment(LOGIN, false);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;
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

    @Override
    public void onFragmentInteraction(Profile profile) {
        /*Toast.makeText(MainActivity.this, "MainActivity onFragmentInteraction", Toast.LENGTH_SHORT).show();
        if(profile != null) {
            drawerUserName.setText(profile.getFirstName() + " " + profile.getLastName());
            profilePictureView.setProfileId(profile.getId());
            showAlbumsFragment();
        } else {
            drawerUserName.setText("Username");
            profilePictureView.setProfileId(null);
            showLoginFragment();

        }*/
    }

    @Override
    public void onFragmentInteraction() {

    }

    private void showLoginFragment() {
        showFragment(LOGIN, true);
    }

    private void showAlbumsFragment() {
        showFragment(ALBUMS_GRID, true);
    }

    private void showPhotosFragment() {
        showFragment(PHOTOS_GRID, true);
    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
