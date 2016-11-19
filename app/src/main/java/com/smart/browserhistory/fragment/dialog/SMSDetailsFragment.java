package com.smart.browserhistory.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.smart.browserhistory.R;
import com.smart.browserhistory.vo.SMSVO;

import java.util.Date;

/**
 * Created by Purushotham on 09-09-2014.
 */
public class SMSDetailsFragment extends DialogFragment implements View.OnClickListener {

    private Button closeButton;
    private TextView smsBodyView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sms_details, null);
        SMSVO smsVO = (SMSVO) getArguments().getSerializable("smsVO");
        getDialog().setTitle("From: " + smsVO.address);
        smsBodyView = (TextView) rootView.findViewById(R.id.smsDateView);
        Date date = new Date(smsVO.date);
        smsBodyView.setText("Date: " + date);

        smsBodyView = (TextView) rootView.findViewById(R.id.smsBodyView);
        smsBodyView.setText("Message: " + smsVO.body);
        closeButton = (Button) rootView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        getDialog().dismiss();
    }
}