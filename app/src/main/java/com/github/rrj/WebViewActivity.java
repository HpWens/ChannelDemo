package com.github.rrj;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.rrj.widget.PageStateLayout;

import static android.view.KeyEvent.KEYCODE_BACK;
import static com.github.rrj.R.id.webview;

public class WebViewActivity extends BaseActivity {

    private PageStateLayout mPageStateLayout;
    private WebView mWebView;
    private ImageView mIvLoad;

    private FrameLayout mRoot;

    private static final String BASE_URL = "http://m.test.366ec.net/Store/Default.aspx?sid=19";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        initViews();

        initPageStateLayout();

        initWebViewConfig();

        initTint();

        mWebView.loadUrl(BASE_URL);
    }

    private void initTint() {
        //FEC00F
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.home_bg);
    }

    private void initWebViewConfig() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }
        mWebView.setWebViewClient(new WebClientWithLoading());
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
    }

    private void initViews() {
        mRoot = (FrameLayout) findViewById(R.id.fl_root);
        mWebView = (WebView) findViewById(webview);
        mIvLoad = (ImageView) findViewById(R.id.iv_bg);
    }


    private void initPageStateLayout() {
        if (mPageStateLayout == null) {
            mPageStateLayout = new PageStateLayout(this);
        }
        if (mRoot.getId() != 0) {
            ViewGroup rootView = (ViewGroup) findViewById(mRoot.getId());
            mPageStateLayout = new PageStateLayout(this);
            rootView.addView(mPageStateLayout, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mPageStateLayout.setOnRetryListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    retryLoading();
                }
            });
        }
    }

    //从新加载界面
    protected void retryLoading() {
        mWebView.loadUrl(BASE_URL);
    }

    /**
     * 监听按下返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    private long mKeyDownTime;

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KEYCODE_BACK)) {
            if (mWebView.canGoBack()) {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                mWebView.goBack();
            } else {

                if (System.currentTimeMillis() - mKeyDownTime >= 1000) {
                    Toast.makeText(WebViewActivity.this, "再按一次返回键退出" + getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }

                mKeyDownTime = System.currentTimeMillis();

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * WebClientWithLoading
     */
    private class WebClientWithLoading extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //mPageStateLayout.show(PageStateLayout.LOADING_STATE);
            if (!MyApplication.getInstance().isConnected()) {
                mPageStateLayout.show(PageStateLayout.ERROR_STATE);
                return;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (!view.getSettings().getLoadsImagesAutomatically()) {
                view.getSettings().setLoadsImagesAutomatically(true);
            }

            if (mIvLoad.getVisibility() == View.VISIBLE) {
                mIvLoad.setVisibility(View.GONE);

                SystemBarTintManager tintManager = new SystemBarTintManager(WebViewActivity.this);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setStatusBarTintResource(R.color.white);
            }

            if (mPageStateLayout.getState() != PageStateLayout.NORMAL_STATE) {
                mPageStateLayout.show(PageStateLayout.NORMAL_STATE);
            }
            //mPageStateLayout.show(PageStateLayout.NORMAL_STATE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            mPageStateLayout.show(PageStateLayout.ERROR_STATE);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWebView!=null){
            mWebView.destroy();
        }
    }
}
