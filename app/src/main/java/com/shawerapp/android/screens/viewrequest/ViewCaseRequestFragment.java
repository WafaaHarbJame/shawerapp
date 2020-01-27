package com.shawerapp.android.screens.viewrequest;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.jakewharton.rxbinding2.view.RxView;
import com.shawerapp.android.R;
import com.shawerapp.android.autovalue.PracticeRequest;
import com.shawerapp.android.base.BaseFragment;
import com.shawerapp.android.base.FragmentLifecycle;
import com.shawerapp.android.custom.views.CustomLineBarVisualizer;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.screens.container.ContainerContract;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class ViewCaseRequestFragment extends BaseFragment implements ViewCaseRequestContract.View {

    public static final String ARG_PRACTICE_REQUEST = "practiceRequest";

    public static ViewCaseRequestFragment newInstance(PracticeRequest practiceRequest) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_PRACTICE_REQUEST, practiceRequest);
        ViewCaseRequestFragment fragment = new ViewCaseRequestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    ViewCaseRequestContract.ViewModel mViewModel;

    @Inject
    ContainerContract.View mContainerView;

    @BindView(R.id.subSubjectNameTextView)
    TextView mSubSubjectNameTextView;

    @BindView(R.id.requestDate)
    TextView mRequestDate;

    @BindView(R.id.outbound)
    View mOutboundContainer;

    @BindView(R.id.outboundMessageContainer)
    View mOutboundMessageContainer;

    @BindView(R.id.outboundMessage)
    TextView mOutboundMessage;

    @BindView(R.id.outboundVoiceContainer)
    View mOutboundVoiceContainer;

    @BindView(R.id.outboundPlayPauseImageView)
    ImageButton mOutboundPlayPauseImageView;

    @BindView(R.id.outboundAudioDownloading)
    ContentLoadingProgressBar mOutboundAudioDownloading;

    @BindView(R.id.outboundVoiceProgressBar)
    CustomLineBarVisualizer mOutboundVoiceProgressBar;

    @BindView(R.id.outboundVoiceDurationTextView)
    TextView mOutboundVoiceDurationTextView;

    @BindView(R.id.outboundAttachmentContainer)
    LinearLayout mOutboundAttachmentContainer;

    @BindView(R.id.responseDate)
    TextView mResponseDate;

    @BindView(R.id.inbound)
    View mInboundContainer;

    @BindView(R.id.inboundMessageContainer)
    View mInboundMessageContainer;

    @BindView(R.id.lawyerName)
    TextView mLawyerName;

    @BindView(R.id.lawyerContactNo)
    TextView mLawyerContactNo;

    @BindView(R.id.lawyerAddress)
    TextView mLawyerAddress;

    @BindView(R.id.lawyerOfficeName)
    TextView mLawyerOfficeName;

    @BindView(R.id.shawerSpecialPromoCode)
    TextView mShawerSpecialPromoCode;

    @BindView(R.id.status)
    TextView mStatus;

    private RxPermissions mPermissions;

    @Override
    protected FragmentLifecycle.ViewModel getViewModel() {
        return mViewModel;
    }

    @Override
    public void onAttach(Context context) {
        DaggerViewCaseRequestComponent.builder()
                .containerComponent(((ContainerActivity) context).getContainerComponent())
                .viewCaseRequestModule(new ViewCaseRequestModule(this, this))
                .build()
                .inject(this);
        super.onAttach(context);
    }

    @Override
    public void initBindings() {
        mPermissions = new RxPermissions(getActivity());

        mOutboundVoiceProgressBar.setColor(ContextCompat.getColor(getContext(), R.color.yankeesBlue));
        mOutboundVoiceProgressBar.setDensity(45f);

        RxView.clicks(mOutboundPlayPauseImageView)
                .throttleFirst(1, TimeUnit.SECONDS)
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .compose(mPermissions.ensure(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe(isGranted -> {
                    if (isGranted) {
                        mViewModel.onOutboundPlayButtonClicked();
                    } else {
                        mContainerView.showMessage(getString(R.string.error_permission), true);
                    }
                });

    }

    @Override
    public void setSubSubjectName(String subSubjectName) {
        mSubSubjectNameTextView.setText(subSubjectName);
    }

    @Override
    public void setRequestDate(String date) {
        mRequestDate.setText(date);
    }

    @Override
    public void showOutboundContainer() {
        mOutboundContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showOutboundMessage(String message) {
        mOutboundMessageContainer.setVisibility(View.VISIBLE);
        mOutboundMessage.setText(message);
    }

    @Override
    public void showOutboundAudio() {
        mOutboundVoiceContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public LinearLayout getOutboundAttachmentContainer() {
        return mOutboundAttachmentContainer;
    }

    @Override
    public void setOutboundPlayButtonResource(int drawableRes) {
        mOutboundPlayPauseImageView.setImageResource(drawableRes);
    }

    @Override
    public void setOutboundProgress(String progress) {
        mOutboundVoiceDurationTextView.setText(progress);
    }

    @Override
    public void showOutboundDownloading() {
        mOutboundAudioDownloading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideOutboundDownloading() {
        mOutboundAudioDownloading.setVisibility(View.GONE);
    }

    @Override
    public void showInboundContainer() {
        mInboundContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void setLawyerName(String lawyerName) {
        mLawyerName.setText(lawyerName);
    }

    @Override
    public void setLawyerContactNo(String lawyerContactNo) {
        mLawyerContactNo.setText(lawyerContactNo);
    }

    @Override
    public void setLawyerAddress(String lawyerAddress) {
        mLawyerAddress.setText(lawyerAddress);
    }

    @Override
    public void setLawyerOfficeName(String lawyerOfficeName) {
        mLawyerOfficeName.setText(lawyerOfficeName);
    }

    @Override
    public void setShawerSpecialPromoCode(String shawerSpecialPromoCode) {
        mShawerSpecialPromoCode.setText(shawerSpecialPromoCode);
    }

    @Override
    public void setStatus(String statusMessage) {
        mStatus.setText(statusMessage);
    }

    @Override
    public void startPlaybackVisualization(int audioSessionId) {
        mOutboundVoiceProgressBar.setPlayer(audioSessionId);
    }

    @Override
    public void stopPlaybackVisualization() {
        mOutboundVoiceProgressBar.release();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_case_request, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }
}
