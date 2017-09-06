package com.hhd2002.androidbaselib.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.hhd2002.androidbaselib.HhdCollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hhd on 2017-08-24.
 */

public class HhdHintStringArrayAdapter extends ArrayAdapter<String> {
    private HhdHintStringArrayAdapter(@NonNull Context context,
                                      int rowLayoutId,
                                      int textViewId,
                                      @NonNull List<String> itemListWithHintText) {
        super(context, rowLayoutId, textViewId, itemListWithHintText);
    }

    @Override
    public int getCount() {
        int orgCount = super.getCount();
        int count = orgCount > 0 ? orgCount - 1 : orgCount;
        return count;
    }

    public static HhdHintStringArrayAdapter init(
            AdapterView adapterView,
            @NonNull Context context,
            int rowLayoutId,
            int textViewId,
            @NonNull String[] itemArray,
            String hintText) {

        ArrayList<String> itemList = new ArrayList<>(Arrays.asList(itemArray));
        HhdHintStringArrayAdapter adapter = init(adapterView, context, rowLayoutId, textViewId, itemList, hintText);
        return adapter;

    }

    public static HhdHintStringArrayAdapter init(
            AdapterView adapterView,
            @NonNull Context context,
            int rowLayoutId,
            int textViewId,
            @NonNull ArrayList<String> itemList,
            String hintText) {

        itemList.add(hintText);
        HhdHintStringArrayAdapter adapter = new HhdHintStringArrayAdapter(context, rowLayoutId, textViewId, itemList);
        adapterView.setAdapter(adapter);
        adapterView.setSelection(itemList.size() - 1);
        return adapter;

    }
}
