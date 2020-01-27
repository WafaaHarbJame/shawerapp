package com.shawerapp.android.utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;

import com.shawerapp.android.R;

import java.util.List;

/**
 * Created by Ernest on 4/2/2015.
 */
public class AnimationUtils {
    public static final int COLOR_CHANGE_ANIM_DURATION = 500;

    public static final int VISIBILITY_ANIM_DURATION = 250;

    public static final int BOUNCE_ANIM_DURATION = 1000;

    public static final int TRANSLATE_ANIM_DURATION = 150;

    public static final int ROTATE_ANIM_DURATION = 1000;

    public enum ANIM_STYLE {
        SLIDE_IN_FROM_LEFT(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_right, R.anim.slide_out_to_left),
        SLIDE_IN_FROM_RIGHT(R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left, R.anim.slide_out_to_right),
        SLIDE_IN_FROM_RIGHT_ENTER(R.anim.slide_in_from_right, 0, R.anim.slide_in_from_left, 0),
        SLIDE_IN_FROM_BOTTOM(R.anim.slide_in_from_bottom, R.anim.fade_out, R.anim.slide_in_from_bottom, R.anim.fade_out),
        SLIDE_OUT_TO_BOTTOM(0, R.anim.slide_out_to_bottom, 0, R.anim.slide_out_to_bottom),
        ROTATE_FROM_RIGHT(R.anim.rotate_in, R.anim.rotate_out, R.anim.rotate_in, R.anim.rotate_out),
        FADE(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out),
        FADE_IN(R.anim.fade_in, 0, R.anim.fade_in, 0),
        FADE_OUT(0, R.anim.fade_out, 0, R.anim.fade_out),
        POP_IN(R.anim.pop_in, 0, R.anim.pop_in, 0),
        POP_OUT(0, R.anim.pop_out, 0, R.anim.pop_out);

        private int enterAnim = Integer.MIN_VALUE;

        private int enterRTLAnim = Integer.MIN_VALUE;

        private int exitAnim = Integer.MIN_VALUE;

        private int exitRTLAnim = Integer.MIN_VALUE;

        ANIM_STYLE(int enterAnim, int exitAnim, int enterRTLAnim, int exitRTLAnim) {
            this.enterAnim = enterAnim;
            this.exitAnim = exitAnim;
            this.enterRTLAnim = enterRTLAnim;
            this.exitRTLAnim = exitRTLAnim;
        }

        public int getEnterAnim() {
            return enterAnim;
        }

        public int getExitAnim() {
            return exitAnim;
        }

        public int getEnterRTLAnim() {
            return enterRTLAnim;
        }

        public int getExitRTLAnim() {
            return exitRTLAnim;
        }
    }

