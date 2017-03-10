package zhwx.ui.dcapp.assets;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.DensityUtil;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.view.capture.core.CaptureActivity;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.refreshlayout.PullableListView;
import zhwx.ui.dcapp.assets.model.AllAssets;

/**
 * @Title: GrantActivity.java
 * @Package zhwx.ui.dcapp.assets
 * @Description: 资产发放购物车
 * @author Li.xin @ zdhx
 * @date 2016年8月22日 下午12:42:43 
 */
public class AssetsSearchListActivity extends BaseActivity implements OnClickListener{
	
	private Activity context;
		
	private RequestWithCacheGet cache;
	
	private HashMap<String, ParameterValue> map;
	
	private PullableListView assetsLV;
	
	private TextView emptyTV;
	
	public static List<AllAssets> searchList;
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_as_assetlist;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		cache = new RequestWithCacheGet(context);
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt,-1,"检索结果", this);
		assetsLV = (PullableListView) findViewById(R.id.assetsLV);
		assetsLV.enableAutoLoad(false);
		assetsLV.setLoadmoreVisible(false);
		emptyTV = (TextView) findViewById(R.id.emptyTV);
		assetsLV.setEmptyView(emptyTV);
		refreshData();
	}

	/**
	 * @param circleJson2
	 */
	private void refreshData() {
		assetsLV.setAdapter(new OrderListAdapter());
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
			return searchList.size();
		}

		@Override
		public AllAssets getItem(int position) {
			return searchList.get(position);
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
			
			//动态添加操作按钮
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
		 * @param holder
		 * @param position
		 * @param view
		 */
		private void addListener(final ViewHolder holder, final int position,
				final View view) {
		}
		private class ViewHolder {
			private TextView asNameTV,asTypeTV,asKindTV,codeTV,getDateTV,checkStatusViewTV;
			private LinearLayout buttonContentLay;
		}
		
	}
	
	public List<TextView> getOrderButtonList(final int position){
		List<TextView> btnList = new ArrayList<TextView>();
		TextView ckBT = getOrderButton("查看");
		ckBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, AssetDetailActivity.class);
				intent.putExtra("assetsCode", searchList.get(position).getCode());
				intent.putExtra("isAddMode", "isAddMode");
				startActivity(intent);
			}
		});
		
		if (GrantBoxActivity.isHas(searchList.get(position))) {
			TextView addBT = getOrderButton("移除");
			addBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					GrantBoxActivity.removeAsset(searchList.get(position));
					OrderListAdapter adapter = (OrderListAdapter) assetsLV.getAdapter();
					adapter.notifyDataSetChanged();
				}
			});
			btnList.add(addBT);
		} else {
			TextView addBT = getOrderButton("添加");
			addBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					GrantBoxActivity.addAsset(searchList.get(position));
					OrderListAdapter adapter = (OrderListAdapter) assetsLV.getAdapter();
					adapter.notifyDataSetChanged();
				}
			});
			btnList.add(addBT);
		}
		btnList.add(ckBT);
		return btnList;
	}
	
	public TextView getOrderButton (String text) {
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		searchList.clear();
	}
}
