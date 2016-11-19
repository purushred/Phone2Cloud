package com.smart.browserhistory.vo;

import android.graphics.Bitmap;

import java.io.Serializable;

public class WhatsAppMediaVO implements Serializable {
    public String name;
    public String path;
    public long size;
    public boolean isSelected;
    transient public Bitmap bitmap;
    public SyncState syncState = SyncState.NOT_SYNCED;

    @Override
    public String toString() {
        return name;
    }
}


