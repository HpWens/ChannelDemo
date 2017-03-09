package com.github.rrj.fragment;


import android.webkit.JavascriptInterface;

/**
 * Created by Administrator on 9/9 0009.
 */
public class WebClientClickListener {


    private JsCallAndroidListener mJsCallAndroidListener;


    @JavascriptInterface
    public void JsGoBackFunction() {
        if (mJsCallAndroidListener != null) {
            mJsCallAndroidListener.OnGoBack();
        }
    }


    public interface JsCallAndroidListener {
        void OnGoBack();
    }

    public void setOnGoBackListener(JsCallAndroidListener listener) {
        mJsCallAndroidListener = listener;
    }

}
