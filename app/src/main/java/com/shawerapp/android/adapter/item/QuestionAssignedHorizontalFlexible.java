package com.shawerapp.android.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.utils.LocaleHelper;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class QuestionAssignedHorizontalFlexible extends AbstractFlexibleItem<QuestionAssignedHorizontalFlexible.ViewHolder> {

    private Question mQuestion;

    public QuestionAssignedHorizontalFlexible(Question question) {
        mQuestion = question;
    }

    public Question getQuestion() {
        return mQuestion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionAssignedHorizontalFlexible that = (QuestionAssignedHorizontalFlexible) o;
        return Objects.equals(getQuestion(), that.getQuestion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuestion());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_pending_tasks;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder, int position, List<Object> payloads) {
        Context context = holder.itemView.getContext();

        if (LocaleHelper.getLanguage(context).equalsIgnoreCase(LocaleHelper.ARABIC)) {
            holder.fieldName.setText(mQuestion.ar_fieldName());
            holder.subSubjectName.setText(mQuestion.ar_subSubjectName());
        } else {
            holder.fieldName.setText(mQuestion.fieldName());
            holder.subSubjectName.setText(mQuestion.subSubjectName());
        }
        holder.serviceFee.setText(context.getString(R.string.format_coins, String.valueOf(mQuestion.serviceFee().intValue())));
    }

    public class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.fieldName)
        TextView fieldName;

        @BindView(R.id.subSubjectName)
        TextView subSubjectName;

        @BindView(R.id.serviceFee)
        TextView serviceFee;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
