package com.shawerapp.android.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.CommercialUser;
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

public class ReportUserFlexible extends AbstractFlexibleItem<ReportUserFlexible.ViewHolder> implements IFilterable {

    private Object mUser;

    private String mCurrentUserUid;

    public ReportUserFlexible(Object user, String currentUserUid) {
        mUser = user;
        mCurrentUserUid = currentUserUid;
    }

    public Object getUser() {
        return mUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportUserFlexible that = (ReportUserFlexible) o;
        return Objects.equals(mUser, that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(mUser);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_report_user;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder,
                               int position, List<Object> payloads) {
        Context context = holder.itemView.getContext();

        String username;
        String imageUrl;
        boolean isFavorite = false;

        if (mUser instanceof LawyerUser) {
            LawyerUser lawyerUser = (LawyerUser) mUser;
            username = lawyerUser.username();
            imageUrl = lawyerUser.imageUrl();
            isFavorite = lawyerUser.favoritedBy() != null &&
                    lawyerUser.favoritedBy().containsKey(mCurrentUserUid) &&
                    lawyerUser.favoritedBy().get(mCurrentUserUid);
        } else if (mUser instanceof CommercialUser) {
            CommercialUser commercialUser = (CommercialUser) mUser;
            username = commercialUser.username();
            imageUrl = commercialUser.imageUrl();
        } else {
            IndividualUser individualUser = (IndividualUser) mUser;
            username = individualUser.username();
            imageUrl = individualUser.imageUrl();
        }

        holder.userNameTextView.setText(username);
        GlideApp.with(context)
                .clear(holder.avatarImageView);
        if (CommonUtils.isNotEmpty(imageUrl)) {
            GlideApp.with(context)
                    .load(imageUrl)
                    .placeholder(R.mipmap.icon_profile_default)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(holder.avatarImageView);
        }
        if (isFavorite) {
            holder.favoriteIndicatorImageView.setImageResource(R.drawable.icon_item_favorite_set);
        } else {
            holder.favoriteIndicatorImageView.setImageResource(R.drawable.icon_item_favorite_unset);
        }

        holder.radioButton.setChecked(adapter.isSelected(position));
    }

    @Override
    public boolean filter(Serializable constraint) {
        String name;

        if (mUser instanceof LawyerUser) {
            LawyerUser lawyerUser = (LawyerUser) mUser;
            name = lawyerUser.fullName();
        } else if (mUser instanceof CommercialUser) {
            CommercialUser commercialUser = (CommercialUser) mUser;
            name = commercialUser.fullName();
        } else {
            IndividualUser individualUser = (IndividualUser) mUser;
            name = individualUser.fullName();
        }

        return name.toLowerCase().contains(constraint.toString().toLowerCase());
    }

    public static final class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.userNameTextView)
        public TextView userNameTextView;

        @BindView(R.id.iconImageView)
        public ImageView avatarImageView;

        @BindView(R.id.favoriteIndicatorImageView)
        public ImageView favoriteIndicatorImageView;

        @BindView(R.id.mask)
        public View mask;

        @BindView(R.id.radioButton)
        public RadioButton radioButton;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
