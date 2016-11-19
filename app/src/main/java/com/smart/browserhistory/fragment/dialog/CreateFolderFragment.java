package com.smart.browserhistory.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.smart.browserhistory.R;
import com.smart.browserhistory.filebrowser.FolderCreationListener;

/**
 * Created by Purushotham on 09-09-2014.
 */
public class CreateFolderFragment extends DialogFragment implements View.OnClickListener {

    private Button cancelButton;
    private Button createButton;
    private EditText folderNameView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_folder, null);
        getDialog().setTitle("Create New Folder");
        folderNameView = (EditText) rootView.findViewById(R.id.folderNameView);
        createButton = (Button) rootView.findViewById(R.id.createButton);
        cancelButton = (Button) rootView.findViewById(R.id.cancelButton);
        createButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == createButton.getId()) {
            FolderCreationListener listener = (FolderCreationListener) getActivity();
            listener.onFolderCreated(folderNameView.getText().toString());
            getDialog().dismiss();
        } else {
            getDialog().dismiss();
        }
    }
}
