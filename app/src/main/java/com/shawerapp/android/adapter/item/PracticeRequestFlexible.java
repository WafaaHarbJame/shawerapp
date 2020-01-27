package com.shawerapp.android.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.PracticeRequest;
import com.shawerapp.android.utils.LocaleHelper;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.io.Serializable;
import java.util.List;

public class PracticeRequestFlexible extends AbstractFlexibleItem<PracticeRequestFlexible.ViewHolder> implements IFilterable {

    private final PracticeRequest mPracticeRequest;

    public PracticeRequestFlexible(PracticeRequest practiceRequest) {
        this.mPracticeRequest = practiceRequest;
    }

    public PracticeRequest getPracticeRequest() {
        return mPracticeRequest;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_requesting_shawer;
    }

    @Override
    public ViewHolder createViewHolder(View view,
                                       FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder,
                               int position, List<Object> payloads) {
        Context context = holder.itemView.getContext();

        if (LocaleHelper.getLanguage(context).equalsIgnoreCase(LocaleHelper.ARABIC)) {
            holder.titleTextView.setText(mPracticeRequest.ar_subSubjectName());
        } else {
            holder.titleTextView.setText(mPracticeRequest.subSubjectName());
        }
        holder.descriptionTextView.setText(context.getString(R.string.app_name));
        holder.costTextView.setText(context.getString(R.string.format_coins, String.valueOf(mPracticeRequest.serviceFee())));

        if (mPracticeRequest.status().equalsIgnoreCase(PracticeRequest.Status.FULFILLED)) {
            holder.statusImageView.setImageResource(R.drawable.gold_gradient_circle);
        } else {
            holder.statusImageView.setImageResource(R.drawable.gold_stroke);
        }
    }

    @Override
    public boolean filter(Serializable constraint) {
        return mPracticeRequest.subSubjectName().toLowerCase().contains(constraint.toString().toLowerCase());
    }

    static final class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.titleTextView)
        TextView titleTextView;

        @BindView(R.id.descriptionTextView)
        TextView descriptionTextView;

        @BindView(R.id.feeTextView)
        TextView costTextView;

        @BindView(R.id.statusImageView)
        ImageView statusImageView;

        ViewHolder(View view,
                   FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
