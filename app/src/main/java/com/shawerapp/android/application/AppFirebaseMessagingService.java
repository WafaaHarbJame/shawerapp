package com.shawerapp.android.application;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.shawerapp.android.R;
import com.shawerapp.android.events.PushNotifEvent;
import com.shawerapp.android.screens.container.ContainerActivity;
import com.shawerapp.android.utils.CommonUtils;
import com.shawerapp.android.utils.LoginUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by john.ernest on 1/3/18.
 */

public class AppFirebaseMessagingService extends FirebaseMessagingService {

    @Inject
    LoginUtil mLoginUtil;

    @Override
    public void onCreate() {
        ((ApplicationModel) getApplication()).getAppComponent()
                .inject(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (mLoginUtil.isLoggedIn() && CommonUtils.isNotEmpty(mLoginUtil.getPhoneNumber())) {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                Intent notificationIntent = new Intent(this, ContainerActivity.class);

                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent intent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(getString(R.string.app_name),
                            getString(R.string.app_name),
                            NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                }

                notificationManager.notify(0, new NotificationCompat.Builder(getApplicationContext(), getString(R.string.app_name))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setSmallIcon(R.mipmap.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_notification))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()))
                        .setContentIntent(intent)
                        .build());

                EventBus.getDefault().post(new PushNotifEvent());
            }
        }
    }
}
