package zhwx.ui.dcapp.assets;

/** code is far away from bug with the animal protecting
 *
 *----------Dragon be here!----------/
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃神兽保佑
 * 　　　　┃　　　┃代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━神兽出没━━━━━━
*/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
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
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.assets.model.AIndex;
import zhwx.ui.dcapp.assets.model.CheckListItem;

/**   
 * @Title: AMainActivity.java 
 * @Package zhwx.ui.dcapp.assets
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午9:52:07 
 */
public class AMainActivityccccc extends BaseActivity implements OnClickListener {
	
	private Activity context;
	
	private RequestWithCacheGet mRequestWithCache;
	
	private HashMap<String, ParameterValue> map;
	
	private HashMap<String, ParameterValue> mapA;
	
	private String noticeJson;
	
	private String indexJson;
	
	private TextView noticeTV;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private TextView count_dsh_a,count_dff_a;
	
	private RelativeLayout dsh_a_lay,dff_a_lay,cz_a_lay;
	
    @Override
	protected int getLayoutId() {return R.layout.activity_as_main;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setBackGroundColor(R.color.main_bg_assets);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"资产管理", this);
		mRequestWithCache = new RequestWithCacheGet(context);
		initView();
//		getData();
	}
	
	private void getData() {
		getNotice();   //获取公告板数据
	}
	
	private void getNotice(){
		mapA = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getNotSignedGrantNumJson(ECApplication.getInstance().getV3Address(), mapA);
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
	
	private void getIndex(){
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getIndexJson(ECApplication.getInstance().getV3Address(), map);
					noticeJson = UrlUtil.getNotSignedGrantNumJson(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData1(indexJson);
							refreshData(noticeJson);
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
	
	private void refreshData1(String indexJson) {
		
		System.out.println(indexJson);
		if (indexJson.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		if ("{}".equals(indexJson)){
			dsh_a_lay.setVisibility(View.INVISIBLE);
			dff_a_lay.setVisibility(View.INVISIBLE);
			cz_a_lay.setVisibility(View.INVISIBLE);
			mPostingdialog.dismiss();
			return;
		}
		AIndex data = new Gson().fromJson(indexJson, AIndex.class);
		if (data != null) {
			count_dsh_a.setText(data.getCheck()+"");
			count_dsh_a.setVisibility(0 == data.getCheck()?View.INVISIBLE:View.VISIBLE);
			
			count_dff_a.setText(data.getGrant()+"");
			count_dff_a.setVisibility(0 == data.getGrant()?View.INVISIBLE:View.VISIBLE);
		}
		mPostingdialog.dismiss();
	}
	
	/**
	 * @param noticeJson2
	 */
	private void refreshData(String noticeJson2) {
		if (indexJson.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		Num n = new Gson().fromJson(noticeJson2,Num.class);
		if ("0".equals(n.getNum().trim())) {
			
		} else {
			noticeTV.setText("当前您有 " + n.getNum() + " 个需要补签的发放记录，请及时到管理员处补签");	
		}
	}
	
	class Num {
		String num;

		public String getNum() {
			return num;
		}

		public void setNum(String num) {
			this.num = num;
		}
	}
	
	public void initView(){
		noticeTV = (TextView) findViewById(R.id.textView3);
		count_dsh_a = (TextView) findViewById(R.id.count_dsh_a);
		count_dff_a = (TextView) findViewById(R.id.count_dff_a);
		dsh_a_lay = (RelativeLayout) findViewById(R.id.dsh_a_lay);
		dff_a_lay = (RelativeLayout) findViewById(R.id.dff_a_lay);
		cz_a_lay = (RelativeLayout) findViewById(R.id.cz_a_lay);
	} 
	
	/**申请资产*/
	public void onOrderCar(View v) {
		startActivity(new Intent(context, ApplyForAssetActivity.class));
	}
	
	/** 待审核 */
	public void onDshA(View v){
		startActivity(new Intent(context, CheckManageActivity.class)
		.putExtra("status", CheckListItem.STATUS_NOTAUDIT));
	}
	
	/** 发放 */
	public void onGrantA(View v){
		startActivity(new Intent(context, CheckManageActivity.class)
		.putExtra("status", CheckListItem.STATUS_PASS));
	}
	
	/** 已发放*/
	public void onGranted(View v){
		
		startActivity(new Intent(context, GrantedListActivity.class));
	}
	
	/** 查找*/
	public void onSearch(View v){
		startActivity(new Intent(context, AssetsListActivity.class));
	}
	
	/** 我的*/
	public void onMyAssets(View v){
		startActivity(new Intent(context, MyAssetsManageActivity.class));
	}
	
	
	@Override
	protected void onResume() {
		getIndex();		//获取状态信息                      
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
