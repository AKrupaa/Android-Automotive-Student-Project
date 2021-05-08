package com.example.automotive.RxJavaBLE;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.ParcelUuid;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.automotive.ViewModels.MyViewModel;
import com.google.android.exoplayer2.util.Log;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.RxBleDeviceServices;
import com.polidea.rxandroidble2.utils.ConnectionSharingAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
//import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class RxJavaBLE {
    private RxBleDevice rxBleDevice;
    private RxBleClient rxBleClient;
    // for dealing with scan (RxJava) RxBleClient
    private Disposable scanDisposable;
    private Disposable connectionDisposable;
    private String MAC;
    MyViewModel myViewModel;

    private UUID characteristicUuid;
    private PublishSubject<Boolean> disconnectTriggerSubject = PublishSubject.create();
    private Observable<RxBleConnection> connectionObservable;
    private RxBleDevice bleDevice;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public RxJavaBLE(RxBleClient rxBleClient) {
        this.rxBleClient = rxBleClient;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;

        // Set the associated device on MacAddress change
        rxBleDevice = rxBleClient.getBleDevice(this.MAC);
    }

    public Observable<RxBleConnection> prepareConnectionObservable() {
        return rxBleDevice
                .establishConnection(false);
//                .takeUntil(disconnectTriggerSubject)
//                .share();
    }
//    public void discoverServices(RxBleConnection rxBleConnection) {
//
//    }


    public void sendData(char[] buffer) {

//        rxBleDevice.

//        rxBleDevice.establishConnection(false)
//                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(characteristicUUID, bytesToWrite))
//                .subscribe(
//                        characteristicValue -> {
//                            // Characteristic value confirmed.
//                            System.out.println("Wyslano");
//                        },
//                        throwable -> {
//                            // Handle an error here.
//                            System.out.println(throwable.fillInStackTrace());
//                        }
//                ).dispose();

        rxBleDevice.establishConnection(false)
                .flatMapSingle(rxBleConnection -> rxBleConnection.readCharacteristic(UUID.fromString("0x2A00")))
                .subscribe(
                        characteristicValue -> {
                            // Read characteristic value.
                            System.out.println(characteristicValue);
                        },
                        throwable -> {
                            // Handle an error here.
                            System.out.println(throwable);
                        }
                )
                .dispose();

    }

    protected void updateUIscan() {
        Log.i("state", isConnected() ? "disconnect" : "connect");
//        connectBT.setText(isConnected() ? getString(R.string.disconnect) :
//                "Connect");
//        con_state.setText(isConnected() ? "Connected" : "Disconected");
//        readBT.setEnabled(isConnected());
//        writeBT.setEnabled(isConnected());
//        notifyBT.setEnabled(isConnected());
    }

    public boolean isConnected() {
        if (rxBleDevice != null) {
            return rxBleDevice.getConnectionState() ==
                    RxBleConnection.RxBleConnectionState.CONNECTED;
        } else {
            return false;
        }
    }

    private void dispose() {
        scanDisposable = null;
//        myConnectionRecyclerViewAdapter.clearScanResults();
//        updateButtonUIState();
    }

    private void triggerDisconnect() {

        if (connectionDisposable != null) {
            connectionDisposable.dispose();
        }
    }

//    private void triggerDisconnect() {
//        disconnectTriggerSubject.onNext(null);
//    }

    private void onConnectionFailure(Throwable throwable) {
        //noinspection ConstantConditions
        Log.i("onConnectionFailure", Arrays.toString(throwable.getStackTrace()));
//        Toast.makeText(, "Connection error: " + throwable, Toast.LENGTH_SHORT).show();
    }
//    public void sendBuffer(char[] buffer, int size) {
//    }
}
