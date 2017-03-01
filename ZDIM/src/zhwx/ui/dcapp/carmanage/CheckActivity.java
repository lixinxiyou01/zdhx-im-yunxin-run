package zhwx.ui.dcapp.carmanage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.HashMap;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;


/**
 * @Title: CheckActivity.java
 * @Package zhwx.ui.dcapp.carmanage
 * @Description: 订车单审核
 * @author Li.xin @ 中电和讯
 * @date 2016-3-30 上午10:42:16
 */
public class CheckActivity extends BaseActivity {

	private Activity context;

	private FrameLayout top_bar;

	private String orderId;

	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;

	private HashMap<String, ParameterValue> map;

	private RadioGroup checkRG;

	private EditText noteET;

	private boolean pass = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		orderId = getIntent().getStringExtra("orderId");
		initView();
	}

	private void initView() {
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar, 1);
		checkRG = (RadioGroup) findViewById(R.id.checkRG);
		checkRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int radioId) {
				// TODO Auto-generated method stub
				if (radioId == R.id.passRB) {
					pass = true;
				} else {
					pass = false;
				}
			}
		});

		noteET = (EditText) findViewById(R.id.noteET);
	}

	// 生成订车单
	public void onSend(View v) {
		// id，status，advice
		mPostingdialog = new ECProgressDialog(this, "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(orderId));
		if (pass) {
			map.put("status", new ParameterValue("1"));
		} else {
			map.put("status", new ParameterValue("2"));
		}
		map.put("advice", new ParameterValue(noteET.getText().toString()));

		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String sendFlag = UrlUtil.checkOrderCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (sendFlag.contains("ok")) {
								ToastUtil.showMessage("已审核");
								setResult(110);
								finish();
								mPostingdialog.dismiss();
							} else {
								ToastUtil.showMessage("提交出错");
							}
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("请求失败，请稍后重试");
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							mPostingdialog.dismiss();
						}
					}, 5);
				}
			}
		}).start();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_cm_check;
	}
}
