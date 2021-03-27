package com.example.automotive.Fragments;

import android.bluetooth.BluetoothGattService;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.automotive.Adapters.MyConnectionRecyclerViewAdapter;
import com.example.automotive.R;
import com.example.automotive.SampleApplication;
import com.example.automotive.ViewModels.MyViewModel;
import com.example.automotive.dummy.DummyContent;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.RxBleDeviceServices;
import com.polidea.rxandroidble2.internal.util.UUIDUtil;
import com.polidea.rxandroidble2.internal.util.UUIDUtil_Factory;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.utils.StandardUUIDsParser;

import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String ARG_OBJECT = "object";
    public static final String ARG_PLAY_WHEN_READY = "ARG_PLAY_WHEN_READY";
    public static final String ARG_CURRENT_WINDOW = "ARG_CURRENT_WINDOW";
    public static final String ARG_PLAYBACK_POSITION = "ARG_PLAYBACK_POSITION";
    // 0000xxxx-0000-1000-8000-00805F9B34FB
    public static final String UUID_SERVICE = "00001234-0000-1000-8000-00805F9B34FB";
    public static final String UUID_CHARACTERISTIC_WRITE = "00001234-0000-1000-8000-00805F9B34FB";

    private Uri URI;
    private String mParam1;
    private String mParam2;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    private PlayerView playerView;
    private SimpleExoPlayer player;

    private Unbinder unbinder;
    @BindView(R.id.forward_btn)
    Button forwardButton;
    @BindView(R.id.back_btn)
    Button backwardButton;
    @BindView(R.id.turn_left_btn)
    Button turnLeftButton;
    @BindView(R.id.turn_right_btn)
    Button turnRightButton;
    @BindView(R.id.manual_btn)
    Button manualButton;

    MyViewModel myViewModel;
    private String macAddress;

    private RxBleClient rxBleClient;
    private RxBleDevice bleDevice;

    private Disposable connectionDisposable;

    List<BluetoothGattService> bluetoothGattServiceList;

    String BASE_UUID = "00000000-0000-1000-8000-00805F9B34FB";

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

        View view = inflater.inflate(R.layout.fragment_video, container, false);
        unbinder = ButterKnife.bind(this, view);
        myViewModel = new ViewModelProvider(getActivity()).get(MyViewModel.class);
        this.rxBleClient = SampleApplication.getRxBleClient(getContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        URI = Uri.parse(getString(R.string.media_url_gandalf));

        Bundle args = getArguments();
        //        video
//        https://developer.android.com/codelabs/exoplayer-intro#2
        playerView = view.findViewById(R.id.video_view);

        myViewModel.getBluetoothGattServiceList().observe(getViewLifecycleOwner(), item -> {
            this.bluetoothGattServiceList = item;
        });


        myViewModel.getMacAddress().observe(getViewLifecycleOwner(), item -> {
            // Update the UI.
            this.macAddress = item;
        });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        triggerDisconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        if (stateDisposable != null) {
//            stateDisposable.dispose();
//        }
//
//        if (servicesSubscribe != null) {
//            servicesSubscribe.dispose();
//        }
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

    @OnClick(R.id.manual_btn)
    public void onManualButton() {

        if(bleDevice == null)
        bleDevice = rxBleClient.getBleDevice(this.macAddress);

        if (isConnected()) {
            triggerDisconnect();
        } else {
            // connect -> and send value, leave connected;
        /*
        Observing client state ---------------------------------------------------------------------
        Connection ---------------------------------------------------------------------
         */
            if (this.macAddress == null) {
                Toast.makeText(getContext(), "Brak polaczenia", Toast.LENGTH_SHORT).show();
                return;
            }

            if (this.bluetoothGattServiceList == null) {
                myViewModel.getBluetoothGattServiceList().observe(getViewLifecycleOwner(), item -> {
                    this.bluetoothGattServiceList = item;
                });
            }

            byte[] bytesToWrite = "MANUAL".getBytes();
            connectionDisposable = bleDevice.establishConnection(false)
                    .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(UUID.fromString(UUID_CHARACTERISTIC_WRITE), bytesToWrite))
                    .doFinally(() -> connectionDisposable.dispose())
                    .subscribe(
                            characteristicValue -> {
                                // Characteristic value confirmed.
                                DummyContent.DummyItem dummyItem = new DummyContent.DummyItem("1","CONTENT", "JEST OK");
                                DummyContent.addSingleItem(dummyItem);
                            },
                            throwable -> {
                                // Handle an error here.
                                Log.e("ERROR", "onManualButton() " + throwable.getStackTrace().toString());
                                DummyContent.DummyItem dummyItem = new DummyContent.DummyItem("1","CONTENT", "ERROR");
                                DummyContent.addSingleItem(dummyItem);
                            }
                    );

//            connectionDisposable = bleDevice.establishConnection(false)
//                    .subscribe(rxBleConnection -> {
//                                Log.i("USTANOWIONE POLACZENIE", "POLACZONO!");
////                                Single<RxBleDeviceServices> rxBleDeviceServicesSingle = rxBleConnection.discoverServices();
////                                rxBleDeviceServicesSingle.
////                        RxBleDeviceServices rxBleDeviceServices = rxBleConnection.discoverServices()
//                            },
//                            throwable -> {
//                                Log.e("ERROR", "onManualButton() " + throwable.toString());
//                            });


//            String UUID = "181C";
//            java.util.UUID uuid = new UUID();

//            String standardUUIDsParser = StandardUUIDsParser.getCharacteristicName();


//            bleDevice.establishConnection(false)
//                    .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(CHARACTERISTIC, bytesToWrite))
//                    .subscribe(
//                            characteristicValue -> {
//                                // Characteristic value confirmed.
//                            },
//                            throwable -> {
//                                // Handle an error here.
//                            }
//                    );

        }
//        device.establishConnection(false)
//                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(characteristicUUID, bytesToWrite))
//                .subscribe(
//                        characteristicValue -> {
//                            // Characteristic value confirmed.
//                        },
//                        throwable -> {
//                            // Handle an error here.
//                        }
//                );

    }

    @OnClick(R.id.forward_btn)
    public void onForwardButton() {

    }

    @OnClick(R.id.back_btn)
    public void onBackwardButton() {

    }

    @OnClick(R.id.turn_left_btn)
    public void onTurnLeftButton() {

    }

    @OnClick(R.id.turn_right_btn)
    public void onTurnRightButton() {

    }

    private boolean isConnected() {
        return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    private void triggerDisconnect() {

        if (connectionDisposable != null) {
            connectionDisposable.dispose();
        }
    }

//    public void write(byte[] bytes) {
//        try {
//            mmOutStream.write(bytes);
//            Message writtenMsg = handler.obtainMessage(MessageConstants.MESSAGE_WRITE, -1, -1, bytes);
//            writtenMsg.sendToTarget();
//        } catch (IOException e) {
//            Log.e(TAG, "Error occurred when sending data", e);
//            Send a failure message back to the activity.
//            Message writeErrorMsg = handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
//            Bundle bundle = new Bundle();
//            bundle.putString("toast", "Couldn't send data to the other device");
//            writeErrorMsg.setData(bundle);
//            handler.sendMessage(writeErrorMsg);
//        }
//    }

}