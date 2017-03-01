package zhwx.common.view.pullrefreshview;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class PuBaseActivity extends FragmentActivity {
	//
	// public static final String TAG = "SuperActivity";
	// public FragmentManager fragmentManager;
	public FragmentActivity context;

	private Dialog mDialog;
	private float onClickX = 0, firstOnClickX = -1; // 按下时时间X左边event.getX();
	private float onClickY = 0, firstOnClickY = -1; // 按下时时间X左边event.getY();
	private int flingLength, flingHeight;

	public GestureDetector getDetector() {
		return detector;
	}

	private GestureDetector detector;

	private boolean gesture;
	private boolean startAnim = true;

	public boolean isStartAnim() {
		return startAnim;
	}

	public void setStartAnim(boolean startAnim) {
		this.startAnim = startAnim;
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
	}

	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

	public void startActivityForResult(Class<?> clazz, int requestCode,
			boolean anim) {
		startAnim = anim;
		startActivityForResult(new Intent(context, clazz), requestCode);
		if (anim) {
			// overridePendingTransition(R.anim.push_right_in,
			// R.anim.push_left_out);
		} else {
			// overridePendingTransition(0, 0);
		}
	}

	public void startActivityForResult(Intent intent, int requestCode,
			boolean anim) {
		startAnim = anim;
		startActivityForResult(intent, requestCode);
		if (anim) {
			// overridePendingTransition(R.anim.push_right_in,
			// R.anim.push_left_out);
		} else {
			// overridePendingTransition(0, 0);
		}
	}

	protected void startActivity(Class<?> clazz, boolean anim) {
		startAnim = anim;
		startActivity(new Intent(context, clazz));
		if (anim) {
			// overridePendingTransition(R.anim.push_right_in,
			// R.anim.push_left_out);
		} else {
			// overridePendingTransition(0, 0);
		}
	}

	protected void startActivity(Intent intent, boolean anim) {
		startAnim = anim;
		startActivity(intent);
		if (anim) {
			// overridePendingTransition(R.anim.push_right_in,
			// R.anim.push_left_out);
		} else {
			// overridePendingTransition(0, 0);
		}
	}

	@Override
	public void finish() {
		super.finish();
	}

	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	protected void openActivity(String pAction) {
		openActivity(pAction, null);
	}

	protected void openActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	public void dismissDialog() {
		if (mDialog != null && mDialog.isShowing() && !this.isFinishing()) {
			mDialog.dismiss();
		}
	}

	public void showDialog() {
		if (isFinishing())
			return;
		if (mDialog == null)
			mDialog = CommonUtils
					.createLoadingDialog(PuBaseActivity.this, true);
		if (!mDialog.isShowing())
			mDialog.show();
	}

	public boolean isGesture() {
		return gesture;
	}

	public void setGesture(boolean gesture) {
		this.gesture = gesture;
	}
}