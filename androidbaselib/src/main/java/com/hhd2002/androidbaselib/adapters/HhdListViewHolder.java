package com.hhd2002.androidbaselib.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public abstract class HhdListViewHolder {
    public Object adapterListener;
    public abstract View createNewView(Context context, ViewGroup parent);
    public abstract void updateUiByItem(int position, View convertView, ViewGroup parent, Object item);
}
