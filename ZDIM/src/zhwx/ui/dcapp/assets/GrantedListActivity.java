package zhwx.ui.dcapp.assets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.assets.model.Granted;

/**   
 * @Title: GrantActivity.java 
 * @Package zhwx.ui.dcapp.assets
 * @Description: 资产发放购物车
 * @author Li.xin @ zdhx
 * @date 2016年8月22日 下午12:42:43 
 */
public class GrantedListActivity extends BaseActivity implements OnClickListener{
	
	private Activity context;
		
	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();
	
	private ListView assetsLV;
	
	private TextView emptyTV;
	
	private String circleJson;
	
	private  List<Granted> allDataList;
	
	private int clickPosition = -1;
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_as_grantlist;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"已发放资产", this);
		assetsLV = (ListView) findViewById(R.id.assetsLV);
		emptyTV = (TextView) findViewById(R.id.emptyTV);
		assetsLV.setEmptyView(emptyTV);
		getData();
	}

	/**
	 * 
	 */
	private void getData() {
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					circleJson = UrlUtil.getGrantListJson(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData1(circleJson);
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
	 * @param circleJson2
	 */
	private void refreshData1(String circleJson2) {
		System.out.println(circleJson2);
		if (circleJson2.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		allDataList = new Gson().fromJson(circleJson2, new TypeToken<List<Granted>>() {}.getType());
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
		public Granted getItem(int position) {
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
				
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_as_granted, null);
				holder = new ViewHolder();
				holder.userNameTV = (TextView) convertView.findViewById(R.id.userNameTV);
				holder.departmentTV = (TextView) convertView.findViewById(R.id.departmentTV);
				holder.granterTV = (TextView) convertView.findViewById(R.id.granterTV);
				holder.grantDateTV = (TextView) convertView.findViewById(R.id.grantDateTV);
				holder.checkStatusViewTV = (TextView) convertView.findViewById(R.id.checkStatusViewTV);
				holder.buttonContentLay = (LinearLayout) convertView.findViewById(R.id.buttonContentLay);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.userNameTV.setText(getItem(position).getUserName());
			holder.departmentTV.setText(getItem(position).getDepartmentName());
			holder.granterTV.setText(getItem(position).getOperatorName());
			holder.checkStatusViewTV.setText("是否签字：" + getItem(position).getSignatureShow());
			holder.grantDateTV.setText(getItem(position).getOperateDate());
			
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
			private TextView userNameTV,departmentTV,granterTV,grantDateTV,checkStatusViewTV;
			private LinearLayout buttonContentLay;
		}
		
	}
	
	public List<TextView> getOrderButtonList(final int position){
		List<TextView> btnList = new ArrayList<TextView>();
		
		if (!allDataList.get(position).isSignatureFlag()) {
			TextView bqBT = getOrderButton("补签");
			bqBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, ReSingActivity.class);
					intent.putExtra("model", allDataList.get(position));
					clickPosition = position;
					startActivityForResult(intent,119);
				}
			});
			btnList.add(bqBT);
		}

		TextView ckBT = getOrderButton("查看");
		ckBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, GrantDetailActivity.class);
				intent.putExtra("id", allDataList.get(position).getId());
				startActivity(intent);
			}
		});
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
		button.setLayoutParams(params);
		return button;
	}
	
	@Override
	protected void onActivityResult(int requstCode, int resultCode, Intent data) {
		super.onActivityResult(requstCode, resultCode, data);
		if (resultCode == 120) {
			if (clickPosition >= 0) {
				allDataList.get(clickPosition).setSignatureFlag(true);
				allDataList.get(clickPosition).setSignatureShow("是");
				BaseAdapter adapter  = (BaseAdapter) assetsLV.getAdapter();
				adapter.notifyDataSetChanged();
			}
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
