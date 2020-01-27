package com.shawerapp.android.adapter.item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.CoinPackage;
import com.shawerapp.android.autovalue.Invoice;
import com.shawerapp.android.utils.LocaleHelper;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import io.reactivex.Observable;

public class InvoiceFlexible extends AbstractFlexibleItem<InvoiceFlexible.ViewHolder> {

    private Invoice mInvoice;

    private Context mContext;


    public InvoiceFlexible(Invoice invoice, Context mContext) {
        this.mInvoice = invoice;
        this.mContext = mContext;
    }

    public Invoice getInvoice() {
        return mInvoice;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceFlexible that = (InvoiceFlexible) o;
        return Objects.equals(getInvoice(), that.getInvoice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInvoice());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_invoice;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder,
                               int position, List<Object> payloads) {
        holder.requestNumberText.setText(mInvoice.orderRequestNumber());
        if (LocaleHelper.getLanguage(mContext).equals("ar")) {
            holder.requestNumberTxt.setText("رقم الطلب");
        } else {
            holder.requestNumberTxt.setText("Request Number");
        }


    }

    static final class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.requestNumberText)
        TextView requestNumberText;

        @BindView(R.id.requestNumberTxt)
        TextView requestNumberTxt;


        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
