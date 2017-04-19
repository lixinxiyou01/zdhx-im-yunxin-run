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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.storeroom.model.StatisticsData;
import zhwx.ui.dcapp.storeroom.model.StoreHandleRecord;

/**   
 * @Title: AMainActivity.java 
 * @Package com.lanxum.hzth.im.ui.v3.assets 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午9:52:07 
 */
public class StoreHandleDetailActivity extends BaseActivity implements OnClickListener {
	
	private Activity context;
	
	private HashMap<String, ParameterValue> map;
	
	private String indexJson;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private ListView mystoreLV;
	
	private StatisticsData staticData;
	
	private String id = "";

	private String warehouseId = "";
	
	private String name = "";
	
	private int kind = 0;
	
	private List<StoreHandleRecord> recordList;
	
	public static final int KIND_STORE = 0;
	public static final int KIND_OUT = 1;
	public static final int KIND_IN = 2;
	
	
    @Override
	protected int getLayoutId() {return R.layout.activity_sm_statistics_detail;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		kind = getIntent().getIntExtra("kind", 0);
		id = getIntent().getStringExtra("id");
		warehouseId = getIntent().getStringExtra("warehouseId");
		name = getIntent().getStringExtra("name");
		if (kind == KIND_IN) {
			getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"入库详情", this);
		} else if(kind == KIND_OUT){
			getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"出库详情", this);
		} else if(kind == KIND_STORE){
			getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"库存详情", this);
		}
		getTopBarView().setSubTitle(name);
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
		map.put("goodsId", new ParameterValue(id));
		map.put("warehouseId", new ParameterValue(warehouseId));
		if (kind == KIND_STORE) {
		} else if (kind == KIND_IN){
			map.put("handleFlag", new ParameterValue("1"));
		}else if (kind == KIND_OUT){
			map.put("handleFlag", new ParameterValue("2"));
		}
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getStoreHandleRecord(ECApplication.getInstance().getV3Address(), map);
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
		recordList = new Gson().fromJson(indexJson, new TypeToken<List<StoreHandleRecord>>() {}.getType());
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
			return recordList.size() + 1;
		}

		@Override
		public StoreHandleRecord getItem(int position) {
			return recordList.get(position - 1);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sm_statistics_inout, null);
				holder = new ViewHolder();
				holder.schoolTV = (TextView) convertView.findViewById(R.id.schoolTV);
				holder.storeTV = (TextView) convertView.findViewById(R.id.storeTV);
				holder.countTV = (TextView) convertView.findViewById(R.id.countTV);
				holder.moneyTV = (TextView) convertView.findViewById(R.id.moneyTV);
				holder.linearLayout1 = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position % 2 == 0) {
				holder.linearLayout1.setBackgroundColor(Color.parseColor("#f6fbff"));
			} else {
				holder.linearLayout1.setBackgroundColor(Color.parseColor("#ffffff"));
			}
			if (position == 0) {
				if (kind == KIND_IN) {
					holder.schoolTV.setText("制单人");
				} else if(kind == KIND_OUT){
					holder.schoolTV.setText("申领人");

				}
				holder.storeTV.setText("日期");
				holder.countTV.setText("数量");
				holder.moneyTV.setText("金额");
			} else {
				holder.schoolTV.setText(getItem(position).getName());
				holder.storeTV.setText(getItem(position).getDate());
				holder.countTV.setText(getItem(position).getCount());
				holder.moneyTV.setText(getItem(position).getSum());
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
			private TextView schoolTV,storeTV,countTV,moneyTV;
			private LinearLayout linearLayout1;
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
		}
	}
}
