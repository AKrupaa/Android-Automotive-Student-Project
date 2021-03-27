package com.example.automotive.ViewModels;

import android.bluetooth.BluetoothGattService;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.polidea.rxandroidble2.RxBleClient;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class MyViewModel extends ViewModel {
    private MutableLiveData<RxBleClient> rxBleClient = new MutableLiveData<RxBleClient>();
    private MutableLiveData<Disposable>  scanDisposable = new MutableLiveData<Disposable>();
    private MutableLiveData<String>  macAddress = new MutableLiveData<String>();
    private MutableLiveData<List<BluetoothGattService>>  bluetoothGattServiceList = new MutableLiveData<List<BluetoothGattService>>();

    public MyViewModel() {

    }

    public void setRxBleClient(RxBleClient item) {
        rxBleClient.setValue(item);
    }

    public LiveData<RxBleClient> getRxBleClient() {
        return rxBleClient;
    }

    public void setScanDisposable(Disposable item) {
        scanDisposable.setValue(item);
    }

    public LiveData<Disposable> getScanDisposable() {
        return scanDisposable;
    }

    public MutableLiveData<String> getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress.setValue(macAddress);
    }

    public MutableLiveData<List<BluetoothGattService>> getBluetoothGattServiceList() {
        return bluetoothGattServiceList;
    }

    public void setBluetoothGattServiceList(List<BluetoothGattService> bluetoothGattServiceList) {
        this.bluetoothGattServiceList.setValue(bluetoothGattServiceList);
    }

    public void setBluetoothGattServiceListFromBackgroundThread(List<BluetoothGattService> bluetoothGattServiceList) {
        this.bluetoothGattServiceList.postValue(bluetoothGattServiceList);
    }
}