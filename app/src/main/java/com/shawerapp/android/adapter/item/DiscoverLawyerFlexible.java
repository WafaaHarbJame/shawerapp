package com.shawerapp.android.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.LawyerUser;
import com.shawerapp.android.backend.glide.GlideApp;
import com.shawerapp.android.model.Country;
import com.shawerapp.android.utils.CommonUtils;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class DiscoverLawyerFlexible extends AbstractFlexibleItem<DiscoverLawyerFlexible.ViewHolder> implements IFilterable {

    private LawyerUser mLawyerUser;

    private String mCurrentUserId;

    public DiscoverLawyerFlexible(LawyerUser lawyerUser, String userID) {
        this.mLawyerUser = lawyerUser;
        mCurrentUserId = userID;
    }

    public LawyerUser getLawyerUser() {
        return mLawyerUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscoverLawyerFlexible that = (DiscoverLawyerFlexible) o;
        return Objects.equals(getLawyerUser(), that.getLawyerUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLawyerUser());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_discover_lawyer;
    }

    @Override
    public ViewHolder createViewHolder(View view,
                                       FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter,
                               ViewHolder holder, int position, List<Object> payloads) {
        Context context = holder.itemView.getContext();

        GlideApp.with(context)
                .clear(holder.avatarImageView);
        if (CommonUtils.isNotEmpty(mLawyerUser.imageUrl())) {
            GlideApp.with(context)
                    .load(mLawyerUser.imageUrl())
                    .placeholder(R.mipmap.icon_profile_default)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(holder.avatarImageView);
        }
        holder.lawyerNameTextView.setText(mLawyerUser.username());
        holder.numOfLikesTextView.setText(mLawyerUser.likes() != null ? String.valueOf(mLawyerUser.likes().size()) : "0");
        holder.yearsOfExperienceTextView.setText(CommonUtils.isNotEmpty(mLawyerUser.yearsOfExperience()) ? mLawyerUser.yearsOfExperience() : "");

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
        if (constraint instanceof String) {
            return CommonUtils.isNotEmpty(mLawyerUser.username()) && mLawyerUser.username().toLowerCase().contains(constraint.toString().toLowerCase());
        } else if (constraint instanceof Boolean) {
            if (mLawyerUser.favoritedBy() != null) {
                boolean isFavorited = mLawyerUser.favoritedBy().containsKey(mCurrentUserId) &&
                        mLawyerUser.favoritedBy().get(mCurrentUserId);
                return isFavorited == (Boolean) constraint;
            } else {
                return false;
            }
        } else if (constraint instanceof Country) {
            Country country = (Country) constraint;
            String countryValue = country.country;
            if (CommonUtils.isNotEmpty(countryValue)) {
                return CommonUtils.isNotEmpty(mLawyerUser.country()) && mLawyerUser.country().equalsIgnoreCase(countryValue);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public static final class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.iconImageView)
        public ImageView avatarImageView;

        @BindView(R.id.descriptionTextView)
        public TextView lawyerNameTextView;

        @BindView(R.id.numOfLikesTextView)
        public TextView numOfLikesTextView;

        @BindView(R.id.yearsOfExperienceTextView)
        public TextView yearsOfExperienceTextView;

        @BindView(R.id.onlineIndicatorImageView)
        public ImageView onlineIndicatorImageView;

        @BindView(R.id.favoriteIndicatorImageView)
        public ImageView favoriteIndicatorImageView;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
