package com.hhd2002.hhdtest.GlideTest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hhd2002.androidbaselib.HhdDeviceUtils;
import com.hhd2002.androidbaselib.adapters.HhdRecyclerViewHolder;
import com.hhd2002.androidbaselib.funcdelegate.IHhdFuncDelegateIn;
import com.hhd2002.hhdtest.GlideTest.models.GlideTestImage;
import com.hhd2002.hhdtest.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by hhd on 2017-06-30.
 */

public class GlideTestVh extends HhdRecyclerViewHolder {
    private IHhdFuncDelegateIn<GlideTestImage> _onClick;
    private ImageView _imgObj;
    private GlideTestImage _item;

    public GlideTestVh(View converView) {
        super(converView);
    }

    @Override
    public void onCreateViewHolder(Object onEvent, Object onEvent2, Object onEvent3) {
        _onClick = (IHhdFuncDelegateIn<GlideTestImage>) onEvent;
        _imgObj = (ImageView) this.itemView.findViewById(R.id.img_obj);
        _imgObj.setOnClickListener((v) -> {
            _onClick.execute(_item);
        });
    }

    @Override
    public View createConvertView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.item_glide_test, parent, false);
        return convertView;
    }

    @Override
    public void onBindViewHolder(Object item, int position) {
        _item = (GlideTestImage)item;
        int displayWidth = HhdDeviceUtils.getDisplayWidth(this.itemView.getContext()) / 3;
        int displayHeight = displayWidth * _item.height / Math.max(_item.width, 1);
        _imgObj.getLayoutParams().width = displayWidth;
        _imgObj.getLayoutParams().height = displayHeight;

        Glide.with(this.itemView.getContext())
                .load(_item.thumbnailUri)
                .transition(withCrossFade())
                .into(_imgObj);
    }




}
