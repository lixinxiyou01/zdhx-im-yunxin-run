package zhwx.ui.dcapp.checkin;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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
 * @Title: SupplementSaveActivity.java 
 * @Package com.zdhx.edu.im.ui.v3.checkin
 * @author Li.xin @ zdhx
 * @date 2016年9月26日 下午12:55:24 
 */
public class SupplementSaveActivity extends BaseActivity implements OnClickListener ,OnTimeSetListener{
	
	
	private TextView realTimeTV;
	
	private String TIMEPICKER_TAG = "timepicker";
	
	private String realTime = "";
	
	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();
	
	private ECProgressDialog mPostingdialog;
	
	private String json;
	
	private EditText noteET;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_supplementsave;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setBackGroundColor(R.color.main_bg_checkin);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"上班补签", this);
		findView();
	}
	
	/**
	 * 
	 */
	private void findView() {
		realTimeTV = (TextView) findViewById(R.id.realTimeTV);
		realTimeTV.setOnClickListener(this);
		noteET = (EditText) findViewById(R.id.noteET);
	}
	
	public void onSend(View v) {
		if (StringUtil.isBlank(realTimeTV.getText().toString())) {
			ToastUtil.showMessage("请选择实际上班时间");
			return;
		}
		if (StringUtil.isBlank(noteET.getText().toString())) {
			ToastUtil.showMessage("请填写补签说明");
			return;
		}
		
		mPostingdialog = new ECProgressDialog(this, "正在提交");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
		map.put("address", new ParameterValue(getIntent().getStringExtra("address")));
		map.put("note", new ParameterValue(noteET.getText().toString()));
		map.put("equipType", new ParameterValue(android.os.Build.MODEL));
		map.put("startTime", new ParameterValue(realTimeTV.getText().toString()));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.supplementSave(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							ToastUtil.showMessage("补签成功");
							setResult(RESULT_OK);
							finish();
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("提交失败，请稍后重试");
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
		case R.id.realTimeTV:
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(SupplementSaveActivity.this, calendar.get(java.util.Calendar.HOUR_OF_DAY),
					calendar.get(java.util.Calendar.MINUTE), true);
			timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);
			break;
		}
	}
	
	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		realTime =  (hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute);
		realTimeTV.setText(realTime);
	}
}
