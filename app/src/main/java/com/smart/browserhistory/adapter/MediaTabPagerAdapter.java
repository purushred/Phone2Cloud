package com.smart.browserhistory.adapter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.smart.browserhistory.fragment.CustomFragment;
import com.smart.browserhistory.fragment.WhatsAppMediaFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by purushoy on 11/15/2016.
 */

public class MediaTabPagerAdapter extends FragmentPagerAdapter {

    private Map<Integer, CustomFragment> fragmentMap = new HashMap<>();

    public MediaTabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Return fragment with respect to Position .
     */

    @Override
    public CustomFragment getItem(int position) {
        CustomFragment fragment;
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                Log.e("MediaTabPagerAdapter", "Video Tab Fragment called.");
                if (fragmentMap.get(position) == null) {
                    fragment = new WhatsAppMediaFragment();
                    args.putString("mediaType", "videos");
                    fragment.setArguments(args);
                    fragmentMap.put(position, fragment);
                } else {
                    fragment = fragmentMap.get(position);
                }
                break;
            default:
                Log.e("MediaTabPagerAdapter", "Image Tab Fragment called.");
                if (fragmentMap.get(position) == null) {
                    fragment = new WhatsAppMediaFragment();
                    fragment.setArguments(args);
                    args.putString("mediaType", "images");
                    fragmentMap.put(position, fragment);
                } else {
                    fragment = fragmentMap.get(position);
                }
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    /**
     * This method returns the title of the tab according to the position.
     */

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "Videos";
            case 1:
                return "Photos";
        }
        return null;
    }
}