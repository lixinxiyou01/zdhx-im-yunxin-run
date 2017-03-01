package zhwx.ui.webapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import volley.Response;
import volley.VolleyError;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.model.TreeNode;
import zhwx.common.model.TreeUser;
import zhwx.common.model.UserClass;
import zhwx.common.util.PinYinUtil;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.StringUtil;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.dialog.ECProgressDialog;
import zhwx.common.view.treelistview.bean.FileBean;
import zhwx.common.view.treelistview.utils.Node;
import zhwx.common.view.treelistview.utils.adapter.TreeListViewAdapter;

/**
 * @Title: DcContactSelectActivity.java
 * @Package com.lanxum.smscenter.activity
 * @author Li.xin @ 中电和讯
 * @date 2015-11-24 下午1:58:25
 */
public class WebAppSelectActivity extends BaseActivity {

	private Activity context;

	private RequestWithCacheGet mRequestWithCache;

	private String treeJson = "";

	private HashMap<String, ParameterValue> map;

	private ListView mListView;

	private List<FileBean> datas;

	private Handler handler = new Handler();

	private ECProgressDialog progressDialog;

	private SimpleTreeListViewAdapterForWebApp<FileBean> treeListViewAdapter;

	public static final int KIND_TEACHER = 0;
	public static final int KIND_STUDENT = 1;
	public static final int KIND_PARENT = 2;
	public static final int KIND_GROUP = 3;

	private List<UserClass> classes = new ArrayList<UserClass>();

	private TextView titleTV;

	private FrameLayout top_bar;
	
	private EditText searchET;
	
	private ListView searchLV;
	
	private myAdapter searchAdapter = new myAdapter();
	
	private String searchKeyWord = "";

	private long time1;
	private long time2;
	private long time3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		context = this;
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
				getData(KIND_TEACHER);
			}
		}, 50);
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
				//数据是使用Intent返回
				Intent intent = new Intent();
				//把返回数据存入Intent
				intent.putExtra("userId", classes.get(position).getId());
				intent.putExtra("userName", classes.get(position).getName());
				//设置返回数据
				setResult(RESULT_OK, intent);
				//关闭Activity
				finish();
			}
		});
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar);
	}

	private void getData(int kind) {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance()
				.getV3LoginMap();
		String url = "";
		if (kind == KIND_TEACHER) {
			url = UrlUtil.getTeacherTree(ECApplication.getInstance()
					.getV3Address(), map);
			titleTV.setText("教师");
		}
		treeJson = mRequestWithCache.getRseponse(url, new RequestWithCacheGet.RequestListener() {
			@Override
			public void onResponse(String response) {
				System.out.println(response + "response");
				if (response != null
						&& !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
					refreshTree(response);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		if ((treeJson != null && !treeJson.equals(RequestWithCacheGet.NO_DATA))) {
			refreshTree(treeJson);
		}
	}

	/**
	 * 刷新好友列表数据
	 * 
	 * @param json
	 */
	public void refreshTree(String json) {
		if (json.contains("<html>")) {
			ToastUtil.showMessage("数据异常");
			return;
		}
		time1 = System.currentTimeMillis();
		Gson gson = new Gson();
		List<TreeNode> tree = gson.fromJson(json,
				new TypeToken<List<TreeNode>>() {
				}.getType());
		datas = new ArrayList<FileBean>();
		for (int i = 0; i < tree.size(); i++) {
			datas.add(new FileBean(Integer
					.parseInt(tree.get(i).getId().length() < 5 ? "0" : tree
							.get(i).getId().substring(23, 31)), Integer
					.parseInt(tree.get(i).getParentId().length() < 5 ? "0"
							: tree.get(i).getParentId().substring(23, 31)),
					tree.get(i).getName(), "", ""));
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
						datas.add(new FileBean(1, Integer.parseInt(tree.get(i)
								.getId().length() < 5 ? "0" : tree.get(i)
								.getId().substring(23, 31)), "暂无人员", "123456",
								""));
					}
				} else {
					for (int j = 0; j < tree.get(i).getUserList().size(); j++) {
						TreeUser user = tree.get(i).getUserList().get(j);
						datas.add(new FileBean(Integer.parseInt(user.getId()
								.length() < 5 ? "0" : user.getId().substring(
								25, 31)), Integer.parseInt(tree.get(i).getId()
								.length() < 5 ? "0" : tree.get(i).getId()
								.substring(23, 31)), user.getName(), user
								.getId(), PinYinUtil.getNameMatchString(user
								.getName())));
					}
				}
			}
		}
		time2 = System.currentTimeMillis();
		System.out.println("构造数据：" + (time2 - time1));
		try {
			treeListViewAdapter = new SimpleTreeListViewAdapterForWebApp<FileBean>(
					mListView, (WebAppSelectActivity) context, datas, 0);
			mListView.setAdapter(treeListViewAdapter);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		time3 = System.currentTimeMillis();
		System.out.println("适配数据：" + (time3 - time2));
		// ListView点击事件，调用自己的点击回调函数
		treeListViewAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {

					@Override
					public void onClick(Node node, int arg0) {
						if (node.isLeaf()) {
							//数据是使用Intent返回
							Intent intent = new Intent();
							//把返回数据存入Intent
							intent.putExtra("userId", node.getContactId());
							intent.putExtra("userName", node.getName());
							//设置返回数据
							setResult(RESULT_OK, intent);
							//关闭Activity
							finish();
						}
					}
				});
		progressDialog.dismiss();
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
				convertView = View.inflate(context, R.layout.list_item1, null);
				holder.nameTV = (TextView) convertView.findViewById(R.id.textView1);
				holder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
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
			return convertView;
		}

		class Holder{
			private TextView nameTV;
			private ImageView imageView1;
		}
	}
	
	public boolean checkHas(FileBean bean) {
		for (int i = 0; i < classes.size(); i++) {
			if (classes.get(i).getId().equals(bean.getContactId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.contacts_select_activity_webapp;
	}
}
