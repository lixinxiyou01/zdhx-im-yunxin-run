package zhwx.ui.dcapp.assets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.netease.nim.demo.R;

import zhwx.common.base.BaseActivity;
import zhwx.common.view.treelistview.bean.FileBean;
import zhwx.common.view.treelistview.utils.Node;
import zhwx.common.view.treelistview.utils.adapter.TreeListViewAdapter;
import zhwx.ui.dcapp.assets.adapter.SimpleTreeListViewAdapterForAsset;

/**
 * @Title: SelectActivity.java
 * @Package com.lanxum.smscenter.activity
 * @author Li.xin @ 中电和讯
 * @date 2015-11-24 下午1:58:25
 */
public class AssetKindSelectActivity extends BaseActivity {

	private Activity context;

	private ListView mListView;

	private SimpleTreeListViewAdapterForAsset<FileBean> treeListViewAdapter;

	private FrameLayout top_bar;
	
	private TreeListViewAdapter.OnTreeNodeClickListener listener = new TreeListViewAdapter.OnTreeNodeClickListener() {

		@Override
		public void onClick(Node node, int code) {
			if (node.isLeaf() || code == 99999) {
				Intent intent = new Intent();
				intent.putExtra("userId", node.getContactId());
				intent.putExtra("userName", node.getName());
				setResult(RESULT_OK, intent);
				finish();
			}
		}		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTopBarView().setVisibility(View.GONE);
		context = this;
		mListView = (ListView) findViewById(R.id.mListView);
		top_bar = (FrameLayout) findViewById(R.id.top_bar);
		setImmerseLayout(top_bar);
		refreshTree();
	}

	/**
	 * 刷新好友列表数据
	 * @param json
	 */
	public void refreshTree() {
		try {
			treeListViewAdapter = new SimpleTreeListViewAdapterForAsset<FileBean>(mListView, (AssetKindSelectActivity) context, SendSearchActivity.datas, 0);
			treeListViewAdapter.setListener(listener);
			mListView.setAdapter(treeListViewAdapter);
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		// ListView点击事件，调用自己的点击回调函数
		treeListViewAdapter.setOnTreeNodeClickListener(listener);
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_assetkind_select;
	}
}
