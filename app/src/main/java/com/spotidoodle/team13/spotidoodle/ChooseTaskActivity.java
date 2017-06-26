package com.spotidoodle.team13.spotidoodle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private String CLIENT_ID;
    private String playlist;
    private String playlistUri;
    private int REQUEST_CODE;
    private String ACCSSES_TOKEN;
    private String userID;
    private String playlistTitle;

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
            this.playlistUri =  bundle.get("playlistUri").toString();
            this.ACCSSES_TOKEN = bundle.getString("accessToken");
            this.userID = bundle.getString("userID");
            this.playlistTitle = bundle.getString("playlistTitle");
        }
        System.out.println("_____________________________" + ACCSSES_TOKEN);
        final Button sortPlaylist = (Button) findViewById(R.id.sortMusicButton);
        final Button sortMusic = (Button) findViewById(R.id.sortPlaylistButton);
        sortPlaylist.setOnClickListener(onClickListener);
        sortMusic.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            Intent intent;
            Bundle bundle;
            switch(v.getId()){
                case R.id.sortMusicButton:
                    intent = new Intent(ChooseTaskActivity.this, SortMusicActivity.class);
                    bundle = new Bundle();
                    bundle.putString("playlist", playlist);
                    bundle.putString("playlistUri", playlistUri);
                    bundle.putString("clientID", CLIENT_ID);
                    bundle.putInt("requestCode", REQUEST_CODE);
                    bundle.putString("accessToken", ACCSSES_TOKEN);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.sortPlaylistButton:
                    intent = new Intent(ChooseTaskActivity.this, ChooseSortingAlgorithmActivity.class);
                    bundle = new Bundle();
                    bundle.putString("playlist", playlist);
                    bundle.putString("playlistUri", playlistUri);
                    bundle.putString("clientID", CLIENT_ID);
                    bundle.putInt("requestCode", REQUEST_CODE);
                    bundle.putString("accessToken", ACCSSES_TOKEN);
                    bundle.putString("userID", userID);
                    bundle.putString("playlistTitle", playlistTitle);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }
        }
    };
}
