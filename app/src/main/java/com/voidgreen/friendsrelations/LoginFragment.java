package com.voidgreen.friendsrelations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.voidgreen.facerelations.R;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    @Bind(R.id.login_button)LoginButton loginButton;
    private ProfilePictureView profilePictureView;;
    private TextView drawerUserName;

    private DrawerLayout drawerLayout;
    private View content;

    private CallbackManager callbackManager;

    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    private View view;

    private List<String> permisionsList = Arrays.asList("public_profile", "user_photos");

    private OnFragmentInteractionListener onFragmentInteractionListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        FacebookSdk.sdkInitialize(getContext());
        //AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        Log.d(MainActivity.TAG, "onCreateView onFragmentInteractionListener = " + onFragmentInteractionListener);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                if(currentAccessToken == null) {
                    Log.d(MainActivity.TAG, "onCurrentAccessTokenChanged onFragmentInteractionListener = " + onFragmentInteractionListener);
                    onFragmentInteractionListener.onFragmentInteraction(null);
                } else {
                    accessToken = currentAccessToken;
                }
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null) {
            Log.d(MainActivity.TAG, "permitions: " + accessToken.getPermissions());
        } else {
            Log.d(MainActivity.TAG, "accessToken: is null ");
        }

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        onFragmentInteractionListener.onFragmentInteraction(Profile.getCurrentProfile());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Toast.makeText(getContext(), "cancel", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                    }
                });

        return view;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(MainActivity.TAG, "onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            onFragmentInteractionListener = (OnFragmentInteractionListener) context;
            Log.d(MainActivity.TAG, "onCurrentAccessTokenChanged onFragmentInteractionListener = " + onFragmentInteractionListener);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(MainActivity.TAG, "onDetach");
        onFragmentInteractionListener = null;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Profile profile);
    }
}
