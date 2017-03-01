package zhwx.ui.dcapp.score;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.HashMap;

import volley.Response;
import volley.VolleyError;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.Log;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.score.model.AllExamInfo;
import zhwx.ui.dcapp.score.model.AllTermInfo;
import zhwx.ui.dcapp.score.model.StudentScoreInfo;

/**   
 * @Title: MyScoreActivity.java 
 * @Package com.zdhx.edu.im.ui.v3.score 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ zdhx
 * @date 2016年7月15日 下午12:30:37 
 */
public class MyScoreActivity extends BaseActivity{
	
	private Activity context;
	
	private FrameLayout top_bar;
	
	private RequestWithCacheGet mRequestWithCache;
	
	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private String termJson;
	private String examJson;
	private String scoreJson;
	
	private ArrayList<AllTermInfo> mAllTerminfos = new ArrayList<AllTermInfo>();// 学期
	private ArrayList<AllExamInfo> mAllExaminfos = new ArrayList<AllExamInfo>();// 考试
	private ArrayList<StudentScoreInfo> mAllScoreinfos = new ArrayList<StudentScoreInfo>();// 分数
	
	private Spinner termSP,examSP;
	
	private ListView scoreLV;
	
	private TextView emptyTV;
	
	private LinearLayout scrollLay;
	
	private int mMinHeaderTranslation;
	
