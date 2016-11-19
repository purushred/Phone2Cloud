package com.smart.browserhistory.adapter;

/**
 * Created by purushoy on 11/16/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smart.browserhistory.ImageViewerActivity;
import com.smart.browserhistory.R;
import com.smart.browserhistory.util.AppUtil;
import com.smart.browserhistory.vo.SyncState;
import com.smart.browserhistory.vo.WhatsAppMediaVO;

import java.text.DecimalFormat;
import java.util.List;

public class MediaRecycleViewAdapter extends RecyclerView.Adapter<MediaRecycleViewAdapter.ViewHolder> {

    private final String mediaType;
    private final Context context;
    private List<WhatsAppMediaVO> mediaVOList;

    public MediaRecycleViewAdapter(List<WhatsAppMediaVO> mediaVOList, String mediaType, Context context) {
        this.mediaVOList = mediaVOList;
        this.mediaType = mediaType;
        this.context = context;
    }

    public void add(int position, WhatsAppMediaVO item) {
        mediaVOList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(WhatsAppMediaVO item) {
        int position = mediaVOList.indexOf(item);
        mediaVOList.remove(position);
        notifyItemRemoved(position);
    }

    public void replace(int status, WhatsAppMediaVO newVO) {
        if (newVO != null) {
            for (WhatsAppMediaVO vo : mediaVOList) {
                if (vo.size == newVO.size && vo.name.equals(newVO.name)) {
                    int index = mediaVOList.indexOf(vo);
                    if (status == AppUtil.DROPBOX_UPDATE_SUCCESS) {
                        vo.syncState = SyncState.SYNCED;
                    } else {
                        vo.syncState = SyncState.NOT_SYNCED;
                    }
                    vo.isSelected = false;
                    notifyItemChanged(index);
                    return;
                }
            }
        }
    }


    public void setListData(List<WhatsAppMediaVO> list) {
        this.mediaVOList = list;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MediaRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_video_list_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final WhatsAppMediaVO whatsAppMediaVO = mediaVOList.get(position);
        holder.whatsAppVideoThumbView.setImageBitmap(whatsAppMediaVO.bitmap);
        holder.whatsAppVideoThumbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(whatsAppMediaVO.path));
                switch (mediaType) {
                    case "videos":
                        intent.setDataAndType(Uri.parse(whatsAppMediaVO.path), "video/*");
                        context.startActivity(intent);

                        break;
                    case "images":
                        intent = new Intent(context, ImageViewerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("imageURL", whatsAppMediaVO.path);
                        context.startActivity(intent);
                        break;
                    case "voices":
                        break;
                }
            }
        });
        holder.videoSizeView.setText(getStringSizeLengthFile(whatsAppMediaVO.size));
        holder.checkBox.setChecked(whatsAppMediaVO.isSelected);
        switch (whatsAppMediaVO.syncState) {

            case SYNC_IN_PROGRESS:
                holder.cloudImageView.setVisibility(View.GONE);
                holder.checkBox.setChecked(false);
                holder.checkBox.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
                break;
            case SYNCED:
                holder.cloudImageView.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(false);
                holder.progressBar.setVisibility(View.GONE);
                holder.checkBox.setVisibility(View.GONE);
                break;
            case SYNC_FAILED:
//                break;
            default:
                holder.progressBar.setVisibility(View.GONE);
                holder.cloudImageView.setVisibility(View.GONE);
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox cb = (CheckBox) view;
                        whatsAppMediaVO.isSelected = cb.isChecked();
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mediaVOList.size();
    }

    private String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMo = sizeKb * sizeKb;
        float sizeGo = sizeMo * sizeKb;
        float sizeTerra = sizeGo * sizeKb;

        if (size < sizeMo)
            return df.format(size / sizeKb) + " KB";
        else if (size < sizeGo)
            return df.format(size / sizeMo) + " MB";
        else if (size < sizeTerra)
            return df.format(size / sizeGo) + " GB";

        return "";
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView whatsAppVideoThumbView;
        TextView videoSizeView;
        ImageView cloudImageView;
        ProgressBar progressBar;
        CheckBox checkBox;

        ViewHolder(View rowView) {
            super(rowView);
            videoSizeView = (TextView) rowView.findViewById(R.id.videoSizeView);
            cloudImageView = (ImageView) rowView.findViewById(R.id.cloudImageView);
            whatsAppVideoThumbView = (ImageView) rowView.findViewById(R.id.whatsAppVideoThumbnail);
            progressBar = (ProgressBar) rowView.findViewById(R.id.progressBar);
            checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
        }
    }
}
