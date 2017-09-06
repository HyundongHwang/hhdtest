package com.hhd2002.hhdtest.ListViewTest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hhd2002.androidbaselib.Adapters.HhdListViewHolder;
import com.hhd2002.hhdtest.R;

public class GreenVh extends HhdListViewHolder {
    public GreenVh() {
    }

    private TextView tvObj;

    @Override
    public View createNewView(Context context, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_item_my_green, parent, false);
        tvObj = (TextView) view.findViewById(R.id.tv_obj);
        return view;
    }

    @Override
    public void updateUiByItem(int position, View convertView, ViewGroup parent, Object item) {
        GreenItem ritem = (GreenItem)item;
        tvObj.setText(ritem.str);
    }
}