	private View mPlaceHolderView;
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_score_main;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		context = this;
		mRequestWithCache = new RequestWithCacheGet(context);
		initView();
		mMinHeaderTranslation = - getResources().getDimensionPixelSize(R.dimen.month_select_circle_radius);
		getTermData();
		setImmerseLayout(top_bar);
	}
	
	/**
	 * 
	 */
	private void initView() {
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		termSP = (Spinner) findViewById(R.id.termSP);
		examSP = (Spinner) findViewById(R.id.examSP);
		scoreLV = (ListView) findViewById(R.id.scoreV);
		emptyTV = (TextView) findViewById(R.id.emptyTV);
		scrollLay = (LinearLayout) findViewById(R.id.scrollLay);
		scoreLV.setEmptyView(emptyTV);
		mPlaceHolderView = getLayoutInflater().inflate(R.layout.view_header_placeholder, scoreLV, false);
		scoreLV.addHeaderView(mPlaceHolderView);
		scoreLV.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				 int scrollY = getScrollY();
	             scrollLay.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
			}
		});
	}
	
    public int getScrollY() {
	        View c = scoreLV.getChildAt(0);
	        if (c == null) {
	            return 0;
	        }
	        int firstVisiblePosition = scoreLV.getFirstVisiblePosition();
	        int top = c.getTop();

	        int headerHeight = 0;
	        if (firstVisiblePosition >= 1) {
	            headerHeight = scoreLV.getHeight();
	        }
	        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
	}
    
	private void getTermData(){

		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		try {
			termJson = mRequestWithCache.getRseponse(UrlUtil.getSchoolTermAllJsonUrl(ECApplication.getInstance().getV3Address(),map),
					new RequestWithCacheGet.RequestListener() {

						@Override
						public void onResponse(String response) {
							if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
								Log.i("新数据:" + response);
								refreshTermData(response);
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

		if ((termJson != null && !termJson.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:" + termJson);
			refreshTermData(termJson);
		}
	}
	
	private void refreshTermData(String response) {
		mAllTerminfos = new Gson().fromJson(response, new TypeToken<ArrayList<AllTermInfo>>() {}.getType());
		termSP.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				AllTermInfo term = (AllTermInfo) parent.getAdapter().getItem(position);
				getExamData(term.getId());
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		termSP.setAdapter(new TermSpinnerAdapter(context, mAllTerminfos));
		
		for (int i = 0; i < mAllTerminfos.size(); i++) {
			if ("1".equals(mAllTerminfos.get(i).getSTATUS_CURRENT())) {
				termSP.setSelection(i);
				getExamData(mAllTerminfos.get(i).getId());
			}
		}
	}
	
	private void getExamData(String termId){

		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("schoolTermId", new ParameterValue(termId));
		try {
			examJson = mRequestWithCache.getRseponse(UrlUtil.getExamListJsonWithSchoolTermIdJsonUrl(ECApplication.getInstance().getV3Address(),map),
					new RequestWithCacheGet.RequestListener() {

						@Override
						public void onResponse(String response) {
							if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
								Log.i("新数据:" + response);
								refreshExamData(response);
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

		if ((examJson != null && !examJson.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:" + examJson);
			refreshExamData(examJson);
		}
	}

	/**
	 * @param examJson2
	 */
	private void refreshExamData(String examJson2) {
		mAllExaminfos = new Gson().fromJson(examJson2, new TypeToken<ArrayList<AllExamInfo>>() {}.getType());
		examSP.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				AllExamInfo exam = (AllExamInfo) parent.getAdapter().getItem(position);
				getScore(exam.getId());
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		examSP.setAdapter(new ExamSpinnerAdapter(context, mAllExaminfos));
		if (mAllExaminfos.size()!=0) {
			getScore(mAllExaminfos.get(0).getId());
		}
	}

	/**
	 * @param id
	 */
	private void getScore(String id) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("examId", new ParameterValue(id));
		map.put("studentId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));

		try {
			scoreJson = mRequestWithCache.getRseponse(UrlUtil.getStudentScoreJsonUrl(ECApplication.getInstance().getV3Address(),map),
					new RequestWithCacheGet.RequestListener() {

						@Override
						public void onResponse(String response) {
							if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
								Log.i("新数据:" + response);
								refreshScoreData(response);
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

		if ((scoreJson != null && !scoreJson.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:" + scoreJson);
			refreshScoreData(scoreJson);
		}
	}

	/**
	 * @param scoreJson2
	 */
	private void refreshScoreData(String scoreJson2) {
		mAllScoreinfos = new Gson().fromJson(scoreJson2, new TypeToken<ArrayList<StudentScoreInfo>>() {}.getType());
		scoreLV.setAdapter(new StudentScoreAdapter());
	}
	
	public class StudentScoreAdapter extends BaseAdapter {
		private String publishTime, times1;

		public StudentScoreAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return mAllScoreinfos.size();
		}

		@Override
		public StudentScoreInfo getItem(int position) {
			return mAllScoreinfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			GradeHolders holder = null;
			if (view == null) {
				holder = new GradeHolders();
				view = LayoutInflater.from(context).inflate( R.layout.list_item_student_score, null);
				holder.nameTV = (TextView) view.findViewById(R.id.nameTV);
				holder.scoreTV = (TextView) view.findViewById(R.id.scoreTV);
				holder.classRankTV = (TextView) view.findViewById(R.id.classRankTV);
				holder.greadRankTV = (TextView) view.findViewById(R.id.greadRankTV);
				holder.mainLay = (LinearLayout) view.findViewById(R.id.mainLay);
				view.setTag(holder);
			} else {
				holder = (GradeHolders) view.getTag();
			}
//			holder.nameTV.setText("aaa");
//			holder.scoreTV.setText("aaa");
//			holder.classRankTV.setText("aaa");
//			holder.greadRankTV.setText("aaa");		
			if (position%2==0) {
				holder.mainLay.setBackgroundColor(0);
			} else {
				holder.mainLay.setBackgroundColor(getResources().getColor(R.color.text_light_gray));
			}
			holder.nameTV.setText(getItem(position).getCourseName());
			holder.scoreTV.setText(getItem(position).getScore());
			holder.classRankTV.setText(getItem(position).getEclassRank());
			holder.greadRankTV.setText(getItem(position).getGradeRank());		
			return view;
		}

		class GradeHolders {
			TextView nameTV,scoreTV,classRankTV,greadRankTV;
			LinearLayout mainLay;
		}
	}
}
