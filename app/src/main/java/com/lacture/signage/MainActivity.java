package com.lacture.signage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * Signage Kiosk — WebView เต็มจอ โหลดหน้า display, กันจอดับ, รีโหลดเองเมื่อเน็ตหลุด
 * แตะมุมซ้ายบน 5 ครั้งเร็วๆ เพื่อเปิดหน้าตั้งค่า URL
 */
public class MainActivity extends Activity {

    private WebView web;
    private SharedPreferences prefs;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private int tapCount = 0;
    private long lastTap = 0;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        prefs = getSharedPreferences("signage", MODE_PRIVATE);

        FrameLayout root = new FrameLayout(this);
        web = new WebView(this);
        root.addView(web, new FrameLayout.LayoutParams(-1, -1));
        setContentView(root);

        setupWeb();

        String url = prefs.getString("url", "");
        if (url == null || url.trim().isEmpty()) {
            askUrl(true);
        } else {
            web.loadUrl(url);
        }
    }

    private void setupWeb() {
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false); // ให้วิดิโอ/YouTube เล่นเองได้
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setJavaScriptCanOpenWindowsAutomatically(true);
        web.setBackgroundColor(0xFF000000);

        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) { return false; }

            @Override
            public void onReceivedError(WebView v, WebResourceRequest req, WebResourceError err) {
                if (req != null && req.isForMainFrame()) scheduleReload();
            }
        });
    }

    private void scheduleReload() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            public void run() {
                if (isOnline()) web.reload(); else scheduleReload();
            }
        }, 5000);
    }

    private boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isConnected();
        } catch (Exception e) { return true; }
    }

    private void askUrl(final boolean firstTime) {
        final EditText in = new EditText(this);
        in.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        in.setHint("https://ชื่อเว็บ.netlify.app/display.html");
        in.setText(prefs.getString("url", ""));

        AlertDialog.Builder d = new AlertDialog.Builder(this)
                .setTitle("ตั้งค่าที่อยู่หน้าจอ (URL)")
                .setView(in)
                .setPositiveButton("บันทึก & โหลด", (dl, w) -> {
                    String u = in.getText().toString().trim();
                    if (!u.isEmpty()) {
                        prefs.edit().putString("url", u).apply();
                        web.loadUrl(u);
                    }
                });
        if (!firstTime) {
            d.setNeutralButton("รีโหลด", (dl, w) -> web.reload());
            d.setNegativeButton("ปิด", null);
        }
        d.setCancelable(!firstTime);
        d.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            float dp = getResources().getDisplayMetrics().density;
            if (ev.getX() < 130 * dp && ev.getY() < 130 * dp) {
                long now = System.currentTimeMillis();
                if (now - lastTap < 1500) tapCount++; else tapCount = 1;
                lastTap = now;
                if (tapCount >= 5) { tapCount = 0; askUrl(false); return true; }
            } else {
                tapCount = 0;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (web != null) web.reload(); // กันออกจากแอปโดยไม่ตั้งใจ
    }

    private void immersive() {
        View v = getWindow().getDecorView();
        v.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_FULLSCREEN
              | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onWindowFocusChanged(boolean f) { super.onWindowFocusChanged(f); if (f) immersive(); }

    @Override
    protected void onResume() { super.onResume(); immersive(); if (web != null) web.onResume(); }

    @Override
    protected void onPause() { super.onPause(); if (web != null) web.onPause(); }
}
