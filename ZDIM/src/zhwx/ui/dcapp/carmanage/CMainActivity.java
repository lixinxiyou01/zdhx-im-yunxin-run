package zhwx.ui.dcapp.carmanage;

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
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.HashMap;

import volley.Response;
import volley.VolleyError;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.Log;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.carmanage.model.IndexData;
import zhwx.ui.dcapp.carmanage.model.OrderCarListItem;

/**   
 * @Title: RMainActivity.java
 * @Package zhwx.ui.dcapp.carmanage
 * @Description: (订车主页面) 
 * @author Li.xin @ 中电和讯
 * @date 2016-3-7 上午9:52:07 
 */
public class CMainActivity extends BaseActivity {
	
	private Activity context;
	
	private FrameLayout top_bar;
	
	private RequestWithCacheGet mRequestWithCache;
	
	private HashMap<String, ParameterValue> map;
	
	private String noticeJson;
	
	private String indexJson;
	
	private TextView noticeTV;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private TextView count_dpc_a,count_pcz_a,count_ypc_a,count_dpj_a,count_wsh_b,count_wpc_b,
					 count_pcz_b,count_ypc_b,count_wjs_c,count_wqr_c,count_dpj_c;
	
	private LinearLayout myOrderLay,managerLay,dirverLay;
	
	/** 司机 */
	public static final int STARTFLAG_MYTASK = 0;
	
	/** 订车人 */
	public static final int STARTFLAG_MYORDERCAR = 1;
	
	/** 派车人 */
	public static final int STARTFLAG_ORDERCHECK = 2;
	
	private PopupWindow publicPop;
    
    private View publicView;

    private TextView publicTV;
    
