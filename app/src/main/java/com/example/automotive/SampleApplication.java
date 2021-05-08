package com.example.automotive;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.polidea.rxandroidble2.LogConstants;
import com.polidea.rxandroidble2.LogOptions;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.exceptions.BleException;

import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;

public class SampleApplication extends Application {

    private RxBleClient rxBleClient;
    private Subscription connectionSubscription;
    Observable<RxBleConnection> connectionObservable;
    private String MacAddress;
//    private final Context context;
    private RxBleDevice bleDevice;

//    public SampleApplication(Context context) {
//        this.context = context;
//        SampleApplication application = (SampleApplication) context.getApplicationContext();
//        this.rxBleClient = application.rxBleClient;
//    }



    /**
     * In practice you will use some kind of dependency injection pattern.
     */
    public static RxBleClient getRxBleClient(Context context) {
        SampleApplication application = (SampleApplication) context.getApplicationContext();
        return application.rxBleClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rxBleClient = RxBleClient.create(this);
        RxBleClient.updateLogOptions(new LogOptions.Builder()
                .setLogLevel(LogConstants.INFO)
                .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
                .setUuidsLogSetting(LogConstants.UUIDS_FULL)
                .setShouldLogAttributeValues(true)
                .build()
        );
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable instanceof UndeliverableException && throwable.getCause() instanceof BleException) {
                Log.v("SampleApplication", "Suppressed UndeliverableException: " + throwable.toString());
                return; // ignore BleExceptions as they were surely delivered at least once
            }
            // add other custom handlers if needed
            throw new RuntimeException("Unexpected Throwable in RxJavaPlugins error handler", throwable);
        });
    }

    public RxBleClient getRxBleClient() {
        return rxBleClient;
    }

    public void setRxBleClient(RxBleClient rxBleClient) {
        this.rxBleClient = rxBleClient;
    }

    public Subscription getConnectionSubscription() {
        return connectionSubscription;
    }

    public void setConnectionSubscription(Subscription connectionSubscription) {
        this.connectionSubscription = connectionSubscription;
    }

    public Observable<RxBleConnection> getConnectionObservable() {
        return connectionObservable;
    }

    public void setConnectionObservable(Observable<RxBleConnection> connectionObservable) {
        this.connectionObservable = connectionObservable;
    }

    public String getMacAddress() {
        return MacAddress;
    }

    public void setMacAddress(String macAddress) {
        MacAddress = macAddress;
    }

//    public Context getContext() {
//        return context;
//    }

    public RxBleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(RxBleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }
}
