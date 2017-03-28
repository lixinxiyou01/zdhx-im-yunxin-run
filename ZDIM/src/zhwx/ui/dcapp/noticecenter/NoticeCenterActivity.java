package zhwx.ui.dcapp.noticecenter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import volley.Response;
import volley.VolleyError;
import zhwx.Constant;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.Apps;
import zhwx.common.model.ParameterValue;
import zhwx.common.model.V3NoticeCenter;
import zhwx.common.util.IntentUtil;
import zhwx.common.util.Log;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.refreshlayout.PullableListView;
import zhwx.common.view.waveview.WaveSwipeRefreshLayout;


public class NoticeCenterActivity extends BaseActivity implements OnClickListener{

	private Activity context;

	public int UNREAD = 1; // 未读
	
	public int ALL = 2; // 全部
	
	public int MARK = 3; // 被标记
	
	public int CURRENT = 0; 
	
	private PullableListView listView;
	
	private TextView emptyView;

	private WaveSwipeRefreshLayout layout;

	private PopupWindow mPopHeader, mPopFooter, changeTypePop;

	private View headerView, footerView, changeTypeView;

	private HashMap<String, ParameterValue> map;
	
	private HashMap<String, ParameterValue> noticeMap;

	private List<V3NoticeCenter> allDataList = new ArrayList<V3NoticeCenter>();

	private List<V3NoticeCenter> newDataList = new ArrayList<V3NoticeCenter>();

	private RequestWithCacheGet mRequestWithCache;

	private MessageListAdapter adapter;

	private Handler handler = new Handler();

	private int pageNo = 1;

	private String noticeJson;

	private FrameLayout top_bar;

	private long mPressedTime = 0;

	private RelativeLayout mainLay;

	/** 判断是否是编辑状态 */
	private boolean isLongState = false;

	private Button editBT, deleteAllBT,allMarkBT, cancelBT;

	private TextView seleteCountTV, noticeTypeTV, allMessageTV, unReadTV,
			markMessageTV, unReadCountTV;

	private ImageView flagIV, allMessageIV, unReadIV, markMessageIV;

	private LinearLayout changeTypeLay;

	private RelativeLayout unReadLay, allMessageLay, markMessageLay;

	private CheckBox seleteAllCB;

	private String delFlag = "";
	
	private String markFlag = "";
	
	private String messageCount = "";

	private HashMap<Integer, Boolean> checkedItemMap = new HashMap<Integer, Boolean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;

