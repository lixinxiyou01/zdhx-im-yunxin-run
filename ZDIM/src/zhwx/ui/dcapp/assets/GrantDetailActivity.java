package zhwx.ui.dcapp.assets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
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
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.assets.model.GrantDetail;

/**   
 * @Title: ApplyDetailActivity.java 
 * @Package zhwx.ui.dcapp.assets
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年8月18日 下午3:41:53 
 */
public class GrantDetailActivity extends BaseActivity implements OnClickListener {
	
	private Activity context;
	
	private GrantDetail model;
	
	private String id;
	
	private String json;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;
	
	private TextView userNameTV; //使用人
	private TextView departmentTV; //使用人
	private TextView granterTV; //使用人
	private TextView grantDateTV; //使用人
	private ListView assetLV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"发放详情", this);
		id = getIntent().getStringExtra("id");
		getData();	
	}

	/**
	 * 
	 */
	private void getData() {
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.getGrantInfoJson(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (json.contains("<html>")) {
								ToastUtil.showMessage("数据异常");
								mPostingdialog.dismiss();
								return;
							}
							model = new Gson().fromJson(json, GrantDetail.class);
							initView();
							mPostingdialog.dismiss();
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
	 * 
	 */
	private void initView() {
		assetLV = (ListView) findViewById(R.id.assetLV);
		assetLV.setAdapter(new OrderListAdapter());
		Tools.setListViewHeightBasedOnChildren(assetLV);
		assetLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent(context, AssetDetailActivity.class);
				intent.putExtra("assetsCode", model.getGrantAssets().get(position).getCode());
				startActivity(intent);
			}
		});
		userNameTV = (TextView) findViewById(R.id.userNameTV);
		userNameTV.setText(model.getUser());
		departmentTV = (TextView) findViewById(R.id.departmentTV);
		departmentTV.setText(model.getDepartment());
		granterTV = (TextView) findViewById(R.id.granterTV);
		granterTV.setText(model.getOperator());
		grantDateTV = (TextView) findViewById(R.id.grantDateTV);
		grantDateTV.setText(model.getOperateDate());
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
			return model.getGrantAssets().size();
		}

		@Override
		public GrantDetail.GrantAssetsBean getItem(int position) {
			return model.getGrantAssets().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_as_grantassets, null);
				holder = new ViewHolder();
				holder.asNameTV = (TextView) convertView.findViewById(R.id.asNameTV);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.asNameTV.setText((position + 1) + ". " + getItem(position).getName());
			return convertView;
		}
		private class ViewHolder {
			private TextView asNameTV;
		}
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_as_grantdetail;
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
