package com.example.automotive.Fragments;

import android.content.Context;
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

import com.example.automotive.Adapters.MyConnectionRecyclerViewAdapter;
import com.example.automotive.R;
import com.example.automotive.SampleApplication;
import com.example.automotive.ViewModels.MyViewModel;
import com.example.automotive.dummy.DummyContent;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * A fragment representing a list of Items.
 */
public class ConnectionFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private List<ScanResult> scanResultList;
    private RxBleClient rxBleClient;
    private MyViewModel myViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConnectionFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
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
        View view = inflater.inflate(R.layout.fragment_connection_list, container, false);
        scanResultList = new ArrayList<ScanResult>(0);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyConnectionRecyclerViewAdapter(scanResultList));
        }

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

        Disposable scanSubscription = rxBleClient.scanBleDevices(
                new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // change if needed
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES) // change if needed
                        .build()
                // add filters if needed
        )
                .subscribe(
                        scanResult -> {
                            // Process scan result here.
                            scanResultList.add(scanResult);
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                );

// When done, just dispose.
//        scanSubscription.dispose();
    }
}