package com.shawerapp.android.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.shawerapp.android.adapter.item.LawyerFileFlexible;
import com.shawerapp.android.autovalue.LawyerFile;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class LawyerFileAdapter extends FlexibleAdapter<LawyerFileFlexible> {

    private View.OnClickListener mOnClickListener;

    public LawyerFileAdapter(@Nullable List<LawyerFileFlexible> items, View.OnClickListener onClickListener) {
        super(items);
        mOnClickListener = onClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (holder instanceof LawyerFileFlexible.ViewHolder) {
            LawyerFileFlexible.ViewHolder viewHolder = (LawyerFileFlexible.ViewHolder) holder;

            LawyerFile item = getItem(position).getLawyerFile();
            viewHolder.btnView.setTag(item);
            viewHolder.btnView.setOnClickListener(mOnClickListener);
            viewHolder.clickableView.setTag(item);
            viewHolder.clickableView.setOnClickListener(mOnClickListener);
        }
        super.onBindViewHolder(holder, position, payloads);
    }
}
