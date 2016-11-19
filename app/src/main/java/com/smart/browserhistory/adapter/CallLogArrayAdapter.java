package com.smart.browserhistory.adapter;

import android.content.Context;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smart.browserhistory.R;
import com.smart.browserhistory.vo.CallVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CallLogArrayAdapter extends ArrayAdapter<CallVO> {

    private final List<CallVO> callList;
    private final List<CallVO> arrayList;
    private final Context context;

    public CallLogArrayAdapter(Context context, List<CallVO> callList) {
        super(context, R.layout.history_list_row, callList);
        this.context = context;
        this.callList = callList;
        this.arrayList = new ArrayList<>();
        arrayList.addAll(callList);
    }

    public void filter(String searchStr) {
        searchStr = searchStr.toLowerCase(Locale.getDefault());
        callList.clear();
        if (searchStr.length() == 0) {
            callList.addAll(arrayList);
        } else {
            for (CallVO callVO : arrayList) {
                if (callVO.toString().toLowerCase(Locale.getDefault()).contains(searchStr)) {
                    callList.add(callVO);
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
            rowView = inflater.inflate(R.layout.call_list_row, parent, false);
        } else {
            rowView = convertView;
        }
        CallVO callVO = callList.get(position);
        TextView callerView = (TextView) rowView.findViewById(R.id.callerView);
        TextView mobileNoView = (TextView) rowView.findViewById(R.id.mobileNoView);
        TextView dateView = (TextView) rowView.findViewById(R.id.dateView);
        ImageView callTypeImage = (ImageView) rowView.findViewById(R.id.callTypeImage);
        TextView callDurationView = (TextView) rowView.findViewById(R.id.callDurationView);
        callerView.setText(callVO.caller);
        mobileNoView.setText(callVO.address);
        callerView.setTextColor(context.getResources().getColor(R.color.black_color));
        dateView.setTextColor(context.getResources().getColor(R.color.light_gray_color));
        callDurationView.setTextColor(context.getResources().getColor(R.color.light_gray_color));
        mobileNoView.setTextColor(context.getResources().getColor(R.color.gray_color));
        switch (callVO.callType) {
            case CallLog.Calls.OUTGOING_TYPE:
                callTypeImage.setImageResource(R.drawable.ic_call_made);
                callDurationView.setText("(" + callVO.duration + " secs)");
                break;

            case CallLog.Calls.INCOMING_TYPE:
                callTypeImage.setImageResource(R.drawable.ic_call_received);
                callDurationView.setText("(" + callVO.duration + " secs)");
                break;

            case CallLog.Calls.MISSED_TYPE:
                callerView.setTextColor(context.getResources().getColor(R.color.red_color));
                dateView.setTextColor(context.getResources().getColor(R.color.red_color));
                callDurationView.setTextColor(context.getResources().getColor(R.color.red_color));
                mobileNoView.setTextColor(context.getResources().getColor(R.color.red_color));
                callTypeImage.setImageResource(R.drawable.ic_call_missed);
                callDurationView.setText("");
                break;
        }


        dateView.setText(callVO.dateStr);

        return rowView;
    }
}
