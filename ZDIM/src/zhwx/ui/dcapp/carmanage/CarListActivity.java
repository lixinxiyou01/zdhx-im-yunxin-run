package zhwx.ui.dcapp.carmanage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.dialog.ECListDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.carmanage.model.CarInfo;

/**   
 * @Title: CarListActivity.java 
 * @Package zhwx.ui.dcapp.carmanage
 * @author Li.xin @ 中电和讯
 * @date 2016-3-28 上午10:40:08 
 */
public class CarListActivity extends BaseActivity {
	
	private Activity context;
	
	private FrameLayout top_bar;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private ImageLoader mImageLoader;
	
	private String carListJson;
	
	private HashMap<String, ParameterValue> map;
	
	private ListView carLV;
	
	private LinearLayout changeTypeLay;
	
	private TextView schoolNameTV;
	
	private ImageView flagIV;
	
	private TextView emptyTV;
	
	private TextView assignCountTV,noAssignCountTV;
	
	private String orderId;
	
	private CarInfo carInfo = new CarInfo();
	
	private int schoolPosition = 0;
	
	private Button zdBT;
	
	private int SENDCAR_CODE = 105;
	
	private CarListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		mImageLoader = new ImageLoader(context);
		getTopBarView().setVisibility(View.GONE);
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar);
		emptyTV = (TextView) findViewById(R.id.emptyTV);
		assignCountTV = (TextView) findViewById(R.id.assignCountTV);
		noAssignCountTV = (TextView) findViewById(R.id.noAssignCountTV);
		schoolNameTV = (TextView) findViewById(R.id.schoolNameTV);
		carLV = (ListView) findViewById(R.id.carLV);
		carLV.setEmptyView(emptyTV);
		zdBT = (Button) findViewById(R.id.zdBT);
		zdBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ToastUtil.showMessage("暂未开通");
			}
		});
		orderId = getIntent().getStringExtra("orderId");
		getCarInfo();
	}

	private void getCarInfo(){
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(orderId));
		map.put("operationCode", new ParameterValue("carmanage"));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					carListJson = UrlUtil.getCarsInfo(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(carListJson);
						}

					}, 5);
				} catch (IOException e) {
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
	
	private void refreshData(String carListJson) {
		carInfo = new Gson().fromJson(carListJson, CarInfo.class);
		assignCountTV.setText(carInfo.getAssignCount());
		noAssignCountTV.setText(carInfo.getNoAssignCount());
		if (carInfo.getSchoolsData().size() > 0 ) {
			schoolNameTV.setText(carInfo.getSchoolsData().get(schoolPosition).getSchoolName());
			if (!carInfo.getSchoolsData().get(schoolPosition).isScope()) {
        		zdBT.setVisibility(View.VISIBLE);
        	} else {
        		zdBT.setVisibility(View.INVISIBLE);
        	}
		}
		for (int i = 0; i < carInfo.getSchoolsData().size(); i++) {
			//TODO 如果是当前用校区  更改position
			if (carInfo.getSchoolsData().get(i).isCurrSchool()) {
				schoolPosition = i;
			}
		}
		adapter = new CarListAdapter();
		carLV.setAdapter(adapter);
		mPostingdialog.dismiss();
	}
	
	public class CarListAdapter extends BaseAdapter {
		
		public CarListAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return carInfo.getSchoolsData().get(schoolPosition).getCarData().size();
		}

		@Override
		public CarInfo.CarData getItem(int position) {
			return carInfo.getSchoolsData().get(schoolPosition).getCarData().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_cm_car, null);
				holder = new ViewHolder();
				holder.carNameTV = (TextView) convertView.findViewById(R.id.carNameTV);
				holder.carNumTV = (TextView) convertView.findViewById(R.id.carNumTV);
				holder.limitCountTV = (TextView) convertView.findViewById(R.id.limitCountTV);
				holder.isFullTV = (TextView) convertView.findViewById(R.id.isFullTV);
				holder.assignCountTV = (TextView) convertView.findViewById(R.id.assignCountTV);
				holder.carImgIV = (ImageView) convertView.findViewById(R.id.carImgIV);
				holder.useThisBT = (Button) convertView.findViewById(R.id.useThisBT);
				holder.buttonLay = (RelativeLayout) convertView.findViewById(R.id.buttonRLay);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.carNameTV.setText(getItem(position).getCarName());
			holder.carNumTV.setText(getItem(position).getCarNum());
			holder.limitCountTV.setText("限乘" + getItem(position).getLimitCount() + "人");
			holder.useThisBT.setEnabled(!getItem(position).isFullFlag());
			holder.isFullTV.setVisibility(getItem(position).isFullFlag()?View.VISIBLE:View.INVISIBLE);
			holder.assignCountTV.setText("今日已派车" + getItem(position).getAssignCount() + "次");
			holder.carImgIV.setImageResource(R.drawable.icon_carmanager);
			mImageLoader.DisplayImage(ECApplication.getInstance().getV3Address()+getItem(position).getCarPicUrl(), holder.carImgIV, false);
			if (carInfo.getSchoolsData().get(schoolPosition).isScope()) {
				holder.buttonLay.setVisibility(View.VISIBLE);
			} else {
				holder.buttonLay.setVisibility(View.GONE);
			}
			addListener(holder, position, convertView);
			return convertView;
		}

		/**
		 * holerView 添加监听器
		 * @param holder
		 * @param position
		 * @param view
		 */
		private void addListener(final ViewHolder holder, final int position,
				final View view) {
			holder.useThisBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(Integer.parseInt(carInfo.getNoAssignCount())!=0) {
						Intent intent = new Intent(context, SendCarActivity.class);
						Bundle bundle = new Bundle();
						intent.putExtra("data", bundle);
						intent.putExtra("orderId", orderId);
						bundle.putSerializable("carInfo", getItem(position));
						startActivityForResult(intent, SENDCAR_CODE);
					} else {
						ToastUtil.showMessage("已完成派车");
					}
				}
			});
		}
		
		private class ViewHolder {
			private TextView carNameTV,carNumTV,limitCountTV,isFullTV,assignCountTV;
			private Button useThisBT;
			private ImageView carImgIV;
			private RelativeLayout buttonLay;
		}
		
	}
	
	public void onSelectSchool(View v){
		final List<String> names = new ArrayList<String>();
		for (int i = 0; i < carInfo.getSchoolsData().size(); i++) {
			names.add(carInfo.getSchoolsData().get(i).getSchoolName());
		}
		final ECListDialog dialog;
		dialog = new ECListDialog(this , names ,schoolPosition);
        dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
            @Override
            public void onDialogItemClick(Dialog d, int position) {
            	schoolPosition = position;
            	dialog.dismiss();
            	schoolNameTV.setText(names.get(position));
            	if (!carInfo.getSchoolsData().get(schoolPosition).isScope()) {
            		zdBT.setVisibility(View.VISIBLE);
            	} else {
            		zdBT.setVisibility(View.INVISIBLE);
            	}
            	adapter.notifyDataSetChanged();
//            	carLV.setAdapter(new CarListAdapter());
            }
        });
        dialog.setTitle("选择校区","#f28d2b");
        dialog.show();
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SENDCAR_CODE) {
			getCarInfo();
			setResult(103);
		}
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_cm_carlist;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
