package zhwx.ui.contact;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;
import com.netease.nim.uikit.contact_selector.activity.ContactEntity;
import com.netease.nim.uikit.contact_selector.activity.ContactSelectActivity;
import com.netease.nim.uikit.contact_selector.activity.DcContactSelectCloseListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import volley.Response;
import volley.VolleyError;
import zhwx.common.base.BaseActivity;
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
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.treelistview.bean.FileBean;
import zhwx.common.view.treelistview.utils.Node;
import zhwx.common.view.treelistview.utils.adapter.TreeListViewAdapter;
import zhwx.ui.imapp.notice.SendNewNoticeActivity;

import static android.R.attr.keycode;

/**
 * @Title: DcContactSelectActivity.java
 * @Package com.lanxum.smscenter.activity
 * @author Li.xin @ 中电和讯
 * @date 2015-11-24 下午1:58:25
 */
public class DcContactSelectActivity extends BaseActivity {

	private Activity context;

	private RequestWithCacheGet mRequestWithCache;

	private String treeJson = "";

	private HashMap<String, ParameterValue> map;

	private ListView mListView;
	
	private ListView searchLV;

	private List<FileBean> datas;
	
	private Handler handler = new Handler();
	
	private ECProgressDialog progressDialog;

	private SimpleTreeListViewAdapterForSelectContact<FileBean> treeListViewAdapter;
	
	public static final int KIND_SCHOOL = 0;
	public static final int KIND_CLASS = 1;

	private EditText searchET;
	
	private List<UserClass> classes = new ArrayList<UserClass>();
	
	private myAdapter searchAdapter = new myAdapter();
	
	private String searchKeyWord = "";
	
	public static Button okBT;
	
	private TextView titleTV;

	public static int kind = 0;
	
	private FrameLayout top_bar;


	/**当前选择联系人位置*/
	public static Map<String, Node> positionMap_SCHOOL = new HashMap<String, Node>();
	public static Map<String, Node> positionMap_CLASS = new HashMap<String, Node>();
	public static Map<String, Node> positionMap = new HashMap<String, Node>();

	public static Map<Integer, Node> parentPositionMap = new HashMap<Integer, Node>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		context = this;
		ContactSelectActivity.context.setOnSelectCloseListener(new DcContactSelectCloseListener() {
			@Override
			public void onSelectClose() {
				positionMap_SCHOOL.clear();
				positionMap_CLASS.clear();
				positionMap.clear();
				parentPositionMap.clear();
			}
		});
		kind = getIntent().getIntExtra("kind", 0);
		mRequestWithCache = new RequestWithCacheGet(this);
		mListView = (ListView) findViewById(R.id.mListView);
		titleTV = (TextView) findViewById(R.id.titleTV);
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
		},50);
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
				if (SendNewNoticeActivity.positionMap.get(classes.get(position).getId()) != null) {
					remove(classes.get(position).getId());
				} else {
					Node node = new Node();
					node.setName(classes.get(position).getName());
					node.setContactId(classes.get(position).getId());
					put(node.getContactId(), node);
				}
				if (kind == KIND_SCHOOL) {
					okBT.setText("确定\n" + positionMap_SCHOOL.size());
				} else if (kind == KIND_CLASS){
					okBT.setText("确定\n" + positionMap_CLASS.size());
				}
				searchAdapter.notifyDataSetChanged();
			}
		});
		
		okBT = (Button) findViewById(R.id.okBT);
		if (kind == KIND_SCHOOL) {
			okBT.setText("确定\n" + positionMap_SCHOOL.size());
		} else if (kind == KIND_CLASS){
			okBT.setText("确定\n" + positionMap_CLASS.size());
		}
		okBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ContactSelectActivity.contactList = formString();
				finish();
			}
		});
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar);
	}


	public List<ContactEntity> formString () {
		List<ContactEntity> result = new ArrayList<ContactEntity>();
		for (Map.Entry<String, Node> node : positionMap.entrySet()) {
			if (node!=null){
				ContactEntity ce = new ContactEntity();
				ce.setContactId(node.getValue().getContactId());
				ce.setName(node.getValue().getName());
				result.add(ce);
			}
		}
		return result;
	}


	private void getData(int kind) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance().getLoginMap();
		String url = "";
		if(kind == KIND_SCHOOL) {
			map.put("organizationId", new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getOrganizationId()));
			url = UrlUtil.getDepartmentTreeJson(ECApplication.getInstance().getAddress(),map);
			titleTV.setText("组织结构");
		} else if (kind == KIND_CLASS) {
			url = UrlUtil.getClassTreeJson(ECApplication.getInstance().getAddress(),map);
			titleTV.setText("班级");
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
	 * 刷新好友列表数据
	 * 
	 * @param json
	 */
	public void refreshTree(String json) {
		if(json.contains("<html>")){
			ToastUtil.showMessage("数据异常");
			return;
		}
		Gson gson = new Gson();
		List<TreeNode> tree = gson.fromJson(json,new TypeToken<List<TreeNode>>() {}.getType());
		datas = new ArrayList<FileBean>();
		for (int i = 0; i < tree.size(); i++) {
			datas.add(new FileBean(Integer.parseInt(tree.get(i).getId().length() < 5 ? "0" : tree
					.get(i).getId().substring(23, 31)), Integer.parseInt(
							tree.get(i).getParentId().length() < 5 ? "0" : tree.get(i).getParentId().substring(23, 31)),
							tree.get(i).getName(),"", ""));
			if (tree.get(i).getUserList() != null) {
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
										.getAccId(), PinYinUtil.getNameMatchString(user.getName())));
					}	
				}
			}
		}
		try {
			treeListViewAdapter = new SimpleTreeListViewAdapterForSelectContact<FileBean>(
					mListView, (DcContactSelectActivity) context, datas, 0);
			mListView.setAdapter(treeListViewAdapter);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		// ListView点击事件，调用自己的点击回调函数
		treeListViewAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {

			@Override
			public void onClick(Node node, int arg0) {
				if (node.isLeaf()) {
					treeListViewAdapter.onItemClick(node);
					treeListViewAdapter.notifyDataSetChanged();
				}
			}
		});	
		progressDialog.dismiss();
	}
	
	public boolean checkHas(FileBean bean){
		for (int i = 0; i < classes.size(); i++) {
			if (classes.get(i).getId().equals(bean.getContactId())) {
				return true;
			}
		}
		return false;
	}
	
	
	private void searchLocalUser(String name) {
		classes.clear();
		for (FileBean bean : datas) {
			if (bean.getHeadPortraitUrl().toUpperCase().contains(name.trim().toUpperCase())) {
				if(StringUtil.isNotBlank(bean.getContactId())) {
					if(!checkHas(bean)) {
						classes.add(new UserClass(bean.getContactId(),bean.getLabel()));
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
			if(positionMap.get(getItem(position).getId())!=null){
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


	public static void put(String userId , Node node){
		positionMap.put(userId, node);
		if(kind == 0) {
			positionMap_SCHOOL.put(userId, node);
		} else if (kind == 1) {
			positionMap_CLASS.put(userId, node);
		}
	}

	public static void remove(String userId){
		positionMap.remove(userId);
		if(kind == 0) {
			positionMap_SCHOOL.remove(userId);
		} else if (kind == 1) {
			positionMap_CLASS.remove(userId);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keycode == KeyEvent.KEYCODE_BACK) {
			positionMap.clear();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.contacts_select_activity_group;
	}
}
