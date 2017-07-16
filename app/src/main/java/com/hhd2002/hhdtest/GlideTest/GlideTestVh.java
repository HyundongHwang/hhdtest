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

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by hhd on 2017-06-30.
 */

public class GlideTestVh extends HhdRecyclerViewHolder {
    private ImageView _imgObj;

    public GlideTestVh(View converView) {
        super(converView);
    }

    @Override
    public View inflateConvertView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View convertView = inflater.inflate(R.layout.item_glide_test, parent, false);
        return convertView;
    }

    @Override
    public void findAllViews(Object parentCallback) {
        _imgObj = (ImageView) this.itemView.findViewById(R.id.img_obj);
    }

    @Override
    public void onBindViewHolder() {
        GlideTestImage gtImage = (GlideTestImage)this.item;
        int displayWidth = HhdDeviceUtils.getDisplayWidth(this.itemView.getContext()) / 3;
        int displayHeight = displayWidth * gtImage.height / Math.max(gtImage.width, 1);
        _imgObj.getLayoutParams().width = displayWidth;
        _imgObj.getLayoutParams().height = displayHeight;

        Glide.with(this.itemView.getContext())
                .load(gtImage.thumbnailUri)
                .transition(withCrossFade())
                .into(_imgObj);
    }
}
