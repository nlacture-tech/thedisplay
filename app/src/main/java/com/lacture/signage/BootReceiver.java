package com.lacture.signage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** เปิดแอปเองอัตโนมัติเมื่อจอบูตเสร็จ (บางเฟิร์มแวร์จีนต้องเปิด Autostart ให้แอปนี้ก่อน) */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        Intent launch = new Intent(c, MainActivity.class);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(launch);
    }
}
