package com.shawerapp.android.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.shawerapp.android.adapter.item.ReportUserFlexible;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class ReportUserAdapter extends FlexibleAdapter<ReportUserFlexible> {

    private boolean mIsShowLawyers;

    private View.OnClickListener mOnClickListener;

    public ReportUserAdapter(@Nullable List<ReportUserFlexible> items, boolean isShowLawyers, View.OnClickListener onClickListener) {
        super(items);
        mIsShowLawyers = isShowLawyers;
        mOnClickListener = onClickListener;
    }

    public boolean isShowLawyers() {
        return mIsShowLawyers;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        if (holder instanceof ReportUserFlexible.ViewHolder) {
            ReportUserFlexible.ViewHolder viewHolder = (ReportUserFlexible.ViewHolder) holder;

            ReportUserFlexible item = getItem(position);
            if (isShowLawyers()) {
                viewHolder.favoriteIndicatorImageView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.favoriteIndicatorImageView.setVisibility(View.GONE);
            }

            viewHolder.mask.setTag(item);
            viewHolder.mask.setOnClickListener(mOnClickListener);
        }
        super.onBindViewHolder(holder, position, payloads);
    }
}
