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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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
import com.example.automotive.dummy.DiscoveryItem;
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
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * A fragment representing a list of Items.
 */
public class ConnectionFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private List<ScanResult> scanResultList;
    private MyViewModel myViewModel;
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
    private RxBleClient rxBleClient;
    private Disposable scanDisposable;
    private boolean hasClickedScan;
    private final CompositeDisposable servicesDisposable = new CompositeDisposable();

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

        this.rxBleClient = SampleApplication.getRxBleClient(view.getContext());
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
                        .build())
                .doFinally(this::dispose)
                .subscribe(
                        scanResult -> {
                            // Process scan result here.
                            myConnectionRecyclerViewAdapter.addScanResult(scanResult);
                        },
                        throwable -> {
                            // Handle an error here.
                            android.util.Log.e("BLE search error", Arrays.toString(throwable.getStackTrace()));
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

//        bleDevice = rxBleClient.getBleDevice(macAddress);

        // share mac address of the device
        myViewModel.setMacAddress(macAddress);
        MutableLiveData<String> macAddress1 = myViewModel.getMacAddress();
        Toast.makeText(getContext(), macAddress1.getValue(), Toast.LENGTH_SHORT).show();

        scanDisposable.dispose();
        myConnectionRecyclerViewAdapter.clearScanResults();

        bleDevice = rxBleClient.getBleDevice(macAddress);

        if(isConnected()) {
            scanDisposable.dispose();
        }

        final Disposable disposable = bleDevice.establishConnection(false)
                .flatMapSingle(RxBleConnection::discoverServices)
                .take(1) // Disconnect automatically after discovery
//                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::updateUI)
                .subscribe(rxBleDeviceServices -> {
//                            myViewModel.setBluetoothGattServiceList(bluetoothGattServices);
                            final List<DiscoveryItem> discoveryItemList = new ArrayList<>(0);
                            for (BluetoothGattService service : rxBleDeviceServices.getBluetoothGattServices()) {
                                // Add service
//                                myViewModel.se
//                                data.add(new AdapterItem(AdapterItem.SERVICE, getServiceType(service), service.getUuid()));
                                final List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                                discoveryItemList.add(new DiscoveryItem(DiscoveryItem.SERVICE, getServiceType(service), service.getUuid()));
                                for (BluetoothGattCharacteristic characteristic : characteristics) {
                                    discoveryItemList.add(new DiscoveryItem(DiscoveryItem.CHARACTERISTIC, describeProperties(characteristic), characteristic.getUuid()));
                                }
                            }
                            myViewModel.setDiscoveryItemListPost(discoveryItemList);
                            System.out.println(discoveryItemList.toString());
                        },
                        this::onConnectionFailure);
        servicesDisposable.add(disposable);
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

    private void updateUI() {
//        Toast.makeText(getContext(), "Discovered services", Toast.LENGTH_SHORT).show();
        Log.i("Discovered services", "Discovered services!");
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
//        Log.e("onConnectionFailure", throwable.getStackTrace());
        throwable.printStackTrace();
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
        servicesDisposable.clear();
        servicesDisposable.dispose();
        //noinspection ConstantConditions
//        Snackbar.make(findViewById(android.R.id.content), "Connection error: " + throwable, Snackbar.LENGTH_SHORT).show();
//        Toast.makeText(getContext(), "Connection error: " + throwable, Toast.LENGTH_SHORT).show();
    }

    private String getServiceType(BluetoothGattService service) {
        return service.getType() == BluetoothGattService.SERVICE_TYPE_PRIMARY ? "primary" : "secondary";
    }

    private boolean isCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
    }

    private boolean isCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0);
    }

    private boolean isCharacteristicWriteable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE
                | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != 0;
    }

    private String describeProperties(BluetoothGattCharacteristic characteristic) {
        List<String> properties = new ArrayList<>();
        if (isCharacteristicReadable(characteristic)) properties.add("Read");
        if (isCharacteristicWriteable(characteristic)) properties.add("Write");
        if (isCharacteristicNotifiable(characteristic)) properties.add("Notify");
        return TextUtils.join(" ", properties);
    }

    @Override
    public void onPause() {
        super.onPause();
        triggerDisconnect();
        servicesDisposable.clear();
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