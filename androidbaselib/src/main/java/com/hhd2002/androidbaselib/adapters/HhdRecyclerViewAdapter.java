package com.hhd2002.androidbaselib.adapters;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.hhd2002.androidbaselib.HhdAsyncTask;

import java.util.ArrayList;

/**
 * Created by hhd on 2017-06-30.
 */

public class HhdRecyclerViewAdapter extends RecyclerView.Adapter {

    public static final int VIEW_TYPE_LOADMORE = 1000;
    public ArrayList<Object> items = new ArrayList<>();

    private RecyclerView _rcView;
    private ArrayList<Class> _itemTypes;
    private ArrayList<Class<? extends HhdRecyclerViewHolder>> _vhTypes;
    private IHhdRecyclerViewListener _listener;
    private Object _onEvent;
    private Object _onEvent2;
    private Object _onEvent3;
    private boolean _isLoading = false;

    public HhdRecyclerViewAdapter(
            RecyclerView rcView,
            ArrayList<Class> itemTypes,
            ArrayList<Class<? extends HhdRecyclerViewHolder>> vhTypes,
            IHhdRecyclerViewListener listener,
            Object onEvent,
            Object onEvent2,
            Object onEvent3) {
        _rcView = rcView;
        _itemTypes = itemTypes;
        _vhTypes = vhTypes;
        _listener = listener;
        _onEvent = onEvent;
        _onEvent2 = onEvent2;
        _onEvent3 = onEvent3;

        _itemTypes.add(HhdRecyclerLoadMoreInfo.class);
        _vhTypes.add(HhdRecyclerLoadMoreViewHolder.class);

        _rcView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int itemsSize = HhdRecyclerViewAdapter.this.items.size();
                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
                boolean isLast = false;

                if (lm instanceof LinearLayoutManager) {
                    LinearLayoutManager llm = (LinearLayoutManager) lm;
                    int lvp = llm.findLastCompletelyVisibleItemPosition();

                    if (lvp == (itemsSize - 1)) {
                        isLast = true;
                    }
                } else if (lm instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager slm = (StaggeredGridLayoutManager) lm;
                    int[] lcvp = slm.findLastCompletelyVisibleItemPositions(new int[]{0, 1, 2});

                    for (int i : lcvp) {
                        if (i == (itemsSize - 1)) {
                            isLast = true;
                            break;
                        }
                    }
                }

                if (isLast &&
                        !_isLoading &&
                        _listener.canLoadMore()) {

                    _isLoading = true;
                    HhdRecyclerViewAdapter.this.items.add(new HhdRecyclerLoadMoreInfo());

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            HhdRecyclerViewAdapter.this.notifyDataSetChanged();

                            new HhdAsyncTask() {

                                @Override
                                protected void doInBackground() {
                                    _listener.onLoadMore();
                                    Object loadMoreInfo = null;

                                    for (int i = HhdRecyclerViewAdapter.this.items.size() - 1; i >= 0; i--) {
                                        Object item = HhdRecyclerViewAdapter.this.items.get(i);
                                        
                                        if (item instanceof HhdRecyclerLoadMoreInfo) {
                                            HhdRecyclerViewAdapter.this.items.remove(item);
                                        }
                                    }

                                    _isLoading = false;
                                }

                                @Override
                                protected void onPostExecute() {
                                    super.onPostExecute();
                                    HhdRecyclerViewAdapter.this.notifyDataSetChanged();
                                }
                            }.execute();


                        }
                    });
                }
            }
        });
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

    @Override
    public int getItemViewType(int position) {

        Object item = this.items.get(position);
        int size = _itemTypes.size();
        int viewType = 0;

        for (int i = 0; i < size; i++) {
            Class cls = _itemTypes.get(i);

            if (item.getClass() == cls) {
                viewType = i;
                break;
            }
        }

        return viewType;
    }
}

