package com.example.keshav.tipcalculator;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/*
    <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
    <uses-permission android:name="com.android.launcher.permission.UNINSTALL_SHORTCUT" />
 */
public class AppShortCut<T> {



   public void shortcut(Context context,T activityClass,int resourceId){
       SharedPreferences  appPreferences;
       boolean isAppInstalled=false;
       appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
       isAppInstalled = appPreferences.getBoolean("isAppInstalled",false);
       if(!isAppInstalled){
           //  create short code
           Intent shortcutIntent = new Intent(context,activityClass.getClass());
           shortcutIntent.setAction(Intent.ACTION_MAIN);
           Intent intent = new Intent();
           intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
           intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, R.string.app_name);
           intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,Intent.ShortcutIconResource
                   .fromContext(context, resourceId));
           intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
           context.sendBroadcast(intent);

           //Make preference true

           SharedPreferences.Editor editor = appPreferences.edit();
           editor.putBoolean("isAppInstalled", true);
           editor.commit();
       }
   }
}
