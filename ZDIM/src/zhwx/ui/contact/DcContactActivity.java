package zhwx.ui.contact;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.demo.contact.activity.UserProfileActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import volley.Response;
import volley.VolleyError;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ECContacts;
import zhwx.common.model.ParameterValue;
import zhwx.common.model.TreeNode;
import zhwx.common.model.TreeUser;
import zhwx.common.model.UserClass;
import zhwx.common.util.Log;
import zhwx.common.util.PinYinUtil;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.sortlistview.CharacterParser;
import zhwx.common.view.sortlistview.PhoneModel;
import zhwx.common.view.sortlistview.PinyinComparator;
import zhwx.common.view.sortlistview.SideBar;
import zhwx.common.view.sortlistview.SortAdapter;
import zhwx.common.view.treelistview.adapter.SimpleTreeListViewAdapter;
import zhwx.common.view.treelistview.bean.FileBean;
import zhwx.common.view.treelistview.utils.Node;
import zhwx.common.view.treelistview.utils.adapter.TreeListViewAdapter;
import zhwx.db.ContactSqlManager;
import zhwx.ui.imapp.notice.SendNewNoticeActivity;

/**
 * @Title: DcContactSelectActivity.java
 * @Package com.lanxum.smscenter.activity
 * @author Li.xin @ 中电和讯
 * @date 2015-11-24 下午1:58:25
 */
public class DcContactActivity extends BaseActivity implements View.OnClickListener{

	private Activity context;

	private RequestWithCacheGet mRequestWithCache;

	private String treeJson = "";

	private HashMap<String, ParameterValue> map;

	private ListView mListView;
	
	private ListView searchLV;

	private List<FileBean> datas;
	
	private Handler handler = new Handler();
	
	private ECProgressDialog progressDialog;

	private SimpleTreeListViewAdapter<FileBean> treeListViewAdapter;

	public static final int KIND_SCHOOL = 0; 
	public static final int KIND_CLASS = 1;
	
	private EditText searchET;
	
	private List<UserClass> classes = new ArrayList<UserClass>();

	private ArrayList<ECContacts> shoolContacts;
	
	private myAdapter searchAdapter = new myAdapter();
	
	private String searchKeyWord = "";
	
	private int kind;
	
	private ImageLoader mImageLoader;
	
	private long time1;
	private long time2;
	private long time3;

	/**  汉字转换成拼音的类 */
	private CharacterParser characterParser;

	/** 根据拼音来排列ListView里面的数据类 */
	private PinyinComparator pinyinComparator;

	private SideBar contactSideBar;
	private TextView contactDialog;
	private SortAdapter	schoolAdapter;

	private List<PhoneModel> sourceDateList = new ArrayList<PhoneModel>();

