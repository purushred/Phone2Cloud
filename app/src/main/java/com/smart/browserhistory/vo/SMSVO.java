package com.smart.browserhistory.vo;

import java.io.Serializable;

public class SMSVO implements Serializable {
    public String address;
    public String body;
    public long date;
    public String dateStr;

    @Override
    public String toString() {
        return address + body;
    }
}
