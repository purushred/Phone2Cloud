package com.smart.browserhistory.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Purushotham on 10-09-2014.
 */
public class WrapperVO implements Serializable{
    private List<HistoryVO> historyVOList;
    private List<SMSVO> smsVOList;
    private List<CallVO> callVOList;
    private List<WhatsAppMediaVO> whatsAppMediaVOList;

    public WrapperVO() {
        historyVOList = new ArrayList<>();
        smsVOList = new ArrayList<>();
        callVOList = new ArrayList<>();
    }

    public List<HistoryVO> getHistoryVOList() {
        return historyVOList;
    }

    public void setHistoryVOList(List<HistoryVO> historyVOList) {
        this.historyVOList = historyVOList;
    }

    public void setWhatsAppMediaVOList(List<WhatsAppMediaVO> whatsAppMediaVOList) {
        this.whatsAppMediaVOList = whatsAppMediaVOList;
    }
    public List<WhatsAppMediaVO> getWhatsAppMediaVOList(){
        return this.whatsAppMediaVOList;
    }

    public List<SMSVO> getSmsVOList() {
        return smsVOList;
    }

    public void setSmsVOList(List<SMSVO> smsVOList) {
        this.smsVOList = smsVOList;
    }

    public List<CallVO> getCallVOList() {
        return callVOList;
    }

    public void setCallVOList(List<CallVO> callVOList) {
        this.callVOList = callVOList;
    }
}
