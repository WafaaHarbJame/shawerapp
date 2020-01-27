package com.shawerapp.android.adapter.item;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.LawyerFile;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;

public class LawyerFileFlexible extends AbstractFlexibleItem<LawyerFileFlexible.ViewHolder> {

    private LawyerFile mLawyerFile;

    public LawyerFileFlexible(LawyerFile lawyerFile) {
        mLawyerFile = lawyerFile;
    }

    public LawyerFile getLawyerFile() {
        return mLawyerFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LawyerFileFlexible that = (LawyerFileFlexible) o;
        return Objects.equals(getLawyerFile(), that.getLawyerFile());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getLawyerFile());
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_lawyer_file;
    }

    @Override
    public ViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ViewHolder holder, int position, List<Object> payloads) {
        Context context = holder.itemView.getContext();

        holder.fileName.setText(mLawyerFile.fileName());
        holder.description.setText(mLawyerFile.description());
        holder.radioButton.setChecked(adapter.isSelected(position));
    }

    public class ViewHolder extends FlexibleViewHolder {

        @BindView(R.id.radioButton)
        public RadioButton radioButton;

        @BindView(R.id.fileName)
        public TextView fileName;

        @BindView(R.id.description)
        public TextView description;

        @BindView(R.id.btnView)
        public Button btnView;

        @BindView(R.id.clickableView)
        public View clickableView;

        public ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            ButterKnife.bind(this, view);
        }
    }
}
