package com.shawerapp.android.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.Field;
import com.shawerapp.android.utils.LocaleHelper;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class FieldFlexible extends AbstractFlexibleItem<FieldFlexible.ViewHolder> implements IFilterable {

    private final Field mField;

    public FieldFlexible(Field field) {
        this.mField = field;
    }

    public Field getField() {
        return mField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldFlexible that = (FieldFlexible) o;
        return Objects.equals(getField(), that.getField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getField());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_field;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder,
                               int position, List<Object> payloads) {
        final Context context = holder.itemView.getContext();
        final String subjectCount =
                context.getString(R.string.label_item_sub_subject_count, mField.subSubjectCount());
        if (LocaleHelper.getLanguage(context).equalsIgnoreCase(LocaleHelper.ARABIC)) {
            holder.titleTextView.setText(mField.ar_fieldName());
            holder.descriptionTextView.setText(mField.ar_description());
        } else {
            holder.titleTextView.setText(mField.fieldName());
            holder.descriptionTextView.setText(mField.description());
        }
        holder.countTextView.setText(subjectCount);
    }

    @Override
    public boolean filter(Serializable constraint) {
        return mField.fieldName().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                mField.description().toLowerCase().contains(constraint.toString().toLowerCase());
    }

    static final class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.titleTextView)
        TextView titleTextView;

        @BindView(R.id.descriptionTextView)
        TextView descriptionTextView;

        @BindView(R.id.subjectCountTextView)
        TextView countTextView;

        public ViewHolder(View view,
                          FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
