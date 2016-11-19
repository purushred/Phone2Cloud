package com.smart.browserhistory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.smart.browserhistory.R;
import com.smart.browserhistory.fragment.SettingsFragment;
import com.smart.browserhistory.vo.SettingsVO;

import java.util.List;

public class SettingsArrayAdapter extends ArrayAdapter<SettingsVO> {

    private final List<SettingsVO> arrayList;
    private final Context context;
    private final SettingsFragment fragment;

    public SettingsArrayAdapter(SettingsFragment fragment, List<SettingsVO> historyList) {
        super(fragment.getActivity().getApplicationContext(), R.layout.history_list_row, historyList);
        this.fragment = fragment;
        this.context = fragment.getActivity().getApplicationContext();

        this.arrayList = historyList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (viewType == 0) {
                rowView = inflater.inflate(R.layout.settings_list_item_backup_path, parent, false);
            } else {
                rowView = inflater.inflate(R.layout.settings_list_item_sync_dropbox, parent, false);
            }
        }

        SettingsVO settingsVO = arrayList.get(position);
        TextView titleView = (TextView) rowView.findViewById(R.id.titleView);
        TextView subTitleView = (TextView) rowView.findViewById(R.id.subTitleView);

        titleView.setText(settingsVO.getTitle());
        subTitleView.setText(settingsVO.getSubTitle());

        if (viewType == 1 || viewType == 2) {

            if (settingsVO.getSubTitle().contains("Status : Linked")) {
                subTitleView.setTextColor(context.getResources().getColor(R.color.blue_color));
            } else {
                subTitleView.setTextColor(context.getResources().getColor(R.color.orange_color));
            }

            CheckBox statusCheckView = (CheckBox) rowView.findViewById(R.id.statusCheckView);
            statusCheckView.setChecked(settingsVO.isStatus());
            statusCheckView.setOnCheckedChangeListener(fragment);
        }
        return rowView;
    }
}
