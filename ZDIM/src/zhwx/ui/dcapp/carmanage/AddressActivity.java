package zhwx.ui.dcapp.carmanage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.util.ArrayList;
import java.util.Arrays;

import zhwx.common.base.BaseActivity;
import zhwx.ui.dcapp.carmanage.adapter.SearchAutoAdapter;
import zhwx.ui.dcapp.carmanage.model.SearchAutoData;


/**   
 * @Title: AddressActivity.java 
 * @Package zhwx.ui.dcapp.carmanage
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Li.xin @ 中电和讯
 * @date 2016-4-11 下午3:03:59 
 */
public class AddressActivity extends BaseActivity implements OnClickListener {
	
	public static final String SEARCH_HISTORY = "search_history";
	private Activity context;
	private ListView mAutoListView;
	private TextView mSearchButtoon;
	private EditText mAutoEdit;
	private SearchAutoAdapter mSearchAutoAdapter;
	private FrameLayout top_bar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		getTopBarView().setVisibility(View.GONE);
		init();
	}
	
	private void init() {
		getTopBarView().setVisibility(View.GONE);
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar);
		mSearchAutoAdapter = new SearchAutoAdapter(this, -1, this);
		mAutoListView = (ListView) findViewById(R.id.auto_listview);
		mAutoListView.setAdapter(mSearchAutoAdapter);
		mAutoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				SearchAutoData data = (SearchAutoData) mSearchAutoAdapter.getItem(position);
				mAutoEdit.setText(data.getContent());
				mAutoEdit.setSelection(mAutoEdit.getText().length());
				saveSearchHistory();
				mSearchAutoAdapter.initSearchHistory();
			}
		});

		mSearchButtoon = (TextView) findViewById(R.id.search_button);
		mSearchButtoon.setOnClickListener(this);
		mAutoEdit = (EditText) findViewById(R.id.auto_edit);
		mAutoEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mSearchAutoAdapter.performFiltering(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.search_button) {//搜索按钮
			saveSearchHistory();
			mSearchAutoAdapter.initSearchHistory();
			Intent intent = new Intent();
			intent.putExtra("address", mAutoEdit.getText().toString().trim());
			setResult(122, intent);
			finish();
		} else {//"+"号按钮
			SearchAutoData data = (SearchAutoData) v.getTag();
			mAutoEdit.setText(data.getContent());
		}
	}

	/*
	 * 保存搜索记录
	 */
	private void saveSearchHistory() {
		String text = mAutoEdit.getText().toString().trim();
		if (text.length() < 1) {
			return;
		}
		SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, 0);
		String longhistory = sp.getString(SEARCH_HISTORY, "");
		String[] tmpHistory = longhistory.split(",");
		ArrayList<String> history = new ArrayList<String>(
				Arrays.asList(tmpHistory));
		if (history.size() > 0) {
			int i;
			for (i = 0; i < history.size(); i++) {
				if (text.equals(history.get(i))) {
					history.remove(i);
					break;
				}
			}
			history.add(0, text);
		}
		if (history.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < history.size(); i++) {
				sb.append(history.get(i) + ",");
			}
			sp.edit().putString(SEARCH_HISTORY, sb.toString()).commit();
		} else {
			sp.edit().putString(SEARCH_HISTORY, text + ",").commit();
		}
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_cm_address;
	}
}
