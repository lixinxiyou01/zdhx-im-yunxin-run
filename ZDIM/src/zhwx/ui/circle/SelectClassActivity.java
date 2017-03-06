package zhwx.ui.circle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.model.UserClass;


public class SelectClassActivity extends BaseActivity {
	
	private ListView classLV;
	
	private List<UserClass> userClasses;
	
	private FrameLayout top_bar;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar);
		userClasses = (List<UserClass>) getIntent().getSerializableExtra("classes");
		classLV = (ListView) findViewById(R.id.classLV);
		classLV.setAdapter(new myAdapter());
		classLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				UserClass class1 = (UserClass) adapter.getAdapter().getItem(position);
				Intent intent = new Intent();
				intent.putExtra("className", class1.getName());
				intent.putExtra("classId", class1.getId());
				setResult(101, intent);
				finish();
			}
		});
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_selectclass;
	}
	
	public class myAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return userClasses.size();
		}

		@Override
		public UserClass getItem(int position) {
			return userClasses.get(position);
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
				convertView = View.inflate(SelectClassActivity.this, R.layout.spinner_items1, null);
				holder.nameTV = (TextView) convertView.findViewById(R.id.spinnerTV);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.nameTV.setText(getItem(position).getName());
			return convertView;
		}
		
		class Holder{
			private TextView nameTV;
		}
	}
}
