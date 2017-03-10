package zhwx.ui.dcapp.carmanage;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.android.datetimepicker.time.TimePickerDialog.OnTimeSetListener;
import com.google.gson.Gson;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.ECContacts;
import zhwx.common.model.ParameterValue;
import zhwx.common.util.DateUtil;
import zhwx.common.util.DensityUtil;
import zhwx.common.util.Log;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.imageLoader.AsyncImageLoader;
import zhwx.common.util.imageLoader.ImageCacheManager;
import zhwx.common.view.dialog.ECAlertDialog;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.taggroup.TagGroup;
import zhwx.common.view.treelistview.utils.Node;
import zhwx.db.ContactSqlManager;
import zhwx.ui.dcapp.carmanage.model.InputBaseInformation;

/**   
 * @Title: RepairsRequestActivity.java
 * @Package zhwx.ui.dcapp.carmanage
 * @author Li.xin @ 中电和讯
 * @date 2016-3-16 下午2:15:48 
 */
public class OrderCarActivity extends BaseActivity implements OnDateSetListener, OnTimeSetListener {
	
	private Activity context;

	private Handler handler = new Handler();

	private ECProgressDialog mPostingdialog;
	
	private HashMap<String, ParameterValue> map;
	
	private String infoJson;
	
	private String orderFlag;
	
	private FrameLayout top_bar;
	
	private TagGroup carUserET;
	
	private TagGroup backCarUserET;
	
	private EditText addressET,userCountET,reasonET,instructionET, backAddressET,backUserCountET;
	
	private EditText carUsersET,backCarUsersET;
	
	private TextView backUseDateET,backArriveTimeET;
	
	private String keyWord = null;
	
    private String regex = "[^(a-zA-Z\\u4e00-\\u9fa5)]"; 
    
    private List<ECContacts> friendResults;
    
    private ListView carUserLV;
    
    private PopupWindow searchPop;
    
    private View searchView;
    
    private String carUserId;
    
    private String departmentId;
    
    public static Map<String, Node> positionMap = new HashMap<String, Node>();
    
    public static Map<String, Node> positionMap_back = new HashMap<String, Node>();
    
    public static Map<Integer, Node> parentPositionMap = new HashMap<Integer, Node>();	
    
    private TextView useDateET,arriveTimeET,userNameTV,phoneTV,departmentTV;
    
    private TextView dateTV,timeTV,addressTV,userCountTV;
    
    private TextView backDateTV,backTimeTV,backAddressTV,backCountTV;
    
    private RadioGroup isBackRG;
    
    private String DATEPICKER_TAG = "datepicker";
    private String TIMEPICKER_TAG = "timepicker";
    private String DATEPICKER_TAG_back = "datepicker_back";
    private String TIMEPICKER_TAG_back = "timepicker_back";
    private String TIMEPICKER_TAG_current = "timepicker";
    
    private Animation shake; //表单必填项抖动
    
    private InputBaseInformation information;
    
    private LinearLayout backContenerLay;
    
