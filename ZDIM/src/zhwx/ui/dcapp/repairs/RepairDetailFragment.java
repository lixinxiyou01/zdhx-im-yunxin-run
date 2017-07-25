package zhwx.ui.dcapp.repairs;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import zhwx.common.base.CCPAppManager;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.imagegallery.ViewImageInfo;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;
import zhwx.ui.dcapp.repairs.adapter.RmImageGirdAdapter;
import zhwx.ui.dcapp.repairs.model.RepairDetail;

/**   
 * @Title: RepairDetailFragment.java
 * @Package zhwx.ui.dcapp.carmanage
 * @author Li.xin @ 中电和讯
 * @date 2016-3-17 下午4:33:18 
 */
public class RepairDetailFragment extends ScrollTabHolderFragment {

	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private String id;
	
	private String status;

	private String evaluateFlag;

	private String json;
	
	private int startFlag;

	private TextView repairStatusRG;

	private RatingBar suduRB,taiduRB,jishuBar,zhiliangRB,allRB;

	private EditText suggestET;

	private GridView feedbackImgGV;

	private LinearLayout feedBackLay;

	//R
	private TextView requestUserTV,requestTimeTV,requestDeviceTV,faultKindTV,faultDescriptionTV,
			repairHistoryTV,faultPlaceTV,requestPhoneTV,repairerTV,repairTimeTV,repairStatusTV
			,faultLeixingTV,faultReasonTV,elseTV,costTV;

	private GridView requestImgGV,repairImgGV;

	private RepairDetail repairDetail;


	public static Fragment newInstance(String id,int startFlag,String status,String evaluateFlag) {
		RepairDetailFragment f = new RepairDetailFragment();
		Bundle b = new Bundle();
		b.putString("id", id);
		b.putString("status", status);
		b.putString("evaluateFlag", evaluateFlag);
		b.putInt("startFlag", startFlag);
		f.setArguments(b);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		id = getArguments().getString("id");
		status = getArguments().getString("status");
		evaluateFlag = getArguments().getString("evaluateFlag");
		startFlag = getArguments().getInt("startFlag",-1);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = null;
		v = inflater.inflate(R.layout.fragment_repair_detail, null);
		requestUserTV = (TextView) v.findViewById(R.id.requestUserTV);
		requestTimeTV = (TextView) v.findViewById(R.id.requestTimeTV);
		requestDeviceTV = (TextView) v.findViewById(R.id.requestDeviceTV);
		faultKindTV = (TextView) v.findViewById(R.id.faultKindTV);
		faultDescriptionTV = (TextView) v.findViewById(R.id.faultDescriptionTV);
		repairHistoryTV = (TextView) v.findViewById(R.id.repairHistoryTV);
		requestPhoneTV = (TextView) v.findViewById(R.id.requestPhoneTV);
		repairerTV = (TextView) v.findViewById(R.id.repairerTV);
		repairTimeTV = (TextView) v.findViewById(R.id.repairTimeTV);
		repairStatusTV = (TextView) v.findViewById(R.id.repairStatusTV);
		faultPlaceTV = (TextView) v.findViewById(R.id.faultPlaceTV);
		faultLeixingTV = (TextView) v.findViewById(R.id.faultLeixingTV);
		faultReasonTV = (TextView) v.findViewById(R.id.faultReasonTV);
		elseTV = (TextView) v.findViewById(R.id.elseTV);
		requestImgGV = (GridView) v.findViewById(R.id.requestImgGV);
		repairImgGV = (GridView) v.findViewById(R.id.repairImgGV);
		costTV = (TextView) v.findViewById(R.id.costTV);

		repairStatusRG = (TextView) v.findViewById(R.id.repairStatusRG);
		suggestET = (EditText) v.findViewById(R.id.suggestET);
		jishuBar = (RatingBar) v.findViewById(R.id.jishuBar);
		zhiliangRB = (RatingBar) v.findViewById(R.id.zhiliangRB);
		allRB = (RatingBar) v.findViewById(R.id.allRB);
		taiduRB = (RatingBar) v.findViewById(R.id.taiduRB);
		suduRB = (RatingBar) v.findViewById(R.id.suduRB);
		feedbackImgGV = (GridView) v.findViewById(R.id.feedbackImgGV);
		feedBackLay = (LinearLayout) v.findViewById(R.id.feedBackLay);
		return v;
	}
	@Override
	public void onResume() {
		super.onResume();
		getDetail();
	}
	
