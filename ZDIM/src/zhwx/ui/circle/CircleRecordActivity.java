package zhwx.ui.circle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.HashMap;
import java.util.List;

import zhwx.Constant;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.MomentRecord;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.dialog.ECProgressDialog;

/**
 * 评论和赞
 * @author Li.Xin
 * @time 2015-10-28 16:38:04
 *
 */
public class CircleRecordActivity extends BaseActivity {
	
	private Activity context;
	
	private Handler handler = new Handler();
	
	private ListView recordLV;
	
	private HashMap<String, ParameterValue> map;
	
	private String kind = "0";
	
	private List<MomentRecord> records;
	
	private ImageLoader mImageLoader;
	
	private ECProgressDialog mPostingdialog;
	
	private FrameLayout top_bar;
	
	private boolean isAll = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		mImageLoader = new ImageLoader(context);
		getTopBarView().setVisibility(View.GONE);
		kind = getIntent().getStringExtra("kind");
		isAll = getIntent().getBooleanExtra("isAll", false);
		initView();
		setImmerseLayout(top_bar);
		getData();
	}
	
	private void getData() {
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		map.put("kind", new ParameterValue(kind));
		if(Constant.CIRCLE_KIND_SCHOOL.equals(kind)) {
			if (isAll) {
				map.put("time", new ParameterValue("1970-01-01 00:00:00"));
			} else {
				map.put("time", new ParameterValue(ECApplication.getInstance().getLastSchoolCircleRecordTime()));
			}
		} else if (Constant.CIRCLE_KIND_CLASS.equals(kind)) {
			if (isAll) {
				map.put("time", new ParameterValue("1970-01-01 00:00:00"));
			} else {
				map.put("time", new ParameterValue(ECApplication.getInstance().getLastClassCircleRecordTime()));
			}
		}
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String json = UrlUtil.getNewMomentRecordList(ECApplication.getInstance().getAddress(), map).trim();
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(json);
						}
					}, 5);
				} catch (Exception e) {
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
	
	private void refreshData(String json) {
		System.out.println(json);
		if(!json.contains("<html>")){
			records = new Gson().fromJson(json, new TypeToken<List<MomentRecord>>(){}.getType());
			if(records.size()!=0){
				if(Constant.CIRCLE_KIND_SCHOOL.equals(kind)){
					ECApplication.getInstance().saveLastSchoolCircleRecordTime(records.get(0).getTime());
					//TODO
//					MainActivity.counts.get(2).setCount("0");
				} else if (Constant.CIRCLE_KIND_CLASS.equals(kind)) {
					ECApplication.getInstance().saveLastClassCircleRecordTime(records.get(0).getTime());
//					MainActivity.counts.get(3).setCount("0");
				}
			}
			recordLV.setAdapter(new MomentRecordAdapter());
		}else{
			ToastUtil.showMessage("返回数据异常");
		}
		mPostingdialog.dismiss();
	}
	
	private void initView() {
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		recordLV = (ListView) findViewById(R.id.recordLV);
		recordLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				MomentRecord value = (MomentRecord) parent.getAdapter().getItem(position);
				if(value!=null){
					startActivity(new Intent(context, CircleDetailActivity.class).putExtra("momentId", value.getMomentId()).putExtra("kind", kind));
				}
			}
		});
	}
	
	public class MomentRecordAdapter extends BaseAdapter {

		@SuppressWarnings("unchecked")
		public MomentRecordAdapter(Context context) {
			super();
		}

		public MomentRecordAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return records.size();
		}

		@Override
		public MomentRecord getItem(int position) {
			return records.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_circle_record, null);
				holder = new ViewHolder();
				holder.userNameTV = (TextView) convertView.findViewById(R.id.userNameTV);
				holder.timeTV = (TextView) convertView.findViewById(R.id.timeTV);
				holder.contentTV = (TextView) convertView.findViewById(R.id.contentTV);
				holder.momentContentTV = (TextView) convertView.findViewById(R.id.momentContentTV);
				holder.headIV = (ImageView) convertView.findViewById(R.id.headIV);
				holder.momentContentIV = (ImageView) convertView.findViewById(R.id.contentIV);
				holder.thumbUpIV = (ImageView) convertView.findViewById(R.id.thumbUpIV);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.userNameTV.setText(getItem(position).getUserName());
			holder.contentTV.setText(getItem(position).getContent());
			holder.timeTV.setText(getItem(position).getDisplayTime());
			
			if (StringUtil.isNotBlank(getItem(position).getPicUrl())) {
				holder.momentContentTV.setVisibility(View.INVISIBLE);
				holder.momentContentIV.setVisibility(View.VISIBLE);
				mImageLoader.DisplayImage(ECApplication.getInstance().getAddress() + getItem(position).getPicUrl(), holder.momentContentIV,
						false);
			} else {
				holder.momentContentTV.setVisibility(View.VISIBLE);
				holder.momentContentIV.setVisibility(View.INVISIBLE);
				holder.momentContentTV.setText(getItem(position).getMomentContent());
			}
			if (Constant.RECORD_KIND_THUMBUP.equalsIgnoreCase(getItem(position).getKind())) {
				holder.thumbUpIV.setVisibility(View.VISIBLE);
				holder.contentTV.setVisibility(View.INVISIBLE);
			} else {
				holder.thumbUpIV.setVisibility(View.INVISIBLE);
				holder.contentTV.setVisibility(View.VISIBLE);
			}
			mImageLoader.DisplayImage(ECApplication.getInstance().getAddress() + getItem(position).getHeadUrl(), holder.headIV,
					false);
			return convertView;
		}

		private class ViewHolder {
			private TextView userNameTV, contentTV, timeTV, momentContentTV;
			private ImageView headIV, momentContentIV,thumbUpIV;
		}
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_circlerecord;
	}
}