    private boolean isBack = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		shake = AnimationUtils.loadAnimation(context, R.anim.shake);//加载动画资源文件
		initView();
		initPopMenu();
		getData();
	}
	
	private void getData() {
		getInfo();   //获取默认信息
	}
	
	private void getInfo(){
		mPostingdialog = new ECProgressDialog(this, "正在获取信息");
		mPostingdialog.show();
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					infoJson = UrlUtil.getInputBaseInformation(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							refreshData(infoJson);
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
	
	private void refreshData(String infoJson) {
		System.out.println(infoJson);
		if (infoJson.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		information = new Gson().fromJson(infoJson, InputBaseInformation.class);
		if (information.getDeptData().size() != 0) {
			departmentTV.setText(information.getDeptData().get(0).getName());
			departmentId = information.getDeptData().get(0).getId();
		}
		mPostingdialog.dismiss();
	}
	private void initView() {
		getTopBarView().setVisibility(View.GONE);
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar,1);
		carUserET = (TagGroup) findViewById(R.id.carUserET);
		carUserET.setOnTagChangeListener(new TagGroup.OnTagChangeListener() {
			
			@Override
			public void onDelete(TagGroup tagGroup, String tag) {
				//  删除人的时候把 map里面的也删掉
				for (Map.Entry<String, Node> entry : OrderCarActivity.positionMap.entrySet()) {
					if (entry.getValue().getName().equals(tag)) {
						Log.e("删除"+ ":" + tag);
						OrderCarActivity.positionMap.remove(entry.getKey());
						break;
					}
				}
			}
			
			@Override
			public void onAppend(TagGroup tagGroup, String tag) {
				
			}
		});
		userNameTV = (TextView) findViewById(R.id.userNameTV);
		userNameTV.setText(ECApplication.getInstance().getCurrentIMUser().getName());
		carUserId = ECApplication.getInstance().getCurrentIMUser().getV3Id();
//		orderNameET.setSelection(orderNameET.getText().length());
//		orderNameET.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				searcheFriend(arg0.toString());
//				keyWord = arg0.toString();
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//					int arg3) {
//			}
//			
//			@Override
//			public void afterTextChanged(Editable arg0) {
//			}
//		});
		phoneTV = (TextView) findViewById(R.id.phoneTV);
		phoneTV.setText(StringUtil.isBlank(ECApplication.getInstance().getCurrentIMUser().getMobileNum())?"无":ECApplication.getInstance().getCurrentIMUser().getMobileNum());
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
				OrderCarActivity.this, calendar.get(java.util.Calendar.YEAR),
				calendar.get(java.util.Calendar.MONTH),
				calendar.get(java.util.Calendar.DAY_OF_MONTH));
		final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
				OrderCarActivity.this, calendar.get(java.util.Calendar.HOUR_OF_DAY),
				calendar.get(java.util.Calendar.MINUTE), true);
		useDateET = (TextView) findViewById(R.id.useDateET);
		useDateET.setText(DateUtil.getFormatDateTommorrow(DateUtil.getCurrDateString()));
		useDateET.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (!datePickerDialog.isAdded()) {
					datePickerDialog.show(getFragmentManager(),DATEPICKER_TAG);
				}
			}
		});
		
		arriveTimeET = (TextView) findViewById(R.id.arriveTimeET);
		arriveTimeET.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (!timePickerDialog.isAdded()) {
					timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);
					TIMEPICKER_TAG_current = TIMEPICKER_TAG;
				}
			}
		});
		
		departmentTV = (TextView) findViewById(R.id.departmentTV);
		addressET = (EditText) findViewById(R.id.addressET);
		userCountET = (EditText) findViewById(R.id.userCountET);
		reasonET = (EditText) findViewById(R.id.reasonET);
		instructionET = (EditText) findViewById(R.id.instructionET);
		backContenerLay = (LinearLayout) findViewById(R.id.backContenerLay);
		isBackRG = (RadioGroup) findViewById(R.id.isBackRG);
		isBackRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				if(checkedId == R.id.noRB) { 
					
                    backContenerLay.setVisibility(View.GONE);
                    isBack = false;
                }else if(checkedId==R.id.yesRB) {
                	
                    backContenerLay.setVisibility(View.VISIBLE);
                    isBack = true;
                } 
			}
		});
		
		backCarUserET = (TagGroup) findViewById(R.id.backCarUserET);
		backCarUserET.setOnTagChangeListener(new TagGroup.OnTagChangeListener() {
			
			@Override
			public void onDelete(TagGroup tagGroup, String tag) {
				//  删除人的时候把 map里面的也删掉
				for (Map.Entry<String, Node> entry : OrderCarActivity.positionMap_back.entrySet()) {
					if (entry.getValue().getName().equals(tag)) {
						OrderCarActivity.positionMap.remove(entry.getKey());
						break;
					}
				}
			}
			
			@Override
			public void onAppend(TagGroup tagGroup, String tag) {
				
			}
		});
		
		backUseDateET = (TextView) findViewById(R.id.backUseDateET);
		backUseDateET.setText(DateUtil.getFormatDateTommorrow(DateUtil.getCurrDateString()));
		backUseDateET.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (!datePickerDialog.isAdded()) {
					datePickerDialog.show(getFragmentManager(),DATEPICKER_TAG_back);
				}
			}
		});
		
				
		backArriveTimeET = (TextView) findViewById(R.id.backArriveTimeET);
		backArriveTimeET.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (!timePickerDialog.isAdded()) {
					timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG_back);
					TIMEPICKER_TAG_current = TIMEPICKER_TAG_back; 
				}
			}
		});
		
		backAddressET = (EditText) findViewById(R.id.backAddressET);
		
		backUserCountET = (EditText) findViewById(R.id.backUserCountET);
		
		dateTV = (TextView) findViewById(R.id.requsetUser);
		timeTV = (TextView) findViewById(R.id.timeTV);
		addressTV = (TextView) findViewById(R.id.addressTV);
		userCountTV = (TextView) findViewById(R.id.userCountTV);
		
		backDateTV = (TextView) findViewById(R.id.backDateTV);
		backTimeTV = (TextView) findViewById(R.id.backTimeTV);
		backAddressTV = (TextView) findViewById(R.id.backAddressTV);
		backCountTV = (TextView) findViewById(R.id.backCountTV);
		
		carUsersET = (EditText) findViewById(R.id.carUsersET);
		backCarUsersET = (EditText) findViewById(R.id.backCarUsersET);
	}
	
	private void searcheFriend(String toAddUsername2) {
		Pattern p = Pattern.compile(regex);     
	    Matcher m = p.matcher(toAddUsername2);
	    if(m.find()){
	    	ToastUtil.showMessage("请输入中文或字母");
	    	return;
	    }
		friendResults = ContactSqlManager.getContactByUsername(toAddUsername2);
		if(friendResults != null){
			for (int i = 0; i < friendResults.size(); i++) {
				if("10089".equals(friendResults.get(i).getContactid())||"10088".equals(friendResults.get(i).getContactid())){
					friendResults.remove(i);
				}
			}
			carUserLV.setAdapter(new addContactAdapter(this));
		}else{
			friendResults = new ArrayList<ECContacts>();
			carUserLV.setAdapter(new addContactAdapter(this));
			searchPop.dismiss();
		}
//		showPop(orderNameET);
	}
	
	public void showPop(View view) {
		if (!searchPop.isShowing()) {
			searchPop.setFocusable(false);
			searchPop.setOutsideTouchable(true);
			searchPop.setAnimationStyle(R.style.PopupAnimation3);
			searchPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			searchPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			searchPop.showAsDropDown(view,0, DensityUtil.dip2px(15));
		}
	}
	
	
	public class addContactAdapter extends BaseAdapter {
		private AsyncImageLoader imageLoader;
		private Bitmap bmp;
		public addContactAdapter(Activity context) {
			super();
			ImageCacheManager cacheMgr = new ImageCacheManager(context);
		    imageLoader = new AsyncImageLoader(context, cacheMgr.getMemoryCache(), cacheMgr.getPlacardFileCache());
		}
		@Override
		public int getCount() {
			return friendResults.size();
		}

		@Override
		public ECContacts getItem(int position) {
			return friendResults.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = View.inflate(context, R.layout.list_search_friend_item, null);
				holder.avatar = (ImageView) convertView.findViewById(R.id.group_card_item_avatar_iv);
				holder.name = (TextView) convertView.findViewById(R.id.group_card_item_nick);
				holder.voipAccount = (TextView) convertView.findViewById(R.id.account);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			
			//将搜索关键字变色显示
			holder.name.setText(getItem(position).getNickname());
			if(getItem(position).getNickname().toUpperCase().contains(keyWord.toUpperCase())){
				SpannableStringBuilder builder = new SpannableStringBuilder(holder.name.getText().toString());
				//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
				ForegroundColorSpan redSpan = new ForegroundColorSpan(OrderCarActivity.this.getResources().getColor(R.color.main_bg));
				
				int start = holder.name.getText().toString().toUpperCase().indexOf(keyWord.toUpperCase());
				int end = start + keyWord.length();
				builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.name.setText(builder);
			}
			holder.voipAccount.setText(getItem(position).getContactid());
			bmp = imageLoader.loadBitmap(holder.avatar, ECApplication.getInstance().getAddress() + getItem(position).getRemark(), true);
			if(bmp == null) {
				holder.avatar.setImageResource(R.drawable.defult_head_img);
			} else {
				holder.avatar.setImageBitmap(bmp);
			}
			addListener(holder,position);
			return convertView;
		}
		
		private void addListener(Holder holder,final int position){
			
		}
		
		class Holder{
			private ImageView avatar;
			private TextView name;
			private TextView voipAccount;
		}
	}
	
	/**
	 * 初始化popupWindow
	 */
	private void initPopMenu() {
		// 顶部pop
		searchView = context.getLayoutInflater().inflate(R.layout.layout_searchuser, null);
		if (searchPop == null) {
			searchPop = new PopupWindow(searchView, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, true);
		}
		if (searchPop.isShowing()) {
			searchPop.dismiss();
		}
		searchPop.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				
			}
		});
		
		carUserLV = (ListView) searchView.findViewById(R.id.searchResultLV);
		carUserLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				final ECContacts contacts = (ECContacts) parent.getAdapter().getItem(position);
				carUserId = contacts.getSubToken();
