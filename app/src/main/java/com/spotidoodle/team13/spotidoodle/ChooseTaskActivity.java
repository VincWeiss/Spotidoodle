package com.spotidoodle.team13.spotidoodle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

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

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.TrackToRemove;

/**
 * Created by Oxana on 18.06.2017.
 */

public class ChooseTaskActivity  extends AppCompatActivity {

    private Player mPlayer;
    private String CLIENT_ID;
    private String playlist;
    private String playlistUri;
    private SpotifyService spotify;
    private int REQUEST_CODE;
    private static final String REDIRECT_URI = "http://spotidoodle2.com/callback/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_task);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            this.CLIENT_ID = bundle.getString("clientID");
            this.REQUEST_CODE = bundle.getInt("requestCode");
            this.playlist = bundle.getString("playlist");
            System.out.println("_____________________________________" + bundle.get("playlistUri").toString());
            this.playlistUri =  bundle.get("playlistUri").toString();
        }

        final ImageButton sortPlaylist = (ImageButton) findViewById(R.id.imageButton);
        final ImageButton sortMusic = (ImageButton) findViewById(R.id.imageButton2);
        sortPlaylist.setOnClickListener(onClickListener);
        sortMusic.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            Intent intent;
            Bundle bundle;
            switch(v.getId()){
                case R.id.imageButton:
                    intent = new Intent(ChooseTaskActivity.this, SortMusicActivity.class);
                    bundle = new Bundle();
                    bundle.putString("playlist", playlist);
                    bundle.putString("playlistUri", playlistUri);
                    bundle.putString("clientID", CLIENT_ID);
                    bundle.putInt("requestCode", REQUEST_CODE);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.imageButton2:
                    intent = new Intent(ChooseTaskActivity.this, SortMusicActivity.class);
                    bundle = new Bundle();
                    bundle.putString("playlist", playlist);
                    bundle.putString("playlistUri", playlistUri);
                    bundle.putString("clientID", CLIENT_ID);
                    bundle.putInt("requestCode", REQUEST_CODE);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }
        }
    };
}
