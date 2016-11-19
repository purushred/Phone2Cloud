package com.smart.browserhistory.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.dropbox.core.v2.files.Metadata;

import java.util.List;

/**
 * Created by Purushotham on 21-11-2014.
 */
public class AppUtil {

    public static final String DROPBOX_APP_KEY = "gyr08vsflcod1de";
    public static final String PHONE2CLOUD_PREF = "Phone2CloudPref";

    public static final String MODULE_SMS_LIST = "sms_list";
    public static final String MODULE_CALL_LOG = "call_log";
    public static final String MODULE_WHATSAPP = "whatsapp_video";
    public static final String WHATSAPP_FOLDER_PATH = "/WhatsApp";
    public static final String WHATSAPP_FOLDER = "WhatsApp";
    public static final String PHONE2CLOUD_FOLDER = "";
    public static final int SORT_BY_SIZE = 0;
    public static final int SORT_BY_DATE = 1;
    public static final int SORT_BY_UPDATE_STATUS = 2;
    public static List<Metadata> dbxWhatsAppVideos;
    public static List<Metadata> dbxWhatsAppPhotos;

    public static final int DROPBOX_UPDATE_SUCCESS = 0;
    public static final int DROPBOX_UPDATE_FAILURE = 1;
    public static final int DROPBOX_NOT_LINKED = -1;

    public static boolean isDropboxLinked(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(AppUtil.PHONE2CLOUD_PREF, Activity.MODE_PRIVATE);
        String accessToken = prefs.getString("access-token", null);
        return accessToken != null;
    }

    public static String getAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(AppUtil.PHONE2CLOUD_PREF, Activity.MODE_PRIVATE);
        return prefs.getString("access-token", null);
    }

    public static void setAccessToken(String accessToken, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(AppUtil.PHONE2CLOUD_PREF, Activity.MODE_PRIVATE);
        prefs.edit().putString("access-token", accessToken).commit();
    }
}
