package zhwx.ui.dcapp.carmanage;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zhwx.common.model.ParameterValue;
import zhwx.common.util.DensityUtil;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.carmanage.model.AssignCarInfo;
import zhwx.ui.dcapp.carmanage.model.OrderCarListItem;
import zhwx.ui.dcapp.carmanage.model.OrderDetail;
import zhwx.ui.dcapp.carmanage.model.OrderDetail.OaCarData;
import zhwx.ui.dcapp.carmanage.model.OrderDetail.StarData;
import zhwx.ui.dcapp.carmanage.view.ScrollTabHolderFragment;

/**   
 * @Title: OrderDetailFragment.java 
 * @Package zhwx.ui.dcapp.carmanage
 * @author Li.xin @ 中电和讯
 * @date 2016-3-17 下午4:33:18 
 */
public class OrderDetailFragment extends ScrollTabHolderFragment {

	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private String id;
	
	private String status;
	
	private String evaluateFlag;
	
	private String json;
	
	private OrderDetail orderDetail;
	
	private AssignCarInfo assignCarInfo;
	
	private LinearLayout linkLay,onCallLay,onMsgLay,onIMLay,actionLay;
	
	private TextView orderUserTV,telephoneTV,departmentNameTV,userDateTV,arriveTimeTV,
				     checkTextTV,checkerNameTV,checkAdviceTV,
					 addressTV,userCountTV,personListTV,reasonTV,instructionTV,fanchengFlagTV;
	
	private TextView realCountTV,realAddressTV,realTimeTV,noteTV;
	
	private TextView backUseDateET,backArriveTimeET,backAddressET,backUserCountET,backCarUserET;
	
	private LinearLayout pcRecordLay,orderUserLay,backContenerLay,checkLay,dirverFeedBackLay;
	
	private int startFlag;
	
