package zhwx.ui.dcapp.assets;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.assets.model.CheckListItem;

/**
 * @Title: CheckActivity.java
 * @Package zhwx.ui.dcapp.carmanage
 * @Description: 订车单审核
 * @author Li.xin @ 中电和讯
 * @date 2016-3-30 上午10:42:16
 */
public class AsCheckActivity extends BaseActivity implements OnClickListener{

	private Activity context;

	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;

	private HashMap<String, ParameterValue> map;

	private RadioGroup checkRG;

	private EditText noteET;

	private boolean pass = true;
	
	private CheckListItem model;
	
	private Gson gson = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"申请审核", this);
		model = (CheckListItem) getIntent().getSerializableExtra("checkListItem");
		initView();
	}

	private void initView() {
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
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("applyIds", model.getId());
		if (pass) {
			paramMap.put("checkResult", "3");
		} else {
			paramMap.put("checkResult", "2");
		}
		paramMap.put("checkReason", noteET.getText().toString());
		map.put("resultJson", new ParameterValue(gson.toJson(paramMap)));
		
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String sendFlag = UrlUtil.saveCheck(ECApplication.getInstance().getV3Address(), map);
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
		return R.layout.activity_as_check;
	}
}
