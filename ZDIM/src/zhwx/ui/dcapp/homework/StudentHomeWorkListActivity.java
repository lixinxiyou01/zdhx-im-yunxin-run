package zhwx.ui.dcapp.homework;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import volley.Response;
import volley.VolleyError;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.DateUtil;
import zhwx.common.util.Log;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECListDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.refreshlayout.PullToRefreshLayout;
import zhwx.common.view.refreshlayout.PullableListView;

public class StudentHomeWorkListActivity extends BaseActivity {
	
	private Activity context;
	
	private PullToRefreshLayout layout;
	
	private PullableListView listView;
	
	private List<HomeWorkCourse> projectlist = new ArrayList<HomeWorkCourse>();
	
	private List<String> names = new  ArrayList<String>();
	
	private int checkPosition = 0; 
	
	private FrameLayout top_bar1;
	
	private LinearLayout changeTypeLay;
	
	private TextView subjectTV;
	
	private RequestWithCacheGet mRequestWithCache;
	
	private int pageNo = 1;
	
	private HashMap<String, ParameterValue> map;
	
	private HashMap<String, ParameterValue> courseMap;
	
	private String resultJson;
	
	private String courseResultJson;
	
	private String currentSubject = "";
	
	private StudentListAdapter adapter;
	
	private TextView emptyTV;
	
	private int checkDetailPosition;
	
	public static List<HomeWork> allDataList = new ArrayList<HomeWork>();

	private List<HomeWork> newDataList = new ArrayList<HomeWork>();
	
	private ECProgressDialog progressDialog;
	