	public static Fragment newInstance(String id,int startFlag,String status,String evaluateFlag) {
		OrderDetailFragment f = new OrderDetailFragment();
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
		if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
			//  司机查看任务详情
			v = inflater.inflate(R.layout.fragment_order_detail_dirver, null);
			realCountTV = (TextView) v.findViewById(R.id.realCountTV);
			realAddressTV = (TextView) v.findViewById(R.id.realAddressTV);
			realTimeTV = (TextView) v.findViewById(R.id.realTimeTV);
			noteTV = (TextView) v.findViewById(R.id.noteTV);
			orderUserLay = (LinearLayout) v.findViewById(R.id.orderUserLay);
			actionLay = (LinearLayout) v.findViewById(R.id.actionLay);
			dirverFeedBackLay = (LinearLayout) v.findViewById(R.id.dirverFeedBackLay);
			
		} else {
			v = inflater.inflate(R.layout.fragment_order_detail, null);
			orderUserTV = (TextView) v.findViewById(R.id.orderUserTV);
			telephoneTV = (TextView) v.findViewById(R.id.telephoneTV);
			departmentNameTV = (TextView) v.findViewById(R.id.departmentNameTV);
			userDateTV = (TextView) v.findViewById(R.id.userDateTV);
			arriveTimeTV = (TextView) v.findViewById(R.id.arriveTimeTV);
			addressTV = (TextView) v.findViewById(R.id.addressTV);
			userCountTV = (TextView) v.findViewById(R.id.userCountTV);
			personListTV = (TextView) v.findViewById(R.id.personListTV);
			reasonTV = (TextView) v.findViewById(R.id.reasonTV);
			instructionTV = (TextView) v.findViewById(R.id.instructionTV);
			
			checkTextTV = (TextView) v.findViewById(R.id.checkTextTV);
			checkerNameTV = (TextView) v.findViewById(R.id.checkerNameTV);
			checkAdviceTV = (TextView) v.findViewById(R.id.checkAdviceTV);
			
			fanchengFlagTV = (TextView) v.findViewById(R.id.fanchengFlagTV);
			backUseDateET = (TextView) v.findViewById(R.id.backUseDateET);
			backArriveTimeET = (TextView) v.findViewById(R.id.backArriveTimeET);
			backAddressET = (TextView) v.findViewById(R.id.backAddressET);
			backUserCountET = (TextView) v.findViewById(R.id.backUserCountET);
			backCarUserET = (TextView) v.findViewById(R.id.backCarUserET);
			backContenerLay = (LinearLayout) v.findViewById(R.id.backContenerLay);
			linkLay = (LinearLayout) v.findViewById(R.id.linkLay_ref);
			checkLay = (LinearLayout) v.findViewById(R.id.checkLay);
			actionLay = (LinearLayout) v.findViewById(R.id.actionLay);
			onCallLay = (LinearLayout) v.findViewById(R.id.onCallLay);
			onCallLay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if (StringUtil.isBlank(orderDetail.getTelephone())) {
						ToastUtil.showMessage("无可用电话号码");
						return;		
					}
					Intent phoneIntent=new Intent("android.intent.action.DIAL", Uri.parse("tel:" + orderDetail.getTelephone()));
					startActivity(phoneIntent);
				}
			});
			onMsgLay = (LinearLayout) v.findViewById(R.id.onMsgLay);
			onMsgLay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if (StringUtil.isBlank(orderDetail.getTelephone())) {
						ToastUtil.showMessage("无可用电话号码");
						return;		
					}
					Uri smsToUri = Uri.parse("smsto:" + orderDetail.getTelephone());
					Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
					intent.putExtra("sms_body", "");
					startActivity(intent);
				}
			});
			onIMLay = (LinearLayout) v.findViewById(R.id.onIMLay);
			onIMLay.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ToastUtil.showMessage("暂未开通");
				}
			});
			pcRecordLay =  (LinearLayout) v.findViewById(R.id.pcRecordLay);
		}
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
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					if (startFlag == CMainActivity.STARTFLAG_MYTASK) { 
						//司机任务详情
						json = UrlUtil.getAssignCarInfo(ECApplication.getInstance().getV3Address(), map);
					} else {
						json = UrlUtil.getOrderCarInfo(ECApplication.getInstance().getV3Address(), map);
					}
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
		if (startFlag == CMainActivity.STARTFLAG_MYTASK) {
			// 司机查看任务详情
			assignCarInfo = new Gson().fromJson(json, AssignCarInfo.class);
			realCountTV.setText(assignCarInfo.getRealCount());
			realAddressTV.setText(assignCarInfo.getRealAddress());
			realTimeTV.setText(assignCarInfo.getRealTime());
			noteTV.setText(assignCarInfo.getNote());
			
			if (assignCarInfo.getRealAddress() == null) {
				dirverFeedBackLay.setVisibility(View.GONE);
			}
			//循环加载订车人信息。。!
			if (assignCarInfo.getOaCarData().size() > 0) {
				int childCount = orderUserLay.getChildCount();
				int index = -1;
				for (final zhwx.ui.dcapp.carmanage.model.AssignCarInfo.OaCarData carData : assignCarInfo.getOaCarData()) {
					index++;
					LinearLayout carRecordItem = null;
					if(index < childCount){
						carRecordItem = (LinearLayout) orderUserLay.getChildAt(index);
					}else{
						carRecordItem = (LinearLayout) View.inflate(getActivity(), R.layout.layout_cm_order_item, null);
						orderUserLay.addView(carRecordItem);
					}
					carRecordItem.setVisibility(View.VISIBLE);
					TextView orderUserTV = (TextView) carRecordItem.findViewById(R.id.orderUserTV);
					TextView telephoneTV = (TextView) carRecordItem.findViewById(R.id.telephoneTV);
					TextView departmentNameTV = (TextView) carRecordItem.findViewById(R.id.departmentNameTV);
					TextView userDateTV = (TextView) carRecordItem.findViewById(R.id.userDateTV);
					TextView arriveTimeTV = (TextView) carRecordItem.findViewById(R.id.arriveTimeTV);
					TextView addressTV = (TextView) carRecordItem.findViewById(R.id.addressTV);
					TextView userCountTV = (TextView) carRecordItem.findViewById(R.id.userCountTV);
					TextView personListTV = (TextView) carRecordItem.findViewById(R.id.personListTV);
					TextView reasonTV = (TextView) carRecordItem.findViewById(R.id.reasonTV);
					TextView instructionTV = (TextView) carRecordItem.findViewById(R.id.instructionTV);
					TextView fanchengFlagTV = (TextView) carRecordItem.findViewById(R.id.fanchengFlagTV);
					TextView backUseDateET = (TextView) carRecordItem.findViewById(R.id.backUseDateET);
					TextView backArriveTimeET = (TextView) carRecordItem.findViewById(R.id.backArriveTimeET);
					TextView backAddressET = (TextView) carRecordItem.findViewById(R.id.backAddressET);
					TextView backUserCountET = (TextView) carRecordItem.findViewById(R.id.backUserCountET);
					TextView backCarUserET = (TextView) carRecordItem.findViewById(R.id.backCarUserET);
					LinearLayout backContenerLay = (LinearLayout) carRecordItem.findViewById(R.id.backContenerLay);
					
					//派车单信息
					TextView dirverNameTV = (TextView) carRecordItem.findViewById(R.id.dirverNameTV);
					dirverNameTV.setText(assignCarInfo.getDriver());
					TextView useCarTV = (TextView) carRecordItem.findViewById(R.id.useCarTV);
					useCarTV.setText(assignCarInfo.getCarName()+ "   " +assignCarInfo.getCarNum());
					TextView useTimeTV = (TextView) carRecordItem.findViewById(R.id.useTimeTV);
					useTimeTV.setText(carData.getUseTime());
					TextView useAddressTV = (TextView) carRecordItem.findViewById(R.id.useAddressTV);
					useAddressTV.setText(carData.getUseAddress());
					LinearLayout feedBackLay = (LinearLayout) carRecordItem.findViewById(R.id.feedBackLay);
					// 评价信息
					if ("1".equals(carData.getFeedBackFlag())) {
						feedBackLay.setVisibility(View.VISIBLE);
						LinearLayout startLay = (LinearLayout) carRecordItem.findViewById(R.id.starLay);
						int childCount_a = startLay.getChildCount();
						int index_a = -1;
						for (zhwx.ui.dcapp.carmanage.model.AssignCarInfo.StarData starData : carData.getStarData()) {
							index_a++;
							LinearLayout startDataItem = null;
							if(index_a < childCount_a){
								startDataItem = (LinearLayout) startLay.getChildAt(index);
							}else{
								startDataItem = (LinearLayout) View.inflate(getActivity(), R.layout.list_item_cm_stardata, null);
								startLay.addView(startDataItem);
							}
							startDataItem.setVisibility(View.VISIBLE);
							TextView starNameTV = (TextView) startDataItem.findViewById(R.id.starNameTV);
							starNameTV.setText(starData.getName());
							RatingBar starRB = (RatingBar) startDataItem.findViewById(R.id.starRB);
							starRB.setRating(Float.parseFloat(starData.getValue()));
							TextView feedBackAdviceTV = (TextView) carRecordItem.findViewById(R.id.feedBackAdviceTV);
							feedBackAdviceTV.setText(StringUtil.isBlank(carData.getFeedBackAdvice())?"无":carData.getFeedBackAdvice());
						}
						for (index_a++; index_a < childCount; index_a++) {  //把未使用的复用view设置成不可见
							pcRecordLay.getChildAt(index).setVisibility(View.GONE);
						}
					} else {
						feedBackLay.setVisibility(View.GONE);
					}
					//如果司机是自己  就不显示联系方式了
					LinearLayout linkLay = (LinearLayout) carRecordItem.findViewById(R.id.linkLay_ref);
					if (ECApplication.getInstance().getCurrentIMUser().getMobileNum().equals(carData.getOrderUser())) {
						linkLay.setVisibility(View.GONE);
					} else {
						linkLay.setVisibility(View.VISIBLE);
					}
					orderUserTV.setText(carData.getOrderUser());
					telephoneTV.setText(carData.getTelephone());
					departmentNameTV.setText(carData.getDepartmentName());
					userDateTV.setText(carData.getUserDate());
					arriveTimeTV.setText(carData.getArriveTime());
					addressTV.setText(carData.getAddress());
					userCountTV.setText(carData.getUserCount());
					personListTV.setText(StringUtil.isBlank(carData.getPersonList())?"无":carData.getPersonList());
					reasonTV.setText(StringUtil.isBlank(carData.getReason())?"无":carData.getReason());
					instructionTV.setText(StringUtil.isBlank(carData.getInstruction())?"无":carData.getInstruction());
					fanchengFlagTV.setText(StringUtil.isNotBlank(carData.getBackPerson())?"是":"否");
					//  返程信息
					if (StringUtil.isNotBlank(carData.getBackCount())) {
						backContenerLay.setVisibility(View.VISIBLE);
						backUseDateET.setText(carData.getBackDate());
						backArriveTimeET.setText(carData.getBackTime());
						backAddressET.setText(carData.getBackAddress());
						backUserCountET.setText(carData.getBackCount());
						backCarUserET.setText(carData.getBackPerson());
					}
					LinearLayout onCallLay = (LinearLayout) carRecordItem.findViewById(R.id.onCallLay);
					onCallLay.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							if (StringUtil.isBlank(carData.getTelephone())) {
								ToastUtil.showMessage("无可用电话号码");
								return;		
							}
							Intent phoneIntent=new Intent("android.intent.action.DIAL", Uri.parse("tel:" + carData.getTelephone()));
							startActivity(phoneIntent);
						}
					});
					LinearLayout onMsgLay = (LinearLayout) carRecordItem.findViewById(R.id.onMsgLay);
					onMsgLay.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							if (StringUtil.isBlank(carData.getTelephone())) {
								ToastUtil.showMessage("无可用电话号码");
								return;		
							}
							Uri smsToUri = Uri.parse("smsto:" + carData.getTelephone());
							Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
							intent.putExtra("sms_body", "");
							startActivity(intent);
						}
					});
					onIMLay = (LinearLayout) carRecordItem.findViewById(R.id.onIMLay);
					onIMLay.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							ToastUtil.showMessage("暂未开通");
						}
					});
				}
				for (index++; index < childCount; index++) {  //把未使用的复用view设置成不可见
					orderUserLay.getChildAt(index).setVisibility(View.GONE);
				}
			}
			
		} else {
			orderDetail = new Gson().fromJson(json, OrderDetail.class);
			if (ECApplication.getInstance().getCurrentIMUser().getMobileNum().equals(orderDetail.getTelephone())) {
				linkLay.setVisibility(View.GONE);
			} else {
				linkLay.setVisibility(View.VISIBLE);
			}
			orderUserTV.setText(orderDetail.getOrderUser());
			telephoneTV.setText(orderDetail.getTelephone());
			departmentNameTV.setText(orderDetail.getDepartmentName());
			userDateTV.setText(orderDetail.getUserDate());
			arriveTimeTV.setText(orderDetail.getArriveTime());
			addressTV.setText(orderDetail.getAddress());
			userCountTV.setText(orderDetail.getUserCount());
			personListTV.setText(StringUtil.isBlank(orderDetail.getPersonList())?"无":orderDetail.getPersonList());
			reasonTV.setText(StringUtil.isBlank(orderDetail.getReason())?"无":orderDetail.getReason());
			instructionTV.setText(StringUtil.isBlank(orderDetail.getInstruction())?"无":orderDetail.getInstruction());
			if ("1".equals(orderDetail.getCheckFlag())) {
				checkLay.setVisibility(View.VISIBLE);
				if ("未审核".equals(orderDetail.getCheckStatusView())) {
					checkLay.setVisibility(View.GONE);
				}
				checkTextTV.setText(orderDetail.getCheckStatusView());
				checkerNameTV.setText(orderDetail.getCheckUser());
//				checkTimeTV.setText(orderDetail.getCheckStatusView());
				checkAdviceTV.setText(orderDetail.getCheckAdvice());
				if ("审核通过".equals(orderDetail.getCheckStatusView())) {
					checkTextTV.setTextColor(Color.parseColor("#00a63c"));
				} else {
					checkTextTV.setTextColor(Color.RED);
				}
			} else {
				checkLay.setVisibility(View.GONE);
			}
			
			fanchengFlagTV.setText(StringUtil.isNotBlank(orderDetail.getBackPerson())?"是":"否");
			if (StringUtil.isNotBlank(orderDetail.getBackCount())) {
				backContenerLay.setVisibility(View.VISIBLE);
				backUseDateET.setText(orderDetail.getBackDate());
				backArriveTimeET.setText(orderDetail.getBackTime());
				backAddressET.setText(orderDetail.getBackAddress());
				backUserCountET.setText(orderDetail.getBackCount());
				backCarUserET.setText(orderDetail.getBackPerson());
			}
			/** 前方高能！ */
			if (orderDetail.getOaCarData().size() > 0) {
				// 派车记录
				int childCount = pcRecordLay.getChildCount();
				int index = -1;
				for (final OaCarData carData : orderDetail.getOaCarData()) {
					index++;
					LinearLayout carRecordItem = null;
					if(index < childCount){
						carRecordItem = (LinearLayout) pcRecordLay.getChildAt(index);
					}else{
						carRecordItem = (LinearLayout) View.inflate(getActivity(), R.layout.list_item_cm_pcjl, null);
						pcRecordLay.addView(carRecordItem);
					}
					TextView orderTitleTV = (TextView) carRecordItem.findViewById(R.id.orderTitleTV);
					orderTitleTV.setText("派车单 " + (orderDetail.getOaCarData().size() <= 1? "" : (index + 1)));
					
					//司机反馈
					LinearLayout dirverFeedBackLay = (LinearLayout) carRecordItem.findViewById(R.id.dirverFeedBackLay);
					if (StringUtil.isBlank(carData.getRealCount())) {
						dirverFeedBackLay.setVisibility(View.GONE);
					}
					TextView realCountTV = (TextView) carRecordItem.findViewById(R.id.realCountTV);
					realCountTV.setText(carData.getRealCount());
					TextView realAddressTV = (TextView) carRecordItem.findViewById(R.id.realAddressTV);
					realAddressTV.setText(carData.getRealAddress());
					TextView realTimeTV = (TextView) carRecordItem.findViewById(R.id.realTimeTV);
					realTimeTV.setText(carData.getRealTime());
					TextView noteTV = (TextView) carRecordItem.findViewById(R.id.noteTV);
					noteTV.setText(carData.getNote());
					LinearLayout linkLay = (LinearLayout) carRecordItem.findViewById(R.id.linkLay_ref);
					if (ECApplication.getInstance().getCurrentIMUser().getMobileNum().equals(carData.getPhone())) {
						linkLay.setVisibility(View.GONE);
					} else {
						linkLay.setVisibility(View.VISIBLE);
					}
					LinearLayout onCallLay = (LinearLayout) carRecordItem.findViewById(R.id.onCallLay);
					onCallLay.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							if (StringUtil.isBlank(carData.getPhone())) {
								ToastUtil.showMessage("无可用电话号码");
								return;		
							}
							Intent phoneIntent=new Intent("android.intent.action.DIAL", Uri.parse("tel:" + carData.getPhone()));
							startActivity(phoneIntent);
						}
					});
					LinearLayout onMsgLay = (LinearLayout) carRecordItem.findViewById(R.id.onMsgLay);
					onMsgLay.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							if (StringUtil.isBlank(carData.getPhone())) {
								ToastUtil.showMessage("无可用电话号码");
								return;		
							}
							Uri smsToUri = Uri.parse("smsto:" + carData.getPhone());
							Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
							intent.putExtra("sms_body", "");
							startActivity(intent);
						}
					});
					onIMLay = (LinearLayout) carRecordItem.findViewById(R.id.onIMLay);
					onIMLay.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							ToastUtil.showMessage("暂未开通");
						}
					});
					carRecordItem.setVisibility(View.VISIBLE);
					showData(carData, carRecordItem);
				}
				for (index++; index < childCount; index++) {  //把未使用的复用view设置成不可见
					pcRecordLay.getChildAt(index).setVisibility(View.GONE);
				}
			}
		}
		actionLay.removeAllViews();
		List<TextView> btns = getOrderButtonList(status,evaluateFlag);
		if (btns.size() == 0) {
			actionLay.setVisibility(View.GONE);
		} else {
			for (TextView button : btns) {
				actionLay.addView(button);
			}
		}
		mPostingdialog.dismiss();
	}
	
	public void showData(OaCarData carData, LinearLayout carRecordItem){
		
		TextView dirverNameTV = (TextView) carRecordItem.findViewById(R.id.dirverNameTV);
		dirverNameTV.setText(carData.getDriver());
		TextView useCarTV = (TextView) carRecordItem.findViewById(R.id.useCarTV);
		useCarTV.setText(carData.getCarName()+ "   " +carData.getCarNum());
		TextView useTimeTV = (TextView) carRecordItem.findViewById(R.id.useTimeTV);
		useTimeTV.setText(carData.getUseTime());
		TextView useAddressTV = (TextView) carRecordItem.findViewById(R.id.useAddressTV);
		useAddressTV.setText(carData.getUseAddress());
		LinearLayout feedBackLay = (LinearLayout) carRecordItem.findViewById(R.id.feedBackLay);
		// 评价信息
		if ("1".equals(carData.getFeedBackFlag())) {
			feedBackLay.setVisibility(View.VISIBLE);
			LinearLayout startLay = (LinearLayout) carRecordItem.findViewById(R.id.starLay);
			int childCount = startLay.getChildCount();
			int index = -1;
			for (StarData starData : carData.getStarData()) {
				index++;
				LinearLayout startDataItem = null;
				if(index < childCount){
					startDataItem = (LinearLayout) startLay.getChildAt(index);
				}else{
					startDataItem = (LinearLayout) View.inflate(getActivity(), R.layout.list_item_cm_stardata, null);
					startLay.addView(startDataItem);
				}
				startDataItem.setVisibility(View.VISIBLE);
				TextView starNameTV = (TextView) startDataItem.findViewById(R.id.starNameTV);
				starNameTV.setText(starData.getName());
				RatingBar starRB = (RatingBar) startDataItem.findViewById(R.id.starRB);
				starRB.setRating(Float.parseFloat(starData.getValue()));
				TextView feedBackAdviceTV = (TextView) carRecordItem.findViewById(R.id.feedBackAdviceTV);
				feedBackAdviceTV.setText(StringUtil.isBlank(carData.getFeedBackAdvice())?"无":carData.getFeedBackAdvice());
			}
			for (index++; index < childCount; index++) {  //把未使用的复用view设置成不可见
				pcRecordLay.getChildAt(index).setVisibility(View.GONE);
			}
		} else {
			feedBackLay.setVisibility(View.GONE);
		}
	}
	
	
	public List<TextView> getOrderButtonList(final String status,final String evaluateFlag){
		List<TextView> btnList = new ArrayList<TextView>();
		if (startFlag == CMainActivity.STARTFLAG_MYORDERCAR) {
			TextView pjBT = getOrderButton("评价");
			pjBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					startActivityForResult(new Intent(getActivity(), EvaluateActivity.class).putExtra("orderId", id), 103);
				}
			});
			TextView qxBT = getOrderButton("取消订车单");
			qxBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.question_carmanager_cancel, null, new DialogInterface.OnClickListener() {
						 @Override
			                public void onClick(DialogInterface dialog, int which) {
							 	cancelOrderCar();
			                }
			         });
			        buildAlert.setTitle("取消订车单");
			        buildAlert.show();	
				}
			});
			
			if (status.equals(OrderCarListItem.CHECKSTATUS_FINISH)) {  //待评价
				if ("0".equals(evaluateFlag)) {
					btnList.add(pjBT);
				}
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_CANCEL)) { //已取消
				
			} else {   
				btnList.add(qxBT);
			}
		} else if(startFlag == CMainActivity.STARTFLAG_ORDERCHECK) {

			TextView shBT = getOrderButton("审核");
			shBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					startActivityForResult(new Intent(getActivity(), CheckActivity.class).putExtra("orderId", id), 103);
				}
			});
			TextView pcBT = getOrderButton("派车");
			pcBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					startActivityForResult(new Intent(getActivity(), CarListActivity.class).putExtra("orderId", id), 103);
				} 
			});
			TextView jxpcBT = getOrderButton("继续派车");
			jxpcBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					getActivity().startActivityForResult(new Intent(getActivity(), CarListActivity.class).putExtra("orderId", id), 103);
				}
			});
			TextView jspcBT = getOrderButton("结束派车");
			jspcBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.question_carmanager_finish, null, new DialogInterface.OnClickListener() {
						 @Override
			                public void onClick(DialogInterface dialog, int which) {
							 passOrderCar();
			                }
			         });
			        buildAlert.setTitle("已结束派车");
			        buildAlert.show();	
				}
			});
			TextView qxBT = getOrderButton("取消订车单");
			qxBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.question_carmanager_cancel, null, new DialogInterface.OnClickListener() {
						 @Override
			                public void onClick(DialogInterface dialog, int which) {
							 	cancelOrderCar();
			                }
			         });
			        buildAlert.setTitle("取消订车单");
			        buildAlert.show();	
				}
			});

			TextView byBT = getOrderButton("不予派车");
			byBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					ECAlertDialog buildAlert = ECAlertDialog.buildAlert(getActivity(), R.string.question_carmanager_cancel, null, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							unPassOrderCar();
						}
					});
					buildAlert.setMessage("确定不予派车吗？");
					buildAlert.setTitle("不予派车");
					buildAlert.show();	
				}
			});
			if (status.equals(OrderCarListItem.CHECKSTATUS_CHECK)) {   //审核
				btnList.add(shBT);
//				btnList.add(qxBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_CHECK_UNPASS)) {  //审核未通过
//				btnList.add(pcBT);
				btnList.add(shBT);
//				btnList.add(qxBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_DRAFT)) {  //待派车
				btnList.add(pcBT);
				btnList.add(qxBT);
				btnList.add(byBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_ASSIGNING)) { //派车中
				btnList.add(jxpcBT);
				btnList.add(jspcBT);
				btnList.add(qxBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_PASS)) { //已派车
				btnList.add(qxBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_FINISH)) { //已完成
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_CANCEL)) { //已取消
			}else {
			}
		} else if(startFlag == CMainActivity.STARTFLAG_MYTASK) {

			TextView pjBT = getOrderButton("反馈");
			pjBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					startActivityForResult(new Intent(getActivity(), DirverFeedBackActivity.class).putExtra("orderId", id),103);
				}
			});
			TextView qrBT = getOrderButton("确认出车");
			qrBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					driverEnsureBus();
				}
			});
			TextView jsBT = getOrderButton("结束任务");
			jsBT.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					driverEndTask();
				}
			});
			
			if (status.equals(OrderCarListItem.CHECKSTATUS_DQR)) {  //待确认
				btnList.add(qrBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_DJS)) { //待结束
				btnList.add(jsBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_DPJ)) {  //待评价
				btnList.add(pjBT);
			} else if (status.equals(OrderCarListItem.CHECKSTATUS_CANCEL)) { //已取消
			} else {
			}
		}
		return btnList;
	}

	public TextView getOrderButton (String text) {
		LayoutParams params = new LayoutParams(
			    LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(30));
		params.setMargins(0, 0, DensityUtil.dip2px(10), 0);
		TextView button = new TextView(getActivity());
		button.setText(text);
		button.setTextColor(Color.parseColor("#555555"));
		button.setBackgroundResource(R.drawable.btn_selector_ordercar);
		button.setGravity(Gravity.CENTER);
		button.setLayoutParams(params);
		return button;
	}
	
	/** 取消订车单 */
	public void cancelOrderCar() {
		mPostingdialog = new ECProgressDialog(getActivity(), "正在取消订车单");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.cancelOrderCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("订单已取消");
								getActivity().finish();
							} else {
								ToastUtil.showMessage("操作失败");
							}
							mPostingdialog.dismiss();
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
	
	/** 不予通过 */
	public void unPassOrderCar() {
		mPostingdialog = new ECProgressDialog(getActivity(), "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.unPassOrderCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("操作成功");
							} else {
								ToastUtil.showMessage("操作失败");
							}
							mPostingdialog.dismiss();
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
	
	/** 结束派车 */
	public void passOrderCar() {
		mPostingdialog = new ECProgressDialog(getActivity(), "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.passOrderCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("已结束派车");
								getActivity().finish();
							} else {
								ToastUtil.showMessage("操作失败");
							}
							mPostingdialog.dismiss();
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
	
	/** 删除订车单 */
	public void delOrderCar() {
		mPostingdialog = new ECProgressDialog(getActivity(), "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("id", new ParameterValue(id));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.delOrderCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("订车单已删除");
								getActivity().finish();
							} else {
								ToastUtil.showMessage("操作失败");
							}
							mPostingdialog.dismiss();
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
	
	/** 确认接单 */
	public void driverEnsureBus() {
		mPostingdialog = new ECProgressDialog(getActivity(), "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("assignCarId", new ParameterValue(id));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.driverEnsureBus(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("已确认出车");
								getActivity().finish();
							} else {
								ToastUtil.showMessage("操作失败");
							}
							mPostingdialog.dismiss();
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
	
	/** 结束任务 */
	public void driverEndTask() {
		mPostingdialog = new ECProgressDialog(getActivity(), "正在操作");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("assignCarId", new ParameterValue(id));
		new ProgressThreadWrap(getActivity(), new RunnableWrap() {
			@Override
			public void run() {
				try {
					final String flag = UrlUtil.driverEndTask(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (flag.contains("ok")) {
								ToastUtil.showMessage("已结束任务");
								startActivityForResult(new Intent(getActivity(), DirverFeedBackActivity.class).putExtra("orderId", id),103);
								getActivity().finish();
							} else {
								ToastUtil.showMessage("操作失败");
							}
							mPostingdialog.dismiss();
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
