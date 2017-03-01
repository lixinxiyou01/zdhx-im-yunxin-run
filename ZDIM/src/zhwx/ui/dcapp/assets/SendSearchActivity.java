package zhwx.ui.dcapp.assets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.treelistview.bean.FileBean;
import zhwx.ui.dcapp.assets.adapter.IdAndNameSpinnerAdapter;
import zhwx.ui.dcapp.assets.adapter.IdAndNameSpinnerAdapterAdd;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.assets.model.AssetPageModel;
import zhwx.ui.dcapp.assets.model.IdAndName;

/**   
 * @Title: SendSearchActivity.java 
 * @Package zhwx.ui.dcapp.assets
 * @author Li.xin @ zdhx
 * @date 2016年8月19日 下午2:44:44 
 */
public class SendSearchActivity extends BaseActivity implements OnDateSetListener,OnClickListener{
	
	private Activity context;
	
	private EditText nameET,codeET;
	
	private TextView startDateTV,endDateTV;
	
	private Spinner statusSP,storePlaceSP,kindSP,brandsSP,typeSP,schoolSP;
	
	private String DATEPICKER_TAG_START = "start";
	
	private String DATEPICKER_TAG_END = "end";
	
	private DatePickerDialog datePickerDialog;
	
	private Handler handler = new Handler();
	
	private RadioGroup checkRG;
	
	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;
	
	private HashMap<String, ParameterValue> detailMap;
	
	
	private String infoJson;
	
	private String detailInfoJson;
	
	private LinearLayout moreLay;
	
	private CheckBox moreCB;
	
	
	public static List<FileBean> datas;
	
	/**
	 *  按条件查找资产
	 *  json{"name":名称,"code":编码,"status":状态编码,"brandId":品牌id,"patternId":型号id,"purchaseDateStart":购置日期开始,
	 *  "purchaseDateEnd":购置日期截止,"locationId":存放地点,"kitFlag":是否成套,"pageNum":分页页码} &operationCode：AssetManage
	 */
	private String assetKindId = "";
//	private String name;
//	private String code;
	private String status = "";
	private String brandId = "";
	private String patternId = "";
//	private String purchaseDateStart;
//	private String purchaseDateEnd;
	private String locationId = "";
	private String kitFlag = "";
	private int kindPosition;
	private TextView kindTV;
	
	private AssetPageModel applyInfos;
	
