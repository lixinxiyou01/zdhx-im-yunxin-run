package zhwx.ui.dcapp.carmanage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.carmanage.model.EvaluateData;
import zhwx.ui.dcapp.carmanage.model.EvaluateInfo;
import zhwx.ui.dcapp.carmanage.model.EvaluateViewData;
import zhwx.ui.dcapp.carmanage.model.RatingData;

/**
 * @Title: EvaluateActivity.java
 * @Package zhwx.ui.dcapp.carmanage
 * @Description: TODO(用一句话描述该文件做什么)
 * @author Li.xin @ 中电和讯
 * @date 2016-3-30 下午2:46:18
 */
public class EvaluateActivity extends BaseActivity {

	private Activity context;

	private FrameLayout top_bar;

	private String orderId;

	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;

	private HashMap<String, ParameterValue> map;

	private LinearLayout evaluateContener;
	
	private EvaluateInfo evaluateData;
	
	private List<EvaluateViewData> evaluateViewDatas = new ArrayList<EvaluateViewData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		orderId = getIntent().getStringExtra("orderId");
		initView();
		getData();
	}

	private void getData() {
		mPostingdialog = new ECProgressDialog(this, "正在加载数据");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(orderId));

		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String json = UrlUtil.toCarUserFeedback(ECApplication.getInstance().getV3Address(), map);
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
			
			evaluateData = new Gson().fromJson(json, EvaluateInfo.class);
			evaluateViewDatas.clear();
			//司机数据
			int childCount = evaluateContener.getChildCount();
			int index = -1;
			for (EvaluateInfo.AssignData assignData : evaluateData.getAssignData()) {
				
				index++;
				LinearLayout assignDataItem = null;
				if (index < childCount) {
					assignDataItem = (LinearLayout) evaluateContener.getChildAt(index);
				} else {
					assignDataItem = (LinearLayout) View.inflate(context, R.layout.list_item_cm_evaluate, null);
					evaluateContener.addView(assignDataItem);
				}
				assignDataItem.setVisibility(View.VISIBLE);
				TextView dirverTV = (TextView) assignDataItem.findViewById(R.id.dirverTV);
				dirverTV.setText(assignData.getDriver());
				TextView carTV = (TextView) assignDataItem.findViewById(R.id.carTV);
				EditText noteET = (EditText) assignDataItem.findViewById(R.id.noteET);
				Button evaluateBT = (Button) assignDataItem.findViewById(R.id.evaluateBT);
				if (index == evaluateData.getAssignData().size() - 1) {
					evaluateBT.setVisibility(View.VISIBLE);
				} else {
					evaluateBT.setVisibility(View.GONE);
				}
				EvaluateViewData viewData = new EvaluateViewData();
				viewData.setEditText(noteET); //存储数据View
				viewData.setId(assignData.getAssignId());
				
				carTV.setText(assignData.getCarName() + "  " + assignData.getCarNum());
				if (evaluateData.getAssignData().size() > 1) {
					TextView numberTV = (TextView) assignDataItem.findViewById(R.id.numberTV);
					numberTV.setText("车辆 " + (index+1));
				} else {
					RelativeLayout numberLay = (RelativeLayout) assignDataItem.findViewById(R.id.numberLay);
					numberLay.setVisibility(View.GONE);
				}
				
				//星星数据
				LinearLayout starContener = (LinearLayout) assignDataItem.findViewById(R.id.starContener);
				int childCount_S = starContener.getChildCount();
				int index_S = -1;
				List<RatingData> ratingData = new ArrayList<RatingData>();
				for (EvaluateInfo.StarData starData : evaluateData.getStarData()) {
					index_S++;
					LinearLayout starDataItem = null;
					if (index_S < childCount_S) {
						starDataItem = (LinearLayout) starContener.getChildAt(index);
					} else {
						starDataItem = (LinearLayout) View.inflate(context,R.layout.list_item_cm_stardata1, null);
						starContener.addView(starDataItem);
					}
					starDataItem.setVisibility(View.VISIBLE);
					TextView starNameTV = (TextView) starDataItem.findViewById(R.id.starNameTV);
					starNameTV.setText(starData.getName());
					
					RatingBar starRB = (RatingBar) starDataItem.findViewById(R.id.starRB);
					RatingData rateData = new RatingData(); //星星数据
					rateData.setRatingBar(starRB);  
					rateData.setCode(starData.getCode());
					ratingData.add(rateData);
					viewData.setRatingData(ratingData);
				}
				
				evaluateViewDatas.add(viewData);
				for (index_S++; index < childCount_S; index_S++) { // 把未使用的复用view设置成不可见
					starContener.getChildAt(index).setVisibility(View.GONE);
				}
			}
			for (index++; index < childCount; index++) { // 把未使用的复用view设置成不可见
				evaluateContener.getChildAt(index).setVisibility(View.GONE);
			}
		}
		mPostingdialog.dismiss();
	}
	
	private void initView() {
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar, 1);
		evaluateContener = (LinearLayout) findViewById(R.id.evaluateContener);

	}

	// 生成订车单
	public void onSend(View v) {
//		参数：id：订车单id  resultJson 评价拼接的json[{oaCarId:派车记录id,satisfaction:2,4,5,feedback:意见}]
		mPostingdialog = new ECProgressDialog(this, "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(orderId));
		
		List<EvaluateData> evaluateDatas = new ArrayList<EvaluateData>();
		for (EvaluateViewData data : evaluateViewDatas) {
			EvaluateData evaluateData = new EvaluateData();
			evaluateData.setFeedback(data.getEditText().getText().toString());
			String ratingString =  "";
			for (int i = 0; i < data.getRatingData().size(); i++) {
				if (i == data.getRatingData().size() - 1) {
					ratingString += (int)data.getRatingData().get(i).getRatingBar().getRating();
				} else {
					ratingString += (int)data.getRatingData().get(i).getRatingBar().getRating() + ",";
				}
			}
			evaluateData.setSatisfaction(ratingString);
			evaluateData.setOaCarId(data.getId());
			evaluateDatas.add(evaluateData);
		}
		map.put("resultJson", new ParameterValue(new Gson().toJson(evaluateDatas)));
		System.out.println(new Gson().toJson(evaluateDatas));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String sendFlag = UrlUtil.saveCarUserFeedback(ECApplication.getInstance().getV3Address(), map);
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
	protected int getLayoutId() {
		return R.layout.activity_cm_user_evaluate;
	}
}
