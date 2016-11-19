package com.smart.browserhistory.filebrowser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smart.browserhistory.R;

import java.util.List;

/**
 * Created by Purushotham on 20-11-2014.
 */
public class FileArrayAdapter extends ArrayAdapter<Item> {

    private Context c;
    private int id;
    private List<Item> items;

    public FileArrayAdapter(Context context, int textViewResourceId,
                            List<Item> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        items = objects;
    }

    public Item getItem(int i) {
        return items.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }

        final Item o = items.get(position);
        if (o != null) {
            TextView titleView = (TextView) v.findViewById(R.id.titleView);
            TextView subTitleView = (TextView) v.findViewById(R.id.subTitleView);
            ImageView folderImg = (ImageView) v.findViewById(R.id.folderIcon);
            String uri = "drawable/" + o.getImage();
            int imageResource = c.getResources().getIdentifier(uri, null, c.getPackageName());
            Drawable image = c.getResources().getDrawable(imageResource);
            folderImg.setImageDrawable(image);

            if (titleView != null)
                titleView.setText(o.getName());
            if (subTitleView != null)
                subTitleView.setText(o.getData());
        }
        return v;
    }
}