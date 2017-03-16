package zhwx.ui.dcapp.repairs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.base.CCPAppManager;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.imagegallery.ViewImageInfo;
import zhwx.ui.dcapp.repairs.adapter.RmImageGirdAdapter;
import zhwx.ui.dcapp.repairs.model.DeviceKind;
import zhwx.ui.dcapp.repairs.model.RepairDetail;

import static com.netease.nim.demo.team.TeamSynchroHelper.handler;


/**   
 * @Title: GrantActivity.java 
 * @Package zhwx.ui.dcapp.assets
 * @Description: 资产发放购物车
 * @author Li.xin @ zdhx
 * @date 2016年8月22日 下午12:42:43 
 */
public class WorkerFeedBackActivity extends BaseActivity implements OnClickListener{
	
	private Activity context;
		
	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;
	
	private String circleJson;
	
	private List<DeviceKind> allDataList;

	private ImageLoader imageLoader;

	//R
	private TextView requestUserTV,requestTimeTV,requestDeviceTV,faultKindTV,faultDescriptionTV,
			repairHistoryTV,faultPlaceTV,requestPhoneTV,repairerTV,repairTimeTV,repairStatusTV
			,faultLeixingTV,faultReasonTV,elseTV;

	private GridView requestImgGV,repairImgGV;

	private RepairDetail repairDetail;

	private String repairId;


	@Override
	protected int getLayoutId() {
		return R.layout.activity_worker_feedback;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		imageLoader = new ImageLoader(context);
		repairId = getIntent().getStringExtra("repairId");
		getTopBarView().setBackGroundColor(R.color.main_bg_repairs);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1,"维修反馈", this);
		initView();
		getDetail();
	}

	private void initView() {
		requestUserTV = (TextView) findViewById(R.id.requestUserTV);
		requestTimeTV = (TextView) findViewById(R.id.requestTimeTV);
		requestDeviceTV = (TextView) findViewById(R.id.requestDeviceTV);
		faultKindTV = (TextView) findViewById(R.id.faultKindTV);
		faultDescriptionTV = (TextView) findViewById(R.id.faultDescriptionTV);
		repairHistoryTV = (TextView) findViewById(R.id.repairHistoryTV);
		requestPhoneTV = (TextView) findViewById(R.id.requestPhoneTV);
		repairerTV = (TextView) findViewById(R.id.repairerTV);
		repairTimeTV = (TextView) findViewById(R.id.repairTimeTV);
		repairStatusTV = (TextView) findViewById(R.id.repairStatusTV);
		faultPlaceTV = (TextView) findViewById(R.id.faultPlaceTV);
		faultLeixingTV = (TextView) findViewById(R.id.faultLeixingTV);
		faultReasonTV = (TextView) findViewById(R.id.faultReasonTV);
		elseTV = (TextView) findViewById(R.id.elseTV);
		requestImgGV = (GridView) findViewById(R.id.requestImgGV);
		repairImgGV = (GridView) findViewById(R.id.repairImgGV);
	}


	private void getDetail(){
		mPostingdialog = new ECProgressDialog(context, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("repairId", new ParameterValue(repairId));
		new ProgressThreadWrap(context, new RunnableWrap() {
			@Override
			public void run() {
				try {
					circleJson = UrlUtil.getRepairDetail(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(circleJson);
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

	private void refreshData(String json) {
		if(json.contains("<html>")){
			ToastUtil.showMessage("数据异常");
			return;
		}
		System.out.println(json);
		repairDetail = new Gson().fromJson(json,RepairDetail.class);

		//报修图片
		requestImgGV.setAdapter(new RmImageGirdAdapter(context, repairDetail.getRequestInfo().getImageList()));
		Tools.setGridViewHeightBasedOnChildren4(requestImgGV);
		requestImgGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ArrayList<ViewImageInfo> urls = new ArrayList<ViewImageInfo>();
				ViewImageInfo imageInfo;
				for (int i = 0; i < repairDetail.getRequestInfo().getImageList().size(); i++) {
					imageInfo = new ViewImageInfo("", ECApplication.getInstance().getV3Address()+repairDetail.getRequestInfo().getImageList().get(i));
					urls.add(imageInfo);
				}
				CCPAppManager.startChattingImageViewAction(context,position , urls);
			}
		});

		requestUserTV.setText(repairDetail.getRequestInfo().getRequestUserName());
		requestTimeTV.setText(repairDetail.getRequestInfo().getRequestTime());
		requestDeviceTV.setText(repairDetail.getRequestInfo().getDeviceName());
		faultKindTV.setText(repairDetail.getRequestInfo().getMalfunction());
		faultDescriptionTV.setText(repairDetail.getRequestInfo().getMalfunctionDescribe());
		repairHistoryTV.setText(StringUtil.isNotBlank(repairDetail.getRequestInfo().getRepairHistory())?repairDetail.getRequestInfo().getRepairHistory():"无");
		faultPlaceTV.setText(repairDetail.getRequestInfo().getMalfunctionPlace());
		requestPhoneTV.setText(repairDetail.getRequestInfo().getPhoneNum());

		//维修图片
		repairImgGV.setAdapter(new RmImageGirdAdapter(context, repairDetail.getRepairInfo().getRepairImageList()));
		Tools.setGridViewHeightBasedOnChildren4(repairImgGV);
		repairImgGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ArrayList<ViewImageInfo> urls = new ArrayList<ViewImageInfo>();
				ViewImageInfo imageInfo;
				for (int i = 0; i < repairDetail.getRepairInfo().getRepairImageList().size(); i++) {
					imageInfo = new ViewImageInfo("", ECApplication.getInstance().getV3Address()+repairDetail.getRepairInfo().getRepairImageList().get(i));
					urls.add(imageInfo);
				}
				CCPAppManager.startChattingImageViewAction(context,position, urls);
			}
		});

		repairerTV.setText(repairDetail.getRepairInfo().getWorkerName());
		repairTimeTV.setText(repairDetail.getRepairInfo().getRepairTime());
		repairStatusTV.setText(repairDetail.getRepairInfo().getRepairStatus());
		faultLeixingTV.setText(repairDetail.getRepairInfo().getMalfunctionKind());
		faultReasonTV.setText(repairDetail.getRepairInfo().getMalfunctionReason());
		elseTV.setText(StringUtil.isNotBlank(repairDetail.getRepairInfo().getGoodsSum())?repairDetail.getRepairInfo().getGoodsSum():"无");

//		actionLay.removeAllViews();
//		List<TextView> btns = getOrderButtonList(status,evaluateFlag);
//		if (btns.size() == 0) {
//			actionLay.setVisibility(View.GONE);
//		} else {
//			for (TextView button : btns) {
//				actionLay.addView(button);
//			}
//		}
		mPostingdialog.dismiss();
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
