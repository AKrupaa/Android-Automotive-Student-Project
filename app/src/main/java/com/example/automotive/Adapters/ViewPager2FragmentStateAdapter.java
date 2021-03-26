package com.example.automotive.Adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.automotive.Fragments.ConnectionFragment;
import com.example.automotive.Fragments.DebuggingFragment;
import com.example.automotive.Fragments.VideoFragment;

public class ViewPager2FragmentStateAdapter extends FragmentStateAdapter {
    public ViewPager2FragmentStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Fragment fragment = null;
        Bundle args = null;
        switch (position) {
            case 0:
                fragment = DebuggingFragment.newInstance(1);
//                args = new Bundle();
//                args.putInt(VideoFragment.ARG_OBJECT, position + 1);
//                fragment.setArguments(args);
                break;
            case 1:
//                fragment = BluetoothFragment.newInstance();
                fragment = ConnectionFragment.newInstance(1);
                args = new Bundle();
                args.putInt(VideoFragment.ARG_OBJECT, position + 1);
                fragment.setArguments(args);
                break;
            case 2:
                fragment = VideoFragment.newInstance();
                args = new Bundle();
                args.putInt(VideoFragment.ARG_OBJECT, position + 1);
                fragment.setArguments(args);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + position);
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
