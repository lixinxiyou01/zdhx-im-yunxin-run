package zhwx.ui.dcapp.storeroom;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.gson.Gson;
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
import zhwx.ui.dcapp.storeroom.model.CheckResult;

/**
 * @Title: CheckActivity.java
 * @Package com.lanxum.hzth.im.ui.v3.carmanage
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
	
	private int kind = 0;
	
	private CheckResult checkResult = new CheckResult();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		orderId = getIntent().getStringExtra("orderId");
		kind = getIntent().getIntExtra("kind", 0);
		checkResult.setId(orderId);
		initView();
	}

	private void initView() {
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar, 1);
		checkRG = (RadioGroup) findViewById(R.id.checkRG);
		checkRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int radioId) {
				if (radioId == R.id.passRB) {
					pass = true;
				} else {
					pass = false;
				}
			}
		});

		noteET = (EditText) findViewById(R.id.noteET);
	}

	public void onSend(View v) {
		mPostingdialog = new ECProgressDialog(this, "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		if (pass) {
			if (kind == ToCheckFragment.SMCHECK_FLAG_BM) {
				checkResult.setCheckStatus(2+"");
			} else {
				checkResult.setCheckStatus(4+"");
			}
		} else {
			if (kind == ToCheckFragment.SMCHECK_FLAG_BM) {
				checkResult.setCheckStatus(1+"");
			} else {
				checkResult.setCheckStatus(3+"");
			}
		}
		checkResult.setKind(kind == ToCheckFragment.SMCHECK_FLAG_BM?"dept":"zw");
		checkResult.setCheckOpinion(noteET.getEditableText().toString());
		map.put("resultJson", new ParameterValue(new Gson().toJson(checkResult)));
		System.out.println(new Gson().toJson(checkResult));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String sendFlag = UrlUtil.saveCheckApplyReceiveRecord(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							ToastUtil.showMessage("已审核");
							setResult(110);
							finish();
							mPostingdialog.dismiss();	
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
		return R.layout.activity_sm_check;
	}
}
