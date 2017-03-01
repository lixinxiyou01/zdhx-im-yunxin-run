package zhwx.ui.dcapp.storeroom;

/** code is far away from bug with the animal protecting
* 
*     ┏┓　　　┏┓
*   ┏┛┻━━━┛┻┓
*   ┃　　　　　　　┃ 　
*   ┃　　　━　　　┃
*   ┃　┳┛　┗┳　┃
*   ┃　　　　　　　┃
*   ┃　　　┻　　　┃
*   ┃　　　　　　　┃
*   ┗━┓　　　┏━┛
 *     　   　┃　　　┃神兽保佑
 *     　   　┃　　　┃永无BUG！
 *     　　   ┃　　　┗━━━┓
 *     　   　┃　　　　　　　┣┓
 *     　   　┃　　　　　　　┏┛
 *     　   　┗┓┓┏━┳┓┏┛
 *   　  　   　┃┫┫　┃┫┫
 *   　  　   　┗┻┛　┗┻┛
*
*/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
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

import com.google.gson.Gson;
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
import zhwx.ui.dcapp.storeroom.model.StatisticsData;

/**   
 * @Title: AMainActivity.java 
 * @Package com.lanxum.hzth.im.ui.v3.assets 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午9:52:07 
 */
public class StatisticsActivity extends BaseActivity implements OnClickListener {
	
	private Activity context;
	
	private HashMap<String, ParameterValue> map;
	
	private String indexJson;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private ListView mystoreLV;
	
	private StatisticsData staticData;
	
    @Override
	protected int getLayoutId() {return R.layout.activity_sm_statistics;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"物品统计", this);
		initView();
		getData();
	}
	
	/**
	 * 
	 */
	private void initView() {
		mystoreLV = (ListView) findViewById(R.id.mystoreLV);
	}

	private void getData() {
		getNotice();   
	}
	
	private void getNotice(){
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getGoodsStatistics(ECApplication.getInstance().getV3Address(), map);
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
	
	private void refreshData(String indexJson) {
		if (indexJson.contains("</html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		staticData = new Gson().fromJson(indexJson, StatisticsData.class);
		mystoreLV.setAdapter(new OrderListAdapter());
		mPostingdialog.dismiss();
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
			return staticData.getGoodsstatistics().size();
		}

		@Override
		public StatisticsData.GoodsstatisticsBean getItem(int position) {
			return staticData.getGoodsstatistics().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sm_statistics, null);
				holder = new ViewHolder();
				holder.nameTV = (TextView) convertView.findViewById(R.id.nameTV);
				holder.inventoryTV = (TextView) convertView.findViewById(R.id.inventoryTV);
				holder.intoWarehouseCount = (TextView) convertView.findViewById(R.id.intoWarehouseCount);
				holder.outWarehouseCountTV = (TextView) convertView.findViewById(R.id.outWarehouseCountTV);
				holder.linearLayout1 = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position % 2 == 0) {
				holder.linearLayout1.setBackgroundColor(Color.parseColor("#ffffff"));
			} else {
				holder.linearLayout1.setBackgroundColor(Color.parseColor("#f6fbff"));
			}
			holder.nameTV.setText(getItem(position).getGoodsInfoName());
			holder.inventoryTV.setText(Html.fromHtml("<u>"+getItem(position).getInventory()+"</u>"));
			holder.intoWarehouseCount.setText(Html.fromHtml("<u>"+getItem(position).getIntoWarehouseCount()+"</u>"));
			holder.outWarehouseCountTV.setText(Html.fromHtml("<u>"+getItem(position).getOutWarehouseCount()+"</u>"));
			
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
			holder.inventoryTV.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, StatisticsDetailActivity.class);
					intent.putExtra("id", staticData.getGoodsstatistics().get(position).getId());
					intent.putExtra("kind", StatisticsDetailActivity.KIND_STORE);
					intent.putExtra("name", staticData.getGoodsstatistics().get(position).getGoodsInfoName());
					startActivity(intent);
				}
			});
			holder.intoWarehouseCount.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, StatisticsDetailActivity.class);
					intent.putExtra("id", staticData.getGoodsstatistics().get(position).getId());
					intent.putExtra("kind", StatisticsDetailActivity.KIND_IN);
					intent.putExtra("name", staticData.getGoodsstatistics().get(position).getGoodsInfoName());
					startActivity(intent);
				}
			});
			holder.outWarehouseCountTV.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, StatisticsDetailActivity.class);
					intent.putExtra("id", staticData.getGoodsstatistics().get(position).getId());
					intent.putExtra("kind", StatisticsDetailActivity.KIND_OUT);
					intent.putExtra("name", staticData.getGoodsstatistics().get(position).getGoodsInfoName());
					startActivity(intent);
				}
			});
		}
		private class ViewHolder {
			private TextView nameTV,inventoryTV,intoWarehouseCount,outWarehouseCountTV;
			private LinearLayout linearLayout1;
		}
		
	}
	
	public List<Button> getOrderButtonList(final int position){
		List<Button> btnList = new ArrayList<Button>();
		Button ckBT = getOrderButton("查看");
		ckBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, ApplyDetailActivity.class));
			}
		});
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
