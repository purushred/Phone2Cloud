package com.smart.browserhistory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smart.browserhistory.R;
import com.smart.browserhistory.vo.SMSVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SMSArrayAdapter extends ArrayAdapter<SMSVO> {

    private final List<SMSVO> smsList;
    private final List<SMSVO> arrayList;
    private final Context context;
    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

    public SMSArrayAdapter(Context context, List<SMSVO> smsList) {
        super(context, R.layout.history_list_row, smsList);
        this.context = context;
        this.smsList = smsList;
        this.arrayList = new ArrayList<>();
        arrayList.addAll(smsList);
    }

    public void filter(String searchStr) {
        searchStr = searchStr.toLowerCase(Locale.getDefault());
        smsList.clear();
        if (searchStr.length() == 0) {
            smsList.addAll(arrayList);
        } else {
            for (SMSVO smsVO : arrayList) {
                if (smsVO.toString().toLowerCase(Locale.getDefault()).contains(searchStr)) {
                    smsList.add(smsVO);
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
        SMSVO smsVO = smsList.get(position);
        TextView titleView = (TextView) rowView.findViewById(R.id.titleView);
        TextView lastVisitedView = (TextView) rowView.findViewById(R.id.lastVisitedView);
        titleView.setText(smsVO.address);
        lastVisitedView.setText(smsVO.dateStr);

        return rowView;
    }
}