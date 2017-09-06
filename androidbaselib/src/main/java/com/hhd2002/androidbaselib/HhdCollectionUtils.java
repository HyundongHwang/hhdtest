package com.hhd2002.androidbaselib;

import android.text.TextUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class HhdCollectionUtils {

    private HhdCollectionUtils() {
    }

    public static boolean contains(Object[] array, Object target) {
        if (target != null && array != null) {
            for (Object o : array) {
                if (o.equals(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> T putEntryToMapIfNotNull(HashMap<String, ? super T> map, String key, T value) {
        if (value != null) {
            map.put(key, value);
        }
        return value;
    }

    public static CharSequence putEntryToMapIfNotEmpty(HashMap<String, ? super String> map, String key, String value) {
        if (!TextUtils.isEmpty(value)) {
            map.put(key, value);
        }

        return value;
    }

    public static <TSource, TTarget> ArrayList<TTarget> convertArrayListType(Iterable<TSource> sourceList) {
        ArrayList<TTarget> targetList = new ArrayList<TTarget>();

        for (TSource item : sourceList) {
            targetList.add((TTarget) item);
        }

        return targetList;
    }


    public static class ArrayListBuilder<T> {
        private ArrayList<T> list;

        private ArrayListBuilder() {
            list = new ArrayList<T>();
        }

        public static <T> ArrayListBuilder<T> newBuilder() {
            return new ArrayListBuilder<T>();
        }

        public static <T> ArrayListBuilder<T> newBuilder(T item) {
            ArrayListBuilder<T> builder = new ArrayListBuilder<T>();
            return builder.add(item);
        }

        public ArrayListBuilder<T> add(T item) {
            list.add(item);
            return this;
        }


        public ArrayList<T> build() {
            return list;
        }
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }


    public static <T> T getLast(ArrayList<T> list) {
        if (list == null) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }
}
