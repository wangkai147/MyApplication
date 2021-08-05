package com.wangng.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;

import com.donkingliang.consecutivescroller.ConsecutiveScrollerLayout;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import static android.view.KeyEvent.KEYCODE_BACK;
import static com.tencent.smtt.export.external.interfaces.IX5WebViewBase.OVER_SCROLL_NEVER;

public class MainActivity extends AppCompatActivity {

    private X5WebView mWebView;
    private View re_ads;
    private Button bt_visible, bt_gone;
    private ConsecutiveScrollerLayout mViewParent;

    String url = "http://ceshi.h5.520diandu.com/xuexijiqiao/index.html?uid=3229496&key=042440925d8f93ab4133894a4800665f&version=0&code_version=110&cv=110&channel=";
//    String url = "http://debugtbs.qq.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initHardwareAccelerate();

        mWebView = findViewById(R.id.x5webview);
        re_ads = findViewById(R.id.re_ads);
        mViewParent = findViewById(R.id.mViewParent);

        bt_visible = findViewById(R.id.bt_visible);
        bt_gone = findViewById(R.id.bt_gone);

        bt_visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(1);
            }
        });

        bt_gone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(2);
            }
        });

        //隐藏导航条
        View view = mWebView.getView();
        view.setVerticalScrollBarEnabled(false);
        view.setHorizontalScrollBarEnabled(false);
        view.setOverScrollMode(OVER_SCROLL_NEVER);

        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);

        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);


        webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        mWebView.addJavascriptInterface(new JSInterface(), "webviewShare");

        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();

        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new myWebViewClient());
        mWebView.setWebChromeClient(new myWebChromeClient());
    }

    class JSInterface {
        @JavascriptInterface
        public void showAppAd() {
            Log.e("AAAAAAAAAAAAAAAAAA","SSSSSSSSSSSSSSSSSSSSSSS");
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    handler.sendEmptyMessage(1);
                }
            });
        }

        @JavascriptInterface
        public void htmlPageTitle(String data) {
            bt_visible.setText("aaaa");
        }
    }

    /**
     * 启用硬件加速
     */

    private void initHardwareAccelerate() {
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && mWebView.canGoBack()) {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    handler.sendEmptyMessage(2);
                }
            });


            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean is_ad = false;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (!is_ad) {
                        is_ad = true;
//                        mViewParent.addView(re_ads);
                        re_ads.setVisibility(View.VISIBLE);
//                        checkAndRequestPermission();
                        mViewParent.checkLayoutChange();
                    }
                    break;

                case 2:
                    if (is_ad) {
                        is_ad = false;
                        re_ads.setVisibility(View.GONE);
//                        mViewParent.removeView(re_ads);
                        mViewParent.checkLayoutChange();
                    }
                    break;
            }
        }
    };

    /**
     * WebViewClient监听
     */

    private class myWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String s) {
            webView.loadUrl(s);
            return true;
        }
    }

    /**
     * WebChromeClient监听
     */

    private class myWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView webView, int i) {
            super.onProgressChanged(webView, i);
        }
    }
}