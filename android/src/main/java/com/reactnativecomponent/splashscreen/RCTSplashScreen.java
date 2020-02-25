package com.reactnativecomponent.splashscreen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;


public class RCTSplashScreen {

    private static int recLen = 5;//跳过倒计时提示5秒

    public static final int UIAnimationNone = 0;
    public static final int UIAnimationFade = 1;
    public static final int UIAnimationScale = 2;

    public static final String CloseSplashScreen = "closeSplashScreen";

    private static Dialog dialog;

    private static WeakReference<Activity> wr_activity;

    protected static Activity getActivity() {
        return wr_activity.get();
    }

    public static void openSplashScreen(final Activity activity, final boolean isFullScreen) {
        if (activity == null) return;
        wr_activity = new WeakReference<>(activity);
        if ((dialog != null && dialog.isShowing())) {
            return;
        }

        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (!getActivity().isFinishing()) {
                    Context context = getActivity();


                    dialog = new Dialog(context, isFullScreen ? android.R.style.Theme_Translucent_NoTitleBar_Fullscreen : android.R.style.Theme_Translucent_NoTitleBar);
                    dialog.setContentView(R.layout.activity_splash_screen);
                    dialog.setCancelable(false);
                    dialog.show();

                    final Handler handler = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            // 正常情况下不点击跳过
                            closeSplashScreen(getActivity());
                        }
                    };

                    final Button btn = dialog.findViewById(R.id.btnTime);
                    btn.setText(recLen + " 跳过");
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 点击跳过
                            if (runnable != null) {
                                handler.removeCallbacks(runnable);
                            }
                            closeSplashScreen(getActivity());
                        }
                    });

                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recLen--;
                                    if (recLen < 0) {
                                        timer.cancel();
                                        // 倒计时到0隐藏字体
                                        btn.setVisibility(View.GONE);
                                    } else {
                                        btn.setText(recLen + " 跳过");
                                    }
                                }
                            });
                        }
                    }, 1000, 1000);

                    // 正常情况下不点击跳过
                    handler.postDelayed(runnable, 5000);//延迟5S后发送handler信息
                }

            }
        });
    }

    public static void removeSplashScreen(Activity activity, final int animationType, final int duration) {
        if (activity == null) {
            activity = getActivity();
            if (activity == null) return;
        }
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (dialog != null && dialog.isShowing()) {
                    AnimationSet animationSet = new AnimationSet(true);

                    if (animationType == UIAnimationScale) {
                        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setDuration(duration);
                        animationSet.addAnimation(fadeOut);

                        ScaleAnimation scale = new ScaleAnimation(1, 1.5f, 1, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.65f);
                        scale.setDuration(duration);
                        animationSet.addAnimation(scale);
                    } else if (animationType == UIAnimationFade) {
                        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setDuration(duration);
                        animationSet.addAnimation(fadeOut);
                    } else {
                        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setDuration(0);
                        animationSet.addAnimation(fadeOut);
                    }

                    final View view = ((ViewGroup) dialog.getWindow().getDecorView()).getChildAt(0);
                    view.startAnimation(animationSet);

                    animationSet.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    dialog = null;
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private static void closeSplashScreen(final Activity activity) {
        try {
            if (RCTSplashScreenModule.uniqueInstance != null) {
                RCTSplashScreenModule.uniqueInstance.sendEvent(RCTSplashScreen.CloseSplashScreen);
            }

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    removeSplashScreen(activity, UIAnimationScale, 800);
                }
            }, 800);
        } catch (Exception ex) {
        }
    }
}
