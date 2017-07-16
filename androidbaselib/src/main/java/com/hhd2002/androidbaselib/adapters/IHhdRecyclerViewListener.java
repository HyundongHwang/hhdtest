package com.hhd2002.androidbaselib.adapters;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by hhd on 2017-07-03.
 */

public interface IHhdRecyclerViewListener {
    
    void onClickItem(Object item,
                     int position,
                     View convertView);

    void onLastItem();
}
