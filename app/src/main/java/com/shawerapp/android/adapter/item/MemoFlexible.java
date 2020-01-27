package com.shawerapp.android.adapter.item;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.Memo;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MemoFlexible extends AbstractFlexibleItem<MemoFlexible.ViewHolder> {

    private final Memo memo;

    public MemoFlexible(Memo memo) {
        this.memo = memo;
    }

    public Memo getMemo() {
        return memo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoFlexible that = (MemoFlexible) o;
        return Objects.equals(memo, that.memo);
    }

    @Override
    public int hashCode() {

        return Objects.hash(memo);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_memo;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder,
                               int position, List<Object> payloads) {
        final Context context = holder.itemView.getContext();
        holder.memoTextView.setText(memo.message());
        holder.dateTextView.setText(DateUtils.getRelativeTimeSpanString(memo.datePosted().getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
    }

    static final class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.memoTextView)
        TextView memoTextView;

        @BindView(R.id.dateTextView)
        TextView dateTextView;

        public ViewHolder(View view,
                          FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }

    public static class Comparator implements java.util.Comparator<IFlexible> {
        @Override
        public int compare(IFlexible o1, IFlexible o2) {
            if (o1 instanceof MemoFlexible && o2 instanceof MemoFlexible) {
                MemoFlexible item1 = (MemoFlexible) o1;
                MemoFlexible item2 = (MemoFlexible) o2;

                Memo memo1 = item1.getMemo();
                Memo memo2 = item2.getMemo();

                return memo2.datePosted().compareTo(memo1.datePosted());
            }
            return 0;
        }
    }
}