    private RelativeLayout chack_item_lay,nuLay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		context = this;
		mRequestWithCache = new RequestWithCacheGet(context);
		initView();
		getData();
	}
	
	private void getData() {
		getNotice();   //获取公告板数据
	}
	
	private void getNotice(){

		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		try {
			noticeJson = mRequestWithCache.getRseponse(UrlUtil.getNotice(ECApplication.getInstance().getV3Address(),map),
					new RequestWithCacheGet.RequestListener() {

						@Override
						public void onResponse(String response) {
							if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
								Log.i("新数据:" + response);
								refreshData(response);
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							 
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

		if ((noticeJson != null && !noticeJson.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:" + noticeJson);
			refreshData(noticeJson);
		}
	
	}
	
	private void getIndex(){
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("operationCode", new ParameterValue("carmanage"));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					indexJson = UrlUtil.getIndex(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData1(indexJson);
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
			ToastUtil.showMessage("无权限");
			finish();
			return;
		}
		IndexData data = new Gson().fromJson(indexJson, IndexData.class);
		
		//角色权限控制
//		if (StringUtil.isNotBlank(data.getMyOrderCar().getDpc())) {
//			myOrderLay.setVisibility(View.VISIBLE);
//		}
		
		
		if (data.getMyOrderCar() != null) {
			count_dpc_a.setText(data.getMyOrderCar().getDpc());
			count_dpc_a.setVisibility("0".equals(data.getMyOrderCar().getDpc())?View.INVISIBLE:View.VISIBLE);
			count_dpc_a.bringToFront();
			
			count_pcz_a.setText(data.getMyOrderCar().getPcz());
			count_pcz_a.setVisibility("0".equals(data.getMyOrderCar().getPcz())?View.INVISIBLE:View.VISIBLE);
			count_pcz_a.bringToFront();

			count_ypc_a.setText(data.getMyOrderCar().getYpc());
			count_ypc_a.setVisibility("0".equals(data.getMyOrderCar().getYpc())?View.INVISIBLE:View.VISIBLE);
			count_ypc_a.bringToFront();

			count_dpj_a.setText(data.getMyOrderCar().getDpj());
			count_dpj_a.setVisibility("0".equals(data.getMyOrderCar().getDpj())?View.INVISIBLE:View.VISIBLE);
			count_dpj_a.bringToFront();
		}
		 
		if (data.getOrderCarManage() != null) {
			managerLay.setVisibility(View.VISIBLE);
			count_wsh_b.setText(data.getOrderCarManage().getOrderCheck());
			count_wsh_b.setVisibility("0".equals(data.getOrderCarManage().getOrderCheck())?View.INVISIBLE:View.VISIBLE);
			chack_item_lay.setVisibility(data.getOrderCarManage().getOrderCheck() == null?View.GONE:View.VISIBLE);
//			nuLay.setVisibility(data.getOrderCarManage().getOrderCheck() == null?View.VISIBLE:View.GONE);
			
			count_wpc_b.setText(data.getOrderCarManage().getDpc());
			count_wpc_b.setVisibility("0".equals(data.getOrderCarManage().getDpc())?View.INVISIBLE:View.VISIBLE);
			
			count_pcz_b.setText(data.getOrderCarManage().getPcz());
			count_pcz_b.setVisibility("0".equals(data.getOrderCarManage().getPcz())?View.INVISIBLE:View.VISIBLE);
			
			count_ypc_b.setText(data.getOrderCarManage().getYpc());
			count_ypc_b.setVisibility("0".equals(data.getOrderCarManage().getYpc())?View.INVISIBLE:View.VISIBLE);
		}
		
		if (data.getMyTask() != null) {
			dirverLay.setVisibility(View.VISIBLE);
			count_wjs_c.setText(data.getMyTask().getWjs());
			count_wjs_c.setVisibility("0".equals(data.getMyTask().getWjs())?View.INVISIBLE:View.VISIBLE);
			
			count_wqr_c.setText(data.getMyTask().getWqr());
			count_wqr_c.setVisibility("0".equals(data.getMyTask().getWqr())?View.INVISIBLE:View.VISIBLE);
			
			count_dpj_c.setText(data.getMyTask().getWpj());
			count_dpj_c.setVisibility("0".equals(data.getMyTask().getWpj())?View.INVISIBLE:View.VISIBLE);
		}
	
		
		mPostingdialog.dismiss();
	}
	
	/**
	 * @param noticeJson2
	 */
	private void refreshData(String noticeJson2) {
		noticeTV.setText(Html.fromHtml(noticeJson2));
		publicTV.setText(Html.fromHtml(noticeJson2));
	}

	public void onExpend(View v) {
		showPop();
	}
	
	public void initView(){
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar);
		noticeTV = (TextView) findViewById(R.id.noticeTV);
		count_dpc_a = (TextView) findViewById(R.id.count_dpc_a);
		count_pcz_a = (TextView) findViewById(R.id.count_pcz_a);
		count_ypc_a = (TextView) findViewById(R.id.count_ypc_a);
		count_dpj_a = (TextView) findViewById(R.id.count_dpj_a);
		count_wsh_b = (TextView) findViewById(R.id.count_wsh_b);
		count_wpc_b = (TextView) findViewById(R.id.count_wpc_b);
		count_pcz_b = (TextView) findViewById(R.id.count_pcz_b);
		count_ypc_b = (TextView) findViewById(R.id.count_ypc_b);
		count_wjs_c = (TextView) findViewById(R.id.count_wjs_c);
		count_wqr_c = (TextView) findViewById(R.id.count_wqr_c);
		count_dpj_c = (TextView) findViewById(R.id.count_dpj_c);
		myOrderLay = (LinearLayout) findViewById(R.id.myOrderLay);
		managerLay = (LinearLayout) findViewById(R.id.managerLay);
		dirverLay = (LinearLayout) findViewById(R.id.dirverLay);
		chack_item_lay = (RelativeLayout) findViewById(R.id.chack_item_lay);
		nuLay = (RelativeLayout) findViewById(R.id.nuLay);
		initPopMenu();
	}
	
	/**
	 * 初始化popupWindow
	 */
	private void initPopMenu() {
		// 公告详情弹窗
		publicView = context.getLayoutInflater().inflate(R.layout.layout_cm_public, null);
		if (publicPop == null) {
			publicPop = new PopupWindow(publicView, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, true);
		}
		if (publicPop.isShowing()) {
			publicPop.dismiss();
		}
		
		publicTV = (TextView) publicView.findViewById(R.id.publicTV);
	}
	
	public void showPop() {
		if (!publicPop.isShowing()) {
			publicPop.setFocusable(false);
			publicPop.setOutsideTouchable(true);
			publicPop.setAnimationStyle(R.style.PopupAnimation_cm);
			publicPop.showAtLocation(context.getWindow().getDecorView(),Gravity.CENTER, 0, 0);
		} else {
			publicPop.dismiss();
		}
	}
	
	/** 订车人查看所有订车单 */
	public void oncheckAllOrder(View v) {
		startActivity(new Intent(context, OrderManageActivity.class)
						  .putExtra("startFlag", STARTFLAG_MYORDERCAR)
						  .putExtra("status", OrderCarListItem.CHECKSTATUS_ALL));
	}
	
	/** 管理员查看所有订车单 */
	public void oncheckAllOrderManager(View v) {
		startActivity(new Intent(context, OrderManageActivity.class)
		.putExtra("startFlag", STARTFLAG_ORDERCHECK)
		.putExtra("status", OrderCarListItem.CHECKSTATUS_ALL));
	}
	
	/** 司机查看所有订车单 */
	public void oncheckAllOrderDirver(View v) {
		startActivity(new Intent(context, OrderManageActivity.class)
		.putExtra("startFlag", STARTFLAG_MYTASK)
		.putExtra("status", OrderCarListItem.CHECKSTATUS_ALL));
	}
	
	/** 订车 */
	public void onOrderCar(View v) {
		startActivity(new Intent(context, OrderCarActivity.class));
	}
	
	/** 待派车 */
	public void onDpcA(View v){
		startActivity(new Intent(context, OrderManageActivity.class)
		.putExtra("startFlag", STARTFLAG_MYORDERCAR)
		.putExtra("status", OrderCarListItem.CHECKSTATUS_DRAFT));
	}
	
	/** 派车中 */
	public void onPczA(View v){
		startActivity(new Intent(context, OrderManageActivity.class)
		.putExtra("startFlag", STARTFLAG_MYORDERCAR)
		.putExtra("status", OrderCarListItem.CHECKSTATUS_ASSIGNING));
	}
	
	/** 已派车 */
	public void onYpcA(View v){
		startActivity(new Intent(context, OrderManageActivity.class)
		.putExtra("startFlag", STARTFLAG_MYORDERCAR)
		.putExtra("status", OrderCarListItem.CHECKSTATUS_PASS));
	}
	
	/** 待评价 */
	public void onDpjA(View v){
		startActivity(new Intent(context, OrderManageActivity.class)
		.putExtra("startFlag", STARTFLAG_MYORDERCAR)
		.putExtra("status", OrderCarListItem.CHECKSTATUS_FINISH));
	}
	
	/** 未审核 */
	public void onWshB(View v){
		startActivity(new Intent(context, OrderManageActivity.class)
		.putExtra("startFlag", STARTFLAG_ORDERCHECK)
		.putExtra("status", OrderCarListItem.CHECKSTATUS_CHECK));
	}
	
	/** 未派车 */
	public void onWpcB(View v){
		startActivity(new Intent(context, OrderManageActivity.class)
		.putExtra("startFlag", STARTFLAG_ORDERCHECK)
		.putExtra("status", OrderCarListItem.CHECKSTATUS_DRAFT));
	}
	
	/** 派车中 */
	public void onPczB(View v){
		startActivity(new Intent(context, OrderManageActivity.class)
		.putExtra("startFlag", STARTFLAG_ORDERCHECK)
		.putExtra("status", OrderCarListItem.CHECKSTATUS_ASSIGNING));
	}

	/** 补单 */
	public void onBdB(View v){
		//TODO
		ToastUtil.showMessage("开发中");
//		startActivity(new Intent(context, OrderManageActivity.class)
//		.putExtra("startFlag", STARTFLAG_ORDERCHECK)
//		.putExtra("status", OrderCarListItem.CHECKSTATUS_ASSIGNING));
	}

	/** 已派车 */
	public void onYpcB(View v){
		startActivity(new Intent(context, OrderManageActivity.class)
		.putExtra("startFlag", STARTFLAG_ORDERCHECK)
		.putExtra("status", OrderCarListItem.CHECKSTATUS_PASS));
	}
	
	/** 待确认 */
	public void onDqrC(View v){
		startActivity(new Intent(context, OrderManageActivity.class)
		.putExtra("startFlag", STARTFLAG_MYTASK)
		.putExtra("status", OrderCarListItem.CHECKSTATUS_DQR));
	}
	
	/** 待结束 */
	public void onDjsC(View v){
		startActivity(new Intent(context, OrderManageActivity.class)
		.putExtra("startFlag", STARTFLAG_MYTASK)
		.putExtra("status", OrderCarListItem.CHECKSTATUS_DJS));
	}
	
	/** 待评价 */
	public void onDpjC(View v){
		startActivity(new Intent(context, OrderManageActivity.class)
		.putExtra("startFlag", STARTFLAG_MYTASK)
		.putExtra("status", OrderCarListItem.CHECKSTATUS_DPJ));
	}


	
	public void onClose(View v) {
		if (publicPop.isShowing()) {
			publicPop.dismiss();
		}
	}
	
	@Override
	protected void onResume() {
		getIndex();		//获取状态信息                      
		super.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (publicPop.isShowing()) {
				publicPop.dismiss();
				return true;
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_cm_main;
	}
}
