package com.example.automotive.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.automotive.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String ARG_OBJECT = "object";
    public static final String ARG_PLAY_WHEN_READY = "ARG_PLAY_WHEN_READY";
    public static final String ARG_CURRENT_WINDOW = "ARG_CURRENT_WINDOW";
    public static final String ARG_PLAYBACK_POSITION = "ARG_PLAYBACK_POSITION";

    private Uri URI;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    private PlayerView playerView;
    private SimpleExoPlayer player;

    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(/*String param1, String param2*/) {
        VideoFragment fragment = new VideoFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

//        if (savedInstanceState != null) {
//            playWhenReady = savedInstanceState.getBoolean(ARG_PLAY_WHEN_READY);
//            currentWindow = savedInstanceState.getInt(ARG_CURRENT_WINDOW);
//            playbackPosition = savedInstanceState.getLong(ARG_PLAYBACK_POSITION);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        URI = Uri.parse(getString(R.string.media_url_gandalf));

        Bundle args = getArguments();
        ((TextView) view.findViewById(R.id.video_text)).setText(Integer.toString(args.getInt(ARG_OBJECT)));
        //        video
//        https://developer.android.com/codelabs/exoplayer-intro#2
        playerView = view.findViewById(R.id.video_view);
//        initializePlayer();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private void initializePlayer() {
//////////////////////////////////////////////////////
        // Build the media item.
        MediaItem mediaItem = MediaItem.fromUri("http://demo.unified-streaming.com");

        player = new SimpleExoPlayer.Builder(getContext()).build();
// Set the media item to be played.
        player.setMediaItem(mediaItem);

        playerView.setPlayer(player);
// Prepare the player.
        player.prepare();
// Start the playback.
        player.play();
//////////////////////////////////////////////////////


//        DataSource.Factory dataSourceFactory = new RtmpDataSourceFactory();
//        MediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(dataSourceFactory);
//        rtsp
//        MediaItem mediaItem = new MediaItem.Builder().setUri(URI).build();
//
//
////        player = new SimpleExoPlayer.Builder(getContext()).setMediaSourceFactory(mediaSourceFactory).build();
//        player = new SimpleExoPlayer.Builder(getContext()).build();
//
//        playerView.setPlayer(player);
//
//        player.setMediaItem(mediaItem);
//        player.prepare();
//
//        player.setPlayWhenReady(playWhenReady);


// Create a data source factory.
//        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory();
//// Create a DASH media source pointing to a DASH manifest uri.
//        MediaSource mediaSource =
//                new DashMediaSource.Factory(dataSourceFactory)
//                        .createMediaSource(MediaItem.fromUri("http://webcam.st-malo.com/axis-cgi/mjpg/video.cgi?resolution=352x288"));
// Create a player instance.
//        player = new SimpleExoPlayer.Builder(getContext()).build();
//
//        playerView.setPlayer(player);
//
//// Set the media source to be played.
//        player.setMediaSource(mediaSource);
//// Prepare the player.
//        player.prepare();
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }
}