package com.shawerapp.android.screens.newrequest.step1;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.shawerapp.android.R;
import com.shawerapp.android.adapter.FieldAdapter;
import com.shawerapp.android.adapter.item.FieldFlexible;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.screens.container.ContainerActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SelectRequestFieldFragment extends BaseFragment implements SelectRequestFieldContract.View {

    public static SelectRequestFieldFragment newInstance() {
        return new SelectRequestFieldFragment();
    }

    @Inject
    SelectRequestFieldContract.ViewModel viewModel;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private FieldAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_request, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        DaggerSelectRequestFieldComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .selectRequestFieldModule(new SelectRequestFieldModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void initBindings() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final List<FieldFlexible> fieldFlexibleList = new ArrayList<>();

        adapter = new FieldAdapter(fieldFlexibleList, this);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onItemClick(View view, int position) {
        final FieldFlexible item = adapter.getItem(position);
        if (item != null) {
            viewModel.onFieldClicked(item.getField());
        }

        return false;
    }
}