package com.github.rrj.widget;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.github.rrj.widget.PageStateLayout.EMPTY_STATE;
import static com.github.rrj.widget.PageStateLayout.ERROR_STATE;
import static com.github.rrj.widget.PageStateLayout.LOADING_STATE;
import static com.github.rrj.widget.PageStateLayout.NORMAL_STATE;

/**
 * Created by boby on 2016/12/12.
 */
@IntDef({NORMAL_STATE, LOADING_STATE, ERROR_STATE, EMPTY_STATE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PageState {
}
