package com.reactnativecomponent.splashscreen;

import android.os.Handler;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class RCTSplashScreenModule extends ReactContextBaseJavaModule {

    private static ReactContext mContext = null;
    public volatile static RCTSplashScreenModule uniqueInstance = null;

    public RCTSplashScreenModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mContext = reactContext;
    }

    //采用Double CheckLock(DCL)实现单例
    public static RCTSplashScreenModule getInstance(ReactApplicationContext reactContext) {
        if (uniqueInstance == null) {
            synchronized (RCTSplashScreenModule.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new RCTSplashScreenModule(reactContext);
                }
            }
        }
        return uniqueInstance;
    }

    @Override
    public String getName() {
        return "SplashScreen";
    }

    public void sendEvent(String eventName) {
        if (mContext != null) {
            WritableMap data = Arguments.createMap();
            mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, data);
        }
    }

    @ReactMethod
    public void close(ReadableMap options) {

        int animationType = RCTSplashScreen.UIAnimationNone;
        int duration = 0;
        int delay = 0;

        if (options != null) {
            if (options.hasKey("animationType")) {
                animationType = options.getInt("animationType");
            }
            if (options.hasKey("duration")) {
                duration = options.getInt("duration");
            }
            if (options.hasKey("delay")) {
                delay = options.getInt("delay");
            }
        }

        if (animationType == RCTSplashScreen.UIAnimationNone) {
            delay = 0;
        }

        final int final_animationType = animationType;
        final int final_duration = duration;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                RCTSplashScreen.removeSplashScreen(getCurrentActivity(), final_animationType, final_duration);
            }
        }, delay);
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        return Collections.unmodifiableMap(new HashMap<String, Object>() {
            {
                put("animationType", getAnimationTypes());
                put("eventName", getEventNames());
            }

            private Map<String, Object> getAnimationTypes() {
                return Collections.unmodifiableMap(new HashMap<String, Object>() {
                    {
                        put("none", RCTSplashScreen.UIAnimationNone);
                        put("fade", RCTSplashScreen.UIAnimationFade);
                        put("scale", RCTSplashScreen.UIAnimationScale);
                    }
                });
            }

            private Map<String, Object> getEventNames() {
                return Collections.unmodifiableMap(new HashMap<String, Object>() {
                    {
                        put("close", RCTSplashScreen.CloseSplashScreen);
                    }
                });
            }
        });
    }
}
