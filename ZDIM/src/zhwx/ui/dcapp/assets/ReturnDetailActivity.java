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
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.assets.model.ReturnDetail;

/**   
 * @Title: AssetDetailActivity.java 
 * @Package zhwx.ui.dcapp.assets
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年8月18日 下午6:11:49 
 */
public class ReturnDetailActivity extends BaseActivity implements OnClickListener{
	
	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;
	
	private Activity context;
	
	private Handler handler = new Handler();
	
	private String json;
	
	private String id;
	
	private Gson gson = new Gson();
	
	private ReturnDetail detail;
	
	private ListView assetLV;
	
	private TextView asNameTV,asKindTV,asTypeTV,codeTV,getDateTV,checkStatusViewTV;
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_as_returndetail;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"归还详情", this);
		id = getIntent().getStringExtra("id");
		initView();
		getDetail();
		
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		asNameTV = (TextView) findViewById(R.id.asNameTV);
		asKindTV = (TextView) findViewById(R.id.asKindTV);
		asTypeTV = (TextView) findViewById(R.id.asTypeTV);
		codeTV = (TextView) findViewById(R.id.codeTV);
		getDateTV = (TextView) findViewById(R.id.getDateTV);
		assetLV = (ListView) findViewById(R.id.assetLV);
		assetLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent(context, AssetDetailActivity.class);
				intent.putExtra("assetsCode", detail.getReturnAssets().get(position).getCode());
				startActivity(intent);
			}
		});
	}

	private void getDetail(){
		mPostingdialog = new ECProgressDialog(context, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.getReturnInfoJson(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(json);
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("资产未找到");
					finish();
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
	
	private void refreshData(String json) {
		System.out.println(json);
		if (StringUtil.isNotBlank(json)&&!json.contains("<html>")) {
			detail = gson.fromJson(json, ReturnDetail.class);
			asNameTV.setText(detail.getDepartmentName());
			asTypeTV.setText(detail.getUserName());
			codeTV.setText(detail.getOperatorName());
			getDateTV.setText(detail.getOperateDate());
			assetLV.setAdapter(new OrderListAdapter());
		}
		mPostingdialog.dismiss();
	}
	
	public class OrderListAdapter extends BaseAdapter {
		
		public OrderListAdapter(Context context, List<ReturnDetail.ReturnAssetsBean> list,
				int listFlag) {
			super();
		}

		public OrderListAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return detail.getReturnAssets().size();
		}

		@Override
		public ReturnDetail.ReturnAssetsBean getItem(int position) {
			return detail.getReturnAssets().get(position);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		}
	}
}
