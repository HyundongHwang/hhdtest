package com.hhd2002.androidbaselib.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class HhdRecyclerLoadMoreViewHolder extends HhdRecyclerViewHolder {
    public HhdRecyclerLoadMoreViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public View inflateConvertView(ViewGroup parent) {
        Context context = parent.getContext();
        LinearLayout llRoot = new LinearLayout(context);
        parent.addView(llRoot);
        llRoot.setOrientation(LinearLayout.VERTICAL);

        {
            ViewGroup.LayoutParams lp = llRoot.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            llRoot.setLayoutParams(lp);
        }

        ProgressBar pb = new ProgressBar(context);
        pb.setIndeterminate(true);
        llRoot.addView(pb);

        {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pb.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
            pb.setLayoutParams(lp);
        }

        return llRoot;
    }

    @Override
    public void findAllViews(Object parentCallback) {
        
    }

    @Override
    public void onBindViewHolder() {

    }
}
