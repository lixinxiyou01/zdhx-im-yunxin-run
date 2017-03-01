package zhwx.ui.dcapp.carmanage;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
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
import zhwx.ui.dcapp.carmanage.model.OrderCarListItem;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;

public class OrderListFragment extends ScrollTabHolderFragment {

	private static final String ARG_POSITION = "position";

	private PullableListView mListView;
	
	private int mPosition;

	private String tabName;
	
	private String status;
	
	/** 启动分类 */
	private int startFlag = -1;
	
	private RequestWithCacheGet mRequestWithCache;
	
	private HashMap<String, ParameterValue> map;
	
	private  List<OrderCarListItem> allDataList = new ArrayList<OrderCarListItem>();

	private List<OrderCarListItem> newDataList = new ArrayList<OrderCarListItem>();
	
	private OrderListAdapter adapter;
	
	private String json;
	
	private int pageNum = 1;
	
	private ECProgressDialog mPostingdialog;
	
	private Handler handler = new Handler();
	
	public static Fragment newInstance(int position,String tabName,String status,int startFlag) {
		OrderListFragment f = new OrderListFragment();
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
		System.out.println("onCreate");
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
				getOrderCaarListByStatus(status, pageNum);
			}
		});
		getOrderCaarListByStatus(status, pageNum);
		System.out.println("onCreateView");
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
	
	
	private void getOrderCaarListByStatus(String status,int pageNum){
		mPostingdialog = new ECProgressDialog(getActivity(), "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("status", new ParameterValue(status));
		map.put("pageNum", new ParameterValue(pageNum + ""));
		
		String url = "";
		if (CMainActivity.STARTFLAG_MYORDERCAR == startFlag) {
			url = UrlUtil.getMyOrderCarList(ECApplication.getInstance().getV3Address(),map);
		} else if (CMainActivity.STARTFLAG_MYTASK == startFlag){
			url = UrlUtil.getMyAssigncarList(ECApplication.getInstance().getV3Address(),map);
		} else if (CMainActivity.STARTFLAG_ORDERCHECK == startFlag){
			map.put("operationCode", new ParameterValue("carmanage"));
			url = UrlUtil.getCarManageList(ECApplication.getInstance().getV3Address(),map);
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
//		if ((json != null && !json.equals(RequestWithCache.NO_DATA))) {
//			Log.i("缓存数据:" + json);
//			refreshData(json);
//		}
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
		newDataList = gson.fromJson(json2, new TypeToken<List<OrderCarListItem>>() {}.getType());
		
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
		
		public OrderListAdapter(Context context, List<OrderCarListItem> list,
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
		public OrderCarListItem getItem(int position) {
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
				
				if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_ordercar_dirver, null);
				} else {
					convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_ordercar, null);
				}
				holder = new ViewHolder();
				holder.carUserNameTV = (TextView) convertView.findViewById(R.id.carUserNameTV);
				holder.telephoneTV = (TextView) convertView.findViewById(R.id.telephoneTV);
				holder.arriveTimeTV = (TextView) convertView.findViewById(R.id.arriveTimeTV);
				holder.addressTV = (TextView) convertView.findViewById(R.id.addressTV);
				holder.orderTimeTV = (TextView) convertView.findViewById(R.id.orderTimeTV);
				holder.buttonContentLay = (LinearLayout) convertView.findViewById(R.id.buttonContentLay);
				holder.checkStatusViewTV = (TextView) convertView.findViewById(R.id.checkStatusViewTV);
				
				if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
					holder.leaveTimeTV = (TextView) convertView.findViewById(R.id.leaveTimeTV);
					holder.leaveDateTV = (TextView) convertView.findViewById(R.id.leaveDateTV);
					holder.useAddressTV = (TextView) convertView.findViewById(R.id.useAddressTV);
					holder.carNameTV = (TextView) convertView.findViewById(R.id.carNameTV);
					holder.carNumTV = (TextView) convertView.findViewById(R.id.carNumTV);
				} else {
					holder.userDateTV = (TextView) convertView.findViewById(R.id.userDateTV);
					holder.departmentNameTV = (TextView) convertView.findViewById(R.id.departmentNameTV);
				}
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.carUserNameTV.setText(getItem(position).getCarUserName());
			holder.telephoneTV.setText(getItem(position).getTelephone());
			holder.arriveTimeTV.setText(getItem(position).getArriveTime());
			holder.addressTV.setText(getItem(position).getAddress());
			holder.orderTimeTV.setText(getItem(position).getOrderTime());
			holder.checkStatusViewTV.setText(getItem(position).getCheckStatusView());
			
			if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
				holder.leaveDateTV.setText(getItem(position).getLeaveDate());
				holder.leaveTimeTV.setText(getItem(position).getLeaveTime());
				holder.useAddressTV .setText(getItem(position).getUseAddress());
				holder.carNameTV.setText(getItem(position).getCarName());
				holder.carNumTV.setText(getItem(position).getCarNum());
			} else {
				holder.userDateTV.setText(getItem(position).getUserDate());
				holder.departmentNameTV.setText(getItem(position).getDepartmentName());
			}
			
			//动态添加操作按钮
			holder.buttonContentLay.removeAllViews();
			List<TextView> btns = getOrderButtonList(getItem(position).getCheckStatus(),getItem(position).getEvaluateFlag(),position);
			for (TextView button : btns) {
				holder.buttonContentLay.addView(button);
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
			private TextView orderTimeTV, checkStatusViewTV, userDateTV, arriveTimeTV,
							 addressTV,carUserNameTV,departmentNameTV,telephoneTV,
							 leaveTimeTV,leaveDateTV,useAddressTV,carNameTV,carNumTV;
			private LinearLayout buttonContentLay;
		}
		
	}
	
	public List<TextView> getOrderButtonList(final String status,final String evaluateFlag,final int position){
		List<TextView> btnList = new ArrayList<TextView>();
		TextView ckBT = getOrderButton("查看订车单");
		ckBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
					startActivity(new Intent(getActivity(), OrderDetailActivity.class)
					.putExtra("id", allDataList.get(position).getAssignCarId())
					.putExtra("status", status)
					.putExtra("startFlag", startFlag));
				} else {
					startActivity(new Intent(getActivity(), OrderDetailActivity.class)
					.putExtra("id", allDataList.get(position).getOrderCarId())
					.putExtra("status", status)
					.putExtra("evaluateFlag", evaluateFlag)
					.putExtra("startFlag", startFlag));
				}
			}
		});
		
		if (startFlag == CMainActivity.STARTFLAG_MYORDERCAR) {
			TextView pjBT = getOrderButton("评价");
			pjBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO 订车人反馈
					startActivityForResult(new Intent(getActivity(), EvaluateActivity.class).putExtra("orderId", allDataList.get(position).getOrderCarId()), 103);
				}
			});
			TextView qxBT = getOrderButton("取消订车单");
			qxBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.question_carmanager_cancel, null, new DialogInterface.OnClickListener() {
						 @Override
			                public void onClick(DialogInterface dialog, int which) {
							 	cancelOrderCar(position);
			                }
			         });
			        buildAlert.setTitle("取消订车单");
			        buildAlert.show();	
				}
			});
			
			if (status.equals(OrderCarListItem.CHECKSTATUS_FINISH)) {  //待评价
				if ("0".equals(evaluateFlag)) {
					btnList.add(pjBT);
				}
				btnList.add(ckBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_CANCEL)) { //已取消
				btnList.add(ckBT);
			}  else if (status.equals(OrderCarListItem.CHECKSTATUS_ASSIGNING)) { //派车中
				btnList.add(ckBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_PASS)) { //已派车
				btnList.add(ckBT);
			}else {   
				btnList.add(qxBT);
				btnList.add(ckBT);
			}
		} else if(startFlag == CMainActivity.STARTFLAG_ORDERCHECK) {

			TextView shBT = getOrderButton("审核");
			shBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					//TODO
					startActivityForResult(new Intent(getActivity(), CheckActivity.class).putExtra("orderId", allDataList.get(position).getOrderCarId()), 103);
				}
			});
			TextView pcBT = getOrderButton("派车");
			pcBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					startActivityForResult(new Intent(getActivity(), CarListActivity.class).putExtra("orderId", allDataList.get(position).getOrderCarId()), 103);
				}
			});
			TextView jxpcBT = getOrderButton("继续派车");
			jxpcBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					getActivity().startActivityForResult(new Intent(getActivity(), CarListActivity.class).putExtra("orderId", allDataList.get(position).getOrderCarId()), 103);
				}
			});
			TextView jspcBT = getOrderButton("结束派车");
			jspcBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.question_carmanager_finish, null, new DialogInterface.OnClickListener() {
						 @Override
			                public void onClick(DialogInterface dialog, int which) {
							 passOrderCar(position);
			                }
			         });
			        buildAlert.setTitle("已结束派车");
			        buildAlert.show();	
				}
			});
			TextView qxBT = getOrderButton("取消订车单");
			qxBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.question_carmanager_cancel, null, new DialogInterface.OnClickListener() {
						 @Override
			                public void onClick(DialogInterface dialog, int which) {
							 	cancelOrderCar(position);
			                }
			         });
			        buildAlert.setTitle("取消订车单");
			        buildAlert.show();	
				}
			});
			TextView byBT = getOrderButton("不予派车");
			byBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.question_carmanager_cancel, null, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							unPassOrderCar(position);
						}
					});
					buildAlert.setMessage("确定不予派车吗？");
					buildAlert.setTitle("不予派车");
					buildAlert.show();	
				}
			});
			if (status.equals(OrderCarListItem.CHECKSTATUS_CHECK)) {   //审核
				btnList.add(shBT);
				btnList.add(ckBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_CHECK_UNPASS)) {  //审核未通过
				btnList.add(shBT);
				btnList.add(ckBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_DRAFT)) {  //待派车
				btnList.add(pcBT);
				btnList.add(qxBT);
				btnList.add(byBT);
				btnList.add(ckBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_ASSIGNING)) { //派车中
				btnList.add(jxpcBT);
				btnList.add(jspcBT);
				btnList.add(qxBT);
				btnList.add(ckBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_PASS)) { //已派车
				btnList.add(qxBT);
				btnList.add(ckBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_FINISH)) { //已完成
				btnList.add(ckBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_CANCEL)) { //已取消
				btnList.add(ckBT);
			}else {
				btnList.add(ckBT);
			}
		} else if(startFlag == CMainActivity.STARTFLAG_MYTASK) {

			TextView pjBT = getOrderButton("反馈");
			pjBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					startActivityForResult(new Intent(getActivity(), DirverFeedBackActivity.class).putExtra("orderId", allDataList.get(position).getAssignCarId()),103);
				}
			});
			TextView qrBT = getOrderButton("确认出车");
			qrBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					driverEnsureBus(position);
				}
			});
			TextView jsBT = getOrderButton("结束任务");
			jsBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					driverEndTask(position);
				}
			});
			
			if (status.equals(OrderCarListItem.CHECKSTATUS_DQR)) {  //待确认
				btnList.add(qrBT);
				btnList.add(ckBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_DJS)) { //待结束
				btnList.add(jsBT);
				btnList.add(ckBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_DPJ)) {  //待评价
				btnList.add(pjBT);
				btnList.add(ckBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_CANCEL)) { //已取消
				btnList.add(ckBT);
			} else {
				btnList.add(ckBT);
			}
		}
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
		if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
			orderId = allDataList.get(position).getAssignCarId();
		} else {
			orderId = allDataList.get(position).getOrderCarId();
		}
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
	
	/** 不予通过 */
	public void unPassOrderCar(final int position) {
		String orderId = "";
		if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
			orderId = allDataList.get(position).getAssignCarId();
		} else {
			orderId = allDataList.get(position).getOrderCarId();
		}
		mPostingdialog = new ECProgressDialog(getActivity(), "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(orderId));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.unPassOrderCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("操作成功");
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
	
	/** 结束派车 */
	public void passOrderCar(final int position) {
		String orderId = "";
		if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
			orderId = allDataList.get(position).getAssignCarId();
		} else {
			orderId = allDataList.get(position).getOrderCarId();
		}
		mPostingdialog = new ECProgressDialog(getActivity(), "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(orderId));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.passOrderCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("已结束派车");
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
	
	/** 删除订车单 */
	public void delOrderCar(final int position) {
		String orderId = "";
		if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
			orderId = allDataList.get(position).getAssignCarId();
		} else {
			orderId = allDataList.get(position).getOrderCarId();
		}
		mPostingdialog = new ECProgressDialog(getActivity(), "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(orderId));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.delOrderCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("订车单已删除");
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
	
	/** 确认接单 */
	public void driverEnsureBus(final int position) {
		String orderId = "";
		if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
			orderId = allDataList.get(position).getAssignCarId();
		} else {
			orderId = allDataList.get(position).getOrderCarId();
		}
		mPostingdialog = new ECProgressDialog(getActivity(), "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("assignCarId", new ParameterValue(orderId));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.driverEnsureBus(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("已接单");
								allDataList.remove(position);
								adapter.notifyDataSetChanged();
								OrderManageActivity.mPagerAdapter.notifyDataSetChanged();
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
	
	/** 结束任务 */
	public void driverEndTask(final int position) {
		String orderId = "";
		if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
			orderId = allDataList.get(position).getAssignCarId();
		} else {
			orderId = allDataList.get(position).getOrderCarId();
		}
		mPostingdialog = new ECProgressDialog(getActivity(), "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("assignCarId", new ParameterValue(orderId));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.driverEndTask(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("已结束任务，请反馈");
//								allDataList.remove(position);
//								adapter.notifyDataSetChanged();
//								OrderManageActivity.mPagerAdapter.notifyDataSetChanged();
								startActivityForResult(new Intent(getActivity(), DirverFeedBackActivity.class).putExtra("orderId", allDataList.get(position).getAssignCarId()),103);
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