package com.hhd2002.androidbaselib.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.ArrayList;


public class HhdListViewAdapter extends BaseAdapter {
    

    public ArrayList<Object> items = new ArrayList<>();


    private Context context;
    private ArrayList<Class> itemTypes = new ArrayList<>();
    private ArrayList<Class<? extends HhdListViewHolder>> vhTypes = new ArrayList<>();
    private Object adapterListener;


    public HhdListViewAdapter(Context context, ArrayList<Class> itemTypes, ArrayList<Class<? extends HhdListViewHolder>> vhTypes, Object adapterListener) {
        this.context = context;
        this.itemTypes = new ArrayList<>(itemTypes);
        this.vhTypes = new ArrayList<>(vhTypes);
        this.adapterListener = adapterListener;
    }


    @Override
    public int getCount() {
        int size = items.size();
        return size;
    }

    @Override
    public int getViewTypeCount() {
        int size = itemTypes.size();
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 0 || position >= items.size()) {
            return AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
        }

        Object item = items.get(position);
        int idx = itemTypes.indexOf(item.getClass());
        return idx;
    }

    @Override
    public Object getItem(int position) {
        Object item = items.get(position);
        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object item = getItem(position);
        Class itemType = item.getClass();
        int idx = itemTypes.indexOf(itemType);
        Class<? extends HhdListViewHolder> vhType = vhTypes.get(idx);
        HhdListViewHolder vh = null;

        if (convertView == null) {
            try {
                vh = vhType.newInstance();
                vh.adapterListener = adapterListener;
                convertView = vh.createNewView(context, parent);
                convertView.setTag(vh);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            vh = (HhdListViewHolder) convertView.getTag();
        }

        vh.updateUiByItem(position, convertView, parent, item);
        return convertView;
    }
}