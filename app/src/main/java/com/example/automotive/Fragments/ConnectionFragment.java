package com.example.automotive.Fragments;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.exoplayer2.util.Log;
import com.google.android.material.snackbar.Snackbar;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.RxBleDeviceServices;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;
import com.polidea.rxandroidble2.utils.StandardUUIDsParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

/**
 * A fragment representing a list of Items.
 */
public class ConnectionFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private List<ScanResult> scanResultList;
    private RxBleClient rxBleClient;
    private MyViewModel myViewModel;
    private Disposable scanDisposable;
    private MyConnectionRecyclerViewAdapter myConnectionRecyclerViewAdapter;
    private Unbinder unbinder;
    @BindView(R.id.find_devices_btn)
    Button findDevicesButton;
    @BindView(R.id.ble_rv)
    RecyclerView recyclerView;
    private RxBleDevice bleDevice;
    private Disposable stateDisposable;
    private Disposable connectionDisposable;
    @BindView(R.id.ble_status_tv)
    TextView bluetoothStatusTextView;
    private Disposable servicesSubscribe;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConnectionFragment() {
    }

    public static ConnectionFragment newInstance(int columnCount) {
        ConnectionFragment fragment = new ConnectionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ble_control, container, false);
        scanResultList = new ArrayList<ScanResult>(0);
        // Set the adapter
//        ButterKnife.bind(view);
        unbinder = ButterKnife.bind(this, view);
//        RecyclerView recyclerView = view.findViewById(R.id.ble_rv);

//        if (view instanceof RecyclerView) {
        Context context = view.getContext();
//            RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        myConnectionRecyclerViewAdapter = new MyConnectionRecyclerViewAdapter(scanResultList);
        recyclerView.setAdapter(myConnectionRecyclerViewAdapter);
        myConnectionRecyclerViewAdapter.setClickListener((view1, position, scanResult) -> {
            onAdapterItemClick(view1, position, scanResult);
        });
//        }

        myViewModel = new ViewModelProvider(getActivity()).get(MyViewModel.class);

