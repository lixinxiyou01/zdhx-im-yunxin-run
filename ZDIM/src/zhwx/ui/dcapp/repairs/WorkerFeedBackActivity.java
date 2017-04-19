package zhwx.ui.dcapp.repairs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.photoselector.model.PhotoModel;
import com.photoselector.ui.PhotoPreviewActivity;
import com.photoselector.ui.PhotoSelectorActivity;
import com.photoselector.util.CommonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.base.CCPAppManager;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.FileUpLoadCallBack;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.compressImg.PictureUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.imagegallery.ViewImageInfo;
import zhwx.ui.dcapp.repairs.adapter.RmImageGirdAdapter;
import zhwx.ui.dcapp.repairs.model.ConsumItem;
import zhwx.ui.dcapp.repairs.model.ConsumResult;
import zhwx.ui.dcapp.repairs.model.RepairDetail;


/**   
 * @author Li.xin @ zdhx
 */
public class WorkerFeedBackActivity extends BaseActivity implements OnClickListener{
	
	private Activity context;
		
	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;

	private HashMap<String, ParameterValue> loginMap;
	
	private String circleJson;
	
	//R
	private TextView requestUserTV,requestTimeTV,requestDeviceTV,faultKindTV,faultDescriptionTV,
			repairHistoryTV,faultPlaceTV,requestPhoneTV,repairerTV,repairTimeTV,repairStatusTV
			,faultLeixingTV,elseTV;

	private EditText faultReasonTV,repairCostTV;

	private RadioGroup faultKindRG;

	private RadioGroup repairStatusRG;

	private GridView requestImgGV;

	private RepairDetail repairDetail;

	private String repairId;

	private Handler handler = new Handler();

	private String faultKind = "0";

	private String repairStatus = "2";

	private List<PhotoModel> nowPhotos = new ArrayList<PhotoModel>();

	private List<Bitmap> nowBmp = new ArrayList<Bitmap>();

	public static final int MAX_IMG_COUNT = 6; // 选择图数量上限

	private List<File> sendFiles = new ArrayList<File>();

	private GridView repairImgGV;

	private ImageGVAdapter imgAdapter;

	private String orderFlag;

	private LinearLayout elseLay;

	private LinearLayout elseContener;

	private RequestWithCacheGet cache;

	private String totalPrice = "";


