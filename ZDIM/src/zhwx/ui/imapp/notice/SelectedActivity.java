package zhwx.ui.imapp.notice;

import android.app.Activity;
import android.os.Bundle;
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
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import zhwx.common.base.BaseActivity;
import zhwx.common.view.treelistview.utils.Node;


/**   
 * @Title: SelectedActivity.java 
 * @Package com.zdhx.edu.im.ui.v3.notice 
 * @author Li.xin @ 中电和讯
 * @date 2015-12-14 下午4:35:24 
 */
public class SelectedActivity extends BaseActivity {
	
	private Activity context;
	
	private ListView mListView;
	
	private myAdapter searchAdapter = new myAdapter();
	
	private Button okBT;
	
	public static List<Node> list = new ArrayList<Node>();
	
	private FrameLayout top_bar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		context = this;
		okBT = (Button) findViewById(R.id.okBT);
		okBT.setText("确定\n" + SendNewNoticeActivity.positionMap.size());
		formArry();
		mListView = (ListView) findViewById(R.id.mListView);
		mListView.setAdapter(searchAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Node node = (Node) parent.getAdapter().getItem(position);
				if (SendNewNoticeActivity.positionMap.get(node.getContactId()) != null) {
					SeletePageOneActivity.remove(node.getContactId());
					SeletePageOneActivity.positionMap_P.remove(node.getContactId());
					SeletePageOneActivity.positionMap_T.remove(node.getContactId());
					SeletePageOneActivity.positionMap_S.remove(node.getContactId());
				} else {
					SeletePageOneActivity.put(node.getContactId(), node);
				}
				okBT.setText("确定\n" + SendNewNoticeActivity.positionMap.size());
				searchAdapter.notifyDataSetChanged();
			}
		});
		okBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setResult(100);
				finish();
			}
		});
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar);
	}
	
	public class myAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Node getItem(int position) {
			return list.get(position);
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
			if(SendNewNoticeActivity.positionMap.get(getItem(position).getContactId())!=null){
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
	
	public void formArry() {
		list.clear();
		for (Map.Entry<String, Node> entry : SendNewNoticeActivity.positionMap.entrySet()) {
			list.add(entry.getValue());
		}
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.contacts_selected_activity_notice;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(100);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void onBack(View v) {
		setResult(100);
		finish();
	}
}
