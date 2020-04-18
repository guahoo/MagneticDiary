package com.magneticdiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ListViewAdapter extends BaseAdapter {
    private final ArrayList<Map.Entry<String, ArrayList<String>>> mData;
    Map.Entry<String, ArrayList<String>> item;
    Map<String, ArrayList<String>> nullMap;
    ArrayList<String> nullArray = new ArrayList<>();


    public ListViewAdapter(HashMap<String, ArrayList<String>> map) {
        mData = new ArrayList<Map.Entry<String, ArrayList<String>>>();
        mData.addAll(map.entrySet());
    }


    @Override
    public int getCount() {

        return mData.size();

    }

    @Override
    public Map.Entry<String, ArrayList<String>> getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.lv, parent, false);
        } else {
            result = convertView;
        }

        item = getItem(position);

        try {
            ((TextView) result.findViewById(R.id.colId)).setText(item.getKey());
            ((TextView) result.findViewById(R.id.colPlaceName)).setText(item.getValue().get(0));
            ((TextView) result.findViewById(R.id.colMagneticValue)).setText(item.getValue().get(1));
            ((TextView) result.findViewById(R.id.colDate)).setText(item.getValue().get(6));
        } catch (IndexOutOfBoundsException aie) {
            ((TextView) result.findViewById(R.id.colPlaceName)).setText("Ничего нет в базе");
        }
        return result;
    }

}
