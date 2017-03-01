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
import zhwx.ui.dcapp.assets.adapter.SimpleTreeListViewAdapterForAssetApply;


/**
 * @Title: SelectActivity.java
 * @Package com.lanxum.smscenter.activity
 * @author Li.xin @ 中电和讯
 * @date 2015-11-24 下午1:58:25
 */
public class AssetKindSelectActivityApply extends BaseActivity {

	private Activity context;

	private ListView mListView;

	private SimpleTreeListViewAdapterForAssetApply<FileBean> treeListViewAdapter;

	private FrameLayout top_bar;
	
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
			treeListViewAdapter = new SimpleTreeListViewAdapterForAssetApply<FileBean>(
					mListView, (AssetKindSelectActivityApply) context, ApplyForAssetActivity.datas, 0);
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
					Intent intent = new Intent();
					intent.putExtra("userId", node.getContactId());
					intent.putExtra("userName", node.getName());
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_assetkind_select;
	}
}
