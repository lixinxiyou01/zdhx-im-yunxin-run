package zhwx.ui.dcapp.storeroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.demo.contact.activity.UserProfileActivity;
import com.netease.nim.uikit.common.util.string.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zhwx.common.model.ParameterValue;
import zhwx.common.util.DensityUtil;
import zhwx.common.util.IMUtils;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.ui.contact.SearchFriendResult;
import zhwx.ui.dcapp.assets.model.AllAssets;
import zhwx.ui.dcapp.storeroom.model.GetOutBean;


public class SearchGetOutActivity extends Activity {
	private Activity context;
	private EditText editText;
	private HashMap<String, ParameterValue> map;
	private String searcheResult = "";
	//过滤出字母、数字和中文的正则表达式
	private String regex = "[^(a-zA-Z\\u4e00-\\u9fa5)]"; 
	
	private Handler handler = new Handler();
	
	private ListView resultLV;
	  /**查看名片*/
    public static final int REQUEST_VIEW_CARD = 0x6;
    
    private FrameLayout title;

	private List<GetOutBean> allDataList = new ArrayList<GetOutBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sm_search_getout);
		title = (FrameLayout) findViewById(R.id.title);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = getWindow();
			/*window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);*/
			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			
			int statusBarHeight = IMUtils.getStatusBarHeight(this.getBaseContext());
			title.setPadding(0, statusBarHeight, 0, 0);
		}
		editText = (EditText) findViewById(R.id.edit_note);
		editText.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER){//修改回车键功能
					searchContact(null);
				}
				return false;
			}
		});
		resultLV = (ListView) findViewById(R.id.resultLV);
		resultLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				SearchFriendResult contacts = (SearchFriendResult) parent.getAdapter().getItem(position);
				if (!StringUtil.isEmpty(contacts.getAccId())) {
					UserProfileActivity.start(SearchGetOutActivity.this,contacts.getAccId());
				} else {
					ToastUtil.showMessage("无此用户和信息");
				}
			}
		});
	}
	
	/**
	 * 查找contact
	 * @param v
	 */
	public void searchContact(View v) {
		String toAddUsername2 = editText.getEditableText().toString();
		if(toAddUsername2.length()==0){
			editText.setError(Html.fromHtml("<font color=#808183>" + "查找内容不能为空" + "</font>"));
			return;
		}
		Pattern p = Pattern.compile(regex);     
	    Matcher m = p.matcher(toAddUsername2);
		if(!m.find()){
			searcheFriend(toAddUsername2);
		}else{
			ToastUtil.showMessage("请输入中文或字母");
		}
	}	
	
	private void searcheFriend(String toAddUsername2) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		map.put("userName", new ParameterValue(toAddUsername2));
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("正在查找…");
		pd.setCancelable(false);
		pd.show();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					searcheResult = UrlUtil.searchGetOutByUserName(ECApplication.getInstance().getAddress(),map);  //发送请求
					handler.postDelayed(new Runnable() {
						public void run() { 
							System.out.println(searcheResult);
							if(searcheResult.contains("html")){
								ToastUtil.showMessage("数据错误");
								return;
							}
							if(!searcheResult.contains("name")){
								ToastUtil.showMessage("未找到");
								OrderListAdapter adapter = (OrderListAdapter) resultLV.getAdapter();
								if(adapter!=null&&allDataList!=null){
									allDataList.clear();
									adapter.notifyDataSetChanged();
								}
								pd.dismiss();
								return;
							}
							// 先隐藏键盘
							((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(SearchGetOutActivity.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
							Gson gson = new Gson();
							allDataList = gson.fromJson(searcheResult, new TypeToken<List<GetOutBean>>() {}.getType());
							resultLV.setAdapter(new OrderListAdapter(context));
							pd.dismiss();
						}
					}, 5);
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.showMessage("未找到");
					pd.dismiss();
				}
			}
		}).start();
	}

	public void back(View v) {
		finish();
	}

	public class OrderListAdapter extends BaseAdapter {

		public OrderListAdapter(Context context, List<AllAssets> list,
								int listFlag) {
			super();
		}
		public OrderListAdapter(Context context) {
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
		public GetOutBean getItem(int position) {
			return allDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {

				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sm_getout, null);
				holder = new ViewHolder();
				holder.smCodeTV = (TextView) convertView.findViewById(R.id.smCodeTV);
				holder.departmentNameTV = (TextView) convertView.findViewById(R.id.departmentNameTV);
				holder.smApplyDateTV = (TextView) convertView.findViewById(R.id.smApplyDateTV);
				holder.outKindTV = (TextView) convertView.findViewById(R.id.outKindTV);
				holder.signViewTV = (TextView) convertView.findViewById(R.id.signViewTV);
				holder.receiverNameTV = (TextView) convertView.findViewById(R.id.receiverNameTV);
				holder.buttonContentLay = (LinearLayout) convertView.findViewById(R.id.buttonContentLay);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.smCodeTV.setText(getItem(position).getCode());
			holder.departmentNameTV.setText(getItem(position).getWarehouseName());
			holder.smApplyDateTV.setText(getItem(position).getDate());
			holder.outKindTV.setText(getItem(position).getOutKindValue());
			holder.receiverNameTV.setText(getItem(position).getReceiverName());
			if (!getItem(position).isSignatureFlag()) {
				holder.signViewTV.setText("未签字");
				holder.signViewTV.setTextColor(Color.RED);
			} else {
				holder.signViewTV.setText("已签字");
				holder.signViewTV.setTextColor(Color.parseColor("#3573a2"));
			}

			//动态添加操作按钮
			holder.buttonContentLay.removeAllViews();
			List<TextView> btns = getOrderButtonList(position);
			for (TextView button : btns) {
				holder.buttonContentLay.addView(button);
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
			private TextView signViewTV,departmentNameTV,smApplyDateTV,outKindTV,smCodeTV,receiverNameTV;
			private LinearLayout buttonContentLay;
		}

	}

	public List<TextView> getOrderButtonList(final int position){
		List<TextView> btnList = new ArrayList<TextView>();
		TextView ckBT = getOrderButton("查看");
		ckBT.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, GetOutDetailActivity.class).putExtra("id", allDataList.get(position).getId()));
			}
		});
		TextView bqBT = getOrderButton("补签");
		bqBT.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(context, SReSingActivity.class).putExtra("id", allDataList.get(position).getId()),111);
			}
		});
		if(!allDataList.get(position).isSignatureFlag()) {
			btnList.add(bqBT);
		}
		btnList.add(ckBT);
		return btnList;
	}

	public TextView getOrderButton (String text) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(30));
		params.setMargins(0, 0, DensityUtil.dip2px(10), 0);
		TextView button = new TextView(context);
		button.setText(text);
		button.setTextColor(Color.parseColor("#555555"));
		button.setBackgroundResource(R.drawable.btn_selector_ordercar);
		button.setGravity(Gravity.CENTER);
		button.setLayoutParams(params);
		return button;
	}
}
