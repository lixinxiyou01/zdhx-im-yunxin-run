package zhwx.ui.dcapp.repairs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.HashMap;
import java.util.List;

import volley.Response;
import volley.VolleyError;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.Log;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.capture.core.CaptureActivity;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.refreshlayout.PullableListView;
import zhwx.ui.dcapp.assets.SendSearchActivity;
import zhwx.ui.dcapp.assets.model.AllAssets;


/**   
 * @Title: GrantActivity.java 
 * @Package zhwx.ui.dcapp.assets
 * @Description: 资产发放购物车
 * @author Li.xin @ zdhx
 * @date 2016年8月22日 下午12:42:43 
 */
public class DeviceLevelOneActivity extends BaseActivity implements OnClickListener{
	
	private Activity context;
		
	private ECProgressDialog mPostingdialog;
	
	private RequestWithCacheGet cache;
	
	private HashMap<String, ParameterValue> map;
	
	private PullableListView assetsLV;
	
	private TextView emptyTV;
	
	private int pageNo = 1;
	
	private String circleJson;
	
	private  List<AllAssets> allDataList;
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_as_assetlist;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		cache = new RequestWithCacheGet(context);
		getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, R.drawable.icon_sao,"设备分类", this);
		assetsLV = (PullableListView) findViewById(R.id.assetsLV);
		assetsLV.enableAutoLoad(false);
		assetsLV.setLoadmoreVisible(false);
		emptyTV = (TextView) findViewById(R.id.emptyTV);
		assetsLV.setEmptyView(emptyTV);
		getData();
	}

	/**
	 * 
	 */
	private void getData() {
		mPostingdialog = new ECProgressDialog(context, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		try {
			circleJson = cache.getRseponse(UrlUtil.getDeviceKindLevelOneList(ECApplication.getInstance().getV3Address(), map), new RequestWithCacheGet.RequestListener() {
				
				@Override
				public void onResponse(String response) {
					if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
						Log.i("新数据:"+response);
						refreshData(response);
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			mPostingdialog.dismiss();
		}
		
		if ((circleJson != null && !circleJson.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:"+circleJson);
			refreshData(circleJson);
		}
	}

	/**
	 * @param circleJson2
	 */
	private void refreshData(String circleJson2) {
		if (circleJson2.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		allDataList = new Gson().fromJson(circleJson2, new TypeToken<List<AllAssets>>() {}.getType());
		mPostingdialog.dismiss();
		if(allDataList != null) {
			assetsLV.setAdapter(new OrderListAdapter());
		}
	}
	
	
	public class OrderListAdapter extends BaseAdapter {
		
		public OrderListAdapter(Context context, List<AllAssets> list,
				int listFlag) {
			super();
		}

		public OrderListAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return allDataList.size();
		}

		@Override
		public AllAssets getItem(int position) {
			return allDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_as_myassets, null);
				holder = new ViewHolder();
				holder.asNameTV = (TextView) convertView.findViewById(R.id.asNameTV);
				holder.asTypeTV = (TextView) convertView.findViewById(R.id.asTypeTV);
				holder.asKindTV = (TextView) convertView.findViewById(R.id.asKindTV);
				holder.codeTV = (TextView) convertView.findViewById(R.id.codeTV);
				holder.getDateTV = (TextView) convertView.findViewById(R.id.getDateTV);
				holder.checkStatusViewTV = (TextView) convertView.findViewById(R.id.checkStatusViewTV);
				holder.buttonContentLay = (LinearLayout) convertView.findViewById(R.id.buttonContentLay);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.asNameTV.setText(getItem(position).getName());
			holder.asTypeTV.setText(getItem(position).getPatternName());
			holder.asKindTV.setText(getItem(position).getAssetKindName());
			holder.codeTV.setText(getItem(position).getCode());
			holder.getDateTV.setText(getItem(position).getRegistrationDate());
			holder.checkStatusViewTV.setVisibility(View.INVISIBLE);
			
			addListener(holder, position, convertView);
			return convertView;
		}

		/**
		 * holerView 添加监听器
		 * @param holder
		 * @param position
		 * @param view
		 */
		private void addListener(final ViewHolder holder, final int position,final View view) {
		}
		private class ViewHolder {
			private TextView asNameTV,asTypeTV,asKindTV,codeTV,getDateTV,checkStatusViewTV;
			private LinearLayout buttonContentLay;
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			ECAlertDialog buildAlert = ECAlertDialog.buildColorButtonAlert(context, "查找资产", "#3989fc", "", "条件查找", "", "扫码查找", "#3989fc", new DialogInterface.OnClickListener() {
				 @Override
	             public void onClick(DialogInterface dialog, int which) {
					 startActivity(new Intent(context, SendSearchActivity.class));
	             }
			}, new DialogInterface.OnClickListener() {
				 @Override
	             public void onClick(DialogInterface dialog, int which) {
					 startActivity(new Intent(context, CaptureActivity.class));
	             }
	         });
	         buildAlert.setMessage("请选择查找方式");
	         buildAlert.show();  
			break;
		}
	}
}
