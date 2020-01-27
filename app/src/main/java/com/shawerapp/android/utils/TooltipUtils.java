package com.shawerapp.android.utils;

import android.content.Context;
import android.view.View;

import com.shawerapp.android.R;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.operators.single.SingleCreate;
import io.reactivex.internal.operators.single.SingleDefer;
import it.sephiroth.android.library.tooltip.Tooltip;

/**
 * Created by john.ernest on 02/10/2017.
 */

public class TooltipUtils {

    private static final String PREFS_TOOLTIPS = "tooltips";

    public static final int SHOT_ASK_QUESTION = 1001;

    public static final int SHOT_REQUEST_LAWYER = 1002;

    private static boolean hasShot(Context context, int shotId) {
        return context
                .getSharedPreferences(PREFS_TOOLTIPS, Context.MODE_PRIVATE)
                .getBoolean("hasShot" + shotId, false);
    }

    public static void setShot(Context context, int shotId) {
        context.getSharedPreferences(PREFS_TOOLTIPS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean("hasShot" + shotId, true)
                .apply();
    }

    public static Single<Boolean> show(Context context, int shotId, View target, Tooltip.Gravity gravity, CharSequence message) {
        return new SingleDefer<Boolean>(() ->
                new SingleCreate<>(emitter -> {
                    if (hasShot(context, shotId)) {
                        emitter.onSuccess(false);
                    } else {
                        if (target != null) {
                            Tooltip
                                    .make(context,
                                            new Tooltip.Builder(shotId)
                                                    .anchor(target, gravity)
                                                    .closePolicy(new Tooltip.ClosePolicy()
                                                            .insidePolicy(true, true)
                                                            .outsidePolicy(true, false), 0)
                                                    .showDelay(300)
                                                    .text(message)
                                                    .withArrow(true)
                                                    .withStyleId(R.style.TooltipTheme)
                                                    .floatingAnimation(null)
                                                    .withCallback(new Tooltip.Callback() {
                                                        @Override
                                                        public void onTooltipClose(Tooltip.TooltipView tooltipView, boolean fromUser, boolean containsTouch) {
                                                            if (containsTouch) {
                                                                setShot(context, shotId);
                                                                emitter.onSuccess(true);
                                                            } else {
                                                                emitter.onSuccess(false);
                                                            }
                                                        }

                                                        @Override
                                                        public void onTooltipFailed(Tooltip.TooltipView tooltipView) {
                                                            emitter.onError(new Throwable(context.getString(R.string.title_error)));
                                                        }

                                                        @Override
                                                        public void onTooltipShown(Tooltip.TooltipView tooltipView) {

                                                        }

                                                        @Override
                                                        public void onTooltipHidden(Tooltip.TooltipView tooltipView) {

                                                        }
                                                    })
                                                    .build())
                                    .show();
                        } else {
                            emitter.onSuccess(false);
                        }
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread());
    }
}
