package com.shawerapp.android.screens.loginbytype;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shawerapp.android.R;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.utils.AnimationUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginByTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_type);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnLoginAsIndividual)
    public void onLoginAsIndividualClicked() {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ContainerActivity.EXTRA_TYPE, ContainerActivity.TYPE_INDIVIDUAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
        AnimationUtils.overridePendingTransition(this, AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_RIGHT);
    }

    @OnClick(R.id.btnLoginAsCommercial)
    public void onLoginAsCommercialClicked() {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ContainerActivity.EXTRA_TYPE, ContainerActivity.TYPE_COMMERCIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
        AnimationUtils.overridePendingTransition(this, AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_RIGHT);
    }

    @OnClick(R.id.btnLoginAsLawyer)
    public void onLoginAsLawyerClicked() {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ContainerActivity.EXTRA_TYPE, ContainerActivity.TYPE_LAWYER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
        AnimationUtils.overridePendingTransition(this, AnimationUtils.ANIM_STYLE.SLIDE_IN_FROM_RIGHT);
    }
}
