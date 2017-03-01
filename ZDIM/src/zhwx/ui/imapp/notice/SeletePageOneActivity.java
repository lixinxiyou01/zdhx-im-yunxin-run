package zhwx.ui.imapp.notice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import volley.Response;
import volley.VolleyError;
import zhwx.Constant;
import zhwx.common.base.BaseActivity;
import zhwx.common.model.ParameterValue;
import zhwx.common.model.UserClass;
import zhwx.common.util.Log;
import zhwx.common.util.RequestWithCacheGet;
import zhwx.common.util.ToastUtil;
import zhwx.common.util.Tools;
import zhwx.common.util.UrlUtil;
import zhwx.common.view.treelistview.utils.Node;

/**   
 * @Title: SeletePageOneActivity.java 
 * @Package com.lanxum.smscenter.activity 
 * @author Li.xin @ 中电和讯
 * @date 2015-11-27 下午3:52:04 
 */
public class SeletePageOneActivity extends BaseActivity {
	
	private Activity context;
	
	 /**当前选择联系人位置*/
	public static Map<String, Node> positionMap_T = new HashMap<String, Node>();
	public static Map<String, Node> positionMap_S = new HashMap<String, Node>();
	public static Map<String, Node> positionMap_P = new HashMap<String, Node>();
	public static Map<String, Node> positionMap_G = new HashMap<String, Node>();
	   
	public static Map<Integer, Node> parentPositionMap = new HashMap<Integer, Node>();	
	
	public static int kind = 0;

	private static TextView teacherTV,parentTV,studentTV,grouptTV;
	
	private Button button2;
	
	private RequestWithCacheGet mRequestWithCache;
	
	private HashMap<String, ParameterValue> map;
	
	private String resultJson;
	
	private ListView receiveUserLV;
	
	private ScrollView scrollView1;
	
	private Handler handler = new Handler();
	
	private List<UserClass> classes = new ArrayList<UserClass>();
	
	private myAdapter searchAdapter = new myAdapter();
	
	private ProgressBar progressBar1;
	
