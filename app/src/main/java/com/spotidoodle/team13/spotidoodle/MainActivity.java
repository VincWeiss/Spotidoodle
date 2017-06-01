package com.spotidoodle.team13.spotidoodle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.view.View;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class MainActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    // TODO: Replace with your client ID
    //SHA1 Fingerprint 3D:5B:E8:6A:56:D1:FF:EC:91:AB:A8:27:50:13:A1:A6:85:35:CA:F6
    private static final String CLIENT_ID = "9f703a39b15a4241b08dcea6685e5f50";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "http://spotidoodle2.com/callback/";

    private Player mPlayer;

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        Button playlistOne = (Button) findViewById(R.id.button);
        playlistOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    onAuthenticationComplete(response);
                    break;
                // Auth flow returned an error
                case ERROR:
                    Log.d("Auth error: ", response.getError());
                    break;
                // Most likely auth flow was cancelled
                default:
                    Log.d("Auth error: ", response.getType().toString());
            }
        }
    }

    private void onAuthenticationComplete(AuthenticationResponse authResponse) {
        // Once we have obtained an authorization token, we can proceed with creating a Player.
        Log.d("myTag", "Got authentication token");
        if (mPlayer == null) {
            Config playerConfig = new Config(getApplicationContext(), authResponse.getAccessToken(), CLIENT_ID);
            // Since the Player is a static singleton owned by the Spotify class, we pass "this" as
            // the second argument in order to refcount it properly. Note that the method
            // Spotify.destroyPlayer() also takes an Object argument, which must be the same as the
            // one passed in here. If you pass different instances to Spotify.getPlayer() and
            // Spotify.destroyPlayer(), that will definitely result in resource leaks.
            mPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer player) {
                    Log.d("myTag", "-- Player initialized --");
                    //mPlayer.setConnectivityStatus(mOperationCallback, getNetworkConnectivity(MainActivity.this));
                    mPlayer.addNotificationCallback(MainActivity.this);
                    mPlayer.addConnectionStateCallback(MainActivity.this);
                    // Trigger UI refresh
                    //updateView();
                }

                @Override
                public void onError(Throwable error) {
                    error.printStackTrace();
                }
            });
        } else {
            mPlayer.login(authResponse.getAccessToken());
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");

        mPlayer.playUri(null, "spotify:track:4aSon118zcGeJoKINAmiav", 0, 0);
    }
    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error e) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }
}