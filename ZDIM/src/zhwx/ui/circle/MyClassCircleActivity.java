package zhwx.ui.circle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import volley.Response;
import volley.VolleyError;
import zhwx.Constant;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.Moment;
import zhwx.common.model.MyCircle;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.Log;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.CircleImageViewWithWhite;
import zhwx.common.view.pullrefreshview.PullListView;
import zhwx.common.view.pullrefreshview.RotateLayout;

public class MyClassCircleActivity extends BaseActivity {
	
	private PullListView pullToRefreshListView;
	
	private RotateLayout rotateLayout;
	
	private TextView nameTV;
	
	private CircleImageViewWithWhite headImgIV;
	
	private Activity context;
	
	private int pageNo = 1;
	
	private HashMap<String, ParameterValue> map;
	
	private RequestWithCacheGet mRequestWithCache;
	
	private Handler handler = new Handler();
	
	private String myCircleJson;
	
	public static MyCircle myCircle;
	
	public static List<Moment> allCircles = new ArrayList<Moment>();
	
	private List<Moment> newCircles = new ArrayList<Moment>();
	
	private String departmentId;
	
	private String departmentName;
	
	private MyCircleListAdapter adapter;
	
	private FrameLayout top_bar;
	
	private long mPressedTime = 0;
	
	private TextView emptyTV;
	
	private ImageButton goRecordBT;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		mRequestWithCache = new RequestWithCacheGet(context);
		departmentId = getIntent().getStringExtra("departmentId");
		departmentName = getIntent().getStringExtra("departmentName");
		pullToRefreshListView = (PullListView) findViewById(R.id.refreshlistview);
		rotateLayout = (RotateLayout) findViewById(R.id.rotateLayout);
		View pullView = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.classheadlayout, null);
		pullToRefreshListView.setPullHeaderView(pullView);
		pullToRefreshListView.setRotateLayout(rotateLayout);
		pullToRefreshListView.setCacheColorHint(Color.TRANSPARENT);
		pullToRefreshListView.setHeaderDividersEnabled(false);
		pullToRefreshListView.setDividerHeight(1);
		emptyTV = (TextView) findViewById(R.id.emptyTV);
		pullToRefreshListView.setEmptyView(emptyTV);
		pullToRefreshListView.setOnLoadMoreListener(new PullListView.OnLoadMoreListener<ListView>() {

					@Override
					public void onLoadMore(PullListView refreshView) {
						pullToRefreshListView.onCompleteLoad();
						pageNo++;
						getUserMoments(pageNo);
					}
				});
		pullToRefreshListView.setOnRefreshListener(new PullListView.OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullListView refreshView) {
				pageNo = 1;
				getUserMoments(pageNo);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						pullToRefreshListView.onCompleteRefresh();
					}
				}, 2000);
			}
		});
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				Moment value = (Moment) parent.getAdapter().getItem(position);
				if(value!=null){
					startActivity(new Intent(context, CircleDetailActivity.class).putExtra("momentId", value.getId()).putExtra("kind", Constant.CIRCLE_KIND_CLASS));
				}
			}
		});
		nameTV = (TextView) pullToRefreshListView.getPullHeaderView().findViewById(R.id.nameTV);
		headImgIV = (CircleImageViewWithWhite) pullToRefreshListView.getPullHeaderView().findViewById(R.id.headImgIV);
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		top_bar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				doubleClick();
			}
		});
		goRecordBT = (ImageButton) findViewById(R.id.goRecordBT);
		goRecordBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, CircleRecordActivity.class).putExtra("kind", Constant.CIRCLE_KIND_CLASS).putExtra("isAll", true));
			}
		});
		setImmerseLayout(top_bar);
		getUserMoments(pageNo);
	}
	
	
	
	private void getUserMoments(int pageNo) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("departmentId", new ParameterValue(departmentId));
		map.put("pageNum", new ParameterValue(pageNo+""));
		try {
			myCircleJson = mRequestWithCache.getRseponse(UrlUtil.getClassMoments(ECApplication.getInstance().getAddress(), map), new RequestWithCacheGet.RequestListener() {
				
				@Override
				public void onResponse(String response) {
					if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
						Log.i("新数据:"+response);
						refreshData(response);
					}
//					else{
//						if ((myCircleJson != null && !myCircleJson.equals(RequestWithCache.NO_DATA))) {
//							Log.i("缓存数据:"+myCircleJson);
//							refreshData(myCircleJson);
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
		
		if ((myCircleJson != null && !myCircleJson.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:"+myCircleJson);
			refreshData(myCircleJson);
		}
	}
	
	private void refreshData(String json) {
		if(pageNo==1){
			allCircles.clear();
		}
		Gson gson = new Gson();
		myCircle = gson.fromJson(json, MyCircle.class);
		newCircles = myCircle.getMoments();
		if(newCircles != null && newCircles.size() != 0){
			for (Moment circle : newCircles) {
				allCircles.add(circle);
			}
		}
		if(pageNo==1){
			adapter = new MyCircleListAdapter(context, allCircles,headImgIV,myCircle.getHeadPortraitUrl());
			pullToRefreshListView.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
		nameTV.setText(departmentName);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_myclasscircle;
	}
	
	public void doubleClick(){
		long mNowTime = System.currentTimeMillis();//获取第一次按键时间
		if((mNowTime - mPressedTime) > 1000){//比较两次按键时间差
			mPressedTime = mNowTime;
		} else {
			pullToRefreshListView.setSelection(0);
			pullToRefreshListView.OnRefreshing();
			pageNo = 1;
			getUserMoments(pageNo);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					pullToRefreshListView.onCompleteRefresh();
				}
			}, 2000);
		
		}
	}
}
