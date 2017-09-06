package com.hhd2002.androidbaselib.Adapters;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by hhd on 2017-06-30.
 */

public class HhdRecyclerViewAdapter extends RecyclerView.Adapter {

    
    
    public ArrayList<Object> items = new ArrayList<>();
    private RecyclerView _rcView;
    private ArrayList<Class> _itemTypes;
    private ArrayList<Class<? extends HhdRecyclerViewHolder>> _vhTypes;
    private IHhdRecyclerViewListener _listener;
    private Object _additionalCallback;

    
    
    public HhdRecyclerViewAdapter(
            RecyclerView rcView,
            ArrayList<Class> itemTypes,
            ArrayList<Class<? extends HhdRecyclerViewHolder>> vhTypes,
            IHhdRecyclerViewListener listener) {
        
        _rcView = rcView;
        _itemTypes = itemTypes;
        _vhTypes = vhTypes;
        _listener = listener;

        _init();
    }
    
    
    
    public HhdRecyclerViewAdapter(
            RecyclerView rcView,
            ArrayList<Class> itemTypes,
            ArrayList<Class<? extends HhdRecyclerViewHolder>> vhTypes,
            IHhdRecyclerViewListener listener,
            Object additionalCallback) {
        
        _rcView = rcView;
        _itemTypes = itemTypes;
        _vhTypes = vhTypes;
        _listener = listener;
        _additionalCallback = additionalCallback;

        _init();
    }//public HhdRecyclerViewAdapter

    
    
    private void _init() {
        _itemTypes.add(HhdRecyclerLoadMoreInfo.class);
        _vhTypes.add(HhdRecyclerLoadMoreViewHolder.class);
        Handler uiHandler = new Handler();

        _rcView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int itemsSize = HhdRecyclerViewAdapter.this.items.size();
                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();

                if (lm instanceof LinearLayoutManager) {
                    LinearLayoutManager llm = (LinearLayoutManager) lm;
                    int lvp = llm.findLastCompletelyVisibleItemPosition();

                    if (lvp == (itemsSize - 1)) {
                        _fireOnLastItem();
                    }
                } else if (lm instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager slm = (StaggeredGridLayoutManager) lm;
                    int[] lcvp = slm.findLastCompletelyVisibleItemPositions(new int[]{0, 1, 2});

                    for (int i : lcvp) {
                        if (i == (itemsSize - 1)) {
                            _fireOnLastItem();
                            break;
                        }
                    }
                }

            }//public void onScrolled

            private void _fireOnLastItem() {
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        _listener.onLastItem();        
                    }
                }, 100);
            }
            
        }); //_rcView.addOnScrollListener
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            Class<? extends HhdRecyclerViewHolder> vhType = _vhTypes.get(viewType);

            View convertView =
                    vhType.getDeclaredConstructor(View.class)
                            .newInstance(parent)
                            .inflateConvertView(parent);

            HhdRecyclerViewHolder vh =
                    vhType.getDeclaredConstructor(View.class)
                            .newInstance(convertView);

            vh.findAllViews(_additionalCallback);
            
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    _listener.onClickItem(
                            vh.item, 
                            vh.position, 
                            convertView);
                    
                }
            });

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
        vh.item = item;
        vh.position = position;
        vh.onBindViewHolder();
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