//				orderNameET.setText(contacts.getNickname());
				searchPop.dismiss();
			}
		});
	}
	
	//添加乘车人
	public void onAddUser(View v) {
		startActivityForResult(new Intent(this, CMSelectActivity.class), 100);
	}
	
	//添加乘车人
	public void onAddBackUser(View v) {
		startActivityForResult(new Intent(this, CMSelectActivity.class), 101);
	}
	
	//乘车地点
	public void onFindAddress(View v) {
		startActivityForResult(new Intent(this, AddressActivity.class), 102);
	}
	
	//提交订车单
	public void onOrder(View v) {
		
		if (!checkOrderList()) {
			ToastUtil.showMessage("缺少必填信息");
			return;
		}
		
		if (Integer.parseInt(userCountET.getText().toString())>1000) {
			userCountTV.startAnimation(shake); 
			ToastUtil.showMessage("乘车人数不得超过1000人!");
			return;
		}
		
		if (isBack) {
			if (Integer.parseInt(backUserCountET.getText().toString())>1000) {
				backCountTV.startAnimation(shake); 
				ToastUtil.showMessage("乘车人数不得超过1000人!");
				return;
			}
			String dateTime = useDateET.getText().toString() + " " + arriveTimeET.getText().toString();
			String backDateTime = backUseDateET.getText().toString() + " " + backArriveTimeET.getText().toString();
			if (StringUtil.isNotBlank(dateTime) && StringUtil.isNotBlank(backDateTime)) {
				if (DateUtil.compareTime(dateTime, backDateTime)) {
					ToastUtil.showMessage("返程日期早于出发日期，请检查填写是否正确");
					return;
				}
			}
		}
		mPostingdialog = new ECProgressDialog(this, "正在提交订车单");
		mPostingdialog.show();
		/*userId,departmentId,phone,useDate,arriveTime,address,userCount,personList,reason,
		instruction backDate,backCount,backAddress,backTime,backPersonList */
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getV3LoginMap();
		map.put("userId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getV3Id()));
		map.put("departmentId", new ParameterValue(departmentId));
		map.put("phone", new ParameterValue(phoneTV.getText().toString()));
		map.put("useDate", new ParameterValue(useDateET.getText().toString()));
		map.put("arriveTime", new ParameterValue(arriveTimeET.getText().toString()));
		map.put("address", new ParameterValue(addressET.getText().toString()));
		map.put("userCount", new ParameterValue(userCountET.getText().toString()));
//		map.put("personList", new ParameterValue(getArryString(carUserET.getTags())));
		map.put("personList", new ParameterValue(carUsersET.getText().toString()));
		map.put("reason", new ParameterValue(reasonET.getText().toString()));
		map.put("instruction", new ParameterValue(instructionET.getText().toString()));
		if (isBack) {
			map.put("backDate", new ParameterValue(backUseDateET.getText().toString()));
			map.put("backCount", new ParameterValue(backUserCountET.getText().toString()));
			map.put("backAddress", new ParameterValue(backAddressET.getText().toString()));
			map.put("backTime", new ParameterValue(backArriveTimeET.getText().toString()));
//			map.put("backPersonList", new ParameterValue(getArryString(backCarUserET.getTags())));
			map.put("backPersonList", new ParameterValue(backCarUsersET.getText().toString()));
		}
		
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					orderFlag = UrlUtil.saveOrderCar(ECApplication.getInstance().getV3Address(), map);
					handler.postDelayed(new Runnable() {
						public void run() {
							if (orderFlag.contains("ok")) {
								ToastUtil.showMessage("订单已提交，请等待派车");
								finish();
							}
						}
					}, 5);
				} catch (IOException e) {
					e.printStackTrace();
					ToastUtil.showMessage("提交出错，请重试");
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
	
	public boolean checkOrderList() {
		boolean pass = true; 
//		dateTV,timeTV,addressTV,userCountTV;
		if (StringUtil.isBlank(useDateET.getText().toString())) {
			dateTV.startAnimation(shake); 
			pass = false;
		}
		if (StringUtil.isBlank(arriveTimeET.getText().toString())) {
			timeTV.startAnimation(shake); 
			pass = false;
		}
		if (StringUtil.isBlank(addressET.getText().toString())) {
			addressTV.startAnimation(shake); 
			pass = false;
		}
		if (StringUtil.isBlank(userCountET.getText().toString())) {
			userCountTV.startAnimation(shake); 
			pass = false;
		} 
		
		if (isBack) {
			if (StringUtil.isBlank(backUseDateET.getText().toString())) {
				backDateTV.startAnimation(shake); 
				pass = false;
			}
			if (StringUtil.isBlank(backArriveTimeET.getText().toString())) {
				backTimeTV.startAnimation(shake); 
				pass = false;
			}
			if (StringUtil.isBlank(backAddressET.getText().toString())) {
				backAddressTV.startAnimation(shake); 
				pass = false;
			}
			if (StringUtil.isBlank(backUserCountET.getText().toString())) {
				backCountTV.startAnimation(shake); 
				pass = false;
			}
		}
		return pass;
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		
        if(requestCode == 100){
			carUserET.setTagsWithOutNull(formArry3());
			
			if (backCarUserET.getTags() == null || backCarUserET.getTags().length == 0) {
				backCarUserET.setTagsWithOutNull(formArry3());
			}
        }
        
		if(resultCode == 101){
			backCarUserET.setTagsWithOutNull(formArry3());
		}
		
		if(requestCode == 102){
			if (data != null) {
				addressET.setText(data.getStringExtra("address") + "");
			}
		}
	}
	
	public List<Node> formArry3() {
		List<Node> nodes = new ArrayList<Node>();
		if(OrderCarActivity.positionMap.size() == 0) {
			return nodes;
		}
		for (Map.Entry<String, Node> entry : OrderCarActivity.positionMap.entrySet()) {
			nodes.add(entry.getValue());
		}
		return nodes;
	}
	
	public String getArryString(String[] nameArry) {
		String names = "";
		for (int i = 0; i < nameArry.length; i++) {
			if (i == nameArry.length - 1) {
				names += nameArry[i];
			}else {
				names += nameArry[i] + "、";
			}
		} 
		
		return names;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		positionMap.clear();
		parentPositionMap.clear();
		super.onDestroy();
	    
	}
	
	@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear,
			int dayOfMonth) {
		String date = year + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
		if (DATEPICKER_TAG.equals(dialog.getTag())) {
			useDateET.setText(date);
		} else {
			backUseDateET.setText(date);
		}
	}
	
	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		String time =  (hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute);
		if (TIMEPICKER_TAG_current.equals(TIMEPICKER_TAG)) {
			arriveTimeET.setText(time);
		} else {
			backArriveTimeET.setText(time);
		}
	}
	
	/**
	 * 退出提示
	 */
	private void showTips() {
		 ECAlertDialog buildAlert = ECAlertDialog.buildAlert(context, R.string.question_carmanager, null, new DialogInterface.OnClickListener() {
			 @Override
                public void onClick(DialogInterface dialog, int which) {
				 	finish();
                }
            });
            buildAlert.setTitle("放弃本次编辑");
            buildAlert.show();
    }
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showTips();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_ordercar;
	}

}
