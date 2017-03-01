package zhwx.ui.dcapp.takecourse;

/* 
 * 项目名称：选课
 * 版本：v1.1
 * @author Lixin 2013-6-25 14:46:00
 * Copyright 北京合众天恒科技有限公司
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import volley.RequestQueue;
import volley.Response;
import volley.VolleyError;
import volley.toolbox.StringRequest;
import volley.toolbox.Volley;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.takecourse.listviewgroup.MailListView;
import zhwx.ui.dcapp.takecourse.listviewgroup.bean.Message;
import zhwx.ui.dcapp.takecourse.listviewgroup.bean.MyEletiveCourse;
import zhwx.ui.dcapp.takecourse.listviewgroup.interfaces.ItemClickedListener;


public class MyCourseActivity extends BaseActivity {
	private MailListView mListView;
	private MailAdapter mAdapter;
	private RequestQueue mRequestQueue;
	private FrameLayout top_bar;
	public static String ecActivityId;
	public static String ecActivityNote;
	public static String userId;
	public static String kind;
	private ElectiveCourseNote courseNote;
	private ECProgressDialog dialog;
	private TextView emptyTV;
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_takesourse_main;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog = new ECProgressDialog(this);
		getTopBarView().setVisibility(View.GONE);
		mRequestQueue = Volley.newRequestQueue(this);
		findViewById(R.id.choosecoursebt).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (StringUtil.isNotBlank(ecActivityId)) {
					startActivity(new Intent(MyCourseActivity.this, ChooseAcitivty.class));
				} else {
					ToastUtil.showMessage("暂无选课活动");
				}
			}
		});
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar);
		courseNote = (ElectiveCourseNote) getIntent().getSerializableExtra("courseNote");
		if (courseNote!=null) {
			ecActivityId = courseNote.getEcActivityId();
			ecActivityNote = courseNote.getEcActivityNote();
			userId = courseNote.getUserId();
			kind = courseNote.getKind();
		}
		if (StringUtil.isNotBlank(getIntent().getStringExtra("go"))) {
			startActivity(new Intent(MyCourseActivity.this, ChooseAcitivty.class));
		}
		mListView = (MailListView) findViewById(R.id.listview_main);
		emptyTV = (TextView) findViewById(R.id.emptyTV);
		mListView.setEmptyView(emptyTV);
	}

	private void downLoadDate() {
		dialog.setPressText("获取数据中");
		dialog.show();
		Map<String, ParameterValue> map = ECApplication.getInstance().getV3LoginMap();
		StringRequest sRequest = new StringRequest(UrlUtil.toMyElectiveCourse(ECApplication.getInstance().getV3Address(), map), new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				System.out.println(response);
				MyEletiveCourse myEletiveCourse = new Gson().fromJson(response, MyEletiveCourse.class);
				initListview(myEletiveCourse);
				dialog.dismiss();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				dialog.dismiss();
			}
		});
		mRequestQueue.add(sRequest);

	}
	@Override
	protected void onResume() {
		super.onResume();
		downLoadDate();
	}
	
	private void initListview(MyEletiveCourse myEletiveCourse) {

		List<Message> messages = new ArrayList<Message>();

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 5; j++) {
				Message msg = new Message();
				msg.setGroupName("Group" + i);
				msg.setInfo("info" + j);
				messages.add(msg);
			}
		}
		mAdapter = new MailAdapter(MyCourseActivity.this, myEletiveCourse, false);// 第三个参数是：第一次填充
																				// listview
																				// 时，分组是否展开
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickedListener(new ItemClickedListener() {

			@Override
			public void onItemClick(View item, int itemPosition) {
				MyEletiveCourse.schoolTermStudentCourseMap schoolTermStudentCourse = (MyEletiveCourse.schoolTermStudentCourseMap) mAdapter.getItem(itemPosition);
				Map<String, ParameterValue> map = ECApplication.getInstance().getV3LoginMap();
				map.put("ecActivityCourseId",new ParameterValue(schoolTermStudentCourse.getEcActivityCourseId()));
				String url = UrlUtil.getUrl(ECApplication.getInstance().getV3Address()+"/ec/mobile/ecMobileTerminal!info.action", map);
				Intent intent = new Intent(MyCourseActivity.this, CouseDetailActivity.class);
				intent.putExtra("webUrl", url);
				intent.putExtra("kind", CouseDetailActivity.KIND_UNSELECT);
				startActivity(intent);
			}
		});
	}
}
