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
import com.example.automotive.EngineValuesPWM;
import com.example.automotive.HexString;
import com.example.automotive.R;
import com.example.automotive.RxJavaBLE.RxJavaBLE;
import com.example.automotive.SampleApplication;
import com.example.automotive.ViewModels.MyViewModel;
import com.example.automotive.dummy.DummyContent;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rx3.ReplayingShare;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.RxBleDeviceServices;
import com.polidea.rxandroidble2.internal.util.UUIDUtil;
import com.polidea.rxandroidble2.internal.util.UUIDUtil_Factory;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.utils.StandardUUIDsParser;

import io.github.controlwear.virtual.joystick.android.JoystickView;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.disposables.CompositeDisposable;

import static java.lang.Math.cos;
import static java.lang.Math.sin;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {
    private static final String MAC_ADDRESS = "EA:A5:34:E6:28:2E";
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
    // Importing also other views
//    @BindView(R.id.joystick)
    JoystickView joystick;

//    @BindView(R.id.manual_btn)
//    Button manualButton;

    @BindView(R.id.manualButton)
    Button manualButton;

    @BindView(R.id.connectButton)
    Button connectButton;

    @BindView(R.id.statusTextView)
    TextView statusTextView;

    MyViewModel myViewModel;
    private String macAddress;

    private RxBleClient rxBleClient;
//    private RxBleDevice bleDevice;

    private Observable<RxBleConnection> connectionObservable;
    private PublishSubject<Boolean> disconnectTriggerSubject = PublishSubject.create();
    private Disposable connectionDisposable;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private UUID uuid = UUID.fromString("00001235-0000-1000-8000-00805f9b34fb");
    List<BluetoothGattService> bluetoothGattServiceList;
    RxJavaBLE rxJavaBLE;
    Disposable subscribe;
    RxBleDevice rxBleDevice;
    String BASE_UUID = "00000000-0000-1000-8000-00805F9B34FB";

//    private Observable<RxBleConnection> prepareConnectionObservable() {
//        return bleDevice
//                .establishConnection(false)
//                .takeUntil(disconnectTriggerSubject)
//                .compose(ReplayingShare.instance());
//    }

    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VideoFragment.
     */
    public static VideoFragment newInstance() {
        VideoFragment fragment = new VideoFragment();
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
//        rxJavaBLE = new RxJavaBLE(rxBleClient);

//        rxJavaBLE.setMAC(MAC_ADDRESS);

//        connectionDisposable = rxJavaBLE.prepareConnectionObservable().subscribe(rxBleConnection -> {
//            Log.i("connected", "connected");
//        });


//        rxBleConnectionObservable = rxJavaBLE.prepareConnectionObservable();


//        this.rxBleClient = SampleApplication.getRxBleClient(getActivity());

//        this.rxBleClient.getBleDevice()

//        this.rxBleClient = SampleApplication.getRxBleClient(getContext());
//        this.rxBleClient =
//        Set<RxBleDevice> value = this.rxBleClient.getBondedDevices();
        joystick = (JoystickView) view.findViewById(R.id.joystick);
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


//        RxJavaBLE rxJavaBLE = new RxJavaBLE(bleDevice, MAC_ADDRESS);
//        rxBleConnectionObservable = rxJavaBLE.prepareConnectionObservable();

        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                double nJoyX = strength * cos(angle * Math.PI / 180);
                double nJoyY = strength * sin(angle * Math.PI / 180);
                Log.i("RUCH:", String.valueOf(nJoyX) + "____" + String.valueOf(nJoyY));

                EngineValuesPWM engineValuesPWM = new EngineValuesPWM(nJoyX, nJoyY);
                engineValuesPWM.calulcate();

                double engineLeft = engineValuesPWM.getnMotMixL();
                double engineRight = engineValuesPWM.getnMotMixR();

                Log.i("Engine left with sign:", Integer.toString((int) engineLeft));
                Log.i("Engine right with sign", Integer.toString((int) engineRight));

                boolean negative_l = engineLeft < 0;
                boolean negative_r = engineRight < 0;

                int engineLeft_i;
                int engineRight_i;

                if (negative_l) {
                    engineLeft_i = (int) -engineLeft;
                } else {
                    engineLeft_i = (int) engineLeft;
                }

                if (negative_r) {
                    engineRight_i = (int) -engineRight;
                } else {
                    engineRight_i = (int) engineRight;
                }

                Log.i("Engine left:", Integer.toString(engineLeft_i));
                Log.i("Engine right", Integer.toString(engineRight_i));

                if (negative_l) {
                    engineLeft_i += 128;
                }
                if (negative_r) {
                    engineRight_i += 128;
                }

                String l = Integer.toHexString(engineLeft_i);

                if (l.length() == 1) {
                    l = "0" + l;
                }

                String r = Integer.toHexString(engineRight_i);
                if (r.length() == 1) {
                    r = "0" + r;
                }
                String movement = "0x03";
                String data = movement + l + r;

                Log.i("movement", data);

                if (rxBleDevice != null) {
//                    if (isConnected()) {
                        final Disposable disposable = connectionObservable
                                .firstOrError()
                                .flatMap(rxBleConnection -> rxBleConnection.writeCharacteristic(uuid, HexString.hexToBytes(data)))
                                .subscribe(
                                        bytes -> onWriteSuccess(bytes),
                                        throwable -> onWriteFailure(throwable)
                                );
                        compositeDisposable.add(disposable);
//                    }
                }
            }
        }, 300);

//        initializePlayer();
    }


    @OnClick(R.id.connectButton)
    void onConnectButton() {

        if (rxBleDevice == null) {
            if (myViewModel.getMacAddress().getValue() != null) {
                if (!myViewModel.getMacAddress().getValue().isEmpty()) {

                    // get BLE device
                    rxBleDevice = SampleApplication.getRxBleClient(this.getActivity())
                            .getBleDevice(myViewModel.getMacAddress().getValue());

                    // establish connection
                    connectionObservable = rxBleDevice.establishConnection(false)
                            .takeUntil(disconnectTriggerSubject);

                    statusTextView.setText(R.string.connected);
                }

            }
        } else {
            triggerDisconnect();
            statusTextView.setText(R.string.disconnected);
        }

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


    private boolean isConnected() {
        return rxBleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    private void triggerDisconnect() {
        disconnectTriggerSubject.onNext(true);
        if (connectionDisposable != null) {
            connectionDisposable.dispose();
        }
    }

    private void onConnectionFailure(Throwable throwable) {
        //noinspection ConstantConditions
        Toast.makeText(getContext(), "Connection error: " + throwable, Toast.LENGTH_LONG).show();
        Log.i(getClass().getSimpleName(), "Connection error: ", throwable);
//        updateUI(null);
    }

    private void onWriteFailure(Throwable throwable) {
        //noinspection ConstantConditions
//        Snackbar.make(findViewById(R.id.main), "Write error: " + throwable, Snackbar.LENGTH_SHORT).show();
        Log.i(getClass().getSimpleName(), "Write error: ", throwable);
    }

    private void onWriteSuccess(byte[] bytes) {
        //noinspection ConstantConditions
//        Snackbar.make(findViewById(R.id.main), "Write success", Snackbar.LENGTH_SHORT).show();
        Log.i(getClass().getSimpleName(), "Wrote: " + bytes);
    }
}