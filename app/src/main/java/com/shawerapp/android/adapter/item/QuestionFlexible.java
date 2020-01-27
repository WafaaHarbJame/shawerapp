package com.shawerapp.android.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.model.SubSubjectCase;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LocaleHelper;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class QuestionFlexible extends AbstractFlexibleItem<QuestionFlexible.ViewHolder> implements IFilterable {

    private Question mQuestion;

    public Question getQuestion() {
        return mQuestion;
    }

    public QuestionFlexible(Question question) {
        mQuestion = question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionFlexible that = (QuestionFlexible) o;
        return Objects.equals(getQuestion(), that.getQuestion());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getQuestion());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_ask_shawer;
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
            holder.subSubjectNameTextView.setText(mQuestion.ar_subSubjectName());
        } else {
            holder.subSubjectNameTextView.setText(mQuestion.subSubjectName());
        }
        holder.lawyerNameTextView.setText(CommonUtils.isNotEmpty(mQuestion.assignedLawyerName()) ? mQuestion.assignedLawyerName() : mQuestion.assignedLawyerUsername());
        if (mQuestion.serviceFee().intValue() == 0) {
            holder.costTextView.setText(context.getString(R.string.free));
        } else {
            holder.costTextView.setText(context.getString(R.string.format_coins, String.valueOf(mQuestion.serviceFee().intValue())));
        }

        if (mQuestion.status().equals(Question.Status.PENDING_ANSWER)) {
            holder.statusImageView.setImageDrawable(null);
        } else if (mQuestion.status().equals(Question.Status.OPEN_FOR_FEEDBACK) || mQuestion.status().equals(Question.Status.HAS_FEEDBACK)) {
            holder.statusImageView.setImageResource(R.drawable.icon_require_feed_back);
        } else if (mQuestion.status().equals(Question.Status.OPEN_FOR_MORE_DETAILS) || mQuestion.status().equals(Question.Status.HAS_MORE_DETAILS)) {
            holder.statusImageView.setImageResource(R.drawable.icon_require_detail);
        } else if (mQuestion.status().equals(Question.Status.CLOSED)) {
            holder.statusImageView.setImageResource(R.drawable.icon_status_locked);
        } else {
            holder.statusImageView.setImageDrawable(null);
        }
    }

    @Override
    public boolean filter(Serializable constraint) {
        return mQuestion.subSubjectName().toLowerCase().contains(constraint.toString().toLowerCase());
    }

    static final class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.titleTextView)
        TextView subSubjectNameTextView;

        @BindView(R.id.descriptionTextView)
        TextView lawyerNameTextView;

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
