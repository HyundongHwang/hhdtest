package com.hhd2002.androidbaselib.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by hhd on 2017-06-30.
 */

public class HhdRecyclerViewAdapter extends RecyclerView.Adapter {

    public ArrayList<Object> items = new ArrayList<>();

    private Context _context;
    private ArrayList<Class> _itemTypes;
    private ArrayList<Class<? extends HhdRecyclerViewHolder>> _vhTypes;
    private Object _onEvent;
    private Object _onEvent2;
    private Object _onEvent3;

    public HhdRecyclerViewAdapter(
            Context context,
            ArrayList<Class> itemTypes,
            ArrayList<Class<? extends HhdRecyclerViewHolder>> vhTypes,
            Object onEvent,
            Object onEvent2,
            Object onEvent3) {
        _context = context;
        _itemTypes = itemTypes;
        _vhTypes = vhTypes;
        _onEvent = onEvent;
        _onEvent2 = onEvent2;
        _onEvent3 = onEvent3;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            Class<? extends HhdRecyclerViewHolder> vhType = _vhTypes.get(viewType);
            View convertView = vhType.getDeclaredConstructor(View.class).newInstance(parent).createConvertView(parent);
            HhdRecyclerViewHolder vh = vhType.getDeclaredConstructor(View.class).newInstance(convertView);
            vh.onCreateViewHolder(_onEvent, _onEvent2, _onEvent3);
            return vh;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);
        HhdRecyclerViewHolder vh = (HhdRecyclerViewHolder) holder;
        vh.onBindViewHolder(item, position);
    }
}

