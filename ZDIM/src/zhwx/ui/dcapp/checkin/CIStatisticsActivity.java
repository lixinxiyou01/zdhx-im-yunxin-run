package zhwx.ui.dcapp.checkin;

import android.app.Activity;
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

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
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
import zhwx.common.util.DateUtil;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.checkin.model.CIStatistics;

/**
 * @Title: CIStatisticsActivity.java 
 * @Package com.zdhx.edu.im.ui.v3.checkin
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年9月26日 下午3:11:05 
 */
public class CIStatisticsActivity extends BaseActivity implements OnClickListener ,OnDateSetListener {
	
	private Activity context;
	
	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();
	
	private ECProgressDialog mPostingdialog;
	
	private String json;
	
	private List<CIStatistics> list = new ArrayList<CIStatistics>();
	
	private ListView statisticLV;
	
	private String DATEPICKER_TAG = "datepicker";
	
	private String dataString = "";
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_cistatistics;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		statisticLV = (ListView) findViewById(R.id.statisticLV);
		getTopBarView().setBackGroundColor(R.color.main_bg_checkin);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, R.drawable.icon_ci_history,"考勤统计", this);
		dataString = DateUtil.getCurrDateString();
		getNotice(dataString);
	}
	
	
	private void getNotice(String timeString){
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("date", new ParameterValue(timeString));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.getListOfDate(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData1(json);
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
	
	private void refreshData1(String json) {
		mPostingdialog.dismiss();
		if (json.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		list = new Gson().fromJson(json, new TypeToken<List<CIStatistics>>() {}.getType());
		statisticLV.setAdapter(new StatisticListAdapter());
		getTopBarView().setSubTitle(dataString);
	}

	public class StatisticListAdapter extends BaseAdapter {

		public StatisticListAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public CIStatistics getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_ci_statistics, null);
				holder = new ViewHolder();
				holder.nameTV = (TextView) convertView.findViewById(R.id.nameTV);
				holder.timeLongTV = (TextView) convertView.findViewById(R.id.timeLongTV);
				holder.shangTimeTV = (TextView) convertView.findViewById(R.id.shangTimeTV);
				holder.shangAddressTV = (TextView) convertView.findViewById(R.id.shangAddressTV);
				holder.xiaAddressTV = (TextView) convertView.findViewById(R.id.xiaAddressTV);
				holder.xiaTimeTV = (TextView) convertView.findViewById(R.id.xiaTimeTV);
				holder.nameLay = (LinearLayout) convertView.findViewById(R.id.nameLay);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (ECApplication.getInstance().getCurrentIMUser().getName().equals(getItem(position).getName())) {
				holder.nameTV.setTextColor(Color.parseColor("#ed9435"));
			} else {
				holder.nameTV.setTextColor(Color.parseColor("#18ab8e"));
			}
			
			holder.nameTV.setText(getItem(position).getName());
			if(StringUtil.isNotBlank(getItem(position).getWorkTime())) {
				holder.timeLongTV.setText(getItem(position).getWorkTime() + "小时");
			} else {
				holder.timeLongTV.setText("");
			}
 			holder.shangTimeTV.setText(StringUtil.isBlank(getItem(position).getStartTime())?"未考勤":getItem(position).getStartTime());
			holder.shangAddressTV.setText(StringUtil.isBlank(getItem(position).getStartAddress())?"暂无":getItem(position).getStartAddress());
			holder.xiaAddressTV.setText(StringUtil.isBlank(getItem(position).getEndAddress())?"暂无":getItem(position).getEndAddress());
			holder.xiaTimeTV.setText(StringUtil.isBlank(getItem(position).getEndTime())?"未考勤":getItem(position).getEndTime());
			addListener(holder, position, convertView);
			return convertView;
		}

		/**
		 * holerView 添加监听器
		 * 
		 * @param holder
		 * @param position
		 * @param view
		 */
		private void addListener(final ViewHolder holder, final int position,
				final View view) {
		}

		private class ViewHolder {
			private LinearLayout nameLay;
			private TextView nameTV, timeLongTV, shangTimeTV, shangAddressTV, xiaAddressTV, xiaTimeTV;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
					this, calendar.get(java.util.Calendar.YEAR),
					calendar.get(java.util.Calendar.MONTH),
					calendar.get(java.util.Calendar.DAY_OF_MONTH));
			datePickerDialog.show(getFragmentManager(),DATEPICKER_TAG);
			break;
		}
	}

	@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear,
			int dayOfMonth) {
		dataString = year + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
		getNotice(dataString);
	}
}
