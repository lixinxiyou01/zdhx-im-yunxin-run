package zhwx.ui.dcapp.assets;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

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
import zhwx.ui.dcapp.assets.model.CheckListItem;


/**   
 * @Title: ApplyDetailActivity.java 
 * @Package zhwx.ui.dcapp.assets
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年8月18日 下午3:41:53 
 */
public class ApplyDetailActivity extends BaseActivity implements OnClickListener {
	
	private CheckListItem model;
	
	private String id;
	
	private String json;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;
	
	private TextView asKindTV, useDateTV, userNameTV, departmentTV,
			schoolTV,needIntroductionsTV,useWayTV,orderTimeTV,checkStatusViewTV,checkInfoTV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"详情", this);
		model = (CheckListItem) getIntent().getSerializableExtra("checkListItem");
		if (model!=null) {
			initView();
		} else {
			id = getIntent().getStringExtra("id");
			getData();
		}
	}

	/**
	 * 
	 */
	private void getData() {
		// TODO Auto-generated method stub
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.getCheckInfoJson(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (json.contains("<html>")) {
								ToastUtil.showMessage("数据异常");
								mPostingdialog.dismiss();
								return;
							}
							model = new Gson().fromJson(json, CheckListItem.class);
							initView();
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

	/**
	 * 
	 */
	private void initView() {
		// TODO Auto-generated method stub
		asKindTV = (TextView) findViewById(R.id.asKindTV);
		useDateTV = (TextView) findViewById(R.id.useDateTV);
		userNameTV = (TextView) findViewById(R.id.userNameTV);
		departmentTV = (TextView) findViewById(R.id.departmentTV);
		schoolTV = (TextView) findViewById(R.id.schoolTV);
		needIntroductionsTV = (TextView) findViewById(R.id.needIntroductionsTV);
		useWayTV = (TextView) findViewById(R.id.useWayTV);
		orderTimeTV = (TextView) findViewById(R.id.orderTimeTV);
		checkStatusViewTV = (TextView) findViewById(R.id.checkStatusViewTV);
		checkInfoTV = (TextView) findViewById(R.id.checkInfoTV);
		asKindTV.setText(model.getAssetKindName());
		useDateTV.setText(model.getApplyDate());
		userNameTV.setText(model.getApplyUser());
		departmentTV.setText(model.getDepartment());
		schoolTV.setText(model.getSchool());
		needIntroductionsTV.setText(model.getDemand());
		useWayTV.setText(model.getReason());
		checkStatusViewTV.setText(model.getCheckStatusView());
		checkInfoTV.setText(model.getCheckReason());
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_as_checkdetail;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		}
	}
}
