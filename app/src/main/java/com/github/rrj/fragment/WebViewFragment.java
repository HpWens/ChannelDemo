package com.github.rrj.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.github.rrj.MyApplication;
import com.github.rrj.R;
import com.github.rrj.SystemBarTintManager;
import com.github.rrj.widget.PageStateLayout;

import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Created by boby on 2017/3/2.
 */
@SuppressLint("SetJavaScriptEnabled")
public class WebViewFragment extends SupportFragment {

    private static final String ARG_URL = "arg_url";

    private WebView mWebView;

    private FrameLayout mRoot;

    private PageStateLayout mPageStateLayout;

    private ProgressBar mProgressBar;

    private String mLoadUrl;

    //是否添加js代码
    private boolean mIsAddJsCode = true;

    private WebClientClickListener mWebClientClickListener;

    public static WebViewFragment newInstance(String url) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mLoadUrl = args.getString(ARG_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        SystemBarTintManager tintManager = new SystemBarTintManager(_mActivity);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.white);

        mWebView = (WebView) view.findViewById(R.id.webview);
        mRoot = (FrameLayout) view.findViewById(R.id.fl_root);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:HelloWord()");
            }
        });

        initPageStateLayout();

        initWebViewConfig();
    }

    private void initPageStateLayout() {
        if (mPageStateLayout == null) {
            mPageStateLayout = new PageStateLayout(_mActivity);
        }
        if (mRoot.getId() != 0) {
            mRoot.addView(mPageStateLayout, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mPageStateLayout.setOnRetryListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    retryLoading();
                }
            });
        }
    }

    private void initWebViewConfig() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setBlockNetworkImage(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //mPageStateLayout.show(PageStateLayout.LOADING_STATE);
                if (!MyApplication.getInstance().isConnected()) {
                    mPageStateLayout.show(PageStateLayout.ERROR_STATE);
                    return;
                }

                if (mIsAddJsCode) {
                    mIsAddJsCode = false;
                    mWebView.loadUrl("javascript:" + "window.onload = function(){\n" +
                            "    var a = document.getElementsByTagName(\"a\");\n" +
                            "    for(i=0;i<a.length;i++){\n" +
                            "        if(a[i].className == \"ui-header-btn icon-pure-back left\"){\n" +
                            "            a[i].onclick = function(){window.JsInterface.JsGoBackFunction();}\n" +
                            "        }\n" +
                            "    }  \n" +
                            "}");

                    mWebView.loadUrl("javascript:" + "function HelloWord() {\n" +
                            "    alert(\"hello wodrd\");\n" +
                            "}");
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!view.getSettings().getLoadsImagesAutomatically()) {
                    view.getSettings().setLoadsImagesAutomatically(true);
                }

                view.getSettings().setBlockNetworkImage(false);

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
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                start(WebViewFragment.newInstance(url));
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress != 100) {
                    mProgressBar.setProgress(newProgress);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        mWebClientClickListener = new WebClientClickListener();
        mWebView.addJavascriptInterface(mWebClientClickListener, "JsInterface");

        mWebView.loadUrl(mLoadUrl);

        mWebClientClickListener.setOnGoBackListener(new WebClientClickListener.JsCallAndroidListener() {
            @Override
            public void OnGoBack() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        _mActivity.onBackPressed();
                    }
                });
            }
        });
    }

    private void retryLoading() {
        mWebView.loadUrl(mLoadUrl);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
        }
    }

    @Override
    protected FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }

}
