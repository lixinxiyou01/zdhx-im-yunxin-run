package zhwx.common.view.sortablegridview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.Collections;
import java.util.List;

import zhwx.common.model.Apps;
import zhwx.common.util.SharPreUtil;

/**
 * @blog http://blog.csdn.net/xiaanming 
 * 
 * @author xiaanming
 *
 */
public class DragAdapter extends BaseAdapter implements DragGridBaseAdapter {
	private List<Apps> list;
	private LayoutInflater mInflater;
	private int mHidePosition = -1;
	
	public DragAdapter(Context context, List<Apps> list){
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Apps getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 由于复用convertView导致某些item消失了，所以这里不复用item，
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.item_nmain_gv, null);
		ImageView mImageView = (ImageView) convertView.findViewById(R.id.iv_icon);
		TextView mTextView = (TextView) convertView.findViewById(R.id.tv_title);
		
		mImageView.setImageResource((Integer) list.get(position).getIcon());
		mTextView.setText((CharSequence) list.get(position).getName());
		
		if(position == mHidePosition){
			convertView.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

	@Override
	public void reorderItems(int oldPosition, int newPosition) {
		Apps temp = list.get(oldPosition);
		if(oldPosition < newPosition){
			for(int i=oldPosition; i<newPosition; i++){
				Collections.swap(list, i, i+1);
			}
		}else if(oldPosition > newPosition){
			for(int i=oldPosition; i>newPosition; i--){
				Collections.swap(list, i, i-1);
			}
		}
		list.set(newPosition, temp);
		// 记录该用户的应用顺序
		String appString = "";
		for (Apps apps : list) {
			appString += apps.getCode()+",";
		}
		SharPreUtil.saveField(ECApplication.getInstance().getCurrentIMUser().getId()+"appCode", appString);
	}

	@Override
	public void setHideItem(int hidePosition) {
		this.mHidePosition = hidePosition; 
		notifyDataSetChanged();
	}
}
