package com.baraa.firebase.whatsapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.baraa.firebase.whatsapp.fragments.CallsFragment;
import com.baraa.firebase.whatsapp.fragments.ChatFragment;
import com.baraa.firebase.whatsapp.fragments.StatusFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

//    private Context myContext;
    int totalTabs;

    public ViewPagerAdapter(FragmentManager fm, int totalTabs) {
        super(fm);
//        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatFragment();
            case 1:
                return new StatusFragment();

            case 2:
                return new CallsFragment();

            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}