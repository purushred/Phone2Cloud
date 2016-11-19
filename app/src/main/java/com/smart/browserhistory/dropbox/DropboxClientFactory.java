package com.smart.browserhistory.dropbox;

import android.util.Log;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;

/**
 * Singleton instance of {@link DbxClientV2}
 */
public class DropboxClientFactory {

    private static DbxClientV2 sDbxClient;

    public static void init(final String accessToken) {

        long time = System.currentTimeMillis();
        if (sDbxClient == null) {
            DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("phone2cloud")
                    .withHttpRequestor(OkHttp3Requestor.INSTANCE)
                    .build();
            sDbxClient = new DbxClientV2(requestConfig, accessToken);
        }
        Log.e("Dbx Client Init ", "Time: " + (System.currentTimeMillis() - time));
    }


    public static DbxClientV2 getClient() {
        if (sDbxClient == null) {
            throw new IllegalStateException("Client not initialized.");
        }
        return sDbxClient;
    }

    public static boolean isDbxClientInitialised() {
        return sDbxClient != null;
    }
}
