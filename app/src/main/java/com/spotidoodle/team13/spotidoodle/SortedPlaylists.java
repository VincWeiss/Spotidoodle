package com.spotidoodle.team13.spotidoodle;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Result;
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
    private Float algorithm;
    private TreeMap <Float, PlaylistTrack> unsortedTracks;

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
            this.algorithm = bundle.getFloat("algorithm");

        }
        this.api = new SpotifyApi();
        this.api.setAccessToken(this.ACCSSES_TOKEN);
        spotify = api.getService();
        final TextView title = (TextView) findViewById(R.id.playlistTitle);
        title.setText(this.playlistTitle);

        if (intent.getFlags() == 0) {
            spotify.getPlaylistTracks(userID, playlist, new Callback<Pager<PlaylistTrack>>() {
                @Override
                public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                    List<PlaylistTrack> playlistTracks = playlistTrackPager.items;
                    final TableLayout playlistTable = (TableLayout) findViewById(R.id.playlistTable);
                    for( final PlaylistTrack track : playlistTracks){
                        Button song = new Button(SortedPlaylists.this);
                        setButtonLayout(song);
                        final TextView value = new TextView((SortedPlaylists.this));
                        song.setText(track.track.name);
                        spotify.getTrackAudioFeatures(track.track.id, new Callback<AudioFeaturesTrack>() {
                            @Override
                            public void success(AudioFeaturesTrack audioFeaturesTrack, Response response) {
                                value.setText(String.valueOf(audioFeaturesTrack.danceability));
                                setTextLayout(value);
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
        } else {
            this.unsortedTracks = new TreeMap(Collections.reverseOrder());
            final TableLayout playlistTable = (TableLayout) findViewById(R.id.playlistTable);
            spotify.getPlaylistTracks(userID, playlist, new Callback<Pager<PlaylistTrack>>() {
                @Override
                public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                    final List<PlaylistTrack> playlistTracks = playlistTrackPager.items;
                    for (final PlaylistTrack track : playlistTracks) {
                        spotify.getTrackAudioFeatures(track.track.id, new Callback<AudioFeaturesTrack>() {
                            @Override
                            public void success(AudioFeaturesTrack audioFeaturesTrack, Response response) {
                                unsortedTracks.put(audioFeaturesTrack.danceability, track);
                                if (playlistTracks.size() == unsortedTracks.size()) {
                                    for (Map.Entry<Float, PlaylistTrack> track : unsortedTracks.entrySet()) {
                                        Button song = new Button(SortedPlaylists.this);
                                        final TextView value = new TextView((SortedPlaylists.this));
                                        song.setText(track.getValue().track.name);
                                        setButtonLayout(song);
                                        value.setText(track.getKey().toString());
                                        setTextLayout(value);
                                        TableRow row = new TableRow(SortedPlaylists.this);
                                        GridLayout grid = new GridLayout(SortedPlaylists.this);
                                        TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                                        grid.addView(song);
                                        grid.addView(value);
                                        row.addView(grid, rowLayout);
                                        playlistTable.addView(row);
                                    }
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                error.printStackTrace();
                            }
                        });
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    title.setText("no songs found");
                    error.printStackTrace();
                }
            });
        }
        final Button sortPlaylist = (Button) findViewById(R.id.sortButton);
        sortPlaylist.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.sortButton:
                    finish();
                    startActivity(getIntent());
                    break;
            }
        }
    };

    private void setButtonLayout(Button button){
        button.setBackgroundResource(R.drawable.buttonstyling);
        button.setAlpha((float) 0.8);
        button.setWidth(1100);
    }

    private void setTextLayout(TextView text) {
        //text.setBackgroundColor(Color.parseColor("#ff0099cc"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        //text.setTextColor(Color.parseColor("#ff000000"));
    }
}
