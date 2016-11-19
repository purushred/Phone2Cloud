package com.smart.browserhistory.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smart.browserhistory.R;
import com.smart.browserhistory.fragment.SummaryFragment;

import java.util.List;
import java.util.Map;

public class SummaryArrayAdapter extends ArrayAdapter<Map<String, String>> {

    private final List<Map<String, String>> arrayList;
    private final Context context;
    private final SummaryFragment fragment;

    public SummaryArrayAdapter(SummaryFragment fragment, List<Map<String, String>> summaryList) {
        super(fragment.getActivity().getApplicationContext(), R.layout.summary_list_item, summaryList);
        this.fragment = fragment;
        this.context = fragment.getActivity().getApplicationContext();
        this.arrayList = summaryList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Map<String, String> getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.summary_list_item, parent, false);
        }
        TextView titleView = (TextView) rowView.findViewById(R.id.titleView);
        Map<String, String> settingsMap = arrayList.get(position);

        String title = settingsMap.get("title");
        titleView.setText(title);
        TextView subTitleView = (TextView) rowView.findViewById(R.id.subTitleView);
        if(title.equals("Dropbox Sync Info") || title.equals("Backup Summary"))
        {
            RelativeLayout relativeLayout = (RelativeLayout) rowView.findViewById(R.id.relativeLayout);
            relativeLayout.setBackgroundResource(R.color.light_green_color);
            titleView.setGravity(Gravity.CENTER);
            titleView.setTextColor(context.getResources().getColor(R.color.white_color));
            subTitleView.setVisibility(View.GONE);
        }
        else {
            subTitleView.setText(settingsMap.get("subTitle"));
        }
        return rowView;
    }
}
