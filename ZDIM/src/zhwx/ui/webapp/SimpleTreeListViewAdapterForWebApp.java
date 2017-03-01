package zhwx.ui.webapp;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.netease.nim.demo.R;

import java.io.Serializable;
import java.util.List;

import zhwx.common.base.BaseActivity;
import zhwx.common.view.treelistview.utils.Node;
import zhwx.common.view.treelistview.utils.adapter.TreeListViewAdapter;


public class SimpleTreeListViewAdapterForWebApp<T> extends TreeListViewAdapter<T> implements Serializable{
	
	private BaseActivity context;
	
	public SimpleTreeListViewAdapterForWebApp(ListView tree, BaseActivity context,
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
		
		holder.textView.setText(node.getName());
		if (node.getIcon() == -1) {//子节点
			holder.imageView.setVisibility(View.INVISIBLE);
			if("123456".equals(node.getContactId())){//无人员
				holder.headimgIV.setVisibility(View.GONE);
			}else{//联系人
				holder.headimgIV.setVisibility(View.VISIBLE);
			}
		}else{
			holder.headimgIV.setVisibility(View.GONE);
			holder.imageView.setVisibility(View.VISIBLE);
			holder.imageView.setImageResource(node.getIcon());
		}
		
		return view;
	}
	
	private class ViewHolder{
		ImageView imageView;
		ImageView headimgIV;
		TextView textView;
	}
}
