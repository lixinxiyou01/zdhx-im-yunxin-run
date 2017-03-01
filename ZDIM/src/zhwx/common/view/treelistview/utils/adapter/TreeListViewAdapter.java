package zhwx.common.view.treelistview.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.List;

import zhwx.common.view.treelistview.utils.Node;
import zhwx.common.view.treelistview.utils.TreeHelper;


public abstract class TreeListViewAdapter<T> extends BaseAdapter {
	protected Context mContext;
	protected List<Node> mAllNodes;
	protected List<Node> mVisibleNodes;
	protected LayoutInflater mInflater;
	protected ListView mTree;
	
	/**
	 * 设置Node的点击回调
	 * @author Administrator
	 *
	 */
	public interface OnTreeNodeClickListener{
		void onClick(Node node, int arg0);
	}
	private OnTreeNodeClickListener mListener;
	public void setOnTreeNodeClickListener(OnTreeNodeClickListener mListener) {
		this.mListener = mListener;
	}
	
	public TreeListViewAdapter(ListView tree, Context context, List<T> datas, int defaultExpandLevel) throws IllegalAccessException, IllegalArgumentException {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mAllNodes = TreeHelper.getSortedNodes(datas, defaultExpandLevel);
		mVisibleNodes = TreeHelper.filterVisibelNodes(mAllNodes);
		mTree = tree;
		mTree.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				expandOrCollapse(arg2);
				if(mListener != null){
					mListener.onClick(mVisibleNodes.get(arg2), arg2);
				}
			}
		});
	}

	/**
	 * 点击收缩或展开
	 * @param arg2
	 */
	protected void expandOrCollapse(int arg2) {
		Node n = mVisibleNodes.get(arg2);
		if(n != null){
			if(n.isLeaf()) return;
			n.setExpand(!n.isExpand());
			mVisibleNodes = TreeHelper.filterVisibelNodes(mAllNodes);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mVisibleNodes.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mVisibleNodes.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		Node node = mVisibleNodes.get(arg0);
		arg1 = getConvertView(node, arg0, arg1, arg2);
		arg1.setPadding(node.getLevel()*30, 3, 3, 3);
		return arg1;
	}

	public abstract View getConvertView(Node node, int arg0, 
			View arg1, ViewGroup arg2);
}