	@Override
	protected int getLayoutId() {
		return R.layout.activity_worker_feedback;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		cache = new RequestWithCacheGet(context);
		repairId = getIntent().getStringExtra("repairId");
		getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"维修反馈", this);
		initView();
		getDetail();
		refreshDataCost(); //配件明细
	}

	private void initView() {
		requestUserTV = (TextView) findViewById(R.id.requestUserTV);
		requestTimeTV = (TextView) findViewById(R.id.requestTimeTV);
		requestDeviceTV = (TextView) findViewById(R.id.requestDeviceTV);
		faultKindTV = (TextView) findViewById(R.id.faultKindTV);
		faultDescriptionTV = (TextView) findViewById(R.id.faultDescriptionTV);
		repairHistoryTV = (TextView) findViewById(R.id.repairHistoryTV);
		requestPhoneTV = (TextView) findViewById(R.id.requestPhoneTV);
		repairerTV = (TextView) findViewById(R.id.repairerTV);
		repairerTV.setText(ECApplication.getInstance().getCurrentIMUser().getName());
		repairTimeTV = (TextView) findViewById(R.id.repairTimeTV);
		repairStatusTV = (TextView) findViewById(R.id.repairStatusTV);
		faultPlaceTV = (TextView) findViewById(R.id.faultPlaceTV);
		faultLeixingTV = (TextView) findViewById(R.id.faultLeixingTV);
		faultReasonTV = (EditText) findViewById(R.id.faultReasonTV);
		repairCostTV = (EditText) findViewById(R.id.repairCostTV);
		elseTV = (TextView) findViewById(R.id.elseTV);
		requestImgGV = (GridView) findViewById(R.id.requestImgGV);
		repairImgGV = (GridView) findViewById(R.id.repairImgGV);
		elseLay = (LinearLayout) findViewById(R.id.elseLay);
		elseContener = (LinearLayout) findViewById(R.id.elseContener);
		nowPhotos.add(null);
		imgAdapter = new ImageGVAdapter();
		repairImgGV.setAdapter(imgAdapter);
		Tools.setGridViewHeightBasedOnChildren4(repairImgGV);

		faultKindRG = (RadioGroup) findViewById(R.id.faultKindRG);
		faultKindRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				switch (checkedId) {
					case R.id.normalFaultRb:
						faultKind = "0"; //自然损坏
						break;
					case R.id.personFaultRb:
						faultKind = "1"; //人为损坏
						break;
				}
			}
		});
		repairStatusRG = (RadioGroup) findViewById(R.id.repairStatusRG);
		repairStatusRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				switch (checkedId) {
					case R.id.repairedRb:
						repairStatus = "2"; //已修好
						break;
					case R.id.cantRepairRb:
						repairStatus = "3"; //不能维修
						break;
				}
			}
		});
	}


	private void getDetail(){
		mPostingdialog = new ECProgressDialog(context, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("repairId", new ParameterValue(repairId));
//		try {
//			goodJson = cache.getRseponse(UrlUtil.getGoods(ECApplication.getInstance().getV3Address(), map), new RequestWithCacheGet.RequestListener() {
//
//				@Override
//				public void onResponse(String response) {
//					if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
//						Log.i("新数据:"+response);
//						refreshDataCost(response);
//					}
//				}
//			}, new Response.ErrorListener() {
//				@Override
//				public void onErrorResponse(VolleyError error) {
//
//				}
//			});
//		} catch (Exception e) {
//			e.printStackTrace();
//			mPostingdialog.dismiss();
//		}
//
//		if ((goodJson != null && !goodJson.equals(RequestWithCacheGet.NO_DATA))) {
//			Log.i("缓存数据:"+goodJson);
//			refreshDataCost(goodJson);
//		}

		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {

					circleJson = UrlUtil.getRepairDetail(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(circleJson);
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

//	private void refreshDataCost1(String json) {
//		if(json.contains("<html>")){
//			ToastUtil.showMessage("数据异常");
//			return;
//		}
//		idAndNames = new Gson().fromJson(json, new TypeToken<List<IdAndName>>() {}.getType());
//		elseLay.removeAllViews();
//		final RelativeLayout addItem = (RelativeLayout) View.inflate(context, R.layout.list_item_ci_add, null);
//		elseLay.addView(addItem);
//		Button addBT = (Button) addItem.findViewById(R.id.addBT);
//		addBT.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				elseLay.removeView(addItem);
//				final RelativeLayout custItem = (RelativeLayout) View.inflate(context, R.layout.list_item_rm_cost, null);
//				Button delBT = (Button) custItem.findViewById(R.id.delBT);
//				delBT.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						elseLay.removeView(custItem);
//					}
//				});
//				final Spinner custKindSP = (Spinner) custItem.findViewById(R.id.custKindSP);
//				custKindSP.setAdapter(new IdAndNameSpinnerAdapter(context, idAndNames));
//				custKindSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//					@Override
//					public void onItemSelected(AdapterView<?> parent,View view, int position, long id) {
//						custKindSP.setTag(idAndNames.get(position).getId());
//					}
//
//					@Override
//					public void onNothingSelected(AdapterView<?> parent) {
//
//					}
//				});
//				elseLay.addView(custItem);
//				elseLay.addView(addItem);
//			}
//		});
//	}

	private void refreshDataCost() {
		elseContener.removeAllViews();
		final RelativeLayout addItem = (RelativeLayout) View.inflate(context, R.layout.list_item_rm_add, null);
		final RelativeLayout totleItem = (RelativeLayout) View.inflate(context, R.layout.list_item_rm_cost_totle, null);
		elseContener.addView(addItem);
		LinearLayout addBT = (LinearLayout) addItem.findViewById(R.id.addBT);
		addBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				elseContener.removeView(addItem);
				elseContener.removeView(totleItem);
				final RelativeLayout custItem = (RelativeLayout) View.inflate(context, R.layout.list_item_rm_cost_big, null);
				Button delBT = (Button) custItem.findViewById(R.id.delBT);
				EditText pjPriceTV = (EditText) custItem.findViewById(R.id.pjPriceTV);
				EditText pjCountTV = (EditText) custItem.findViewById(R.id.pjCountTV);
				TextView totlePriceTV = (TextView) custItem.findViewById(R.id.totlePriceTV);
				elseContener.addView(custItem);
				elseContener.addView(addItem);
				elseContener.addView(totleItem);
				for (int i = 0; i < elseContener.getChildCount()-2; i++) {
					TextView titleTV = (TextView) elseContener.getChildAt(i).findViewById(R.id.titleTV);
					titleTV.setText("配件明细" + "(" + (i+1) + ")");
				}
				addTextChange(pjPriceTV,pjCountTV,totlePriceTV,totleItem,elseContener,delBT,custItem);
			}
		});
	}

	private void addTextChange(final EditText pjPriceTV, final EditText pjCountTV, final TextView totlePriceTV, final RelativeLayout totleItem, final LinearLayout elseContener, Button delBT, final RelativeLayout custItem) {
		pjPriceTV.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable edt) {
				String temp = edt.toString();
				int posDot = temp.indexOf(".");
				if (posDot <= 0) return;
				if (temp.length() - posDot - 1 > 2)
				{
					edt.delete(posDot + 3, posDot + 4);
				}
				if(StringUtil.isNotBlank(pjPriceTV.getEditableText().toString())&&StringUtil.isNotBlank(pjCountTV.getEditableText().toString())) {
					totlePriceTV.setText(String.format("%5.2f",(Float.parseFloat(pjPriceTV.getEditableText().toString())*Float.parseFloat(pjCountTV.getEditableText().toString()))));
					float totlePrice = 0;
					for (int i = 0; i < elseContener.getChildCount()-2; i++) {
						RelativeLayout dataItem = (RelativeLayout) elseContener.getChildAt(i);
						TextView totlePriceTV = (TextView) dataItem.findViewById(R.id.totlePriceTV);
						totlePrice += Float.parseFloat(totlePriceTV.getText().toString());
					}
					TextView totlePriceTV = (TextView) totleItem.findViewById(R.id.allCostTV);
					totlePriceTV.setText(String.format("%5.2f",totlePrice) + "元");
					totalPrice = String.format("%5.2f",totlePrice);
				}
			}
		});

		pjCountTV.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(StringUtil.isNotBlank(pjPriceTV.getEditableText().toString())&&StringUtil.isNotBlank(pjCountTV.getEditableText().toString())) {
					totlePriceTV.setText(String.format("%5.2f",(Float.parseFloat(pjPriceTV.getEditableText().toString())*Float.parseFloat(pjCountTV.getEditableText().toString()))));
					float totlePrice = 0;
					for (int i = 0; i < elseContener.getChildCount()-2; i++) {
						RelativeLayout dataItem = (RelativeLayout) elseContener.getChildAt(i);
						TextView totlePriceTV = (TextView) dataItem.findViewById(R.id.totlePriceTV);
						totlePrice += Float.parseFloat(totlePriceTV.getText().toString());
					}
					TextView totalPriceTV = (TextView) totleItem.findViewById(R.id.allCostTV);
					totalPriceTV.setText(String.format("%5.2f",totlePrice) + "元");
					totalPrice = String.format("%5.2f",totlePrice);
				}
			}
		});

		delBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				elseContener.removeView(custItem);
				float totlePrice = 0;
				for (int i = 0; i < elseContener.getChildCount()-2; i++) {
					RelativeLayout dataItem = (RelativeLayout) elseContener.getChildAt(i);
					TextView titleTV = (TextView) dataItem.findViewById(R.id.titleTV);
					titleTV.setText("配件明细" + "(" + (i+1) + ")");
					TextView totlePriceTV = (TextView) dataItem.findViewById(R.id.totlePriceTV);
					totlePrice += Float.parseFloat(totlePriceTV.getText().toString());
				}
				TextView totalPriceTV = (TextView) totleItem.findViewById(R.id.allCostTV);
				totalPriceTV.setText(String.format("%5.2f",totlePrice) + "元");
				totalPrice = String.format("%5.2f",totlePrice);
			}
		});
	}

	private void refreshData(String json) {
		if(json.contains("<html>")){
			ToastUtil.showMessage("数据异常");
			return;
		}
		System.out.println(json);
		repairDetail = new Gson().fromJson(json,RepairDetail.class);

		//报修图片
		if(repairDetail.getRequestInfo().getImageList().size()!=0) {
			requestImgGV.setAdapter(new RmImageGirdAdapter(context, repairDetail.getRequestInfo().getImageList()));
			Tools.setGridViewHeightBasedOnChildren4(requestImgGV);
			requestImgGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					ArrayList<ViewImageInfo> urls = new ArrayList<ViewImageInfo>();
					ViewImageInfo imageInfo;
					for (int i = 0; i < repairDetail.getRequestInfo().getImageList().size(); i++) {
						imageInfo = new ViewImageInfo("", ECApplication.getInstance().getV3Address()+repairDetail.getRequestInfo().getImageList().get(i));
						urls.add(imageInfo);
					}
					CCPAppManager.startChattingImageViewAction(context,position , urls);
				}
			});
		}

		requestUserTV.setText(repairDetail.getRequestInfo().getRequestUserName());
		requestTimeTV.setText(repairDetail.getRequestInfo().getRequestTime());
		requestDeviceTV.setText(repairDetail.getRequestInfo().getDeviceName());
		faultKindTV.setText(repairDetail.getRequestInfo().getMalfunction());
		faultDescriptionTV.setText(repairDetail.getRequestInfo().getMalfunctionDescribe());
		repairHistoryTV.setText(StringUtil.isNotBlank(repairDetail.getRequestInfo().getRepairHistory())?repairDetail.getRequestInfo().getRepairHistory():"无");
		faultPlaceTV.setText(repairDetail.getRequestInfo().getMalfunctionPlace());
		requestPhoneTV.setText(repairDetail.getRequestInfo().getPhoneNum());

		mPostingdialog.dismiss();
	}

	private boolean checkError(EditText et) {
		if(StringUtil.isBlank(et.getText().toString())) {
			et.setError(Html.fromHtml("<font color=#808183>" + "不能为空" + "</font>"));
			ToastUtil.showMessage("表单有未填项");
			return  false;
		}
		return true;
	}

	//提交
	public void onFeedBack(View v) {

		if(!checkOrderList()) {
			return;
		}
		ConsumResult consumResult = new ConsumResult();
		List<ConsumItem> cimList = new ArrayList<>();
		if (elseContener.getChildCount() > 2) {
			for (int i = 0; i < elseContener.getChildCount()-2; i++) {
				ConsumItem ci = new ConsumItem();
				RelativeLayout dataItem = (RelativeLayout) elseContener.getChildAt(i);
				EditText pjPriceTV = (EditText) dataItem.findViewById(R.id.pjPriceTV);
				EditText pjNameET = (EditText) dataItem.findViewById(R.id.pjNameET);
				EditText pjCountTV = (EditText) dataItem.findViewById(R.id.pjCountTV);
				TextView totlePriceTV = (TextView) dataItem.findViewById(R.id.totlePriceTV);
				if (!checkError(pjPriceTV)) {
					return;
				}
				if (!checkError(pjNameET)) {
					return;
				}
				if (!checkError(pjCountTV)) {
					return;
				}
				ci.setCount(pjCountTV.getText().toString());
				ci.setName(pjNameET.getText().toString());
				ci.setPrice(pjPriceTV.getText().toString());
				ci.setSubtotal(totlePriceTV.getText().toString());
				cimList.add(ci);
			}
			consumResult.setConsumItems(cimList);
			consumResult.setTotalCost(totalPrice);
		}
		System.out.println(new Gson().toJson(consumResult));
		mPostingdialog = new ECProgressDialog(this, "正在提交反馈");
		mPostingdialog.show();
		loginMap = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map = new HashMap<String, ParameterValue>();
		map.put("faultReason",new ParameterValue(faultReasonTV.getEditableText().toString())); //故障原因
		map.put("faultkind",new ParameterValue(faultKind)); //故障类别
		map.put("repairCost",new ParameterValue(repairCostTV.getEditableText().toString())); //花费
		map.put("repairId",new ParameterValue(repairId)); //报修人Id
		map.put("repairStatus",new ParameterValue(repairStatus)); //报修人Id
		map.put("workerId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id())); //报修人Id
		if(consumResult.getConsumItems()!=null&&consumResult.getConsumItems().size()>0) {
			map.put("consumItems",new ParameterValue(new Gson().toJson(consumResult))); //配件明细
		}
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					if(sendFiles.size() == 0) {
						loginMap.putAll(map);
						orderFlag = UrlUtil.saveWokerFeedBack(ECApplication.getInstance().getV3Address(),loginMap);
					} else {
						orderFlag = UrlUtil.saveWokerFeedBack(ECApplication.getInstance().getV3Address(), sendFiles, loginMap, map, new FileUpLoadCallBack() {
							@Override
							public void upLoadProgress(int fileCount, int currentIndex, int currentProgress,int allProgress) {

							}
						});
					}
					handler.postDelayed(new Runnable() {
						public void run() {
							if (orderFlag.contains("ok")) {
								ToastUtil.showMessage("已提交");
								finish();
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

	public boolean checkOrderList() {
		boolean pass = true;

		return pass;
	}

	/**
	 * 已选择图片适配
	 * @author lenovo
	 */
	class ImageGVAdapter extends BaseAdapter {

		public ImageGVAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return nowPhotos.size();
		}

		@Override
		public PhotoModel getItem(int position) {
			return nowPhotos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
							ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = View.inflate(context, R.layout.gv_item_image,
						null);
				holder.imageGV = (ImageView) convertView
						.findViewById(R.id.imageGV);
				holder.delBT = (ImageView) convertView.findViewById(R.id.delBT);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			if (nowPhotos.get(position) == null) {
				holder.imageGV.setImageResource(R.drawable.icon_rm_addpic);
				holder.delBT.setVisibility(View.INVISIBLE);
			} else {
				if (nowPhotos.get(position) != null) {
					holder.imageGV.setImageBitmap(nowBmp.get(position));
					holder.delBT.setVisibility(View.VISIBLE);
				}
			}
			addListener(holder, position);
			return convertView;
		}

		private void addListener(Holder holder, final int position) {
			holder.delBT.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (position == nowPhotos.size() - 1) {

					} else {
						remove(position);
						for (int i = 0; i < nowPhotos.size(); i++) {
							if (nowPhotos.get(i) == null) {
								nowPhotos.remove(i);
							}
						}
						nowPhotos.add(null);
						imgAdapter.notifyDataSetChanged();
					}
				}
			});
			holder.imageGV.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {

					if (position == nowPhotos.size() - 1) {
						if (nowPhotos.get(nowPhotos.size() - 1) == null) {
							Intent intent = new Intent(context,
									PhotoSelectorActivity.class);
							intent.putExtra("canSelectCount", MAX_IMG_COUNT + 1 - nowPhotos.size());
							startActivityForResult(intent, 0);
						} else {
							remove(position);
							for (int i = 0; i < nowPhotos.size(); i++) {
								if (nowPhotos.get(i) == null) {
									nowPhotos.remove(i);
								}
							}
							nowPhotos.add(null);
							imgAdapter.notifyDataSetChanged();
						}
					} else {
						List<PhotoModel> nowPhoto = new ArrayList<PhotoModel>();
						nowPhoto.add(nowPhotos.get(position));
						Bundle bundle = new Bundle();
						bundle.putSerializable("photos",(Serializable) nowPhoto);
						CommonUtils.launchActivity(context,PhotoPreviewActivity.class, bundle);
					}
				}
			});
		}

		class Holder {
			private ImageView imageGV;
			private ImageView delBT;
		}
	}

	public void remove(int position) {
		nowPhotos.remove(position);
		nowBmp.remove(position);
		sendFiles.remove(position);
		Tools.setGridViewHeightBasedOnChildren4(requestImgGV);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == RESULT_OK) {
			if (data != null && data.getExtras() != null) {
				@SuppressWarnings("unchecked")
				List<PhotoModel> photos = (List<PhotoModel>) data.getExtras().getSerializable("photos");
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
				imgAdapter.notifyDataSetChanged();
				Tools.setGridViewHeightBasedOnChildren4(repairImgGV);
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
					File fs = new File(PictureUtil.getAlbumDir(), "small_"+ f.getName());
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		}
	}
}
