package com.hhd2002.androidbaselib.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by hhd on 2017-06-30.
 */

public abstract class HhdRecyclerViewHolder extends RecyclerView.ViewHolder {

    public ArrayList<Object> items;
    public Object item;
    public int position;
    
    public HhdRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public abstract View inflateConvertView(
            ViewGroup parent);

    public abstract void findAllViews(Object parentCallback);
    
    public abstract void onBindViewHolder();
}
