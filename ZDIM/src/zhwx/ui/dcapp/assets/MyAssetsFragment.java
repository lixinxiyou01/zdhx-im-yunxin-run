package zhwx.ui.dcapp.assets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

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
import zhwx.common.view.refreshlayout.PullableListView;
import zhwx.ui.dcapp.assets.model.CheckListItem;
import zhwx.ui.dcapp.assets.model.MyAssets;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;


public class MyAssetsFragment extends ScrollTabHolderFragment {

	private static final String ARG_POSITION = "position";

	private PullableListView mListView;
	
	private int mPosition;

	private HashMap<String, ParameterValue> map;
		
	private  List<MyAssets.MyAssetListBean> allDataList;

	private OrderListAdapter adapter;
	
	private ECProgressDialog mPostingdialog;
	
	private Handler handler = new Handler();
	
	private MyAssets assets;
	
	public static Fragment newInstance(int position,MyAssets assets) {
		MyAssetsFragment f = new MyAssetsFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		b.putSerializable("assets", assets);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mPosition = getArguments().getInt(ARG_POSITION);
		assets = (MyAssets) getArguments().getSerializable("assets");
		allDataList = assets.getMyAssetList();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_ordercar_list, null);
		mListView = (PullableListView) v.findViewById(R.id.content_view);
		mListView.setEmptyView(v.findViewById(R.id.emptyView));
		mListView.enableAutoLoad(false);
		mListView.setLoadmoreVisible(false);
		adapter = new OrderListAdapter();
		mListView.setAdapter(adapter);
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
		public MyAssets.MyAssetListBean getItem(int position) {
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
				
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_as_myassets, null);
				holder = new ViewHolder();
				holder.asNameTV = (TextView) convertView.findViewById(R.id.asNameTV);
				holder.asTypeTV = (TextView) convertView.findViewById(R.id.asTypeTV);
				holder.asKindTV = (TextView) convertView.findViewById(R.id.asKindTV);
				holder.codeTV = (TextView) convertView.findViewById(R.id.codeTV);
				holder.getDateTV = (TextView) convertView.findViewById(R.id.getDateTV);
				holder.checkStatusViewTV = (TextView) convertView.findViewById(R.id.checkStatusViewTV);
				holder.buttonContentLay = (LinearLayout) convertView.findViewById(R.id.buttonContentLay);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.asNameTV.setText(getItem(position).getName());
			holder.asTypeTV.setText(getItem(position).getPatternName());
			holder.asKindTV.setText(getItem(position).getAssetKindName());
			holder.codeTV.setText(getItem(position).getCode());
			holder.getDateTV.setText(getItem(position).getRegistrationDate());
			holder.checkStatusViewTV.setVisibility(View.INVISIBLE);
			
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
			private TextView asNameTV,asTypeTV,asKindTV,codeTV,getDateTV,checkStatusViewTV;
			private LinearLayout buttonContentLay;
		}
		
	}
	
	public List<TextView> getOrderButtonList(final int position){
		List<TextView> btnList = new ArrayList<TextView>();
		TextView ckBT = getOrderButton("查看");
		ckBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), AssetDetailActivity.class);
				intent.putExtra("assetsCode", allDataList.get(position).getCode());
				startActivity(intent);
			}
		});
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