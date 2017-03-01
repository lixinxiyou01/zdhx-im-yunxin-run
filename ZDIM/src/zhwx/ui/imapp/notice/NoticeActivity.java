package zhwx.ui.imapp.notice;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.refreshlayout.PullableListView;
import zhwx.common.view.waveview.WaveSwipeRefreshLayout;
import zhwx.ui.imapp.notice.model.V3Notice;



public class NoticeActivity extends BaseActivity{

	private Activity context;

	public int UNREAD = 1; 	// 未读
	public int ALL = 2; 	// 全部
	public int MARK = 3;	// 被标记
	public int SENDED = 4;  //发件箱
	public int CURRENT = 0; 
	
	private PullableListView listView;
	
	private TextView emptyView;

	private WaveSwipeRefreshLayout layout;

	private PopupWindow mPopHeader, mPopFooter, changeTypePop;

	private View headerView, footerView, changeTypeView;

	private HashMap<String, ParameterValue> map;
	
	private HashMap<String, ParameterValue> couldSendMap;

	public static List<V3Notice> allDataList = new ArrayList<V3Notice>();

	private List<V3Notice> newDataList = new ArrayList<V3Notice>();

	private RequestWithCacheGet mRequestWithCache;

	private MessageListAdapter adapter;

	private Handler handler = new Handler();

	private int pageNo = 1;

	private String noticeJson;
	
	public static String couldSendflag = "";

	private FrameLayout top_bar;

	private long mPressedTime = 0;

	private LinearLayout sendLay;

	/** 判断是否是编辑状态 */
	private boolean isLongState = false;

	private Button editBT, deleteAllBT,allMarkBT, cancelBT;

	private TextView seleteCountTV, noticeTypeTV, allMessageTV, unReadTV,markMessageTV, unReadCountTV,sendedTV;

	private ImageView flagIV, allMessageIV, unReadIV, markMessageIV,sendedIV;

	private LinearLayout changeTypeLay;

	private RelativeLayout unReadLay, allMessageLay, markMessageLay,sendedLay;

	private CheckBox seleteAllCB;

	private String delFlag = "";
	
	private String markFlag = "";
	
	private String messageCount = "";
	
	private HashMap<Integer, Boolean> checkedItemMap = new HashMap<Integer, Boolean>();
	
