package com.smart.browserhistory.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.smart.browserhistory.R;
import com.smart.browserhistory.adapter.MediaRecycleViewAdapter;
import com.smart.browserhistory.dropbox.DropboxClientFactory;
import com.smart.browserhistory.service.UploadMediaFileService;
import com.smart.browserhistory.util.AppUtil;
import com.smart.browserhistory.util.SpacesItemDecoration;
import com.smart.browserhistory.vo.SyncState;
import com.smart.browserhistory.vo.WhatsAppMediaVO;
import com.smart.browserhistory.vo.WrapperVO;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WhatsAppMediaFragment extends CustomFragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private List<WhatsAppMediaVO> whatsAppMediaVOList = new ArrayList<>();
    private MediaRecycleViewAdapter adapter;
    private LinearLayout progressBarLayout;
    private Handler handler = new Handler();
    private String mediaType;
    private Context context;
    private RecyclerView recycleView;
    private View permissionLayout;
    private View mainLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_whatsapp_video, null);
        mediaType = getArguments().getString("mediaType");
        Log.e("WhatsAppMediaFragment", "onCreateView called for " + mediaType);
        recycleView = (RecyclerView) rootView.findViewById(R.id.recycleView);

        permissionLayout = rootView.findViewById(R.id.permissionLayout);
        mainLayout = rootView.findViewById(R.id.mainLayout);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
        recycleView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        recycleView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        recycleView.setLayoutManager(layoutManager);

        progressBarLayout = (LinearLayout) rootView.findViewById(R.id.progressBarLayout);

        Button requestPermissionButton = (Button) rootView.findViewById(R.id.requestButton);
        requestPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRuntimePermission();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        checkRuntimePermission();
    }

    /**
     * This method is to check the app has required permissions to behave properly.
     */
    private void checkRuntimePermission() {

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Display a dialog to show the reason for requesting the persmission.
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Storage read permission is required to access videos and photos to upload to dropbox. Do you want to grant permission?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            permissionLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            // Condition checks if the permission already granted earlier.
            fetchMediaData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionLayout.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                    fetchMediaData();
                } else {
                    permissionLayout.setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.GONE);
                    Log.e("Request Grant", "Failed!!!!!!!!");
                }
        }
    }

    private void fetchMediaData() {
        switch (mediaType) {
            case "videos":
                String[] VIDEO_PROJECTION = {MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE};

                Uri VIDEO_SOURCE_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                File externalStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/WhatsApp/Media/WhatsApp Video");
                CursorLoader cursorLoader = new CursorLoader(context, VIDEO_SOURCE_URI, VIDEO_PROJECTION,
                        MediaStore.Video.Media.DATA + " like ? ", new String[]{"%" + externalStorageDir.getAbsolutePath() + "%"},
                        MediaStore.Video.Media.SIZE + " DESC");
                processMediaCursor(cursorLoader);
                break;

            case "images":
                Uri IMAGE_SOURCE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] IMAGE_PROJECTION = {MediaStore.Images.Media._ID, MediaStore.Images.Media.TITLE,
                        MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};
                File whatsAppImageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/WhatsApp/Media/WhatsApp Images");

                CursorLoader imageCursorLoader = new CursorLoader(context, IMAGE_SOURCE_URI, IMAGE_PROJECTION,
                        MediaStore.Images.Media.DATA + " like ? ", new String[]{"%" + whatsAppImageDir.getAbsolutePath() + "%"},
                        MediaStore.Images.Media.SIZE + " DESC");
                processMediaCursor(imageCursorLoader);
                break;

            case "voices":
                break;
        }
    }

    private void processMediaCursor(CursorLoader cursorLoader) {

        final Cursor cursor = cursorLoader.loadInBackground();
        while (cursor.moveToNext()) {
            WhatsAppMediaVO video = new WhatsAppMediaVO();
            video.name = cursor.getString(1);
            video.path = cursor.getString(2);
            video.size = cursor.getLong(3);

            whatsAppMediaVOList.add(video);
            if (mediaType.equals("videos")) {
                video.bitmap = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                        cursor.getLong(0), MediaStore.Video.Thumbnails.MICRO_KIND, null);
            } else if (mediaType.equals("images")) {
                video.bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
                        cursor.getLong(0), MediaStore.Images.Thumbnails.MICRO_KIND, null);
            }
        }
        cursor.close();
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBarLayout.setVisibility(View.GONE);
                recycleView.setVisibility(View.VISIBLE);
                adapter = new MediaRecycleViewAdapter(whatsAppMediaVOList, mediaType, context);
                recycleView.setAdapter(adapter);
            }
        });
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.setListData(whatsAppMediaVOList);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                checkDropboxSyncFiles();
                return null;
            }
        }.execute();
    }

    private void checkDropboxSyncFiles() {
        if (DropboxClientFactory.isDbxClientInitialised()) {
            try {
                ListFolderResult listFolderResult = DropboxClientFactory.getClient().files().listFolder(AppUtil.WHATSAPP_FOLDER_PATH);
                for (Metadata metadata : listFolderResult.getEntries()) {
                    for (WhatsAppMediaVO video : whatsAppMediaVOList) {
                        if (metadata instanceof FileMetadata) {
                            FileMetadata fileMetadata = (FileMetadata) metadata;
                            if (video.size == fileMetadata.getSize() && fileMetadata.getName().startsWith(video.name)) {
                                video.syncState = SyncState.SYNCED;
                                break;
                            }
                        }
                    }
                }
            } catch (DbxException e) {
                Log.e("Dbx Exception", e.toString());
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void handleCheckboxClick(boolean isChecked) {
        for (WhatsAppMediaVO vo : whatsAppMediaVOList) {
            vo.isSelected = isChecked;
        }
        adapter.setListData(whatsAppMediaVOList);
    }

    @Override
    public void handleFABClick() {

        boolean isDropboxLinked = AppUtil.isDropboxLinked(getContext());
        if (!isDropboxLinked) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Dropbox account not linked, Do you want to link to initiate backup?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Auth.startOAuth2Authentication(context, AppUtil.DROPBOX_APP_KEY);
                            Toast.makeText(context, "Link Dropbox account and trigger backup again.", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            List<WhatsAppMediaVO> selectedMediaList = new ArrayList<>();
            for (WhatsAppMediaVO vo : whatsAppMediaVOList) {
                if (vo.isSelected) {
                    selectedMediaList.add(vo);
                    vo.syncState = SyncState.SYNC_IN_PROGRESS;
                }
            }
            if (selectedMediaList.size() == 0) {
                Toast.makeText(context, "No files selected for backup.", Toast.LENGTH_LONG).show();
                return;
            } else {
                Toast.makeText(context, "Backup to Dropbox is in progress.", Toast.LENGTH_SHORT).show();
            }
            adapter.notifyDataSetChanged();
            Intent intent = new Intent(context.getApplicationContext(), UploadMediaFileService.class);
            WrapperVO wrapper = new WrapperVO();
            wrapper.setWhatsAppMediaVOList(selectedMediaList);
            intent.putExtra("wrapperVO", wrapper);
            intent.putExtra("customReceiver", new CustomResultReceiver(new Handler(), context));
            context.startService(intent);
        }
    }

    @Override
    public void share() {
        List<WhatsAppMediaVO> selectedVOs = new ArrayList<>();
        for (WhatsAppMediaVO vo : whatsAppMediaVOList) {
            if (vo.isSelected)
                selectedVOs.add(vo);
        }
        if (selectedVOs.size() == 0) {
            Toast.makeText(context, "No media selected to share", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<Uri> mediaUris = new ArrayList<Uri>();
        for (WhatsAppMediaVO vo : selectedVOs) {
            mediaUris.add(Uri.fromFile(new File(vo.path)));
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mediaUris);

        if (mediaType.equals("images")) {
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, "Share photos to.."));
        } else if (mediaType.equals("videos")) {
            shareIntent.setType("video/*");
            startActivity(Intent.createChooser(shareIntent, "Share videos to.."));
        }
    }

    @Override
    public void sort(final int by) {
        Collections.sort(whatsAppMediaVOList, new Comparator<WhatsAppMediaVO>() {
            @Override
            public int compare(WhatsAppMediaVO vo1, WhatsAppMediaVO vo2) {
                switch (by) {
                    case AppUtil.SORT_BY_SIZE:
                        return (int) (vo2.size - vo1.size);
                    case AppUtil.SORT_BY_UPDATE_STATUS:
                        return (vo1.syncState.ordinal() - vo2.syncState.ordinal());
                }
                return 0;
            }
        });
        adapter.setListData(whatsAppMediaVOList);
    }

    class CustomResultReceiver extends ResultReceiver {

        private final Context context;

        public CustomResultReceiver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData.getInt("status") == AppUtil.DROPBOX_NOT_LINKED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Dropbox is not liked. Do you want to link it?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Auth.startOAuth2Authentication(context, AppUtil.DROPBOX_APP_KEY);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (resultData.getInt("status") == AppUtil.DROPBOX_UPDATE_FAILURE) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(resultData.getString("msg"))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                adapter.replace(resultData.getInt("status"), (WhatsAppMediaVO) resultData.getSerializable("vo"));
            } else {
                adapter.replace(resultData.getInt("status"), (WhatsAppMediaVO) resultData.getSerializable("vo"));
            }
            super.onReceiveResult(resultCode, resultData);
        }
    }
}
