package zhwx.ui.dcapp.assets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import volley.Response;
import volley.VolleyError;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.DensityUtil;
import zhwx.common.util.Log;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.refreshlayout.PullableListView;
import zhwx.ui.dcapp.assets.model.CheckListItem;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;

public class CheckListFragment extends ScrollTabHolderFragment {

	private static final String ARG_POSITION = "position";

	private PullableListView mListView;
	
	private int mPosition;

	private String status = "";
	
	/** 启动分类 */
	private int startFlag = -1;
	
	private RequestWithCacheGet mRequestWithCache;
	
	private HashMap<String, ParameterValue> map;
	
	private  List<CheckListItem> allDataList = new ArrayList<CheckListItem>();

	private List<CheckListItem> newDataList = new ArrayList<CheckListItem>();
	
	private OrderListAdapter adapter;
	
	private String json;
	
	private int pageNum = 1;
	
	private ECProgressDialog mPostingdialog;
	
	private Handler handler = new Handler();
	
	private Gson gson = new Gson(); 
	
	public static Fragment newInstance(int position,String status) {
		CheckListFragment f = new CheckListFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		b.putString("status", status);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mRequestWithCache = new RequestWithCacheGet(getActivity());
		mPosition = getArguments().getInt(ARG_POSITION);
		status = getArguments().getString("status");
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_ordercar_list, null);
		mListView = (PullableListView) v.findViewById(R.id.content_view);
		mListView.setEmptyView(v.findViewById(R.id.emptyView));
		mListView.enableAutoLoad(false);
		mListView.setOnLoadListener(new PullableListView.OnLoadListener() {

			@Override
			public void onLoad(PullableListView pullableListView) {
				if (StringUtil.isBlank(json)||"[]".equals(json)) {
					mListView.finishLoading();
					mListView.setLoadmoreVisible(false);
					return;
				}
				pageNum++;
				getcheckListByStatus(status, pageNum);
			}
		});
		getcheckListByStatus(status, pageNum);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		System.out.println("onActivityCreated");
		mListView.setOnScrollListener(new OnScroll());
	}
	
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public void adjustScroll(int scrollHeight) {
		if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
			return;
		}

		mListView.setSelectionFromTop(1, scrollHeight);

	}
	
	public class OnScroll implements OnScrollListener{

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (mScrollTabHolder != null)
				mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount, int pagePosition) {
	}
	
	
	private void getcheckListByStatus(String status,int pageNum){
		mPostingdialog = new ECProgressDialog(getActivity(), "正在获取信息");
		mPostingdialog.show();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("status", status);
		paramMap.put("pageNum", pageNum + "");
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("resultJson", new ParameterValue(gson.toJson(paramMap)));
		
		try {
			json = mRequestWithCache.getRseponse(UrlUtil.getCheckListJson(ECApplication.getInstance().getV3Address(), map),new RequestWithCacheGet.RequestListener() {

						@Override
						public void onResponse(String response) {
							if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
								Log.i("新数据:" + response);
								refreshData(response);
							} else {
								Log.i("缓存数据" + json);
								refreshData(json);
							}
							mPostingdialog.dismiss();
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							mPostingdialog.dismiss();
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
			mPostingdialog.dismiss();
		}
	}

	/**
	 * @param json2
	 */
	private void refreshData(String json2) {
		
		if(json2.contains("<html>")){
			ToastUtil.showMessage("数据异常");
			mPostingdialog.dismiss();
			return;
		}
		
		if(StringUtil.isBlank(json2)) {
			mPostingdialog.dismiss();
			return;
		}
		
		if (StringUtil.isBlank(json)||"[]".equals(json)) {
			mListView.finishLoading();
			mListView.setLoadmoreVisible(false);
		}
		if (pageNum == 1) {
			allDataList.clear();
		} else {
			mListView.finishLoading();
		}
		Gson gson = new Gson();
		newDataList = gson.fromJson(json2, new TypeToken<List<CheckListItem>>() {}.getType());
		
		if (newDataList != null && newDataList.size() != 0) {
			if(newDataList.size() < 20) {
				mListView.setLoadmoreVisible(false);
			}
			allDataList.addAll(newDataList);
		} else {
			return;
		}
		if (pageNum == 1) {
			adapter = new OrderListAdapter();
			mListView.setAdapter(adapter);
		} else {
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
		mPostingdialog.dismiss();
	}
	
	public class OrderListAdapter extends BaseAdapter {
		
		public OrderListAdapter(Context context, List<CheckListItem> list,
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
		public CheckListItem getItem(int position) {
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
				
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_as_check, null);
				holder = new ViewHolder();
				holder.asKindTV = (TextView) convertView.findViewById(R.id.asKindTV);
				holder.useDateTV = (TextView) convertView.findViewById(R.id.useDateTV);
				holder.userNameTV = (TextView) convertView.findViewById(R.id.userNameTV);
				holder.departmentTV = (TextView) convertView.findViewById(R.id.departmentTV);
				holder.schoolTV = (TextView) convertView.findViewById(R.id.schoolTV);
				holder.needIntroductionsTV = (TextView) convertView.findViewById(R.id.needIntroductionsTV);
				holder.useWayTV = (TextView) convertView.findViewById(R.id.useWayTV);
				holder.orderTimeTV = (TextView) convertView.findViewById(R.id.orderTimeTV);
				holder.checkStatusViewTV = (TextView) convertView.findViewById(R.id.checkStatusViewTV);
				holder.buttonContentLay = (LinearLayout) convertView.findViewById(R.id.buttonContentLay);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.asKindTV.setText(getItem(position).getAssetKindName());
			holder.useDateTV.setText(getItem(position).getApplyDate());
			holder.userNameTV.setText(getItem(position).getApplyUser());
			holder.departmentTV.setText(getItem(position).getDepartment());
			holder.schoolTV.setText(getItem(position).getSchool());
			holder.needIntroductionsTV.setText(getItem(position).getDemand());
			holder.useWayTV.setText(getItem(position).getReason());
//			holder.orderTimeTV.setText(getItem(position).getCheckStatusView());
			holder.checkStatusViewTV.setText(getItem(position).getCheckStatusView());
			
			//动态添加操作按钮
			
			if(StringUtil.isNotBlank(getItem(position).getCheckStatus())) {
				holder.buttonContentLay.removeAllViews();
				List<TextView> btns = getOrderButtonList(getItem(position).getCheckStatus(),position);
				for (TextView button : btns) {
					holder.buttonContentLay.addView(button);
				}
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
			private TextView asKindTV, useDateTV, userNameTV, departmentTV,
			schoolTV,needIntroductionsTV,useWayTV,orderTimeTV,checkStatusViewTV;
			private LinearLayout buttonContentLay;
		}
		
	}
	
	public List<TextView> getOrderButtonList(final String status,final int position){
		List<TextView> btnList = new ArrayList<TextView>();
		TextView ckBT = getOrderButton("查看");
		ckBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				if (status.equals(CheckListItem.STATUS_GRANTED)) {
//					Intent intent = new Intent(getActivity(), GrantDetailActivity.class);
//					intent.putExtra("checkListItem", allDataList.get(position));
//					intent.putExtra("id", allDataList.get(position).getId());
//					startActivity(intent);
//				} else {
//					Intent intent = new Intent(getActivity(), ApplyDetailActivity.class);
//					intent.putExtra("checkListItem", allDataList.get(position));
//					startActivity(intent);
//				}
				Intent intent = new Intent(getActivity(), ApplyDetailActivity.class);
				intent.putExtra("checkListItem", allDataList.get(position));
				startActivity(intent);
			}
		});
		TextView shBT = getOrderButton("审核");
		shBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), AsCheckActivity.class);
				intent.putExtra("checkListItem", allDataList.get(position));
				startActivityForResult(intent, 103);
			}
		});
		TextView ffBT = getOrderButton("发放");
		ffBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent  = new Intent(getActivity(), GrantBoxActivity.class);
				intent.putExtra("model", allDataList.get(position));
				startActivity(intent);
			}
		});
		
		
		if (status.equals(CheckListItem.STATUS_NOTAUDIT)) {  //未审核
			btnList.add(shBT);
		} else if (status.equals(CheckListItem.STATUS_NOTPASS)) { //未通过
			btnList.add(shBT);
		} else if (status.equals(CheckListItem.STATUS_GRANTED)) { //已发放
			
		}else if (status.equals(CheckListItem.STATUS_PASS)) {  //已通过
			btnList.add(shBT);
			btnList.add(ffBT);
		}
		btnList.add(ckBT);
		return btnList;
	}
	
	public TextView getOrderButton (String text) {
		LayoutParams params = new LayoutParams(
			    LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(30));
		params.setMargins(0, 0, DensityUtil.dip2px(10), 0);
		TextView button = new TextView(getActivity());
		button.setText(text);
		button.setTextColor(Color.parseColor("#555555"));
		button.setBackgroundResource(R.drawable.btn_selector_ordercar);
		button.setLayoutParams(params);
		return button;
	}
	public void addButtonListener(Button btn) {
		
	}
	
	/** 取消订车单 */
	public void cancelOrderCar(final int position) {
		String orderId = "";
		mPostingdialog = new ECProgressDialog(getActivity(), "正在取消订车单");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(orderId));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.cancelOrderCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("订单已取消");
								allDataList.remove(position);
								adapter.notifyDataSetChanged();
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
}