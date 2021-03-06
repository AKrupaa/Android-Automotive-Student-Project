package com.example.automotive;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.automotive.Adapters.ViewPager2FragmentStateAdapter;
import com.example.automotive.ViewModels.MyViewModel;
//import com.example.automotive.ViewModels.SharedViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;

import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String MAC_ADDRESS = "EA:A5:34:E6:28:2E";
    ViewPager2 viewPager2;
    ViewPager2FragmentStateAdapter fragmentStateAdapter;
    TabLayout tabLayout;
//    RxBleDevice device = rxBleClient.getBleDevice(macAddress);
//    SampleApplication sampleApplication = new SampleApplication(getApplicationContext());


    //   --------------------------------------------------- przechowywanie info przy ROTACJI ---------------------------------------------------
//      https://stackoverflow.com/questions/151777/how-to-save-an-activity-state-using-save-instance-state?page=1&tab=votes#tab-top
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
//        outState.putString("stringContainer", stringContainer);
    }

//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        stringContainer = savedInstanceState.getString("stringContainer");
//        setStringContainer(stringContainer);
//        textView_input = replaceTheContentOfTextView(textView_input, stringContainer);
//    }
//      https://stackoverflow.com/questions/151777/how-to-save-an-activity-state-using-save-instance-state?page=1&tab=votes#tab-top
//    --------------------------------------------------- przechowywanie info przy ROTACJI ---------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MyViewModel model = new ViewModelProvider(this).get(MyViewModel.class);

        //        Turn on Bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        int REQUEST_ENABLE_BT = 1;
        this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        create instance in ViewModel
//        rxBleClient = RxBleClient.create(getApplicationContext());
//        model.setRxBleClient(rxBleClient);
//        rxBleClient = SampleApplication.getRxBleClient(this);

        ButterKnife.bind(this);

        if (savedInstanceState == null) {
//            nic nie zosta??o zapisane -> patrz onSaveInstanceState()
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
//                nic nie zosta??o przekazane pomi??dzy aktywno??ciami
//                finish(); // ciekawe jak to dzia??a xd KURWA, nie uzywac tego w zadnym jebanym wypadku
            } else {
//                pierwsze wywo??anie danej aktywno??ci z innej aktywno??ci
//                przej??cie pomi??dzy activity, tj. activity 1 zapisa??o co?? do extras i teraz mo??na co?? zgarna?? :)
//                np. extras.getLong(SOMETHING);
            }
        } else {
//            czyli ponowne wywo??anie aplikacji
//            co?? jest w ??rodku np. -> savedInstanceState.getLong(SOMETHING);
        }

//        FRAGMENT ADAPTER
        fragmentStateAdapter = new ViewPager2FragmentStateAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2 = findViewById(R.id.pager);
        viewPager2.setUserInputEnabled(false);
        viewPager2.setAdapter(fragmentStateAdapter);

        // TOP BAR
        final String tabTitles[] = {"Debugging", "Bluetooth", "Video"};
        final @DrawableRes int tabDrawable[] = {R.drawable.ic_bug_report_24px, R.drawable.ic_bluetooth_24px, R.drawable.ic_videocam_24px};
        tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText(tabTitles[position]).setIcon(tabDrawable[position])
        ).attach();


        // Use this check to determine whether SampleApplication is supported on the device. Then
        // you can selectively disable SampleApplication-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        } else {
//            good to know ;)
            Toast.makeText(this, R.string.ble_is_supported, Toast.LENGTH_SHORT).show();
        }


//        rxBleClient.getBleDevice(MAC_ADDRESS).getBluetoothDevice().createBond();
//        rxBleClient.getBleDevice(MAC_ADDRESS).establishConnection(false);

////        https://developer.android.com/guide/topics/connectivity/use-ble
//        // Initializes Bluetooth adapter.
//        final BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
//        BluetoothAdapter bluetoothAdapter = null;
//        if (bluetoothManager != null) {
//            bluetoothAdapter = bluetoothManager.getAdapter();
//        }
//
//
//        int REQUEST_ENABLE_BT = 0;
//        // Ensures Bluetooth is available on the device and it is enabled. If not,
//    // displays a dialog requesting user permission to enable Bluetooth.
//        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }

//        rxBleClient = SampleApplication.getRxBleClient(this);

//        MyApplicationContext
//        rxBleClient = RxBleClient.create(this.getApplicationContext());


    }

    //    lifecycle
    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }

    //    lifecycle
    @Override
    protected void onResume() {
        super.onResume();
    }

    //    lifecycle
    @Override
    protected void onPause() {
        super.onPause();
    }

    //    lifecycle
    @Override
    protected void onStop() {
        super.onStop();
    }

    //    lifecycle
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    //    lifecycle
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        } else {
            checkPermission();
        }
    }
}