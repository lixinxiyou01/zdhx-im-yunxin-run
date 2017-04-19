package zhwx.ui.circle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import volley.Response;
import volley.VolleyError;
import zhwx.Constant;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.Comment;
import zhwx.common.model.CommentAndReply;
import zhwx.common.model.ParameterValue;
import zhwx.common.model.Reply;
import zhwx.common.model.SchoolCircle;
import zhwx.common.util.InputTools;
import zhwx.common.util.Log;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.SystemBarTintManager;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.CircleImageViewWithWhite;
import zhwx.common.view.pullrefreshview.PullListView;
import zhwx.common.view.pullrefreshview.RotateLayout;

public class SchoolCircleActivity extends BaseActivity {
	
	private PullListView pullToRefreshListView;
	
	private RotateLayout rotateLayout;
	
	private TextView nameTV;
	
	private CircleImageViewWithWhite headImgIV;
	
	private Activity context;
	
	private int pageNo = 1;
	
	private HashMap<String, ParameterValue> map;
	
	private RequestWithCacheGet mRequestWithCache; 
	
	private Handler handler = new Handler();
	
	private String circleJson;
	
	public static List<SchoolCircle> allCircles = new ArrayList<SchoolCircle>();
	
	private List<SchoolCircle> newCircles = new ArrayList<SchoolCircle>();
	
	public static SchoolCircleListAdapter adapter;
	
	private EditText mEditTextContent;  //回复栏
	
	public LinearLayout rl_bottom;
	
	private RelativeLayout edittext_layout;
	
	private Button btn_send;
	
	public static int commentPosition = 0;
	
	public static int commentKind = 0;
	
	public static CommentAndReply comment;
	
	private ImageView topBackIV;
	
	private FrameLayout top_bar;
	
	private long mPressedTime = 0;
	
	private TextView emptyTV;
	
	private LinearLayout recordTipTv;
	
	private TextView tipTV;
	
	private ImageView tipIV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		context = this;
		mRequestWithCache = new RequestWithCacheGet(context);
	    pullToRefreshListView = (PullListView) findViewById(R.id.refreshlistview);
		rotateLayout = (RotateLayout) findViewById(R.id.rotateLayout);
		View pullView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.headlayout, null);
		pullToRefreshListView.setPullHeaderView(pullView);
		pullToRefreshListView.setRotateLayout(rotateLayout);
		pullToRefreshListView.setCacheColorHint(Color.TRANSPARENT);
		pullToRefreshListView.setHeaderDividersEnabled(false);
		pullToRefreshListView.setDividerHeight(1);
		emptyTV = (TextView) findViewById(R.id.emptyTV);
		pullToRefreshListView.setEmptyView(emptyTV);
		pullToRefreshListView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				closeRebackText();
				return false;
			}
		});
		pullToRefreshListView.setOnLoadMoreListener(new PullListView.OnLoadMoreListener<ListView>() {

			@Override
			public void onLoadMore(PullListView refreshView) {
				pullToRefreshListView.onCompleteLoad();
				pageNo++;
				getPersonalMoments(pageNo);
			}		
		});
		pullToRefreshListView.setOnRefreshListener(new PullListView.OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullListView refreshView) {
				pageNo = 1;
				getPersonalMoments(pageNo);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						pullToRefreshListView.onCompleteRefresh();
					}
				}, 2000);
			}
		});
		
//		pullToRefreshListView.setOnScrollListener(new OnScrollListener() {
//
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				switch (scrollState) {
//				case OnScrollListener.SCROLL_STATE_IDLE:// 空闲状态
//					System.out.println("SCROLL_STATE_IDLE");
//					adapter.setFlagBusy(false);
//					break;
//				case OnScrollListener.SCROLL_STATE_FLING:// 滚动状态
//					System.out.println("SCROLL_STATE_FLING");
//					adapter.setFlagBusy(true);
//					break;
//				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸后滚动
//					System.out.println("SCROLL_STATE_TOUCH_SCROLL");
//					adapter.setFlagBusy(true);
//					break;
//				}
//			}
//
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//
//			}
//		});
		nameTV = (TextView) pullToRefreshListView.getPullHeaderView().findViewById(R.id.nameTV);
		headImgIV = (CircleImageViewWithWhite) pullToRefreshListView.getPullHeaderView().findViewById(R.id.headImgIV);
		nameTV.setText(ECApplication.getInstance().getCurrentIMUser().getName());
		headImgIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				context.startActivity(new Intent(context,MyCircleActivity.class).putExtra("userId", ECApplication.getInstance().getCurrentIMUser().getId()));
			}
		});
		topBackIV = (ImageView) pullToRefreshListView.getPullHeaderView().findViewById(R.id.topBackIV);
		topBackIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
