package com.smart.browserhistory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smart.browserhistory.R;
import com.smart.browserhistory.vo.HistoryVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryArrayAdapter extends ArrayAdapter<HistoryVO> {

    private final List<HistoryVO> historyList;
    private final List<HistoryVO> arrayList;
    private final Context context;

    public HistoryArrayAdapter(Context context, List<HistoryVO> historyList) {
        super(context, R.layout.history_list_row, historyList);
        this.context = context;
        this.historyList = historyList;
        this.arrayList = new ArrayList<HistoryVO>();
        arrayList.addAll(historyList);
    }

    public void filter(String searchStr) {
        searchStr = searchStr.toLowerCase(Locale.getDefault());
        historyList.clear();
        if (searchStr.length() == 0) {
            historyList.addAll(arrayList);
        } else {
            for (HistoryVO historyVO : arrayList) {
                if (historyVO.toString().toLowerCase(Locale.getDefault()).contains(searchStr)) {
                    historyList.add(historyVO);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.history_list_row, parent, false);
        } else {
            rowView = convertView;
        }
        HistoryVO historyVO = historyList.get(position);
        TextView titleView = (TextView) rowView.findViewById(R.id.titleView);
        // TextView urlView = (TextView) rowView.findViewById(R.id.urlView);
        TextView lastVisitedView = (TextView) rowView.findViewById(R.id.lastVisitedView);
        //TextView visitsView = (TextView) rowView.findViewById(R.id.visitsView);
        if (historyVO.getTitle() != null && historyVO.getTitle().length() > 0) {
            titleView.setText(historyVO.getTitle());
        } else {
            titleView.setText(historyVO.getUrl());

        }
        lastVisitedView.setText(historyVO.getDateStr());

        return rowView;
    }
}
