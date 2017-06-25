package com.hhd2002.hhdtest.ListViewTest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hhd2002.androidbaselib.HhdMultiViewTypeAdapter;
import com.hhd2002.hhdtest.R;

public class BlueVh extends HhdMultiViewTypeAdapter.IViewHolderBase {
    public BlueVh() {
    }

    private EditText etObj;

    @Override
    public View createNewView(Context context, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_item_my_blue, parent, false);
        etObj = (EditText) view.findViewById(R.id.et_obj);
        return view;
    }

    @Override
    public void updateUiByItem(int position, View convertView, ViewGroup parent, Object item) {
        BlueItem bitem = (BlueItem) item;
        etObj.setText(bitem.str);
    }
}
