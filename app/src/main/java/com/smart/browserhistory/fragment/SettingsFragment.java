package com.smart.browserhistory.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.dropbox.core.android.Auth;
import com.smart.browserhistory.R;
import com.smart.browserhistory.adapter.SettingsArrayAdapter;
import com.smart.browserhistory.filebrowser.FileChooserActivity;
import com.smart.browserhistory.util.AppUtil;
import com.smart.browserhistory.vo.SettingsVO;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener {

    private List<SettingsVO> settingsList;
    private SettingsArrayAdapter adapter;
    private String dirPathStr;
    //    private DbxAccountManager mDbxAcctMgr;
    private static final int REQUEST_LINK_TO_DBX = 0;
    private static final int REQUEST_DIR_PATH = 1;
    private boolean dbSyncStatus;
    private boolean gdSyncStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, null);
        ListView listView = (ListView) rootView.findViewById(R.id.settingsListView);
//        getActivity().setTitle("Backup Settings");

        createAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return rootView;
    }

    private void createAdapter() {

        SharedPreferences settings = getActivity().getSharedPreferences(AppUtil.PHONE2CLOUD_PREF, Activity.MODE_PRIVATE);
        dirPathStr = settings.getString("backupDirPath", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        dbSyncStatus = settings.getBoolean("dbSyncStatus", false);

        settingsList = new ArrayList<>();
        SettingsVO settingsVO = new SettingsVO("Change Backup Location", dirPathStr, false);
        settingsList.add(settingsVO);

        settingsVO = new SettingsVO("Link Dropbox", "", dbSyncStatus);
        settingsList.add(settingsVO);

       /* settingsVO = new SettingsVO("Sync to Google Drive", "", dbSyncStatus);
        settingsList.add(settingsVO);*/

        adapter = new SettingsArrayAdapter(this, settingsList);

    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LINK_TO_DBX:
                if (resultCode == Activity.RESULT_OK) {

                    SettingsVO settingsVO = settingsList.get(1);
                    boolean status = mDbxAcctMgr.hasLinkedAccount();
                    if (status) {
                        settingsVO.setSubTitle("Status : Linked");
                    } else {
                        settingsVO.setSubTitle("Status : Not Linked");
                    }
                    settingsVO.setStatus(status);
                    SharedPreferences settings = getActivity().getSharedPreferences(AppUtil.PHONE2CLOUD_PREF, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("dbSyncStatus", status);
                    editor.apply();
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Failed to connect to DB", Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_DIR_PATH:
                if (resultCode == Activity.RESULT_OK) {
                    dirPathStr = data.getStringExtra("DirPath");

                    SharedPreferences settings = getActivity().getSharedPreferences(AppUtil.PHONE2CLOUD_PREF, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("backupDirPath", dirPathStr);
                    editor.apply();

                    SettingsVO settingsVO = settingsList.get(0);
                    settingsVO.setSubTitle(dirPathStr);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "New Backup Location : '" + dirPathStr + "'.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
        SettingsVO settingsVO = settingsList.get(1);
        /*boolean status = mDbxAcctMgr.hasLinkedAccount();
        if (status) {
            settingsVO.setSubTitle("Status : Linked");
        } else {
            settingsVO.setSubTitle("Status : Not Linked");
        }*/
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Intent intent1 = new Intent(getActivity(), FileChooserActivity.class);
                intent1.putExtra("dirPath", dirPathStr);
                startActivityForResult(intent1, REQUEST_DIR_PATH);
                break;
            case 1:
                // Dropbox sync
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences settings = getActivity().getSharedPreferences(AppUtil.PHONE2CLOUD_PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("dbSyncStatus", isChecked);
        editor.apply();

        SettingsVO settingsVO = settingsList.get(1);
        settingsVO.setStatus(isChecked);
        adapter.notifyDataSetChanged();

        SharedPreferences prefs = getActivity().getSharedPreferences(AppUtil.PHONE2CLOUD_PREF, Activity.MODE_PRIVATE);
        if (isChecked) {
            String accessToken = prefs.getString("access-token", null);
            if (accessToken == null) {
                accessToken = Auth.getOAuth2Token();
                if (accessToken != null) {
                    Log.e("DBX LINK STATUS:", accessToken);
                } else {
                    Auth.startOAuth2Authentication(getActivity(), AppUtil.DROPBOX_APP_KEY);
                }
            } else {
                Log.e("DBX AccessToken", accessToken);
            }
        } else {
            prefs.edit().putString("access-token", null).apply();
        }
    }
}