    public static void startActivityWithAnimations(AppCompatActivity activity, Intent intent, ANIM_STYLE animationStyle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle animation = ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle();
            ActivityCompat.startActivity(activity, intent, animation);
        } else {
            activity.startActivity(intent);
            activity.overridePendingTransition(animationStyle.getEnterAnim(), animationStyle.getExitAnim());
        }
    }

    public static void startActivityWithAnimationsForResult(Activity activity, Intent intent, ANIM_STYLE animationStyle, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle animation = ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle();
            ActivityCompat.startActivityForResult(activity, intent, requestCode, animation);
        } else {
            activity.startActivityForResult(intent, requestCode);
            activity.overridePendingTransition(animationStyle.getEnterAnim(), animationStyle.getExitAnim());
        }
    }

    public static void finishWithTransition(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityCompat.finishAfterTransition(activity);
        } else {
            activity.finish();
        }
    }

    public static void finishWithTransition(Activity activity, ANIM_STYLE animationStyle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityCompat.finishAfterTransition(activity);
        } else {
            activity.finish();
            overridePendingTransition(activity, animationStyle);
        }
    }

    public static void overridePendingTransition(Activity activity, ANIM_STYLE animationStyle) {
        activity.overridePendingTransition(!CommonUtils.isRTL() ? animationStyle.getEnterAnim() : animationStyle.getEnterRTLAnim(),
                !CommonUtils.isRTL() ? animationStyle.getExitAnim() : animationStyle.getExitRTLAnim());
    }

    public static void colorChangeAnimation(final AppCompatActivity activity, final View backgroundView, @ColorInt int statusBarToColorFromFragment, @ColorInt int backgroundToColorFromFragment) {
        // Initial colors of each system bar.
        final int backgroundColor = getBackgroundColor(backgroundView);

        // Desired final colors of each bar.
        final int statusBarToColor = statusBarToColorFromFragment;
        final int backgroundToColor = backgroundToColorFromFragment;

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();

                // Apply blended color to the status bar.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int statusBarColor = activity.getWindow().getStatusBarColor();
                    int statusBarBlended = blendColors(statusBarColor, statusBarToColor, position);
                    activity.getWindow().setStatusBarColor(statusBarBlended);
                }

                // Apply blended color to the ActionBar.
                int backgroundBlended = blendColors(backgroundColor, backgroundToColor, position);
                ColorDrawable background = new ColorDrawable(backgroundBlended);
                backgroundView.setBackgroundDrawable(background);
            }
        });

        anim.setDuration(COLOR_CHANGE_ANIM_DURATION).start();
    }

    public static Animator getColorChangeAnimation(View view, @ColorInt final int toColor, @ColorInt final int fromColor) {
        return getColorChangeAnimation(view, toColor, fromColor, COLOR_CHANGE_ANIM_DURATION);
    }

    public static Animator getColorChangeAnimation(View view, @ColorInt final int toColor, @ColorInt final int fromColor, int duration) {
        return getColorChangeAnimation(view, toColor, fromColor, duration, 0);
    }

    public static Animator getColorChangeAnimation(View view, @ColorInt final int toColor, @ColorInt final int fromColor, int duration, int delay) {
        return getColorChangeAnimation(view, toColor, fromColor, duration, delay, null);
    }

    public static Animator getColorChangeAnimation(final View view, @ColorInt final int toColor, @ColorInt final int fromColor, int duration, int delay, AnimatorListener listener) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();

                // Apply blended color to the ActionBar.
                int backgroundBlended = blendColors(fromColor, toColor, position);
                ColorDrawable background = new ColorDrawable(backgroundBlended);
                view.setBackgroundDrawable(background);
            }
        });
        if (listener != null) {
            anim.addListener(listener);
        }
        anim.setDuration(duration);
        anim.setStartDelay(delay);
        return anim;
    }

    private static Bitmap mBitmap;

    private static Canvas mCanvas;

    private static Rect mBounds;

    public static void initIfNeeded() {
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mBounds = new Rect();
        }
    }

    public static int getBackgroundColor(View view) {
        // The actual color, not the id.
        int color = Color.WHITE;

        if (view.getBackground() instanceof ColorDrawable) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                initIfNeeded();

                // If the ColorDrawable makes use of its bounds in the draw method,
                // we may not be able to get the color we want. This is not the usual
                // case before Ice Cream Sandwich (4.0.1 r1).
                // Yet, we change the bounds temporarily, just to be sure that we are
                // successful.
                ColorDrawable colorDrawable = (ColorDrawable) view.getBackground();

                mBounds.set(colorDrawable.getBounds()); // Save the original bounds.
                colorDrawable.setBounds(0, 0, 1, 1); // Change the bounds.

                colorDrawable.draw(mCanvas);
                color = mBitmap.getPixel(0, 0);

                colorDrawable.setBounds(mBounds); // Restore the original bounds.
            } else {
                color = ((ColorDrawable) view.getBackground()).getColor();
            }
        }

        return color;
    }

    public static int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }

    public static ObjectAnimator getVisibilityAnimator(View view, boolean isFadeIn, int animEndVisibility) {
        return getVisibilityAnimator(view, isFadeIn, animEndVisibility, VISIBILITY_ANIM_DURATION);
    }

    public static ObjectAnimator getVisibilityAnimator(View view, boolean isFadeIn, int animEndVisibility, int duration) {
        return getVisibilityAnimator(view, isFadeIn, animEndVisibility, duration, 0);
    }

    public static ObjectAnimator getVisibilityAnimator(final View view, final boolean isFadeIn, final int animEndVisibility, int duration, int delay) {
        if ((view.getVisibility() == View.VISIBLE && isFadeIn)
                || (view.getVisibility() == View.GONE && !isFadeIn)) {
            return null;
        }

        ObjectAnimator visibilityAnimator = ObjectAnimator.ofFloat(view, "alpha", isFadeIn ? 0f : 1f, isFadeIn ? 1f : 0f);
        visibilityAnimator.setDuration(duration);
        visibilityAnimator.setStartDelay(delay);

        final boolean isOriginallyClickable = view.isClickable();

        visibilityAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isOriginallyClickable) {
                    view.setClickable(false);
                }
                if ((view.getVisibility() == View.GONE || view.getVisibility() == View.INVISIBLE) && isFadeIn) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isOriginallyClickable) {
                    view.setClickable(true);
                }
                if (view.getVisibility() == View.VISIBLE && !isFadeIn) {
                    view.setVisibility(animEndVisibility);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return visibilityAnimator;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Animator getCircularRevealAnimator(View view, boolean isReveal, int animEndVisibility) {
        return getCircularRevealAnimator(view, isReveal, animEndVisibility, VISIBILITY_ANIM_DURATION);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Animator getCircularRevealAnimator(View view, boolean isReveal, int animEndVisibility, int duration) {
        return getCircularRevealAnimator(view, isReveal, animEndVisibility, duration, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Animator getCircularRevealAnimator(final View view, final boolean isReveal, final int animEndVisibility, int duration, int delay) {
        // get the center for the clipping circle
        int cx = view.getMeasuredWidth() / 2;
        int cy = view.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = view.getWidth() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator circularRevealAnim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
                isReveal ? 0 : initialRadius, isReveal ? finalRadius : 0);

        final boolean isOriginallyClickable = view.isClickable();

        circularRevealAnim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isOriginallyClickable) {
                    view.setClickable(false);
                }

                if ((view.getVisibility() == View.GONE || view.getVisibility() == View.INVISIBLE) && isReveal) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isOriginallyClickable) {
                    view.setClickable(true);
                }
                if (view.getVisibility() == View.VISIBLE && !isReveal) {
                    view.setVisibility(animEndVisibility);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return circularRevealAnim;
    }

    public static ObjectAnimator getBounceAnimator(View view) {
        return getBounceAnimator(view, BOUNCE_ANIM_DURATION);
    }

    public static ObjectAnimator getBounceAnimator(View view, int duration) {
        return getBounceAnimator(view, duration, 0);
    }

    public static ObjectAnimator getBounceAnimator(View view, int duration, long delay) {
        ObjectAnimator bounceAnimator = ObjectAnimator.ofFloat(view, "translationY", -200, 0);
        bounceAnimator.setInterpolator(new BounceInterpolator());
        bounceAnimator.setDuration(duration);
        bounceAnimator.setStartDelay(delay);

        return bounceAnimator;
    }

    public static ObjectAnimator getTranslateYAnimator(View view, float translateFrom, float translateTo) {
        return getTranslateYAnimator(view, translateFrom, translateTo, TRANSLATE_ANIM_DURATION);
    }

    public static ObjectAnimator getTranslateYAnimator(View view, float translateFrom, float translateTo, int duration) {
        return getTranslateYAnimator(view, translateFrom, translateTo, duration, 0);
    }

    public static ObjectAnimator getTranslateYAnimator(View view, float translateFrom, float translateTo, int duration, long delay) {
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(view, "translationY", translateFrom, translateTo);
        translateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimator.setDuration(duration);
        translateAnimator.setStartDelay(delay);
        return translateAnimator;
    }

    public static ObjectAnimator getTranslateXAnimator(View view, float translateFrom, float translateTo) {
        return getTranslateXAnimator(view, translateFrom, translateTo, TRANSLATE_ANIM_DURATION);
    }

    public static ObjectAnimator getTranslateXAnimator(View view, float translateFrom, float translateTo, int duration) {
        return getTranslateXAnimator(view, translateFrom, translateTo, duration, 0);
    }

    public static ObjectAnimator getTranslateXAnimator(View view, float translateFrom, float translateTo, int duration, long delay) {
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(view, "translationX", translateFrom, translateTo);
        translateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimator.setDuration(duration);
        translateAnimator.setStartDelay(delay);
        return translateAnimator;
    }

    public static ObjectAnimator getScaleAnimator(View view, float scaleFrom, float scaleTo) {
        return getScaleAnimator(view, scaleFrom, scaleTo, TRANSLATE_ANIM_DURATION);
    }

    public static ObjectAnimator getScaleAnimator(View view, float scaleFrom, float scaleTo, int duration) {
        return getScaleAnimator(view, scaleFrom, scaleTo, duration, 0);
    }

    public static ObjectAnimator getScaleAnimator(View view, float scaleFrom, float scaleTo, int duration, long delay) {
        ObjectAnimator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", scaleFrom, scaleTo),
                PropertyValuesHolder.ofFloat("scaleY", scaleFrom, scaleTo));
        scaleAnimator.setInterpolator(new AccelerateInterpolator());
        scaleAnimator.setDuration(duration);
        scaleAnimator.setStartDelay(delay);
        return scaleAnimator;
    }

    public static ObjectAnimator getRotateAnimator(final View view, float fromRotateValue, float toRotateValue) {
        return getRotateAnimator(view, fromRotateValue, toRotateValue, ROTATE_ANIM_DURATION);
    }

    public static ObjectAnimator getRotateAnimator(final View view, float fromRotateValue, float toRotateValue, int duration) {
        return getRotateAnimator(view, fromRotateValue, toRotateValue, duration, 0);
    }

    public static ObjectAnimator getRotateAnimator(final View view, float fromRotateValue, float toRotateValue, int duration, int delay) {
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(view, "rotation", fromRotateValue, toRotateValue);
        rotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateAnimator.setDuration(duration);
        rotateAnimator.setStartDelay(delay);

        final boolean isOriginallyClickable = view.isClickable();

        rotateAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isOriginallyClickable) {
                    view.setClickable(false);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isOriginallyClickable) {
                    view.setClickable(true);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return rotateAnimator;
    }

    public static void playAnimationSequentially(List<Animator> animators) {
        playAnimationSequentially(null, animators);
    }

    public static void playAnimationSequentially(AnimatorListener listener, List<Animator> animators) {
        AnimatorSet animatorSet = new AnimatorSet();
        if (listener != null) {
            animatorSet.addListener(listener);
        }
        animatorSet.playSequentially(animators);
        animatorSet.start();
    }

    public static void playAnimationSequentially(Animator... animators) {
        playAnimationSequentially(null, animators);
    }

    public static void playAnimationSequentially(AnimatorListener listener, Animator... animators) {
        AnimatorSet animatorSet = new AnimatorSet();
        if (listener != null) {
            animatorSet.addListener(listener);
        }
        animatorSet.playSequentially(animators);
        animatorSet.start();
    }

    public static void playAnimationTogether(Animator... animators) {
        playAnimationTogether(null, animators);
    }

    public static void playAnimationTogether(AnimatorListener listener, Animator... animators) {
        playAnimationTogether(listener, 0, animators);
    }

    public static void playAnimationTogether(AnimatorListener listener, int startDelay, Animator... animators) {
        AnimatorSet animatorSet = new AnimatorSet();
        if (listener != null) {
            animatorSet.addListener(listener);
        }
        animatorSet.setStartDelay(startDelay);
        animatorSet.playTogether(animators);
        animatorSet.start();
    }
}
