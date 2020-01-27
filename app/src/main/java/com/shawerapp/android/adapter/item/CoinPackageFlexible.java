package com.shawerapp.android.adapter.item;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.CoinPackage;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

import java.util.List;
import java.util.Objects;

public class CoinPackageFlexible extends AbstractFlexibleItem<CoinPackageFlexible.ViewHolder> {

    private CoinPackage mCoinPackage;

    private String mAmount;

    public CoinPackageFlexible(CoinPackage coinPackage, String amount) {
        this.mCoinPackage = coinPackage;
        mAmount = amount;
    }

    public CoinPackage getCoinPackage() {
        return mCoinPackage;
    }

    public String getAmount() {
        return mAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoinPackageFlexible that = (CoinPackageFlexible) o;
        return Objects.equals(getCoinPackage(), that.getCoinPackage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCoinPackage());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_coin_package;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder,
                               int position, List<Object> payloads) {
        holder.titleTextView.setText(mCoinPackage.title());
        holder.descriptionTextView.setText(mCoinPackage.description());
        holder.amountTextView.setText(mAmount);
    }

    static final class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.titleTextView)
        TextView titleTextView;

        @BindView(R.id.descriptionTextView)
        TextView descriptionTextView;

        @BindView(R.id.amountTextView)
        TextView amountTextView;

        @BindView(R.id.iconImageView)
        ImageView iconImageView;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
