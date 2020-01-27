package com.shawerapp.android.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.shawerapp.android.adapter.item.DiscoverLawyerFlexible;

import eu.davidea.flexibleadapter.FlexibleAdapter;

import java.util.List;

public class DiscoverLawyerAdapter extends FlexibleAdapter<DiscoverLawyerFlexible> {

    private View.OnClickListener mOnClickListener;

    public DiscoverLawyerAdapter(@Nullable List<DiscoverLawyerFlexible> items, View.OnClickListener onClickListener) {
        super(items);
        mOnClickListener = onClickListener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (holder instanceof DiscoverLawyerFlexible.ViewHolder) {
            DiscoverLawyerFlexible.ViewHolder viewHolder = (DiscoverLawyerFlexible.ViewHolder) holder;

            DiscoverLawyerFlexible item = getItem(position);
            viewHolder.favoriteIndicatorImageView.setTag(item);
            viewHolder.favoriteIndicatorImageView.setOnClickListener(mOnClickListener);
        }
        super.onBindViewHolder(holder, position, payloads);
    }
}
