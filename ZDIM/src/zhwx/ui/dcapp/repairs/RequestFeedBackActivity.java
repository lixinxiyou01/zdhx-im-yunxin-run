package zhwx.ui.dcapp.repairs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.HashMap;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.repairs.model.RequestFeedBackItem;

/**
 * @author Li.xin @ 中电和讯
 * 报修人反馈
 */
public class RequestFeedBackActivity extends BaseActivity implements View.OnClickListener {

	private Activity context;

	private String repairId;

	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;

	private HashMap<String, ParameterValue> map;

	private RequestFeedBackItem requestFeedBackItem;

	private RadioGroup repairStatusRG;

	private RatingBar suduRB,taiduRB,jishuBar,zhiliangRB,allRB;

	private String isFixed = "1";

	private EditText suggestET;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"报修反馈评价", this);
		repairId = getIntent().getStringExtra("repairId");
		initView();
		getData();
	}

	private void getData() {
		mPostingdialog = new ECProgressDialog(this, "正在加载数据");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("reportId",new ParameterValue(repairId));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String json = UrlUtil.getFeedbackContentList(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() { 
							refreshData(json);
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
	
	private void refreshData(String json) {
		System.out.println(json);
		if (!json.contains("<html>")) {
			requestFeedBackItem = new Gson().fromJson(json, RequestFeedBackItem.class);
			suduRB.setRating(Float.parseFloat(requestFeedBackItem.getSpeedStr()));
			taiduRB.setRating(Float.parseFloat(requestFeedBackItem.getAttitudeStr()));
			jishuBar.setRating(Float.parseFloat(requestFeedBackItem.getTechnicalLevelStr()));
			zhiliangRB.setRating(Float.parseFloat(requestFeedBackItem.getQualityStr()));
			allRB.setRating(Float.parseFloat(requestFeedBackItem.getScoreStr()));
			suggestET.setText(StringUtil.isNotBlank(requestFeedBackItem.getSuggestion())?requestFeedBackItem.getSuggestion():"");
			if (requestFeedBackItem.getRepairFlag() != null && "0".equals(requestFeedBackItem.getRepairFlag())) {
				RadioButton radioButton = (RadioButton)findViewById(R.id.cantRepairRb);
				radioButton.setChecked(true);
			}
			mPostingdialog.dismiss();
		}
	}
	
	@SuppressLint("NewApi")
	private void initView() {
		repairStatusRG = (RadioGroup) findViewById(R.id.repairStatusRG);
		repairStatusRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				switch (checkedId) {
					case R.id.repairedRb:
						isFixed = "1";
						break;
					case R.id.cantRepairRb:
						isFixed = "0";
						break;
				}
			}
		});
		suggestET = (EditText) findViewById(R.id.suggestET);
		jishuBar = (RatingBar) findViewById(R.id.jishuBar);
		zhiliangRB = (RatingBar) findViewById(R.id.zhiliangRB);
		allRB = (RatingBar) findViewById(R.id.allRB);
		taiduRB = (RatingBar) findViewById(R.id.taiduRB);
		suduRB = (RatingBar) findViewById(R.id.suduRB);
	}

	public void onSend(View v) {
		mPostingdialog = new ECProgressDialog(this, "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("reportId", new ParameterValue(repairId)); //报修单Id
		map.put("attitudeStr", new ParameterValue(taiduRB.getRating()+"")); //服务态度
		map.put("qualityStr", new ParameterValue(zhiliangRB.getRating()+"")); //维修质量
		map.put("scoreStr", new ParameterValue(allRB.getRating()+"")); //整体评价
		map.put("speedStr", new ParameterValue(suduRB.getRating()+""));//响应速度
		map.put("technicalLevelStr", new ParameterValue(jishuBar.getRating()+""));//技术水平
		map.put("repairFlag", new ParameterValue(isFixed));//是否修好
		map.put("suggestion", new ParameterValue(suggestET.getEditableText().toString()));//意见

		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String sendFlag = UrlUtil.saveFeedBack(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (sendFlag.contains("ok")) {
								ToastUtil.showMessage("评价已提交");
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
		return R.layout.activity_rm_request_feedback;
	}
}
