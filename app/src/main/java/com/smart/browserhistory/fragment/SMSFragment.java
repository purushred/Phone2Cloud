package com.smart.browserhistory.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
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
import com.smart.browserhistory.adapter.SMSArrayAdapter;
import com.smart.browserhistory.fragment.dialog.SMSDetailsFragment;
import com.smart.browserhistory.service.UploadBackupFileService;
import com.smart.browserhistory.util.AppUtil;
import com.smart.browserhistory.util.CustomResultReceiver;
import com.smart.browserhistory.vo.SMSVO;
import com.smart.browserhistory.vo.WrapperVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SMSFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String SMS_INBOX_URI = "content://sms/inbox";
    private static final int MY_PERMISSIONS_MULTIPLE_REQUESTS = 2;

    private List<SMSVO> smsVOList = new ArrayList<>();
    private ListView listView;
    private SMSArrayAdapter adapter;
    private LinearLayout progressBarLayout;
    private EditText searchView;
    private ProgressBar backupProgress;
    private Handler handler = new Handler();
    private Button uploadButton;
    private Map<String, String> phoneNameMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sms, null);
        listView = (ListView) rootView.findViewById(R.id.smsListView);
        AdView adView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setVisibility(View.GONE);

        backupProgress = (ProgressBar) rootView.findViewById(R.id.backupProgress);
        uploadButton = (Button) rootView.findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(this);
        progressBarLayout = (LinearLayout) rootView.findViewById(R.id.progressBarLayout);
        searchView = (EditText) rootView.findViewById(R.id.smsSearchView);
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

    private void checkRuntimePermission() {
        // This if condition checks if the permission already granted earlier.
        if ((ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)) {

            // Display a dialog to show the reason for requesting the persmission.
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_SMS)) {

            } else {

                // No explanation needed, we can request the permission.

                requestPermissions(
                        new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_MULTIPLE_REQUESTS);
            }
        } else {
            getData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_MULTIPLE_REQUESTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getData();
                } else {
                    Log.e("Request Grant", "Failed!!!!!!!!");
                }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkRuntimePermission();
        listView.setOnItemClickListener(this);
    }

    /**
     * This method will read the sms and return a list of objects.
     *
     * @return
     */
    public void readSMS() {

        Uri inboxURI = Uri.parse(SMS_INBOX_URI);
        String[] reqCols = new String[]{"_id", "address", "body", "date"};
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(inboxURI, reqCols, null, null, "date DESC");

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            SMSVO smsVO;
            int addressIndex = cursor.getColumnIndex("address");
            int bodyIndex = cursor.getColumnIndex("body");
            int dateIndex = cursor.getColumnIndex("date");
            readAllContacts();
            while (!cursor.isAfterLast()) {
                smsVO = new SMSVO();
                smsVO.address = cursor.getString(addressIndex);
                smsVO.body = cursor.getString(bodyIndex);
                smsVO.date = cursor.getLong(dateIndex);
                long timeNow = System.currentTimeMillis();

                String dateStr = DateUtils.getRelativeTimeSpanString(smsVO.date, timeNow,
                        DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
                smsVO.dateStr = dateStr;
                String contactName = phoneNameMap.get(smsVO.address);//getContactName(smsVO.address);
                if (contactName != null)
                    smsVO.address = contactName;
                smsVOList.add(smsVO);
                cursor.moveToNext();
            }
        }
        if (cursor != null)
            cursor.close();
    }

    private void readAllContacts() {
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones != null && phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNameMap.put(phoneNumber, name);
        }
        if (phones != null)
            phones.close();
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
        SMSDetailsFragment fragment = new SMSDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("smsVO", smsVOList.get(position));
        fragment.setArguments(bundle);
        fragment.show(ft, "Dialog");
    }

    @Override
    public void onClick(View v) {
        searchView.setText("");
//        backupProgress.setVisibility(View.VISIBLE);
//        uploadButton.setVisibility(View.GONE);
        Intent intent = new Intent(getActivity().getApplicationContext(), UploadBackupFileService.class);
        WrapperVO wrapper = new WrapperVO();
        wrapper.setSmsVOList(smsVOList);
        intent.putExtra("backupLocation", ((MainActivity) getActivity()).getBackupLocation());
        intent.putExtra("moduleName", AppUtil.MODULE_SMS_LIST);
        intent.putExtra("wrapperVO", wrapper);
        intent.putExtra("customReceiver", new CustomResultReceiver(new Handler(), getActivity()));
        getActivity().startService(intent);
    }

    /**
     * Method to read the sms information on successful permission grant.
     */
    private void getData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                readSMS();
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        progressBarLayout.setVisibility(View.GONE);
                        searchView.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        adapter = new SMSArrayAdapter(getActivity(), smsVOList);
                        listView.setAdapter(adapter);
                    }
                });
            }
        }).start();
        Log.e("Request Granted", "Successful!!!!!!!!");

    }
}