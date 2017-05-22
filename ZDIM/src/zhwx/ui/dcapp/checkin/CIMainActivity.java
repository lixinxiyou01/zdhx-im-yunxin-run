package zhwx.ui.dcapp.checkin;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.permission.MPermission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import onekeyshare.OnekeyShare;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.DateUtil;
import zhwx.common.util.LocationUtils;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECListDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.ui.dcapp.checkin.model.Attendance;

/**   
 * @Title: CIMainActivity.java 
 * @Package com.zdhx.edu.im.ui.v3.checkin
 * @author Li.xin @ zdhx
 * @date 2016年9月18日 下午12:30:47 
 */
public class CIMainActivity extends BaseActivity implements OnClickListener {
	
	private Activity context;
	
	public static String addrStr = "";
	
	private LocationUtils locationService;
	
	private TextView resultTV;
	
	private TextView addressTV;
	
	private List<String> names = new ArrayList<String>();
	
	private int addressPositios = 0;
	
	private TextView timeTV,shareTV;
	
	private HashMap<String, ParameterValue> map;
	
	private Handler handler = new Handler();
	
	private ECProgressDialog mPostingdialog;
	
	private String json;
	
	private String signJson;
	
	private RelativeLayout rootLay,shangLay,xiaLay,circleLay,singOutLay;
	
	private TextView shangTimeTV,xiaTimeTV,shangAddressTV,xiaAddressTV,shangRefreshTV
					,xiaRefreshTV,noteTV,singTextTV,dayTimeTV,shangbuTV;
	
	private ImageView shangIV,xiaIV;
	
	private LinearLayout singLay;
	
	private static final int msgKey1 = 1;
	
	private Attendance attendance;
	
	private ImageLoader imageLoader;
	
	private String streetName = "签到地点";
	
	private String cityName = "北京";
	
	private String localCity = "北京";

	private String COMPANYADDRESS = "金域国际中心A座";

	private String COMPANYNAME = "中电和讯科技有限公司";

	private boolean xiaFlag = false;
	
	private int lineTime = 12; //自动切换成下班的时间
	
	private int endTime = 17; //下班时间

	private final int BASIC_PERMISSION_REQUEST_CODE = 100;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case msgKey1:
				timeTV.setText(DateUtil.getCurrTimeString() +  " " +DateUtil.getWeekStrEn(DateUtil.getCurrDateString()));
				if (attendance!=null) {
					if (StringUtil.isBlank(attendance.getStartTime())&&StringUtil.isBlank(attendance.getEndTime())) {
						formCurrentView();
					} else {
						formCurrentViewShang();
					}
				} 
				break;

			default:
				break;
			}
		}
	};
	
	public class TimeThread extends Thread {
        @Override
        public void run () {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while(true);
        }
    }

	@Override
	protected int getLayoutId() {
		return R.layout.activity_cimain;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setBackGroundColor(R.color.main_bg_checkin);
		getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, "统计","考勤", this);
		context = this;
		imageLoader = new ImageLoader(context);
		initView();
		getData();
		new TimeThread().start();
		requestBasicPermission();
		getLocation();

