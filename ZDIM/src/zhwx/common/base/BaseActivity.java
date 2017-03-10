package zhwx.common.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.netease.nim.demo.ECApplication;

import java.io.Serializable;
import java.util.List;

import zhwx.common.util.IMUtils;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.view.TopBarView;
import zhwx.common.view.swipebacklayout.SwipeBackActivityBase;
import zhwx.common.view.swipebacklayout.SwipeBackActivityHelper;
import zhwx.common.view.swipebacklayout.SwipeBackLayout;
import zhwx.common.view.swipebacklayout.Utils;


/**
 * @Title: PuBaseActivity.java
 * @Package com.xinyulong.seagood
 * @date 2016-5-3 上午10:30:17
 */
public abstract class BaseActivity extends FragmentActivity implements SwipeBackActivityBase {

	protected abstract int getLayoutId();

	private CCPActivityBase mBaseActivity = new CCPActivityImpl(this);

	private static final String TAG = "PuBaseActivity";

	private SwipeBackActivityHelper mHelper;

	private SwipeBackLayout mSwipeBackLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ECApplication.getInstance().addActivity(this);
		mBaseActivity.init(getBaseContext(), this);
		mHelper = new SwipeBackActivityHelper(this);
		mHelper.onActivityCreate();
		mSwipeBackLayout = getSwipeBackLayout();
		mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
		setImmerseLayout(getTopBarView());
		mBaseActivity.getActivityLayoutView().setBackgroundColor(Color.parseColor("#f6f6f6"));
	}

	@Override
	public SwipeBackLayout getSwipeBackLayout() {
		return mHelper.getSwipeBackLayout();
	}

	@Override
	public void setSwipeBackEnable(boolean enable) {
		getSwipeBackLayout().setEnableGesture(enable);
	}

	@Override
	public void scrollToFinishActivity() {
		Utils.convertActivityToTranslucent(this);
		getSwipeBackLayout().scrollToFinishActivity();
	}

	public void hideSoftKeyboard() {
		mBaseActivity.hideSoftKeyboard();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		FragmentManager fm = getSupportFragmentManager();
		int index = requestCode >> 16;
		if (index != 0) {
			index--;
			if (fm.getFragments() == null || index < 0
					|| index >= fm.getFragments().size()) {
				Log.w(TAG, "Activity result fragment index out of range: 0x"
						+ Integer.toHexString(requestCode));
				return;
			}
			Fragment frag = fm.getFragments().get(index);
			if (frag == null) {
				Log.w(TAG, "Activity result no fragment exists for index: 0x"
						+ Integer.toHexString(requestCode));
			} else {
				handleResult(frag, requestCode, resultCode, data);
			}
			return;
		}

	}

	/**
	 * 递归调用，对所有子Fragement生效
	 * 
	 * @param frag
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	private void handleResult(Fragment frag, int requestCode, int resultCode,
			Intent data) {
		frag.onActivityResult(requestCode & 0xffff, resultCode, data);
		List<Fragment> frags = frag.getChildFragmentManager().getFragments();
		if (frags != null) {
			for (Fragment f : frags) {
				if (f != null)
					handleResult(f, requestCode, resultCode, data);
			}
		}
	}

	protected void setImmerseLayout(View view) {
		if (view != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				Window window = getWindow();
    			/*window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/
				window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

				int statusBarHeight = IMUtils.getStatusBarHeight(this.getBaseContext());
				view.setPadding(0, statusBarHeight, 0, 0);
			}
		} else {
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	protected void setImmerseLayout(View view,int flag) {
		if (view != null && flag == 1) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				Window window = getWindow();
    			/*window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				view.setPadding(0, 0, 0, 0);
			}
		}
	}

	protected void onActivityInit() {
		// CCPAppManager.setContext(this);
	}

	public void onBaseContentViewAttach(View contentView) {
		setContentView(contentView);
	}

	public TopBarView getTopBarView() {
		return mBaseActivity.getTopBarView();
	}

	public static String getUnNullString(String s, String defalt) {
		return (s == null || StringUtil.isBlank(s) || "null".equals(s)) ? defalt
				: s;
	}

	public void doToast(String string) {
		ToastUtil.showMessage(string);
	}

	public void transfer(Class<?> clazz) {
		try {
			Intent intent = new Intent(this, clazz);
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public void transfer(Class<?> clazz, int requestCode) {
		try {
			Intent intent = new Intent(this, clazz);
			startActivityForResult(intent, requestCode);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public void transfer(Class<?> clazz, Serializable obj, String params) {
		try {
			Intent intent = new Intent(this, clazz);
			intent.putExtra(params, obj);
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public void transfer(Class<?> clazz, String obj, String params) {
		try {
			Intent intent = new Intent(this, clazz);
			intent.putExtra(params, obj);
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public void transfer(Class<?> clazz, String obj, String params,
			String obj1, String params1) {
		try {
			Intent intent = new Intent(this, clazz);
			intent.putExtra(params, obj);
			intent.putExtra(params1, obj1);
			startActivityForResult(intent, 0);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public void transfer(Class<?> clazz, String str, String params,
			int requestCode) {
		try {
			Intent intent = new Intent(this, clazz);
			intent.putExtra(params, str);
			startActivityForResult(intent, requestCode);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public void transfer(Class<?> clazz, int requestCode, Serializable obj,
			String params) {
		try {
			Intent intent = new Intent(this, clazz);
			intent.putExtra(params, obj);
			startActivityForResult(intent, requestCode);
		} catch (Exception e) {
			doToast("该功能尚未开发,敬请期待");
			Log.e("transfer", e.toString());
		}
	}

	public String getStringByUI(View view) {

		if (view instanceof EditText) {
			return ((EditText) view).getText().toString().trim();
		} else if (view instanceof TextView) {
			return ((TextView) view).getText().toString().trim();
		}
		return "";
	}

	/**
	 * 设置ActionBar标题
	 * @param text
	 */
	public void setActionBarTitle(CharSequence text) {
		mBaseActivity.setActionBarTitle(text);
	}

	/**
	 * #getLayoutId()
	 * @return
	 */
	public View getActivityLayoutView() {
//		getWindow().setContentView(LayoutInflater.from(this).inflate(R.layout.main,null));
		return mBaseActivity.getActivityLayoutView();
	}

	/**
	 *
	 */
	public final void showTitleView() {
		mBaseActivity.showTitleView();
	}

	/**
	 *
	 */
	public final void hideTitleView() {
		mBaseActivity.hideTitleView();
	}

	public void onBack(View v) {
		finish();
	}
}
