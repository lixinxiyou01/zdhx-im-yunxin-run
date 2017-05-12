package zhwx.ui.dcapp.storeroom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.util.DensityUtil;
import zhwx.common.util.ToastUtil;
import zhwx.ui.dcapp.assets.model.CheckListItem;


/**   
 * @Title: GrantActivity.java 
 * @Package com.zdhx.edu.im.ui.v3.assets
 * @Description: 资产发放购物车
 * @author Li.xin @ zdhx
 * @date 2016年8月22日 下午12:42:43 
 */
public class SendBoxActivity extends BaseActivity implements OnClickListener{
	
	private Activity context;
	
	private static ListView grantLV;
	
	private Button addBT,grantBT;

	private TextView emptyTV;
	
	private CheckListItem model;
	
//	public List<Goods> allDataList = Goods.getMobList();
	@Override
	protected int getLayoutId() {
		return R.layout.activity_sm_grantbox;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		model = (CheckListItem) getIntent().getSerializableExtra("model");
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"待申领物品", this);
		grantLV = (ListView) findViewById(R.id.grantLV);
		emptyTV = (TextView) findViewById(R.id.emptyTV);
		grantLV.setEmptyView(emptyTV);
		addBT = (Button) findViewById(R.id.addBT);
		addBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, GoodsKindActivity.class));
			}
		});
		grantBT = (Button) findViewById(R.id.grantBT);
		grantBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, ApplyForSActivity.class));
			}
		});
//		grantLV.setAdapter(new OrderListAdapter());
	}
	
//	public class OrderListAdapter extends BaseAdapter {
//		
//		public OrderListAdapter(Context context, List<AllAssets> list,
//				int listFlag) {
//			super();
//		}
//
//		public OrderListAdapter() {
//			super();
//		}
//
//		@Override
//		public int getCount() {
//			return allDataList.size();
//		}
//
//		@Override
//		public Goods getItem(int position) {
//			return allDataList.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder holder;
//			if (convertView == null) {
//				
//				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sm_applybox, null);
//				holder = new ViewHolder();
//				holder.goodsNameTV = (TextView) convertView.findViewById(R.id.goodsNameTV);
//				holder.goodsUnitTV = (TextView) convertView.findViewById(R.id.goodsUnitTV);
//				holder.buttonContentLay = (LinearLayout) convertView.findViewById(R.id.buttonContentLay);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//			}
//			
//			holder.goodsNameTV.setText(getItem(position).getName());
//			holder.goodsUnitTV.setText(getItem(position).getUnit());
//			
//			//动态添加操作按钮
//			holder.buttonContentLay.removeAllViews();
//			List<Button> btns = getOrderButtonList(position);
//			for (Button button : btns) {
//				holder.buttonContentLay.addView(button);
//			}
//			addListener(holder, position, convertView);
//			return convertView;
//		}
//
//		/**
//		 * holerView 添加监听器
//		 * @param holder
//		 * @param position
//		 * @param view
//		 */
//		private void addListener(final ViewHolder holder, final int position,
//				final View view) {
//		}
//		private class ViewHolder {
//			private TextView goodsNameTV,goodsUnitTV;
//			private LinearLayout buttonContentLay;
//		}
//		
//	}
	
	public List<TextView> getOrderButtonList(final int position){
		List<TextView> btnList = new ArrayList<TextView>();
		TextView removeBT = getOrderButton("移除");
		removeBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ToastUtil.showMessage("移除");
			}
		});
		btnList.add(removeBT);
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
	protected void onResume() {
		super.onResume();
//		OrderListAdapter adapter = (OrderListAdapter) grantLV.getAdapter();
//		if(adapter!=null) {
//			adapter.notifyDataSetChanged();
//		}
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
