package zhwx.ui.dcapp.storeroom;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
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

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.DensityUtil;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.assets.adapter.IdAndNameSpinnerAdapter;
import zhwx.ui.dcapp.assets.model.IdAndName;
import zhwx.ui.dcapp.storeroom.model.ApplyJsonModel;
import zhwx.ui.dcapp.storeroom.model.ApplyRecordInfo;
import zhwx.ui.dcapp.storeroom.model.Goods;
import zhwx.ui.dcapp.storeroom.view.QuantityView;

/**
 * @Title: ApplyForAssetActivity.java
 * @Package com.lanxum.hzth.im.ui.v3.assets
 * @author Li.xin @ zdhx
 * @date 2016年8月17日 下午3:52:22
 */
public class ApplyForSActivity extends BaseActivity implements
		OnDateSetListener, OnClickListener {

	private Activity context;

	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;

	private HashMap<String, ParameterValue> map;

	private String infoJson;

	private String applyFlag;

	private EditText applyCodeET, noteET, reasonET;

	private Spinner applyDepatmentSP, checkUserSp, applyKindSP;

	private ApplyRecordInfo applyInfos;

	private String DATEPICKER_TAG = "datepicker";

	private String departmentId, assetKindId, userId, schoolId, applyDate;

	private Animation shake; // 表单必填项抖动

	private static ListView grantLV;

	private TextView emptyTV, dateET;
	
	private ApplyJsonModel applyJsonModel = new ApplyJsonModel();

	public static List<Goods.GridModelBean> selectedGoodList = new ArrayList<Goods.GridModelBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		shake = AnimationUtils.loadAnimation(context, R.anim.shake);// 加载动画资源文件
		initView();
		getData();
	}

	private void getData() {
		getInfo(); // 获取默认信息
	}

	private void getInfo() {
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(ECApplication.getInstance().getInstance().getCurrentIMUser().getV3Id()));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					infoJson = UrlUtil.getNewApplyRecordInfo(ECApplication
							.getInstance().getV3Address(), map);
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
	
	/**
	 * 提交申领单
	 */
	public void apply() {
		if (StringUtil.isBlank(applyCodeET.getEditableText().toString())) {
			ToastUtil.showMessage("申领单号不能为空");
			return;
		}
		if (StringUtil.isBlank(applyJsonModel.getDeptCheckUserId())) {
			ToastUtil.showMessage("审核人不能为空");
			return;
		}
		if (selectedGoodList.size() == 0) {
			ToastUtil.showMessage("还没选择物品呢！");
			return;
		}
		
		applyJsonModel.setCode(applyCodeET.getEditableText().toString());
		applyJsonModel.setDate(dateET.getText().toString());
		applyJsonModel.setReason(reasonET.getEditableText().toString());
		applyJsonModel.setUserId(ECApplication.getInstance().getCurrentIMUser().getV3Id());
		applyJsonModel.setNote(noteET.getEditableText().toString());
		List<ApplyJsonModel.GoodInfo> giList = new ArrayList<ApplyJsonModel.GoodInfo>();
		for (int i = 0; i < selectedGoodList.size(); i++) {
			ApplyJsonModel.GoodInfo gi = new ApplyJsonModel.GoodInfo();
			gi.setCount(selectedGoodList.get(i).getCount()+"");
			gi.setGoodsInfoId(selectedGoodList.get(i).getId());
			giList.add(gi);
		}
		applyJsonModel.setGoodsInfos(giList);
		String resutJson = new Gson().toJson(applyJsonModel);
		System.out.println(resutJson);
		mPostingdialog = new ECProgressDialog(this, "正在提交");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance()
				.getV3LoginMap();
		map.put("resultJson", new ParameterValue(resutJson));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					infoJson = UrlUtil.saveApplyReceiveRecord(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							ToastUtil.showMessage("提交成功");
							finish();
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
		if (infoJson.contains("</html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}

		applyInfos = new Gson().fromJson(infoJson, ApplyRecordInfo.class);
		if (applyInfos != null) {
			applyCodeET.setText(applyInfos.getApplymessage().get(0).getCode());
			applyJsonModel.setCode(applyInfos.getApplymessage().get(0).getCode());
			dateET.setText(applyInfos.getApplymessage().get(0).getDate());
			applyDepatmentSP.setAdapter(new IdAndNameSpinnerAdapter(context,applyInfos.getMyDepartmentList()));
			applyDepatmentSP.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,final int arg2, long arg3) {
					applyJsonModel.setDepartmentId(applyInfos.getMyDepartmentList().get(arg2).getDepartmentId());
					applyJsonModel.setDeptCheckUserId("");
					//TODO 部门审核人
					if (applyInfos.getMyDepartmentList().get(arg2).getUserList() != null) {
						checkUserSp.setAdapter(new IdAndNameSpinnerAdapter(context,applyInfos.getMyDepartmentList().get(arg2).getUserList()));
						checkUserSp.setOnItemSelectedListener(new OnItemSelectedListener() {
							
							@Override
							public void onItemSelected(AdapterView<?> parent, View view,
									int position, long arg3) {
								applyJsonModel.setDeptCheckUserId(applyInfos.getMyDepartmentList().get(arg2).getUserList().get(position).getCheckUserId());
							}
							
							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								
							}
						});
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
			});
			List<IdAndName> andNames3 = new ArrayList<IdAndName>();
			andNames3.add(new IdAndName("grsl", "个人申领"));
			andNames3.add(new IdAndName("bmsl", "部门申领"));
			applyKindSP.setAdapter(new IdAndNameSpinnerAdapter(context,andNames3));
			applyKindSP.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					if (arg2 == 0) {
						applyJsonModel.setKind("grsl");
					} else {
						applyJsonModel.setKind("bmsl");
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
			});
		}
		mPostingdialog.dismiss();
	}

	private void initView() { 
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, "提交",
				"申领单", this);
		setImmerseLayout(getTopBarView(), 1);
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
				ApplyForSActivity.this, calendar.get(java.util.Calendar.YEAR),
				calendar.get(java.util.Calendar.MONTH),
				calendar.get(java.util.Calendar.DAY_OF_MONTH));
		applyCodeET = (EditText) findViewById(R.id.applyCodeET);
		dateET = (TextView) findViewById(R.id.dateET);
		dateET.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!datePickerDialog.isAdded()) {
					datePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
				}
			}
		});
		noteET = (EditText) findViewById(R.id.noteET);
		reasonET = (EditText) findViewById(R.id.reasonET);
		applyDepatmentSP = (Spinner) findViewById(R.id.applyDepatmentSP);
		checkUserSp = (Spinner) findViewById(R.id.applySchoolSP);
		applyKindSP = (Spinner) findViewById(R.id.auditorSP);

		emptyTV = (TextView) findViewById(R.id.emptyTV);
		grantLV = (ListView) findViewById(R.id.grantLV);
		grantLV.setAdapter(new OrderListAdapter());
		grantLV.setEmptyView(emptyTV);
		Tools.setListViewHeightBasedOnChildren(grantLV);
	}

	// 添加物品
	public void onAddGoods(View v) {
		startActivity(new Intent(context, GoodsKindActivity.class));
	}

	public class OrderListAdapter extends BaseAdapter {

		public OrderListAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return selectedGoodList.size();
		}

		@Override
		public Goods.GridModelBean getItem(int position) {
			return selectedGoodList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sm_applybox, null);
				holder = new ViewHolder();
				holder.goodsNameTV = (TextView) convertView.findViewById(R.id.goodsNameTV);
				holder.goodsUnitTV = (TextView) convertView.findViewById(R.id.goodsUnitTV);
				holder.quantityView1 = (QuantityView) convertView.findViewById(R.id.quantityView1);
				holder.buttonContentLay = (LinearLayout) convertView.findViewById(R.id.buttonContentLay);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.goodsNameTV.setText(getItem(position).getName());
			holder.goodsUnitTV.setText(getItem(position).getUnit());

			// 动态添加操作按钮
			holder.buttonContentLay.removeAllViews();
			List<Button> btns = getOrderButtonList(position);
			for (Button button : btns) {
				holder.buttonContentLay.addView(button);
			}
			addListener(holder, position, convertView);
			return convertView;
		}

		/**
		 * holerView 添加监听器
		 * 
		 * @param holder
		 * @param position
		 * @param view
		 */
		private void addListener(final ViewHolder holder, final int position,
				final View view) {
			
			holder.quantityView1.setOnQuantityChangeListener(new QuantityView.OnQuantityChangeListener() {
				
				@Override
				public void onQuantityChanged(int newQuantity, boolean programmatically) {
					selectedGoodList.get(position).setCount(newQuantity);
				}
				
				@Override
				public void onLimitReached() {
					
				}
			});
		}

		private class ViewHolder {
			private TextView goodsNameTV, goodsUnitTV;
			private LinearLayout buttonContentLay;
			private QuantityView quantityView1;
		}

	}

	public List<Button> getOrderButtonList(final int position) {
		List<Button> btnList = new ArrayList<Button>();
		Button removeBT = getOrderButton("移除");
		removeBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				selectedGoodList.remove(position);
				OrderListAdapter adapter = (OrderListAdapter) grantLV.getAdapter();
				if(adapter != null) {
					adapter.notifyDataSetChanged();
					Tools.setListViewHeightBasedOnChildren(grantLV);
				}
			}
		});
		btnList.add(removeBT);
		return btnList;
	}

	public Button getOrderButton(String text) {
		LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(30));
		params.setMargins(0, 0, DensityUtil.dip2px(10), 0);
		Button button = new Button(context);
		button.setText(text);
		button.setTextColor(Color.parseColor("#555555"));
		button.setBackgroundResource(R.drawable.btn_selector_ordercar);
		button.setLayoutParams(params);
		return button;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 101) {
			if (data != null) {
				assetKindId = data.getStringExtra("userId");
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		OrderListAdapter adapter = (OrderListAdapter) grantLV.getAdapter();
		if(adapter != null) {
			adapter.notifyDataSetChanged();
			Tools.setListViewHeightBasedOnChildren(grantLV);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		selectedGoodList.clear();
	}

	@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear,
			int dayOfMonth) {
		String date = year
				+ "-"
				+ ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-"
				+ (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
		if (DATEPICKER_TAG.equals(dialog.getTag())) {
			dateET.setText(date);
			applyJsonModel.setDate(date);
		}
	}

	public static void putGoods(Goods.GridModelBean goods) {
		boolean key = true;
		for (int i = 0; i < selectedGoodList.size(); i++) {
			if(selectedGoodList.get(i).getId().equals(goods.getId())) {
				selectedGoodList.remove(i);
				key = false;
				break;
			}
		}
		if (key) {
			selectedGoodList.add(goods);
		} 
	}

	/**
	 * 退出提示
	 */
	private void showTips() {
		ECAlertDialog buildAlert = ECAlertDialog.buildColorButtonAlert(context,
				"放弃本次申请", "#3573a2", "", "取消", "", "确认", "#3573a2", null,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		buildAlert.setMessage("退出后当前页面内容不会保存，确认退出吗？");
		buildAlert.show();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			showTips();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.text_right:
			apply();
			break;
		}
	}
	
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_sm_apply;
	}

}
