package zhwx.ui.dcapp.storeroom;

/** code is far away from bug with the animal protecting
* 
*     ┏┓　　　┏┓
*   ┏┛┻━━━┛┻┓
*   ┃　　　　　　　┃ 　
*   ┃　　　━　　　┃
*   ┃　┳┛　┗┳　┃
*   ┃　　　　　　　┃
*   ┃　　　┻　　　┃
*   ┃　　　　　　　┃
*   ┗━┓　　　┏━┛
 *     　   　┃　　　┃神兽保佑
 *     　   　┃　　　┃永无BUG！
 *     　　   ┃　　　┗━━━┓
 *     　   　┃　　　　　　　┣┓
 *     　   　┃　　　　　　　┏┛
 *     　   　┗┓┓┏━┳┓┏┛
 *   　  　   　┃┫┫　┃┫┫
 *   　  　   　┗┻┛　┗┻┛
*
*/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
import zhwx.common.util.DensityUtil;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.storeroom.model.MyApply;

/**   
 * @Title: AMainActivity.java 
 * @Package com.lanxum.hzth.im.ui.v3.assets 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午9:52:07 
 */
public class MyApplyListActivity extends BaseActivity implements OnClickListener {
	
	private Activity context;
	
	private HashMap<String, ParameterValue> map;
	
	private String indexJson;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private ListView mystoreLV;
	
	private MyApply myApply;
	
    @Override
	protected int getLayoutId() {return R.layout.activity_sm_myapplylist;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"我的申领", this);
		initView();
		getData();
	}
	
	/**
	 * 
	 */
	private void initView() {
		mystoreLV = (ListView) findViewById(R.id.mystoreLV);
	}

	private void getData() {
		getNotice();   //获取公告板数据
	}
	
	private void getNotice(){
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(ECApplication.getInstance().getInstance().getCurrentIMUser().getV3Id()));
		map.put("pageObj.pageSize", new ParameterValue("1000"));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.MyApplyListData(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(indexJson);
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
	
	private void refreshData(String indexJson) {
		if (indexJson.contains("</html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		myApply = new Gson().fromJson(indexJson, MyApply.class);
		mystoreLV.setAdapter(new OrderListAdapter());
		mPostingdialog.dismiss();
	}
	
	public class OrderListAdapter extends BaseAdapter {
		
		public OrderListAdapter(Context context, List<AllAssets> list,
				int listFlag) {
			super();
		}

		public OrderListAdapter() {
			super();
		}

		@Override
		public int getCount() {
			return myApply.getGridModel().size();
		}

		@Override
		public MyApply.GridModelBean getItem(int position) {
			return myApply.getGridModel().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sm_myassets, null);
				holder = new ViewHolder();
				holder.smCodeTV = (TextView) convertView.findViewById(R.id.smCodeTV);
				holder.departmentNameTV = (TextView) convertView.findViewById(R.id.departmentNameTV);
				holder.smApplyDateTV = (TextView) convertView.findViewById(R.id.smApplyDateTV);
				holder.getKindTV = (TextView) convertView.findViewById(R.id.getKindTV);
				holder.checkStatusViewTV = (TextView) convertView.findViewById(R.id.checkStatusViewTV);
				holder.sendStatusViewTV = (TextView) convertView.findViewById(R.id.sendStatusViewTV);
				holder.buttonContentLay = (LinearLayout) convertView.findViewById(R.id.buttonContentLay);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.smCodeTV.setText(getItem(position).getCode());
			holder.departmentNameTV.setText(getItem(position).getDepartmentName());
			holder.smApplyDateTV.setText(getItem(position).getDate());
			holder.getKindTV.setText(getItem(position).getKindValue());
			holder.checkStatusViewTV.setText(getItem(position).getCheckStatusValue());
			holder.sendStatusViewTV.setText(getItem(position).getProvideStatusValue());
			if ("未发放".equals(getItem(position).getProvideStatusValue())) {
				holder.sendStatusViewTV.setTextColor(Color.RED);
			} else {
				holder.sendStatusViewTV.setTextColor(Color.parseColor("#3573a2"));
			}
			//动态添加操作按钮
			holder.buttonContentLay.removeAllViews();
			List<TextView> btns = getOrderButtonList(position);
			for (TextView button : btns) {
				holder.buttonContentLay.addView(button);
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
		}
		private class ViewHolder {
			private TextView smCodeTV,departmentNameTV,smApplyDateTV,getKindTV,sendStatusViewTV,checkStatusViewTV;
			private LinearLayout buttonContentLay;
		}
		
	}
	
	public List<TextView> getOrderButtonList(final int position){
		List<TextView> btnList = new ArrayList<TextView>();
		TextView ckBT = getOrderButton("查看");
		ckBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, ApplyDetailActivity.class).putExtra("id", myApply.getGridModel().get(position).getId()));
			}
		});
		btnList.add(ckBT);
		return btnList;
	}
	
	public TextView getOrderButton (String text) {
		LayoutParams params = new LayoutParams(
			    LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(30));
		params.setMargins(0, 0, DensityUtil.dip2px(10), 0);
		TextView button = new TextView(context);
		button.setText(text);
		button.setTextColor(Color.parseColor("#555555"));
		button.setBackgroundResource(R.drawable.btn_selector_ordercar);
		button.setGravity(Gravity.CENTER);
		button.setLayoutParams(params);
		return button;
	}
	
	/**申请资产*/
	public void onOrderCar(View v) {
		startActivity(new Intent(context, ApplyForSActivity.class));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		}
	}
}
