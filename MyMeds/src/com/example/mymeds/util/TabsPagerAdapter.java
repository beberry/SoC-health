package com.example.mymeds.util;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mymeds.fragments.FirstFragment;
import com.example.mymeds.fragments.SecondFragment;
import com.example.mymeds.fragments.ThirdFragment;
 
public class TabsPagerAdapter extends FragmentPagerAdapter {
 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Top Rated fragment activity
            return new FirstFragment();
        case 1:
            // Games fragment activity
            return new SecondFragment();
        case 2:
            // Movies fragment activity
            return new ThirdFragment();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
 
}