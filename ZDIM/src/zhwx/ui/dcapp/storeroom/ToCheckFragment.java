package zhwx.ui.dcapp.storeroom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zhwx.common.model.ParameterValue;
import zhwx.common.util.DensityUtil;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;
import zhwx.ui.dcapp.storeroom.model.AuditData;

/**   
 * @Title: RepairDetailFragment.java
 * @Package com.zdhx.edu.im.ui.v3.carmanage
 * @author Li.xin @ 中电和讯
 * @date 2016-3-17 下午4:33:18 
 */
public class ToCheckFragment extends ScrollTabHolderFragment {

	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private String json;

	private View emptyView;
	
	private TextView orderUserTV,telephoneTV,departmentNameTV,userDateTV,arriveTimeTV,
					 addressTV,userCountTV,personListTV,reasonTV,instructionTV,fanchengFlagTV;
	
	public static int SMCHECK_FLAG_BM = 0;
	public static int SMCHECK_FLAG_ZW = 1;
	
	private AuditData auditData;
	
	private int startFlag;
	
	private ListView mystoreLV;
	
	public static Fragment newInstance(int startFlag) {
		ToCheckFragment f = new ToCheckFragment();
		Bundle b = new Bundle();
		b.putInt("startFlag", startFlag);
		f.setArguments(b);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		startFlag = getArguments().getInt("startFlag",-1);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = null;
		if (startFlag == SMCHECK_FLAG_BM) {
			v = inflater.inflate(R.layout.activity_sm_goodskind, null);
		} else {
			v = inflater.inflate(R.layout.activity_sm_goodskind, null);
		}
		emptyView = v.findViewById(R.id.emptyView);
		mystoreLV = (ListView) v.findViewById(R.id.mystoreLV);
		mystoreLV.setEmptyView(emptyView);
		return v;
	}
	@Override
	public void onResume() {
		super.onResume();
		getDetail();
	}
	
	private void getDetail(){
		mPostingdialog = new ECProgressDialog(getActivity(), "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("pageObj.pageSize", new ParameterValue("1000"));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					if (startFlag == SMCHECK_FLAG_BM) { 
						map.put("kind", new ParameterValue("dept"));
						json = UrlUtil.auditListData(ECApplication.getInstance().getV3Address(), map);
					} else {
						map.put("kind", new ParameterValue("zw"));
						json = UrlUtil.auditZWListData(ECApplication.getInstance().getV3Address(), map);
					}
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
		if(json.contains("</html>")){
			ToastUtil.showMessage("数据异常");
			return;
		}
		System.out.println(json);
		auditData = new Gson().fromJson(json, AuditData.class);
		if (startFlag == SMCHECK_FLAG_BM) {
			
		} else {
			
		}
		mystoreLV.setAdapter(new OrderListAdapter());
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
			return auditData.getGridModel().size();
		}

		@Override
		public AuditData.GridModelBean getItem(int position) {
			return auditData.getGridModel().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_sm_myassets, null);
				holder = new ViewHolder();
				holder.smCodeTV = (TextView) convertView.findViewById(R.id.smCodeTV);
				holder.departmentNameTV = (TextView) convertView.findViewById(R.id.departmentNameTV);
				holder.smApplyDateTV = (TextView) convertView.findViewById(R.id.smApplyDateTV);
				holder.getKindTV = (TextView) convertView.findViewById(R.id.getKindTV);
				holder.checkStatusViewTV = (TextView) convertView.findViewById(R.id.checkStatusViewTV);
				holder.sendStatusViewTV = (TextView) convertView.findViewById(R.id.sendStatusViewTV);
				holder.buttonContentLay = (LinearLayout) convertView.findViewById(R.id.buttonContentLay);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.smCodeTV.setText(getItem(position).getCode());
			holder.departmentNameTV.setText(getItem(position).getDepartmentName());
			holder.smApplyDateTV.setText(getItem(position).getDeptDate());
			holder.getKindTV.setText(getItem(position).getKindValue());
			
			if (startFlag == SMCHECK_FLAG_BM) { 
				holder.checkStatusViewTV.setText(getItem(position).getDeptCheckStatusValue());
			} else {
				holder.checkStatusViewTV.setText(getItem(position).getZwCheckStatusValue());
			}
			
			holder.sendStatusViewTV.setText(getItem(position).getProvideStatusValue());
			
			//动态添加操作按钮
			holder.buttonContentLay.removeAllViews();
			List<TextView> btns = getOrderButtonList(position);
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
			private TextView smCodeTV,departmentNameTV,smApplyDateTV,getKindTV,sendStatusViewTV,checkStatusViewTV;
			private LinearLayout buttonContentLay;
		}
		
	}
	
	public List<TextView> getOrderButtonList(final int position){
		
		List<TextView> btnList = new ArrayList<TextView>();
		TextView ckBT = getOrderButton("查看");
		ckBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(getActivity(), ApplyDetailActivity.class).putExtra("id", auditData.getGridModel().get(position).getId()));
			}
		});

		TextView shBT = getOrderButton("审核");
		shBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(getActivity(), CheckActivity.class)
				.putExtra("orderId", auditData.getGridModel().get(position).getId())
				.putExtra("kind", startFlag), 103);
			}
		});
		btnList.add(ckBT);
		if (startFlag == SMCHECK_FLAG_BM) { 
			if ("部门未审核".equals(auditData.getGridModel().get(position).getDeptCheckStatusValue())) {
				btnList.add(shBT);
			}
		} else {
			if ("未发放".equals(auditData.getGridModel().get(position).getProvideStatusValue())) {
				btnList.add(shBT);
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
		button.setGravity(Gravity.CENTER);
		button.setLayoutParams(params);
		return button;
	}
	
	/** 取消订车单 */
	public void cancelOrderCar() {
		mPostingdialog = new ECProgressDialog(getActivity(), "正在取消订车单");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.cancelOrderCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("订单已取消");
								getActivity().finish();
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
