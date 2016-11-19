package com.smart.browserhistory.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by purushoy on 11/18/2016.
 */

public abstract class CustomFragment extends Fragment {

    public abstract void handleCheckboxClick(boolean isChecked);

    public abstract void handleFABClick();
    public abstract void sort(int by);
    public abstract void share();
}
