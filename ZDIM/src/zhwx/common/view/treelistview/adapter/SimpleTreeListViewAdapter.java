package zhwx.common.view.treelistview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.List;

import zhwx.common.util.lazyImageLoader.cache.ImageLoader;
import zhwx.common.view.treelistview.utils.Node;
import zhwx.common.view.treelistview.utils.TreeHelper;
import zhwx.common.view.treelistview.utils.adapter.TreeListViewAdapter;

public class SimpleTreeListViewAdapter<T> extends TreeListViewAdapter<T> {
	
	private  Context context;
	private ImageLoader mImageLoader;
	
	public SimpleTreeListViewAdapter(ListView tree, Context context,List<T> datas, int defaultExpandLevel)
			throws IllegalAccessException, IllegalArgumentException {
		super(tree, context, datas, defaultExpandLevel);
		this.context = context;
		mImageLoader = new ImageLoader(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getConvertView(Node node, int arg0, View view, ViewGroup arg2) {
		ViewHolder holder = null;
		if(view == null){
			//两个参数时，list_item.xml中的最外层设置无效。
			//false返回我们的list_item，true返回arg2（list_item加载到arg2中后）
			view = mInflater.inflate(R.layout.list_item1, arg2, false);
			holder = new ViewHolder();
			holder.imageView = (ImageView) view.findViewById(R.id.imageView1);
			holder.headimgIV = (ImageView) view.findViewById(R.id.headimgIV);
			holder.textView = (TextView) view.findViewById(R.id.textView1);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		
		if(node.isLeaf()){
			holder.textView.setTextColor(context.getResources().getColor(R.color.main_bg));
		}else{
			holder.textView.setTextColor(context.getResources().getColor(R.color.normal_text_color));
		}
		
		//加载头像
		holder.headimgIV.setImageResource(R.drawable.defult_head_img);
		mImageLoader.DisplayImage(ECApplication.getInstance().getAddress() + node.getHeadPortraitUrl(), holder.headimgIV, false);
		
		if (node.getIcon() == -1) {
			holder.imageView.setVisibility(View.INVISIBLE);
			if("123456".equals(node.getContactId())){
				holder.headimgIV.setVisibility(View.GONE);
			}else{
				holder.headimgIV.setVisibility(View.VISIBLE);
			}
		}else{
			holder.headimgIV.setVisibility(View.GONE);
			holder.imageView.setVisibility(View.VISIBLE);
			holder.imageView.setImageResource(node.getIcon());
		}
		holder.textView.setText(node.getName());
		return view;
	}
	
	private class ViewHolder{
		ImageView imageView;
		ImageView headimgIV;
		TextView textView;
	}

	/**
	 * 动态插入节点
	 * @param arg2
	 * @param string
	 */
	public void addExtraNode(int arg2, String string) {
		Node node = mVisibleNodes.get(arg2);
		int indexOf = mAllNodes.indexOf(node);
		
		Node extraNode = new Node(-1, node.getId(), string);
		extraNode.setParent(node);
		node.getChildren().add(extraNode);
		mAllNodes.add(indexOf+1, extraNode);
		mVisibleNodes = TreeHelper.filterVisibelNodes(mAllNodes);
		notifyDataSetChanged();
	}
}
