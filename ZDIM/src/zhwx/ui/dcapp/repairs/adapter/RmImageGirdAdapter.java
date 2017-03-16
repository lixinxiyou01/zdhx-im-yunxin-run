package zhwx.ui.dcapp.repairs.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.netease.nim.demo.ECApplication;
import com.netease.nim.demo.R;

import java.util.List;

import zhwx.common.util.lazyImageLoader.cache.ImageLoader;


public class RmImageGirdAdapter extends BaseAdapter {
	private Activity context;
	private List<String> list;
	private ImageLoader mImageLoader;
	public RmImageGirdAdapter(Activity context, List<String> list) {
		super();
		this.context = context;
		this.list = list;
		mImageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public String getItem(int position) {
		return list.get(position);
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
			convertView = View.inflate(context, R.layout.gv_item_image, null);
			holder.imageGV = (ImageView) convertView.findViewById(R.id.imageGV);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		mImageLoader.DisplayImage(ECApplication.getInstance().getV3Address()+getItem(position), holder.imageGV, false);
		return convertView;
	}
	
	class Holder{
		private ImageView imageGV;
	}
}
