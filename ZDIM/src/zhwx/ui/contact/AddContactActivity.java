/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zhwx.ui.contact;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
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
import android.widget.ImageView;
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
import zhwx.common.util.IMUtils;
import zhwx.common.util.ProgressThreadWrap;
import zhwx.common.util.RunnableWrap;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.imageLoader.AsyncImageLoader;
import zhwx.common.util.imageLoader.ImageCacheManager;


public class AddContactActivity extends Activity {
	private String tag = "AddContactActivity";
	private Activity context;
	private EditText editText;
	private HashMap<String, ParameterValue> map;
	private String searcheResult = "";
	//过滤出字母、数字和中文的正则表达式
	private String regex = "[^(a-zA-Z\\u4e00-\\u9fa5)]"; 
	
	private Handler handler = new Handler();
	
	private List<SearchFriendResult> friendResults = new ArrayList<SearchFriendResult>();
	
	private ListView resultLV;
	private String keyWord = null;
	
	  /**查看名片*/
    public static final int REQUEST_VIEW_CARD = 0x6;
    
    private FrameLayout title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_contact);
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
				if(keyCode==KeyEvent.KEYCODE_ENTER){//修改回车键功能
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
					UserProfileActivity.start(AddContactActivity.this,contacts.getAccId());
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
			editText.setError(Html.fromHtml("<font color=#808183>"
	                + "查找内容不能为空" + "</font>"));
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
		map.put("organizationId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getOrganizationId()));
		map.put("name", new ParameterValue(toAddUsername2));
		keyWord = toAddUsername2;
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage("正在查找…");
		pd.setCancelable(false);
		pd.show();
		new ProgressThreadWrap(this, new RunnableWrap() {
			@Override
			public void run() {
				try {
					searcheResult = UrlUtil.serachUserWithParam(ECApplication.getInstance().getAddress(),map);  //发送请求
					handler.postDelayed(new Runnable() {
						public void run() { 
							System.out.println(searcheResult);
							if(searcheResult.contains("html")){
								ToastUtil.showMessage("数据错误");
								return;
							}
							if(!searcheResult.contains("name")){
								ToastUtil.showMessage("未找到");
								addContactAdapter adapter = (addContactAdapter) resultLV.getAdapter();
								if(adapter!=null&&friendResults!=null){
									friendResults.clear();
									adapter.notifyDataSetChanged();
								}
								pd.dismiss();
								return;
							}
							// 先隐藏键盘
							((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(AddContactActivity.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
							Gson gson = new Gson();
							friendResults = gson.fromJson(searcheResult, new TypeToken<List<SearchFriendResult>>() {}.getType());
							resultLV.setAdapter(new addContactAdapter(context));
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
	
	public class addContactAdapter extends BaseAdapter{
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
		public SearchFriendResult getItem(int position) {
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
			holder.name.setText(getItem(position).getName());
			holder.voipAccount.setText(getItem(position).getVoipAccount());
			try {
				SpannableStringBuilder builder = new SpannableStringBuilder(holder.name.getText().toString());
				//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
				ForegroundColorSpan redSpan = new ForegroundColorSpan(AddContactActivity.this.getResources().getColor(R.color.main_bg));
				int start = holder.name.getText().toString().indexOf(keyWord);
				int end = start + keyWord.length();
				builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.name.setText(builder);
			} catch (Exception e) {
				e.printStackTrace();
			}
			bmp = imageLoader.loadBitmap(holder.avatar, ECApplication.getInstance().getAddress() + getItem(position).getHeadPortraitUrl(), true);
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
}