//        MyViewModel model = new ViewModelProvider(this).get(MyViewModel.class);
//        myViewModel.getRxBleClient().observe(getViewLifecycleOwner(), client -> {
//            this.rxBleClient = client;
//        });

        this.rxBleClient = SampleApplication.getRxBleClient(getContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        scanBleDevices();
// When done, just dispose.
//        scanSubscription.dispose();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.find_devices_btn)
    public void onScanToggleClick() {
        if (isScanning()) {
            scanDisposable.dispose();
        } else {
            scanBleDevices();
        }
        updateButtonUIState();
    }

    private void scanBleDevices() {
        scanDisposable = rxBleClient.scanBleDevices(
                new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // change if needed
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES) // change if needed
                        .build(),
                new ScanFilter.Builder()
                        .build()
                // add filters if needed
        )
                .doFinally(() -> dispose())
                .subscribe(
                        scanResult -> {
                            // Process scan result here.
                            myConnectionRecyclerViewAdapter.addScanResult(scanResult);
                            // Check MyConnectionRecyclerViewAdapter -> onBindViewHolder();
                        },
                        throwable -> {
                            // Handle an error here.
                            Log.e("BLE search error", throwable.getStackTrace().toString());
                        }
                );
    }

    private void dispose() {
        scanDisposable = null;
        myConnectionRecyclerViewAdapter.clearScanResults();
        updateButtonUIState();
    }

    private boolean isScanning() {
        return scanDisposable != null;
    }

    private void updateButtonUIState() {
        findDevicesButton.setText(isScanning() ? R.string.stop_scan : R.string.find_devices);
    }

    private void onAdapterItemClick(View view, int position, ScanResult scanResult) {
        final String macAddress = scanResult.getBleDevice().getMacAddress();
//        final Intent intent = new Intent(this, DeviceActivity.class);
//        intent.putExtra(DeviceActivity.EXTRA_MAC_ADDRESS, macAddress);
//        startActivity(intent);

//        scanResult.getBleDevice().getBluetoothDevice();

        bleDevice = rxBleClient.getBleDevice(macAddress);

        // share mac address of the device
        myViewModel.setMacAddress(macAddress);
        /*
        Observing client state ---------------------------------------------------------------------
         */
        stateDisposable = bleDevice.observeConnectionStateChanges()
                .subscribe(rxBleConnectionState -> {
//                    works every time until lost connection -> dispose()
                    onConnectionStateChange(rxBleConnectionState);
                });

        if (isConnected()) {
            triggerDisconnect();
        } else {
        /*
        Observing client state ---------------------------------------------------------------------
        Connection ---------------------------------------------------------------------
         */
            connectionDisposable = bleDevice.establishConnection(true)
                    .doFinally(this::dispose)
                    .subscribe(connection -> {
                                onConnectionReceived(connection);
                                servicesSubscribe = connection.discoverServices()
                                        .doFinally(() -> {
                                            ///////////////////////////////////////////
                                            servicesSubscribe.dispose();
                                        })
                                        .subscribe((rxBleDeviceServices, throwable) -> {
                                            List<BluetoothGattService> bluetoothGattServices = rxBleDeviceServices.getBluetoothGattServices();

                                            myViewModel.setBluetoothGattServiceListFromBackgroundThread(bluetoothGattServices);

//                                            for (int i = 0; i < bluetoothGattServices.size(); i++) {
////                                        int a = bluetoothGattServices.get(i).getType();
//
//                                                String jakasNazwa = StandardUUIDsParser.getServiceName(bluetoothGattServices.get(i).getUuid());
//                                                List<BluetoothGattCharacteristic> characteristics = bluetoothGattServices.get(i).getCharacteristics();
//
//                                                for (int j = 0; j < characteristics.size(); j++) {
//                                                    String drugaNazwa = StandardUUIDsParser.getCharacteristicName(characteristics.get(j).getUuid());
//
//                                                    List<BluetoothGattDescriptor> descriptors = characteristics.get(j).getDescriptors();
//
//                                                    for (int k = 0; k < descriptors.size(); k++) {
//
//                                                        String trzeciaNazwa = StandardUUIDsParser.getDescriptorName(descriptors.get(k).getUuid());
//                                                        Log.e("trzecia nazwa", trzeciaNazwa.toString());
//
//                                                    }
//
//                                                }
//                                            }

                                        });
                            },
                            throwable -> onConnectionFailure(throwable));
        }
    }

    private void onConnectionStateChange(RxBleConnection.RxBleConnectionState newState) {
//        Toast.makeText(getContext(), newState.toString(), Toast.LENGTH_SHORT).show();
//        connectionStateView.setText(newState.toString());
        bluetoothStatusTextView.setText(newState.toString());
    }

    private boolean isConnected() {
        return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    private void triggerDisconnect() {

        if (connectionDisposable != null) {
            connectionDisposable.dispose();
        }
    }

    private void onConnectionReceived(RxBleConnection connection) {
        //noinspection ConstantConditions
//        connection.
        Log.e("onConnectionFailure", "Connection received");
//        Snackbar.make(findViewById(android.R.id.content), "Connection received", Snackbar.LENGTH_SHORT).show();
//        Toast.makeText(getContext(), "Connection received", Toast.LENGTH_SHORT).show();
//        Toast.makeText(getContext(), "Connection received", Toast.LENGTH_SHORT).show();
    }

    private void onConnectionFailure(Throwable throwable) {
        Log.e("onConnectionFailure", throwable.getStackTrace().toString());
        //noinspection ConstantConditions
//        Snackbar.make(findViewById(android.R.id.content), "Connection error: " + throwable, Snackbar.LENGTH_SHORT).show();
//        Toast.makeText(getContext(), "Connection error: " + throwable, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPause() {
        super.onPause();
        triggerDisconnect();
//        mtuDisposable.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (stateDisposable != null) {
            stateDisposable.dispose();
        }

        if (servicesSubscribe != null) {
            servicesSubscribe.dispose();
        }
    }
}