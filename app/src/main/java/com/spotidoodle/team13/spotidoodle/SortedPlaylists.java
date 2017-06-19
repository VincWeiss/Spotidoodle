package com.spotidoodle.team13.spotidoodle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
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
    private AudioFeaturesTrack trackAnalyser;
    private SpotifyApi api;
    private int REQUEST_CODE;
    private String ACCSSES_TOKEN;
    private String userID;
    private PlaylistTrack tracks;
    private static final String REDIRECT_URI = "http://spotidoodle2.com/callback/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sorted_playlist);

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
        }
        this.api = new SpotifyApi();
        this.api.setAccessToken(this.ACCSSES_TOKEN);
        spotify = api.getService();
        final TextView title = (TextView) findViewById(R.id.playlistTitle);

        spotify.getPlaylistTracks(userID,playlist, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                List<PlaylistTrack> playlistTracks = playlistTrackPager.items;
                final TableLayout playlistTable = (TableLayout) findViewById(R.id.playlistTable);
                for( PlaylistTrack track : playlistTracks){
                    Button song = new Button(SortedPlaylists.this);
                    song.setText(track.track.name);
                    TableRow row = new TableRow(SortedPlaylists.this);
                    TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    row.addView(song, rowLayout);
                    playlistTable.addView(row);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                title.setText("no songs found");
                error.printStackTrace();
            }
        });
    }
}