		getTopBarView().setBackGroundColor(R.color.main_bg);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, R.drawable.btn_mark_read,"消息中心", this);
	
		mRequestWithCache = new RequestWithCacheGet(context);
		mainLay = (RelativeLayout) findViewById(R.id.mainLay);
		listView = (PullableListView) findViewById(R.id.content_view);
		layout = (WaveSwipeRefreshLayout) findViewById(R.id.refresh_view);
		layout.setColorSchemeColors(Color.WHITE, Color.WHITE);
		layout.setWaveColor(Color.parseColor("#18ab8e"));
		layout.setMaxDropHeight(400);
		layout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				pageNo = 1;
				listView.setLoadmoreVisible(true);
				getData(pageNo);
				new Handler() {
					@Override
					public void handleMessage(Message msg) {
						layout.setRefreshing(false);
					}
				}.sendEmptyMessageDelayed(0, 1000);
			}
		},context);
		
		listView.setOnLoadListener(new PullableListView.OnLoadListener() {

			@Override
			public void onLoad(PullableListView pullableListView) {
				if (pageNo != 1 && (StringUtil.isBlank(noticeJson)||"[]".equals(noticeJson))) {
					listView.finishLoading();
					listView.setLoadmoreVisible(false);
					
					return;
				}
				pageNo++;
				getData(pageNo);
			}
		});
		
		emptyView = (TextView) findViewById(R.id.emptyView);
		
		listView.setEmptyView(emptyView);
	
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				if (isLongState) {
					putCheckedItemMap(position);
					if(adapter!=null){
						adapter.notifyDataSetChanged();
					}
					
				} else {
					V3NoticeCenter notice = (V3NoticeCenter) parent.getAdapter().getItem(position);
					Intent intent = IntentUtil.getStartAppIntent(context, notice); //获取APP对应的Intent
					if (intent != null) {

						startActivity(intent);
					} else {
						ToastUtil.showMessage("无详情");
					}
					
					setMessageRead(notice.getId(),position);  //置已读
					
					if (notice.getKind().equals(V3NoticeCenter.NOTICE_KIND_NOTICE)) {
						setNoticeRead(notice.getSourceId());  //相应通知置已读
					}
				}
			}
		}); 
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				isLongState = true;
				showEditPop();
				putCheckedItemMap(position);
				if(adapter!=null){
					adapter.notifyDataSetChanged();
				}
				return true;
			}
		});
		
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		top_bar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doubleClick();
			}
		});
	
		editBT = (Button) findViewById(R.id.editBT);
		editBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!isLongState) {
					isLongState = true;
					showEditPop();
					if(adapter != null){
						adapter.notifyDataSetChanged();
					}
				}
			}
		});

		flagIV = (ImageView) findViewById(R.id.flagIV);
		noticeTypeTV = (TextView) findViewById(R.id.noticeTypeTV);
		changeTypeLay = (LinearLayout) findViewById(R.id.changeTypeLay);
		changeTypeLay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				showChanagePop();
			}
		});
		setImmerseLayout(top_bar);
		initPopMenu();
		CURRENT = ALL;
		getData(pageNo);
	}

	private void getData(final int pageNo) {
		if(pageNo==1){
			listView.setLoadmoreVisible(true);
		}
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		map.put("pageNum", new ParameterValue(pageNo + ""));
		String url = "";
		if (CURRENT == ALL) {
			url = UrlUtil.getAllMessageList(ECApplication.getInstance().getAddress(),map);
		} else if (CURRENT == UNREAD) {
			url = UrlUtil.getUnReadMessageList(
					ECApplication.getInstance().getV3Address(), map);
		} else if (CURRENT == MARK) {
			url = UrlUtil.getMyattentionList(ECApplication.getInstance().getV3Address(),map);
		}
		try {
			noticeJson = mRequestWithCache.getRseponse(url,
					new RequestWithCacheGet.RequestListener() {

						@Override
						public void onResponse(String response) {
							if (response != null
									&& !response
											.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
								Log.i("新数据:" + response);
								refreshData(response,pageNo);
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							 
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

		if ((noticeJson != null && !noticeJson.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:" + noticeJson);
			refreshData(noticeJson,pageNo);
		}
	}

	private void refreshData(String json,int pageNo) {
		if(json.contains("<html>")){
			ToastUtil.showMessage("数据异常");
			return;
		}
		if (StringUtil.isBlank(json)||"[]".equals(json)) {
			listView.finishLoading();
			listView.setLoadmoreVisible(false);
		}
		if (pageNo == 1) {
			allDataList.clear();
		} else {
			listView.finishLoading();
		}
		Gson gson = new Gson();
		newDataList = gson.fromJson(json, new TypeToken<List<V3NoticeCenter>>() {
		}.getType());
		if (newDataList != null && newDataList.size() != 0) {
			allDataList.addAll(newDataList);
		} else {
			return;
		}
		if (pageNo == 1) {
			if(allDataList.size() < 20) {
				listView.finishLoading();
				listView.setLoadmoreVisible(false);
			}
			adapter = new MessageListAdapter();
			listView.setAdapter(adapter);
		} else {
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	public class MessageListAdapter extends BaseAdapter {
		public static final int UNREAD = 1; // 未读
		public static final int READ = 2; // 已读(已读)
		public static final int DELETE = 3; // 已删除(废纸篓)
		public static final int TASK = 4; // 提醒

		public MessageListAdapter(Context context, List<Message> list,
				int listFlag) {
			super();
		}

		public MessageListAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return allDataList.size();
		}

		@Override
		public Object getItem(int position) {
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
				convertView = LayoutInflater.from(context).inflate(R.layout.package_row, null);
				holder = new ViewHolder();
				holder.kindNameTV = (TextView) convertView.findViewById(R.id.kindNameTV);
				holder.timeTV = (TextView) convertView.findViewById(R.id.timeTV);
				holder.messageContentTV = (TextView) convertView.findViewById(R.id.messageContentTV);
				holder.app_headIV = (ImageView) convertView.findViewById(R.id.app_headIV);
				holder.isReadIV = (ImageView) convertView.findViewById(R.id.isReadIV);
				holder.mainLay = (LinearLayout) convertView.findViewById(R.id.mainLay);
				holder.frontLay = (RelativeLayout) convertView.findViewById(R.id.frontLay);
				holder.goWatchLay = (RelativeLayout) convertView.findViewById(R.id.goWatchLay);
				holder.selectFlagCB = (CheckBox) convertView.findViewById(R.id.selectFlagCB);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.kindNameTV.setText(Apps.getApp(allDataList.get(position).getKind()).getName());
			holder.timeTV.setText(allDataList.get(position).getTime());
			holder.messageContentTV.setText(allDataList.get(position)
					.getTitle());
			
			if(Constant.NOTICE_READ_YES.equals(allDataList.get(position).getReadFlag())){
				holder.isReadIV.setVisibility(View.GONE);
			}else{
				holder.isReadIV.setVisibility(View.VISIBLE);
			}
			
			if (isLongState) {
				holder.selectFlagCB.setVisibility(View.VISIBLE);
				if (checkedItemMap.containsKey(position)) {
					holder.selectFlagCB.setChecked(true);
					holder.frontLay.setSelected(true);
					holder.goWatchLay.setSelected(true);
				} else {
					holder.selectFlagCB.setChecked(false);
					holder.frontLay.setSelected(false);
					holder.goWatchLay.setSelected(false);
				}
			} else {
				holder.frontLay.setSelected(false);
				holder.goWatchLay.setSelected(false);
				holder.selectFlagCB.setVisibility(View.GONE);
			}

			// TODO 应用图标
			holder.app_headIV.setImageResource(Apps.getApp(allDataList.get(position).getKind()).getIcon());
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
//			holder.app_headIV.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					
//				}
//			});
//			holder.frontLay.setOnLongClickListener(new OnLongClickListener() {
//				
//				@Override
//				public boolean onLongClick(View arg0) {
//					return false;
//				}
//			});
		}

		private class ViewHolder {
			private TextView kindNameTV, timeTV, messageContentTV;
			private ImageView app_headIV, isReadIV;
			private LinearLayout mainLay;
			private RelativeLayout frontLay,goWatchLay;
			private CheckBox selectFlagCB;
		}
	}

	public void doubleClick() {
		long mNowTime = System.currentTimeMillis();// 获取第一次按键时间
		if ((mNowTime - mPressedTime) > 1000) {// 比较两次按键时间差
			mPressedTime = mNowTime;
		} else {
			listView.setSelection(0);
			pageNo = 1;
			getData(pageNo);
		}
	}

	/**
	 * @Function:收集选中listview item 状态
	 * */
	private void putCheckedItemMap(int key) {
		if (checkedItemMap.containsKey(key)) {
			boolean value = checkedItemMap.get(key);
			if (value) {
				checkedItemMap.remove(key);
			}
		} else {
			checkedItemMap.put(key, true);
		}
		seleteCountTV.setText(checkedItemMap.size() + "");
		
		List<Integer> positions = new ArrayList<Integer>();
		Set<Entry<Integer, Boolean>> entrySet = checkedItemMap.entrySet();
		for (Entry<Integer, Boolean> entry : entrySet) {
			int position = entry.getKey();
			positions.add(position);
		}
		boolean isMark = false;
		for (int i = 0; i < positions.size(); i++) {
			if (Constant.NOTICE_MARK_NO.equals(allDataList.get(positions.get(i)).getAttentionFlag())) {
				isMark = true;
				break;
			}
		}
		if (isMark) {
			allMarkBT.setText("关注");
		}else{
			allMarkBT.setText("取消关注");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isLongState) {
				isLongState = false;
				checkedItemMap.clear();
				if (mPopHeader != null && mPopHeader.isShowing()) {
					mPopHeader.dismiss();
				}
				if (mPopFooter != null && mPopFooter.isShowing()) {
					mPopFooter.dismiss();
				}
				if(adapter!=null){
					adapter.notifyDataSetChanged();
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 初始化popupWindow
	 */
	private void initPopMenu() {
		// 顶部pop
		headerView = context.getLayoutInflater().inflate(
				R.layout.noticecenter_list_edit_header, null);
		if (mPopHeader == null) {
			mPopHeader = new PopupWindow(headerView, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, true);
		}
		if (mPopHeader.isShowing()) {
			mPopHeader.dismiss();
		}
		mPopHeader.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				if (isLongState) {
					showEditPop();
				} else {
					seleteCountTV.setText("0");
					seleteAllCB.setChecked(false);
				}
			
			}
		});
		seleteCountTV = (TextView) headerView.findViewById(R.id.seleteCountTV);
		seleteAllCB = (CheckBox) headerView.findViewById(R.id.seleteAllCB);
		seleteAllCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				// 全选
				checkedItemMap.clear();
				if (isChecked) {
					for (int i = 0; i < allDataList.size(); i++) {
						checkedItemMap.put(i, true);
					}
				} else {
					checkedItemMap.clear();
				}
				seleteCountTV.setText(checkedItemMap.size() + "");
				
				List<Integer> positions = new ArrayList<Integer>();
				Set<Entry<Integer, Boolean>> entrySet = checkedItemMap.entrySet();
				for (Entry<Integer, Boolean> entry : entrySet) {
					int position = entry.getKey();
					positions.add(position);
				}
				boolean isMark = false;
				for (int i = 0; i < positions.size(); i++) {
					if (Constant.NOTICE_MARK_NO.equals(allDataList.get(positions.get(i)).getAttentionFlag())) {
						isMark = true;
						break;
					}
				}
				if (isMark) {
					allMarkBT.setText("关注");
				}else{
					allMarkBT.setText("取消关注");
				}	
				
				if(adapter!=null){
					adapter.notifyDataSetChanged();
				}
			}
		});
		cancelBT = (Button) headerView.findViewById(R.id.cancelBT);
		cancelBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isLongState) {
					isLongState = false;
					checkedItemMap.clear();
					if (mPopHeader != null && mPopHeader.isShowing()) {
						mPopHeader.dismiss();
					}
					if (mPopFooter != null && mPopFooter.isShowing()) {
						mPopFooter.dismiss();
					}
					if(adapter!=null){
						adapter.notifyDataSetChanged();
					}
				}
			}
		});

		// 底部pop
		footerView = context.getLayoutInflater().inflate(
				R.layout.noticecenter_list_edit_footer, null);
		if (mPopFooter == null) {
			mPopFooter = new PopupWindow(footerView, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, true);
		}
		if (mPopFooter.isShowing()) {
			mPopFooter.dismiss();
		}
		//批量删除
		deleteAllBT = (Button) footerView.findViewById(R.id.allDeleteBT);
		deleteAllBT.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				ECAlertDialog buildAlert = ECAlertDialog.buildAlert(context, R.string.delete_all_notice1, null, new DialogInterface.OnClickListener() {
					 @Override
		                public void onClick(DialogInterface dialog, int which) {
						 	delSeletedNotice();
		                }
		            });
		            buildAlert.setTitle("删除");
		            buildAlert.show();  
			}
		});
		//批量关注
		allMarkBT = (Button) footerView.findViewById(R.id.allMarkBT);
		allMarkBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				 markSeletedNotice();
			}
		});
		// 切换pop
		changeTypeView = footerView = context.getLayoutInflater().inflate(
				R.layout.pop_chosemessagegroup, null);
		if (changeTypePop == null) {
			changeTypePop = new PopupWindow(changeTypeView,
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		}
		if (changeTypePop.isShowing()) {
			changeTypePop.dismiss();
		}
		changeTypePop.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				flagIV.setImageResource(R.drawable.iv_change);
			}
		});
		allMessageTV = (TextView) changeTypeView
				.findViewById(R.id.allMessageTV);
		unReadTV = (TextView) changeTypeView.findViewById(R.id.unReadTV);
		markMessageTV = (TextView) changeTypeView
				.findViewById(R.id.markMessageTV);
		unReadCountTV = (TextView) changeTypeView
				.findViewById(R.id.unReadCountTV);
		allMessageIV = (ImageView) changeTypeView
				.findViewById(R.id.allMessageIV);
		unReadIV = (ImageView) changeTypeView.findViewById(R.id.unReadIV);
		markMessageIV = (ImageView) changeTypeView
				.findViewById(R.id.markMessageIV);
		unReadLay = (RelativeLayout) changeTypeView
				.findViewById(R.id.unReadLay);
		unReadLay.setOnTouchListener(new OnTouchListener() {// 未读
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							unReadLay.setBackgroundResource(R.color.bg_pressed);
							unReadIV.setImageResource(R.drawable.icon_message_unuse_pressed);
							unReadCountTV.setTextColor(Color.BLACK);
							unReadTV.setTextColor(Color.BLACK);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							// listView.setOffsetLeft(getDeviceWidth()/2);
							noticeTypeTV.setText("未读");
							unReadLay.setBackgroundResource(R.color.main_bg);
							unReadIV.setImageResource(R.drawable.icon_message_unuse_normal);
							unReadTV.setTextColor(Color.WHITE);
							unReadCountTV.setTextColor(Color.WHITE);
							changeTypePop.dismiss();
							CURRENT = UNREAD;
							pageNo = 1;
							getData(pageNo);
						}
						return true;
					}
				});

		allMessageLay = (RelativeLayout) changeTypeView
				.findViewById(R.id.allMessageLay);
		allMessageLay.setOnTouchListener(new OnTouchListener() {// 全部

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							allMessageLay
									.setBackgroundResource(R.color.bg_pressed);
							allMessageIV
									.setImageResource(R.drawable.icon_message_pressed);
							allMessageTV.setTextColor(Color.BLACK);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							// listView.setOffsetLeft(getDeviceWidth()/2);
							noticeTypeTV.setText("收件箱");
							allMessageLay
									.setBackgroundResource(R.color.main_bg);
							allMessageIV
									.setImageResource(R.drawable.icon_message_normal);
							allMessageTV.setTextColor(Color.WHITE);
							changeTypePop.dismiss();
							CURRENT = ALL;
							pageNo = 1;
							getData(pageNo);
						}
						return true;
					}
				});
		markMessageLay = (RelativeLayout) changeTypeView.findViewById(R.id.markMessageLay);
		markMessageLay.setOnTouchListener(new OnTouchListener() {// 关注

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							markMessageLay
									.setBackgroundResource(R.color.bg_pressed);
							markMessageIV
									.setImageResource(R.drawable.icon_message_use_pressed);
							markMessageTV.setTextColor(Color.BLACK);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							// listView.setOffsetLeft(getDeviceWidth()/2);
							noticeTypeTV.setText("关注");
							markMessageLay
									.setBackgroundResource(R.color.main_bg);
							markMessageIV
									.setImageResource(R.drawable.icon_message_use_normal);
							markMessageTV.setTextColor(Color.WHITE);
							changeTypePop.dismiss();
							CURRENT = MARK;
							pageNo = 1;
							getData(pageNo);
						}
						return true;
					}
				});
	}

	private void delSeletedNotice() {
		List<String> ids = new ArrayList<String>();
		List<Integer> positions = new ArrayList<Integer>();

		Set<Entry<Integer, Boolean>> entrySet = checkedItemMap.entrySet();
		for (Entry<Integer, Boolean> entry : entrySet) {
			int position = entry.getKey();
			V3NoticeCenter msg = (V3NoticeCenter) adapter.getItem(position);
			ids.add(msg.getId());
			positions.add(position);
		}
		String resourcepackageIds = "";
		for (int i = 0; i < ids.size(); i++) {
			if (i == ids.size() - 1) {
				resourcepackageIds = resourcepackageIds + ids.get(i);
			} else {
				resourcepackageIds = resourcepackageIds + ids.get(i) + ",";
			}
		}
		
		for (int i = 0; i < positions.size(); i++) {
			for (int j = 0; j < positions.size() - 1 - i; j++) {
				int before = positions.get(j);
				int after = positions.get(j + 1);
				if (before > after) {
					positions.set(j, after);
					positions.set(j + 1, before);
				}
			}
		}
		for (int k = positions.size() - 1; k >= 0; k--) {
			System.out.println(positions.get(k) + "positions.get(k)");
			int location = positions.get(k);
			allDataList.remove(location);
		}
		
		// 删除选中条目
		setMessageDelete(resourcepackageIds, checkedItemMap.size());
		if (isLongState) { // 取消编辑状态
			isLongState = false;
			checkedItemMap.clear();
			if (mPopHeader != null && mPopHeader.isShowing()) {
				mPopHeader.dismiss();
			}
			if (mPopFooter != null && mPopFooter.isShowing()) {
				mPopFooter.dismiss();
			}
			if(adapter!=null){
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	private void markSeletedNotice() {
		boolean isMark = false;
		List<String> ids = new ArrayList<String>();
		List<Integer> positions = new ArrayList<Integer>();
		HeaderViewListAdapter adapter1 = (HeaderViewListAdapter) listView.getAdapter();
		Set<Entry<Integer, Boolean>> entrySet = checkedItemMap.entrySet();
		for (Entry<Integer, Boolean> entry : entrySet) {
			int position = entry.getKey();
			V3NoticeCenter msg = (V3NoticeCenter) adapter1.getItem(position);
			ids.add(msg.getId());
			positions.add(position);
		}
		String resourcepackageIds = "";
		for (int i = 0; i < ids.size(); i++) {
			if (i == ids.size() - 1) {
				resourcepackageIds = resourcepackageIds + ids.get(i);
			} else {
				resourcepackageIds = resourcepackageIds + ids.get(i) + ",";
			}
		}
		
		for (int i = 0; i < positions.size(); i++) {
			for (int j = 0; j < positions.size() - 1 - i; j++) {
				int before = positions.get(j);
				int after = positions.get(j + 1);
				if (before > after) {
					positions.set(j, after);
					positions.set(j + 1, before);
				}
			}
		}
		
//		for (int k = positions.size() - 1; k >= 0; k--) {
//			System.out.println(positions.get(k) + "positions.get(k)");
//			int location = positions.get(k);
//			allDataList.remove(location);
//		}
		
		for (int i = 0; i < positions.size(); i++) {
			if (Constant.NOTICE_MARK_NO.equals(allDataList.get(positions.get(i)).getAttentionFlag())) {
				isMark = true;
				break;
			}
		}
		
		for (int k = positions.size() - 1; k >= 0; k--) {
			System.out.println(positions.get(k) + "positions.get(k)");
			int location = positions.get(k);
			if (isMark) {
				allDataList.get(location).setAttentionFlag(Constant.NOTICE_MARK_YES);
			}else{
				allDataList.get(location).setAttentionFlag(Constant.NOTICE_MARK_NO);
				if(CURRENT==MARK){
					allDataList.remove(location);
				}
			}
		}
		
		//标记/取消标记选中条目
		lightonForattention(resourcepackageIds, checkedItemMap.size(),isMark);
		
		if (isLongState) { // 取消编辑状态
			isLongState = false;
			checkedItemMap.clear();
			if (mPopHeader != null && mPopHeader.isShowing()) {
				mPopHeader.dismiss();
			}
			if (mPopFooter != null && mPopFooter.isShowing()) {
				mPopFooter.dismiss();
			}
			if(adapter!=null){
				adapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 将消息置成删除
	 * @param messageId
	 * @return
	 */
	public void setMessageDelete(String messageId, final int size) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance()
				.getLoginMap();
		map.put("ids", new ParameterValue(messageId));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					delFlag = UrlUtil.setMessageDelete(ECApplication.getInstance().getAddress(), map);
					System.out.println("delFlag" + delFlag);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (delFlag.contains("true")) {
								ToastUtil.showMessage(size + "条已删除");
//								getMessageCount();
							}
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("请求失败，请稍后重试");
				}
			}
		}).start();
	}
	
	/**
	 * 将消息置成标记/取消标记
	 * @param messageId
	 * @return
	 */
	public void lightonForattention(String messageId, final int size,boolean isMark) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance()
				.getV3LoginMap();
		map.put("ids", new ParameterValue(messageId));
		map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
		if (isMark) {
			new ProgressThreadWrap(this, new RunnableWrap() {
				@Override
				public void run() {
					try {
						markFlag = UrlUtil.lightonForattention(ECApplication.getInstance().getV3Address(), map);
						System.out.println("markFlag" + markFlag);
						handler.postDelayed(new Runnable() {
							public void run() {
								if (markFlag.contains("true")) {
									ToastUtil.showMessage(size + "条已关注");
								}
							}
						}, 5);
					} catch (IOException e) {
						e.printStackTrace();
						ToastUtil.showMessage("请求失败，请稍后重试");
					}
				}
			}).start();
		}else{
			new ProgressThreadWrap(this, new RunnableWrap() {
				@Override
				public void run() {
					try {
						markFlag = UrlUtil.lightoffForAttention(ECApplication.getInstance().getV3Address(), map);
						System.out.println("markFlag" + markFlag);
						handler.postDelayed(new Runnable() {
							public void run() {
								if(adapter!=null){
									adapter.notifyDataSetChanged();
								}
								if (markFlag.contains("true")) {
									ToastUtil.showMessage(size + "条已移除关注");
								}
							}
						}, 5);
					} catch (IOException e) {
						e.printStackTrace();
						ToastUtil.showMessage("请求失败，请稍后重试");
					}
				}
			}).start();
		}
	}
	
	/**
	 * 将消息置成已读
	 * @param messageId
	 * @return
	 */
	public void setMessageRead(String messageId,final int position) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("id", new ParameterValue(messageId));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
				UrlUtil.setMessageRead(ECApplication.getInstance().getAddress(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if(CURRENT==UNREAD){
								allDataList.remove(position);
							}else{
								allDataList.get(position).setReadFlag(Constant.NOTICE_READ_YES);
							}
							if(adapter!=null){
								adapter.notifyDataSetChanged();
							}
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("请求失败，请稍后重试");
				}
			}
		}).start();
	}
	
	/**
	 * 将消息置成已读
	 * @return
	 */
	public void setNoticeRead(String noticeId) {
		noticeMap = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		noticeMap.put("id", new ParameterValue(noticeId));
		noticeMap.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
				UrlUtil.setNoticeRead(ECApplication.getInstance().getAddress(), noticeMap);
					handler.postDelayed(new Runnable() {
						public void run() {
							
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("请求失败，请稍后重试");
				}
			}
		}).start();
	}


	public void showEditPop() {
		if (!mPopHeader.isShowing()) {
			mPopHeader.setFocusable(false);
			mPopHeader.setOutsideTouchable(true);
			mPopHeader.setAnimationStyle(R.style.PopupAnimation);
			mPopHeader.showAtLocation(context.getWindow().getDecorView(),
					Gravity.TOP, 0, getStatusBarHeight(context));
		}

		if (!mPopFooter.isShowing()) {
			mPopFooter.setFocusable(false);
			mPopFooter.setOutsideTouchable(true);
			mPopFooter.setAnimationStyle(R.style.PopupAnimation1);
			mPopFooter.showAtLocation(context.getWindow().getDecorView(),
					Gravity.BOTTOM, 0, 0);
		}
	}

	public void showChanagePop() {
		if (!changeTypePop.isShowing()) {
			changeTypePop.setBackgroundDrawable(new BitmapDrawable());
			changeTypePop.setFocusable(true);
			changeTypePop.setOutsideTouchable(true);
			changeTypePop.setAnimationStyle(R.style.PopupAnimation3);
			changeTypePop.showAsDropDown(top_bar, 0, 0);
			flagIV.setImageResource(R.drawable.iv_change_up);
		}
	}

	/**
	 * 获取通知栏高度
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static int getStatusBarHeight(Context context) {
		Class c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	@Override
	protected void onResume() {
//		getMessageCount();
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.btn_right:
				ECAlertDialog buildAlert = ECAlertDialog.buildAlert(NoticeCenterActivity.this, R.string.intent_xlt_opendownload, null, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						markAllRead();
					}
				});
				buildAlert.setMessage("是否全部标记为已读");
				buildAlert.setTitle("提示");
				buildAlert.show();
				break;
		}
	}

	/**
	 * 获取未读消息数
	 * 
	 */
	public void getMessageCount() {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					messageCount = UrlUtil.getMessageCount(ECApplication.getInstance().getV3Address(), map).trim();
					handler.postDelayed(new Runnable() {
						public void run() {
							unReadCountTV.setText(messageCount);
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 获取未读消息数
	 *
	 */
	public void markAllRead() {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					UrlUtil.setAllRead(ECApplication.getInstance().getAddress(), map).trim();
					handler.postDelayed(new Runnable() {
						public void run() {
							ToastUtil.showMessage("已全部标记为已读");
							pageNo = 1;
							getData(pageNo);
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_notice_center;
	}
	
}
