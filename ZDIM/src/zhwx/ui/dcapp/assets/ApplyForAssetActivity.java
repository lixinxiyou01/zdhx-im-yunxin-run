package zhwx.ui.dcapp.assets;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.DateUtil;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.capture.core.CaptureActivity;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.treelistview.bean.FileBean;
import zhwx.ui.dcapp.assets.adapter.IdAndNameSpinnerAdapter;
import zhwx.ui.dcapp.assets.adapter.SchoolSpinnerAdapter;
import zhwx.ui.dcapp.assets.model.ApplyInfos;

/**   
 * @Title: ApplyForAssetActivity.java 
 * @Package zhwx.ui.dcapp.assets
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年8月17日 下午3:52:22 
 */
public class ApplyForAssetActivity extends BaseActivity implements OnDateSetListener,OnClickListener {
	
	private Activity context;

	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;
	
	private String infoJson;
	
	private String applyFlag;
	
	private EditText demandET,reasonET;
	
	private TextView useDateET,kindTV;
	
	private Spinner applyDepatmentSP,assetKindSP,applySchoolSP,auditorSP;
	
	private ApplyInfos applyInfos;
    
    private String DATEPICKER_TAG = "datepicker";
    
    private String departmentId,assetKindId,userId,schoolId,applyDate;
    
    private Animation shake; //表单必填项抖动
    
    private Gson gson = new Gson();
    
