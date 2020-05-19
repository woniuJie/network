package com.chuchujie.core.network.okhttp.utils;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 运行环境检测的工具类, 检测当前环境是运行在Android还是Java
 */
public class PlatformUtils {

    private static final PlatformUtils PLATFORM = findPlatform();

    public static PlatformUtils get() {
        return PLATFORM;
    }

    private static PlatformUtils findPlatform() {
        try {
            Class.forName("android.os.Build");
            if (Build.VERSION.SDK_INT != 0) {
                return new Android();
            }
        } catch (ClassNotFoundException ignored) {
        }
        return new PlatformUtils();
    }

    public Executor defaultCallbackExecutor() {
        return Executors.newCachedThreadPool();
    }

    public void execute(Runnable runnable) {
        defaultCallbackExecutor().execute(runnable);
    }

    /**
     * Android线程调度
     */
    static class Android extends PlatformUtils {

        @Override
        public Executor defaultCallbackExecutor() {
            return new MainThreadExecutor();
        }

        static class MainThreadExecutor implements Executor {
            private final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void execute(Runnable r) {
                handler.post(r);
            }
        }
    }

}
