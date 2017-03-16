package zhwx.ui.dcapp.repairs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
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
import zhwx.ui.dcapp.repairs.model.Worker;


/**   
 * @Title: GrantActivity.java 
 * @Package zhwx.ui.dcapp.assets
 * @Description: 资产发放购物车
 * @author Li.xin @ zdhx
 * @date 2016年8月22日 下午12:42:43 
 */
public class SendWorkActivity extends BaseActivity implements OnClickListener{
	
	private Activity context;
		
	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;
	
	private ListView assetsLV;

	private String circleJson;

	private String repairId;

	private List<Worker> workers;

	private Handler handler = new Handler();

	private String workerId = "";


	@Override
	protected int getLayoutId() {
		return R.layout.activity_rm_choose_worker;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, "确定","指定维修人员", this);
		repairId = getIntent().getStringExtra("repairId");
		assetsLV = (ListView) findViewById(R.id.workerLV);
		assetsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < workers.size(); i++) {
                    if(position == i) {
                        workers.get(i).setCheck(true);
                        workerId = workers.get(i).getWorkerId();
                    } else {
                        workers.get(i).setCheck(false);
                    }
                }
                BaseAdapter adapter = (BaseAdapter) assetsLV.getAdapter();
                adapter.notifyDataSetChanged();
			}
		});
		getData();
	}

	/**
	 *
	 */
	private void getData() {
		mPostingdialog = new ECProgressDialog(context, "正在获取数据");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("repairId", new ParameterValue(repairId));
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					circleJson = UrlUtil.getCanDoWorkerList(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(circleJson);
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
	private void refreshData(String circleJson2) {
		if (circleJson2.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		System.out.println(circleJson2);
		workers = new Gson().fromJson(circleJson2, new TypeToken<List<Worker>>() {}.getType());
		if(workers != null) {
			assetsLV.setAdapter(new OrderListAdapter());
		}
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
			return workers.size();
		}

		@Override
		public Worker getItem(int position) {
			return workers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_rm_worker, null);
				holder = new ViewHolder();
				holder.workerNameTV = (TextView) convertView.findViewById(R.id.workerNameTV);
				holder.kindCountTV = (TextView) convertView.findViewById(R.id.kindCountTV);
				holder.ingTV = (TextView) convertView.findViewById(R.id.ingTV);
				holder.selRB = (RadioButton) convertView.findViewById(R.id.selRB);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.workerNameTV.setText(getItem(position).getWorkerName());
			holder.kindCountTV.setText(getItem(position).getSameKindWorkCount());
			holder.ingTV.setText(getItem(position).getWorkingCount());
			if (getItem(position).isCheck()) {
				holder.selRB.setChecked(true);
			} else {
				holder.selRB.setChecked(false);
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
		private void addListener(final ViewHolder holder, final int position,final View view) {
			holder.selRB.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					for (int i = 0; i < workers.size(); i++) {
						if(position == i) {
							workers.get(i).setCheck(true);
							workerId = workers.get(i).getWorkerId();
						} else {
							workers.get(i).setCheck(false);
						}
					}
					notifyDataSetChanged();
				}
			});
		}
		private class ViewHolder {
			private TextView workerNameTV,kindCountTV,ingTV;
			private RadioButton selRB;
		}

	}


	/** 派单 */
	public void saveSendRequest() {
		mPostingdialog = new ECProgressDialog(context, "正在派单");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("repairId", new ParameterValue(repairId));
		map.put("workerId", new ParameterValue(workerId));
		map.put("sendMessageFlag", new ParameterValue("true"));
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.saveSendRequest(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("派单成功");
								finish();
							} else {
								ToastUtil.showMessage("操作失败");
							}
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.text_right:
			saveSendRequest();
			break;
	}
}
}
