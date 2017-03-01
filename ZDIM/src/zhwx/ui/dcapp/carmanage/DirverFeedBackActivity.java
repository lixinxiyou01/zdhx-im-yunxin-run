package zhwx.ui.dcapp.carmanage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.android.datetimepicker.time.TimePickerDialog.OnTimeSetListener;
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

/**
 * @Title: DirverFeedBackActivity.java
 * @Package zhwx.ui.dcapp.carmanage
 * @Description: 司机反馈
 * @author Li.xin @ 中电和讯
 * @date 2016-3-29 下午4:13:44
 */
public class DirverFeedBackActivity extends BaseActivity implements OnTimeSetListener {

	private Activity context;

	private FrameLayout top_bar;

	private EditText realCountET,realAddressET, noteET;
	
	private TextView timeET;
	
	private TextView realcountTV,realAddressTV,realTimeTV;

	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;

	private HashMap<String, ParameterValue> map;

	private String TIMEPICKER_TAG = "timepicker";
	
	private String TIMEPICKER_TAG1 = "timepicker1";

	private String orderId;
	
	private Animation shake;
	
	private RadioGroup isBackRG;
	
	private LinearLayout backContenerLay;
	    
	private boolean isBack = false;
	
	private TextView backArriveTimeET;
	
	private EditText backUserCountET,backCarUsersET;
	
	private int timeFlag = 1;
	
	private int FLAG = 1;
	private int FLAG_BACK = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		shake = AnimationUtils.loadAnimation(context, R.anim.shake);//加载动画资源文件
		orderId = getIntent().getStringExtra("orderId");
		getTopBarView().setVisibility(View.GONE);
		initView();
	}

	private void initView() {
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar, 1);
		realCountET = (EditText) findViewById(R.id.realCountET);
		realAddressET = (EditText) findViewById(R.id.realAddressET);
		noteET = (EditText) findViewById(R.id.noteTV);
		timeET = (TextView) findViewById(R.id.timeET);
		timeET.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDateTimePicker(FLAG);
			}
		});
		
		realcountTV = (TextView) findViewById(R.id.realcountTV);
		realAddressTV = (TextView) findViewById(R.id.realAddressTV);
		realTimeTV = (TextView) findViewById(R.id.realTimeTV);
		
		backContenerLay = (LinearLayout) findViewById(R.id.backContenerLay);
		backArriveTimeET = (TextView) findViewById(R.id.backArriveTimeET);
		backArriveTimeET.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDateTimePicker(FLAG_BACK);
			}
		});
		backUserCountET = (EditText) findViewById(R.id.backUserCountET);
		backCarUsersET = (EditText) findViewById(R.id.backCarUsersET);
		
		isBackRG = (RadioGroup) findViewById(R.id.isBackRG);
		isBackRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				if(checkedId == R.id.noRB) { 
                    backContenerLay.setVisibility(View.GONE);
                    isBack = false;
                }else if(checkedId==R.id.yesRB) {
                    backContenerLay.setVisibility(View.VISIBLE);
                    isBack = true;
                } 
			}
		});
	}

	private void showDateTimePicker(int flag) {
		timeFlag = flag;
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
				DirverFeedBackActivity.this,
				calendar.get(java.util.Calendar.HOUR_OF_DAY),
				calendar.get(java.util.Calendar.MINUTE), true);
		if (!timePickerDialog.isAdded()) {
			timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);
		}
	}
	
	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		String timeString = (hourOfDay < 10 ? "0" + hourOfDay : hourOfDay)
				+ ":" + (minute < 10 ? "0" + minute : minute);
		if (timeFlag == FLAG) {
			timeET.setText(timeString);
		} else {
			backArriveTimeET.setText(timeString);
		}
	}

	// 生成订车单
	public void onSend(View v) {
		if (!checkOrderList()) {
			ToastUtil.showMessage("缺少必填信息");
			return;
		}
		mPostingdialog = new ECProgressDialog(this, "正在提交反馈");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance()
				.getV3LoginMap();
		map.put("assignCarId", new ParameterValue(orderId));
		map.put("realCount", new ParameterValue(realCountET.getText()
				.toString()));
		map.put("realTime", new ParameterValue(timeET.getText().toString()));
		map.put("realAddress", new ParameterValue(realAddressET.getText().toString()));
		map.put("note", new ParameterValue(noteET.getText().toString()));
		if (isBack) {
			map.put("backCount", new ParameterValue(backUserCountET.getText().toString()));
			map.put("backTime", new ParameterValue(backArriveTimeET.getText().toString()));
			map.put("backPersonList", new ParameterValue(backCarUsersET.getText().toString()));
		}
		

		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String sendFlag = UrlUtil.driverFeedback(
							ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							 if (sendFlag.contains("ok")) {
								ToastUtil.showMessage("已反馈");
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
	
	public boolean checkOrderList() {
		boolean pass = true; 
//		dateTV,timeTV,addressTV,userCountTV;
		if (StringUtil.isBlank(realCountET.getText().toString())) {
			realcountTV.startAnimation(shake); 
			pass = false;
		}
		if (StringUtil.isBlank(realAddressET.getText().toString())) {
			realAddressTV.startAnimation(shake); 
			pass = false;
		}
		if (StringUtil.isBlank(timeET.getText().toString())) {
			realTimeTV.startAnimation(shake); 
			pass = false;
		}
		return pass;
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_diver_feedback;
	}
}
