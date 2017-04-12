package zhwx.ui.dcapp.storeroom;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.refreshlayout.PullableListView;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.storeroom.model.GetOutBean;

/**   
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午9:52:07 
 */
public class ToGetOutListActivity extends BaseActivity implements OnClickListener {
	
	private Activity context;
	
	private HashMap<String, ParameterValue> map;
	
	private String indexJson;
	
	private Handler handler = new Handler();

	private List<GetOutBean> allDataList = new ArrayList<GetOutBean>();

	private List<GetOutBean> newDataList = new ArrayList<GetOutBean>();

	private PullableListView mystoreLV;
	
	private View emptyView;

	private OrderListAdapter adapter;

	private int pageNo = 1;
	
    @Override
	protected int getLayoutId() {return R.layout.activity_sm_getoutlist;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, "添加","出库单", this);
		initView();
		getData(pageNo);
	}
	
	/**
	 * 
	 */
	private void initView() {
		mystoreLV = (PullableListView) findViewById(R.id.mystoreLV);
		emptyView = findViewById(R.id.emptyView);
		mystoreLV.setEmptyView(emptyView);
		mystoreLV.setOnLoadListener(new PullableListView.OnLoadListener() {

			@Override
			public void onLoad(PullableListView pullableListView) {
				if (pageNo != 1 && (StringUtil.isBlank(indexJson)||"[]".equals(indexJson.trim()))) {
					mystoreLV.finishLoading();
					mystoreLV.setLoadmoreVisible(false);
					ToastUtil.showMessage("到底了");
					return;
				}
				pageNo++;
				getData(pageNo);
			}
		});
	}

	private void getData(int pageNo) {
		getNotice(pageNo);   //获取公告板数据
	}
	
	private void getNotice(int pageNo){
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
		map.put("num", new ParameterValue(pageNo+""));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getOutWarehouseList(ECApplication.getInstance().getV3Address(), map);
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
						}
					}, 5);
				}
			}
		}).start();
	}
	
	private void refreshData(String indexJson) {
		if (indexJson.contains("</html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		if (StringUtil.isBlank(indexJson)||"[]".equals(indexJson)) {
			mystoreLV.finishLoading();
			mystoreLV.setLoadmoreVisible(false);
		}
		if (pageNo == 1) {
			allDataList.clear();
		} else {
			mystoreLV.finishLoading();
		}
		Gson gson = new Gson();
		newDataList = gson.fromJson(indexJson, new TypeToken<List<GetOutBean>>() {}.getType());
		if (newDataList != null && newDataList.size() != 0) {
			allDataList.addAll(newDataList);
		} else {
			return;
		}
		if (pageNo == 1) {
			if(allDataList.size() < 20) {
				mystoreLV.finishLoading();
				mystoreLV.setLoadmoreVisible(false);
			}
			adapter = new OrderListAdapter();
			mystoreLV.setAdapter(adapter);
		} else {
			if (adapter != null) {
				adapter.notifyDataSetChanged();
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
		public GetOutBean getItem(int position) {
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
				
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sm_getout, null);
				holder = new ViewHolder();
				holder.smCodeTV = (TextView) convertView.findViewById(R.id.smCodeTV);
				holder.departmentNameTV = (TextView) convertView.findViewById(R.id.departmentNameTV);
				holder.smApplyDateTV = (TextView) convertView.findViewById(R.id.smApplyDateTV);
				holder.outKindTV = (TextView) convertView.findViewById(R.id.outKindTV);
				holder.signViewTV = (TextView) convertView.findViewById(R.id.signViewTV);
				holder.receiverNameTV = (TextView) convertView.findViewById(R.id.receiverNameTV);
				holder.buttonContentLay = (LinearLayout) convertView.findViewById(R.id.buttonContentLay);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.smCodeTV.setText(getItem(position).getCode());
			holder.departmentNameTV.setText(getItem(position).getWarehouseName());
			holder.smApplyDateTV.setText(getItem(position).getDate());
			holder.outKindTV.setText(getItem(position).getOutKindValue());
			holder.receiverNameTV.setText(getItem(position).getReceiverName());
			if (!getItem(position).isSignatureFlag()) {
				holder.signViewTV.setText("未签字");
				holder.signViewTV.setTextColor(Color.RED);
			} else {
				holder.signViewTV.setText("已签字");
				holder.signViewTV.setTextColor(Color.parseColor("#3573a2"));
			}
			
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
			private TextView signViewTV,departmentNameTV,smApplyDateTV,outKindTV,smCodeTV,receiverNameTV;
			private LinearLayout buttonContentLay;
		}
		
	}
	
	public List<TextView> getOrderButtonList(final int position){
		List<TextView> btnList = new ArrayList<TextView>();
		TextView ckBT = getOrderButton("查看");
		ckBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, GetOutDetailActivity.class).putExtra("id", allDataList.get(position).getId()));
			}
		});
		TextView bqBT = getOrderButton("补签");
		bqBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(context, SReSingActivity.class).putExtra("id", allDataList.get(position).getId()),111);
			}
		});
		if(!allDataList.get(position).isSignatureFlag()) {
			btnList.add(bqBT);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 120) {
			pageNo = 1;
			getData(pageNo);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.text_right:
			startActivityForResult(new Intent(context, GrantByHandActivity.class),111);
			break;
		}
	}
}
