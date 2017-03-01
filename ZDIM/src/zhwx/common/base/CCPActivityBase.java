/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package zhwx.common.base;

import android.app.Dialog;
import android.content.Context;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.List;

import zhwx.common.util.LogUtil;
import zhwx.common.view.TopBarView;


/**
 * 界面处理
 * Created by 容联•云通讯 Modify By Li.Xin @ 中电和讯 on 2015/3/17.
 */
public abstract class CCPActivityBase {

    private FragmentActivity mActionBarActivity;

    /**
     * CCPActivity root view
     */
    private View mContentView;

    private LayoutInflater mLayoutInflater;

    /**
     * CCPActivity root View container
     */
    private FrameLayout mContentFrameLayout;

    /**
     * Manager dialog.
     */
    private List<Dialog> mAppDialogCache ;

    /**
     * The volume of music
     */
    private int mMusicMaxVolume;
    private View mBaseLayoutView;
    private View mTransLayerView;

    public CharSequence mTitleText;

    /**
     * 标题
     */
    private View mTopBarView;

    public void init(Context context , FragmentActivity activity)  {
        mActionBarActivity = activity;
        onInit();
        int layoutId = getLayoutId();
        mLayoutInflater = LayoutInflater.from(mActionBarActivity);
        mBaseLayoutView = mLayoutInflater.inflate(R.layout.ccp_activity, null);
        mTransLayerView = mBaseLayoutView.findViewById(R.id.ccp_trans_layer);
        LinearLayout mRootView = (LinearLayout) mBaseLayoutView.findViewById(R.id.ccp_root_view);
        mContentFrameLayout = (FrameLayout) mBaseLayoutView.findViewById(R.id.ccp_content_fl);

        if(getTitleLayout() != -1) {
            mTopBarView = mLayoutInflater.inflate(getTitleLayout() , null);
            mRootView.addView(mTopBarView,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        if (layoutId != -1) {

            mContentView = getContentLayoutView();
            if(mContentView == null) {
                mContentView = mLayoutInflater.inflate(getLayoutId(), null);
            }
            mRootView.addView(mContentView,LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
        }

        onBaseContentViewAttach(mBaseLayoutView);

        CCPLayoutListenerView listenerView = (CCPLayoutListenerView) mActionBarActivity.findViewById(R.id.ccp_content_fl);
        if (listenerView != null && mActionBarActivity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) {
            listenerView.setOnSizeChangedListener(new CCPLayoutListenerView.OnCCPViewSizeChangedListener() {

                @Override
                public void onSizeChanged(int w, int h, int oldw, int oldh) {
                    LogUtil.d(LogUtil.getLogUtilsTag(getClass()), "oldh - h = " + (oldh - h));
                }
            });

        }
    }


    /**
     * hideTitleView
     */
    public final void hideTitleView() {
        LogUtil.d(LogUtil.getLogUtilsTag(BaseActivity.class), "hideTitleView hasTitle :" + (mTopBarView != null? true: false));
        if(mTopBarView != null) {
            mTopBarView.setVisibility(View.GONE);
        }
    }

    /**
     * showTitleView
     */
    public final void showTitleView() {
        LogUtil.d(LogUtil.getLogUtilsTag(BaseActivity.class), "showTitleView hasTitle :" + (mTopBarView != null? true: false));
        if(mTopBarView != null) {
            mTopBarView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * isTitleShowing
     * @return
     */
    public final boolean isTitleShowing() {
        LogUtil.d(LogUtil.getLogUtilsTag(BaseActivity.class), "isTitleShowing hasTitle :" + (mTopBarView != null? true: false));
        if(mTopBarView == null) {
            return  false;
        }

        return mTopBarView.getVisibility() == View.VISIBLE;
    }

    /**
     * The height of acitonBar
     * @return
     */
    public final int getActionBarHeight() {
        if(mTopBarView == null) {
            return 0;
        }

        return mTopBarView.getHeight();
    }

    /**
     *
     * @return
     */
    public View getActivityLayoutView() {
        return mContentView;
    }

    public View getContentView() {
        return mBaseLayoutView;
    }

    /**
     *
     * @param visiable
     */
    public void setActionBarVisiable(int visiable) {
        if(mTopBarView == null) {
            return ;
        }
        if(visiable == View.VISIBLE) {
            showTitleView();
            return ;
        }
        hideTitleView();
    }

    /**
     *
     * @return
     */
    public FragmentActivity getFragmentActivity() {
        return mActionBarActivity;
    }


    /**
     *
     * @param contentDescription
     */
    public final void setActionContentDescription(CharSequence contentDescription) {
        if(TextUtils.isEmpty(contentDescription)) {
            return;
        }
        String description = mActionBarActivity.getString(R.string.common_enter_activity) + contentDescription;
        mActionBarActivity.getWindow().getDecorView().setContentDescription(description);
    }


    /**
     *
     */
    public void displaySoftKeyboard() {
        final FragmentActivity activity = mActionBarActivity;
        // Display the soft keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View localView = activity.getCurrentFocus();
            if (localView != null && localView.getWindowToken() != null) {
                inputMethodManager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * hide input method.
     */
    public void hideSoftKeyboard(View view) {
        if (view == null) {
            return;
        }


        InputMethodManager inputMethodManager = (InputMethodManager) mActionBarActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            IBinder localIBinder = view.getWindowToken();
            if (localIBinder != null)
                inputMethodManager.hideSoftInputFromWindow(localIBinder, 0);
        }
    }

    /**
     * hide inputMethod
     */
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) mActionBarActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null ) {
            View localView = mActionBarActivity.getCurrentFocus();
            if(localView != null && localView.getWindowToken() != null ) {
                IBinder windowToken = localView.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }


    /**
     * 设置ActionBar标题
     * @param title
     */
    public final void setActionBarTitle(CharSequence title) {
        if(mTopBarView == null) {
            return;
        }

        mTitleText = title;
        if(mTopBarView instanceof TopBarView) {
            ((TopBarView) mTopBarView ).setTitle(title!= null ?title.toString():"");
        }
        setActionContentDescription(title);
    }

    /**
     *
     * @return
     */
    public final CharSequence getActionBarTitle() {
        return mTitleText;
    }

    /**
     *
     * @return
     */
    public final TopBarView getTitleBar() {
        return (TopBarView)mTopBarView;
    }


    /**
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP) {
			/*if(mOverFlowAction != null && mOverFlowAction.isEnabled()) {
				callMenuCallback(mOverFlowMenuItem, mOverFlowAction);
				return true;
			}*/
        }
        return false;
    }

    public void onResume(){

    }

    public void onPause(){

    }

    /**
     *
     */
    public void onDestroy() {
        releaseDialogList();
        mTopBarView = null;
    }

    /**
     *
     */
    private void releaseDialogList() {
        if(mAppDialogCache == null) {
            return;
        }

        for(Dialog dialog : mAppDialogCache) {
            if(dialog == null || !dialog.isShowing()) {
                continue;
            }
            dialog.dismiss();
        }
        mAppDialogCache.clear();
        mAppDialogCache = null;
    }


    /**
     * 子类重载该方法自定义标题布局文件
     * @return
     */
    public int getTitleLayout() {
        return R.layout.ec_title_view_base;
    }

    public TopBarView getTopBarView() {
        if(mTopBarView instanceof TopBarView) {
            return (TopBarView) mTopBarView;
        }
        return null;
    }

    protected abstract void onInit();

    /**
     * The sub Activity implement, set the Ui Layout
     * @return
     */
    protected abstract int getLayoutId();
    protected abstract View getContentLayoutView();
    protected abstract String getClassName();

    /**
     *
     */
    protected abstract void onBaseContentViewAttach(View contentView);

    public void addDialog(Dialog dialog) {
        if(dialog == null) {
            return;
        }

        if(mAppDialogCache == null) {
            mAppDialogCache = new ArrayList<Dialog>();
        }
        mAppDialogCache.add(dialog);
    }


    /**
     *
     */
    public final void invalidateActionMenu() {
        mActionBarActivity.supportInvalidateOptionsMenu();
    }
}
