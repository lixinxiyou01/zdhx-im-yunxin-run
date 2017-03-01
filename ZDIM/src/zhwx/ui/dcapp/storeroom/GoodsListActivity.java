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
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.storeroom.model.Goods;

/**   
 * @Title: AMainActivity.java 
 * @Package com.lanxum.hzth.im.ui.v3.assets 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午9:52:07 
 */
public class GoodsListActivity extends BaseActivity implements OnClickListener {
	
	private Activity context;
	
	private HashMap<String, ParameterValue> map;
	
	private String indexJson;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private ListView mystoreLV;
	
	private  List<Goods.GridModelBean> allDataList;
	
	private String goodsKindId = "";
	
	private Goods goods;
	
    @Override
	protected int getLayoutId() {return R.layout.activity_sm_goodskind;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, "确定","物品列表", this);
		goodsKindId = getIntent().getStringExtra("kindId");
		initView();
		getData();
	}
	
	/**
	 * 
	 */
	private void initView() {
		mystoreLV = (ListView) findViewById(R.id.mystoreLV);
		mystoreLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				BaseAdapter adapter = (BaseAdapter) parent.getAdapter();
				ApplyForSActivity.putGoods(allDataList.get(position));
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void getData() {
		getNotice();   //获取公告板数据
	}
	
	private void getNotice(){
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(goodsKindId));
		map.put("pageObj.pageSize", new ParameterValue("1000"));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getGoodsInfoPage(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshJson(indexJson);
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
	
	private void refreshJson(String indexJson) {
		System.out.println(indexJson);
		if (indexJson.contains("</html>")) {
			ToastUtil.showMessage("数据错误");
			return;
		}
		goods = new Gson().fromJson(indexJson, Goods.class);
		allDataList = goods.getGridModel();
		mystoreLV.setAdapter(new OrderListAdapter());
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
			return allDataList.size();
		}

		@Override
		public Goods.GridModelBean getItem(int position) {
			return allDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sm_goods, null);
				holder = new ViewHolder();
				holder.kindNameTV = (TextView) convertView.findViewById(R.id.kindNameTV);
				holder.checkBox1 = (CheckBox) convertView.findViewById(R.id.checkBox1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			boolean key = false; 
			for (int i = 0; i < ApplyForSActivity.selectedGoodList.size(); i++) {
				if(ApplyForSActivity.selectedGoodList.get(i).getId().equals(getItem(position).getId())) {
					key = true;
					break;
				}
			}
			if (key) {
				holder.checkBox1.setChecked(true);
			} else {
				holder.checkBox1.setChecked(false);
				
			}
			
			holder.kindNameTV.setText(getItem(position).getName());
			holder.checkBox1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ApplyForSActivity.putGoods(allDataList.get(position));
					notifyDataSetChanged();
				}
			});
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
			private TextView kindNameTV;
			private CheckBox checkBox1;
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
		case R.id.text_right:
			setResult(RESULT_OK);
			finish();
			break;
		}
	}
}
