package com.shawerapp.android.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.SubSubject;
import com.shawerapp.android.utils.LocaleHelper;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class SubSubjectFlexible extends AbstractFlexibleItem<SubSubjectFlexible.ViewHolder> implements IFilterable {

    private final SubSubject mSubSubject;

    private Long mPracticeRequestCost;

    public SubSubjectFlexible(SubSubject mSubSubjectName, Long practiceRequestCost) {
        this.mSubSubject = mSubSubjectName;
        this.mPracticeRequestCost = practiceRequestCost;
    }

    public SubSubject getSubSubject() {
        return mSubSubject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubSubjectFlexible that = (SubSubjectFlexible) o;
        return Objects.equals(getSubSubject(), that.getSubSubject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubSubject());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_field_sub_subject;
    }

    @Override
    public ViewHolder createViewHolder(View view,
                                       FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder,
                               int position, List<Object> payloads) {
        final Context context = holder.itemView.getContext();
        if (LocaleHelper.getLanguage(context).equalsIgnoreCase(LocaleHelper.ARABIC)) {
            holder.titleTextView.setText(mSubSubject.ar_subSubjectName());
            holder.descriptionTextView.setText(mSubSubject.ar_description());
        } else {
            holder.titleTextView.setText(mSubSubject.subSubjectName());
            holder.descriptionTextView.setText(mSubSubject.description());
        }

        if (mPracticeRequestCost != null && mPracticeRequestCost > 0L) {
            holder.costTextView.setVisibility(View.VISIBLE);
            holder.lawyerCountTextView.setVisibility(View.GONE);

            holder.costTextView.setText(context.getString(R.string.format_coins, String.valueOf(mPracticeRequestCost)));
        } else {
            holder.costTextView.setVisibility(View.GONE);
            holder.lawyerCountTextView.setVisibility(View.VISIBLE);

            int lawyerCount = mSubSubject.lawyers() != null ? Objects.requireNonNull(mSubSubject.lawyers()).size() : 0;
            holder.lawyerCountTextView.setText(context.getString(R.string.label_sub_subject_lawyer_count, lawyerCount));
        }
    }

    @Override
    public boolean filter(Serializable constraint) {
        return mSubSubject.subSubjectName().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                mSubSubject.description().toLowerCase().contains(constraint.toString().toLowerCase());
    }

    static final class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.titleTextView)
        TextView titleTextView;

        @BindView(R.id.descriptionTextView)
        TextView descriptionTextView;

        @BindView(R.id.lawyerCountTextView)
        TextView lawyerCountTextView;

        @BindView(R.id.feeTextView)
        TextView costTextView;

        ViewHolder(View view,
                   FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