	private AssetPageModel applyDetailInfos;
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_as_sendsearch;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"按条件查找", this);
		setImmerseLayout(getTopBarView(),1);
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		datePickerDialog = DatePickerDialog.newInstance(
				SendSearchActivity.this, calendar.get(java.util.Calendar.YEAR),
				calendar.get(java.util.Calendar.MONTH),
				calendar.get(java.util.Calendar.DAY_OF_MONTH));
		initView();
		getInfo();
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
					infoJson = UrlUtil.getAssetPageJson(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							System.out.println(infoJson);
							refreshInfo(infoJson);
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
	
	private void refreshInfo(String infoJson2) {
		if (infoJson.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		applyInfos = new Gson().fromJson(infoJson, AssetPageModel.class);
		if (applyInfos != null) {
			
			schoolSP.setAdapter(new IdAndNameSpinnerAdapter(context, applyInfos.getLocations()));
			schoolSP.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,final int schoolPosition, long id) {
					for (int i = 0; i < applyInfos.getLocations().size(); i++) {
						if (i == schoolPosition) {
							applyInfos.getLocations().get(i).setSelected(true);
						} else {
							applyInfos.getLocations().get(i).setSelected(false);
						}
					}
					BaseAdapter adapter =  (BaseAdapter) parent.getAdapter();
					adapter.notifyDataSetChanged();
					
					storePlaceSP.setAdapter(new IdAndNameSpinnerAdapter(context, applyInfos.getLocations().get(schoolPosition).getClassrooms()));
					storePlaceSP.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
							for (int i = 0; i < applyInfos.getLocations().get(schoolPosition).getClassrooms().size(); i++) {
								if (i == position) {
									applyInfos.getLocations().get(schoolPosition).getClassrooms().get(i).setSelected(true);
									locationId = applyInfos.getLocations().get(schoolPosition).getClassrooms().get(i).getId();
									
									getDetail(locationId);
									
								} else {
									applyInfos.getLocations().get(schoolPosition).getClassrooms().get(i).setSelected(false);
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
	
	
	//获取库存内容
	private void getDetail(String locationId) {
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		detailMap = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		detailMap.put("locationId", new ParameterValue(locationId));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					detailInfoJson = UrlUtil.getInStockNumJson(ECApplication.getInstance().getV3Address(), detailMap);
					handler.postDelayed(new Runnable() {
						public void run() {
							System.out.println(detailInfoJson);
							refreshDetail(detailInfoJson);
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
	
	private void refreshDetail(String detail) {
		if (detail.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		applyDetailInfos = new Gson().fromJson(detail, AssetPageModel.class);
		if (applyDetailInfos != null) {
			datas = new ArrayList<FileBean>();
			List<AssetPageModel.AssetKindsBean> tree = applyDetailInfos.getAssetKinds();
			for (int i = 0; i < tree.size(); i++) {
				datas.add(new FileBean(Integer.parseInt(tree.get(i).getId().length() < 5 ? "0" : tree.get(i).getId().substring(23, 31)), Integer
						.parseInt(tree.get(i).getKindParentId().length() < 5 ? "0" : tree.get(i).getKindParentId().substring(23, 31)),
						tree.get(i).getName() + " [" + (tree.get(i).getQuantity() == 0?tree.get(i).getCount():tree.get(i).getQuantity()) +"]", tree.get(i).getId(), ""));
			}
		}
		
		//TODO 重置型号规格
		assetKindId = "";
		brandId = "";
		patternId = "";
		kindTV.setText("");
		brandsSP.setAdapter(new IdAndNameSpinnerAdapterAdd(context, new ArrayList<IdAndName>()));
		typeSP.setAdapter(new IdAndNameSpinnerAdapterAdd(context, new ArrayList<IdAndName>()));
		mPostingdialog.dismiss();
	}
	/**
	 * 
	 */
	private void getAssData() {
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		Map<String, String> jsonMap = new HashMap<String, String>();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("operationCode", new ParameterValue("CheckManage"));
		jsonMap.put("name", nameET.getText().toString());
		jsonMap.put("code", codeET.getText().toString());
		jsonMap.put("kitFlag", kitFlag);
//		if (moreCB.isSelected()) {
//			jsonMap.put("status", status);
			jsonMap.put("assetKindId", assetKindId);
			jsonMap.put("brandId", brandId);
			jsonMap.put("patternId", patternId);
			jsonMap.put("purchaseDateStart", startDateTV.getText().toString());
			jsonMap.put("purchaseDateEnd", endDateTV.getText().toString());
			jsonMap.put("locationId", locationId);
//		}
		map.put("resultJson", new ParameterValue(new Gson().toJson(jsonMap)));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					infoJson = UrlUtil.getAssetListJson(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							System.out.println(infoJson);
							refreshAssetList(infoJson);
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
	
	private void refreshAssetList(String infoJson2) {
		// TODO Auto-generated method stub
				
		if (infoJson2.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		AssetsSearchListActivity.searchList = new Gson().fromJson(infoJson2, new TypeToken<List<AllAssets>>() {}.getType());
		
		if(AssetsSearchListActivity.searchList.size()!=0) {
			startActivity(new Intent(context, AssetsSearchListActivity.class));
		} else {
			ToastUtil.showMessage("无结果");
		}
		mPostingdialog.dismiss();
	}
	
	
	/**
	 * 
	 */
	private void initView() {
		
		kindTV = (TextView) findViewById(R.id.kindTV);
		moreLay = (LinearLayout) findViewById(R.id.moreLay);
		moreCB = (CheckBox) findViewById(R.id.moreCB);
		moreCB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (moreCB.isSelected()) {
					moreCB.setSelected(false);
					moreLay.setVisibility(View.GONE);
				} else {
					moreCB.setSelected(true);
					moreLay.setVisibility(View.VISIBLE);
				}
			}
		});
		nameET = (EditText) findViewById(R.id.nameET);
		codeET = (EditText) findViewById(R.id.codeET);
		startDateTV = (TextView) findViewById(R.id.startDateTV);
		startDateTV.setOnClickListener(this);
		endDateTV = (TextView) findViewById(R.id.endDateTV);
		endDateTV.setOnClickListener(this);
		statusSP = (Spinner) findViewById(R.id.statusSP);
		statusSP.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		schoolSP = (Spinner) findViewById(R.id.schoolSP);
		storePlaceSP = (Spinner) findViewById(R.id.storePlaceSP);
		kindSP = (Spinner) findViewById(R.id.kindSP);
		brandsSP = (Spinner) findViewById(R.id.brandsSP);
		typeSP = (Spinner) findViewById(R.id.typeSP);
		
		checkRG = (RadioGroup) findViewById(R.id.checkRG);
		checkRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int radioId) {
				if (radioId == R.id.packageRB) {
					kitFlag = "1";
				} else if (radioId == R.id.noPackageRB){
					kitFlag = "0";
				} else {
					kitFlag = "";
				}
			}
		});
		
	}

	public void onSearch(View v) {
		getAssData();
	}
	
	public void onSelectKind(View v) {
		startActivityForResult(new Intent(context, AssetKindSelectActivity.class), 101);
	}
	
	@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear,int dayOfMonth) {
		String date = year + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
		if (DATEPICKER_TAG_START.equals(dialog.getTag())) {
			startDateTV.setText(date);
		} else if(DATEPICKER_TAG_END.equals(dialog.getTag())) {
			endDateTV.setText(date);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 101) {
			if (data!=null) {
				assetKindId = data.getStringExtra("userId");
				kindTV.setText(data.getStringExtra("userName"));
				for (int i = 0; i < applyDetailInfos.getAssetKinds().size(); i++) {
					if (assetKindId.equals(applyDetailInfos.getAssetKinds().get(i).getId())) {
						kindPosition = i;
					}
				}
				//品牌
				brandsSP.setAdapter(new IdAndNameSpinnerAdapterAdd(context, applyDetailInfos.getAssetKinds().get(kindPosition).getAssetBrands()));
				brandsSP.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent, View view,final int brandsPosition, long id) {
						for (int i = 0; i < applyDetailInfos.getAssetKinds().get(kindPosition).getAssetBrands().size(); i++) {
							if (i == brandsPosition) {
								applyDetailInfos.getAssetKinds().get(kindPosition).getAssetBrands().get(i).setSelected(true);
								brandId = applyDetailInfos.getAssetKinds().get(kindPosition).getAssetBrands().get(i).getId();
							} else {
								applyDetailInfos.getAssetKinds().get(kindPosition).getAssetBrands().get(i).setSelected(false);
							}
						}
						BaseAdapter adapter =  (BaseAdapter) parent.getAdapter();
						adapter.notifyDataSetChanged();
						//型号
						typeSP.setAdapter(new IdAndNameSpinnerAdapterAdd(context, applyDetailInfos.getAssetKinds().get(kindPosition).getAssetBrands().get(brandsPosition).getAssetPatterns()));
						typeSP.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
								for (int i = 0; i < applyDetailInfos.getAssetKinds().get(kindPosition).getAssetBrands().get(brandsPosition).getAssetPatterns().size(); i++) {
									if (i == position) {
										applyDetailInfos.getAssetKinds().get(kindPosition).getAssetBrands().get(brandsPosition).getAssetPatterns().get(i).setSelected(true);
										patternId = applyDetailInfos.getAssetKinds().get(kindPosition).getAssetBrands().get(brandsPosition).getAssetPatterns().get(i).getId();
									} else {
										applyDetailInfos.getAssetKinds().get(kindPosition).getAssetBrands().get(brandsPosition).getAssetPatterns().get(i).setSelected(false);
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
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.startDateTV:
			if (!datePickerDialog.isAdded()) {
				datePickerDialog.show(getFragmentManager(),DATEPICKER_TAG_START);
			}
			break;
		case R.id.endDateTV:
			if (!datePickerDialog.isAdded()) {
				datePickerDialog.show(getFragmentManager(),DATEPICKER_TAG_END);
			}
			break;
		}
	}
}
