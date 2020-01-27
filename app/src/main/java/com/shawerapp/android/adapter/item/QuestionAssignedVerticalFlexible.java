package com.shawerapp.android.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.CommercialUser;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.Question;
import com.shawerapp.android.backend.glide.GlideApp;
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

public class QuestionAssignedVerticalFlexible extends AbstractFlexibleItem<QuestionAssignedVerticalFlexible.ViewHolder> implements IFilterable {

    private Question mQuestion;

    public QuestionAssignedVerticalFlexible(Question question) {
        mQuestion = question;
    }

    public Question getQuestion() {
        return mQuestion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionAssignedVerticalFlexible that = (QuestionAssignedVerticalFlexible) o;
        return Objects.equals(mQuestion, that.mQuestion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mQuestion);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_lawyer_task;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder, int position, List<Object> payloads) {
        Context context = holder.itemView.getContext();

        GlideApp.with(context).clear(holder.iconImageView);
        if (CommonUtils.isNotEmpty(mQuestion.askerImageUrl())) {
            GlideApp.with(context)
                    .load(mQuestion.askerImageUrl())
                    .placeholder(R.mipmap.icon_profile_default)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(holder.iconImageView);
        } else {
            holder.iconImageView.setImageResource(R.mipmap.icon_profile_default);
        }

        if (LocaleHelper.getLanguage(context).equalsIgnoreCase(LocaleHelper.ARABIC)) {
            holder.titleTextView.setText(mQuestion.ar_subSubjectName());
        } else {
            holder.titleTextView.setText(mQuestion.subSubjectName());
        }
        holder.issuerNameTextView.setText(mQuestion.askerUsername());
        String role = "";
        if (mQuestion.askerRole().equalsIgnoreCase(IndividualUser.ROLE_VALUE)) {
            role = context.getString(R.string.individual);
        } else if (mQuestion.askerRole().equalsIgnoreCase(CommercialUser.ROLE_VALUE)) {
            role = context.getString(R.string.commercial);
        }
        holder.issuerTypeTextView.setText(role);
        holder.feeTextView.setText(context.getString(R.string.format_coins, String.valueOf(mQuestion.serviceFee())));

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

        @BindView(R.id.iconImageView)
        ImageView iconImageView;

        @BindView(R.id.titleTextView)
        TextView titleTextView;

        @BindView(R.id.issuerNameTextView)
        TextView issuerNameTextView;

        @BindView(R.id.issuerTypeTextView)
        TextView issuerTypeTextView;

        @BindView(R.id.statusImageView)
        ImageView statusImageView;

        @BindView(R.id.feeTextView)
        TextView feeTextView;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
