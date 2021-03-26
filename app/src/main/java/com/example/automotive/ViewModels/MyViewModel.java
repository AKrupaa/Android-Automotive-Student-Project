package com.example.automotive.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.polidea.rxandroidble2.RxBleClient;

import io.reactivex.disposables.Disposable;

public class MyViewModel extends ViewModel {
    private MutableLiveData<RxBleClient> rxBleClient = new MutableLiveData<RxBleClient>();
    private MutableLiveData<Disposable>  scanDisposable = new MutableLiveData<Disposable>();

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
}