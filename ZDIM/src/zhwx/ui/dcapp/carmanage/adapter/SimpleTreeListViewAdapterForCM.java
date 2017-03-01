package zhwx.ui.dcapp.carmanage.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.util.List;

import zhwx.common.util.StringUtil;
import zhwx.common.view.treelistview.utils.Node;
import zhwx.common.view.treelistview.utils.TreeHelper;
import zhwx.common.view.treelistview.utils.adapter.TreeListViewAdapter;
import zhwx.ui.dcapp.carmanage.CMSelectActivity;
import zhwx.ui.dcapp.carmanage.OrderCarActivity;


public class SimpleTreeListViewAdapterForCM<T> extends TreeListViewAdapter<T> {
	
	private  CMSelectActivity context;
	
	public SimpleTreeListViewAdapterForCM(ListView tree, CMSelectActivity context,
			List<T> datas, int defaultExpandLevel)
			throws IllegalAccessException, IllegalArgumentException {
		super(tree, context, datas, defaultExpandLevel);
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getConvertView(Node node, int arg0, View view, ViewGroup arg2) {
		ViewHolder holder = null;
		if(view == null){
			view = mInflater.inflate(R.layout.list_item2, arg2, false);
			holder = new ViewHolder();
			holder.imageView = (ImageView) view.findViewById(R.id.imageView1);
			holder.headimgIV = (ImageView) view.findViewById(R.id.headimgIV);
			holder.textView = (TextView) view.findViewById(R.id.textView1);
			holder.checkBox1 = (CheckBox) view.findViewById(R.id.checkBox1);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		holder.checkBox1.setEnabled(true);
		holder.checkBox1.setVisibility(View.VISIBLE);
		if(node.isLeaf()){
			holder.textView.setTextColor(context.getResources().getColor(R.color.main_bg));
		}else{
			holder.textView.setTextColor(context.getResources().getColor(R.color.normal_text_color));
		}
		
		if(OrderCarActivity.positionMap.get(node.getContactId())!=null){
			holder.checkBox1.setChecked(true);
		}else{
			holder.checkBox1.setChecked(false);
		}
		
		if(!node.isLeaf()){
			if(OrderCarActivity.parentPositionMap.get(node.getId())!=null){
				holder.checkBox1.setChecked(true);
			}else{
				holder.checkBox1.setChecked(false);
			}
		}
		holder.textView.setText(node.getName());
		if (node.getIcon() == -1) {//子节点
			holder.imageView.setVisibility(View.INVISIBLE);
			if("123456".equals(node.getContactId())){//无人员
				holder.headimgIV.setVisibility(View.GONE);
				holder.checkBox1.setVisibility(View.INVISIBLE);
			}else{//联系人
				holder.headimgIV.setVisibility(View.VISIBLE);
				holder.checkBox1.setVisibility(View.VISIBLE);
			}
		}else{
			holder.headimgIV.setVisibility(View.GONE);
			holder.imageView.setVisibility(View.VISIBLE);
			holder.imageView.setImageResource(node.getIcon());
		}
		
		//设置选中人数
//		context.getTopBarView().setRightBtnEnable(ContactListFragmentForSelect1.positionMap.size() > 0 ? true:false);
//		context.getTopBarView().setRightButtonText(context.getString(R.string.radar_ok_count, context.getString(R.string.dialog_ok_button1) , ContactListFragmentForSelect1.positionMap.size()));
		addListener(holder, node,view);
		return view;
	}
	
	private class ViewHolder{
		ImageView imageView;
		ImageView headimgIV;
		TextView textView;
		CheckBox checkBox1;
	}
	private void addListener(final ViewHolder holder, final Node node,View view) {
		holder.checkBox1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(node.isLeaf()){
					if(OrderCarActivity.positionMap.get(node.getContactId()) == null){
						OrderCarActivity.positionMap.put(node.getContactId(), node);
						contentAll(node);
					}else{
						OrderCarActivity.positionMap.remove(node.getContactId());
						removeUperNode(node);
					}
				}else{
					if(OrderCarActivity.parentPositionMap.get(node.getId()) == null){
						OrderCarActivity.parentPositionMap.put(node.getId(), node);
						addChildNode(node);
						contentAll(node);
					}else{
						OrderCarActivity.parentPositionMap.remove(node.getId());
						removeChildNode(node);
						removeUperNode(node);
					}
				}
				CMSelectActivity.okBT.setText("确定\n" + OrderCarActivity.positionMap.size());
				notifyDataSetChanged();
			}
		});
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
//							if(GroupInfoActivity.groupMembers.get(j).getVoipAccount().equals(node.getChildren().get(i).getContactId())){
//								key = false;
//								break;
//							}else{
//								key = true;
//							}
//						}
//					}
//					if(key){
						if(!"123456".equals(node.getChildren().get(i).getContactId())){
							OrderCarActivity.positionMap.put(node.getChildren().get(i).getContactId(), node.getChildren().get(i));
						}
//					}
				}else{
					OrderCarActivity.parentPositionMap.put(node.getChildren().get(i).getId(), node.getChildren().get(i));
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
					OrderCarActivity.positionMap.remove(node.getChildren().get(i).getContactId());
				}else{
					OrderCarActivity.parentPositionMap.remove(node.getChildren().get(i).getId());
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
			OrderCarActivity.parentPositionMap.remove(node.getParent().getId());
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
			if(!(OrderCarActivity.positionMap.containsKey(node.getParent().getChildren().get(i).getContactId())
					||OrderCarActivity.parentPositionMap.containsKey(node.getParent().getChildren().get(i).getId()))){
				return;
			}
		}
		OrderCarActivity.parentPositionMap.put(node.getParent().getId(), node);
		contentAll(node.getParent());
	}
}
