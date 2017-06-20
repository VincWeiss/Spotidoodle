package com.spotidoodle.team13.spotidoodle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.spotify.sdk.android.player.Player;
import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by Oxana on 19.06.2017.
 */

public class ChooseSortingAlgorithmActivity extends AppCompatActivity {

    private String CLIENT_ID;
    private String playlist;
    private String playlistUri;
    private AudioFeaturesTrack trackAnalyser;
    private int REQUEST_CODE;
    private String ACCSSES_TOKEN;
    private String userID;
    private String playlistTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_algorithm);

        this.trackAnalyser = new AudioFeaturesTrack();
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

        final Button danceability = (Button) findViewById(R.id.algorithm1);
        final Button dpi = (Button) findViewById(R.id.algorithm2);
        danceability.setOnClickListener(onClickListener);
        dpi.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            Intent intent;
            Bundle bundle;
            switch(v.getId()){
                case R.id.algorithm1:
                    intent = new Intent(ChooseSortingAlgorithmActivity.this, SortedPlaylists.class);
                    bundle = new Bundle();
                    bundle.putFloat("algorithm", trackAnalyser.danceability);
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
                case R.id.algorithm2:
                    intent = new Intent(ChooseSortingAlgorithmActivity.this, SortedPlaylists.class);
                    bundle = new Bundle();
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }
        }
    };
}