	public static List<FileBean> datas;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		shake = AnimationUtils.loadAnimation(context, R.anim.shake);//加载动画资源文件
		initView();
		getData();
	}
	
	private void getData() {
		getInfo();   //获取默认信息
	}
	
	private void getInfo(){
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("operationCode", new ParameterValue("CheckManage"));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					infoJson = UrlUtil.getApplicationJson(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(infoJson);
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
	
	private void refreshData(String infoJson) {
		System.out.println(infoJson);
		if (infoJson.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		
		applyInfos = new Gson().fromJson(infoJson, ApplyInfos.class);
		if (applyInfos != null) {
			
			applyDepatmentSP.setAdapter(new IdAndNameSpinnerAdapter(context, applyInfos.getDepartments()));
			if (applyInfos.getUserDepartment()!=null) {
				for (int i = 0; i < applyInfos.getDepartments().size(); i++) {
					if (applyInfos.getUserDepartment().getId().equals(applyInfos.getDepartments().get(i).getId())) {
						applyDepatmentSP.setSelection(i);
						break;
					}
				}
			}
			
			applyDepatmentSP.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
					for (int i = 0; i < applyInfos.getDepartments().size(); i++) {
						if (i == position) {
							applyInfos.getDepartments().get(i).setSelected(true);
							departmentId = applyInfos.getDepartments().get(i).getId();
						} else {
							applyInfos.getDepartments().get(i).setSelected(false);
						}
					}
					BaseAdapter adapter =  (BaseAdapter) parent.getAdapter();
					adapter.notifyDataSetChanged();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
			});
			
			datas = new ArrayList<FileBean>();
			List<ApplyInfos.AssetsKind> tree = applyInfos.getAssetKinds();
			for (int i = 0; i < tree.size(); i++) {
				datas.add(new FileBean(Integer.parseInt(tree.get(i).getId().length() < 5 ? "0" : tree.get(i).getId().substring(23, 31)), Integer
						.parseInt(tree.get(i).getKindParentId().length() < 5 ? "0" : tree.get(i).getKindParentId().substring(23, 31)),
						tree.get(i).getName(), tree.get(i).getId(), ""));
			}
			
//			assetKindSP.setAdapter(new IdAndNameSpinnerAdapter(context, applyInfos.getAssetKinds()));
//			assetKindSP.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//				@Override
//				public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
//					for (int i = 0; i < applyInfos.getAssetKinds().size(); i++) {
//						if (i == position) {
//							applyInfos.getAssetKinds().get(i).setSelected(true);
//							assetKindId = applyInfos.getAssetKinds().get(i).getId();
//						} else {
//							applyInfos.getAssetKinds().get(i).setSelected(false);
//						}
//					}
//					BaseAdapter adapter =  (BaseAdapter) parent.getAdapter();
//					adapter.notifyDataSetChanged();
//				}
//
//				@Override
//				public void onNothingSelected(AdapterView<?> arg0) {
//					
//				}
//			});
			
			
			applySchoolSP.setAdapter(new SchoolSpinnerAdapter(context, applyInfos.getSchools()));
			applySchoolSP.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,final int schoolPosition, long id) {
					for (int i = 0; i < applyInfos.getSchools().size(); i++) {
						if (i == schoolPosition) {
							applyInfos.getSchools().get(i).setSelected(true);
							schoolId = applyInfos.getSchools().get(i).getId();
						} else {
							applyInfos.getSchools().get(i).setSelected(false);
						}
					}
					BaseAdapter adapter =  (BaseAdapter) parent.getAdapter();
					adapter.notifyDataSetChanged();
					
					auditorSP.setAdapter(new IdAndNameSpinnerAdapter(context, applyInfos.getSchools().get(schoolPosition).getAuditors()));
					auditorSP.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
							for (int i = 0; i < applyInfos.getSchools().get(schoolPosition).getAuditors().size(); i++) {
								if (i == position) {
									applyInfos.getSchools().get(schoolPosition).getAuditors().get(i).setSelected(true);
									userId = applyInfos.getSchools().get(schoolPosition).getAuditors().get(i).getId();
								} else {
									applyInfos.getSchools().get(schoolPosition).getAuditors().get(i).setSelected(false);
								}
							}
							BaseAdapter adapter =  (BaseAdapter) parent.getAdapter();
							adapter.notifyDataSetChanged();
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							
						}
					});
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
			});
		}
		mPostingdialog.dismiss();
	}
	private void initView() {
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"申请资产", this);
		setImmerseLayout(getTopBarView(),1);
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
				ApplyForAssetActivity.this, calendar.get(java.util.Calendar.YEAR),
				calendar.get(java.util.Calendar.MONTH),
				calendar.get(java.util.Calendar.DAY_OF_MONTH));
		useDateET = (TextView) findViewById(R.id.useDateET);
		kindTV = (TextView) findViewById(R.id.kindTV);
		useDateET.setText(DateUtil.getCurrDateString());
		useDateET.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (!datePickerDialog.isAdded()) {
					datePickerDialog.show(getFragmentManager(),DATEPICKER_TAG);
				}
			}
		});
		demandET = (EditText) findViewById(R.id.reasonET);
		reasonET = (EditText) findViewById(R.id.instructionET);
		
		applyDepatmentSP = (Spinner) findViewById(R.id.applyDepatmentSP);
		assetKindSP = (Spinner) findViewById(R.id.assetKindSP);
		applySchoolSP = (Spinner) findViewById(R.id.applySchoolSP);
		auditorSP = (Spinner) findViewById(R.id.auditorSP);
	}
	
	
	
	//提交订车单
	public void onOrder(View v) {
		//json{"departmentId":申请部门id,"assetKindId":资产类别,"userId":审核人id
		//"schoolId":被申请校区id,"applyDate":申请日期,"demand":需求,"reason":用途}
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		Map<String, String> jsonMap = new HashMap<String, String>();
		jsonMap.put("departmentId", departmentId);
		jsonMap.put("assetKindId", assetKindId);
		jsonMap.put("userId", userId);
		jsonMap.put("schoolId", schoolId);
		jsonMap.put("applyDate", useDateET.getText().toString());
		jsonMap.put("demand", demandET.getText().toString());
		jsonMap.put("reason", reasonET.getText().toString());
		map.put("resultJson", new ParameterValue(gson.toJson(jsonMap)));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					applyFlag = UrlUtil.saveApplication(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (applyFlag.contains("ok")) {
								ToastUtil.showMessage("申请已提交");
								finish();
							} else {
								ToastUtil.showMessage(applyFlag);
							}
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("提交出错，请重试");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
			if (data!=null) {
				assetKindId = data.getStringExtra("userId");
				kindTV.setText(data.getStringExtra("userName"));
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	    
	}
	
	@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear,
			int dayOfMonth) {
		String date = year + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
		if (DATEPICKER_TAG.equals(dialog.getTag())) {
			useDateET.setText(date);
		} 
	}
	
	/**
	 * 退出提示
	 */
	private void showTips() {
		ECAlertDialog buildAlert = ECAlertDialog.buildColorButtonAlert(context, "放弃本次申请", "#3989fc", "", "取消", "", "确认", "#3989fc", null, new DialogInterface.OnClickListener() {
			 @Override
             public void onClick(DialogInterface dialog, int which) {
				 	finish();
             }
         });
         buildAlert.setMessage("退出后当前订车单内容不会保存，确认退出吗？");
         buildAlert.show();  
    }
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showTips();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	
	public void onSelectKind(View v) {
		startActivityForResult(new Intent(context, AssetKindSelectActivityApply.class), 101);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			startActivity(new Intent(context, CaptureActivity.class));
			break;
		}
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_as_apply;
	}

}
