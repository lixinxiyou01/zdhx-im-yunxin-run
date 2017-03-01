package zhwx.ui.dcapp.takecourse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.Map;

import volley.RequestQueue;
import volley.Response;
import volley.VolleyError;
import volley.toolbox.StringRequest;
import volley.toolbox.Volley;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.StringUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;


@SuppressLint("SetJavaScriptEnabled")
public class TackCourseIndexActivity extends BaseActivity {

	private Activity context;

	private RequestQueue mRequestQueue;

	private ElectiveCourseNote courseNote;

	private TextView noteTV;

	private Button checkBT;
	
	private Chronometer timer;
	
	private FrameLayout top_bar;

	// 倒计时;
	private static final int COUNTMAX = 5;
	
	private int countdownNum;
	
	private WebView webView1;
	
	private ECProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		dialog = new ECProgressDialog(context);
		getTopBarView().setVisibility(View.GONE);
		noteTV = (TextView) findViewById(R.id.noteTV);
		checkBT = (Button) findViewById(R.id.checkBT);
		webView1 = (WebView) findViewById(R.id.webView1);
		mRequestQueue = Volley.newRequestQueue(this);
		timer = (Chronometer) findViewById(R.id.chronometer);
		timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

			@Override
			public void onChronometerTick(Chronometer arg0) {
				if(countdownNum > 0) {
					checkBT.setText("开始选课(" + countdownNum + ")");
					checkBT.setEnabled(false);
				} else {
					checkBT.setText("开始选课");
					timer.stop();
					checkBT.setEnabled(true);
				}
				countdownNum--;
			}
		});
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar);
		getData();
	}
	
	public void getData() {
		dialog.setPressText("获取数据中");
		dialog.show();
		Map<String, ParameterValue> map = ECApplication.getInstance().getV3LoginMap();
		StringRequest sRequest = new StringRequest(
				UrlUtil.toElectiveCourseNote(ECApplication.getInstance()
						.getV3Address(), map), new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						System.out.println(response);
						if (StringUtil.isNotBlank(response)) {
							courseNote = new Gson().fromJson(response,ElectiveCourseNote.class);
							if (StringUtil.isNotBlank(courseNote.getEcActivityId())) {
								webView1.loadData(courseNote.getEcActivityNote(), "text/html; charset=UTF-8", null);
//								noteTV.setText(Html.fromHtml(courseNote.getEcActivityNote()));
								start();
							} else {
								Intent intent = new Intent(context, MyCourseActivity.class);
								intent.putExtra("courseNote", courseNote);
								startActivity(intent);
								finish();
							}
							dialog.dismiss();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						dialog.dismiss();
					}
				});
		mRequestQueue.add(sRequest);
	}
	
	public void start(){
		countdownNum = COUNTMAX;
		timer.start();
	}
	
	public void onTack(View v) {
		Intent intent = new Intent(context, MyCourseActivity.class);
		intent.putExtra("courseNote", courseNote);
		intent.putExtra("go", "go");
		startActivity(intent);
		finish();
	}
	
	public void onMe(View v) {
		Intent intent = new Intent(context, MyCourseActivity.class);
		intent.putExtra("courseNote", courseNote);
		startActivity(intent);
		finish();
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_choose_course_index;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
