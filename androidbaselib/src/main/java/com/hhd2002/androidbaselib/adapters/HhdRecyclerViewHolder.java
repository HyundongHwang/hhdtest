package com.hhd2002.androidbaselib.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hhd on 2017-06-30.
 */

public abstract class HhdRecyclerViewHolder extends RecyclerView.ViewHolder {
    public HhdRecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBindViewHolder(Object item, int position);

    public abstract void onCreateViewHolder(
            Object onEvent,
            Object onEvent2,
            Object onEvent3
    );
    
    public abstract View createConvertView(ViewGroup parent);
}