//		ToastUtil.showMessage(new WifiUtil(context).getWifiName());
	}


	/**
	 * 基本权限管理
	 */
	private void requestBasicPermission() {
		MPermission.with(CIMainActivity.this).addRequestCode(BASIC_PERMISSION_REQUEST_CODE).permissions(
						Manifest.permission.READ_PHONE_STATE,
						Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE
				)
				.request();
	}


	private void getData() {
		getNotice();  
	}
	
	private void getNotice(){
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					json = UrlUtil.attendance(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData1(json);
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

	private void refreshData1(String json) {
		mPostingdialog.dismiss();
		if (json.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		attendance = new Gson().fromJson(json, Attendance.class);
		rootLay.setVisibility(View.VISIBLE);
		if ("success".equals(attendance.getStatus())) {
			if(StringUtil.isBlank(attendance.getStartTime())) {
				if (StringUtil.isBlank(attendance.getEndTime())) {  // 没上没下  走一次时间线判断方法(超过12点就自动变成下班,上班栏显示补签样式；没超过12点正常显示)
					shangLay.setVisibility(View.GONE);
					xiaLay.setVisibility(View.GONE);
					if(!attendance.getStartTime().contains("补签")) {
						shangRefreshTV.setVisibility(View.VISIBLE);
					}else {
						shangRefreshTV.setVisibility(View.INVISIBLE);
					}
					noteTV.setText("未考勤"); 
					formCurrentView();
				} else { 											//没上有下    上班时间显示补签  下班正常  按钮显示下班
					noteTV.setText("今日考勤");
					shangLay.setVisibility(View.VISIBLE);
					shangTimeTV.setText("上班未考勤 ");
					shangAddressTV.setText("暂无");
					if(StringUtil.isNotBlank(attendance.getStartTime())&&!attendance.getStartTime().contains("补签")) {
						shangRefreshTV.setVisibility(View.VISIBLE);
					}else {
						shangRefreshTV.setVisibility(View.INVISIBLE);
					}
					shangbuTV.setVisibility(View.VISIBLE);
					xiaFlag = true;
					singTextTV.setText("下班考勤");
					circleLay.setBackgroundResource(R.drawable.sing_circle_xia);
					xiaLay.setVisibility(View.VISIBLE);
					xiaTimeTV.setText("下班考勤时间 " + attendance.getEndTime());
					xiaAddressTV.setText(attendance.getEndAddress());	
				}
			} else {
				noteTV.setText("今日考勤");
				shangLay.setVisibility(View.VISIBLE);
				shangTimeTV.setText("上班考勤时间 " + attendance.getStartTime());
				shangAddressTV.setText(attendance.getStartAddress());
				if(!attendance.getStartTime().contains("补签")) {
					shangRefreshTV.setVisibility(View.VISIBLE);
				}else {
					shangRefreshTV.setVisibility(View.INVISIBLE);
				}
				shangbuTV.setVisibility(View.INVISIBLE);
				xiaFlag = true;
				singTextTV.setText("下班考勤");
				circleLay.setBackgroundResource(R.drawable.sing_circle_xia);
				if(StringUtil.isBlank(attendance.getEndTime())) {  //有上没下    正常显示数据
					xiaLay.setVisibility(View.GONE);
				}else {
					xiaLay.setVisibility(View.VISIBLE); 		   //有上有下    正常显示数据
					xiaTimeTV.setText("下班考勤时间 " + attendance.getEndTime());
					xiaAddressTV.setText(attendance.getEndAddress());
				}
			}
		}
	}
	
	/**
	 * 考勤
	        参数：flag -- start 上班  end下班
       address 
       userId
	 */
	private void sign(String flag){
		
		if (addressTV.getText().toString().contains("正在定位")) {
			ToastUtil.showMessage("未获取到定位信息，请稍后重试");
			return;
		}
		singTextTV.setText("正在考勤");
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
//		map.put("userId", new ParameterValue("20150520125211813393070681955161"));
		map.put("flag", new ParameterValue(flag));
		map.put("equipType", new ParameterValue(android.os.Build.MODEL));
		String address = "";
		if (!cityName.contains(localCity)) { //不在北京考勤的  加上城市信息
			address += cityName + (streetName + addressTV.getText().toString());
		} else {
			address += (streetName + addressTV.getText().toString());
		}
		map.put("address", new ParameterValue(address));
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					signJson = UrlUtil.doAttendance(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (signJson.contains("success")) {
								ToastUtil.showMessage("考勤成功！");
								getData();
							}
							System.out.println(signJson);
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("请求失败，请稍后重试");
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							if(StringUtil.isBlank(attendance.getStartTime())) {
								singTextTV.setText("上班考勤");
							} else {
								singTextTV.setText("下班考勤");
							}
						}
					}, 5);
				}
			}
		}).start();
	}
	/**
	 * 
	 */
	private void initView() {
		resultTV = (TextView) findViewById(R.id.resultTV);
		addressTV = (TextView) findViewById(R.id.addressTV);
		timeTV = (TextView) findViewById(R.id.timeTV);
		shareTV = (TextView) findViewById(R.id.shareTV);
		shareTV.setOnClickListener(this);
		noteTV = (TextView) findViewById(R.id.noteTV);
		shangTimeTV = (TextView) findViewById(R.id.shangTimeTV);
		xiaTimeTV = (TextView) findViewById(R.id.xiaTimeTV);
		shangAddressTV = (TextView) findViewById(R.id.shangAddressTV);
		xiaAddressTV = (TextView) findViewById(R.id.xiaAddressTV);
		singTextTV = (TextView) findViewById(R.id.singTextTV);
		dayTimeTV = (TextView) findViewById(R.id.dayTimeTV);
		shangRefreshTV = (TextView) findViewById(R.id.shangRefreshTV);
		shangRefreshTV.setOnClickListener(this);
		shangbuTV = (TextView) findViewById(R.id.shangbuTV);
		shangbuTV.setOnClickListener(this);
		xiaRefreshTV = (TextView) findViewById(R.id.xiaRefreshTV);
		rootLay = (RelativeLayout) findViewById(R.id.rootLay);
		shangLay = (RelativeLayout) findViewById(R.id.shangLay);
		shangLay.setOnClickListener(this);
		circleLay = (RelativeLayout) findViewById(R.id.circleLay);
		xiaLay = (RelativeLayout) findViewById(R.id.xiaLay);
		xiaLay.setOnClickListener(this);
		shangIV = (ImageView) findViewById(R.id.shangIV);
		xiaIV = (ImageView) findViewById(R.id.xiaIV);
		singLay = (LinearLayout) findViewById(R.id.singLay);
		singLay.setOnClickListener(this);
		rootLay.setVisibility(View.INVISIBLE);
		addressTV.setOnClickListener(this);
		singOutLay = (RelativeLayout) findViewById(R.id.singOutLay);
		singOutLay.setOnClickListener(this);
	}

	private void getLocation() {
		// -----------location config ------------
		locationService =  new LocationUtils(context);
		//获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
		locationService.registerListener(mListener);
		//注册监听
		int type = getIntent().getIntExtra("from", 0);
		if (type == 0) {
			locationService.setLocationOption(locationService.getDefaultLocationClientOption());
		} else if (type == 1) {
			locationService.setLocationOption(locationService.getOption());
		}
		locationService.start();// 定位SDK
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if (locationService != null) {
			locationService.stop();
			getLocation();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		locationService.unregisterListener(mListener); //注销掉监听
		locationService.stop(); //停止定位服务
		super.onStop();
	}
	
	/*****
	 * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 *
	 */
	private BDLocationListener mListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
				StringBuffer sb = new StringBuffer(256);
				sb.append("time : ");
				/**
				 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
				 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
				 */
				sb.append(location.getTime());
				sb.append("\nlocType : ");// 定位类型
				sb.append(location.getLocType());
				sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
				sb.append("\nlatitude : ");// 纬度
				sb.append(location.getLatitude());
				sb.append("\nlontitude : ");// 经度
				sb.append(location.getLongitude());
				sb.append("\nradius : ");// 半径
				sb.append(location.getRadius());
				sb.append("\nCountryCode : ");// 国家码
				sb.append(location.getCountryCode());
				sb.append("\nCountry : ");// 国家名称
				sb.append(location.getCountry());
				sb.append("\ncitycode : ");// 城市编码
				sb.append(location.getCityCode());
				sb.append("\ncity : ");// 城市
				if (location.getCity() != null) {
					cityName = location.getCity();
				}
				sb.append(location.getCity());
				sb.append("\nDistrict : ");// 区
				sb.append(location.getDistrict());
				sb.append("\nStreet : ");// 街道
				sb.append(location.getStreet());
				sb.append("\naddr : ");// 地址信息
				sb.append(location.getAddrStr());
				if (location.getDistrict() !=null) {
					streetName = location.getDistrict() + location.getStreet();
				}
				sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果   1室内 2室外*****
				sb.append(location.getUserIndoorState());
				sb.append("\nDirection(not all devices have value): ");
				sb.append(location.getDirection());// 方向
				sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
				sb.append("\nPoi: ");// POI信息
				if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
					names.clear();
					String addressName = "";
					for (int i = 0; i < location.getPoiList().size(); i++) {
						Poi poi = (Poi) location.getPoiList().get(i);
						sb.append(poi.getName() + ";");
						addressName = poi.getName();
						if (COMPANYADDRESS.equals(poi.getName())) {
//							addressName = COMPANYNAME;
							names.add(COMPANYNAME);
						}
						names.add(addressName);
					}
				}
				if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
					sb.append("\nspeed : ");
					sb.append(location.getSpeed());// 速度 单位：km/h
					sb.append("\nsatellite : ");
					sb.append(location.getSatelliteNumber());// 卫星数目
					sb.append("\nheight : ");
					sb.append(location.getAltitude());// 海拔高度 单位：米
					sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
					sb.append("\ndescribe : ");
					sb.append("gps定位成功");
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
					// 运营商信息
				    if (location.hasAltitude()) {// *****如果有海拔高度*****
				        sb.append("\nheight : ");
	                    sb.append(location.getAltitude());// 单位：米
				    }
					sb.append("\noperationers : ");// 运营商信息
					sb.append(location.getOperators());
					sb.append("\ndescribe : ");
					sb.append("网络定位成功");
				} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
					sb.append("\ndescribe : ");
					sb.append("离线定位成功，离线定位结果也是有效的");
				} else if (location.getLocType() == BDLocation.TypeServerError) {
					sb.append("\ndescribe : ");
					sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
				} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
					sb.append("\ndescribe : ");
					sb.append("网络不同导致定位失败，请检查网络是否通畅");
				} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
					sb.append("\ndescribe : ");
					sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
				}
				logMsg(sb.toString());
			}
		}
	};
	
	/**
	 * 显示请求字符串
	 * 
	 * @param str
	 */
	public void logMsg(String str) {
		try {
			if (resultTV != null)
				resultTV.setText(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//三秒定位一次，当第一次定位返回或新定位列表中无已选地点时更新
		if (names.size() > 0) {
			boolean key = false;//已选定位是否在最新的定位列表中
			for (String name : names) {
				if (name.equals(addressTV.getText().toString())) {
					key = true;
					break;
				}
			}
			
			if (!key) {
				addressTV.setText(names.get(0));
			}
		}
	}
	
	public void callShare(boolean shang){
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//		oks.setTitle("考勤报告");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		if (shang) {
			oks.setTitle("上班考勤报告");
			String text = ECApplication.getInstance().getCurrentIMUser().getName() + "于" + attendance.getStartTime() + "在"+  attendance.getStartAddress() + "打卡上班";
			oks.setText(text);
		} else {
			oks.setTitle("下班考勤报告");
			String text = ECApplication.getInstance().getCurrentIMUser().getName() + "于" +  attendance.getEndTime() + "在"+  attendance.getEndAddress() + "打卡下班";
			oks.setText(text);
		}
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath(imageLoader.saveBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_kaoqin)).getAbsolutePath());//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("https://www.pgyer.com/zhwx");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
//		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
//		oks.setSiteUrl("http://sharesdk.cn");
		// 启动分享GUI
		oks.show(this);
	}
	
	// 根据时间改变当前view状态
	public void formCurrentView() {
		if(Integer.parseInt(DateUtil.getCurrTimeString().split(":")[0]) >= lineTime) { //超过12点就自动变成下班
			xiaFlag = true;
			singTextTV.setText("下班考勤");
			circleLay.setBackgroundResource(R.drawable.sing_circle_xia);
			shangLay.setVisibility(View.VISIBLE);
			shangTimeTV.setText("上班未考勤 ");
			shangAddressTV.setText("暂无");
			shangRefreshTV.setVisibility(View.INVISIBLE);
			shangbuTV.setVisibility(View.VISIBLE);
		} else {
			xiaFlag = false;
			singTextTV.setText("上班考勤");
			circleLay.setBackgroundResource(R.drawable.sing_circle);
		}	
	}
	
	// 过了十二点  隐藏更新考勤
	public void formCurrentViewShang() {
		if(Integer.parseInt(DateUtil.getCurrTimeString().split(":")[0]) >= lineTime) { //超过12点就自动变成下班
			shangRefreshTV.setVisibility(View.INVISIBLE);
		} else {
			shangRefreshTV.setVisibility(View.VISIBLE);
		}	
	}
	
	@Override
	protected void onActivityResult(int requsetCode, int resultCode, Intent intent) {
		super.onActivityResult(requsetCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			getNotice();  
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.text_right:
			startActivity(new Intent(context, CIStatisticsActivity.class));
			break;
		case R.id.shangLay:
			if (StringUtil.isNotBlank(attendance.getStartTime())) {
				callShare(true);
			} else {
				ToastUtil.showMessage("没签到分享啥？");
			}
			break;
		case R.id.xiaLay:
			callShare(false);
			break;
		case R.id.singOutLay:
			startActivity(new Intent(context, OutSignActivity.class));
			break;
		case R.id.singLay:
			if (!xiaFlag) {
				sign("start");
			} else {
				if (StringUtil.isBlank(attendance.getEndTime())) {
					if (Integer.parseInt(DateUtil.getCurrTimeString().split(":")[0]) <= endTime) {
						ECAlertDialog buildAlert = ECAlertDialog.buildColorButtonAlert(context, "下班考勤", "#3989fc", "", "取消", "", "确定", "#3989fc", null, new DialogInterface.OnClickListener() {
							 @Override
				             public void onClick(DialogInterface dialog, int which) {
								 sign("end");
				             }
				         });
				         buildAlert.setMessage("还没到加班时间，确定打卡下班吗？");
				         buildAlert.show();  
					} else {
						sign("end");	
					}
				} else {
					ECAlertDialog buildAlert = ECAlertDialog.buildColorButtonAlert(context, "更新下班考勤", "#3989fc", "", "取消", "", "更新", "#3989fc", null, new DialogInterface.OnClickListener() {
						 @Override
			             public void onClick(DialogInterface dialog, int which) {
							 sign("end");
			             }
			         });
			         buildAlert.setMessage("更新下班考勤会覆盖上一次考勤的时间和地点");
			         buildAlert.show();  
				}
			}
			break;
		case R.id.shangRefreshTV:
			
			ECAlertDialog buildAlert = ECAlertDialog.buildColorButtonAlert(context, "更新上班考勤", "#3989fc", "", "取消", "", "更新", "#3989fc", null, new DialogInterface.OnClickListener() {
				 @Override
	             public void onClick(DialogInterface dialog, int which) {
					 sign("start");
	             }
	         });
	         buildAlert.setMessage("更新上班考勤会覆盖上一次考勤的时间和地点");
	         buildAlert.show();  
			break;
		case R.id.shangbuTV:
			if (!addressTV.getText().toString().contains("正在定位")) {
				startActivityForResult(new Intent(context, SupplementSaveActivity.class).putExtra("address", addressTV.getText().toString()), 111);
			} else {
				ToastUtil.showMessage("暂未获取到位置，请等待");
			}
			break;
		case R.id.addressTV:

//		    if (names != null && names.size() != 0) {
                final ECListDialog dialog;
                dialog = new ECListDialog(this , names ,addressPositios);
                dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(Dialog d, int position) {
                        addressPositios = position;
                        dialog.dismiss();
                        addressTV.setText(names.get(position));
                    }
                });
                dialog.setTitle(streetName,"#5e97f6");
                dialog.show();
//            }
//            else {
//                ToastUtil.showMessage("正在定位，请稍等");
//            }
			break;
		}
	}
}
