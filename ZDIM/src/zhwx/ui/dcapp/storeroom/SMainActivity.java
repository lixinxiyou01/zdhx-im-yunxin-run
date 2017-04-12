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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.HashMap;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.ui.dcapp.storeroom.model.IndexPageData;


/**   
 * @Title: AMainActivity.java 
 * @Package com.lanxum.hzth.im.ui.v3.assets 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午9:52:07 
 */
public class SMainActivity extends BaseActivity implements OnClickListener {
	
	private Activity context;
	
	private HashMap<String, ParameterValue> map;
	
	private String indexJson;
	
	private Handler handler = new Handler();

	private Button toMyApplyBT,toCheckBT,toGrantBT;
	
	private IndexPageData indexData;
	
	private TextView count_dsh_a,count_dff_a,count_ck_a;
	
	private RelativeLayout dsh_a_lay,dff_a_lay,tj_a_lay,ck_a_lay;
	
    @Override
	protected int getLayoutId() {return R.layout.activity_sm_main;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_store);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"易耗品管理", this);
		initView();
		getData();
	}
	
	/**
	 * 
	 */
	private void initView() {
		count_dsh_a = (TextView) findViewById(R.id.count_dsh_a);
		count_dff_a = (TextView) findViewById(R.id.count_dff_a);
		count_ck_a = (TextView) findViewById(R.id.count_ck_a);
		dsh_a_lay = (RelativeLayout) findViewById(R.id.dsh_a_lay);
		dff_a_lay = (RelativeLayout) findViewById(R.id.dff_a_lay);
		tj_a_lay = (RelativeLayout) findViewById(R.id.tj_a_lay);
		ck_a_lay = (RelativeLayout) findViewById(R.id.ck_a_lay);
	}
	

	private void getData() {
		getNotice();   //获取公告板数据
	}

	private void getNotice(){
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getUserOperation(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData();
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("请求失败，请稍后重试");
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
						}
					}, 5);
				}
			}
		}).start();
	}
	
	
	//TODO
	private void refreshData() {
		if (indexJson.contains("</html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		indexData = new Gson().fromJson(indexJson, IndexPageData.class);
		int checkCount = Integer.parseInt(indexData.getDeptCount()) + Integer.parseInt(indexData.getZwCount());
		count_dsh_a.setText(checkCount + "");
		count_dsh_a.setVisibility(checkCount > 0 ? View.VISIBLE : View.INVISIBLE);
//		count_dff_a.setVisibility(Integer.parseInt(indexData.getSr_warehouseManage()) > 0 ? View.VISIBLE : View.INVISIBLE);
//		count_dff_a.setText(indexData.getSr_warehouseManage());
//		count_ck_a.setVisibility(Integer.parseInt(indexData.getSr_warehouseManage()) > 0 ? View.VISIBLE : View.INVISIBLE);
//		count_ck_a.setText(indexData.getSr_warehouseManage());
		
		if ("1".equals(indexData.getDeptslsh())) {
			dsh_a_lay.setVisibility(View.VISIBLE);
		} else {
			dsh_a_lay.setVisibility(View.GONE);
			
		}
		if ("1".equals(indexData.getSr_warehouseManage())) {
			dff_a_lay.setVisibility(View.VISIBLE);
			ck_a_lay.setVisibility(View.VISIBLE);
		} else {
			dff_a_lay.setVisibility(View.GONE);
			ck_a_lay.setVisibility(View.GONE);
		}
		if ("1".equals(indexData.getSr_statistics())) {
			tj_a_lay.setVisibility(View.VISIBLE);
		} else {
			tj_a_lay.setVisibility(View.GONE);
			
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getData();
	}
	
	/**
	 * 我的
	 * @param v
	 */
	public void onMyAssets(View v) {
		startActivity(new Intent(context, MyApplyListActivity.class));
	}
	/**
	 * 待审核
	 * @param v
	 */
	public void onDshA(View v) {
		startActivity(new Intent(context, ToCheckListActivity.class));
	}
	/**
	 * 待发放
	 * @param v
	 */
	public void onGrantA(View v) {
		startActivity(new Intent(context, ToGrantActivity.class));
	}

	/**
	 * 出库
	 * @param v
	 */
	public void onGetOutA(View v) {
		startActivity(new Intent(context, ToGetOutListActivity.class));
	}
	/**
	 * 统计
	 * @param v
	 */
	public void onStatistics(View v) {
		startActivity(new Intent(context, StatisticsActivity.class));
	}
	/**
	 * 申领物品
	 * @param v
	 */
	public void onApply(View v) {
		startActivity(new Intent(context, ApplyForSActivity.class));
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