	@Override
	protected int getLayoutId() {return R.layout.activity_notice;}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		
		mRequestWithCache = new RequestWithCacheGet(context);
		sendLay = (LinearLayout) findViewById(R.id.sendLay);
		listView = (PullableListView) findViewById(R.id.content_view);
		layout = (WaveSwipeRefreshLayout) findViewById(R.id.refresh_view);
		layout.setColorSchemeColors(Color.WHITE, Color.WHITE);
		layout.setWaveColor(Color.parseColor("#ebc426"));
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
		},this);

		listView.setOnLoadListener(new PullableListView.OnLoadListener() {

			@Override
			public void onLoad(PullableListView pullableListView) {
				if (pageNo != 1 && (StringUtil.isBlank(noticeJson.trim())||"[]".equals(noticeJson.trim()))) {
					listView.finishLoading();
					listView.setLoadmoreVisible(false);
					ToastUtil.showMessage("到底了");
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
					V3Notice notice = (V3Notice) parent.getAdapter().getItem(
							position);
					Intent intent = new Intent(context,NoticeDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("notice", notice);
					intent.putExtras(bundle);
					intent.putExtra("position", position);
					intent.putExtra("type", CURRENT);
					startActivityForResult(intent, 100);
					setMessageRead(notice.getId(),position);  //置已读
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
					if(adapter!=null){
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
				showChanagePop();
			}
		});
		setImmerseLayout(top_bar);
		initPopMenu();
		CURRENT = ALL;
		getNoticeGranted();
		getData(pageNo);
	}

	/**
	 * 获取是否有发通知权限
	 */
	private void getNoticeGranted() {
		couldSendMap = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		couldSendMap.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					couldSendflag = UrlUtil.ifNewNoticeGranted(getBaseUrl(), couldSendMap);
					handler.postDelayed(new Runnable() {
						public void run() {
							if("1".equals(couldSendflag.trim())){
								showSendPop();
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

	private void getData(final int pageNo) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		map.put("pageFlag", new ParameterValue(pageNo + ""));
		String url = "";
		if (CURRENT == ALL) {
			url = UrlUtil.getAllNoticeList(getBaseUrl(),map);
		} else if (CURRENT == UNREAD) {
			url = UrlUtil.getUnReadNoticeList(getBaseUrl(), map);
		} else if (CURRENT == MARK) {
			url = UrlUtil.getAttentionNoticeList(getBaseUrl(),map);
		}else if (CURRENT == SENDED) {
			url = UrlUtil.getSendNoticeList(getBaseUrl(),map);
		}
		try {
			noticeJson = mRequestWithCache.getRseponse(url,
					new RequestWithCacheGet.RequestListener() {

						@Override
						public void onResponse(String response) {
							if (response != null&& !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
								Log.i("NoticeActiviy","新数据:" + response);
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
			Log.i("NoticeActiviy","缓存数据:" + noticeJson);
			refreshData(noticeJson,pageNo);
		}
	}
	
	/**
	 * @return
	 */
	public String getBaseUrl() {
		return ECApplication.getInstance().getAddress();
	}

	private void refreshData(String json,int pageNum) {
		if(json.contains("<html>")){
			ToastUtil.showMessage("数据异常");
			return;
		}
		if (StringUtil.isBlank(json)||"[]".equals(json)) {
			listView.finishLoading();
			listView.setLoadmoreVisible(false);
		}
		if (pageNum == 1) {
			allDataList.clear();
		} else {
			listView.finishLoading();
		}
		
		try {
			Gson gson = new Gson();
			if (json.contains("<html>")) {
				return;
			}
			newDataList = gson.fromJson(json, new TypeToken<List<V3Notice>>() {}.getType());
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			ToastUtil.showMessage("数据出现错误");
		}
		
		if (newDataList != null && newDataList.size() != 0) {
			allDataList.addAll(newDataList);
		} else {
			return;
		}
		if (pageNum == 1) {
			adapter = new MessageListAdapter();
			listView.setAdapter(adapter);
			if(allDataList.size() < 20) {
				listView.finishLoading();
				listView.setLoadmoreVisible(false);
			}
//			listView.setLayoutAnimation(LayoutAnimationUtil.getVerticalListAnim());
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
				convertView = LayoutInflater.from(context).inflate(R.layout.notice_list_row, null);
				holder = new ViewHolder();
				holder.senderTV = (TextView) convertView.findViewById(R.id.senderTV);
				holder.timeTV = (TextView) convertView.findViewById(R.id.timeTV);
				holder.messageTitle = (TextView) convertView.findViewById(R.id.messageTitle);
				holder.messageContentTV = (TextView) convertView.findViewById(R.id.messageContentTV);
				holder.aIV = (ImageView) convertView.findViewById(R.id.aIV);
				holder.isReadIV = (ImageView) convertView.findViewById(R.id.isReadIV);
				holder.isMarkIV = (ImageView) convertView.findViewById(R.id.isMarkIV);
				holder.frontLay = (RelativeLayout) convertView.findViewById(R.id.frontLay);
				holder.selectFlagCB = (CheckBox) convertView.findViewById(R.id.selectFlagCB);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.senderTV.setText(allDataList.get(position).getSendUser()==null?"我":allDataList.get(position).getSendUser());
			holder.timeTV.setText(allDataList.get(position).getTime());
			holder.messageTitle.setText(allDataList.get(position).getTitle());
			holder.messageContentTV.setText(StringUtil.isBlank(allDataList.get(position).getContent())?"无内容":allDataList.get(position).getContent());

			if (allDataList.get(position).getAttachmentFlag().size() != 0) {
				holder.aIV.setVisibility(View.VISIBLE);
			} else {
				holder.aIV.setVisibility(View.GONE);
			}

			if (Constant.NOTICE_MARK_YES.equals(allDataList.get(position).getAttentionFlag())) {
				holder.isMarkIV.setVisibility(View.VISIBLE);
			} else {
				holder.isMarkIV.setVisibility(View.GONE);
			}
			
			if(allDataList.get(position).getReadFlag() !=null && Constant.NOTICE_UNREAD.equals(allDataList.get(position).getReadFlag())){
				holder.isReadIV.setVisibility(View.VISIBLE);
			}else{
				holder.isReadIV.setVisibility(View.GONE);
			}
			
			if (isLongState) {
				holder.selectFlagCB.setVisibility(View.VISIBLE);
				if (checkedItemMap.containsKey(position)) {
					holder.selectFlagCB.setChecked(true);
					holder.frontLay.setSelected(true);
				} else {
					holder.selectFlagCB.setChecked(false);
					holder.frontLay.setSelected(false);
				}
			} else {
				holder.frontLay.setSelected(false);
				holder.selectFlagCB.setVisibility(View.GONE);
			}
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
			private TextView senderTV, timeTV, messageTitle, messageContentTV;
			private ImageView aIV, isReadIV, isMarkIV;
			private RelativeLayout frontLay;
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
		} else if(keyCode == KeyEvent.KEYCODE_MENU){
			if("1".equals(couldSendflag.trim())){
				showSendPop();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 初始化popupWindow
	 */
	private void initPopMenu() {
		// 顶部pop
		headerView = context.getLayoutInflater().inflate(R.layout.notice_list_edit_header, null);
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
					if("1".equals(couldSendflag.trim())){
						showSendPop();
					}
				}
			}
		});
		seleteCountTV = (TextView) headerView.findViewById(R.id.seleteCountTV);
		seleteAllCB = (CheckBox) headerView.findViewById(R.id.seleteAllCB);
		seleteAllCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
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
		footerView = context.getLayoutInflater().inflate(R.layout.notice_list_edit_footer, null);
		if (mPopFooter == null) {
			mPopFooter = new PopupWindow(footerView, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, true);
		}
		mPopFooter.setOutsideTouchable(false);
		if (mPopFooter.isShowing()) {
			mPopFooter.dismiss();
		}
		mPopFooter.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				if (isLongState) {
					showEditPop();
				}
			}
		});
		
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
				R.layout.pop_chosenoticegroup, null);
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
				markMessageLay.setBackgroundResource(R.color.main_bg_notice);
				markMessageIV.setImageResource(R.drawable.icon_message_use_normal);
				markMessageTV.setTextColor(Color.WHITE);
				sendedLay.setBackgroundResource(R.color.main_bg_notice);
				sendedIV.setImageResource(R.drawable.icon_message_use_normal);
				sendedTV.setTextColor(Color.WHITE);
				unReadLay.setBackgroundResource(R.color.main_bg_notice);
				unReadIV.setImageResource(R.drawable.icon_message_unuse_normal);
				unReadTV.setTextColor(Color.WHITE);
				unReadCountTV.setTextColor(Color.WHITE);
				allMessageLay.setBackgroundResource(R.color.main_bg_notice);
				allMessageIV.setImageResource(R.drawable.icon_message_normal);
				allMessageTV.setTextColor(Color.WHITE);
			}
		});
		allMessageTV = (TextView) changeTypeView.findViewById(R.id.allMessageTV);
		unReadTV = (TextView) changeTypeView.findViewById(R.id.unReadTV);
		markMessageTV = (TextView) changeTypeView.findViewById(R.id.markMessageTV);
		sendedTV = (TextView) changeTypeView.findViewById(R.id.sendedTV);
		unReadCountTV = (TextView) changeTypeView.findViewById(R.id.unReadCountTV);
		allMessageIV = (ImageView) changeTypeView.findViewById(R.id.allMessageIV);
		unReadIV = (ImageView) changeTypeView.findViewById(R.id.unReadIV);
		markMessageIV = (ImageView) changeTypeView.findViewById(R.id.markMessageIV);
		sendedIV = (ImageView) changeTypeView.findViewById(R.id.sendedIV);
		
		unReadLay = (RelativeLayout) changeTypeView.findViewById(R.id.unReadLay);
		unReadLay.setOnTouchListener(new OnTouchListener() {// 未读
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					unReadLay.setBackgroundResource(R.color.bg_pressed_notice);
					unReadIV.setImageResource(R.drawable.icon_message_unuse_pressed);
					unReadCountTV.setTextColor(Color.BLACK);
					unReadTV.setTextColor(Color.BLACK);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					noticeTypeTV.setText("未读");
					changeTypePop.dismiss();
					CURRENT = UNREAD;
					pageNo = 1;
					getData(pageNo);
				}
				return true;
			}		
		});

		allMessageLay = (RelativeLayout) changeTypeView.findViewById(R.id.allMessageLay);
		allMessageLay.setOnTouchListener(new OnTouchListener() {// 全部

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					allMessageLay
							.setBackgroundResource(R.color.bg_pressed_notice);
					allMessageIV
							.setImageResource(R.drawable.icon_message_pressed);
					allMessageTV.setTextColor(Color.BLACK);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					noticeTypeTV.setText("收件箱");
					changeTypePop.dismiss();
					CURRENT = ALL;
					pageNo = 1;
					getData(pageNo);
				}
				return true;
			}		
		});
		markMessageLay = (RelativeLayout) changeTypeView.findViewById(R.id.markMessageLay);
		markMessageLay.setOnTouchListener(new OnTouchListener() {// 已读

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					markMessageLay.setBackgroundResource(R.color.bg_pressed_notice);
					markMessageIV.setImageResource(R.drawable.icon_message_use_pressed);
					markMessageTV.setTextColor(Color.BLACK);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					noticeTypeTV.setText("关注");
					changeTypePop.dismiss();
					CURRENT = MARK;
					pageNo = 1;
					getData(pageNo);
				}
				return true;
			}		
		});
		
		sendedLay = (RelativeLayout) changeTypeView.findViewById(R.id.sendedLay);
		sendedLay.setOnTouchListener(new OnTouchListener() {// 已读
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					sendedLay.setBackgroundResource(R.color.bg_pressed_notice);
					sendedIV.setImageResource(R.drawable.icon_message_use_pressed);
					sendedTV.setTextColor(Color.BLACK);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					noticeTypeTV.setText("发件箱");
					changeTypePop.dismiss();
					CURRENT = SENDED;
					pageNo = 1;
					getData(pageNo);
				}
				return true;
			}		
		});
		
		sendLay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 startActivity(new Intent(context, SendNewNoticeActivity.class));
			}
		});
	}

	private void delSeletedNotice() {
		List<String> ids = new ArrayList<String>();
		List<Integer> positions = new ArrayList<Integer>();
		Adapter adapter1 = (Adapter) listView.getAdapter();
		Set<Entry<Integer, Boolean>> entrySet = checkedItemMap.entrySet();
		for (Entry<Integer, Boolean> entry : entrySet) {
			int position = entry.getKey();
			V3Notice msg = (V3Notice) adapter1.getItem(position);
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
		Adapter adapter1 = (Adapter) listView.getAdapter();
		Set<Entry<Integer, Boolean>> entrySet = checkedItemMap.entrySet();
		
		for (Entry<Integer, Boolean> entry : entrySet) {
			int position = entry.getKey();
			V3Notice msg = (V3Notice) adapter1.getItem(position);
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
		
		
		//position从小到大排序
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
		
		//如果有没被标记的就标记，如果全都标记了就取消标记
		for (int i = 0; i < positions.size(); i++) {
			if (Constant.NOTICE_MARK_NO.equals(allDataList.get(positions.get(i)).getAttentionFlag())) {
				isMark = true;
				break;
			}
		}
		
		//倒序开始操作  标记or取消标记 
		for (int k = positions.size() - 1; k >= 0; k--) {
			int location = positions.get(k);
			if (isMark) {
				allDataList.get(location).setAttentionFlag(Constant.NOTICE_MARK_YES);
			}else{
				allDataList.get(location).setAttentionFlag(Constant.NOTICE_MARK_NO);
				if(CURRENT == MARK){
					allDataList.remove(location);
				}
			}
		}
		
		//标记or取消标记选中条目
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
	 * @param context
	 * @return
	 */
	public void setMessageDelete(String messageId, final int size) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance()
				.getLoginMap();
		map.put("id", new ParameterValue(messageId));
		map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
//					if (CURRENT == SENDED)  
					delFlag = UrlUtil.setNoticeDelete(getBaseUrl(), map);
					System.out.println("delFlag" + delFlag);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (delFlag.contains("ok")) {
								ToastUtil.showMessage(size + "条已删除");
								getMessageCount();
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
	 * @param context
	 * @return
	 */
	public void lightonForattention(String messageId, final int size,boolean isMark) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance()
				.getLoginMap();
		map.put("id", new ParameterValue(messageId));
		map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		if (isMark) {
			new ProgressThreadWrap(this, new RunnableWrap() {
				@Override
				public void run() {
					try {
						markFlag = UrlUtil.ajaxLighton(getBaseUrl(), map);
						System.out.println("markFlag" + markFlag);
						handler.postDelayed(new Runnable() {
							public void run() {
								if (StringUtil.isNotBlank(markFlag)||markFlag.contains("ok")) {
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
						markFlag = UrlUtil.ajaxLightoff(getBaseUrl(), map);
						System.out.println("markFlag" + markFlag);
						handler.postDelayed(new Runnable() {
							public void run() {
								if(adapter!=null){
									adapter.notifyDataSetChanged();
								}
								if (markFlag.contains("ok")) {
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
	 * 
	 * @param messageId
	 * @param context
	 * @return
	 */
	public void setMessageRead(String messageId,final int position) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance()
				.getLoginMap();
		map.put("id", new ParameterValue(messageId));
		map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
				UrlUtil.setNoticeRead(getBaseUrl(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if(CURRENT == UNREAD){
								allDataList.remove(position);
							}else{
								allDataList.get(position).setReadFlag(Constant.NOTICE_READ);
							}
							if(adapter != null){
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

	public void showEditPop() {
//		if (sendNewPop.isShowing()) {
//			sendNewPop.dismiss();
//		}
		
		if (!mPopHeader.isShowing()) {
			mPopHeader.setFocusable(false);
			mPopHeader.setOutsideTouchable(true);
			mPopHeader.setAnimationStyle(R.style.PopupAnimation);
			mPopHeader.showAtLocation(context.getWindow().getDecorView(),Gravity.TOP, 0, getStatusBarHeight(context));
		}

		if (!mPopFooter.isShowing()) {
			mPopFooter.setFocusable(false);
			mPopFooter.setOutsideTouchable(true);
			mPopFooter.setAnimationStyle(R.style.PopupAnimation1);
			mPopFooter.showAtLocation(context.getWindow().getDecorView(),Gravity.BOTTOM, 0, 0);
		}
	}
	
	public void showSendPop() {
		if (sendLay.getVisibility() != View.VISIBLE) {
			sendLay.setVisibility(View.VISIBLE);
		}
	}

	@SuppressWarnings("deprecation")
	public void showChanagePop() {
		if (!changeTypePop.isShowing()) {
			changeTypePop.setBackgroundDrawable(new BitmapDrawable());
			changeTypePop.setFocusable(true);
			changeTypePop.setOutsideTouchable(true);
			changeTypePop.setAnimationStyle(R.style.PopupAnimation3);
			changeTypePop.showAsDropDown(top_bar, 0, 0);
			flagIV.setImageResource(R.drawable.iv_change_up);
			
			if (CURRENT == ALL) {
				allMessageLay.setBackgroundResource(R.color.bg_pressed_notice);
				allMessageIV.setImageResource(R.drawable.icon_message_pressed);
				allMessageTV.setTextColor(Color.BLACK);
			} else if (CURRENT == UNREAD) {
				unReadLay.setBackgroundResource(R.color.bg_pressed_notice);
				unReadIV.setImageResource(R.drawable.icon_message_unuse_pressed);
				unReadCountTV.setTextColor(Color.BLACK);
				unReadTV.setTextColor(Color.BLACK);
			} else if (CURRENT == MARK) {
				markMessageLay.setBackgroundResource(R.color.bg_pressed_notice);
				markMessageIV.setImageResource(R.drawable.icon_message_use_pressed);
				markMessageTV.setTextColor(Color.BLACK);
			} else if (CURRENT == SENDED) {
				sendedLay.setBackgroundResource(R.color.bg_pressed_notice);
				sendedIV.setImageResource(R.drawable.icon_message_use_pressed);
				sendedTV.setTextColor(Color.BLACK);
			}
		}
	}

	/**
	 * 获取通知栏高度
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
		getMessageCount();
		if("1".equals(couldSendflag.trim())){
			showSendPop();
		}
		super.onResume();
	}

	/**
	 * 获取未读消息数
	 * 
	 * @param UserId
	 */
	public void getMessageCount() {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					messageCount = UrlUtil.getUnReadNoticeCount(
							getBaseUrl(), map).trim();
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
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
        	adapter.notifyDataSetChanged();
		}
	}

}
