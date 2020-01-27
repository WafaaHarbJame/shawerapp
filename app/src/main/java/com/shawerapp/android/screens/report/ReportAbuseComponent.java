package com.shawerapp.android.screens.report;

import com.shawerapp.android.base.FragmentScope;
import com.shawerapp.android.screens.container.ContainerComponent;
import dagger.Component;

@FragmentScope
@Component(modules = ReportAbuseModule.class, dependencies = ContainerComponent.class)
public interface ReportAbuseComponent {
  void inject(ReportAbuseFragment fragment);
}
