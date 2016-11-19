package com.smart.browserhistory.fragment.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.smart.browserhistory.R;
import com.smart.browserhistory.vo.HistoryVO;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Purushotham on 09-09-2014.
 */
public class ChromeHistoryDetailsFragment extends DialogFragment {

    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_details, null);
        //TextView titleView = (TextView) rootView.findViewById(R.id.titleView);
        TextView urlView = (TextView) rootView.findViewById(R.id.urlView);
        TextView lastVisitedView = (TextView) rootView.findViewById(R.id.lastVisitedView);
        TextView visitsView = (TextView) rootView.findViewById(R.id.visitsView);

        Button visitSiteButton = (Button) rootView.findViewById(R.id.visitSiteButton);
        Button closeButton = (Button) rootView.findViewById(R.id.closeButton);
        final HistoryVO historyVO = (HistoryVO) getArguments().getSerializable("historyVO");
        visitSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = historyVO.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        //titleView.setText(historyVO.getTitle());
        urlView.setText(historyVO.getUrl());
        Date date = new Date(historyVO.getLastVisited());
        lastVisitedView.setText("Last Visited On: " + format.format(date));
        visitsView.setText("Visits: " + historyVO.getVisits());
        if (historyVO.getTitle() != null) {
            getDialog().setTitle(historyVO.getTitle());
        } else {
            getDialog().setTitle(historyVO.getUrl());
        }
        return rootView;
    }
}
