package zhwx.ui.imapp.notice;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.LayoutAnimationUtil;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.cakeview.CakeSurfaceView;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.imapp.notice.model.ReadDetail;

public class StatisticsActivity extends BaseActivity {
	
	private Activity context;
	
	private CakeSurfaceView cakeSurfaceView;
	
	private List<CakeSurfaceView.CakeValue> cakeValues2;
	
	private TextView conuntTV,kindTV;
	
	private String id;

	private HashMap<String, ParameterValue> map;
	
	private String json;
	
	private Handler handler = new Handler();
	
	private ECProgressDialog progressDialog;
	
	private ListView detailLV;
	
	private ReadDetail detail;
	
	private TextView copyUnReadTV;
	
	private List<ReadDetail.NameAndTime> currList = new ArrayList<ReadDetail.NameAndTime>();
	
	private LinearLayout readLay,unReadLay;
	
	private FrameLayout top_bar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar);
		id = getIntent().getStringExtra("id");
		conuntTV = (TextView) findViewById(R.id.conuntTV);
		detailLV = (ListView) findViewById(R.id.detailLV);
		kindTV = (TextView) findViewById(R.id.kindTV);
		copyUnReadTV = (TextView) findViewById(R.id.copyUnReadTV);
		readLay = (LinearLayout) findViewById(R.id.readLay);
		readLay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				currList = detail.getReadList();
				kindTV.setText("已读人员");
				copyUnReadTV.setVisibility(View.INVISIBLE);
				detailLV.setAdapter(new myAdapter());
				detailLV.setLayoutAnimation(LayoutAnimationUtil.getVerticalListAnim());
			}
		});
		unReadLay = (LinearLayout) findViewById(R.id.unReadLay);
		unReadLay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				currList = detail.getUnReadList();
				kindTV.setText("未读人员");
				copyUnReadTV.setVisibility(View.VISIBLE);
				detailLV.setAdapter(new myAdapter());
				detailLV.setLayoutAnimation(LayoutAnimationUtil.getVerticalListAnim());
			}
		});
		getData();
	}

	private void getData() {
		progressDialog = new ECProgressDialog(this);
		progressDialog.setPressText("数据获取中");
		progressDialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance()
				.getLoginMap();
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.getReadDetailData(ECApplication.getInstance().getAddress(), map);
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							refreshData(json);
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
	 * @param json2
	 */
	protected void refreshData(String json2) {
		System.out.println(json2);
		if(json2.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		detail = new Gson().fromJson(json2, ReadDetail.class);
		int readSize = detail.getReadList().size();
		int unreadSize = detail.getUnReadList().size();
		int totle = readSize+unreadSize;
		conuntTV.setText("共发送给"+ totle +"人");
		cakeValues2 = new ArrayList<CakeSurfaceView.CakeValue>();
		cakeValues2.add(new CakeSurfaceView.CakeValue("已读",readSize,""));
		cakeValues2.add(new CakeSurfaceView.CakeValue("未读",unreadSize,""));
		cakeSurfaceView = (CakeSurfaceView) findViewById(R.id.cakeSurfaceView1);
		cakeSurfaceView.setData(cakeValues2);
		cakeSurfaceView.setShowDecimals(false);
		cakeSurfaceView.setDetailTopSpacing(0);
		cakeSurfaceView.setOnItemClickListener(new CakeSurfaceView.OnItemClickListener() {

			@Override
			public void onItemClick(int position) {
				if (position == 0) {
					currList = detail.getReadList();
					kindTV.setText("已读人员");
					copyUnReadTV.setVisibility(View.INVISIBLE);
				} else {
					currList = detail.getUnReadList();
					kindTV.setText("未读人员");
					copyUnReadTV.setVisibility(View.VISIBLE);
				}
				detailLV.setAdapter(new myAdapter());
				detailLV.setLayoutAnimation(LayoutAnimationUtil.getVerticalListAnim());
			}
		});
		progressDialog.dismiss();
		if(detail.getReadList().size() != 0) {
			kindTV.setText("已读人员");
			currList = detail.getReadList();
		} else {
			kindTV.setText("未读人员");
			currList = detail.getUnReadList();
		}
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				cakeSurfaceView.drawCakeByAnim();
				detailLV.setAdapter(new myAdapter());
				detailLV.setLayoutAnimation(LayoutAnimationUtil.getVerticalListAnim());
			}
		}, 300);
	}
	
	public class myAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return currList.size();
		}

		@Override
		public ReadDetail.NameAndTime getItem(int position) {
			return currList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = View.inflate(context, R.layout.list_item_notice_statistics, null);
				holder.nameTV = (TextView) convertView.findViewById(R.id.nameTV);
				holder.timeTV = (TextView) convertView.findViewById(R.id.timeTV);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.nameTV.setText(getItem(position).getName());
			holder.timeTV.setText(getItem(position).getReadingTime()==null?"":getItem(position).getReadingTime());
//			if(getItem(position).getReadingTime()==null) {
//				holder.nameTV.setTextColor(Color.parseColor("#e64f5a"));
//			} else {
//				holder.nameTV.setTextColor(Color.parseColor("#5ec480"));
//			}
			return convertView;
		}
		
		class Holder{
			private TextView nameTV;
			private TextView timeTV;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void onCopyUnReadUsers(View v) {
		System.out.println("formArry().trim()");
		ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(formArry().trim());
		if(StringUtil.isNotBlank(cmb.getText().toString().trim())) {
			ToastUtil.showMessage("未读人员名单已复制到剪切板");
		}
	}
	
	public String formArry() {
		String result = "";
		if(currList.size() == 0) {
			return result;
		}
		for (ReadDetail.NameAndTime nat : currList) {
			result += nat.getName() + ",";
		}
		result = result.substring(0, result.lastIndexOf(","));
		return result;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_notice_statistics;
	}

}
