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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.storeroom.model.MyApplyDetail;

/**   
 * @Title: AMainActivity.java 
 * @Package com.lanxum.hzth.im.ui.v3.assets 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午9:52:07 
 */
public class ApplyDetailActivity extends BaseActivity implements OnClickListener {
	
	private Activity context;
	
	private HashMap<String, ParameterValue> map;
	
	private String indexJson;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private ListView mystoreLV;
	
	private String id = "";
	
	private MyApplyDetail detail;
	
	private TextView smCodeTV,departmentNameTV,checkUserTV,smApplyDateTV,getKindTV,reasonTV,beizhuTV,checkStatusViewTV;
	
	
    @Override
	protected int getLayoutId() {return R.layout.activity_sm_applydetail;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"申请详情", this);
		id = getIntent().getStringExtra("id");
		initView();
		getData();
	}
	
	/**
	 * 
	 */
	private void initView() {
 		mystoreLV = (ListView) findViewById(R.id.mystoreLV);
 		smCodeTV = (TextView) findViewById(R.id.smCodeTV);
 		departmentNameTV = (TextView) findViewById(R.id.departmentNameTV);
 		checkUserTV = (TextView) findViewById(R.id.checkUserTV);
 		smApplyDateTV = (TextView) findViewById(R.id.smApplyDateTV);
 		getKindTV = (TextView) findViewById(R.id.getKindTV);
 		reasonTV = (TextView) findViewById(R.id.reasonTV);
 		beizhuTV = (TextView) findViewById(R.id.beizhuTV);
 		checkStatusViewTV = (TextView) findViewById(R.id.checkStatusViewTV);
	}

	private void getData() {
		getNotice();   //获取公告板数据
	}
	
	private void getNotice(){
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getApplyRecordView(ECApplication.getInstance().getV3Address(), map);
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
		detail = new Gson().fromJson(indexJson, MyApplyDetail.class);
		smCodeTV.setText(detail.getApplyreceiverecord().get(0).getCode());
		departmentNameTV.setText(detail.getApplyreceiverecord().get(0).getDepartmentName());
		checkUserTV.setText(detail.getApplyreceiverecord().get(0).getDeptCheckUser()); //部门审核人
		smApplyDateTV.setText(detail.getApplyreceiverecord().get(0).getApplyDate());
		getKindTV.setText(detail.getApplyreceiverecord().get(0).getKindValue());
		reasonTV.setText(detail.getApplyreceiverecord().get(0).getReason());
		beizhuTV.setText(detail.getApplyreceiverecord().get(0).getNote());
		
		String str = detail.getApplyreceiverecord().get(0).getDeptCheckStatus();
		if (StringUtil.isNotBlank(detail.getApplyreceiverecord().get(0).getZwCheckStatus())) {
			str += ","  + detail.getApplyreceiverecord().get(0).getZwCheckStatus();
		}
		checkStatusViewTV.setText(str);
		mystoreLV.setAdapter(new OrderListAdapter());
		Tools.setListViewHeightBasedOnChildren(mystoreLV);
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
			return detail.getApplygoodsList().size()+1;
		}

		@Override
		public MyApplyDetail.ApplygoodsListBean getItem(int position) {
			return detail.getApplygoodsList().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sm_apply_detail, null);
				holder = new ViewHolder();
				holder.goodsNameTV = (TextView) convertView.findViewById(R.id.goodsNameTV);
				holder.goodsCountTV = (TextView) convertView.findViewById(R.id.goodsCountTV);
				holder.costTV = (TextView) convertView.findViewById(R.id.costTV);
				holder.linearLayout1 = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if(position == 0) {
				holder.goodsNameTV.setText("物品名称");
				holder.goodsCountTV.setText("数量");
				holder.costTV.setText("金额");
			} else {
				holder.goodsNameTV.setText(getItem(position - 1).getGoodsInfoName());
				holder.goodsCountTV.setText(getItem(position - 1).getCount() + "");
				holder.costTV.setText(getItem(position - 1).getMoney() + "");
			}
			
			if (position % 2 == 0) {
				holder.linearLayout1.setBackgroundColor(Color.parseColor("#f6fbff"));
			} else {
				holder.linearLayout1.setBackgroundColor(Color.parseColor("#ffffff"));
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
			private TextView goodsNameTV,goodsCountTV,costTV;
			private LinearLayout linearLayout1;
		}
		
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
