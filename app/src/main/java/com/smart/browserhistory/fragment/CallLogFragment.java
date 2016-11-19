package com.smart.browserhistory.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.smart.browserhistory.MainActivity;
import com.smart.browserhistory.R;
import com.smart.browserhistory.adapter.CallLogArrayAdapter;
import com.smart.browserhistory.fragment.dialog.CallDetailsFragment;
import com.smart.browserhistory.service.UploadBackupFileService;
import com.smart.browserhistory.util.AppUtil;
import com.smart.browserhistory.util.CustomResultReceiver;
import com.smart.browserhistory.vo.CallVO;
import com.smart.browserhistory.vo.WrapperVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CallLogFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final int MY_PERMISSIONS_CALL_LOG = 3;
    private List<CallVO> callVOList = new ArrayList<>();
    private ListView listView;
    private CallLogArrayAdapter adapter;
    private LinearLayout progressBarLayout;
    private EditText searchView;
    private ProgressBar backupProgress;
    private Button downloadButton;
    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_call_log, null);
        listView = (ListView) rootView.findViewById(R.id.callLogListView);
        AdView adView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setVisibility(View.GONE);
        backupProgress = (ProgressBar) rootView.findViewById(R.id.backupProgress);
        downloadButton = (Button) rootView.findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(this);
        progressBarLayout = (LinearLayout) rootView.findViewById(R.id.progressBarLayout);
        searchView = (EditText) rootView.findViewById(R.id.callLogSearchView);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (adapter != null && charSequence != null)
                    adapter.filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkRuntimePermission();
        listView.setOnItemClickListener(this);
    }

    private void checkRuntimePermission() {
        // This if condition checks if the permission already granted earlier.
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {

            // Display a dialog to show the reason for requesting the persmission.
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CALL_LOG)) {

            } else {

                // No explanation needed, we can request the permission.

                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG},
                        MY_PERMISSIONS_CALL_LOG);
            }
        } else {
            getData();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_CALL_LOG:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getData();
                } else {
                    Log.e("Request Grant", "Failed!!!!!!!!");
                }
        }
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCallDetails();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressBarLayout.setVisibility(View.GONE);
                        searchView.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        adapter = new CallLogArrayAdapter(getActivity(), callVOList);
                        listView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    /**
     * This method will read the call logs and return a list of objects.
     *
     * @return
     */
    private void getCallDetails() {

        ContentResolver cr = getActivity().getContentResolver();
        Cursor managedCursor = cr.query(CallLog.Calls.CONTENT_URI, null,
                null, null, CallLog.Calls.DATE + " DESC");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int cachedNameIndex = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String cachedName = managedCursor.getString(cachedNameIndex);

            CallVO callVO = new CallVO();
            callVO.address = phNumber;
            callVO.date = format.format(callDayTime);
            long timeNow = System.currentTimeMillis();
            callVO.dateStr = DateUtils.getRelativeTimeSpanString(callDayTime.getTime(), timeNow,
                    DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
            callVO.callType = Integer.parseInt(callType);
            if (cachedName == null)
                callVO.caller = phNumber;
            else
                callVO.caller = cachedName;
            callVO.duration = callDuration;
            callVOList.add(callVO);
        }
        managedCursor.close();
    }

    /**
     * This method will handle the item click event on history list view. On selecting an item
     * will display the details dialog.
     *
     * @param adapterView
     * @param view
     * @param position
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        FragmentManager manager = getActivity().getSupportFragmentManager();

        Fragment prevFragment = getActivity().getSupportFragmentManager().findFragmentByTag("Dialog");
        FragmentTransaction ft = manager.beginTransaction();
        if (prevFragment != null) {
            ft.remove(prevFragment);
        }
        CallDetailsFragment fragment = new CallDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("callVO", callVOList.get(position));
        fragment.setArguments(bundle);
        fragment.show(ft, "Dialog");
    }

    @Override
    public void onClick(View v) {
        searchView.setText("");
//        backupProgress.setVisibility(View.VISIBLE);
//        downloadButton.setVisibility(View.GONE);
        Intent intent = new Intent(getActivity().getApplicationContext(), UploadBackupFileService.class);
        WrapperVO wrapper = new WrapperVO();
        wrapper.setCallVOList(callVOList);
        intent.putExtra("backupLocation", ((MainActivity) getActivity()).getBackupLocation());
        intent.putExtra("moduleName", AppUtil.MODULE_CALL_LOG);
        intent.putExtra("wrapperVO", wrapper);
        intent.putExtra("customReceiver", new CustomResultReceiver(new Handler(), getActivity()));
        getActivity().startService(intent);
    }
}
