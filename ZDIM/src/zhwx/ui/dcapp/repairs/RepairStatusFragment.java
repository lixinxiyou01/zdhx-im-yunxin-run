package zhwx.ui.dcapp.repairs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;
import zhwx.ui.dcapp.repairs.model.RepairStatus;

/**   
 * @Title: RepairStatusFragment.java
 * @Package zhwx.ui.dcapp.carmanage
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-18 下午1:59:08 
 */
public class RepairStatusFragment extends ScrollTabHolderFragment {

	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private String id;
	
	private String json;
	
	private ListView statusLV;
	
	private List<RepairStatus> statusList;
	
	private TextView emptyTV;
	
	public static Fragment newInstance(String id) {
		RepairStatusFragment f = new RepairStatusFragment();
		Bundle b = new Bundle();
		b.putString("id", id);
		f.setArguments(b);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		id = getArguments().getString("id");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_order_status, null);
		statusLV = (ListView) v.findViewById(R.id.statusLV);
		emptyTV = (TextView) v.findViewById(R.id.emptyTV);
		statusLV.setEmptyView(emptyTV);
		getDetail();
		return v;
	}

	private void getDetail(){
		mPostingdialog = new ECProgressDialog(getActivity(), "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("repairId", new ParameterValue(id));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.getRepairRecord(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(json);
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
	
	private void refreshData(String json) {
		System.out.println(json);
		if(json.contains("<html>")){
			ToastUtil.showMessage("数据异常");
			return;
		}
		statusList = new Gson().fromJson(json, new TypeToken<List<RepairStatus>>(){}.getType());
		statusLV.setAdapter(new StatusAdapter());
		mPostingdialog.dismiss();
	}
	
	public class StatusAdapter extends BaseAdapter {
		
		public StatusAdapter(Context context, List<RepairStatus> list,
				int listFlag) {
			super();
		}

		public StatusAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return statusList.size();
		}

		@Override
		public RepairStatus getItem(int position) {
			return statusList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_timeline_rm_repair_stutas, null);
				holder = new ViewHolder();
				holder.timeTV = (TextView) convertView.findViewById(R.id.timeTV);
				holder.contentTV = (TextView) convertView.findViewById(R.id.contentTV);
				holder.statusIV = (ImageView) convertView.findViewById(R.id.statusIV);
				holder.lineView = (View) convertView.findViewById(R.id.lineView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position == getCount() - 1) {
				holder.lineView.setVisibility(View.INVISIBLE);
			} else {
				holder.lineView.setVisibility(View.VISIBLE);
			}
			holder.timeTV.setText(getItem(position).getRecordTime());
			holder.contentTV.setText(getItem(position).getRecordContent());
			return convertView;
		}

		private class ViewHolder {
			private TextView timeTV,contentTV;
			private ImageView statusIV;
			private View lineView;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void adjustScroll(int scrollHeight) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount, int pagePosition) {

	}
}