	private SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ECApplication.getInstance());
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		mImageLoader = new ImageLoader(context);
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		kind = getIntent().getIntExtra("kind", 0);
		//		getTopBarView().setBackGroundColor(R.color.main_bg_checkin);
		if(kind == KIND_SCHOOL) {
			getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, "切换","组织结构", this);
		} else {
			getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, "切换","班级", this);
		}
		mRequestWithCache = new RequestWithCacheGet(this);
		mListView = (ListView) findViewById(R.id.mListView);
		searchLV = (ListView) findViewById(R.id.searchLv);
		searchLV.setAdapter(searchAdapter);
		progressDialog = new ECProgressDialog(context);
		progressDialog.setPressText("数据获取中");
		progressDialog.show();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getData(kind);
			}
		},500);
		searchET = (EditText) findViewById(R.id.searchET);
		searchET.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence string, int arg1, int arg2, int arg3) {
				searchKeyWord = string.toString();
				searchLocalUser(string.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				
			}
		});
		searchLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				UserProfileActivity.start(context, classes.get(position).getAccId());
			}
		});

		contactSideBar = (SideBar) findViewById(R.id.sidrbar);
		contactDialog = (TextView) findViewById(R.id.dialog);
		contactSideBar.setTextView(contactDialog);
	}

	private void getData(int kind) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		String url = "";
		if(kind == KIND_SCHOOL) {
			map.put("organizationId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getOrganizationId()));
			url = UrlUtil.getDepartmentTreeJson(ECApplication.getInstance().getAddress(),map);
		} else if (kind == KIND_CLASS) {
			url = UrlUtil.getClassTreeJson(ECApplication.getInstance().getAddress(),map);
		}
		treeJson = mRequestWithCache.getRseponse(url,new RequestWithCacheGet.RequestListener() {
					@Override
					public void onResponse(String response) {
						System.out.println(response+"response");
						if (response != null && !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
							Log.i("新数据:" + response);
							refreshTree(response);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		if ((treeJson != null && !treeJson.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:" + treeJson);
			refreshTree(treeJson);
		}
	}

	/**
	 * 刷新组织列表
	 * @param json
	 */
	public void refreshTree(String json) {
		if(json.contains("<html>")){
			ToastUtil.showMessage("数据异常");
			return;
		}
		shoolContacts = new ArrayList<ECContacts>();
		sourceDateList.clear();
		time1 = System.currentTimeMillis();
		Gson gson = new Gson();
		List<TreeNode> tree = gson.fromJson(json,new TypeToken<List<TreeNode>>() {}.getType());
		datas = new ArrayList<FileBean>();
		for (int i = 0; i < tree.size(); i++) {
			if(false) {
				//自定义分组时候  只有一层  没有userList 所以不是第一层的就是node  第一层的是分组 不能加id
				if("0".equals(tree.get(i).getParentId())) {
					datas.add(new FileBean(Integer.parseInt(tree.get(i).getId().length() < 5 ? "0" : tree
							.get(i).getId().substring(23, 31)), Integer.parseInt(
									tree.get(i).getParentId().length() < 5 ? "0" : tree.get(i).getParentId().substring(23, 31)),
									tree.get(i).getName(),"", ""));
				} else {
					datas.add(new FileBean(Integer.parseInt(tree.get(i).getId().length() < 5 ? "0" : tree
							.get(i).getId().substring(23, 31)), Integer.parseInt(
									tree.get(i).getParentId().length() < 5 ? "0" : tree.get(i).getParentId().substring(23, 31)),
									tree.get(i).getName(),tree.get(i).getId(), PinYinUtil.getNameMatchString(tree.get(i).getName())));
				}
			}
			else { //组织结构
				datas.add(new FileBean(Integer.parseInt(tree.get(i).getId().length() < 5 ? "0" : tree
						.get(i).getId().substring(23, 31)), Integer.parseInt(
								tree.get(i).getParentId().length() < 5 ? "0" : tree.get(i).getParentId().substring(23, 31)),
								tree.get(i).getName(),"", ""));
			}
			if (tree.get(i).getUserList() != null) { //具体部门人员
				// 组织下没人的情况 加个空的
				if (tree.get(i).getUserList().size() == 0) {
					boolean key = false;
					for (int j = 0; j < tree.size(); j++) {
						if (tree.get(i).getId().equals(tree.get(j).getParentId())) {
							key = true;
							break;
						}
					}
					if (!key) {
						datas.add(new FileBean(1, Integer.parseInt(tree.get(i).getId().length() < 5 ? "0" : tree.get(i)
								.getId().substring(23, 31)), "暂无人员", "123456",""));
					}
				} else {
					for (int j = 0; j < tree.get(i).getUserList().size(); j++) {
						TreeUser user = tree.get(i).getUserList().get(j);
						datas.add(new FileBean(Integer.parseInt(user.getId().length() < 5 ? "0" : user.getId().substring(
								25, 31)), Integer.parseInt(tree.get(i).getId()
										.length() < 5 ? "0" : tree.get(i).getId().substring(23, 31)), user.getName(), user
										.getAccId(), user.getHeadPortraitUrl(),PinYinUtil.getNameMatchString(user.getName())));

						ECContacts bean = new ECContacts();
						bean.setNickname(user.getName());
						bean.setContactid(user.getAccId());
						bean.setRemark(user.getHeadPortraitUrl());
						bean.setPinyin(PinYinUtil.getNameMatchString(user.getName()));  //拼音匹配串
						bean.setV3Id(user.getV3Id());
						bean.setImId(user.getId());
						shoolContacts.add(bean);

						//字母导航列表数据
						boolean key = true;
//						for (PhoneModel model : sourceDateList) {
//							if(StringUtil.isNotBlank(model.getVoipId())&&model.getVoipId().equals(user.getAccId())) {
//								key = false;
//								break;
//							}
//						}
						if (key) {
							PhoneModel model = new PhoneModel();
							model.setImgSrc(user.getHeadPortraitUrl());
							model.setName(user.getName());
							model.setVoipId(user.getAccId());
							// 汉字转换成拼音
							String pinyin = characterParser.getSelling(user.getName());
							String sortString = pinyin.substring(0, 1).toUpperCase();
							// 正则表达式，判断首字母是否是英文字母
							if(sortString.matches("[A-Z]")) {
								model.setSortLetters(sortString.toUpperCase());
							} else {
								model.setSortLetters("#");
							}
							sourceDateList.add(model);
						}
					}
				}
			}
		}
		ContactSqlManager.insertContacts(shoolContacts);

		makeOrganzationTree();
	}

	/**
	 * 构建组织结构列表
	 */
	public void makeOrganzationTree(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ECApplication.getInstance());
		if("list".equals(preferences.getString("contactListKind", "tree"))){
			contactSideBar.setVisibility(View.VISIBLE);
			// 设置右侧触摸监听
			contactSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

				@Override
				public void onTouchingLetterChanged(String s) {
					// 该字母首次出现的位置
					int position = schoolAdapter.getPositionForSection(s.charAt(0));
					if(position != -1) {
						mListView.setSelection(position);
					}
				}
			});
			Collections.sort(sourceDateList, pinyinComparator);
			schoolAdapter = new SortAdapter(context, sourceDateList);
			mListView.setAdapter(schoolAdapter);
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
										long id) {
					PhoneModel node = (PhoneModel) parent.getAdapter().getItem(position);
					if (StringUtil.isNotBlank(node.getVoipId())) {
						UserProfileActivity.start(context, node.getVoipId());
					}
				}
			});
		} else {
			contactSideBar.setVisibility(View.INVISIBLE);
			try {
				if(datas == null){
					datas = new ArrayList<FileBean>();
				}
				treeListViewAdapter = new SimpleTreeListViewAdapter<FileBean>(mListView, context, datas, 0);
				mListView.setAdapter(treeListViewAdapter);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			time3 = System.currentTimeMillis();
			System.out.println("适配数据：" +(time3 - time2));
			// ListView点击事件，调用自己的点击回调函数
			treeListViewAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {

				@Override
				public void onClick(Node node, int arg0) {
					if (node.isLeaf()&&(!"123456".equals(node.getContactId()))) {
						UserProfileActivity.start(context, node.getContactId());
					}
				}
			});
		}

		if (progressDialog!=null) {
			progressDialog.dismiss();
		}
	}

	
	public boolean checkHas(FileBean bean){
		for (int i = 0; i < classes.size(); i++) {
			if (classes.get(i).getAccId().equals(bean.getContactId())) {
				return true;
			}
		}
		return false;
	}
	
	
	private void searchLocalUser(String name) {
		classes.clear();
		for (FileBean bean : datas) {
			if (StringUtil.isNotBlank(bean.getRemark())&&bean.getRemark().toUpperCase().contains(name.trim().toUpperCase())) {
				if(StringUtil.isNotBlank(bean.getContactId())) {
					if(!checkHas(bean)) {
						classes.add(new UserClass(bean.getLabel(),bean.getHeadPortraitUrl(),bean.getContactId()));
					}
				}
			}
		}
		if(name.length()!=0) {
			searchLV.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.INVISIBLE);
			searchAdapter.notifyDataSetChanged();
		} else {
			mListView.setVisibility(View.VISIBLE);
			searchLV.setVisibility(View.INVISIBLE);
			treeListViewAdapter.notifyDataSetChanged();
		}
	}
	
	public class myAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return classes.size();
		}

		@Override
		public UserClass getItem(int position) {
			return classes.get(position);
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
				convertView = View.inflate(context, R.layout.list_item2, null);
				holder.nameTV = (TextView) convertView.findViewById(R.id.textView1);
				holder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
				holder.checkBox1 = (CheckBox) convertView.findViewById(R.id.checkBox1);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
				holder.imageView1.setVisibility(View.GONE);
			}
			holder.nameTV.setText(getItem(position).getName());
			holder.checkBox1.setVisibility(View.INVISIBLE);
			mImageLoader.DisplayImage(ECApplication.getInstance().getAddress() + getItem(position).getHeadPortraitUrl(), holder.imageView1, false);
			try {
				SpannableStringBuilder builder = new SpannableStringBuilder(holder.nameTV.getText().toString());
				//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
				ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.main_bg));
				int start = holder.nameTV.getText().toString().indexOf(searchKeyWord);
				int end = start + searchKeyWord.length();
				builder.setSpan(redSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.nameTV.setText(builder);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(SendNewNoticeActivity.positionMap.get(getItem(position).getId())!=null){
				holder.checkBox1.setChecked(true);
			}else{
				holder.checkBox1.setChecked(false);
			}
			return convertView;
		}

		class Holder{
			private TextView nameTV;
			private ImageView imageView1;
			private CheckBox checkBox1;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.text_right:
				//TODO  切换组织结构展示形式
				progressDialog = new ECProgressDialog(context, "切换列表样式");
				progressDialog.show();
				if("tree".equals(preferences.getString("contactListKind", "tree"))){
					preferences.edit().putString("contactListKind","list").commit();
				}else{
					preferences.edit().putString("contactListKind","tree").commit();
				}
				makeOrganzationTree();
				break;
		}
	}

	@Override
	protected int getLayoutId() {
		return R.layout.contacts_activity;
	}
}
