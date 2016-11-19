package com.smart.browserhistory.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart.browserhistory.R;
import com.smart.browserhistory.adapter.SummaryArrayAdapter;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class SummaryFragment extends Fragment {

    private static final String BROWSER_HISTORY_PREF = "BrowserHistoryPref";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary, null);
        getActivity().setTitle("Backup Summary");
        SharedPreferences settings = getActivity().getSharedPreferences(BROWSER_HISTORY_PREF, Activity.MODE_PRIVATE);
        String backupSummary = settings.getString("backupSummary", null);
        if (backupSummary != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Map<String, String>>>() {
            }.getType();
            List<Map<String, String>> summaryVOList = gson.fromJson(backupSummary, type);
            SummaryArrayAdapter adapter = new SummaryArrayAdapter(this, summaryVOList);

            ListView listView = (ListView) rootView.findViewById(R.id.summaryList);
            listView.setAdapter(adapter);

            TextView noSummaryView = (TextView) rootView.findViewById(R.id.noSummaryText);
            noSummaryView.setVisibility(View.GONE);
        } else {
            TextView noSummaryView = (TextView) rootView.findViewById(R.id.noSummaryText);
            noSummaryView.setVisibility(View.VISIBLE);
        }
        return rootView;
    }
}

