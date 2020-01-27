package com.shawerapp.android.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.IndividualUser;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.backend.glide.GlideApp;
import com.shawerapp.android.utils.CommonUtils;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class HireableLawyerFlexible extends AbstractFlexibleItem<HireableLawyerFlexible.ViewHolder> implements IFilterable {

    private final LawyerUser mLawyerUser;

    private String mCurrentUserRole;

    private String mSubSubjectUid;

    private String mCurrentUserId;

    public HireableLawyerFlexible(LawyerUser lawyerUser, String currentUserRole, String subSubjectUid, String userID) {
        mLawyerUser = lawyerUser;
        mCurrentUserRole = currentUserRole;
        mSubSubjectUid = subSubjectUid;
        mCurrentUserId = userID;
    }

    public LawyerUser getLawyerUser() {
        return mLawyerUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HireableLawyerFlexible that = (HireableLawyerFlexible) o;
        return Objects.equals(getLawyerUser(), that.getLawyerUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLawyerUser());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_hireable_lawyer;
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

        holder.titleTextView.setText(mLawyerUser.username());
        holder.numOfLikesTextView.setText(String.valueOf(mLawyerUser.likes() != null ? mLawyerUser.likes().size() : "0"));
        holder.yearsOfExperienceTextView.setText(CommonUtils.isNotEmpty(mLawyerUser.yearsOfExperience()) ? mLawyerUser.yearsOfExperience() : "");

        if (mCurrentUserRole.equals(IndividualUser.ROLE_VALUE)) {
            String cost = String.valueOf(mLawyerUser.individualFees() != null ? mLawyerUser.individualFees().get(mSubSubjectUid).intValue() : 0);
            holder.costTextView.setText(context.getString(R.string.format_coins, cost));
        } else {
            String cost = String.valueOf(mLawyerUser.commercialFees() != null ? mLawyerUser.commercialFees().get(mSubSubjectUid).intValue() : 0);
            holder.costTextView.setText(context.getString(R.string.format_coins, cost));
        }

        GlideApp.with(holder.itemView.getContext())
                .clear(holder.iconImageView);
        if (CommonUtils.isNotEmpty(mLawyerUser.imageUrl())) {
            GlideApp.with(holder.itemView.getContext())
                    .load(mLawyerUser.imageUrl())
                    .placeholder(R.mipmap.icon_profile_default)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(holder.iconImageView);
        }

        if (mLawyerUser.favoritedBy() != null) {
            boolean isFavorited = mLawyerUser.favoritedBy().containsKey(mCurrentUserId) &&
                    mLawyerUser.favoritedBy().get(mCurrentUserId);
            if (isFavorited) {
                holder.favoriteIndicatorImageView.setImageResource(R.drawable.icon_item_favorite_set);
            } else {
                holder.favoriteIndicatorImageView.setImageResource(R.drawable.icon_item_favorite_unset);
            }
        } else {
            holder.favoriteIndicatorImageView.setImageResource(R.drawable.icon_item_favorite_unset);
        }

        if (CommonUtils.isNotEmpty(mLawyerUser.presence()) && mLawyerUser.presence().equals("online")) {
            holder.onlineIndicatorImageView.setImageResource(R.drawable.icon_status_online);
        } else {
            holder.onlineIndicatorImageView.setImageResource(R.drawable.icon_status_offline);
        }
    }

    @Override
    public boolean filter(Serializable constraint) {
        return CommonUtils.isNotEmpty(mLawyerUser.username()) && mLawyerUser.username().toLowerCase().contains(constraint.toString().toLowerCase());
    }

    static final class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.iconImageView)
        public ImageView iconImageView;

        @BindView(R.id.titleTextView)
        public TextView titleTextView;

        @BindView(R.id.numOfLikesTextView)
        public TextView numOfLikesTextView;

        @BindView(R.id.yearsOfExperienceTextView)
        public TextView yearsOfExperienceTextView;

        @BindView(R.id.feeTextView)
        public TextView costTextView;

        @BindView(R.id.favoriteIndicatorImageView)
        public ImageView favoriteIndicatorImageView;

        @BindView(R.id.onlineIndicatorImageView)
        public ImageView onlineIndicatorImageView;

        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