//				ToastUtil.showMessage("个性化封面暂未开通");
			}
		});
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		rl_bottom = (LinearLayout) findViewById(R.id.rl_bottom);
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					rl_bottom.setVisibility(View.VISIBLE);
					InputTools.KeyBoard(mEditTextContent,"open");
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
				} else {
					rl_bottom.setVisibility(View.GONE);
					InputTools.KeyBoard(mEditTextContent,"close");
					edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
				}
			}
		});
		mEditTextContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
			}
		});
		btn_send = (Button) findViewById(R.id.btn_send);
		//评论和回复
		btn_send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				btn_send.setClickable(false);
				if(mEditTextContent.getEditableText().toString().trim().length()==0){
					ToastUtil.showMessage("回复内容不能为空");
					btn_send.setClickable(true);
					return;
				}
				map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
				map.put("content", new ParameterValue(mEditTextContent.getEditableText().toString().trim()));
				map.put("kind", new ParameterValue(Constant.CIRCLE_KIND_SCHOOL));
				if(commentKind == Constant.COMMENT){ //评论模式
					map.put("momentId", new ParameterValue(allCircles.get(commentPosition).getId()));
					new ProgressThreadWrap(context, new RunnableWrap() {
						@Override
						public void run() {
							try {
								final String flag = UrlUtil.saveMomentComment(ECApplication.getInstance().getAddress(),map);  //评论 发送请求
								handler.postDelayed(new Runnable() {
									public void run() { 
										if(flag.trim().length()>10){
											btn_send.setClickable(true);
											closeRebackText();
											allCircles.get(commentPosition).getComments().add(new Comment(mEditTextContent.getEditableText().toString().trim(),ECApplication.getInstance().getCurrentIMUser().getId(), ECApplication.getInstance().getCurrentIMUser().getName(),flag.trim()));
											adapter.notifyDataSetChanged();
											mEditTextContent.setText("");
										}
									}
								}, 5);
							} catch (Exception e) {
								e.printStackTrace();
								btn_send.setClickable(true);
							}
						}
					}).start();
					closeRebackText();
				}else if(commentKind == Constant.REPLY){ //回复模式
					if(comment.getKind()==Constant.COMMENT){  //回复评论
						map.put("targetUserId", new ParameterValue(comment.getUserId()));
						map.put("commentId", new ParameterValue(comment.getConmmentId()));
						map.put("kind", new ParameterValue(Constant.CIRCLE_KIND_SCHOOL));
						new ProgressThreadWrap(context, new RunnableWrap() {
							@Override
							public void run() {
								try {
									final String flag = UrlUtil.saveCommentReply(ECApplication.getInstance().getAddress(),map);  //回复评论 发送请求
									handler.postDelayed(new Runnable() {
										public void run() { 
											if(flag.trim().length()>10){
												btn_send.setClickable(true);
												for (int i = 0; i < allCircles.get(commentPosition).getComments().size(); i++) {
													if(comment.getConmmentId().equals(allCircles.get(commentPosition).getComments().get(i).getId())){
														allCircles.get(commentPosition).getComments().get(i).getReplys().add(new Reply(mEditTextContent.getEditableText().toString().trim(), ECApplication.getInstance().getCurrentIMUser().getId(), ECApplication.getInstance().getCurrentIMUser().getName(), comment.getUserName(),flag.trim(),comment.getUserId()));
														break;
													}
												}
												adapter.notifyDataSetChanged();
												mEditTextContent.setText("");
											}
										}
									}, 5);
								} catch (Exception e) {
									e.printStackTrace();
									btn_send.setClickable(true);
								}
							}
						}).start();
						closeRebackText();
					}else if(comment.getKind()==Constant.REPLY){
						map.put("targetUserId", new ParameterValue(comment.getReplyUserId()));
						map.put("commentId", new ParameterValue(comment.getConmmentId()));
						map.put("kind", new ParameterValue(Constant.CIRCLE_KIND_SCHOOL));
						new ProgressThreadWrap(context, new RunnableWrap() {
							@Override
							public void run() {
								try {
									final String flag = UrlUtil.saveCommentReply(ECApplication.getInstance().getAddress(),map);  //回复评论 发送请求
									handler.postDelayed(new Runnable() {
										public void run() { 
											if(flag.trim().length()>10){
												btn_send.setClickable(true);
												for (int i = 0; i < allCircles.get(commentPosition).getComments().size(); i++) {
													if(comment.getConmmentId().equals(allCircles.get(commentPosition).getComments().get(i).getId())){
														allCircles.get(commentPosition).getComments().get(i).getReplys().add(new Reply(mEditTextContent.getEditableText().toString().trim(), ECApplication.getInstance().getCurrentIMUser().getId(), ECApplication.getInstance().getCurrentIMUser().getName(), comment.getReplyUserName(),flag.trim(),comment.getReplyUserId()));
														break;
													}
												}
												adapter.notifyDataSetChanged();
												mEditTextContent.setText("");
											}
										}
									}, 5);
								} catch (Exception e) {
									e.printStackTrace();
									btn_send.setClickable(true);
								}
							}
						}).start();
						closeRebackText();
					}
				}
			}
		});
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		top_bar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				doubleClick();
			}
		});
		
		recordTipTv = (LinearLayout) findViewById(R.id.recordTipTv);
		tipTV = (TextView) findViewById(R.id.tipTV);
		tipIV = (ImageView) findViewById(R.id.tipIV);
		
		recordTipTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, CircleRecordActivity.class).putExtra("kind", Constant.CIRCLE_KIND_SCHOOL));
				recordTipTv.setVisibility(View.INVISIBLE);
			}
		});
		setImmerseLayout(top_bar,1);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  
			setTranslucentStatus(true);  
	         SystemBarTintManager mTintManager = new SystemBarTintManager(this);
	         mTintManager.setStatusBarTintEnabled(true);  
	         mTintManager.setNavigationBarTintEnabled(true);  
	         mTintManager.setTintColor(Color.parseColor("#18ab8e"));  
	     }  
		getPersonalMoments(pageNo);
	}
	
	@TargetApi(19)  
	private void setTranslucentStatus(boolean on) {  
	     Window win = getWindow();  
	     WindowManager.LayoutParams winParams = win.getAttributes();  
	     final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;  
	     if (on) {  
	         winParams.flags |= bits;  
	     } else {  
	         winParams.flags &= ~bits;  
	     }  
	     win.setAttributes(winParams);  
	 }  
	
	private void getPersonalMoments(int pageNo) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		map.put("pageNum", new ParameterValue(pageNo+""));
		try {
			circleJson = mRequestWithCache.getRseponse(UrlUtil.getPersonalMoments(ECApplication.getInstance().getAddress(), map), new RequestWithCacheGet.RequestListener() {
				
				@Override
				public void onResponse(String response) {
					if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
						Log.i("新数据:"+response);
						refreshData(response);
					}
//					else{
//						if ((circleJson != null && !circleJson.equals(RequestWithCache.NO_DATA))) {
//							Log.i("缓存数据:"+circleJson);
//							refreshData(circleJson);
//						}
//					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if ((circleJson != null && !circleJson.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:"+circleJson);
			refreshData(circleJson);
		}
	}

	private void refreshData(String json) {
		if(!json.contains("userName")){
			return;
		}
		if(pageNo == 1){
			allCircles.clear();
		}
		Gson gson = new Gson();
		newCircles = gson.fromJson(json, new TypeToken<List<SchoolCircle>>(){}.getType());
		
		if(newCircles!=null&&newCircles.size()!=0){
			for (SchoolCircle circle : newCircles) {
				allCircles.add(circle);
			}
		}
		if(pageNo==1){
			adapter = new SchoolCircleListAdapter(context, allCircles,headImgIV,recordTipTv,tipTV,tipIV);
			pullToRefreshListView.setAdapter(adapter);
			if(allCircles.size()!=0){
				ECApplication.getInstance().saveLastSchoolCircleTime(allCircles.get(0).getTime());
				if("2030-01-01 00:00:00".equals(ECApplication.getInstance().getLastSchoolCircleRecordTime())){
					ECApplication.getInstance().saveLastSchoolCircleRecordTime(allCircles.get(0).getTime());
				}
			}
		}else{
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_schoolcircle;
	}
	
	/**
	 * 发状态
	 * @param v
	 */
	public void onSend(View v){
		Intent intent = new Intent(this, SendNewCircleActivity.class);
		intent.putExtra("circleFlag", "school");
		startActivityForResult(intent, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1){
			if(data!=null){
				String flag = data.getStringExtra("flag");
				if("refresh".equals(flag)){
					pageNo = 1;
					getPersonalMoments(pageNo);
				}
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	protected void onDestroy() {
		super.onDestroy();
		adapter = null;
		System.gc();
	};
	
	public void onClearFocus(View v){
		closeRebackText();
	}
	
	/**
	 * 关闭软键盘&回复栏
	 */
	private void closeRebackText(){
		if(rl_bottom.getVisibility()==View.VISIBLE){
			mEditTextContent.clearFocus();
			rl_bottom.setVisibility(View.GONE);
			InputTools.KeyBoard(mEditTextContent,"close");
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			closeRebackText();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void doubleClick(){
		long mNowTime = System.currentTimeMillis();//获取第一次按键时间
		if((mNowTime - mPressedTime) > 1000){//比较两次按键时间差
			mPressedTime = mNowTime;
		} else {
			pullToRefreshListView.smoothScrollToPositionFromTop(0,0);
//			pullToRefreshListView.setSelection(0);
//			pullToRefreshListView.OnRefreshing();
//			pageNo = 1;
//			getPersonalMoments(pageNo);
//			new Handler().postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
//					pullToRefreshListView.onCompleteRefresh();
//				}
//			}, 2000);
		}
	}

}
