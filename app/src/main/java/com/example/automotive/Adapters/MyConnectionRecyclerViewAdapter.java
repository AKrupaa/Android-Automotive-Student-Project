package com.example.automotive.Adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.automotive.R;
import com.example.automotive.dummy.DummyContent.DummyItem;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.ScanResult;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ScanResult}.
 */
public class MyConnectionRecyclerViewAdapter extends RecyclerView.Adapter<MyConnectionRecyclerViewAdapter.ViewHolder> {

    private final List<ScanResult> scanResultList;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyConnectionRecyclerViewAdapter(List<ScanResult> items) {
        scanResultList = items;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_row_ble, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextViews in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ScanResult scanResult = scanResultList.get(position);
        final RxBleDevice bleDevice = scanResult.getBleDevice();
        holder.line1.setText(String.format(Locale.getDefault(), "%s (%s)", bleDevice.getMacAddress(), bleDevice.getName()));
        holder.line2.setText(String.format(Locale.getDefault(), "RSSI: %d", scanResult.getRssi()));
//        holder.line2.setText(scanResult.getBleDevice().getMacAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(v, position, scanResult);
                }
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return scanResultList.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView line1;
        public final TextView line2;
//        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            line1 = (TextView) view.findViewById(R.id.line1_tv);
            line2 = (TextView) view.findViewById(R.id.line2_tv);
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "mView=" + mView +
                    ", line1=" + line1 +
                    ", line2=" + line2 +
                    '}';
        }
    }


    private static final Comparator<ScanResult> SORTING_COMPARATOR = (lhs, rhs) ->
            lhs.getBleDevice().getMacAddress().compareTo(rhs.getBleDevice().getMacAddress());

    public void addScanResult(ScanResult bleScanResult) {
        // Not the best way to ensure distinct devices, just for sake on the demo.

        for (int i = 0; i < scanResultList.size(); i++) {

            if (scanResultList.get(i).getBleDevice().equals(bleScanResult.getBleDevice())) {
                scanResultList.set(i, bleScanResult);
                notifyItemChanged(i);
                return;
            }
        }

        scanResultList.add(bleScanResult);
        Collections.sort(scanResultList, SORTING_COMPARATOR);
        notifyDataSetChanged();
    }

    public void clearScanResults() {
        scanResultList.clear();
        notifyDataSetChanged();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, ScanResult scanResult);
    }
}