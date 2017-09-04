package zhwx.ui.dcapp.repairs;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.demo.contact.activity.UserProfileActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.refreshlayout.PullableListView;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;
import zhwx.ui.dcapp.repairs.model.RepairListItem;

public class MyRepairListFragment extends ScrollTabHolderFragment {

	private static final String ARG_POSITION = "position";

	private PullableListView mListView;
	
	private int mPosition;

	private String tabName;
	
	private String status;
	
	/** 启动分类 */
	private int startFlag = -1;
	
	private RequestWithCacheGet mRequestWithCache;
	
	private HashMap<String, ParameterValue> map;
	
	private  List<RepairListItem> allDataList = new ArrayList<RepairListItem>();

	private List<RepairListItem> newDataList = new ArrayList<RepairListItem>();
	
	private RepairAdapter adapter;
	
	private String json;
	private String accId;

	private int pageNum = 1;
	
	private ECProgressDialog mPostingdialog;
	
	private Handler handler = new Handler();
	
	public static Fragment newInstance(int position,String tabName,String status,int startFlag) {
		MyRepairListFragment f = new MyRepairListFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		b.putInt("startFlag", startFlag);
		b.putString("tabName", tabName);
		b.putString("status", status);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mRequestWithCache = new RequestWithCacheGet(getActivity());
		mPosition = getArguments().getInt(ARG_POSITION);
		tabName = getArguments().getString("tabName");
		status = getArguments().getString("status");
		startFlag = getArguments().getInt("startFlag");
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_myrapir_list, null);
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
				getRepairListByStatus(status, pageNum);
			}
		});
		getRepairListByStatus(status, pageNum);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
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
	
	
	private void getRepairListByStatus(String status,int pageNum){
		mPostingdialog = new ECProgressDialog(getActivity(), "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("status", new ParameterValue(status));
		map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
		map.put("pageNum", new ParameterValue(pageNum + ""));
		
		String url = "";
		if (RMainActivity.STARTFLAG_MYREQUEST == startFlag) {
			url = UrlUtil.getRepairRequestListByStatus(ECApplication.getInstance().getV3Address(),map);
		} else if (RMainActivity.STARTFLAG_MYTASK == startFlag){
			url = UrlUtil.getMyRepairListByStatus(ECApplication.getInstance().getV3Address(),map);
		} else if (RMainActivity.STARTFLAG_ORDERCHECK == startFlag){
			url = UrlUtil.getManageRepairListByStatus(ECApplication.getInstance().getV3Address(),map);
		}
		try {
			json = mRequestWithCache.getRseponse(url,new RequestWithCacheGet.RequestListener() {

						@Override
						public void onResponse(String response) {
							if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
								Log.i("新数据:" + response);
								refreshData(response);
							} else {
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
		//TODO
		Gson gson = new Gson();
		newDataList = gson.fromJson(json2, new TypeToken<List<RepairListItem>>() {}.getType());

		if (newDataList != null && newDataList.size() != 0) {
			if(newDataList.size() < 20) {
				mListView.setLoadmoreVisible(false);
			}
			allDataList.addAll(newDataList);
		} else {
			return;
		}
		if (pageNum == 1) {
			adapter = new RepairAdapter();
			mListView.setAdapter(adapter);
		} else {
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
		mPostingdialog.dismiss();
	}
	
	public class RepairAdapter extends BaseAdapter {
		
		public RepairAdapter(Context context, List<RepairListItem> list,int listFlag) {
			super();
		}

		public RepairAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return allDataList.size();
		}

		@Override
		public RepairListItem getItem(int position) {
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
				if (startFlag == RMainActivity.STARTFLAG_MYTASK) {
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_repair, null);
				} else if (startFlag == RMainActivity.STARTFLAG_MYREQUEST){
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_repair, null);
				} else if (startFlag == RMainActivity.STARTFLAG_ORDERCHECK){
					if(RepairListItem.COST_STATUS_NEED.equals(getItem(position).getCheckStatus())) {
						convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_repair_manager_check, null);
					} else {
						convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_repair_manager, null);
					}
				}

				holder = new ViewHolder();
				holder.titleTV = (TextView) convertView.findViewById(R.id.titleTV);
				holder.requestTimeTV = (TextView) convertView.findViewById(R.id.requestTimeTV);
				holder.faultTV = (TextView) convertView.findViewById(R.id.faultTV);
				holder.checkStatusViewTV = (TextView) convertView.findViewById(R.id.checkStatusViewTV);
				holder.faultDescriptionTV = (TextView) convertView.findViewById(R.id.faultDescriptionTV);

				holder.requestUserNameTV = (TextView) convertView.findViewById(R.id.requestUserNameTV);
				holder.deviceNameTV = (TextView) convertView.findViewById(R.id.deviceNameTV);
				holder.faultPlaceTV = (TextView) convertView.findViewById(R.id.faultPlaceTV);
				holder.requestUserPhoneTV = (TextView) convertView.findViewById(R.id.requestUserPhoneTV);
				holder.costTV = (TextView) convertView.findViewById(R.id.costTV);

				holder.phoneIV = (ImageView) convertView.findViewById(R.id.phoneIV);
				holder.messageIV = (ImageView) convertView.findViewById(R.id.messageIV);

				holder.buttonContentLay = (LinearLayout) convertView.findViewById(R.id.buttonContentLay);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			setText(holder.titleTV,getItem(position).getPlaceName()+getItem(position).getDeviceName()+"报修");
			setText(holder.requestTimeTV,getItem(position).getApplyTime());
			setText(holder.faultTV,getItem(position).getFaultDescription());
			setText(holder.checkStatusViewTV,getItem(position).getStatusView());
			setText(holder.faultDescriptionTV,getItem(position).getFaultDescription());

			setText(holder.requestUserNameTV,getItem(position).getRequestUserName());
			setText(holder.deviceNameTV,getItem(position).getDeviceName());
			setText(holder.faultPlaceTV,getItem(position).getPlaceName());
			setText(holder.requestUserPhoneTV,getItem(position).getRequestUserPhone());

			setText(holder.costTV,getItem(position).getCostApplication() + "元");

			//动态添加操作按钮
			holder.buttonContentLay.removeAllViews();
			List<TextView> btns = getOrderButtonList(getItem(position).getStatusCode(),position,getItem(position).getCheckStatus());
			for (TextView button : btns) {
				holder.buttonContentLay.addView(button);
			}
			addListener(holder, position, convertView);
			return convertView;
		}


		private void setText(TextView view,String text) {
			if(view != null) {
				view.setText(text);
			}
		}

		/**
		 * holerView 添加监听器
		 * @param holder
		 * @param position
		 * @param view
		 */
		private void addListener(final ViewHolder holder, final int position,final View view) {
			if (holder.phoneIV != null) {
				holder.phoneIV.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (StringUtil.isBlank(getItem(position).getRequestUserPhone())) {
							ToastUtil.showMessage("无可用电话号码");
							return;
						}
						Intent phoneIntent=new Intent("android.intent.action.DIAL", Uri.parse("tel:" + getItem(position).getRequestUserPhone()));
						startActivity(phoneIntent);
					}
				});
			}
			if (holder.messageIV != null) {
				holder.messageIV.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (ECApplication.getInstance().getCurrentIMUser().getV3Id().equals(getItem(position).getUserId())) {
							ToastUtil.showMessage("不能与自己聊天");
							return;
						}
						map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
						map.put("v3Id", new ParameterValue(getItem(position).getUserId()));
						new ProgressThreadWrap(getActivity(), new RunnableWrap() {
							@Override
							public void run() {
								try {
									accId = UrlUtil.getAccIdByV3Id(ECApplication.getInstance().getAddress(), map);
									handler.postDelayed(new Runnable() {
										public void run() {
											if (!"error".equals(accId)) {
												UserProfileActivity.start(getActivity(), accId.trim());
//												SessionHelper.startP2PSession(getActivity(), accId.trim());
											} else {
												ToastUtil.showMessage("此用户未开通即时通讯");
											}
										}
									}, 5);
								} catch (Exception e) {
									e.printStackTrace();
									ToastUtil.showMessage("请求失败，请稍后重试");
									handler.postDelayed(new Runnable() {

										@Override
										public void run() {

										}
									}, 5);
								}
							}
						}).start();
					}
				});
			}
		}
		private class ViewHolder {
			private TextView titleTV, checkStatusViewTV, requestTimeTV, faultTV,requestUserNameTV,deviceNameTV
					,faultPlaceTV,requestUserPhoneTV,faultDescriptionTV,costTV;
			private ImageView phoneIV,messageIV;
			private LinearLayout buttonContentLay;
		}
		
	}

	public List<TextView> getOrderButtonList(final String status,final int position,final String costStatus){

		List<TextView> btnList = new ArrayList<TextView>();
		TextView ckBT = getOrderButton("查看");
		ckBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getActivity(), RepairDetailActivity.class)
						.putExtra("id", allDataList.get(position).getId())
						.putExtra("status", status)
						.putExtra("startFlag", startFlag));
			}
		});

		TextView delBT = getOrderButton("删除");
		delBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.question_carmanager_cancel, null, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						repairRecordDelete(position);
					}
				});
				buildAlert.setMessage("确认删除此报修单吗？");
				buildAlert.setTitle("删除");
				buildAlert.show();
			}
		});
		TextView jdBT = getOrderButton("接单");
		jdBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.question_carmanager_cancel, null, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						receiveRequest(position);
					}
				});
				buildAlert.setMessage("接单后其他人不能再接此单，您确认接单吗？");
				buildAlert.setTitle("接单");
				buildAlert.show();
			}
		});
		TextView sendBT = getOrderButton("派单");
		sendBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getActivity(),SendWorkActivity.class).putExtra("repairId",allDataList.get(position).getId()));
			}
		});

		TextView workerFkBT = getOrderButton("维修反馈");
		workerFkBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getActivity(),WorkerFeedBackActivity.class)
						.putExtra("repairId",allDataList.get(position).getId()));
			}
		});

		TextView requestFkBT = getOrderButton("评价");
		requestFkBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getActivity(),RequestFeedBackActivity.class)
						.putExtra("repairId",allDataList.get(position).getId()));
			}
		});

		TextView fyspBT = getOrderButton("费用审批");
		fyspBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//TODO 费用审批
				startActivity(new Intent(getActivity(),CostCheckActivity.class)
						.putExtra("repairId",allDataList.get(position).getId()));
			}
		});

		if (startFlag == RMainActivity.STARTFLAG_MYREQUEST) { /** 我的报修*/

			if(RepairListItem.STATUS_NOT_ACCEPTED.equals(status)) {
				btnList.add(delBT);
			} else if (RepairListItem.STATUS_REPAIRED_OK.equals(status)) { //已修好
				btnList.add(requestFkBT);
			} else if (RepairListItem.STATUS_CANNOT_BE_FIXED.equals(status)) { //不能维修
				btnList.add(requestFkBT);
			}
		} else if(startFlag == RMainActivity.STARTFLAG_ORDERCHECK) { /**报修管理*/

			if(RepairListItem.STATUS_NOT_ACCEPTED.equals(status)) { //未接单
//				btnList.add(jdBT);
				btnList.add(sendBT);
			} else if (RepairListItem.STATUS_REPAIRING.equals(status)) { //维修中
//				btnList.add(workerFkBT);

			} else if (RepairListItem.STATUS_DELAYFIX.equals(status)) { //延迟维修
//				btnList.add(workerFkBT);
			} else if (RepairListItem.STATUS_CANNOT_BE_FIXED.equals(status)) { //不能维修
//				btnList.add(workerFkBT);
			} else if (RepairListItem.STATUS_REPAIRED_OK.equals(status)) { //已修好
//				btnList.add(workerFkBT);
			}

			if(RepairListItem.COST_STATUS_NEED.equals(costStatus)) {
				btnList.add(fyspBT);
			}

		} else if(startFlag == RMainActivity.STARTFLAG_MYTASK) { /**维修工*/

			if(RepairListItem.STATUS_NOT_ACCEPTED.equals(status)) { //未接单

			} else if (RepairListItem.STATUS_REPAIRING.equals(status)) { //维修中
				btnList.add(workerFkBT);
			} else if (RepairListItem.STATUS_DELAYFIX.equals(status)) { //延迟维修
//				btnList.add(workerFkBT);
			} else if (RepairListItem.STATUS_CANNOT_BE_FIXED.equals(status)) { //不能维修
//				btnList.add(workerFkBT);
			} else if (RepairListItem.STATUS_REPAIRED_OK.equals(status)) { //已修好
//				btnList.add(workerFkBT);
			}
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
		button.setGravity(Gravity.CENTER);
		button.setLayoutParams(params);
		return button;
	}
	public void addButtonListener(Button btn) {
		
	}
	
	/** 接单 */
	public void receiveRequest(final int position) {
		mPostingdialog = new ECProgressDialog(getActivity(), "正在接单");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("repairId", new ParameterValue(allDataList.get(position).getId()));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.receiveRequest(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("已接单");
								allDataList.remove(position);
								adapter.notifyDataSetChanged();
								MyRepairManageActivity.mPagerAdapter.notifyDataSetChanged();
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

	/** 删除报修单 */
	public void repairRecordDelete(final int position) {
		mPostingdialog = new ECProgressDialog(getActivity(), "正在删除报修单");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(allDataList.get(position).getId()));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.repairRecordDelete(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("已删除");
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