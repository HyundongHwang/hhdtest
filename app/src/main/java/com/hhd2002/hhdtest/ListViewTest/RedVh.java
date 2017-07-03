package com.hhd2002.hhdtest.ListViewTest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hhd2002.androidbaselib.adapters.HhdListViewHolder;
import com.hhd2002.hhdtest.R;

public class RedVh extends HhdListViewHolder {
    public RedVh() {
    }

    private Button btnObj;

    @Override
    public View createNewView(Context context, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_item_my_red, parent, false);
        btnObj = (Button) view.findViewById(R.id.btn_obj);
        return view;
    }

    @Override
    public void updateUiByItem(int position, View convertView, ViewGroup parent, Object item) {
        RedItem ritem = (RedItem)item;
        btnObj.setText(ritem.str);
    }
}
