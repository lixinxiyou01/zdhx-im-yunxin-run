package zhwx.ui.dcapp.storeroom;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.photoselector.model.PhotoModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.DateUtil;
import zhwx.common.util.DensityUtil;
import zhwx.common.util.FileUpLoadCallBack;
import zhwx.common.util.IMUtils;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.compressImg.PictureUtil;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.assets.adapter.IdAndNameSpinnerAdapter;
import zhwx.ui.dcapp.assets.model.IdAndName;
import zhwx.ui.dcapp.assets.view.WaterImageUtil;
import zhwx.ui.dcapp.assets.view.pancel.PicelBoradActivity;
import zhwx.ui.dcapp.storeroom.model.Goods;
import zhwx.ui.dcapp.storeroom.model.GrantResult;
import zhwx.ui.dcapp.storeroom.model.MyApplyDetail;
import zhwx.ui.dcapp.storeroom.model.ProvideGoodsDetal;
import zhwx.ui.dcapp.storeroom.view.QuantityView;

import static com.netease.nim.uikit.recent.RecentContactsFragment.adapter;

/**
 * @Title: ApplyForAssetActivity.java
 * @Package com.zdhx.edu.im.ui.v3.assets
 * @author Li.xin @ zdhx
 * @date 2016年8月17日 下午3:52:22
 */
