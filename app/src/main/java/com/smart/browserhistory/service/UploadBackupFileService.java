package com.smart.browserhistory.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.smart.browserhistory.util.AppUtil;
import com.smart.browserhistory.vo.WrapperVO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class UploadBackupFileService extends IntentService {

    private Handler handler;
    private ResultReceiver receiver;
    private Intent intent;
    private File jsonFile;
    private String moduleName;

    public UploadBackupFileService() {
        super("BackupService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        receiver = intent.getParcelableExtra("customReceiver");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;

        moduleName = intent.getStringExtra("moduleName");
        formatFileName();
        cleanDirectory();
        boolean dbSyncStatus = AppUtil.isDropboxLinked(this);
        if (writeToLocalStorage()) {

            List<Map<String, String>> list = new ArrayList<>();
            Map<String, String> summaryMap = new HashMap<>();

            summaryMap.put("title", "Backup Summary");
            summaryMap.put("subTitle", "");
            list.add(summaryMap);

            summaryMap = new HashMap<>();
            summaryMap.put("title", "Backup File Location");
            summaryMap.put("subTitle", jsonFile.getAbsolutePath() + ".gz");
            list.add(summaryMap);

            SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.getDefault());
            String lastBackupDateStr = format.format(new Date());

            summaryMap = new HashMap<>();
            summaryMap.put("title", "Backup Taken On");
            summaryMap.put("subTitle", lastBackupDateStr);
            list.add(summaryMap);

            summaryMap = new HashMap<>();
            summaryMap.put("title", "Backup File Size");
            summaryMap.put("subTitle", new File(jsonFile.getAbsolutePath() + ".gz").length() + " Bytes");
            list.add(summaryMap);

            summaryMap = new HashMap<>();
            summaryMap.put("title", "Dropbox Sync Info");
            summaryMap.put("subTitle", "");
            list.add(summaryMap);

            if (dbSyncStatus) {
//                uploadToDropbox();

                summaryMap = new HashMap<>();
                String lastSyncDateStr = format.format(new Date());
                summaryMap.put("title", "Dropbox Synched On");
                summaryMap.put("subTitle", lastSyncDateStr);
                list.add(summaryMap);

                summaryMap = new HashMap<>();
                summaryMap.put("title", "Files Synched");
                summaryMap.put("subTitle", jsonFile.getName() + ".gz");
                list.add(summaryMap);
            } else {

                summaryMap = new HashMap<>();
                summaryMap.put("title", "Dropbox Synched On");
                summaryMap.put("subTitle", "NA");
                list.add(summaryMap);

                summaryMap = new HashMap<>();
                summaryMap.put("title", "Files Synched");
                summaryMap.put("subTitle", "NA");
                list.add(summaryMap);
            }

            Gson gson = new Gson();
            String jsonStr = gson.toJson(list);
            SharedPreferences settings = getSharedPreferences(AppUtil.PHONE2CLOUD_PREF, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("backupSummary", jsonStr);
            editor.apply();
        }
    }

    private void formatFileName() {
        String backupLocationStr = intent.getStringExtra("backupLocation");

        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        String fileNameStr = format.format(new Date());
        jsonFile = new File(backupLocationStr, moduleName + "_" + fileNameStr + ".json");
    }

    private void cleanDirectory() {
        File[] files = jsonFile.getParentFile().listFiles();
        for (File file : files) {
            if (file.getName().startsWith(moduleName) &&
                    (file.getName().endsWith(".json") || file.getName().endsWith(".gz")))
                file.delete();
        }
    }

    /**
     * This method will write the data to json file and also compress it to gz file.
     *
     * @return
     */
    String msg = "";
    boolean flag = false;

    private boolean writeToLocalStorage() {

        boolean isWrittenToLocalFile = writeToJsonFile();
        if (isWrittenToLocalFile) {
            boolean isCompressed = compressJsonFile();
            if (isCompressed) {
                jsonFile.delete();
                msg = "Backup completed. See summary for more information.";
                flag = true;
            } else {
                msg = "Failed to zip backup file in local storage.";
                flag = false;
            }
        } else {
            msg = "Failed to write backup file to local storage.";
            flag = false;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putString("message", msg);
                receiver.send(0, bundle);
            }
        });
        return flag;
    }

    private String convertHistoryToJSON() {
        WrapperVO wrapper = (WrapperVO) intent.getSerializableExtra("wrapperVO");
        Gson gson = new Gson();
        switch (moduleName) {
            case AppUtil.MODULE_CALL_LOG:
                return gson.toJson(wrapper.getCallVOList());
            case AppUtil.MODULE_SMS_LIST:
                return gson.toJson(wrapper.getSmsVOList());
        }
        return "";
    }

    /**
     * This method will write the data to json file.
     *
     * @return
     */
    private boolean writeToJsonFile() {
        try {

            FileWriter writer = new FileWriter(jsonFile);
            BufferedWriter br = new BufferedWriter(writer);
            br.write(convertHistoryToJSON());
            br.close();
            return true;
        } catch (IOException ex) {
            Log.e("WriteTOFile", ex.getMessage());
            return false;
        }
    }

    /**
     * This method will compress the json file to gz file.
     *
     * @return
     */
    private boolean compressJsonFile() {
        try {
            FileInputStream fis = new FileInputStream(jsonFile);

            FileOutputStream fos = new FileOutputStream(jsonFile.getAbsolutePath() + ".gz");
            GZIPOutputStream gos = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                gos.write(buffer, 0, len);
            }
            gos.flush();
            gos.close();
            fos.close();
            fis.close();
            return true;
        } catch (IOException ex) {
            Log.e("Compression Exception", ex.getMessage());
        }
        return false;
    }

    /**
     * This method will upload the compressed zip file to Dropbox App/Browser History Manager folder.
     */
    /*public void uploadToDropbox() {

        DbxAccountManager mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
                AppUtil.DROPBOX_APP_KEY, AppUtil.DROPBOX_APP_SECRET);
        try {

            DbxPath dropboxFilePath = new DbxPath(DbxPath.ROOT, jsonFile.getName() + ".gz");
            DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
            DbxFile dropboxFile = dbxFs.create(dropboxFilePath);
            dropboxFile.writeFromExistingFile(new File(jsonFile.getAbsolutePath() + ".gz"), false);
            dropboxFile.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Dropbox upload failed.", Toast.LENGTH_LONG).show();
            Log.e("DropBox Exception", e.toString());
        }
    }*/
}