	@Override
	protected int getLayoutId() {return R.layout.activity_student_homework;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		progressDialog = new ECProgressDialog(context);
		projectlist.add(new HomeWorkCourse("", "全部"));
		mRequestWithCache = new RequestWithCacheGet(context);
		layout = (PullToRefreshLayout) findViewById(R.id.refresh_view1);
		layout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				pageNo = 1;
				listView.setLoadmoreVisible(true);
				getData(pageNo);
				new Handler() {
					@Override
					public void handleMessage(Message msg) {
						layout.refreshFinish(layout.SUCCEED);
					}
				}.sendEmptyMessageDelayed(0, 1000);
			}
		});
		listView = (PullableListView) findViewById(R.id.content_view1);
		listView.setOnLoadListener(new PullableListView.OnLoadListener() {

			@Override
			public void onLoad(PullableListView pullableListView) {
				if (pageNo != 1 && (StringUtil.isBlank(resultJson) || "[]".equals(resultJson))) {
					listView.finishLoading();
					listView.setLoadmoreVisible(false);
					return;
				}
				pageNo++;
				getData(pageNo);
			}
		});
		emptyTV = (TextView) findViewById(R.id.emptyTV);
		listView.setEmptyView(emptyTV);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//进入详情
				
				checkDetailPosition = position;
				Intent intent = new Intent(context, StudentHomeWorkDetailsActivity.class);
				intent.putExtra("id", allDataList.get(position).getStudentWorkId());
				startActivityForResult(intent,111);
			}
		});
		top_bar1 = (FrameLayout) findViewById(R.id.top_bar1);
		top_bar1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
			}
		});
		subjectTV = (TextView) findViewById(R.id.subjectTV);
		subjectTV.setText(projectlist.get(0).getCourseName());
		changeTypeLay = (LinearLayout) findViewById(R.id.changeTypeLay);
        changeTypeLay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				names.clear();
				for (int i = 0; i < projectlist.size(); i++) {
					names.add(projectlist.get(i).getCourseName());
				}
				final ECListDialog dialog;
				dialog = new ECListDialog(context , names ,checkPosition);
		        dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
		            @Override
		            public void onDialogItemClick(Dialog d, int position) {
		            	checkPosition = position;
		            	dialog.dismiss();
		            	subjectTV.setText(names.get(position));
		            	currentSubject = projectlist.get(position).getCourseId();
		            	pageNo = 1;
		            	getData(pageNo);
		            }
		        });
		        dialog.setTitle("选择科目","#36BFB5");
		        dialog.show();
			}
		});
		setImmerseLayout(top_bar1);
		getCourseList();
		
	}
	
	/**
	 * 
	 */
	private void getCourseList() {
		courseMap = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		courseMap.put("studentId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
		try {
			courseResultJson = mRequestWithCache.getRseponse(UrlUtil.getCourseList(ECApplication.getInstance().getV3Address(), courseMap), new RequestWithCacheGet.RequestListener() {

				@Override
				public void onResponse(String response) {
					if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
						Log.i("新数据:" + response);
						refreshCourse(response);
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
		if ((courseResultJson != null && !courseResultJson.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:" + courseResultJson);
			refreshCourse(courseResultJson);
		}
	}

	private void refreshCourse(String response) {
		List<HomeWorkCourse> hwc = new Gson().fromJson(response, new TypeToken<List<HomeWorkCourse>>() {}.getType());
		if (hwc != null) {
			for (int i = 0; i < hwc.size(); i++) {
				projectlist.add(new HomeWorkCourse(hwc.get(i).getCourseId(), hwc.get(i).getCourseName()));
			}
		}
		getData(pageNo);
	}
	 
	private void getData(int pageNo) {
		progressDialog.setPressText("正在获取数据");
		progressDialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("studentId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
		map.put("pageNo", new ParameterValue(pageNo + ""));
		map.put("courseId", new ParameterValue(currentSubject));
		try {
			switch (resultJson = mRequestWithCache.getRseponse(UrlUtil.getStudentHomeWork(ECApplication.getInstance()
					.getV3Address(), map), new RequestWithCacheGet.RequestListener() {

				@Override
				public void onResponse(String response) {
					if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
						Log.i("新数据:" + response);
						refreshData(response);
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {

				}
			})) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((resultJson != null && !resultJson
				.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:" + resultJson);
			refreshData(resultJson);
		}
	}

	/**
	 * @param resultJson2
	 */
	private void refreshData(String resultJson2) {
		progressDialog.dismiss();
		if(resultJson2.contains("<html>")){
			ToastUtil.showMessage("数据异常");
			return;
		}
		if (StringUtil.isBlank(resultJson2)||"[]".equals(resultJson2)) {
			listView.finishLoading();
			listView.setLoadmoreVisible(false);
		}
		if (pageNo == 1) {
			allDataList.clear();
		} else {
			listView.finishLoading();
		}
		Gson gson = new Gson();
		newDataList = gson.fromJson(resultJson2, new TypeToken<List<HomeWork>>() {
		}.getType());
		if (newDataList != null && newDataList.size() != 0) {
			allDataList.addAll(newDataList);
		} else {
			if (pageNo == 1) {
				allDataList.clear();
				adapter = new StudentListAdapter();
				listView.setAdapter(adapter);
			}
			return;
		}
		if (pageNo == 1) {
			if(allDataList.size() < 50) {
				listView.setLoadmoreVisible(false);
			}
			adapter = new StudentListAdapter();
			listView.setAdapter(adapter);
		} else {
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	
	public class StudentListAdapter extends BaseAdapter {
		private String publishTime, times1;

		public StudentListAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return allDataList.size();
		}

		@Override
		public HomeWork getItem(int position) {
			return allDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			GradeHolders holder = null;
			if (view == null) {
				holder = new GradeHolders();
				view = LayoutInflater.from(context).inflate(
						R.layout.list_item_homework, null);
				holder.dayTV = (TextView) view.findViewById(R.id.dayTV);
				holder.monthTV = (TextView) view.findViewById(R.id.monthTV);
				holder.weekTV = (TextView) view.findViewById(R.id.weekTV);
				holder.subject = (TextView) view.findViewById(R.id.subject);
				holder.time = (TextView) view.findViewById(R.id.time);
				holder.content = (TextView) view.findViewById(R.id.neirong);
				holder.dateLay = (LinearLayout) view.findViewById(R.id.dateLay);
				holder.statusTV = (TextView) view.findViewById(R.id.statusTV);
				view.setTag(holder);
			} else {
				holder = (GradeHolders) view.getTag();
			}
			publishTime = getItem(position).getSetTime();
			holder.subject.setText(getItem(position).getCourseName());
			holder.time.setText(getItem(position).getEndTime());
			holder.content.setText(getItem(position).getTitle());
			holder.statusTV.setText(getItem(position).getStatusName());
			if (HomeWork.STATE_NO.equals(getItem(position).getStatus())) {
				holder.statusTV.setTextColor(Color.parseColor("#FF0000"));
			} else if (HomeWork.STATE_SUBMIT.equals(getItem(position).getStatus())){
				holder.statusTV.setTextColor(Color.parseColor("#36BFB5"));
			} else if (HomeWork.STATE_OVER.equals(getItem(position).getStatus())){
				holder.statusTV.setTextColor(Color.parseColor("#999999"));
			}

			if (position == 0) {
				String[] times = publishTime.split(" ");
				times1 = times[0];
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String str = formatter.format(curDate);
				if (times1.equals(str)) {
					holder.dayTV.setText("今天");
					holder.monthTV.setText("");
					holder.weekTV.setText("");
					holder.dateLay.setVisibility(View.VISIBLE);
				} else {
					Date data3 = DateUtil.getDate(times[0]);
					holder.dayTV.setText(data3.getDate() < 10 ? "0" + data3.getDate() : data3.getDate() + "");
					holder.monthTV.setText(data3.getMonth() + 1 + "月");
					holder.weekTV.setText(DateUtil.getWeekStr(times[0]));
					holder.dateLay.setVisibility(View.VISIBLE);
				}

			} else if (position > 0) {
				if (publishTime != null) {
					String[] time = publishTime.split(" ");
					Date data = DateUtil.getDate(time[0]);
					holder.dayTV.setText(data.getDate() < 10 ? "0" + data.getDate() : data.getDate() + "");
					holder.monthTV.setText(data.getMonth() + 1 + "月");
					holder.weekTV.setText(DateUtil.getWeekStr(time[0]));
					String[] beforeDate = getItem(position-1)
							.getSetTime().split(" ");
					if (time[0].equals(beforeDate[0])) {
						holder.dateLay.setVisibility(View.INVISIBLE);
					} else {
						holder.dateLay.setVisibility(View.VISIBLE);
					}
				}
			}
			StudentListAdapter.this.notifyDataSetChanged();
			return view;
		}

		class GradeHolders {
			TextView dayTV, monthTV, weekTV, subject, time, content,statusTV;
			LinearLayout dateLay;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 999) {
			allDataList.get(checkDetailPosition).setStatus(HomeWork.STATE_SUBMIT);
			allDataList.get(checkDetailPosition).setStatusName("已完成");
			adapter.notifyDataSetChanged();
		}
	}
}
