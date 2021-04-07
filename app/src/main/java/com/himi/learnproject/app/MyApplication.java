package com.himi.learnproject.app;

import android.app.Application;

import com.himi.learnproject.hotfix.MyFix;
/**
 * 这个是最基础的版本，记得手动添加存储权限，否则会找不到patch.dex
 */
import java.io.File;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyFix.installPatch(this,new File("/sdcard/patch.dex"));
    }
}
