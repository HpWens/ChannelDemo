package com.github.rrj.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.rrj.MyApplication;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by boby on 2017/3/2.
 */

public class FirstWebViewFragment extends SupportFragment {

    public static FirstWebViewFragment newInstance() {
        FirstWebViewFragment fragment = new FirstWebViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start(WebViewFragment.newInstance(MyApplication.loadUrl));
    }
}
