package com.smart.browserhistory.vo;

/**
 * Created by Purushotham on 20-11-2014.
 */
public class SettingsVO {

    private String title;
    private String subTitle;
    private boolean status;

    public SettingsVO(String title,String subTitle,boolean status){
        this.title = title;
        this.subTitle = subTitle;
        this.status = status;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}