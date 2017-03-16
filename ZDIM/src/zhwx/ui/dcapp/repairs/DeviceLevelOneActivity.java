package zhwx.ui.dcapp.repairs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.refreshlayout.PullableListView;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.repairs.model.DeviceKind;


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
	
	private String circleJson;
	
	private  List<DeviceKind> allDataList;

	private ImageLoader imageLoader;

	private String levelOneId;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_rm_device_level_one;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		levelOneId = getIntent().getStringExtra("id");
		imageLoader = new ImageLoader(context);
		cache = new RequestWithCacheGet(context);
		getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"设备分类", this);
		assetsLV = (PullableListView) findViewById(R.id.assetsLV);
		assetsLV.enableAutoLoad(false);
		assetsLV.setLoadmoreVisible(false);
		emptyTV = (TextView) findViewById(R.id.emptyTV);
		assetsLV.setEmptyView(emptyTV);
		assetsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivityForResult(new Intent(context,DeviceLevelTwoActivity.class).putExtra("id",allDataList.get(position).getId()),886);
			}
		});
		getData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 886) {
			setResult(886);
			finish();
		}
	}

	/**
	 * 
	 */
	private void getData() {
		mPostingdialog = new ECProgressDialog(context, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("groupId",new ParameterValue(levelOneId));
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
		allDataList = new Gson().fromJson(circleJson2, new TypeToken<List<DeviceKind>>() {}.getType());
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
		public DeviceKind getItem(int position) {
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
				
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_devicelevel_two, null);
				holder = new ViewHolder();
				holder.kindNameTV = (TextView) convertView.findViewById(R.id.kindNameTV);
				holder.kindImgIV = (ImageView) convertView.findViewById(R.id.kindImgIV);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.kindNameTV.setText(getItem(position).getName());
			imageLoader.DisplayImage(ECApplication.getInstance().getV3Address() + getItem(position).getImageUrl(), holder.kindImgIV,false);
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
			private TextView kindNameTV;
			private ImageView kindImgIV;
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
