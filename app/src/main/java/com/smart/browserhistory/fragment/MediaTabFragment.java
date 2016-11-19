package com.smart.browserhistory.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smart.browserhistory.R;
import com.smart.browserhistory.adapter.MediaTabPagerAdapter;

/**
 * Created by purushoy on 11/15/2016.
 */

public class MediaTabFragment extends CustomFragment implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private MediaTabPagerAdapter mediaTabPagerAdapter;
    private CustomFragment selectedFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_layout, null);
        final TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mediaTabPagerAdapter = new MediaTabPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(mediaTabPagerAdapter);
        tabLayout.addOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(this);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return rootView;
    }

    public void handleCheckboxClick(boolean isChecked) {
        selectedFragment.handleCheckboxClick(isChecked);
    }

    @Override
    public void handleFABClick() {
        selectedFragment.handleFABClick();
    }

    @Override
    public void share() {
        selectedFragment.share();
    }

    @Override
    public void sort(int by) {
        selectedFragment.sort(by);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        selectedFragment = mediaTabPagerAdapter.getItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectedFragment = mediaTabPagerAdapter.getItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}