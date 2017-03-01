package zhwx.ui.dcapp.assets;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.util.DensityUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.view.capture.core.CaptureActivity;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.assets.model.CheckListItem;


/**   
 * @Title: GrantActivity.java 
 * @Package zhwx.ui.dcapp.assets
 * @Description: 资产发放购物车
 * @author Li.xin @ zdhx
 * @date 2016年8月22日 下午12:42:43 
 */
public class GrantBoxActivity extends BaseActivity implements OnClickListener{
	
	private Activity context;
	
	private static ListView grantLV;
	
	private Button addBT,grantBT;

	private TextView emptyTV;
	
	private CheckListItem model;
	
	public static List<AllAssets> allDataList = new ArrayList<AllAssets>();
	@Override
	protected int getLayoutId() {
		return R.layout.activity_as_grantbox;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		model = (CheckListItem) getIntent().getSerializableExtra("model");
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"待发放资产", this);
		grantLV = (ListView) findViewById(R.id.grantLV);
		emptyTV = (TextView) findViewById(R.id.emptyTV);
		grantLV.setEmptyView(emptyTV);
		addBT = (Button) findViewById(R.id.addBT);
		addBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				Intent intent = new Intent(context, AssetsListActivity.class); 
//				startActivityForResult(intent, 111);
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
			}
		});
		grantBT = (Button) findViewById(R.id.grantBT);
		grantBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(allDataList.size()!=0) {
					Intent intent = new Intent(context, GrantActivity.class);
					intent.putExtra("model", model);
					startActivityForResult(intent,120);
				} else {
					ToastUtil.showMessage("未添加待发放资产");
				}
			}
		});
		grantLV.setAdapter(new OrderListAdapter());
	}
	
	public static void addAsset(AllAssets data) {
		boolean ishas = false;
		for (int i = 0; i < allDataList.size(); i++) {
			if (allDataList.get(i).getId().endsWith(data.getId())) {
				ishas = true;
			}
		}
		if (ishas) {
			ToastUtil.showMessage("待发放列表中已存在此资产");
		} else {
			ToastUtil.showMessage("已添加至待发放列表");
			allDataList.add(data);
		}
	}
	
	public static boolean isHas(AllAssets data) {
		boolean ishas = false;
		for (int i = 0; i < allDataList.size(); i++) {
			if (allDataList.get(i).getId().endsWith(data.getId())) {
				ishas = true;
			}
		}
		return ishas;
	}
	
	public static void removeAsset(AllAssets data) {
		for (int i = 0; i < allDataList.size(); i++) {
			if (allDataList.get(i).getId().endsWith(data.getId())) {
				allDataList.remove(i);
				OrderListAdapter adapter = (OrderListAdapter) grantLV.getAdapter();
				adapter.notifyDataSetChanged();
//				ToastUtil.showMessage("已从待发放资产中移除");
				break;
			}
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
			
			//动态添加操作按钮
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
	
	public List<Button> getOrderButtonList(final int position){
		List<Button> btnList = new ArrayList<Button>();
		Button ckBT = getOrderButton("查看");
		ckBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, AssetDetailActivity.class);
				intent.putExtra("assetsCode", allDataList.get(position).getCode());
				startActivity(intent);
			}
		});
		Button removeBT = getOrderButton("移除");
		removeBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				GrantBoxActivity.removeAsset(allDataList.get(position));
			}
		});
		btnList.add(removeBT);
		btnList.add(ckBT);
		return btnList;
	}
	
	public Button getOrderButton (String text) {
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
	protected void onResume() {
		super.onResume();
		OrderListAdapter adapter = (OrderListAdapter) grantLV.getAdapter();
		if(adapter!=null) {
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 120 ){
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		allDataList.clear();
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
