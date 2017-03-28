package zhwx.ui.dcapp.carmanage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.android.datetimepicker.time.TimePickerDialog.OnTimeSetListener;
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
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.dialog.ECListDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.carmanage.model.AssignCarData;
import zhwx.ui.dcapp.carmanage.model.CarInfo.CarData;

/**
 * @Title: SendCarActivity.java
 * @Package zhwx.ui.dcapp.carmanage
 * @Description: 管理员派车页面
 * @author Li.xin @ 中电和讯
 * @date 2016-3-29 上午8:58:17
 */
public class SendCarActivity extends BaseActivity implements OnDateSetListener, OnTimeSetListener {

	private Activity context;
	
	private FrameLayout top_bar;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;
	
	private String DATEPICKER_TAG = "datepicker";
	
    private String TIMEPICKER_TAG = "timepicker";

	private EditText carAddressET, remarkET;
	
	private TextView carNameTV,carNumTV,limitCountTV,carDateTimeET,dirverET,carAddressTV;
	
	private TextView dirverTV,timeTV;
	
	private ImageView carHeadIV;
	
    private ListView carUserLV;
    
    private String carDirverId;
    
    private String orderId;
    
    private String dateString,timeString;
    
    private CarData carData;
    
    private ImageLoader mImageLoader;
    
    private String dirverJson;
    
    private AssignCarData assignCarData;
    
    private int dirverPosition = -1;
    
    private Animation shake; //表单必填项抖动
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		shake = AnimationUtils.loadAnimation(context, R.anim.shake);//加载动画资源文件
		mImageLoader = new ImageLoader(context);
		carData = (CarData) getIntent().getBundleExtra("data").getSerializable("carInfo");
		orderId = getIntent().getStringExtra("orderId");
		getTopBarView().setVisibility(View.GONE);
		initView();
		getData();
	}

	private void getData() {
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(orderId));
		map.put("carId", new ParameterValue(carData.getCarId()));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					dirverJson = UrlUtil.toAssignCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(dirverJson);
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

	private void refreshData(String dirverJson) {
		System.out.println(dirverJson);
		if (!dirverJson.contains("<html>")) {
			assignCarData = new Gson().fromJson(dirverJson, AssignCarData.class);
		}
		mPostingdialog.dismiss();
	}
	
	
	private void initView() {
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		dirverET = (TextView) findViewById(R.id.dirverET);
		carDateTimeET = (TextView) findViewById(R.id.carDateTimeET);
		carDateTimeET.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDateTimePicker();
			}
		});
		carAddressET = (EditText) findViewById(R.id.carAddressET);
		remarkET = (EditText) findViewById(R.id.remarkET);
		carNameTV = (TextView) findViewById(R.id.carNameTV);
		carNameTV.setText(carData.getCarName());
		carNumTV = (TextView) findViewById(R.id.carNumTV);
		carNumTV.setText(carData.getCarNum());
		limitCountTV = (TextView) findViewById(R.id.limitCountTV);
		limitCountTV.setText("限乘" + carData.getLimitCount() + "人");
		carHeadIV = (ImageView) findViewById(R.id.carHeadIV);
		mImageLoader.DisplayImage(ECApplication.getInstance().getV3Address()+carData.getCarPicUrl(), carHeadIV, false);
		dirverTV = (TextView) findViewById(R.id.dirverTV);
		timeTV = (TextView) findViewById(R.id.timeTV);
		carAddressTV = (TextView) findViewById(R.id.carAddressTV);
	}

	
	
	private void showDateTimePicker() {
//		java.util.Calendar calendar = java.util.Calendar.getInstance();
//		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
//				SendCarActivity.this, calendar.get(java.util.Calendar.YEAR),
//				calendar.get(java.util.Calendar.MONTH),
//				calendar.get(java.util.Calendar.DAY_OF_MONTH));
//		if (!datePickerDialog.isAdded()) {
//			datePickerDialog.show(getFragmentManager(),DATEPICKER_TAG);
//		}
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
				SendCarActivity.this, calendar.get(java.util.Calendar.HOUR_OF_DAY),
				calendar.get(java.util.Calendar.MINUTE), true);
		if (!timePickerDialog.isAdded()) {
			timePickerDialog.show(getFragmentManager(),TIMEPICKER_TAG);
		}
	}
	
	@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear,
			int dayOfMonth) {
		dateString = year + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
				SendCarActivity.this, calendar.get(java.util.Calendar.HOUR_OF_DAY),
				calendar.get(java.util.Calendar.MINUTE), true);
		if (!timePickerDialog.isAdded()) {
			timePickerDialog.show(getFragmentManager(),TIMEPICKER_TAG);
		}
	}
	
	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		timeString =  (hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute);
		carDateTimeET.setText(timeString);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		
	}
	
	//添加司机
	public void onAddUser(View v) {
		final List<String> names = new ArrayList<String>();
		for (int i = 0; i < assignCarData.getDriverData().size(); i++) {
			names.add(assignCarData.getDriverData().get(i).getDriverName());
		}
		final ECListDialog dialog;
		dialog = new ECListDialog(this , names ,dirverPosition);
        dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
            @Override
            public void onDialogItemClick(Dialog d, int position) {
            	dirverPosition = position;
            	carDirverId = assignCarData.getDriverData().get(position).getDriverId();
            	dialog.dismiss();
            	dirverET.setText(names.get(position));
            }
        });
        dialog.setTitle("选择司机","#f28d2b");
        dialog.show();
	}
	
	//生成订车单
	public void onSend(View v) {
		if (!checkOrderList()) {
			ToastUtil.showMessage("缺少必填信息");
			return;
		}
		
		mPostingdialog = new ECProgressDialog(this, "正在生成订派车单");
		mPostingdialog.show();
//		id，carId，driverId，useTime（时分），useAddress，leaveTime（时分）
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(orderId));
		map.put("carId", new ParameterValue(carData.getCarId()));
		map.put("driverId", new ParameterValue(carDirverId));
		map.put("useTime", new ParameterValue(carDateTimeET.getText().toString()));
		map.put("useAddress", new ParameterValue(carAddressET.getText().toString()));
		map.put("leaveTime", new ParameterValue(carDateTimeET.getText().toString()));
		
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String sendFlag = UrlUtil.saveAssignCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
//							if (sendFlag.contains("ok")) {
//								
//							}
							ToastUtil.showMessage("订车单已生成！");
							setResult(105);
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
	
	public boolean checkOrderList() {
		boolean pass = true; 
//		dirverTV,timeTV;
		if (StringUtil.isBlank(dirverET.getText().toString())) {
			dirverTV.startAnimation(shake); 
			pass = false;
		}
		if (StringUtil.isBlank(carDateTimeET.getText().toString())) {
			timeTV.startAnimation(shake); 
			pass = false;
		}
		if (StringUtil.isBlank(carAddressET.getText().toString())) {
			carAddressTV.startAnimation(shake); 
			pass = false;
		}
		return pass;
	}
	@Override
	protected int getLayoutId() {
		return R.layout.activity_sendcar;
	}
}
