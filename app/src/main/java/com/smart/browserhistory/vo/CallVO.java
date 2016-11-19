package com.smart.browserhistory.vo;

import java.io.Serializable;

public class CallVO implements Serializable {
    public String address;
    public String date;
    public String dateStr;
    public int callType;
    public String duration;
    public String caller;

    @Override
    public String toString() {
        return address + caller;
    }
}
