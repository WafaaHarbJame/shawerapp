package com.shawerapp.android.screens.viewrequest;

import android.widget.LinearLayout;

import com.shawerapp.android.autovalue.PracticeRequest;
import com.shawerapp.android.base.FragmentLifecycle;

public class ViewCaseRequestContract {

    interface View extends FragmentLifecycle.View {
        void initBindings();

        void setSubSubjectName(String subSubjectName);

        void setRequestDate(String requestDate);

        void showOutboundContainer();

        void showOutboundMessage(String message);

        void showOutboundAudio();

        LinearLayout getOutboundAttachmentContainer();

        void setOutboundPlayButtonResource(int drawableRes);

        void setOutboundProgress(String progress);

        void showOutboundDownloading();

        void hideOutboundDownloading();

        void showInboundContainer();

        void setLawyerName(String lawyerName);

        void setLawyerContactNo(String lawyerName);

        void setLawyerAddress(String lawyerName);

        void setLawyerOfficeName(String lawyerName);

        void setShawerSpecialPromoCode(String lawyerName);

        void setStatus(String status);

        void startPlaybackVisualization(int audioSessionId);

        void stopPlaybackVisualization();
    }

    interface ViewModel extends FragmentLifecycle.ViewModel {

        void setupPracticeRequestDetails(PracticeRequest practiceRequest);

        void setupPracticeRequetsResponse(PracticeRequest practiceRequest);

        void onOutboundPlayButtonClicked();
    }
}
