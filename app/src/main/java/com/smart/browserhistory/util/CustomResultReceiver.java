package com.smart.browserhistory.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by purushoy on 11/15/2016.
 */

public class CustomResultReceiver extends ResultReceiver {

    private final Context context;

    public CustomResultReceiver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(resultData.getString("message"))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        super.onReceiveResult(resultCode, resultData);
    }
}
