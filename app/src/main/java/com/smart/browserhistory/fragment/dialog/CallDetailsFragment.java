package com.smart.browserhistory.fragment.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.smart.browserhistory.R;
import com.smart.browserhistory.vo.CallVO;

/**
 * Created by Purushotham on 09-09-2014.
 */
public class CallDetailsFragment extends DialogFragment implements View.OnClickListener {

    private Button closeButton;
    private Button callButton;
    private CallVO callVO;
    private TextView calledDateView;
    private TextView callTypeText;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_call_details, null);
        callVO = (CallVO) getArguments().getSerializable("callVO");
        getDialog().setTitle("From: " + callVO.caller);
        calledDateView = (TextView) rootView.findViewById(R.id.calledDate);
        callTypeText = (TextView) rootView.findViewById(R.id.callTypeText);
        calledDateView.setText("Date: " + callVO.date);
        if (CallLog.Calls.OUTGOING_TYPE == callVO.callType) {
            callTypeText.setText("Type: Outgoing Call");
        } else if (CallLog.Calls.INCOMING_TYPE == callVO.callType) {
            callTypeText.setText("Type: Incoming Call");
        } else {
            callTypeText.setText("Type: Missed Call");
        }

        closeButton = (Button) rootView.findViewById(R.id.closeButton);
        closeButton = (Button) rootView.findViewById(R.id.closeButton);
        callButton = (Button) rootView.findViewById(R.id.callButton);
        closeButton.setOnClickListener(this);
        callButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == closeButton.getId()) {
            getDialog().dismiss();
        } else {
            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
            phoneIntent.setData(Uri.parse("tel:" + callVO.address));
            startActivity(phoneIntent);
        }
    }
}