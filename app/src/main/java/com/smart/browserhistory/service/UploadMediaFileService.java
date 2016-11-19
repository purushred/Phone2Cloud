package com.smart.browserhistory.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;
import com.smart.browserhistory.dropbox.DropboxClientFactory;
import com.smart.browserhistory.util.AppUtil;
import com.smart.browserhistory.vo.SyncState;
import com.smart.browserhistory.vo.WhatsAppMediaVO;
import com.smart.browserhistory.vo.WrapperVO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadMediaFileService extends IntentService {

    private Handler handler;
    private ResultReceiver receiver;
    private Intent intent;

    public UploadMediaFileService() {
        super("MediaBackupService");
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
        if (!AppUtil.isDropboxLinked(this)) {
            sendMessage("Dropbox is not linked. Please link it via Backup Settings", AppUtil.DROPBOX_NOT_LINKED);
            return;
        }
        uploadToDropbox();
    }

    private void sendPartialUpdate(final int status, final String msg, final WhatsAppMediaVO newVo) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putInt("status", status);
                bundle.putString("msg", msg);
                bundle.putSerializable("vo", newVo);
                receiver.send(0, bundle);
            }
        });
    }

    private void sendMessage(final String msg, final int status) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putString("message", msg);
                bundle.putInt("status", status);

                receiver.send(0, bundle);
            }
        });
    }

    /**
     * This method will upload the compressed zip file to Dropbox App/Browser History Manager folder.
     */

    public Map<String, String> uploadToDropbox() {

        Map<String, String> status = new HashMap<>();
        final DbxClientV2 client = DropboxClientFactory.getClient();
        WrapperVO wrapper = (WrapperVO) intent.getSerializableExtra("wrapperVO");
        final List<WhatsAppMediaVO> selectedVOList = wrapper.getWhatsAppMediaVOList();
        Metadata folderMetadata = null;
        try {

            ListFolderResult rootFolderList = client.files().listFolder(AppUtil.PHONE2CLOUD_FOLDER);
            for (Metadata metadata : rootFolderList.getEntries()) {
                if (metadata instanceof FolderMetadata) {
                    FolderMetadata fM = (FolderMetadata) metadata;
                    if (fM.getName().equals(AppUtil.WHATSAPP_FOLDER)) {
                        folderMetadata = fM;
                        break;
                    }
                }
            }
            if (folderMetadata == null)
                folderMetadata = client.files().createFolder(AppUtil.WHATSAPP_FOLDER_PATH);
            ListFolderResult listFolderResult = client.files().listFolder(AppUtil.WHATSAPP_FOLDER_PATH);
            List<Metadata> entries = listFolderResult.getEntries();

            for (final WhatsAppMediaVO mediaVO : selectedVOList) {

                boolean isFileExists = false;
                for (Metadata entry : entries) {
                    if (entry instanceof FileMetadata) {
                        FileMetadata fileMetadata = (FileMetadata) entry;
                        if (mediaVO.size == fileMetadata.getSize() && fileMetadata.getName().contains(mediaVO.name)) {
                            isFileExists = true;
                            mediaVO.syncState = SyncState.SYNCED;
                            sendPartialUpdate(AppUtil.DROPBOX_UPDATE_SUCCESS, "Partial Update", mediaVO);
                            break;
                        }
                    }
                }
                if (!isFileExists) {
                    File localFile = new File(mediaVO.path);
                    try {
                        Log.e("Uploading File : ", localFile.getName());
                        InputStream inputStream = new FileInputStream(localFile);
                        FileMetadata uploadedFileMetadata = client.files()
                                .uploadBuilder(folderMetadata.getPathDisplay() + "/" + localFile.getName())
                                .withMode(WriteMode.OVERWRITE)
                                .uploadAndFinish(inputStream);
                        mediaVO.syncState = SyncState.SYNCED;
                        sendPartialUpdate(AppUtil.DROPBOX_UPDATE_SUCCESS, "Partial Update", mediaVO);
                        Log.e("DropBox upload status:", uploadedFileMetadata.getName() + " uploaded.");
                    } catch (DbxException | IOException e) {
                        mediaVO.syncState = SyncState.SYNC_FAILED;
                        sendPartialUpdate(AppUtil.DROPBOX_UPDATE_FAILURE, "Partial Update", mediaVO);
                        Log.e("File Upload Exception:", e.toString());
                    }
                }
            }
        } catch (InvalidAccessTokenException e) {
            AppUtil.setAccessToken(null, this);
            sendMessage("Dropbox is not linked. Please link it via Backup Settings", AppUtil.DROPBOX_NOT_LINKED);
            Log.e("DropBox Exception", e.toString());
        } catch (Exception dbxException) {
            sendMessage("Upload to dropbox is failed.", AppUtil.DROPBOX_UPDATE_FAILURE);
            Log.e("DbxException", dbxException.toString());
        }
        return status;
    }
}