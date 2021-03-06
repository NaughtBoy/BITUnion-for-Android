package me.ihainan.bu.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Map;

import me.ihainan.bu.app.R;
import me.ihainan.bu.app.utils.ui.Emoticons;

/**
 * Emoticon Adapter.
 */
public class EmoticonAdapter extends BaseAdapter {
    private static final ArrayList<String> mNames = new ArrayList<>();
    private static final ArrayList<Bitmap> mBitmaps = new ArrayList<>();

    private final LayoutInflater mInflater;

    public EmoticonAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mNames.size();
    }

    @Override
    public String getItem(int position) {
        return mNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position >= mNames.size()) {
            return convertView;
        } else {
            View v = convertView != null ? convertView : mInflater.inflate(R.layout.emoticon_view, null);
            ImageView iv = (ImageView) v.findViewById(R.id.emoticon_image);
            iv.setImageBitmap(mBitmaps.get(position));
            return v;
        }
    }

    public static void init() {
        for (Map.Entry<String, Bitmap> entry : Emoticons.EMOTICON_BITMAPS.entrySet()) {
            if (!mNames.contains(entry.getKey()) && !mBitmaps.contains(entry.getValue())) {
                mNames.add(entry.getKey());
                mBitmaps.add(entry.getValue());
            }
        }
    }

}
