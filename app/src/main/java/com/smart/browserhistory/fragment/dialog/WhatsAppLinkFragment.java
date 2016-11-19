package com.smart.browserhistory.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dropbox.core.android.Auth;
import com.smart.browserhistory.R;
import com.smart.browserhistory.util.AppUtil;

/**
 * Created by Purushotham on 09-09-2014.
 */
public class WhatsAppLinkFragment extends DialogFragment implements View.OnClickListener {

    private Button cancelButton;
    private Button createButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_whatsapp_link, null);
        getDialog().setTitle("WhatsApp Linking");
        createButton = (Button) rootView.findViewById(R.id.createButton);
        cancelButton = (Button) rootView.findViewById(R.id.cancelButton);
        createButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == createButton.getId()) {
            Auth.startOAuth2Authentication(getActivity(), AppUtil.DROPBOX_APP_KEY);
            getDialog().dismiss();
        } else {
            getDialog().dismiss();
        }
    }
}
