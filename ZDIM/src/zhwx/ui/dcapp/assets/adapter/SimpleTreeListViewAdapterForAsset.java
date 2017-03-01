package zhwx.ui.dcapp.assets.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.util.List;

import zhwx.common.util.StringUtil;
import zhwx.common.view.treelistview.utils.Node;
import zhwx.common.view.treelistview.utils.TreeHelper;
import zhwx.common.view.treelistview.utils.adapter.TreeListViewAdapter;
import zhwx.ui.dcapp.assets.AssetKindSelectActivity;
import zhwx.ui.imapp.notice.SeletePageOneActivity;
import zhwx.ui.imapp.notice.SendNewNoticeActivity;


public class SimpleTreeListViewAdapterForAsset<T> extends TreeListViewAdapter<T> {
	
	private AssetKindSelectActivity context;
	
	private OnTreeNodeClickListener listener;
	
	public SimpleTreeListViewAdapterForAsset(ListView tree, AssetKindSelectActivity context,
			List<T> datas, int defaultExpandLevel)
			throws IllegalAccessException, IllegalArgumentException {
		super(tree, context, datas, defaultExpandLevel);
		this.context = context;
	}
	
	public void setListener(OnTreeNodeClickListener listener) {
		this.listener = listener;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getConvertView(final Node node, int arg0, View view, ViewGroup arg2) {
		ViewHolder holder = null;
		if(view == null){
			view = mInflater.inflate(R.layout.list_item_ass, arg2, false);
			holder = new ViewHolder();
			holder.imageView = (ImageView) view.findViewById(R.id.imageView1);
			holder.headimgIV = (ImageView) view.findViewById(R.id.headimgIV);
			holder.textView = (TextView) view.findViewById(R.id.textView1);
			holder.selectBT = (TextView) view.findViewById(R.id.selectBT);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		if(node.isLeaf()){
			holder.selectBT.setVisibility(View.INVISIBLE);
		}else{
			holder.selectBT.setVisibility(View.VISIBLE);
		}
		holder.textView.setText(node.getName());
		if (node.getIcon() == -1) {//子节点
			holder.imageView.setVisibility(View.INVISIBLE);
			holder.headimgIV.setImageResource(node.getIcon());
			holder.headimgIV.setVisibility(View.INVISIBLE);
		}else{
			holder.headimgIV.setVisibility(View.GONE);
			holder.imageView.setVisibility(View.VISIBLE);
			holder.imageView.setImageResource(node.getIcon());
		}
		holder.selectBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.onClick(node, 99999);
			}
		});
		return view;
	}
	
	private class ViewHolder{
		ImageView imageView;
		ImageView headimgIV;
		TextView textView,selectBT;
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
	
	/**
	 * 选中该组下所有节点
	 * @param node
	 */
	public void addChildNode(Node node){
		boolean key = true;
		if(node.getChildren().size()!=0){
			for (int i = 0; i < node.getChildren().size(); i++) {
				if(StringUtil.isNotBlank(node.getChildren().get(i).getContactId())){
//					if(GroupInfoActivity.groupMembers!=null){
//						for (int j = 0; j < GroupInfoActivity.groupMembers.size(); j++) {
//							if(GroupInfoActivity.groupMembers.get(j).getVoipAccount().equals(node.getChildren().get(i).getTerminalId())){
//								key = false;
//								break;
//							}else{
//								key = true;
//							}
//						}
//					}
//					if(key){
						if(!"123456".equals(node.getChildren().get(i).getContactId())){
							SeletePageOneActivity.put(node.getChildren().get(i).getContactId(), node.getChildren().get(i));
						}
//					}
				}else{
					SeletePageOneActivity.parentPositionMap.put(node.getChildren().get(i).getId(), node.getChildren().get(i));
					addChildNode(node.getChildren().get(i));//循环下一层级
				}
			}
		}
	}
	
	/**
	 * 移除该组下所有节点
	 * @param node
	 */
	public void removeChildNode(Node node){
		if(node.getChildren().size()!=0){
			for (int i = 0; i < node.getChildren().size(); i++) {
				if(StringUtil.isNotBlank(node.getChildren().get(i).getContactId())){
					SeletePageOneActivity.remove(node.getChildren().get(i).getContactId());
				}else{
					SeletePageOneActivity.parentPositionMap.remove(node.getChildren().get(i).getId());
					removeChildNode(node.getChildren().get(i));
				}
			}
		}
	}
	
	/**
	 * 移除所有上级根节点
	 * @param node
	 */
	public void removeUperNode(Node node){
		if(node.getParent()!=null){
			SeletePageOneActivity.parentPositionMap.remove(node.getParent().getId());
			removeUperNode(node.getParent());
		}
	}
	
	/**
	 * 选中某个节点后，判断是否此层级已都选中，如果都选中则将上级跟节点选中，循环至顶
	 * @param node
	 */
	public void contentAll(Node node){
		if(node.getParent()==null){
			return;
		}
		for (int i = 0; i < node.getParent().getChildren().size(); i++) {
			if(!(SendNewNoticeActivity.positionMap.containsKey(node.getParent().getChildren().get(i).getContactId())
					||SeletePageOneActivity.parentPositionMap.containsKey(node.getParent().getChildren().get(i).getId()))){
				return;
			}
		}
		SeletePageOneActivity.parentPositionMap.put(node.getParent().getId(), node);
		contentAll(node.getParent());
	}
}
