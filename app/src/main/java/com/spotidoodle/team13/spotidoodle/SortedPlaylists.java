package com.spotidoodle.team13.spotidoodle;

import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.spotify.sdk.android.player.Player;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Oxana on 19.06.2017.
 */

public class SortedPlaylists  extends AppCompatActivity {

    private Player mPlayer;
    private String CLIENT_ID;
    private String playlist;
    private String playlistUri;
    private SpotifyService spotify;
    private SpotifyApi api;
    private int REQUEST_CODE;
    private String ACCSSES_TOKEN;
    private String userID;
    private String playlistTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sorted_playlist);

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
        this.api = new SpotifyApi();
        this.api.setAccessToken(this.ACCSSES_TOKEN);
        spotify = api.getService();
        final TextView title = (TextView) findViewById(R.id.playlistTitle);
        title.setText(this.playlistTitle);

        spotify.getPlaylistTracks(userID, playlist, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                List<PlaylistTrack> playlistTracks = playlistTrackPager.items;
                final TableLayout playlistTable = (TableLayout) findViewById(R.id.playlistTable);
                for( PlaylistTrack track : playlistTracks){
                    Button song = new Button(SortedPlaylists.this);
                    final TextView value = new TextView((SortedPlaylists.this));
                    song.setText(track.track.name);
                    spotify.getTrackAudioFeatures(track.track.id, new Callback<AudioFeaturesTrack>() {
                        @Override
                        public void success(AudioFeaturesTrack audioFeaturesTrack, Response response) {
                            value.setText(String.valueOf(audioFeaturesTrack.danceability));
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            error.printStackTrace();
                        }
                    });
                    TableRow row = new TableRow(SortedPlaylists.this);
                    GridLayout grid = new GridLayout(SortedPlaylists.this);
                    TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    grid.addView(song);
                    grid.addView(value);
                    row.addView(grid, rowLayout);
                    playlistTable.addView(row);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                title.setText("no songs found");
                error.printStackTrace();
            }
        });
        final Button sortPlaylist = (Button) findViewById(R.id.sortButton);
        sortPlaylist.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.sortButton:
                    break;
            }
        }
    };
}