	private void getDetail(){
		mPostingdialog = new ECProgressDialog(getActivity(), "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("repairId", new ParameterValue(id));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.getRepairDetail(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(json);
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
		requestImgGV.setAdapter(new RmImageGirdAdapter(getActivity(), repairDetail.getRequestInfo().getImageList()));
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
				CCPAppManager.startChattingImageViewAction(getActivity(),position , urls);
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
		repairImgGV.setAdapter(new RmImageGirdAdapter(getActivity(), repairDetail.getRepairInfo().getRepairImageList()));
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
				CCPAppManager.startChattingImageViewAction(getActivity(),position, urls);
			}
		});

		repairerTV.setText(StringUtil.isBlank(repairDetail.getRepairInfo().getWorkerName())?"无":repairDetail.getRepairInfo().getWorkerName());
		repairTimeTV.setText(StringUtil.isBlank(repairDetail.getRepairInfo().getRepairTime())?"无":repairDetail.getRepairInfo().getRepairTime());
		repairStatusTV.setText(StringUtil.isBlank(repairDetail.getRepairInfo().getRepairStatus())?"无":repairDetail.getRepairInfo().getRepairStatus());
		faultLeixingTV.setText(StringUtil.isBlank(repairDetail.getRepairInfo().getMalfunctionKind())?"无":repairDetail.getRepairInfo().getMalfunctionKind());
		faultReasonTV.setText(StringUtil.isBlank(repairDetail.getRepairInfo().getMalfunctionReason())?"无":repairDetail.getRepairInfo().getMalfunctionReason());
		costTV.setText("0".equals(repairDetail.getRepairInfo().getCostApplication())?"无":repairDetail.getRepairInfo().getCostApplication()+"元");
		//TODO 消耗品列表
		String eles = "";
		for (int i = 0; i < repairDetail.getRepairInfo().getGoodsSum().size(); i++) {
			String price = repairDetail.getRepairInfo().getGoodsSum().get(i).getPrice();
			String name = repairDetail.getRepairInfo().getGoodsSum().get(i).getName();
			String count = repairDetail.getRepairInfo().getGoodsSum().get(i).getCount();
			String subTotal = repairDetail.getRepairInfo().getGoodsSum().get(i).getSubtotal();
			if (i == repairDetail.getRepairInfo().getGoodsSum().size()-1) {
				eles += (name + " 【" + price + "元*" + count + "个 = " + subTotal +"元】");
			} else {
				eles += (name + " 【" + price + "元*" + count + "个 = " + subTotal +"元】\n");
			}
		}
		elseTV.setText(StringUtil.isBlank(eles)?"无":eles);


		if (repairDetail.getFeedBackInfo()!=null && repairDetail.getFeedBackInfo().getRepairFlag()!=null) {
			feedBackLay.setVisibility(View.VISIBLE);
			suduRB.setRating(Float.parseFloat(repairDetail.getFeedBackInfo().getSpeedStr()));
			taiduRB.setRating(Float.parseFloat(repairDetail.getFeedBackInfo().getAttitudeStr()));
			jishuBar.setRating(Float.parseFloat(repairDetail.getFeedBackInfo().getTechnicalLevelStr()));
			zhiliangRB.setRating(Float.parseFloat(repairDetail.getFeedBackInfo().getQualityStr()));
			allRB.setRating(Float.parseFloat(repairDetail.getFeedBackInfo().getScoreStr()));
			suggestET.setText(StringUtil.isNotBlank(repairDetail.getFeedBackInfo().getSuggestion())?repairDetail.getFeedBackInfo().getSuggestion():"");
			feedbackImgGV.setAdapter(new RmImageGirdAdapter(getActivity(), repairDetail.getFeedBackInfo().getRepairImageList()));
			Tools.setGridViewHeightBasedOnChildren4(feedbackImgGV);
			feedbackImgGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					ArrayList<ViewImageInfo> urls = new ArrayList<ViewImageInfo>();
					ViewImageInfo imageInfo;
					for (int i = 0; i < repairDetail.getFeedBackInfo().getRepairImageList().size(); i++) {
						imageInfo = new ViewImageInfo("", ECApplication.getInstance().getV3Address()+repairDetail.getFeedBackInfo().getRepairImageList().get(i));
						urls.add(imageInfo);
					}
					CCPAppManager.startChattingImageViewAction(getActivity(),position, urls);
				}
			});
			if (repairDetail.getFeedBackInfo().getRepairFlag() != null ) {
				if("0".equals(repairDetail.getFeedBackInfo().getRepairFlag())) {
					repairStatusRG.setText("否");
				} else {
					repairStatusRG.setText("是	");
				}
			}
		} else {
			feedBackLay.setVisibility(View.GONE);
		}
		mPostingdialog.dismiss();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void adjustScroll(int scrollHeight) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount, int pagePosition) {
	}
}