	private FrameLayout top_bar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		context = this;
		mRequestWithCache = new RequestWithCacheGet(context);
		receiveUserLV = (ListView) findViewById(R.id.receiveUserLV);
		receiveUserLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				if (SendNewNoticeActivity.positionMap.get(classes.get(position).getId()) != null) {
					SendNewNoticeActivity.positionMap.remove(classes.get(position).getId());
					if(Constant.USER_TYPE_STUDENT.equals(classes.get(position).getKind())) {
						positionMap_S.remove(classes.get(position).getId());
					} else if (Constant.USER_TYPE_TEACHER.equals(classes.get(position).getKind())) {
						positionMap_T.remove(classes.get(position).getId());
					} else if (Constant.USER_TYPE_PATRIARCH.equals(classes.get(position).getKind())) {
						positionMap_P.remove(classes.get(position).getId());
					}
				} else {
					Node node = new Node();
					node.setName(classes.get(position).getName());
					node.setContactId(classes.get(position).getId());
					SendNewNoticeActivity.positionMap.put(node.getContactId(), node);
					if(Constant.USER_TYPE_STUDENT.equals(classes.get(position).getKind())) {
						positionMap_S.put(classes.get(position).getId(), node);
					} else if (Constant.USER_TYPE_TEACHER.equals(classes.get(position).getKind())) {
						positionMap_T.put(classes.get(position).getId(), node);
					} else if (Constant.USER_TYPE_PATRIARCH.equals(classes.get(position).getKind())) {
						positionMap_P.put(classes.get(position).getId(), node);
					}
				}
				refresh();
			}
		});
		scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
		teacherTV = (TextView) findViewById(R.id.teacherTV);
		parentTV = (TextView) findViewById(R.id.parentTV);
		studentTV = (TextView) findViewById(R.id.studentTV);
		grouptTV = (TextView) findViewById(R.id.grouptTV);
		button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setResult(100);
				finish();
			}
		});
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getData();
			}
		},50);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar);
	}
	
	private void getData() {
		map = (HashMap<String, ParameterValue>) ECApplication.getInstance()
				.getLoginMap();
		map.put("userId",new ParameterValue(ECApplication.getInstance().getCurrentIMUser().getId()));
		try {
			resultJson = mRequestWithCache.getRseponse(UrlUtil.getRecentReceiveUser(ECApplication.getInstance().getAddress(), map),new RequestWithCacheGet.RequestListener() {

						@Override
						public void onResponse(String response) {
							if (response != null
									&& !response.equals(RequestWithCacheGet.NOT_OUTOFDATE)) {
								Log.i("新数据:" + response);
								refreshData(response);
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							progressBar1.setVisibility(View.GONE);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

		if ((resultJson != null && !resultJson.equals(RequestWithCacheGet.NO_DATA))) {
			Log.i("缓存数据:" + resultJson);
			refreshData(resultJson);
		}
	}
	
	public void refreshData(String json){
		if(json.contains("<html>")){
			ToastUtil.showMessage("数据异常");
			progressBar1.setVisibility(View.GONE);
			return;
		}
		Gson gson = new Gson();
		classes = gson.fromJson(json,new TypeToken<List<UserClass>>() {}.getType());
		receiveUserLV.setAdapter(searchAdapter);
		Tools.setListViewHeightBasedOnChildren(receiveUserLV);
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				scrollView1.scrollTo(10, 10);  
			}
		}, 50);
		progressBar1.setVisibility(View.GONE);
	}
	
	public void onSelectTeacher(View v){
		startActivityForResult(new Intent(context, SelectActivity.class).putExtra("kind", SelectActivity.KIND_TEACHER),101);
		kind = SelectActivity.KIND_TEACHER;
	}
	public void onSelectStudent(View v){
		startActivityForResult(new Intent(context, SelectActivity.class).putExtra("kind", SelectActivity.KIND_STUDENT),101);
		kind = SelectActivity.KIND_STUDENT;
	}
	public void onSelectParent(View v){
		startActivityForResult(new Intent(context, SelectActivity.class).putExtra("kind", SelectActivity.KIND_PARENT),101);
		kind = SelectActivity.KIND_PARENT;
	}
	public void onSeleteMyGroup(View v){
		startActivityForResult(new Intent(context, SelectActivity.class).putExtra("kind", SelectActivity.KIND_GROUP),101);
		kind = SelectActivity.KIND_GROUP;
	}
	public static void put(String userId , Node node){
		SendNewNoticeActivity.positionMap.put(userId, node);
		if(kind == SelectActivity.KIND_TEACHER) {
			positionMap_T.put(userId, node);
//			teacherTV.setText("教师" + (positionMap_T.size() > 0 ? "(已选"+positionMap_T.size()+"人)" : ""));
		} else if (kind == SelectActivity.KIND_STUDENT) {
			positionMap_S.put(userId, node);
//			studentTV.setText("学生" + (positionMap_S.size() > 0 ? "(已选"+positionMap_S.size()+"人)" : ""));
		} else if (kind == SelectActivity.KIND_PARENT) {
			positionMap_P.put(userId, node);
//			parentTV.setText("家长" + (positionMap_P.size() > 0 ? "(已选"+positionMap_P.size()+"人)" : ""));
		} else if (kind == SelectActivity.KIND_GROUP) {
			positionMap_G.put(userId, node);
//			grouptTV.setText("常用联系人" + (positionMap_G.size() > 0 ? "(已选"+positionMap_G.size()+"人)" : ""));
		}
	}
	
	public static void remove(String userId){
		SendNewNoticeActivity.positionMap.remove(userId);
		if(kind == SelectActivity.KIND_TEACHER) {
			positionMap_T.remove(userId);
//			teacherTV.setText("教师" + (positionMap_T.size() > 0 ? "(已选"+positionMap_T.size()+"人)" : ""));
		} else if (kind == SelectActivity.KIND_STUDENT) {
			positionMap_S.remove(userId);
//			studentTV.setText("学生" + (positionMap_S.size() > 0 ? "(已选"+positionMap_S.size()+"人)" : ""));
		} else if (kind == SelectActivity.KIND_PARENT) {
			positionMap_P.remove(userId);
//			parentTV.setText("家长" + (positionMap_P.size() > 0 ? "(已选"+positionMap_P.size()+"人)" : ""));
		} else if (kind == SelectActivity.KIND_GROUP) {
			positionMap_G.remove(userId);
//			grouptTV.setText("常用联系人" + (positionMap_G.size() > 0 ? "(已选"+positionMap_G.size()+"人)" : ""));
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}
	
	public void refresh(){
//		teacherTV.setText("教师" + (positionMap_T.size() > 0 ? "(已选"+positionMap_T.size()+"人)" : ""));
//		studentTV.setText("学生" + (positionMap_S.size() > 0 ? "(已选"+positionMap_S.size()+"人)" : ""));
//		parentTV.setText("家长" + (positionMap_P.size() > 0 ? "(已选"+positionMap_P.size()+"人)" : ""));
//		grouptTV.setText("常用联系人" + (positionMap_G.size() > 0 ? "(已选"+positionMap_G.size()+"人)" : ""));
		button2.setText("确定\n" + SendNewNoticeActivity.positionMap.size());
		if(searchAdapter != null) {
			searchAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK){   
	    	setResult(100);
			finish();
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void onBack(View v){
		setResult(100);
		finish();
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
				convertView = View.inflate(context, R.layout.list_item3, null);
				holder.nameTV = (TextView) convertView.findViewById(R.id.textView1);
				holder.imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
				holder.checkBox1 = (CheckBox) convertView.findViewById(R.id.checkBox1);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
				holder.imageView1.setVisibility(View.GONE);
			}
			holder.nameTV.setText(getItem(position).getName());
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
	protected int getLayoutId() {
		return R.layout.activity_selectpageone;
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(resultCode == 500){
        	setResult(100);
    		finish();
		}
	}
}
