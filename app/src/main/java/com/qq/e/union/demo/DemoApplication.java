package com.qq.e.union.demo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.qq.e.comm.managers.setting.GlobalSetting;
import com.qq.e.comm.util.GDTLogger;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;

public class DemoApplication extends MultiDexApplication {

  @Override
  public void onCreate() {
    super.onCreate();
    initLeakCanary();
    config(this);
  }

  void config(Context context) {
    try {
      CrashReport.initCrashReport(this, Constants.BuglyAppID, true);

      GlobalSetting.setChannel(1);
      String packageName = context.getPackageName();
      //Get all activity classes in the AndroidManifest.xml
      PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
              packageName, PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
      if (packageInfo.activities != null) {
        for (ActivityInfo activity : packageInfo.activities) {
          Bundle metaData = activity.metaData;
          if (metaData != null && metaData.containsKey("id")
                  && metaData.containsKey("content") && metaData.containsKey("action")) {
            Log.e("gdt", activity.name);
            try {
              Class.forName(activity.name);
            } catch (ClassNotFoundException e) {
              continue;
            }
            String id = metaData.getString("id");
            String content = metaData.getString("content");
            String action = metaData.getString("action");
            DemoListActivity.register(action, id, content);
          }
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void initLeakCanary() {
    if(!BuildConfig.DEBUG){
      return;
    }
    GDTLogger.d("initLeakCanary");
    if (LeakCanary.isInAnalyzerProcess(this)) {
      return;
    }
    LeakCanary.install(this);
  }
}
