package zhwx.ui.dcapp.carmanage;

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
import zhwx.ui.dcapp.carmanage.model.OrderCarListItem;
import zhwx.ui.dcapp.carmanage.model.OrderCarTStatus;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;

/**   
 * @Title: OrderStatusFragment.java 
 * @Package zhwx.ui.dcapp.carmanage
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-18 下午1:59:08 
 */
public class OrderStatusFragment  extends ScrollTabHolderFragment {

	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private String id;
	
	private String json;
	
	private ListView statusLV;
	
	private List<OrderCarTStatus> statusList;
	
	private TextView emptyTV;
	
	public static Fragment newInstance(String id) {
		OrderStatusFragment f = new OrderStatusFragment();
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
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.getOrderCarStatus(ECApplication.getInstance().getV3Address(), map);
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
		statusList = new Gson().fromJson(json, new TypeToken<List<OrderCarTStatus>>(){}.getType());
		statusLV.setAdapter(new OrderStatusAdapter());
		mPostingdialog.dismiss();
	}
	
	public class OrderStatusAdapter extends BaseAdapter {
		
		public OrderStatusAdapter(Context context, List<OrderCarListItem> list,
				int listFlag) {
			super();
		}

		public OrderStatusAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return statusList.size();
		}

		@Override
		public OrderCarTStatus getItem(int position) {
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
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_timeline, null);
				holder = new ViewHolder();
				holder.statusViewTV = (TextView) convertView.findViewById(R.id.statusViewTV);
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
			holder.statusViewTV.setText(getItem(position).getName());
			holder.timeTV.setText(getItem(position).getOpTime());
			holder.contentTV.setText(getItem(position).getNote());
			if ("1".equals(getItem(position).getStatus())){
				holder.statusIV.setBackgroundResource(R.drawable.icon_timeline_dpc);
			} else if ("5".equals(getItem(position).getStatus())) {
				holder.statusIV.setBackgroundResource(R.drawable.icon_timeline_ypc);
			} else if ("7".equals(getItem(position).getStatus())) {
				holder.statusIV.setBackgroundResource(R.drawable.icon_timeline_ywc);
			}else if ("4".equals(getItem(position).getStatus())) {
				holder.statusIV.setBackgroundResource(R.drawable.icon_timeline_dsh);
			}else if ("2".equals(getItem(position).getStatus())) {
				holder.statusIV.setBackgroundResource(R.drawable.icon_timeline_dsh);
			}else if ("6".equals(getItem(position).getStatus())) {
				holder.statusIV.setBackgroundResource(R.drawable.icon_timeline_ychc);
			}else if ("3".equals(getItem(position).getStatus())) {
				holder.statusIV.setBackgroundResource(R.drawable.icon_timeline_ychc);
			}else if ("8".equals(getItem(position).getStatus())) {
				holder.statusIV.setBackgroundResource(R.drawable.icon_timeline_dpc);
			}else {
				holder.statusIV.setBackgroundResource(R.drawable.icon_cm_ymy);
			}
			return convertView;
		}

		private class ViewHolder {
			private TextView statusViewTV,timeTV,contentTV;
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
