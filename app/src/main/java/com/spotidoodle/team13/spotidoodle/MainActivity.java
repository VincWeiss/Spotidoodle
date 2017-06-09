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
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackBitrate;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback, Player {

    //SHA1 Fingerprint 3D:5B:E8:6A:56:D1:FF:EC:91:AB:A8:27:50:13:A1:A6:85:35:CA:F6
    private static final String CLIENT_ID = "9f703a39b15a4241b08dcea6685e5f50";
    private static final String REDIRECT_URI = "http://spotidoodle2.com/callback/";
    private static final String TEST_ALBUM_URI = "spotify:album:2lYmxilk8cXJlxxXmns1IU";
    private static final String TEST_PLAYLIST_URI = "spotify:user:1139746471:playlist:2Hcu0u9LX7XqNtYlt6iTSM";
    private Player mPlayer;
    private final Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            System.out.println("OK!");
        }

        @Override
        public void onError(Error error) {
            System.out.println("ERROR:" + error);
        }
    };

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
        Button playlisttTwo = (Button) findViewById(R.id.button2);
        playlistOne.setOnClickListener(onClickListener);
        playlisttTwo.setOnClickListener(onClickListener);
        playlistOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SortMusicActivity.class));
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.button:
                    Intent intent = new Intent(MainActivity.this, SortMusicActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("playlist", TEST_PLAYLIST_URI);
                    bundle.putString("clientID", CLIENT_ID);
                    bundle.putInt("requestCode", REQUEST_CODE);
                    intent.putExtras(bundle);
                    break;
                case R.id.button2:
                    //DO something
                    break;
            }
        }
    };

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

    private void onButtonClicked() {
        final Button playlistOne = (Button) findViewById(R.id.button);
        playlistOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mPlayer.playUri(mOperationCallback, TEST_PLAYLIST_URI, 0, 0);
            }
        });
    }

    private void onAuthenticationComplete(AuthenticationResponse authResponse) {
        // Once we have obtained an authorization token, we can proceed with creating a Player.
        if (mPlayer == null) {
            Config playerConfig = new Config(getApplicationContext(), authResponse.getAccessToken(), CLIENT_ID);
            // Since the Player is a static singleton owned by the Spotify class, we pass "this" as
            // the second argument in order to refcount it properly. Note that the method
            // Spotify.destroyPlayer() also takes an Object argument, which must be the same as the
            // one passed in here. If you pass different instances to Spotify.getPlayer() and
            // Spotify.destroyPlayer(), that will definitely result in resource leaks.
            mPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer spotifyPlayer) {
                    mPlayer = spotifyPlayer;
                    mPlayer.addConnectionStateCallback(MainActivity.this);
                    mPlayer.addNotificationCallback(MainActivity.this);
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
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
        mPlayer.playUri(mOperationCallback, "spotify:track:6GoNSKDv6eKxuHxn2Zcr9o", 0, 0);
        mPlayer.addNotificationCallback(new Player.NotificationCallback() {
            @Override
            public void onPlaybackEvent(PlayerEvent playerEvent) {
                if (playerEvent == PlayerEvent.kSpPlaybackNotifyTrackDelivered) {
                    System.out.println("Delivered");
                }
            }

            @Override
            public void onPlaybackError(Error error) {
            }
        });
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

    @Override
    public boolean addNotificationCallback(NotificationCallback notificationCallback) {
        return false;
    }

    @Override
    public boolean removeNotificationCallback(NotificationCallback notificationCallback) {
        return false;
    }

    @Override
    public void initialize(Config config) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public boolean login(String s) {
        return false;
    }

    @Override
    public boolean logout() {
        return false;
    }

    @Override
    public boolean addConnectionStateCallback(ConnectionStateCallback connectionStateCallback) {
        return false;
    }

    @Override
    public boolean removeConnectionStateCallback(ConnectionStateCallback connectionStateCallback) {
        return false;
    }

    @Override
    public void playUri(OperationCallback operationCallback, String s, int i, int i1) {

    }

    @Override
    public void queue(OperationCallback operationCallback, String s) {

    }

    @Override
    public void pause(OperationCallback operationCallback) {

    }

    @Override
    public void resume(OperationCallback operationCallback) {

    }

    @Override
    public void skipToNext(OperationCallback operationCallback) {

    }

    @Override
    public void skipToPrevious(OperationCallback operationCallback) {

    }

    @Override
    public void seekToPosition(OperationCallback operationCallback, int i) {

    }

    @Override
    public void setShuffle(OperationCallback operationCallback, boolean b) {

    }

    @Override
    public void setRepeat(OperationCallback operationCallback, boolean b) {

    }

    @Override
    public void setPlaybackBitrate(OperationCallback operationCallback, PlaybackBitrate playbackBitrate) {

    }

    @Override
    public void setConnectivityStatus(OperationCallback operationCallback, Connectivity connectivity) {

    }

    @Override
    public void refreshCache() {

    }

    @Override
    public Metadata getMetadata() {
        return null;
    }

    @Override
    public PlaybackState getPlaybackState() {
        return null;
    }
}