public class GrantForApplyActivity extends BaseActivity implements
		OnDateSetListener, OnClickListener {

	private Activity context;

	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;

	private HashMap<String, ParameterValue> map;

	private HashMap<String, ParameterValue> loginMap;

	private String indexJson,dataJson;

	private EditText applyCodeET, noteET, getUserET,buildUserET;

	private String DATEPICKER_TAG = "datepicker";
	
	private String DATEPICKER_TAG_B = "datepicker_b";

	private Animation shake; // 表单必填项抖动

	private static ListView grantLV;

	private TextView emptyTV,outDateTV,storeTV,buildDataTV;
	
	private Spinner auditorSP;
	
	private MyApplyDetail detail;

	private String id;
	
	private ECAlertDialog buildAlert = null;
	
	private int allPosition = 0;
	
	private String storeId = "";
	
	private GrantResult grantRsult = new GrantResult();
	
	private ProvideGoodsDetal provideGoodsDetal;

	private RadioGroup checkRG;

	private boolean writeNow;
	
	List<IdAndName> andNames3 = new ArrayList<IdAndName>();

	private List<File> sendFiles = new ArrayList<File>();

	public static Bitmap sourBitmap;

	private List<PhotoModel> nowPhotos = new ArrayList<PhotoModel>();

	private List<Bitmap> nowBmp = new ArrayList<Bitmap>();

	public static final int MAX_IMG_COUNT = 1; // 选择图数量上限

	private ImageView signIV;

	private File sdCard = android.os.Environment.getExternalStorageDirectory();
	
	public static List<Goods.GridModelBean> selectedGoodList = new ArrayList<Goods.GridModelBean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		shake = AnimationUtils.loadAnimation(context, R.anim.shake);// 加载动画资源文件
		id = getIntent().getStringExtra("id");
		initView();
		getData();
	}

	private void getData() {
		getNotice(); // 获取默认信息
	}

	private void getNotice(){
		mPostingdialog = new ECProgressDialog(this, "正在加载");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getApplyRecordView(ECApplication.getInstance().getV3Address(), map);
					
					dataJson = UrlUtil.getProvideGoodsDetal(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(indexJson);
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
		if (StringUtil.isBlank(storeId)) {
			ToastUtil.showMessage("请选择仓库");
			return;
		}
		
		if (detail.getApplygoodsList().size() == 0) {
			ToastUtil.showMessage("还没选择物品呢！");
			return;
		}
		
		grantRsult.setCode(applyCodeET.getEditableText().toString());  //出库单号
		grantRsult.setApplyReceiveId(id);  //申领单号
		grantRsult.setWarehouseId(storeId);
		grantRsult.setNote(noteET.getEditableText().toString());
		grantRsult.setDate(outDateTV.getText().toString());
		grantRsult.setReceiveUserId(detail.getApplyreceiverecord().get(0).getUserId());

		List<GrantResult.GoodsInfosBean> giList = new ArrayList<GrantResult.GoodsInfosBean>();
		for (int i = 0; i < detail.getApplygoodsList().size(); i++) {
			GrantResult.GoodsInfosBean gi = new GrantResult.GoodsInfosBean();
			gi.setCount(detail.getApplygoodsList().get(i).getCount()+"");
			gi.setGoodsInfoId(detail.getApplygoodsList().get(i).getId());
			gi.setSum(detail.getApplygoodsList().get(i).getMoney()+"");
			giList.add(gi);
		}
		grantRsult.setGoodsInfos(giList);
		String resutJson = new Gson().toJson(grantRsult);
		System.out.println(resutJson);
		mPostingdialog = new ECProgressDialog(this, "正在提交");
		mPostingdialog.show();
		loginMap = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map = new HashMap<String, ParameterValue>();
		map.put("resultJson", new ParameterValue(resutJson));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					if(sendFiles.size() == 0) {
						loginMap.putAll(map);
						UrlUtil.saveProvideGoods(ECApplication.getInstance().getV3Address(),loginMap);
					} else {
						UrlUtil.saveProvideGoods(ECApplication.getInstance().getV3Address(), sendFiles, loginMap, map, new FileUpLoadCallBack() {
							@Override
							public void upLoadProgress(int fileCount, int currentIndex, int currentProgress,int allProgress) {

							}
						});
					}
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
		provideGoodsDetal = new Gson().fromJson(dataJson, ProvideGoodsDetal.class);
		detail = new Gson().fromJson(indexJson, MyApplyDetail.class);
		if (detail != null) {
			applyCodeET.setText(provideGoodsDetal.getWarehouseRecordData().get(0).getCode()); 
			outDateTV.setText(DateUtil.getCurrDateString());
			getUserET.setText(detail.getApplyreceiverecord().get(0).getUserName());
			buildUserET.setText(ECApplication.getInstance().getCurrentIMUser().getName());
			buildDataTV.setText(DateUtil.getCurrDateString());
			grantLV.setAdapter(new OrderListAdapter());
			Tools.setListViewHeightBasedOnChildren(grantLV);
			
			andNames3.clear();
			Iterator<Map.Entry<String, String>> it = provideGoodsDetal.getOutwarehouseKind().entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, String> entry = it.next();
				System.out.println("键key ："+entry.getKey()+" value ："+entry.getValue());
				andNames3.add(new IdAndName(entry.getKey(), entry.getValue()));
			}
			auditorSP.setAdapter(new IdAndNameSpinnerAdapter(context,andNames3));
			auditorSP.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					grantRsult.setOutKind(andNames3.get(arg2).getId());
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
			});
			
			for (int i = 0; i < andNames3.size(); i++) {
				if (andNames3.get(i).getName().equals(detail.getApplyreceiverecord().get(0).getKindValue())) {
					auditorSP.setSelection(i);
				}
			}
		}
		
		mPostingdialog.dismiss();
	}

	private void initView() {
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, "提交","申领发放", this);
		setImmerseLayout(getTopBarView(), 1);
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
				GrantForApplyActivity.this, calendar.get(java.util.Calendar.YEAR),
				calendar.get(java.util.Calendar.MONTH),
				calendar.get(java.util.Calendar.DAY_OF_MONTH));
		applyCodeET = (EditText) findViewById(R.id.applyCodeET);
		outDateTV = (TextView) findViewById(R.id.outDateTV);
		outDateTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!datePickerDialog.isAdded()) {
					datePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
				}
			}
		});

		checkRG = (RadioGroup) findViewById(R.id.checkRG);
		checkRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int radioId) {
				if (radioId == R.id.writeNowRB) {
					writeNow = true;
					signIV.setVisibility(View.VISIBLE);
				} else {
					writeNow = false;
					signIV.setVisibility(View.GONE);
				}
			}
		});
		buildDataTV = (TextView) findViewById(R.id.buildDataTV);
		emptyTV = (TextView) findViewById(R.id.emptyTV);
		noteET = (EditText) findViewById(R.id.noteET);
		buildUserET = (EditText) findViewById(R.id.buildUserET);
		getUserET = (EditText) findViewById(R.id.getUserET);

		signIV = (ImageView) findViewById(R.id.fitImageViewForAs2);
		signIV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(context, PicelBoradActivity.class),121);
			}
		});

		storeTV = (TextView) findViewById(R.id.storeTV);
		storeTV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(context, StoreListActivity.class).putExtra("id", id), 101);
			}
		});
		auditorSP = (Spinner) findViewById(R.id.auditorSP);
		grantLV = (ListView) findViewById(R.id.grantLV);
		grantLV.setEmptyView(emptyTV);
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
			return detail.getApplygoodsList().size();
		}

		@Override
		public MyApplyDetail.ApplygoodsListBean getItem(int position) {
			return detail.getApplygoodsList().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sm_grant_apply, null);
				holder = new ViewHolder();
				holder.goodsNameTV = (TextView) convertView.findViewById(R.id.goodsNameTV);
				holder.goodsUnitTV = (TextView) convertView.findViewById(R.id.goodsUnitTV);
				holder.quantityView1 = (QuantityView) convertView.findViewById(R.id.quantityView1);
				holder.buttonContentLay = (LinearLayout) convertView.findViewById(R.id.buttonContentLay);
				holder.moneyET = (EditText) convertView.findViewById(R.id.moneyET);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.goodsNameTV.setText(getItem(position).getGoodsInfoName());
			holder.goodsUnitTV.setText(getItem(position).getUnit());
			holder.quantityView1.setQuantity(getItem(position).getCount());
			holder.moneyET.setText(detail.getApplygoodsList().get(position).getMoney()+"");
			// 动态添加操作按钮
			holder.buttonContentLay.removeAllViews();
			List<TextView> btns = getOrderButtonList(position);
			for (TextView button : btns) {
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
					detail.getApplygoodsList().get(position).setCount(newQuantity);
				}
				
				@Override
				public void onLimitReached() {
					
				}
			});
			
			holder.moneyET.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					allPosition = position;
					showMoneyEdit();
				}
			});
			
		}

		private class ViewHolder {
			private TextView goodsNameTV, goodsUnitTV;
			private LinearLayout buttonContentLay;
			private QuantityView quantityView1;
			private EditText moneyET;
		}

	}

	public List<TextView> getOrderButtonList(final int position) {
		List<TextView> btnList = new ArrayList<TextView>();
		TextView removeBT = getOrderButton("移除");
		removeBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				detail.getApplygoodsList().remove(position);
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

	public TextView getOrderButton(String text) {
		LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(30));
		params.setMargins(0, 0, DensityUtil.dip2px(10), 0);
		TextView button = new TextView(context);
		button.setText(text);
		button.setTextColor(Color.parseColor("#555555"));
		button.setBackgroundResource(R.drawable.btn_selector_ordercar);
		button.setGravity(Gravity.CENTER);
		button.setLayoutParams(params);
		return button;
	}

	// 输入金额
	private void showMoneyEdit() {
		buildAlert = ECAlertDialog.buildColorButtonAlert(this,"输入金额","#3573a2","","取消","","确认","#3573a2", null, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	if (StringUtil.isNotBlank(((EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText))).getText().toString())) {
            		detail.getApplygoodsList().get(allPosition).setMoney(Integer.parseInt(((EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText))).getText().toString()));
            		OrderListAdapter adapter = (OrderListAdapter) grantLV.getAdapter();
            		if(adapter != null) {
            			adapter.notifyDataSetChanged();
            			Tools.setListViewHeightBasedOnChildren(grantLV);
            		}
            	}
            }
        });
        buildAlert.setTitle("输入金额", "#3573a2");
        buildAlert.setCanceledOnTouchOutside(false);
        buildAlert.setContentView(R.layout.editmoney_dialog);
        final EditText editText = (EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText));
        TextView delectTV = (TextView) buildAlert.getWindow().findViewById(R.id.delectTV);
        delectTV.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        if(!buildAlert.isShowing()){
            buildAlert.show();
            editText.setSelection(editText.getText().length());
            editText.setSelectAllOnFocus(true);
        }
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 101) {
			if (data != null) {
				storeId = data.getStringExtra("storeId");
				storeTV.setText(data.getStringExtra("storeName"));
			}

			if (requestCode == 0 && resultCode == RESULT_OK) {
				if (data != null && data.getExtras() != null) {
					@SuppressWarnings("unchecked")
					List<PhotoModel> photos = (List<PhotoModel>) data.getExtras()
							.getSerializable("photos");
					for (int i = 0; i < nowPhotos.size(); i++) {
						if (nowPhotos.get(i) == null) {
							nowPhotos.remove(i);
						}
					}
					for (PhotoModel photoModel : photos) {
						nowPhotos.add(photoModel);
					}
					if (nowPhotos.size() < MAX_IMG_COUNT) {
						nowPhotos.add(null);
					}
					if (nowPhotos == null || nowPhotos.isEmpty())
						return;
					Compress();
					adapter.notifyDataSetChanged();
				}
			}
		}

		if (requestCode == 121) {
			if (sourBitmap != null) {
				Bitmap waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_cjl);
				Bitmap watermarkBitmap = WaterImageUtil.createWaterMaskLeftTop(context,sourBitmap, waterBitmap,0,0);
				Bitmap textBitmap = WaterImageUtil.drawTextToCenter(this, watermarkBitmap, ECApplication.getInstance().getCurrentIMUser().getName() + " " + DateUtil.getCurrDateSecondString() + "#低值易耗品",25, Color.WHITE);
				File tempFile = new File(sdCard, "sing.jpg");
				sendFiles.clear();
				sendFiles.add(tempFile);
				IMUtils.saveBitmapToLocal(tempFile, textBitmap);
				signIV.setImageBitmap(textBitmap);
			}
		}
	}


	// 压缩图片 并存储
	public void Compress() {
		nowBmp.clear();
		sendFiles.clear();
		try {
			for (int i = 0; i < nowPhotos.size(); i++) {
				if (nowPhotos.get(i) != null) {
					File f = new File(nowPhotos.get(i).getOriginalPath());
					File fs = new File(PictureUtil.getAlbumDir(), "small_" + f.getName());
					Bitmap bm = PictureUtil.getSmallBitmap(nowPhotos.get(i).getOriginalPath());
					if (bm != null) {
						FileOutputStream fos = new FileOutputStream(fs);
						bm.compress(Bitmap.CompressFormat.JPEG, 90, fos);
						nowBmp.add(bm);
						sendFiles.add(fs);
					}
				} else {
					nowBmp.add(null);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
			outDateTV.setText(date);
		} else {
			buildDataTV.setText(date);
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
		return R.layout.activity_sm_apply_grant;
	}

}
