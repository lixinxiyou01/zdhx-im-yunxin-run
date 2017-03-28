package zhwx.ui.dcapp.repairs;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
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
 * @author Li.xin @ 中电和讯
 */
public class CostCheckActivity extends BaseActivity implements View.OnClickListener {

	private Activity context;

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
		getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"费用审批", this);
		orderId = getIntent().getStringExtra("repairId");
		initView();
	}

	private void initView() {
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
		map.put("repairId", new ParameterValue(orderId));
		if (pass) {
			map.put("checkFlag", new ParameterValue("1"));
		} else {
			map.put("checkFlag", new ParameterValue("2"));
		}
		map.put("checkNote", new ParameterValue(noteET.getText().toString()));

		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String sendFlag = UrlUtil.saveCostCheck(ECApplication.getInstance().getV3Address(), map);
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
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
		}
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_rm_check;
	}
}
