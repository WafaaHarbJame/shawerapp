package com.shawerapp.android.screens.questiondetails;

import android.app.Fragment;

import com.shawerapp.android.adapter.AnswerAdapter;
import com.shawerapp.android.backend.base.FileFramework;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentScope;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

@Module
public class QuestionDetailsModule {

    private BaseFragment mFragment;

    private QuestionDetailsContract.View mView;

    public QuestionDetailsModule(BaseFragment fragment, QuestionDetailsContract.View view) {
        mFragment = fragment;
        mView = view;
    }

    @FragmentScope
    @Provides
    public BaseFragment providesFragment() {
        return mFragment;
    }

    @FragmentScope
    @Provides
    public QuestionDetailsContract.View providesView() {
        return mView;
    }

    @FragmentScope
    @Provides
    public QuestionDetailsContract.ViewModel providesViewModel(QuestionDetailsViewModel viewModel) {
        return viewModel;
    }

    @FragmentScope
    @Provides
    public AnswerAdapter providesAnswerAdapter(BaseFragment fragment, FileFramework fileFramework) {
        return new AnswerAdapter(new ArrayList<>(), fragment.getContext(), fileFramework);
    }
}
