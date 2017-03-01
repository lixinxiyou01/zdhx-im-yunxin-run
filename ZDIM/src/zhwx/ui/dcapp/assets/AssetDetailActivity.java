package zhwx.ui.dcapp.assets;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import zhwx.common.base.BaseActivity;
import zhwx.common.base.CCPAppManager;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.imagegallery.ViewImageInfo;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.assets.model.AssetDetail;

/**   
 * @Title: AssetDetailActivity.java 
 * @Package zhwx.ui.dcapp.assets
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年8月18日 下午6:11:49 
 */
public class AssetDetailActivity extends BaseActivity implements OnClickListener{
	
	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;
	
	private Activity context;
	
	private Handler handler = new Handler();
	
	private String json;
	
	private String assetsCode;
	
	private Gson gson = new Gson();
	
	private AssetDetail detail;
	
	private LinearLayout addLay;
	
	private TextView saverTV; 	 //保管人
	private TextView userNameTV; //使用人
	private TextView asKindTV;	 //资产类型
	private TextView asNameTV;	 //资产名称
	private TextView typeTV; 	 //型号规格
	private TextView asCodeTV;	 //资产编码
	private TextView statusTV;	 //状态
	private TextView storeNumTV;	 //库存编号
	private TextView departmentTV;	 //所属部门
	private TextView factoryTV;	 //厂商
	private TextView priceTV;	 //价格
	private TextView warrantyPeriodTV;	 //保修期限
	private TextView registrationDateTV;	 //保修期限
	private TextView buyDateTV;	 //采购日期
	private ImageView assetImgIV;
	private ImageLoader loader;
	
	private String isAddMode;
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_as_detail;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"资产卡片", this);
		assetsCode = getIntent().getStringExtra("assetsCode");
		isAddMode = getIntent().getStringExtra("isAddMode");
		loader = new ImageLoader(context);
		initView();
		getDetail();
		
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		saverTV = (TextView) findViewById(R.id.saverTV);
		userNameTV = (TextView) findViewById(R.id.userNameTV);
		asKindTV = (TextView) findViewById(R.id.asKindTV);
		asNameTV = (TextView) findViewById(R.id.asNameTV);
		typeTV = (TextView) findViewById(R.id.typeTV);
		asCodeTV = (TextView) findViewById(R.id.asCodeTV);
		statusTV = (TextView) findViewById(R.id.statusTV);
		storeNumTV = (TextView) findViewById(R.id.storeNumTV);
		departmentTV = (TextView) findViewById(R.id.departmentTV);
		buyDateTV = (TextView) findViewById(R.id.buyDateTV);
		factoryTV = (TextView) findViewById(R.id.factoryTV);
		priceTV = (TextView) findViewById(R.id.priceTV);
		warrantyPeriodTV = (TextView) findViewById(R.id.warrantyPeriodTV);
		registrationDateTV = (TextView) findViewById(R.id.registrationDateTV);
		assetImgIV = (ImageView) findViewById(R.id.assetImgIV);
		addLay = (LinearLayout) findViewById(R.id.addLay);
		if (StringUtil.isNotBlank(isAddMode)) {
			addLay.setVisibility(View.VISIBLE);
		} else {
			addLay.setVisibility(View.GONE);
		}
	}

	private void getDetail(){
		mPostingdialog = new ECProgressDialog(context, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("code", new ParameterValue(assetsCode));
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.getAssetInfoJson(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(json);
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("资产未找到");
					finish();
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
		if (StringUtil.isNotBlank(json)&&!json.contains("<html>")) {
			detail = gson.fromJson(json, AssetDetail.class);
//			userNameTV.setText(detail.get);
			saverTV.setText(detail.getCustodian());
			asKindTV.setText(detail.getAssetKindName());
			asNameTV.setText(detail.getName());
			typeTV.setText(detail.getPatternName());
			asCodeTV.setText(detail.getCode());
			departmentTV.setText(detail.getDepartment());
			statusTV.setText(detail.getStatusView());
			storeNumTV.setText(detail.getStockNumber());
			factoryTV.setText(detail.getPurchaseFactory());
			priceTV.setText(detail.getUnitPrice()+"");
			warrantyPeriodTV.setText(detail.getWarrantyPeriod()+"");
			registrationDateTV.setText(detail.getRegistrationDate());
			buyDateTV.setText(detail.getPurchaseDate());
			if (detail.getAttachments()!=null&&detail.getAttachments().size()!=0) {
				loader.DisplayImage(ECApplication.getInstance().getV3Address() + detail.getAttachments().get(0).getUrl(), assetImgIV, false);
				assetImgIV.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ArrayList<ViewImageInfo> urls = new ArrayList<ViewImageInfo>();
						ViewImageInfo imageInfo = new ViewImageInfo("", ECApplication.getInstance().getV3Address() + detail.getAttachments().get(0).getUrl()); 
						urls.add(imageInfo);
						CCPAppManager.startChattingImageViewAction(context,0, urls);
					}
				});
			}
		}
		mPostingdialog.dismiss();
	}
	
	
	public void onAdd(View v) {
		AllAssets assets = new AllAssets();
		assets.setName(detail.getName());
		assets.setCode(detail.getCode());
		assets.setAssetKindName(detail.getAssetKindName());
		assets.setId(detail.getId());
		assets.setPatternName(detail.getPatternName());
		GrantBoxActivity.addAsset(assets);
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
